package com.navigation_ui.utils;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Yong on 2017/2/26.
 */
public class CallDurationFormatterTest {
    @Test
    public void format() throws Exception {
        for (int i=0; i < 60; i++) {
            assertEquals(i + "秒", CallDurationFormatter.format(i));
        }

        assertEquals("1分0秒", CallDurationFormatter.format(60));
        assertEquals("2分8秒", CallDurationFormatter.format(128));

        assertEquals("59分59秒", CallDurationFormatter.format(3599));
        assertEquals("1小时0分0秒", CallDurationFormatter.format(3600));
        assertEquals("1小时0分1秒", CallDurationFormatter.format(3601));
        assertEquals("1小时1分0秒", CallDurationFormatter.format(3660));
        assertEquals("1小时1分1秒", CallDurationFormatter.format(3661));
    }

}