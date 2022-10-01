package com.black_dog20.modpacksynchelper.utils;

import javax.swing.JOptionPane;

/**
 * Dialog utils for use in user flow
 */
public class DialogUtils {

    /**
     * Shows an error dialog with a message
     * @param message the message
     */
    public static void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Shows an error dialog with a message and then closes the app
     * @param message the message
     */
    public static void showErrorDialogAndClose(String message) {
        showErrorDialog(message);
        System.exit(1);
    }

    /**
     * Shows a choice dialog with a message
     * @param message the message
     * @return true if the user pressed YES, otherwise false
     */
    public static boolean showChoiceDialog(String message) {
        return JOptionPane.showConfirmDialog(null, message, "Error", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }
}
