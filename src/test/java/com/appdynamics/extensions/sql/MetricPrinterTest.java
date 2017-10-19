package com.appdynamics.extensions.sql;

import com.appdynamics.extensions.util.MetricWriteHelper;
import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


/**
 * Created by bhuvnesh.kumar on 9/28/17.
 */
public class MetricPrinterTest {

    private MetricWriteHelper metricWriter = mock(MetricWriteHelper.class);

    private String metricPrefix = "Custom Metrics|Tibco ASG|";
    private String displayName = "FRB-Try";



    @Test
    public void reportNodeMetricsTest () {
        MetricPrinter metricPrinter = new MetricPrinter( metricWriter);

        String metricName = "MetricName";
        BigDecimal value = BigDecimal.valueOf(89);
        metricPrinter.reportMetric(metricName, value);
//        verify(metricPrinter, times(1)).reportMetric(metricName, value);
    }


}
