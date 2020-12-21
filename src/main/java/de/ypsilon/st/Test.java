package de.ypsilon.st;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Test {
    private String className;
    private String methodName;
    private Object[] args;
    private String expectedOutput;
    private long computationTime = Long.MAX_VALUE;

    /**
     * Constructor to construct a new test case
     *
     * @param line line inside the tests file
     */
    public Test(String line) {
        // Split input from output
        String[] inOutPut = line.split("->");

        if (inOutPut.length < 2) {
            System.err.println("Testfile line missing in-out-put-form: className methodName args... -> output");
            return;
        }

        String[] qualifiers = inOutPut[0].split(" ");
        this.expectedOutput = inOutPut[1].trim();

        if (qualifiers.length < 2) {
            System.err.println("Testfile line does not match requirements className methodName args...");
            return;
        }

        this.className = qualifiers[0];
        this.methodName = qualifiers[1];
        args = new String[qualifiers.length - 2];

        for (int i = 0; i < args.length; i++) {
            args[i] = qualifiers[i + 2];
        }
    }

    /**
     * Run the testcase
     */
    public void run() {
        try {
            Thread test = new Thread(() -> {
                boolean success = true;
                String[] expectedOutputLines = expectedOutput
                        .substring(0, expectedOutput.length() - TestInitializer.END_OF_LINE_DELIMITER.length())
                        .replaceAll("<br>", System.lineSeparator())
                        .split(System.lineSeparator());

                String line = Schlapptomat.getInstance().getNextLine();
                List<String> runtimeOutput = new ArrayList<>(Arrays.asList(line.split(System.lineSeparator())));

                Schlapptomat.getInstance().getOut().println(line);

                //while (ref.running) {
                //    String nextLine = Schlapptomat.getInstance().getNextLine();
                //    runtimeOutput.add(nextLine);
                //}

                if (runtimeOutput.size() != expectedOutputLines.length) {
                    success = false;
                } else {
                    for (int i = 0; i < runtimeOutput.size(); i++) {
                        String runtimeOutputLine = runtimeOutput.get(i);
                        String expectedOutputLine = expectedOutputLines[i];

                        if (!runtimeOutputLine.trim().equals(expectedOutputLine.trim())) {
                            Schlapptomat.getInstance().out("TEST FAILED", false);
                            Schlapptomat.getInstance().out("Expected: ", false);
                            Schlapptomat.getInstance().out(expectedOutputLine, false);
                            Schlapptomat.getInstance().out("", false);
                            Schlapptomat.getInstance().out("Got: ", false);
                            Schlapptomat.getInstance().out(expectedOutputLine, true);
                            success = false;
                        } else {
                            Schlapptomat.getInstance().out("TEST SUCCEEDED:", false);
                            Schlapptomat.getInstance().out(Arrays.toString(args) + " -> " + expectedOutput, true);
                            TestInitializer.increaseSucceededTests();
                        }
                    }
                }

                // Feed UI
                Schlapptomat.getInstance().getTestUI().addTestCase(
                        Arrays.toString(args), expectedOutput, line, success, className, methodName, computationTime);
            });
            test.start();

            Executor executor = Schlapptomat.getInstance().getExecutor();
            Object instance = methodName.equals("main") ? "main" : null;
            Object[] arguments = (methodName.equals("main") ? new Object[]{args} : args);
            executor.evaluate(this, className, methodName, instance, arguments);
            System.out.print("<|>");
            System.out.flush();
            test.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Set the computation time needed to execute the test case
     *
     * @param time in ns
     */
    public void setComputationTime(long time) {
        computationTime = time;
    }

}
