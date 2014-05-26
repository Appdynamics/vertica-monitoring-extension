package com.appdynamics.monitors.vertica.stats;

import com.appdynamics.monitors.vertica.ResultSetExtractor;
import com.appdynamics.monitors.vertica.executor.QueryExecutor;
import com.singularity.ee.agent.systemagent.api.exception.TaskExecutionException;
import java.sql.ResultSet;
import java.util.Map;

public class StatsCollector {

    private final QueryExecutor queryExecutor;
    private final ResultSetExtractor resultSetExtractor;

    public StatsCollector(QueryExecutor queryExecutor, ResultSetExtractor resultSetExtractor) {
        this.queryExecutor = queryExecutor;
        this.resultSetExtractor = resultSetExtractor;
    }

    public Map<String, Number> collectStats(StatsRequest request) throws TaskExecutionException {
        ResultSet resultSet = queryExecutor.executeQuery(request.getQuery());
        Map<String, Number> stats = resultSetExtractor.extract(resultSet, request.getMetricPath(), request.getAppenderColumns(), request.getStatColumns());
        return stats;
    }
}
