/*
 *   Copyright 2018. AppDynamics LLC and its affiliates.
 *   All Rights Reserved.
 *   This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 *   The copyright notice above does not evidence any actual or intended publication of such source code.
 *
 */

package com.appdynamics.extensions.sql;

import com.appdynamics.extensions.ABaseMonitor;
import com.appdynamics.extensions.TaskInputArgs;
import com.appdynamics.extensions.TasksExecutionServiceProvider;
import com.appdynamics.extensions.crypto.CryptoUtil;
import com.appdynamics.extensions.util.AssertUtils;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import static com.appdynamics.extensions.TaskInputArgs.ENCRYPTED_PASSWORD;


public class SQLMonitor extends ABaseMonitor {

    private static final Logger logger = LoggerFactory.getLogger(SQLMonitor.class);
    private long previousTimestamp = 0;
    private long currentTimestamp = System.currentTimeMillis() / 1000L;
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
        currentTimestamp = System.currentTimeMillis() / 1000L;
        if (previousTimestamp != 0) {
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


    private SQLMonitorTask createTask(Map server, TasksExecutionServiceProvider serviceProvider) throws IOException {
        String connUrl = createConnectionUrl(server);

        AssertUtils.assertNotNull(serverName(server), "The 'displayName' field under the 'dbServers' section in config.yml is not initialised");
        AssertUtils.assertNotNull(createConnectionUrl(server), "The 'connectionUrl' field under the 'dbServers' section in config.yml is not initialised");
        AssertUtils.assertNotNull(driverName(server), "The 'driver' field under the 'dbServers' section in config.yml is not initialised");

        logger.debug("Task Created");
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

    private String serverName(Map server) {
        String name = Util.convertToString(server.get("displayName"), "");
        return name;
    }

    private String driverName(Map server) {
        String name = Util.convertToString(server.get("driver"), "");
        return name;
    }


    private String createConnectionUrl(Map server) {
        String url = Util.convertToString(server.get("connectionUrl"), "");
        return url;
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
        cryptoMap.put(ENCRYPTED_PASSWORD, encryptedPassword);
        cryptoMap.put(TaskInputArgs.ENCRYPTION_KEY, encryptionKey);
        return CryptoUtil.getPassword(cryptoMap);
    }


}
