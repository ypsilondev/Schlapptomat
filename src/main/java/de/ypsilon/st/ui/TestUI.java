package de.ypsilon.st.ui;

import javax.swing.*;
import java.awt.*;

public class TestUI {

    private final JFrame frame;
    private final JPanel panel;

    private int currentTests = 0;
    private int currentSucceeded = 0;

    // Info-Panel labels
    JLabel testRanLabel = new JLabel("Tests ran: 0");
    JLabel succeededLabel = new JLabel("Success-rate: 0");

    /**
     * Create a new instance from the TestUI
     */
    public TestUI() {
        frame = new JFrame();
        frame.setTitle("Schlapptomat <:");

        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JScrollPane pane = new JScrollPane(panel);
        frame.setContentPane(pane);

        // Info-Panel
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new GridLayout(3,1));
        panel.add(infoPanel);

        infoPanel.add(new JLabel("Schlapptomat <:"));
        infoPanel.add(testRanLabel);
        infoPanel.add(succeededLabel);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 480);
        frame.setVisible(true);
    }

    /**
     * Add a new Testcase to the ui
     *
     * @param input the expected output
     * @param output the actual output
     * @param success success or failed
     * @param className the class name
     * @param methodName the method name
     * @param computingTime the computation time
     */
    public void addTestCase(String input, String output, boolean success, String className,
                            String methodName, long computingTime) {
        // Update Info-Panel
        currentTests++;
        if (success) currentSucceeded++;

        testRanLabel.setText("Test ran: " + currentTests);
        succeededLabel.setText("Success-rate: " + currentSucceeded + "/" + currentSucceeded +
                " (" + ((double) 100*currentSucceeded / currentTests) + "%)");

        // Update detail panel
        panel.add(new TestcaseComponent(input, output, success, className,
                methodName, computingTime));

        frame.revalidate();
    }

}