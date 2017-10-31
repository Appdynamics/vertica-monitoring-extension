package com.appdynamics.extensions.sql.utils;

import java.util.List;
import com.appdynamics.extensions.sql.utils.MetricCharacterReplacer;

/**
 * Created by bhuvnesh.kumar on 10/27/17.
 */
public class MetricReplacerList {

    private List<MetricCharacterReplacer> metricCharacterReplacer;

    public List<MetricCharacterReplacer> getMetricCharacterReplacer() {
        return metricCharacterReplacer;
    }

    public void setMetricCharacterReplacer(List<MetricCharacterReplacer> metricCharacterReplacer) {
        this.metricCharacterReplacer = metricCharacterReplacer;
    }


}
