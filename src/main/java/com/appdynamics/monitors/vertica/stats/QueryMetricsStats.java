package com.appdynamics.monitors.vertica.stats;

import com.appdynamics.monitors.vertica.converter.Converters;
import com.google.common.collect.Lists;
import java.util.List;

public class QueryMetricsStats {
    public static final List<String> APPENDER_COLUMNS = Lists.newArrayList("node_name");
    public static final List<ColumnWithConverter> STAT_COLUMNS = Lists.newArrayList(
            new ColumnWithConverter("active_user_session_count", Converters.toLongConverter()),
            new ColumnWithConverter("active_system_session_count", Converters.toLongConverter()),
            new ColumnWithConverter("total_user_session_count", Converters.toLongConverter()),
            new ColumnWithConverter("total_system_session_count", Converters.toLongConverter()),
            new ColumnWithConverter("total_active_session_count", Converters.toLongConverter()),
            new ColumnWithConverter("total_session_count", Converters.toLongConverter()),
            new ColumnWithConverter("running_query_count", Converters.toLongConverter()),
            new ColumnWithConverter("executed_query_count", Converters.toLongConverter()));
    public static final String QUERY = "SELECT * FROM QUERY_METRICS;";
    public static final String METRIC_PATH = "Custom Metrics|Vertica|Query Metrics";

    public static StatsRequest request() {
        return new StatsRequest(QUERY, METRIC_PATH, APPENDER_COLUMNS, STAT_COLUMNS);
    }
}
