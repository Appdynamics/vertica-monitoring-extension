package com.appdynamics.monitors.vertica.stats;

import com.appdynamics.monitors.vertica.converter.Converters;
import com.google.common.collect.Lists;
import java.util.List;

public class IOUsageStats {

    public static final List<String> APPENDER_COLUMNS = Lists.newArrayList("node_name");
    public static final List<ColumnWithConverter> STAT_COLUMNS = Lists.newArrayList(
            new ColumnWithConverter("read_kbytes_per_sec", Converters.toFloatConverter()),
            new ColumnWithConverter("written_kbytes_per_sec", Converters.toFloatConverter()));

    public static final String QUERY = "SELECT * FROM IO_USAGE;";
    public static final String METRIC_PATH = "Custom Metrics|Vertica|IO Usage";

    public static StatsRequest request() {
        return new StatsRequest(QUERY, METRIC_PATH, APPENDER_COLUMNS, STAT_COLUMNS);
    }
}
