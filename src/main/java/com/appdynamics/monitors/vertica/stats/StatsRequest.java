package com.appdynamics.monitors.vertica.stats;

import java.util.List;

public class StatsRequest {
    private String query;
    private String metricPath;
    public List<String> appenderColumns;
    public List<ColumnWithConverter> statColumns;

    public StatsRequest(String query, String metricPath, List<String> appenderColumns, List<ColumnWithConverter> statColumns) {
        this.query = query;
        this.metricPath = metricPath;
        this.appenderColumns = appenderColumns;
        this.statColumns = statColumns;
    }

    public String getQuery() {
        return query;
    }

    public String getMetricPath() {
        return metricPath;
    }

    public List<String> getAppenderColumns() {
        return appenderColumns;
    }

    public List<ColumnWithConverter> getStatColumns() {
        return statColumns;
    }
}
