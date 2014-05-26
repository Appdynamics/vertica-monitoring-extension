package com.appdynamics.monitors.vertica.stats;

import com.appdynamics.monitors.vertica.converter.Converter;

public class ColumnWithConverter {
    private String columnName;
    private Converter<?> converter;

    public ColumnWithConverter(String columnName, Converter<?> converter) {
        this.columnName = columnName;
        this.converter = converter;
    }

    public String getColumnName() {
        return columnName;
    }

    public Converter<?> getConverter() {
        return converter;
    }
}
