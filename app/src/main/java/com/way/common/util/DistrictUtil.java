package com.way.common.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by miguangshu on 2016/6/1.
 */
public class DistrictUtil {
    private static Map<String,String> DISTRICT = new HashMap<String,String>();
    static{
        DISTRICT.put("海淀区","00101");
        DISTRICT.put("石景山区","00102");
        DISTRICT.put("丰台区","00103");
        DISTRICT.put("房山区","00104");
        DISTRICT.put("朝阳区","00105");
        DISTRICT.put("西城区","00106");
        DISTRICT.put("东城区","00107");
        DISTRICT.put("大兴区","00108");
        DISTRICT.put("通州区","00109");
        DISTRICT.put("顺义区","00110");
        DISTRICT.put("昌平区","00111");
        DISTRICT.put("门头沟区","00112");
        DISTRICT.put("平谷区","00113");
        DISTRICT.put("怀柔区","00114");
        DISTRICT.put("密云区","00115");
        DISTRICT.put("延庆区","00116");
    }
    public static String getDistrictNumbmer(String districtName){
        return DISTRICT.get(districtName);
    }
}
