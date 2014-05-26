package com.appdynamics.monitors.vertica.converter;


import com.google.common.base.Strings;

/**
 * Converts Int in the form of String to Int, additionally it can remove extra chars from String before converting.
 */
public class StringToLongConverter implements Converter<Long> {

    private String exrta;

    public StringToLongConverter(String extra) {
        this.exrta = extra;
    }

    public StringToLongConverter() {
    }

    public Long convert(String value) {
        value = removeExtra(value);
        return Long.valueOf(value);
    }

    private String removeExtra(String value) {
        if (Strings.isNullOrEmpty(exrta)) {
            return value;
        }
        return value.replaceAll(exrta, "");
    }
}