package de.ypsilon.st;

import java.io.*;
import java.util.Scanner;

/**
 * Main class from the Schlapptomat Tool for automatic Tests
 *
 * @version 1.0
 * @author yJulian, yNiklas
 */
public class Schlapptomat {

    public static final String DELIMITER = "+-------------+";
    private static Schlapptomat instance;
    private final Executor executor;
    private final Scanner inputScanner;
    private final PrintStream defaultOut;
    private Scanner outputScanner;

    /**
     * Entry method for the tool
     *
     * @param args no command lines used or interpreted
     */
    public static void main(String[] args) throws IOException {
        new Schlapptomat();
    }

    /**
     * Constructor from the Schlapptomat
     * Creates a new instance and loads all files inside the sources folder.
     * After that it executes all the specified tests
     */
    public Schlapptomat() throws IOException {
        instance = this;
        this.defaultOut = System.out;

        out("", false);
        out("Launching Schlapptomat :)", false);
        out("", false);

        executor = new Executor();

        this.inputScanner = new Scanner(System.in);

        redirectSteams();
        loadFiles();

        TestInitializer.loadTests();
        TestInitializer.runAll();
    }

    /**
     * Method to redirect all System.out calls to the {@link #outputScanner}
     *
     * @throws IOException when something goes wrong. MagicValue
     */
    private void redirectSteams() throws IOException {
        PipedInputStream in = new PipedInputStream();
        final PipedOutputStream out = new PipedOutputStream(in);

        outputScanner = new Scanner(in);
        outputScanner.useDelimiter("\n");

        System.setOut(new PrintStream(out));
    }

    /**
     * Load the files inside the sources directory to the current Executor instance
     */
    private void loadFiles() {
        File sourceFolder = new File("sources/");
        if(!sourceFolder.exists()) {
            defaultOut.println("Created sources file " + (sourceFolder.mkdir() ? "successfully": "unsuccessfully"));
            defaultOut.println("Created the sources/ Folder in your current directory.");
            defaultOut.println("Please place all your source files inside of that folder");
            defaultOut.println("And place your tests.txt file in that folder too.");
            defaultOut.println("Exit now");

            System.exit(1);
        }

        loadFolder(sourceFolder);
    }

    /**
     * Load a folder recursive with all subfolders to the Executor instance
     *
     * @param folder the to load
     */
    private void loadFolder(File folder) {
        File[] files = folder.listFiles();
        if(files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    if (file.getName().endsWith(".java")) {
                        executor.loadFile(file);
                    }
                }
                if (file.isDirectory()) {
                    loadFolder(file);
                }
            }
        }
    }

    /**
     * Return the nextLine from the scanner.
     * Attention this method is BLOCKING and not thread safe
     *
     * @return the next read line
     */
    public String getNextLine() {
        return outputScanner.next();
    }

    /**
     * Get the current Executor with all files from the class
     *
     * @return the current executor instance
     */
    public Executor getExecutor() {
        return executor;
    }

    /**
     * Get the default Sytem.out PrintStream
     * Because the default System.out is redirected to a scanner
     *
     * @return the default System.out PrintStream
     */
    public PrintStream getOut() {
        return defaultOut;
    }

    /**
     * Get the Scanner that is registered to System.in
     * With line delimiters set to a line break
     *
     * @return the Scanner
     */
    public Scanner getInputScanner() {
        return inputScanner;
    }

    /**
     * Get the Scanner used to redirect the default System.out
     * Line delimiters are set to line break
     *
     * @return the Scanner
     */
    public Scanner getScanner() {
        return outputScanner;
    }

    /**
     * Return the base instance
     *
     * @return the current instance
     */
    public static Schlapptomat getInstance() {
        return instance;
    }

    /**
     * Method to print text to the original terminal
     *
     * @param message the message
     * @param delimit when true print the {@link #DELIMITER} to the console after the message
     */
    public void out(Object message, boolean delimit) {
        defaultOut.println(message);
        if (delimit) defaultOut.println(DELIMITER);
    }
}
