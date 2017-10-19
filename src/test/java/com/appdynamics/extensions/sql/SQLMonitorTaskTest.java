package com.appdynamics.extensions.sql;


import com.appdynamics.extensions.conf.MonitorConfiguration;
import com.appdynamics.extensions.sql.SQLMonitor;
import com.appdynamics.extensions.sql.SQLMonitorTask;
import com.appdynamics.extensions.sql.MetricPrinter;
import com.appdynamics.extensions.util.MetricWriteHelper;
import com.appdynamics.extensions.util.MetricWriteHelperFactory;
import com.appdynamics.extensions.yml.YmlReader;
import com.google.common.collect.Maps;
import com.singularity.ee.agent.systemagent.api.MetricWriter;
import com.singularity.ee.agent.systemagent.api.TaskOutput;
import com.singularity.ee.agent.systemagent.api.exception.TaskExecutionException;

import static org.junit.Assert.assertTrue;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


import java.io.File;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.HashMap;

import static org.mockito.Mockito.*;

/**
 * Created by bhuvnesh.kumar on 9/28/17.
 *
 */


public class SQLMonitorTaskTest {

    private MetricWriteHelper metricWriter = mock(MetricWriteHelper.class);
    private MetricPrinter metricPrinter = mock(MetricPrinter.class);

    private String metricPrefix = "Server|Tibco ASG";
    private String displayName = "FRB-Try";
    private static final String CONFIG_FILE_PARAM = "config-file";
    private MonitorConfiguration configuration;

     JDBCConnectionAdapter jdbcAdapter = mock(JDBCConnectionAdapter.class);

    @Test
    public void testNotEmptyQuery(){
        Map queries = YmlReader.readFromFileAsMap(new File("src/test/resources/conf/config_query.yml"));
        Assert.assertTrue(queries != null);
        Assert.assertFalse(queries.isEmpty());

        ArrayList check1 = (ArrayList) queries.get("queries");
        Map check2 = (Map) check1.get(0);
        ArrayList check3 = (ArrayList) check2.get("columns");
        Map check4 = (Map) check3.get(0);
        String check5 = (String) check4.get("name");

        Assert.assertTrue( check5.equals("TRN_TARGET_OPERATION"));
    }

    @Test
    public void testGetConnection() throws SQLException, ClassNotFoundException {

        Map server = YmlReader.readFromFileAsMap(new File("src/test/resources/conf/config.yml"));

        Connection connection= mock(Connection.class);
        when(jdbcAdapter.open((String)server.get("driver"))).thenReturn(connection);

    }

    @Test
    public void testRunFunctionality(){
        Map file = YmlReader.readFromFileAsMap(new File("src/test/resources/conf/config_query.yml"));
        ArrayList queries = (ArrayList) file.get("queries");


    }

//    @Test
//    public void testRun(){

//        Map<String, String> taskParams = new HashMap<String, String>();
//        taskParams.put(CONFIG_FILE_PARAM, "src/test/resources/conf/config_query.yml");
////        Map<String, ?> config = configuration.getConfigYml();
////        List<Map> servers = (List<Map>) config.get("dbServers");
//
//        MetricPrinter metricPrinter = new MetricPrinter(metricWriter);
//
//        Map mapColumn1 = new HashMap();
//        mapColumn1.put("name", "TRN_TARGET_OPERATION");
//        mapColumn1.put("type","metricPathName");
//
//        Map mapColumn2 = new HashMap();
//        mapColumn2.put("name", "TRN_FACADE_DURATION");
//        mapColumn2.put("type","metricValue");
//
//        Map mapColumn3 = new HashMap();
//        mapColumn3.put("name", "TRN_ROUTER_DURATION");
//        mapColumn3.put("type","metricValue");
//
//        ArrayList allColumns = new ArrayList();
//        allColumns.add(mapColumn1);
//        allColumns.add(mapColumn2);
//        allColumns.add(mapColumn3);
//
//        Map query1 = new HashMap();
//        query1.put("display1", "Qu1");
//        query1.put("queryStmt", "SELECT TRN_TARGET_OPERATION, TRN_FACADE_DURATION, TRN_ROUTER_DURATION FROM ASG_TRANSACTIONS WHERE TRN_TARGET_OPERATION IN ('target1','target2','target3','target4','target5')");
//        query1.put("columns",allColumns);
//
//
//        List<Map> queries = new ArrayList<Map>();
//        queries.add(query1);
//
//        String query = queries.get(0).get("queryStmt").toString();
//        Assert.assertTrue(query != null);
//        Object columnNameCheck = queries.get(0).get("columns");

//        String columnNameCheck1 = queries.get(0).get("columns").get(0).get("name").toString();
//
//
//        Assert.assertTrue(queries != null);
//         Assert.assertFalse(queries.isEmpty());
//
//        String dbServerDisplayName = displayName;
//
////        MockConnection connection = getJDBCMockObjectFactory().getMockConnection();
//
//        SQLMonitorTask temp = new SQLMonitorTask();
//        temp.run();
//
//    }
}
