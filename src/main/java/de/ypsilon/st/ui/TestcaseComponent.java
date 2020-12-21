package de.ypsilon.st.ui;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

public class TestcaseComponent extends JPanel {

    /**
     * Initializer for a new Testcase component
     *
     * @param input the expected output
     * @param output the actual output
     * @param success test succeeded
     * @param className the class name
     * @param methodName the method name
     * @param computingTime the computation time
     */
    public TestcaseComponent(String input, String output, boolean success, String className, String methodName, long computingTime) {
        super();

        setLayout(new BorderLayout());

        List<String[]> dataList = new LinkedList<>();
        dataList.add(new String[]{"success", (success ? "yes" : "no")});
        dataList.add(new String[]{"input", input});

        boolean firstLine = true;
        expectedOutput = expectedOutput.substring(0, expectedOutput.length() - TestInitializer.END_OF_LINE_DELIMITER.length()).replaceAll("<br>", "\n");
        for (String line : expectedOutput.split("\n")) {
            dataList.add(new String[]{firstLine ? "output expected" : "", line});
            firstLine = false;
        }

        //dataList.add(new String[]{"output expected", expectedOutput.substring(0, expectedOutput.length() - TestInitializer.END_OF_LINE_DELIMITER.length())});

        firstLine = true;
        for (String line : outputByProgram.split("\n")) {
            dataList.add(new String[]{firstLine ? "output by program" : "", line});
            firstLine = false;
        }

        dataList.add(new String[]{"className", className});
        dataList.add(new String[]{"methodName", methodName});
        dataList.add(new String[]{"computationTime", computingTime/1000000. + " ms"});
        //dataList.add(new String[]{"", ""});


        String[][] data = dataList.toArray(new String[0][0]);
        String[] columns = {"Type", "Value"};
        JTable table = new JTable(data, columns);
        table.setEnabled(false);

        if (!success) table.setGridColor(Color.RED);

        add(table);
        setBorder(new EmptyBorder(10, 10, 10, 10));
    }

}
