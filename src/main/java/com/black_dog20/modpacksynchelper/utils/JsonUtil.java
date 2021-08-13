package com.black_dog20.modpacksynchelper.utils;

import com.black_dog20.modpacksynchelper.Main;
import com.black_dog20.modpacksynchelper.json.ModsSyncInfo;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class JsonUtil {

    public static ModsSyncInfo getModsSyncInfo() throws IOException {
        String json = AppProperties.isDebug() ? readDebugResource() : readUrl(AppProperties.getUrl());
        Gson gson = new Gson();
        return gson.fromJson(json, ModsSyncInfo.class);
    }


    private static String readUrl(String urlString) throws IOException {
        return baseRead(new URL(urlString).openStream());
    }

    private static String readDebugResource() throws IOException {
        return baseRead(Main.class.getClassLoader().getResourceAsStream("debug.json"));
    }

    private static String baseRead(InputStream stream) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))){
            StringBuilder buffer = new StringBuilder();
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1)
                buffer.append(chars, 0, read);

            return buffer.toString();
        }
    }
}
