package com.appdynamics.extensions.sql;

import com.appdynamics.extensions.ABaseMonitor;
import com.appdynamics.extensions.AMonitorRunContext;
import com.appdynamics.extensions.conf.MonitorConfiguration;
import com.appdynamics.extensions.TaskInputArgs;

import com.appdynamics.extensions.crypto.CryptoUtil;
import com.appdynamics.extensions.util.AssertUtils;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.singularity.ee.agent.systemagent.api.AManagedMonitor;
import com.singularity.ee.agent.systemagent.api.exception.TaskExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.appdynamics.extensions.sql.utils.Constants.DEFAULT_METRIC_PREFIX;
import static com.appdynamics.extensions.TaskInputArgs.PASSWORD_ENCRYPTED;


public class SQLMonitor extends ABaseMonitor{

    private static final Logger logger = LoggerFactory.getLogger(SQLMonitor.class);
    private long previousTimestamp,currentTimestamp;
    private static final String CONFIG_ARG = "config-file";

    @Override
    protected String getDefaultMetricPrefix() {
        return DEFAULT_METRIC_PREFIX;
    }

    @Override
    public String getMonitorName() {
        return "Vertica Monitor";
    }

    @Override
    protected void doRun(AMonitorRunContext taskExecutor) {
        List<Map<String,String>> servers = (List<Map<String,String>>)configuration.getConfigYml().get("dbServers");
//        AssertUtils.assertNotNull(servers, "The 'servers' section in config.yml is not initialised");

        for (Map<String, String> server : servers) {

            try {
                SQLMonitorTask task = createTask(server, taskExecutor);
                taskExecutor.submit(server.get("displayName"),task);
                }

        catch (IOException  e){
            logger.error("Cannot construct JDBC uri for {}", Util.convertToString(server.get("displayName"),""));

        }
        }
    }

    @Override
    protected int getTaskCount() {
        List<Map<String,String>> servers = (List<Map<String,String>>)configuration.getConfigYml().get("dbServers");
//        AssertUtils.assertNotNull(servers, "The 'servers' section in config.yml is not initialised");
        return servers.size();
    }



    // Adding Vertica Functions

    private String createConnectionUrl (Map server){

        String url = Util.convertToString(server.get("connectionUrl"),"");

        return url;
    }



    private String getPassword(Map server, String normal_password){

        String encryptionPassword = Util.convertToString(server.get("encryptedPassword"),"");
        String encryptionKey = Util.convertToString(server.get("encryptionKey"),"");
        String password;
        if(!Strings.isNullOrEmpty(encryptionKey) && !Strings.isNullOrEmpty(encryptionPassword)){
            password = getEncryptedPassword(encryptionKey,encryptionPassword);
        }
        else{
            password = normal_password;
        }

        return password;

    }

    private SQLMonitorTask createTask(Map server, AMonitorRunContext taskExecutor) throws IOException {
        String connUrl = createConnectionUrl(server);
        Map<String, String> connectionProperties = getConnectionProperties(server);



        //#TODO check if MA classloader is needed
        Thread.currentThread().setContextClassLoader(AManagedMonitor.class.getClassLoader());

        JDBCConnectionAdapter jdbcAdapter = JDBCConnectionAdapter.create(connUrl, connectionProperties);

//        JDBCConnectionAdapter jdbcAdapter = JDBCConnectionAdapter.create(connUrl, user, password);
        return new SQLMonitorTask.Builder()
                .metricWriter(taskExecutor.getMetricWriteHelper())
                .metricPrefix(configuration.getMetricPrefix())
                .jdbcAdapter(jdbcAdapter)
                .previousTimestamp(previousTimestamp)
                .currentTimestamp(currentTimestamp)
                .server(server).build();

    }

    private Map<String, String > getConnectionProperties(Map server){
        Map<String, String > connectionProperties = new LinkedHashMap<String, String>();
//        connectionProperties = (Map<String, ArrayList<LinkedHashMap<String, String >>>)server.get("connectionProperties");
        ArrayList<LinkedHashMap<String, String>> arrayList = (ArrayList<LinkedHashMap<String, String>>)server.get("connectionProperties");

        for(LinkedHashMap linkedHashMap : arrayList){
            for(Object key: linkedHashMap.keySet()){
                if(key == "password") {
                    String password = getPassword(server,(String)linkedHashMap.get(key));
                    connectionProperties.put((String)key, password);
                }
                else{
                    connectionProperties.put((String)key, (String)linkedHashMap.get(key));
                }

            }
        }

        return connectionProperties;
    }

    private String getEncryptedPassword(String encryptionKey,String encryptedPassword) {
        java.util.Map<String,String> cryptoMap = Maps.newHashMap();
        cryptoMap.put(PASSWORD_ENCRYPTED,encryptedPassword);
        cryptoMap.put(TaskInputArgs.ENCRYPTION_KEY,encryptionKey);
        return CryptoUtil.getPassword(cryptoMap);
    }


    // End of Vertica Functions

    public static void main(String[] args) throws TaskExecutionException {

        final SQLMonitor monitor = new SQLMonitor();
        final Map<String, String> taskArgs = new HashMap<String, String>();
//        taskArgs.put(CONFIG_ARG, "/Users/bhuvnesh.kumar/repos/appdynamics/extensions/frb-sql-monitoring-extension/src/test/resources/conf/config.yml");

        taskArgs.put(CONFIG_ARG, "/Users/bhuvnesh.kumar/repos/appdynamics/extensions/vertica-monitoring-extension/src/test/resources/conf/config_generic.yml");

//        monitor.execute(taskArgs, null);


        //monitor.execute(taskArgs, null);


        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(new Runnable() {
            public void run() {
                try {
                    monitor.execute(taskArgs, null);
                } catch (Exception e) {
                    logger.error("Error while running the task", e);
                }
            }
        }, 2, 10, TimeUnit.SECONDS);
    }


}

//        //#TODO check if MA classloader is needed
//        Thread.currentThread().setContextClassLoader(AManagedMonitor.class.getClassLoader());
//