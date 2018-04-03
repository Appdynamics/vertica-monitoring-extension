/*
 *   Copyright 2018. AppDynamics LLC and its affiliates.
 *   All Rights Reserved.
 *   This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 *   The copyright notice above does not evidence any actual or intended publication of such source code.
 *
 */

package com.appdynamics.extensions.sql;

import com.appdynamics.extensions.metrics.Metric;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by bhuvnesh.kumar on 9/29/17.
 */
public class MetricCollector {
    private static final String METRIC_SEPARATOR = "|";
    private String metricPrefix;
    private String dbServerDisplayName;
    private String queryDisplayName;
    private List<Map<String, String>> metricReplacer;
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(MetricCollector.class);


    public MetricCollector(String metricPrefix, String dbServerDisplayName, String queryDisplayName, List<Map<String, String>> metricReplacer) {
        this.metricPrefix = metricPrefix;
        this.dbServerDisplayName = dbServerDisplayName;
        this.queryDisplayName = queryDisplayName;
        this.metricReplacer = metricReplacer;
    }


    public Map<String, Metric> goingThroughResultSet(ResultSet resultSet, List<Column> columns) throws SQLException {
        List<Metric> list_of_metrics = new ArrayList<Metric>();
        Map<String, Metric> mapOfMetrics = new HashMap<String, Metric>();
        logger.debug("Going through ResultSet for Database: {} and Query: {}", dbServerDisplayName, queryDisplayName);

        while (resultSet != null && resultSet.next()) {
            String metricPath = "";
            metricPath = getMetricPrefix(dbServerDisplayName, queryDisplayName);
            for (Column c : columns) {
                if (c.getType().equals("metricPathName")) {
                    metricPath += METRIC_SEPARATOR + resultSet.getString(c.getName());


                } else if (c.getType().equals("metricValue")) {
                    String updatedMetricPath = metricPath + METRIC_SEPARATOR + c.getName();
                    String val = resultSet.getString(c.getName());

                    if (val != null) {
                        val = replaceCharacter(val);
                        updatedMetricPath = replaceCharacter(updatedMetricPath);
                        Metric current_metric;
                        if (c.getProperties() != null) {
                            current_metric = new Metric(c.getName(), val, updatedMetricPath, c.getProperties());
                        } else {
                            current_metric = new Metric(c.getName(), val, updatedMetricPath);
                        }
                        list_of_metrics.add(current_metric);
                        mapOfMetrics.put(updatedMetricPath,current_metric);
                    }
                }
            }
        }

//        printList(list_of_metrics);
//        printMap(mapOfMetrics);
//        return list_of_metrics;
        return mapOfMetrics;
    }

//    private void printMap(Map<String, Metric> metrics) {
//
//        for ( String metricPath : metrics.keySet()) {
//            System.out.println(metricPath + " -> " + metrics.get(metricPath).getMetricValue());
//        }
//    }
//
//    private void printList(List<Metric> metrics) {
//
//        for (Metric metric : metrics) {
//            System.out.println(metric.getMetricPath() + " :: " + metric.getMetricValue());
//        }
//    }

    private String replaceCharacter(String metricPath) {

        for (Map chars : metricReplacer) {
            String replace = (String) chars.get("replace");
            String replaceWith = (String) chars.get("replaceWith");

            if (metricPath.contains(replace)) {
                metricPath = metricPath.replaceAll(replace, replaceWith);
            }
        }
        return metricPath;
    }

    private String getMetricPrefix(String dbServerDisplayName, String queryDisplayName) {
        return metricPrefix + METRIC_SEPARATOR + dbServerDisplayName + METRIC_SEPARATOR + queryDisplayName;
    }

}
