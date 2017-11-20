#!/usr/bin/env bash

CERTIFICATES_FOLDER=./certificates
CERTIFICATES_MAKEFILE=${CERTIFICATES_FOLDER}/Makefile
CERTIFICATES_CONFIG=${CERTIFICATES_FOLDER}/openssl.cnf

BROKER_CONFIG_FOLDER=./config
BROKER_LOG_FOLDER=./log/broker

DOCKER_COMPOSE_FILE=docker-compose.yml

if [ ! -d "${CERTIFICATES_FOLDER}" ]; then
	echo "Creating folder for certificates ${CERTIFICATES_FOLDER}..."
	mkdir ${CERTIFICATES_FOLDER}
fi

if [ ! -e "${CERTIFICATES_MAKEFILE}" ]; then
	echo "Creating Makefile for certificates ${CERTIFICATES_MAKEFILE}..."
	cat << EOF > ${CERTIFICATES_MAKEFILE}
all: ca-cert broker-cert cloudio_services-cert links

./Authority/ca-cert.pem:
	mkdir -p ./Authority/Signed
	mkdir -p ./private
	chmod 700 ./private
	echo 01 > ./serial
	touch ./index.txt
	openssl req -x509 -config ./openssl.cnf -newkey rsa:2048 -days 3650 -out ./Authority/ca-cert.pem -outform PEM -subj /CN=cloudiO/ -nodes
	openssl x509 -in ./Authority/ca-cert.pem -out ./Authority/ca-cert.cer -outform DER	

./Authority/ca-cert.jks: ./Authority/ca-cert.pem
	echo "123456" > temp
	echo "123456" >> temp
	echo "yes" >> temp
	cat temp | keytool -J-Duser.language=en -import -alias authority -file ./Authority/ca-cert.pem -keystore ./Authority/ca-cert.jks
	
./Broker/broker-cert.pem: ./Authority/ca-cert.pem
	mkdir -p ./Broker
	openssl genrsa -out ./Broker/broker-key.pem 2048
	openssl req -new -key ./Broker/broker-key.pem -out /tmp/req.pem -outform PEM -subj /CN=\$(shell hostname)/O=broker/ -nodes
	openssl ca -config ./openssl.cnf -in /tmp/req.pem -out ./Broker/broker-cert.pem -notext -batch -extensions server_ca_extensions
	

./Clients/%.p12: ./Authority/ca-cert.pem
	mkdir -p ./Clients
	openssl genrsa -out \$(basename \$@)-key.pem 2048
	openssl req -new -key \$(basename \$@)-key.pem -out /tmp/req.pem -outform PEM -subj /CN=\`echo \$(basename \$(notdir \$@)) | awk -F "/" '{print \$\$(NF - 1)}'\`/O=client/ -nodes
	openssl ca -config ./openssl.cnf -in /tmp/req.pem -out \$(basename \$@)-cert.pem -notext -batch -extensions client_ca_extensions
	openssl pkcs12 -export -out \$@ -in \$(basename \$@)-cert.pem -inkey \$(basename \$@)-key.pem -password pass:
	
ca-cert: ./Authority/ca-cert.jks

broker-cert: ./Broker/broker-cert.pem

cloud-cert: ./Clients/cloudio_services.p12

%-cert: ./Clients/%.p12
	@echo Done

links: ./ca-cert.pem ./broker-cert.pem ./broker-key.pem ./ca-cert.jks ./cloudio_services.p12

./ca-cert.pem: ca-cert
	ln -sf ./Authority/ca-cert.pem ./

./broker-cert.pem: broker-cert
	ln -sf ./Broker/broker-cert.pem ./

./broker-key.pem: broker-cert
	ln -sf ./Broker/broker-key.pem ./

./ca-cert.jks: ca-cert
	ln -sf ./Authority/ca-cert.jks ./

./cloudio_services.p12: cloudio_services-cert
	ln -sf ./Clients/cloudio_services.p12 ./
	        
clean:
	@rm -rf ./Authority ./Broker ./Clients ./private ./serial ./index.txt *.old *.attr *.pem *.jks *.p12
    
.PHONY: ca-cert broker-cert cloud-cert %-cert all clean links
.PRECIOUS: ./Clients/%.p12
EOF
fi

if [ ! -e "${CERTIFICATES_CONFIG}" ]; then
echo "Creating Configuration for certificates ${CERTIFICATES_CONFIG}..."
	cat << EOF > ${CERTIFICATES_CONFIG}
[ ca ]
default_ca = cloudioca

[ cloudioca ]
certificate = ./Authority/ca-cert.pem
database = ./index.txt
new_certs_dir = ./Authority/Signed
private_key = ./private/cakey.pem
serial = ./serial

default_crl_days = 7
default_days = 3650
default_md = sha1

policy = ca_policy
x509_extensions = certificate_extensions

[ ca_policy ]
commonName = supplied
stateOrProvinceName = optional
countryName = optional
emailAddress = optional
organizationName = optional
organizationalUnitName = optional

[ certificate_extensions ]
basicConstraints = CA:false

[ req ]
default_bits = 2048
default_keyfile = ./private/cakey.pem
default_md = sha1
prompt = yes
distinguished_name = root_ca_distinguished_name
x509_extensions = root_ca_extensions

[ root_ca_distinguished_name ]
commonName = hostname

[ root_ca_extensions ]
basicConstraints = CA:true
keyUsage = keyCertSign, cRLSign

[ client_ca_extensions ]
basicConstraints = CA:false
keyUsage = digitalSignature
extendedKeyUsage = 1.3.6.1.5.5.7.3.2

[ server_ca_extensions ]
basicConstraints = CA:false
keyUsage = keyEncipherment
extendedKeyUsage = 1.3.6.1.5.5.7.3.1
EOF
fi

if [ ! -d "${BROKER_CONFIG_FOLDER}" ]; then
	echo "Creating folder for RabbitMQ configuration ${BROKER_CONFIG_FOLDER}..."
	mkdir ${BROKER_CONFIG_FOLDER}
fi

if [ ! -d "${BROKER_LOG_FOLDER}" ]; then
	echo "Creating folder for RabbitMQ logs ${BROKER_LOG_FOLDER}..."
	mkdir -p ${BROKER_LOG_FOLDER}
fi

if [ ! -e "${DOCKER_COMPOSE_FILE}" ]; then
	echo "Creating docker compose config ${DOCKER_COMPOSE_FILE}..."
	cat << EOF > ${DOCKER_COMPOSE_FILE}
version: '2'
services:
  cloudio-broker:
    image: cloudio/cloudio-rabbitmq:3.6.2
    environment:
      - RABBITMQ_ADMIN_PASSWORD=admin
    ports:
      - "8883:8883"
      - "5671:5671"
      - "15672:15672"
    volumes:
      - ./certificates:/certificates
      - ./config:/config
      - ./log/broker:/log
  cloudio-mongo:
    image: mongo
    ports:
      - "27017:27017"
  cloudio-influx:
    image: influxdb
    ports:
      - "8086:8086"
      - "8083:8083"
  cloudio-backend-mongo-influx:
    depends_on:
      - cloudio-broker
      - cloudio-mongo
      - cloudio-influx
    image: cloudio/cloudio-cloud-backend-mongo-influx:0.1.0-SNAPSHOT
    environment:
      - CLOUDIO_CA_CERTIFICATE_PASSWORD=123456
    volumes:
      - ./certificates:/certificates
EOF
fi

echo "Generating/Updating certificates..."
make -C "${CERTIFICATES_FOLDER}"

echo "Creating containers..."
docker-compose create
