package com.black_dog20.modpacksynchelper.utils;

import com.black_dog20.modpacksynchelper.Main;
import com.black_dog20.modpacksynchelper.json.ModsSyncInfo;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Util to read and convert the main self-hosted json file
 */
public class JsonUtil {

    //Variable to hold the fetched object such that we only fetch it once.
    private static ModsSyncInfo CACHED_MODS_SYNC_INFO;

    /**
     * Gets the {@link ModsSyncInfo} from the json fetched from the url
     * @return the parsed json as a {@link ModsSyncInfo} object
     */
    public static ModsSyncInfo getModsSyncInfo() throws IOException {
        if (CACHED_MODS_SYNC_INFO == null) {
            String json = AppProperties.isDebug() ? readDebugResource() : UrlHelper.fetchFromUrl(AppProperties.getUrl());
            Gson gson = new Gson();
            CACHED_MODS_SYNC_INFO = gson.fromJson(json, ModsSyncInfo.class);
        }

        return CACHED_MODS_SYNC_INFO;
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
