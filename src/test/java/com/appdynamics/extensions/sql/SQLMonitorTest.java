package com.appdynamics.extensions.sql;

import com.appdynamics.extensions.sql.SQLMonitor;
import com.google.common.collect.Maps;
import com.singularity.ee.agent.systemagent.api.TaskOutput;
import com.singularity.ee.agent.systemagent.api.exception.TaskExecutionException;

import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;


import java.util.HashMap;
import java.util.Map;
import java.util.HashMap;

/**
 * Created by bhuvnesh.kumar on 9/28/17.
 */
public class SQLMonitorTest {

    private static final String CONFIG_ARG = "config-file";

    private SQLMonitor testClass;

//    @Before
//    public void init() throws Exception {
//
//        testClass = new SQLMonitor();
//    }


    @Test
    public void testSQLMonitoringExtension () throws TaskExecutionException{
        SQLMonitor sqlMonitor = new SQLMonitor();
         Map<String, String> taskArgs = new HashMap<String, String>();
        taskArgs.put(CONFIG_ARG, "/Users/bhuvnesh.kumar/repos/appdynamics/extensions/frb-sql-monitoring-extension/src/test/resources/conf/config.yml");
        sqlMonitor.execute(taskArgs, null);

    }
}
