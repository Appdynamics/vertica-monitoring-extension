package com.appdynamics.monitors.vertica.converter;

public interface Converter<T extends Number> {
    
    public T convert(String value);
}
