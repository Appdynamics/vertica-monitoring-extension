package com.appdynamics.monitors.vertica.converter;

/**
 * Converts Float in the form of String to Float
 */
public class StringToFloatConverter implements Converter<Float> {
    public Float convert(String value) {
        return Float.valueOf(value);
    }
}
