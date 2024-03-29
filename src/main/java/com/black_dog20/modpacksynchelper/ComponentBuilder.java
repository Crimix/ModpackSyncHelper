package com.black_dog20.modpacksynchelper;

import com.black_dog20.modpacksynchelper.components.MessageConsole;
import com.black_dog20.modpacksynchelper.utils.AppProperties;
import com.black_dog20.modpacksynchelper.utils.DialogUtils;
import com.black_dog20.modpacksynchelper.utils.UrlHelper;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.text.JTextComponent;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.io.IOException;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Class to build ui components
 */
public class ComponentBuilder {

    private final URL sourceCodeURL = new URL("https://github.com/Crimix/ModpackSyncHelper"); //Is not static because of MalformedURLException
    public static PrintStream PROGRESS_BAR_STREAM; //The link between the ui progressbar and the progressbar util

    public ComponentBuilder() throws MalformedURLException {

    }

    /**
     * Builds the menu bar ui
     * @return the menu bar ui
     */
    public JMenuBar getMenuBar() {
        JMenuBar menubar = new JMenuBar();
        JMenu menu = new JMenu("Help");
        JMenuItem viewSource = new JMenuItem("View Source");
        viewSource.addActionListener(e -> {
            try {
                UrlHelper.openLink(sourceCodeURL);
            } catch (Exception exception) {
                System.err.println(exception.getLocalizedMessage());
                DialogUtils.showErrorDialog("Something went wrong with opening the link");
            }
        });
        JMenuItem viewJson = new JMenuItem("View JSON");
        viewJson.addActionListener(e -> {
            try {
                UrlHelper.openLink(new URL(AppProperties.getUrl()));
            } catch (Exception exception) {
                System.err.println(exception.getLocalizedMessage());
                DialogUtils.showErrorDialog("Something went wrong with opening the link");
            }
        });
        menu.add(viewSource);
        menu.add(viewJson);
        menubar.add(menu);
        return menubar;
    }

    /**
     * Builds the sync button
     * @return the sync button
     */
    public JButton getSyncButton() {
        JButton button = new JButton("Sync mods in folder");
        button.setHorizontalAlignment(JButton.CENTER);
        button.setEnabled(false);
        button.addActionListener(e -> {
            button.setEnabled(false);
            Runnable r = () -> {
                try {
                    new ModHandler().handle();
                    button.setEnabled(true);
                } catch (IOException ioException) {
                    System.err.println(ioException.getLocalizedMessage());
                    DialogUtils.showErrorDialog("Something went wrong while trying to handle sync");
                }
            };
            new Thread(r).start();


        });
        return button;
    }

    /**
     * Builds the warning label
     * @return the warning label
     */
    public JLabel getWarningLabel() {
        JLabel label = new JLabel("<html>Only use this tool, if you trust the person who gave it to you.<br>It can download files directly from the internet specified by this person.</html>");
        label.setHorizontalAlignment(JButton.CENTER);
        label.setMinimumSize(new Dimension(500, 50));
        label.setPreferredSize(new Dimension(500, 50));
        label.setMaximumSize(new Dimension(500, 50));
        return label;
    }

    /**
     * Builds a {@link GridBagConstraints} component used to specify the location of a component
     * @param x the x index of the component
     * @param y the y index of the component
     * @return a {@link GridBagConstraints} component used to specify the location of a component
     */
    public GridBagConstraints getConstraints(int x, int y) {
        return getConstraints(x, y, GridBagConstraints.NONE);
    }

    /**
     * Builds a {@link GridBagConstraints} component used to specify the location of a component
     * @param x the x index of the component
     * @param y the y index of the component
     * @param fillMode the fill mode for the constraint
     * @return a {@link GridBagConstraints} component used to specify the location of a component
     */
    public GridBagConstraints getConstraints(int x, int y, int fillMode) {
        GridBagConstraints c = new GridBagConstraints();
        c.fill = fillMode;
        c.weightx = 1;
        c.weighty = 0;
        c.gridx = x;
        c.gridy = y;
        return c;
    }

    /**
     * Builds the html view used to display the mods to handle
     * @param syncButton the sync button
     * @return the html view
     */
    public JScrollPane getHtmlView(JButton syncButton) {
        JEditorPane editor = new JEditorPane();
        HTMLEditorKit kit = new HTMLEditorKit();
        StyleSheet styleSheet = kit.getStyleSheet();
        styleSheet.addRule("ul { margin-left: 8px; padding: 0px;}");
        editor.setEditorKit(kit);
        editor.setDocument(kit.createDefaultDocument());
        editor.setEditable(false);
        editor.setText(HtmlBuilder.getEmptyHtml());
        editor.addHyperlinkListener(e -> {
            try {
                UrlHelper.openLink(e);
            } catch (Exception exception) {
                System.err.println(exception.getLocalizedMessage());
                DialogUtils.showErrorDialog("Something went wrong with opening the link");
            }
        });
        JScrollPane scrollPane = new JScrollPane(
                editor,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setMinimumSize(new Dimension(500, 200));
        scrollPane.setPreferredSize(new Dimension(500, 300));
        editor.setCaretPosition(0);

        Runnable r = () -> {
            try {
                editor.setText(HtmlBuilder.getHtml());
                syncButton.setEnabled(true);
            } catch (IOException e) {
                System.err.println("Failed in reading online json");
                System.err.println(e.getLocalizedMessage());
            }
        };

        new Thread(r).start(); //We should not block the ui thread

        return scrollPane;
    }

    /**
     * Builds the console used to display errors and output to the user
     * This replaces the need for a logfile
     * @return the console
     */
    public JScrollPane getMessageConsole() {
        JTextComponent textComponent = new JTextPane();
        JScrollPane scrollPane = new JScrollPane(
                textComponent,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setPreferredSize(new Dimension(500, 100));

        MessageConsole console = new MessageConsole(textComponent);
        console.redirectOut();
        console.redirectErr(Color.RED, null);

        return scrollPane;
    }

    /**
     * Builds the progress bar
     * @return the progress bar
     */
    public JTextComponent getProgressBar() {
        JTextComponent textComponent = new JTextPane();
        textComponent.setBackground(UIManager.getColor ( "Panel.background" ));
        MessageConsole console = new MessageConsole(textComponent);
        console.setMessageLines(1);
        PROGRESS_BAR_STREAM = console.redirectStream();
        return textComponent;
    }
}
