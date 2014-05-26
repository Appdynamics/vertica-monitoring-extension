package com.appdynamics.monitors.vertica.converter;

public class Converters {
    private static final StringToLongConverter TO_LONG_CONVERTER = new StringToLongConverter();
    private static final StringToFloatConverter TO_FLOAT_CONVERTER = new StringToFloatConverter();

    public static StringToLongConverter toLongConverter() {
        return TO_LONG_CONVERTER;
    }

    public static StringToLongConverter toLongConverter(String extra) {
        return new StringToLongConverter(extra);
    }

    public static EnumStringToIntConverter toLongConverter(Class<? extends Enum> enumClass) {
        return new EnumStringToIntConverter(enumClass);
    }

    public static StringToFloatConverter toFloatConverter() {
        return TO_FLOAT_CONVERTER;
    }
}
