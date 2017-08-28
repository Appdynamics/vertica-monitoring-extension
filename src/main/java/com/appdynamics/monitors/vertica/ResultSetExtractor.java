package com.appdynamics.monitors.vertica;

import com.appdynamics.monitors.vertica.converter.Converter;
import com.appdynamics.monitors.vertica.stats.ColumnWithConverter;
import com.google.common.base.Strings;
import com.singularity.ee.agent.systemagent.api.exception.TaskExecutionException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

public class ResultSetExtractor {

    private static final Logger LOG = Logger.getLogger(ResultSetExtractor.class);

    public Map<String, Number> extract(ResultSet resultSet, String metricPath, List<String> appenderColumns, List<ColumnWithConverter> statColumns) throws TaskExecutionException {
        Map<String, Number> stats = new LinkedHashMap<String, Number>();

        try {
            while (resultSet.next()) {
                StringBuilder sb = new StringBuilder();
                for (String appenderColumn : appenderColumns) {
                    if (sb.length() != 0) {
                        sb.append("|");
                    }
                    String appenderColVal = resultSet.getString(appenderColumn);
                    //Replace ',' with '-' in the metric name
                    if (appenderColVal.contains(",")) {
                        appenderColVal = appenderColVal.replaceAll(",", "-");
                    }
                    sb.append(appenderColVal);
                }

                if (sb.length() > 0) {
                    sb.append("|");
                }
                for (ColumnWithConverter statColumn : statColumns) {
                    Converter<?> converter = statColumn.getConverter();
                    String columnName = statColumn.getColumnName();
                    String value = resultSet.getString(columnName);
                    if(!Strings.isNullOrEmpty(value)) {
                        stats.put(metricPath + "|" + sb.toString() + columnName, converter.convert(value));
                    }
                }
            }
            return stats;
        } catch (SQLException e) {
            LOG.error("Error while getting stats from result set", e);
            throw new TaskExecutionException();
        } finally {
            try {
                resultSet.close();
            } catch (SQLException e) {
                LOG.error("Error while closing connection", e);
            }
        }
    }
}
