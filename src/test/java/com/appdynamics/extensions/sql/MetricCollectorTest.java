package com.appdynamics.extensions.sql;

import com.appdynamics.extensions.yml.YmlReader;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.junit.Test;

import java.io.File;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Created by bhuvnesh.kumar on 10/5/17.
 */
public class MetricCollectorTest {
    private static final String METRIC_SEPARATOR = "|";
    private String metricPrefix = "metricPrefix";
    private String dbServerDisplayName = "dbServer";

    private String queryDisplayName = "queryName" ;


    @Test
    public void testGoThroughResultSetCorrectValues() throws SQLException {
        Map<String , BigDecimal> values = new HashMap<String, BigDecimal>();

        ResultSet resultSet = mock(ResultSet.class);

        when(resultSet.next()).thenReturn(Boolean.TRUE,Boolean.FALSE);


        String num1 = "6";
        String num2 = "7";

        BigDecimal value1 = BigDecimal.valueOf(6);
        BigDecimal value2 = BigDecimal.valueOf(7);


        when(resultSet.getString("NODE_NAME")).thenReturn("metricPathName");
        when(resultSet.getString("AVERAGE_MEMORY_USAGE_PERCENT")).thenReturn(num1);
        when(resultSet.getString("AVERAGE_CPU_USAGE_PERCENT")).thenReturn(num2);

        when(resultSet.getBigDecimal("AVERAGE_MEMORY_USAGE_PERCENT")).thenReturn(value1);
        when(resultSet.getBigDecimal("AVERAGE_CPU_USAGE_PERCENT")).thenReturn(value2);


        Map queries = YmlReader.readFromFileAsMap(new File("src/test/resources/conf/config_for_columns.yml"));
        ColumnGenerator columnGenerator = new ColumnGenerator();
        List<Column> columns = columnGenerator.getColumns(queries);

        MetricCollector metricCollector = new MetricCollector(metricPrefix,dbServerDisplayName,queryDisplayName);

        Map<String , BigDecimal> resultFromAnswer = metricCollector.goThroughResultSet(resultSet,columns);


        Assert.assertTrue(resultFromAnswer.containsKey("metricPrefix|dbServer|queryName|metricPathName|AVERAGE_CPU_USAGE_PERCENT"));
        Assert.assertTrue(value2 == resultFromAnswer.get("metricPrefix|dbServer|queryName|metricPathName|AVERAGE_CPU_USAGE_PERCENT"));

        Assert.assertTrue(resultFromAnswer.containsKey("metricPrefix|dbServer|queryName|metricPathName|AVERAGE_MEMORY_USAGE_PERCENT"));
        Assert.assertTrue(value1 == resultFromAnswer.get("metricPrefix|dbServer|queryName|metricPathName|AVERAGE_MEMORY_USAGE_PERCENT"));

        Assert.assertTrue(resultFromAnswer.size() == 2);
    }




}
