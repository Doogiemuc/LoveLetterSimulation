package org.loveletter;

import java.io.PrintStream;

public class Log {
    private static PrintStream stream = System.out;
    public static boolean logTRACE = true;
    public static boolean logINFO  = true;
    public static boolean logWARN  = true;
    public static boolean logERROR = true;
    
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
    	if (logTRACE)
    		buf.append(msg);
    }
    
    public static void traceFlush() {
        if (logTRACE) {
            stream.println("TRACE "+buf.toString());
            buf = new StringBuffer();
        }
    }
    
    /** pad the string to the given length */
    public static String padLeftaligned(String s, int space) {
        if (s == null) return "";
        if (s.length() == space) return s;
        if (s.length() > space) return s.substring(0, space);
        StringBuffer buf = new StringBuffer(s);
        for (int i = 0; i < space - s.length(); i++) {
            buf.append(" ");
        }
        return buf.toString();
    }
    
}
