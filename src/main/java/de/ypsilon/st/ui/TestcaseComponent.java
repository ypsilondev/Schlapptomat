package de.ypsilon.st.ui;

import javax.swing.*;
import java.awt.*;

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

        String[][] data = {
                {"success", (success ? "yes" : "no")},
                {"input", input},
                {"output", output},
                {"className", className},
                {"methodName", methodName},
                {"computationTime", computingTime/1000000. + " ms"},
                {"", ""}
        };
        String[] columns = {"Type", "Value"};
        JTable table = new JTable(data, columns);
        table.setEnabled(false);

        if (!success) table.setGridColor(Color.RED);

        add(table);
    }

}
