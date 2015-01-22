package com.appdynamics.monitors.vertica.stats;

import com.appdynamics.monitors.vertica.converter.Converters;
import com.google.common.collect.Lists;
import java.util.List;

public class ActiveEventsStats {

    public static final List<String> APPENDER_COLUMNS = Lists.newArrayList("node_name", "event_id");
    public static final List<ColumnWithConverter> STAT_COLUMNS = Lists.newArrayList(
            new ColumnWithConverter("event_code", Converters.toLongConverter()),
            new ColumnWithConverter("event_posted_count", Converters.toLongConverter()));

    public static final String QUERY = "SELECT * FROM ACTIVE_EVENTS";
    public static final String METRIC_PATH = "Active Events";

    public static StatsRequest request(String metricPrefix) {
        return new StatsRequest(QUERY, metricPrefix+METRIC_PATH, APPENDER_COLUMNS, STAT_COLUMNS);
    }
}