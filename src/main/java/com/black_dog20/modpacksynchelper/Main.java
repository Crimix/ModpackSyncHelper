package com.black_dog20.modpacksynchelper;

import com.black_dog20.modpacksynchelper.utils.AppProperties;
import com.black_dog20.modpacksynchelper.utils.DialogUtils;

import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

public class Main {

    public static void main(String[] args) {
        try {
            Main main = new Main();
            main.display();
        } catch (Exception exception) {
            System.err.println(exception.getLocalizedMessage());
            DialogUtils.showErrorDialogAndClose("The app encountered a unexpected error");
        }
    }

    private void display() throws Exception {
        JFrame frame = new JFrame(String.format("ModpackSyncHelper (%s) - %s", AppProperties.getModpackName(), AppProperties.getVersion()));
        ComponentBuilder componentBuilder = new ComponentBuilder();
        frame.setJMenuBar(componentBuilder.getMenuBar());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.add(componentBuilder.getMessageConsole(), componentBuilder.getConstraints(1, 7, GridBagConstraints.HORIZONTAL)); //Because we want to catch errors ASAP
        panel.add(Box.createHorizontalStrut(10), componentBuilder.getConstraints(0, 0));
        panel.add(Box.createVerticalStrut(10), componentBuilder.getConstraints(1, 0));
        panel.add(componentBuilder.getWarningLabel(), componentBuilder.getConstraints(1, 1));
        panel.add(Box.createVerticalStrut(10), componentBuilder.getConstraints(1, 2));
        panel.add(componentBuilder.getSyncButton(), componentBuilder.getConstraints(1, 3));
        panel.add(Box.createVerticalStrut(10), componentBuilder.getConstraints(1, 4));
        panel.add(componentBuilder.getHtmlView(), componentBuilder.getConstraints(1, 5, GridBagConstraints.HORIZONTAL));
        panel.add(Box.createVerticalStrut(10), componentBuilder.getConstraints(1, 6));
        panel.add(Box.createVerticalStrut(10), componentBuilder.getConstraints(1, 8));
        panel.add(Box.createHorizontalStrut(10), componentBuilder.getConstraints(2, 0));
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        frame.getContentPane().add(panel);
        frame.pack();
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
