package com.appdynamics.monitors.vertica.stats;

import com.appdynamics.monitors.vertica.converter.Converters;
import com.google.common.collect.Lists;
import java.util.List;

public class SystemResourceUsageStats {
    public static final List<String> APPENDER_COLUMNS = Lists.newArrayList("node_name");
    public static final List<ColumnWithConverter> STAT_COLUMNS = Lists.newArrayList(
            new ColumnWithConverter("average_memory_usage_percent", Converters.toFloatConverter()),
            new ColumnWithConverter("average_cpu_usage_percent", Converters.toFloatConverter()),
            new ColumnWithConverter("net_rx_kbytes_per_second", Converters.toFloatConverter()),
            new ColumnWithConverter("net_tx_kbytes_per_second", Converters.toFloatConverter()),
            new ColumnWithConverter("io_read_kbytes_per_second", Converters.toFloatConverter()),
            new ColumnWithConverter("io_written_kbytes_per_second", Converters.toFloatConverter()));
    public static final String QUERY = "SELECT * FROM SYSTEM_RESOURCE_USAGE LIMIT 1;";
    public static final String METRIC_PATH = "System Resource Usage";

    public static StatsRequest request(String metricPrefix) {
        return new StatsRequest(QUERY, metricPrefix+METRIC_PATH, APPENDER_COLUMNS, STAT_COLUMNS);
    }
}
