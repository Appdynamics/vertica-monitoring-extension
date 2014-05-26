package com.appdynamics.monitors.vertica.stats;

import com.appdynamics.monitors.vertica.converter.Converters;
import com.google.common.collect.Lists;
import java.util.List;

public class DiskStorageStats {

    public static final List<String> APPENDER_COLUMNS = Lists.newArrayList("node_name", "storage_usage");
    public static final List<ColumnWithConverter> STAT_COLUMNS = Lists.newArrayList(
            new ColumnWithConverter("disk_block_size_bytes", Converters.toLongConverter()),
            new ColumnWithConverter("disk_space_used_blocks", Converters.toLongConverter()),
            new ColumnWithConverter("disk_space_used_mb", Converters.toLongConverter()),
            new ColumnWithConverter("disk_space_free_blocks", Converters.toLongConverter()),
            new ColumnWithConverter("disk_space_free_mb", Converters.toLongConverter()),
            new ColumnWithConverter("disk_space_free_percent", Converters.toLongConverter("%")));
    
    public static final String QUERY = "SELECT * FROM DISK_STORAGE;";
    public static final String METRIC_PATH = "Custom Metrics|Vertica|Disk Storage";

    public static StatsRequest request() {
        return new StatsRequest(QUERY, METRIC_PATH, APPENDER_COLUMNS, STAT_COLUMNS);
    }
}
