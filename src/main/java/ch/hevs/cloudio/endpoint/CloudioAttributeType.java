package ch.hevs.cloudio.endpoint;

import java.lang.reflect.Type;

/**
 * The different data types of attributes currently supported by cloud.io.
 */
enum CloudioAttributeType {

    /**
     * Invalid data type.
     */
    Invalid,

    /**
     * The attribute's value is of type boolean.
     */
    Boolean,

    /**
     * The attribute's value is of type short, int or long.
     */
    Integer,

    /**
     * The attribute's value is of type float or double.
     */
    Number,

    /**
     * The attribute's value is of type String.
     */
    String,

    /**
     * The attribute's values are of type boolean (multiple, array).
     */
    BooleanArray,

    /**
     * The attribute's values are of type short, int or long (multiple, array).
     */
    IntegerArray,

    /**
     * The attribute's values are of type float or double (multiple, array).
     */
    NumberArray,

    /**
     * The attribute's values are of type string (multiple, array).
     */
    StringArray;

    static CloudioAttributeType fromRawType(Type type) {
        if (type == Boolean.class) {
            return Boolean;
        } else if (type == Short.class || type == Integer.class || type == Long.class) {
            return Integer;
        } else if (type == Float.class || type == Double.class) {
            return Number;
        } else if (type == String.class) {
            return String;
        } else if (type == Boolean[].class) {
            return BooleanArray;
        } else if (type == Short[].class || type == Integer[].class || type == Long[].class) {
            return IntegerArray;
        } else if (type == Float[].class || type == Double[].class) {
            return NumberArray;
        } else if (type == String[].class) {
            return StringArray;
        } else {
            return Invalid;
        }
    }
}
