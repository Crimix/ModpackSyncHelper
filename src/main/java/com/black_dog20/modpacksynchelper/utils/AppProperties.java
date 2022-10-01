package com.black_dog20.modpacksynchelper.utils;

import com.black_dog20.modpacksynchelper.Main;

import java.io.IOException;
import java.util.Properties;

/**
 * Class to interact with app properties
 */
public class AppProperties {

    private static final Properties properties;

    static {
        properties = new Properties();
        try {
            properties.load(Main.class.getClassLoader().getResourceAsStream("app.properties"));
        } catch (IOException e) {
            System.err.println(e.getLocalizedMessage());
            DialogUtils.showErrorDialogAndClose("Something went wrong wile reading properties from jar!");
        }
    }

    /**
     * Gets if the app is run in debug mode
     * @return true if run in debug mode
     */
    public static boolean isDebug() {
        return Boolean.parseBoolean(System.getProperty("isDev", "false"));
    }

    /**
     * Gets the modpack name
     * @return the modpack name
     */
    public static String getModpackName() {
        if (isDebug())
            return "DEBUG";
        return properties.getProperty("modpackName");
    }

    /**
     * Gets the json file url
     * @return the json file url
     */
    public static String getUrl() {
        return properties.getProperty("url");
    }

    /**
     * Gets the version of the app
     * @return the app version
     */
    public static String getVersion() {
        return properties.getProperty("version");
    }
}
