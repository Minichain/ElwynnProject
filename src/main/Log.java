package main;

import console.Console;

import java.awt.Color;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Log {
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_BLACK = "\u001B[30m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_PURPLE = "\u001B[35m";
    private static final String ANSI_CYAN = "\u001B[36m";
    private static final String ANSI_WHITE = "\u001B[37m";

    private static final String logFilePath = System.getenv("APPDATA") + "\\ElwynnProject";
    private static final String logFileName = "log_file.txt";
    private static File file;
    private static FileWriter fileWriter;
    private static boolean fileCreated = false;

    private static void createLogFile() {
        try {
            File directory = new File(logFilePath);
            if (directory.exists() || directory.mkdir()) {
                file = new File(logFilePath + "\\" + logFileName);
                if (file.createNewFile()) {
                    System.out.println("File created: " + logFileName);
                } else {
                    System.out.println("File " + logFileName + " already exists.");
                }
                fileCreated = true;
            }
        } catch (IOException e) {
            System.out.println("Error creating " + logFileName);
            e.printStackTrace();
        }
    }

    private static void appendToLogFile(String log) {
        try {
            fileWriter.append(log).append("\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void openLogFile() {
        try {
            fileWriter = new FileWriter(logFilePath + "\\" + logFileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void closeLogFile() {
        try {
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void l(String log, String ansiColor) {
        Color color = getAnsiColorColor(ansiColor);
        Console.getInstance().addNewLine(log, color);
        if (!fileCreated) {
            createLogFile();
            openLogFile();
        }
        appendToLogFile(log);
        System.out.println(ansiColor + log + ANSI_RESET);
    }

    public static void l(String log) {
        l(getTimeStamp() + log, ANSI_WHITE);
    }

    public static void e(String log) {
        l(getTimeStamp() + log, ANSI_RED);
    }

    private static String getTimeStamp() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        return "[" + dtf.format(now) + "]: ";
    }

    private static Color getAnsiColorColor(String ansiColor) {
        Color color;
        switch (ansiColor) {
            default:
            case ANSI_WHITE:
                color = new Color(1f, 1f, 1f);
                break;
            case ANSI_RED:
                color = new Color(1f, 0f, 0f);
                break;
        }
        return color;
    }
}
