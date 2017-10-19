package com.appdynamics.extensions.sql;

import com.appdynamics.extensions.sql.Columns;
import com.appdynamics.extensions.sql.SQLMonitorTask;
import com.google.common.collect.Maps;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Map;

/**
 * Created by bhuvnesh.kumar on 9/21/17.
 */
public class TestYml {

    @Test
    public void test() throws FileNotFoundException {
        Yaml yaml = new Yaml();
        File f = new File("/Users/bhuvnesh.kumar/repos/appdynamics/extensions/frb-sql-monitoring-extension/src/test/resources/conf/config1.yml");
        Map config = yaml.loadAs(new FileInputStream(f),Map.class);
        final ObjectMapper mapper = new ObjectMapper(); // jackson’s objectmapper
        final Columns columns = mapper.convertValue(config, Columns.class);
        Assert.assertTrue(columns != null);
    }

//    @Test
//    public void testFilterMapOfStringMapsOfStringAndString() {
//        final Map<String, Map<String, String>> filteredOnKeyMap = Maps.newLinkedHashMap();
//        Map<String, String> map1 = Maps.newLinkedHashMap();
//        map1.put("hello1", "val1");
//        filteredOnKeyMap.put("hello1", map1);
//        map1.put("hello2", "val2");
//        filteredOnKeyMap.put("hello2", map1);
//
//        Assert.assertTrue(filteredOnKeyMap.size() == 2);
//
//        // Map<String, Map<String, String>> hello1 = SQLMonitorTask.filterMap(filteredOnKeyMap, "hello1");
//        Map<String, Map<String, String>> hello1 = SQLMonitorTask.filterMap(filteredOnKeyMap, "hello1");
//
//        Assert.assertTrue(hello1.containsKey("hello1"));

//    }
}
