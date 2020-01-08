package com.zylex.livebetbot.controller.logger;

import com.zylex.livebetbot.exception.ConsoleLoggerException;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Base class for loggers.
 */
public abstract class ConsoleLogger {

    private volatile static String logOutput;

    private static AtomicLong programStartTime = new AtomicLong(System.currentTimeMillis());

    static {
        DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("hh:mm a dd.MM.yyyy");
        logOutput = "\n" + StringUtils.repeat("*", 50) + "\n"
                + String.format("Bot started at: %s", LocalDateTime.now().format(DATE_TIME_FORMATTER))
                + "\n" + StringUtils.repeat("-", 50);
        System.out.print(logOutput.substring(1));
    }

    /**
     * Write log to file.
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private synchronized static void writeToLogFile() {
        try {
            File logFile = new File("log.txt");
            logFile.createNewFile();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true))) {
                writer.write(logOutput);
            }
            logOutput = "";
        } catch (IOException e) {
            throw new ConsoleLoggerException(e.getMessage(), e);
        }
    }

    public synchronized static void endMessage(LogType type) {
        if (type == LogType.BOT_END) {
            String output = "\nBot work completed in " + computeTime(programStartTime.get())
            + "\n" + StringUtils.repeat("*", 50);
            writeInLine(output);
            writeToLogFile();
        } else if (type == LogType.BLOCK_END) {
            String output = "\n" + StringUtils.repeat("~", 50);
            writeInLine(output);
            writeToLogFile();
        }
    }

    private synchronized static void addToLog(String line) {
        if (line.contains("\b")) {
            int backspaces = StringUtils.countMatches(line, "\b");
            int lastNewLineIndex = logOutput.lastIndexOf("\n");
            logOutput = logOutput.substring(0, Math.max(logOutput.length() - backspaces, lastNewLineIndex + 1))
                    + line.replace("\b", "");
        } else {
            logOutput += line;
        }
    }

    /**
     * Write exception in log.
     * @param message - stack of exception.
     */
    public synchronized static void writeExceptionToLog(String message) {
        logOutput += "\n" + message;
    }

    synchronized void writeLineSeparator() {
        String line = "\n" + StringUtils.repeat("-", 50);
        writeInLine(line);
    }

    static synchronized void writeErrorMessage(String line) {
        System.err.print(line);
        addToLog(line);
    }

    static synchronized void writeInLine(String line) {
        System.out.print(line);
        addToLog(line);
    }

    static String computeTime(long startTime) {
        long millis = System.currentTimeMillis() - startTime;
        String time = String.format("%02d min. %02d sec.",
                TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        if (hours > 0) {
            return String.format("%02d h. ", hours) + time;
        } else {
            return time;
        }
    }
}
