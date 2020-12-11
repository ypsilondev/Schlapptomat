package de.ypsilon.st;

import java.util.Arrays;

public class Test {
    private String className;
    private String methodName;
    private Object[] args;
    private String expectedOutput;

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

    public void run() {
        try {
            var ref = new Object() {
                boolean running = true;
            };

            Thread test = new Thread(() -> {
                while (ref.running) {
                    String nextLine = Schlapptomat.getInstance().getNextLine();
                    if (!nextLine.trim().equals(expectedOutput.trim())) {
                        Schlapptomat.getInstance().out("TEST FAILED", false);
                        Schlapptomat.getInstance().out("Expected: ", false);
                        Schlapptomat.getInstance().out(expectedOutput, false);
                        Schlapptomat.getInstance().out("", false);
                        Schlapptomat.getInstance().out("Got: ", false);
                        Schlapptomat.getInstance().out(nextLine, true);
                    } else {
                        Schlapptomat.getInstance().out("TEST SUCCEEDED:", false);
                        Schlapptomat.getInstance().out(Arrays.toString(args) + " -> " + expectedOutput, true);
                        TestInitializer.increaseSucceededTests();
                    }
                }
            });
            test.start();

            Executor executor = Schlapptomat.getInstance().getExecutor();
            Object instance = methodName.equals("main") ? "main" : null;
            Object[] arguments = (methodName.equals("main") ? new Object[]{args} : args);
            executor.evaluate(className, methodName, instance, arguments);
            ref.running = false;
            test.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
