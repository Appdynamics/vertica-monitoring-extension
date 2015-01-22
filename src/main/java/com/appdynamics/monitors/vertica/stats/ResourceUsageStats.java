package com.appdynamics.monitors.vertica.stats;

import com.appdynamics.monitors.vertica.converter.Converters;
import com.google.common.collect.Lists;
import java.util.List;

public class ResourceUsageStats {
    public static final List<String> APPENDER_COLUMNS = Lists.newArrayList("node_name");
    public static final List<ColumnWithConverter> STAT_COLUMNS = Lists.newArrayList(
            new ColumnWithConverter("request_count", Converters.toLongConverter()),
            new ColumnWithConverter("local_request_count", Converters.toLongConverter()),
            new ColumnWithConverter("active_thread_count", Converters.toLongConverter()),
            new ColumnWithConverter("open_file_handle_count", Converters.toLongConverter()),
            new ColumnWithConverter("memory_requested_kb", Converters.toLongConverter()),
            new ColumnWithConverter("address_space_requested_kb", Converters.toLongConverter()),
            new ColumnWithConverter("wos_used_bytes", Converters.toLongConverter()),
            new ColumnWithConverter("wos_row_count", Converters.toLongConverter()),
            new ColumnWithConverter("ros_used_bytes", Converters.toLongConverter()),
            new ColumnWithConverter("ros_row_count", Converters.toLongConverter()),
            new ColumnWithConverter("total_row_count", Converters.toLongConverter()),
            new ColumnWithConverter("total_used_bytes", Converters.toLongConverter()));
    public static final String QUERY = "SELECT * FROM RESOURCE_USAGE;";
    public static final String METRIC_PATH = "Resource Usage";

    public static StatsRequest request(String metricPrefix) {
        return new StatsRequest(QUERY, metricPrefix+METRIC_PATH, APPENDER_COLUMNS, STAT_COLUMNS);
    }
}
