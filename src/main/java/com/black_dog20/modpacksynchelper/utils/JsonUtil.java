package com.black_dog20.modpacksynchelper.utils;

import com.black_dog20.modpacksynchelper.Main;
import com.black_dog20.modpacksynchelper.json.ModsSyncInfo;
import com.google.gson.Gson;
import org.jsoup.Jsoup;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class JsonUtil {

    public static ModsSyncInfo getModsSyncInfo() throws IOException {
        String json = AppProperties.isDebug() ? readDebugResource() : readUrl(AppProperties.getUrl());
        Gson gson = new Gson();
        return gson.fromJson(json, ModsSyncInfo.class);
    }


    public static String readUrl(String urlString) throws IOException {
        return Jsoup.connect(urlString)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.131 Safari/537.36")
                .timeout(30000)
                .followRedirects(true)
                .ignoreContentType(true)
                .maxBodySize(20000000)//Increase value if download is more than 20MB
                .execute().body();
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
