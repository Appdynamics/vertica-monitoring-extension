package com.appdynamics.extensions.sql;

import com.appdynamics.extensions.ABaseMonitor;
import com.appdynamics.extensions.TaskInputArgs;
import com.appdynamics.extensions.TasksExecutionServiceProvider;
import com.appdynamics.extensions.crypto.CryptoUtil;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.singularity.ee.agent.systemagent.api.exception.TaskExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.appdynamics.extensions.TaskInputArgs.PASSWORD_ENCRYPTED;


public class SQLMonitor extends ABaseMonitor {

    private static final Logger logger = LoggerFactory.getLogger(SQLMonitor.class);
    private long previousTimestamp = 0;
    private long currentTimestamp = System.currentTimeMillis();
    private static final String CONFIG_ARG = "config-file";

    @Override
    protected String getDefaultMetricPrefix() {
    return "Custom Metrics|Vertica";
    }

    @Override
    public String getMonitorName() {
        return "Vertica Monitor";
    }

    @Override
    protected void doRun(TasksExecutionServiceProvider serviceProvider) {

        List<Map<String, String>> servers = (List<Map<String, String>>) configuration.getConfigYml().get("dbServers");

        previousTimestamp = currentTimestamp;
        currentTimestamp = System.currentTimeMillis();
        if(previousTimestamp != 0) {
            for (Map<String, String> server : servers) {
                try {
                    SQLMonitorTask task = createTask(server, serviceProvider);
                    serviceProvider.submit(server.get("displayName"), task);
                } catch (IOException e) {
                    logger.error("Cannot construct JDBC uri for {}", Util.convertToString(server.get("displayName"), ""));
                }
            }
        }
    }

    @Override
    protected int getTaskCount() {
        List<Map<String, String>> servers = (List<Map<String, String>>) configuration.getConfigYml().get("dbServers");
        return servers.size();
    }

    private String createConnectionUrl(Map server) {
        String url = Util.convertToString(server.get("connectionUrl"), "");
        return url;
    }

    private SQLMonitorTask createTask(Map server, TasksExecutionServiceProvider serviceProvider) throws IOException {
        String connUrl = createConnectionUrl(server);
        Map<String, String> connectionProperties = getConnectionProperties(server);
        JDBCConnectionAdapter jdbcAdapter = JDBCConnectionAdapter.create(connUrl, connectionProperties);

        return new SQLMonitorTask.Builder()
                .metricWriter(serviceProvider.getMetricWriteHelper())
                .metricPrefix(configuration.getMetricPrefix())
                .jdbcAdapter(jdbcAdapter)
                .previousTimestamp(previousTimestamp)
                .currentTimestamp(currentTimestamp)
                .server(server).build();

    }

    private Map<String, String> getConnectionProperties(Map server) {
        Map<String, String> connectionProperties = new LinkedHashMap<String, String>();
        List<Map<String, String>> listOfMaps = (List<Map<String, String>>) server.get("connectionProperties");

        for (Map amap : listOfMaps) {
            for (Object key : amap.keySet()) {
                if (key == "password") {
                    String password = getPassword(server, (String) amap.get(key));
                    connectionProperties.put((String) key, password);
                } else {
                    connectionProperties.put((String) key, (String) amap.get(key));
                }
            }
        }
        return connectionProperties;
    }

    private String getPassword(Map server, String normal_password) {
        String encryptionPassword = Util.convertToString(server.get("encryptedPassword"), "");
        String encryptionKey = Util.convertToString(server.get("encryptionKey"), "");
        String password;
        if (!Strings.isNullOrEmpty(encryptionKey) && !Strings.isNullOrEmpty(encryptionPassword)) {
            password = getEncryptedPassword(encryptionKey, encryptionPassword);
        } else {
            password = normal_password;
        }
        return password;
    }

    private String getEncryptedPassword(String encryptionKey, String encryptedPassword) {
        java.util.Map<String, String> cryptoMap = Maps.newHashMap();
        cryptoMap.put(PASSWORD_ENCRYPTED, encryptedPassword);
        cryptoMap.put(TaskInputArgs.ENCRYPTION_KEY, encryptionKey);
        return CryptoUtil.getPassword(cryptoMap);
    }

    public static void main(String[] args) throws TaskExecutionException {

        final SQLMonitor monitor = new SQLMonitor();
        final Map<String, String> taskArgs = new HashMap<String, String>();

        taskArgs.put(CONFIG_ARG, "/Users/bhuvnesh.kumar/repos/appdynamics/extensions/vertica-monitoring-extension/src/test/resources/conf/config_generic.yml");

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
