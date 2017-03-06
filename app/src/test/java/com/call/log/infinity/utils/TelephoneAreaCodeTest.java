package com.call.log.infinity.utils;

import static org.junit.Assert.*;

/**
 * Created by Yong on 2017/3/1.
 */
public class TelephoneAreaCodeTest {
    @org.junit.Test
    public void getTelephoneAreaByCode() throws Exception {
        assertEquals("四川 绵阳", TelephoneAreaCodeUtil.getTelephoneAreaByCode("0816"));
        assertEquals("四川 成都", TelephoneAreaCodeUtil.getTelephoneAreaByCode("028"));
        assertEquals("湖南 益阳", TelephoneAreaCodeUtil.getTelephoneAreaByCode("0737"));
        assertEquals("湖南 长沙", TelephoneAreaCodeUtil.getTelephoneAreaByCode("0731"));
    }

}