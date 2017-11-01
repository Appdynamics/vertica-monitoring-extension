package com.appdynamics.extensions.sql;


import com.appdynamics.extensions.conf.MonitorConfiguration;
import com.appdynamics.extensions.MetricWriteHelper;
import com.appdynamics.extensions.yml.YmlReader;

import static org.junit.Assert.assertTrue;

import org.junit.Assert;
import org.junit.Test;


import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

import static org.mockito.Mockito.*;

/**
 * Created by bhuvnesh.kumar on 9/28/17.
 *
 */


public class SQLMonitorTaskTest {

    private MetricWriteHelper metricWriter = mock(MetricWriteHelper.class);
//    private MetricPrinter metricPrinter = mock(MetricPrinter.class);

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

}
