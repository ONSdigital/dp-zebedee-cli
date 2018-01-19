package com.github.onsdigital.zebedee.data.processing;

import java.text.SimpleDateFormat;
import java.util.Date;

import static java.text.MessageFormat.format;

/**
 * Created by dave on 19/01/2018.
 */
public class MyLogger {

    static SimpleDateFormat DATE_Format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    public static void log(String message, Object... args) {
        System.out.println(format("[" + DATE_Format.format(new Date()) + "] " + message, args));
    }
}
