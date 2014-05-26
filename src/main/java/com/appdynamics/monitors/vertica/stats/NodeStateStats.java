package com.appdynamics.monitors.vertica.stats;

import com.appdynamics.monitors.vertica.converter.Converters;
import com.google.common.collect.Lists;
import java.util.List;

public class NodeStateStats {
    public static final List<String> APPENDER_COLUMNS = Lists.newArrayList("node_name");
    public static final List<ColumnWithConverter> STAT_COLUMNS = Lists.newArrayList(
            new ColumnWithConverter("node_state", Converters.toLongConverter(NodeState.class)));
    public static final String QUERY = "SELECT * FROM NODE_STATES LIMIT 1;";
    public static final String METRIC_PATH = "Custom Metrics|Vertica|Node State";

    public static StatsRequest request() {
        return new StatsRequest(QUERY, METRIC_PATH, APPENDER_COLUMNS, STAT_COLUMNS);
    }

    public enum NodeState {
        UP, READY, UNSAFE, SHUTDOWN, RECOVERING
    }
}