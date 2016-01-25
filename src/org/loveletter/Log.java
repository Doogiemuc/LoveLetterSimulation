package org.loveletter;

import java.io.PrintStream;

public class Log {
    private static PrintStream stream = System.out;
    public static final boolean doLog = true;
    
    static StringBuffer buf = new StringBuffer();
    
    public static void trace(String msg) {
        if (doLog) {
            stream.println("TRACE " + msg);
        }
    }
    
    public static void warn(String msg) {
        if (doLog) {
            stream.println("WARN  " + msg);
        }
    }
    
    public static void error(String msg) {
        if (doLog) {
            stream.println("ERROR " + msg);
        }
    }
    
    public static void traceAppend(String msg) {
        buf.append(msg);
    }
    
    public static void traceFlush() {
        if (doLog) {
            stream.println("TRACE "+buf.toString());
            buf = new StringBuffer();
        }
    }
    
    
}
