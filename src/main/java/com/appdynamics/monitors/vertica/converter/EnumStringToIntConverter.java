package com.appdynamics.monitors.vertica.converter;

/**
 * Converts enum value to Int
 */
public class EnumStringToIntConverter implements Converter<Integer> {
    private Class<? extends Enum> enumClass;

    public EnumStringToIntConverter(Class<? extends Enum> enumClass) {
        this.enumClass = enumClass;
    }

    public Integer convert(String value) {
        Enum anEnum = Enum.valueOf(enumClass, value);
        return anEnum.ordinal();
    }
}