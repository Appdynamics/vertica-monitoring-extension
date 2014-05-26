package com.appdynamics.monitors.vertica.stats;

import com.appdynamics.monitors.vertica.converter.Converters;
import com.google.common.collect.Lists;
import java.util.List;

public class SystemStats {

    public static final List<String> APPENDER_COLUMNS = Lists.newArrayList();
    public static final List<ColumnWithConverter> STAT_COLUMNS = Lists.newArrayList(
            new ColumnWithConverter("designed_fault_tolerance", Converters.toLongConverter()),
            new ColumnWithConverter("node_count", Converters.toLongConverter()),
            new ColumnWithConverter("node_down_count", Converters.toLongConverter()),
            new ColumnWithConverter("current_fault_tolerance", Converters.toLongConverter()));
    public static final String QUERY = "SELECT * FROM SYSTEM;";
    public static final String METRIC_PATH = "Custom Metrics|Vertica|System";

    public static StatsRequest request() {
        return new StatsRequest(QUERY, METRIC_PATH, APPENDER_COLUMNS, STAT_COLUMNS);
    }
}