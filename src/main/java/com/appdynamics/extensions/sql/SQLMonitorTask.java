package com.appdynamics.extensions.sql;

import com.appdynamics.extensions.util.AssertUtils;
import com.appdynamics.extensions.util.MetricWriteHelper;
import com.appdynamics.extensions.util.YmlUtils;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;



import org.codehaus.jackson.map.ObjectMapper;

public class SQLMonitorTask implements Runnable{

    private static final String METRIC_SEPARATOR = "|";
    private long previousTimestamp;
    private long currentTimestamp;
    private String metricPrefix;
    private MetricWriteHelper metricWriter;
    private JDBCConnectionAdapter jdbcAdapter;
    private Map server;
    private final Yaml yaml = new Yaml();
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(SQLMonitorTask.class);

    public void run(){
        MetricPrinter metricPrinter = new MetricPrinter(metricWriter);

        List<Map> queries = (List<Map>) server.get("queries");
        Connection connection = null;
        if (queries != null && !queries.isEmpty()) {

            try{
                for(Map query: queries){
                    connection = getConnection(connection);
                    Map<String , BigDecimal> values = executeQuery(connection, query);
                    printData(values, metricPrinter);
                    closeCurrentConnection(connection);
                }
            } catch(SQLException e){
                logger.error("Unable to open the jdbc connection",e);
            } catch (ClassNotFoundException e) {
                logger.error("Unable to load the driver ",e);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private void printData( Map<String , BigDecimal> values, MetricPrinter metricPrinter){

        for (String key : values.keySet()) {
            metricPrinter.reportMetric(key,values.get(key));
        }
    }
//    public void run2() {
//
//        List<Map> queries = (List<Map>) server.get("queries");
//
//        Connection connection = null;
//        if (queries != null && !queries.isEmpty()) {
//
//            try{
//                for(Map query: queries){
//                    connection = getConnection(connection);
//                    executeQuery(connection, query);
//                }
//            } catch(SQLException e){
//                logger.error("Unable to open the jdbc connection",e);
//            } catch (ClassNotFoundException e) {
//                logger.error("Unable to load the driver ",e);
//            }
//        }
//
//        }


        private Map<String , BigDecimal>  executeQuery(Connection connection, Map query) throws SQLException {

            String dbServerDisplayName = (String) server.get("displayName");
            String queryDisplayName = (String)query.get("displayName");

            ResultSet resultSet = getResultSet(connection, query);

            ColumnGenerator columnGenerator = new ColumnGenerator();
            List<Column> columns = columnGenerator.getColumns(query);

//            List<Column> columns = getColumns(query);

            MetricCollector metricCollector = new MetricCollector(metricPrefix,dbServerDisplayName,queryDisplayName);
            Map<String , BigDecimal> values = metricCollector.goThroughResultSet(resultSet,columns);
            return values;
        }



        private ResultSet getResultSet(Connection connection, Map query) throws SQLException {
            String statement = (String) query.get("queryStmt");
            statement = substitute(statement);
            ResultSet resultSet = jdbcAdapter.queryDatabase(connection, statement);
            return resultSet;
        }

        private Connection getConnection(Connection connection) throws SQLException, ClassNotFoundException {

            connection = jdbcAdapter.open((String)server.get("driver"));
            return connection;
        }

    private void closeCurrentConnection(Connection connection) throws Exception {

         jdbcAdapter.closeConnection(connection);
    }

    private String substitute(String statement) {
        String stmt = statement;
        stmt = stmt.replace("{{previousTimestamp}}",Long.toString(previousTimestamp));
        stmt = stmt.replace("{{currentTimestamp}}",Long.toString(currentTimestamp));
        return stmt;
    }

    public static class Builder {
        private SQLMonitorTask task = new SQLMonitorTask();

        Builder metricPrefix (String metricPrefix) {
            task.metricPrefix = metricPrefix;
            return this;
        }

        Builder metricWriter (MetricWriteHelper metricWriter) {
            task.metricWriter = metricWriter;
            return this;
        }

        Builder server (Map server) {
            task.server = server;
            return this;
        }

        Builder jdbcAdapter (JDBCConnectionAdapter adapter) {
            task.jdbcAdapter = adapter;
            return this;
        }

        Builder previousTimestamp(long timestamp){
            task.previousTimestamp = timestamp;
            return this;
        }

        Builder currentTimestamp(long timestamp){
            task.currentTimestamp = timestamp;
            return this;
        }

        SQLMonitorTask build () {
            return task;
        }
    }
}
