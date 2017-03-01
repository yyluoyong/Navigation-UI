package com.navigation_ui.tools;

import static org.junit.Assert.*;

/**
 * Created by Yong on 2017/3/1.
 */
public class TelephoneAreaCodeTest {
    @org.junit.Test
    public void getTelephoneAreaByCode() throws Exception {
        assertEquals("四川 绵阳", TelephoneAreaCode.getTelephoneAreaByCode("0816"));
        assertEquals("四川 成都", TelephoneAreaCode.getTelephoneAreaByCode("028"));
        assertEquals("湖南 益阳", TelephoneAreaCode.getTelephoneAreaByCode("0737"));
        assertEquals("湖南 长沙", TelephoneAreaCode.getTelephoneAreaByCode("0731"));
    }

}