package org.loveletter;

import java.io.PrintStream;

public class Log {
    private static PrintStream stream = System.out;
    public static final boolean logTRACE = true;
    public static final boolean logINFO  = true;
    public static final boolean logWARN  = true;
    public static final boolean logERROR = true;
    
    static StringBuffer buf = new StringBuffer();
    
    public static void trace(String msg) {
        if (logTRACE) {
            stream.println("TRACE " + msg);
        }
    }

    public static void info(String msg) {
        if (logINFO) {
            stream.println("INFO  " + msg);
        }
    }
    
    public static void warn(String msg) {
        if (logWARN) {
            stream.println("WARN  " + msg);
        }
    }
    
    public static void error(String msg) {
        if (logERROR) {
            stream.println("ERROR " + msg);
        }
    }
    
    public static void traceAppend(String msg) {
        buf.append(msg);
    }
    
    public static void traceFlush() {
        if (logTRACE) {
            stream.println("TRACE "+buf.toString());
            buf = new StringBuffer();
        }
    }
    
    
    
}
