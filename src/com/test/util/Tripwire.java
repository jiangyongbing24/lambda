package com.test.util;

import sun.util.logging.PlatformLogger;

import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * @Created by JYB
 * @Date 2019/7/13 20:20
 * @Description TODO
 */
final class Tripwire {
    private static final String TRIPWIRE_PROPERTY = "org.openjdk.java.util.stream.tripwire";

    static final boolean ENABLE = AccessController.doPrivileged(
            (PrivilegedAction<Boolean>) () -> Boolean.getBoolean(TRIPWIRE_PROPERTY));

    private Tripwire(){}

    static void trip(Class<?> trippingClass, String msg){
        PlatformLogger.getLogger(trippingClass.getCanonicalName()).warning(msg);
    }
}
