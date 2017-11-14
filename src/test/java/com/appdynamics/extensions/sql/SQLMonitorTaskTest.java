package com.appdynamics.extensions.sql;


import com.appdynamics.extensions.conf.MonitorConfiguration;
import com.appdynamics.extensions.MetricWriteHelper;
import com.appdynamics.extensions.yml.YmlReader;
import static org.junit.Assert.*;

import static org.junit.Assert.assertTrue;

import com.sun.org.apache.regexp.internal.RE;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.LoggerFactory;
import org.mockito.ArgumentCaptor;
import com.google.common.collect.Lists;
import com.appdynamics.extensions.metrics.Metric;


import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;



import static org.mockito.Mockito.*;

/**
 * Created by bhuvnesh.kumar on 9/28/17.
 *
 */


public class SQLMonitorTaskTest {


    private long previousTimestamp = System.currentTimeMillis() ;
    private long currentTimestamp = System.currentTimeMillis();
    private String metricPrefix = "Custom Metrics";
    private MetricWriteHelper metricWriter = mock(MetricWriteHelper.class);
    JDBCConnectionAdapter jdbcAdapter = mock(JDBCConnectionAdapter.class);
    private Map server;

    @Mock
    private ResultSet resultSet = mock(ResultSet.class);

    @Before
    public void setUp() throws  Exception{

        try{
            when(resultSet.getString("NODE_NAME")).thenReturn("v_vmart_node0001");
            when(resultSet.getString("EVENT_ID")).thenReturn("6");
            when(resultSet.getString("Custom Metrics|Vertica|Active Events|v_vmart_node0001|6|EVENT_CODE")).thenReturn("6");
            when(resultSet.getString("Custom Metrics|Vertica|Active Events|v_vmart_node0001|6|EVENT_POSTED_COUNT")).thenReturn("1");

        } catch (Exception e){
            System.out.println(e);
        }

    }


    @Test
    public void testGetConnection() throws SQLException, ClassNotFoundException {
        ArgumentCaptor<List> pathCaptor = ArgumentCaptor.forClass(List.class);

        Map servers_yaml = YmlReader.readFromFileAsMap(new File("src/test/resources/conf/config1.yml"));
        List<Map<String, String>> servers = (List<Map<String, String>>) servers_yaml.get("dbServers");

         server = servers.get(0);
        currentTimestamp = System.currentTimeMillis();
        Connection connection= mock(Connection.class);
        when(jdbcAdapter.open((String)server.get("driver"))).thenReturn(connection);

        SQLMonitorTask sqlMonitorTask = new SQLMonitorTask.Builder().metricWriter(metricWriter)
                .metricPrefix(metricPrefix)
                .jdbcAdapter(jdbcAdapter)
                .previousTimestamp(previousTimestamp)
                .currentTimestamp(currentTimestamp)
                .server(server).build();
        Statement statement = connection.createStatement();

        ResultSet resultSet = mock(ResultSet.class);

        when(resultSet.next()).thenReturn(Boolean.TRUE,Boolean.FALSE);

        when(resultSet.getString("NODE_NAME")).thenReturn("v_vmart_node0001");
        when(resultSet.getString("EVENT_ID")).thenReturn("6");
        when(resultSet.getString("EVENT_CODE")).thenReturn("6");
        when(resultSet.getString("EVENT_POSTED_COUNT")).thenReturn("1");

        when(jdbcAdapter.queryDatabase("Select NODE_NAME, EVENT_CODE, EVENT_ID, EVENT_POSTED_COUNT from Active_events",statement )).thenReturn(resultSet);

        sqlMonitorTask.run();
        verify(metricWriter).transformAndPrintMetrics(pathCaptor.capture());
        List<String> metricPathsList = Lists.newArrayList();
        metricPathsList.add("Custom Metrics|Vertica|Active Events|v_vmart_node0001|6|EVENT_CODE");
        metricPathsList.add("Custom Metrics|Vertica|Active Events|v_vmart_node0001|6|EVENT_POSTED_COUNT");

        for (Metric metric : (List<Metric>)pathCaptor.getValue()){
            org.junit.Assert.assertTrue(metricPathsList.contains(metric.getMetricPath()));
        }

    }





}
