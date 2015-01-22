package com.appdynamics.monitors.vertica.stats;

import com.appdynamics.monitors.vertica.converter.Converters;
import com.google.common.collect.Lists;
import java.util.List;

public class HostResourcesStats {

    public static final List<String> APPENDER_COLUMNS = Lists.newArrayList("host_name");
    public static final List<ColumnWithConverter> STAT_COLUMNS = Lists.newArrayList(
            new ColumnWithConverter("open_files_limit", Converters.toLongConverter()),
            new ColumnWithConverter("threads_limit", Converters.toLongConverter()),
            new ColumnWithConverter("core_file_limit_max_size_bytes", Converters.toLongConverter()),
            new ColumnWithConverter("processor_count", Converters.toLongConverter()),
            new ColumnWithConverter("processor_core_count", Converters.toLongConverter()),
            new ColumnWithConverter("opened_file_count", Converters.toLongConverter()),
            new ColumnWithConverter("opened_socket_count", Converters.toLongConverter()),
            new ColumnWithConverter("opened_nonfile_nonsocket_count", Converters.toLongConverter()),
            new ColumnWithConverter("total_memory_bytes", Converters.toLongConverter()),
            new ColumnWithConverter("total_memory_free_bytes", Converters.toLongConverter()),
            new ColumnWithConverter("total_buffer_memory_bytes", Converters.toLongConverter()),
            new ColumnWithConverter("total_memory_cache_bytes", Converters.toLongConverter()),
            new ColumnWithConverter("total_swap_memory_bytes", Converters.toLongConverter()),
            new ColumnWithConverter("total_swap_memory_free_bytes", Converters.toLongConverter()),
            new ColumnWithConverter("disk_space_free_mb", Converters.toLongConverter()),
            new ColumnWithConverter("disk_space_used_mb", Converters.toLongConverter()),
            new ColumnWithConverter("disk_space_total_mb", Converters.toLongConverter()));
    public static final String QUERY = "SELECT * FROM HOST_RESOURCES;";
    public static final String METRIC_PATH = "Host Resources";

    public static StatsRequest request(String metricPrefix) {
        return new StatsRequest(QUERY, metricPrefix+METRIC_PATH, APPENDER_COLUMNS, STAT_COLUMNS);
    }
}
