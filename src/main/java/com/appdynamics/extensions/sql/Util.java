package com.appdynamics.extensions.sql;


import java.math.BigDecimal;
import java.math.RoundingMode;

public class Util {

    static String toBigIntString(final BigDecimal bigD) {
        return bigD.setScale(0, RoundingMode.HALF_UP).toBigInteger().toString();
    }

    static String convertToString(final Object field,final String defaultStr){
        if(field == null){
            return defaultStr;
        }
        return field.toString();
    }


}
