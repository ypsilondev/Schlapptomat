package de.ypsilon.st;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TestInitializer {
    private static List<Test> tests = new ArrayList<>();
    private static final String FILE_LOCATION = "sources/tests.txt";
    private static int succeededTests = 0;
    public static final String END_OF_LINE_DELIMITER = "<|>";

    /**
     * Loads all test in a specific test file
     *
     * File conventions:
     * Test lines: className methodName args... -> expectedOutput
     *
     * Lines to be ignored must start with #
     *
     * @throws FileNotFoundException if file does not exists
     */
    public static void loadTests() throws FileNotFoundException {
        File testsFile = new File(FILE_LOCATION);
        if(!testsFile.exists()) {
            Schlapptomat.getInstance().getOut().println("No tests.txt file found in the sources folder.");
            System.exit(5);
        }

        Scanner scanner = new Scanner(new FileReader(testsFile));
        scanner.useDelimiter("\n");
        scanner.forEachRemaining(testLine -> {
            if (!testLine.startsWith("#") && !testLine.equals("")) {
                Test test = new Test(testLine + END_OF_LINE_DELIMITER);
                tests.add(test);
            }
        });
    }

    /**
     * Method to run all tests that are in the test file
     */
    public static void runAll() {
        Schlapptomat.getInstance().out(Schlapptomat.DELIMITER, false);

        // Run every test
        tests.forEach(Test::run);

        double successRate = (double) 100*succeededTests / tests.size();

        // Feed console
        Schlapptomat.getInstance().out("All test completed", false);
        Schlapptomat.getInstance().out(
                "Completed " + succeededTests + "/" + tests.size() + " (" + successRate + "%)", true);

        succeededTests = 0;
    }

    /**
     * Method to increase the total succeeded test count
     */
    public static void increaseSucceededTests() {
        succeededTests++;
    }
}
