package main;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Log {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    public static final String logFilePath = System.getenv("APPDATA") + "\\ElwynnProject";
    public static final String logFileName = "log_file.txt";
    public static File file;
    public static FileWriter fileWriter;
    public static boolean fileCreated = false;

    private static void createLogFile() {
        try {
            file = new File(logFilePath + "\\" + logFileName);
            if (file.createNewFile()) {
                System.out.println("File created: " + logFileName);
            } else {
                System.out.println("File " + logFileName + " already exists.");
            }
            fileCreated = true;
        } catch (IOException e) {
            System.out.println("Error creating " + logFileName);
            e.printStackTrace();
        }
    }

    private static void appendToLogFile(String log) {
        try {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            fileWriter.append("[" + dtf.format(now) + "]: ");
            fileWriter.append(log);
            fileWriter.append("\n");
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
        if (!fileCreated) {
            createLogFile();
            openLogFile();
        }
        appendToLogFile(log);
        System.out.println(ansiColor + log + ANSI_RESET);
    }

    public static void l(String log) {
        l(log, ANSI_WHITE);
    }

    public static void e(String log) {
        l(log, ANSI_RED);
    }
}
