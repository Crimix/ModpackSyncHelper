package com.black_dog20.modpacksynchelper.utils;

import com.black_dog20.modpacksynchelper.Main;

import java.io.IOException;
import java.util.Properties;

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


    public static boolean isDebug() {
        return Boolean.parseBoolean(System.getProperty("isDev", "false"));
    }

    public static String getModpackName() {
        if (isDebug())
            return "DEBUG";
        return properties.getProperty("modpackName");
    }

    public static String getUrl() {
        return properties.getProperty("url");
    }

    public static String getVersion() {
        return properties.getProperty("version");
    }
}
