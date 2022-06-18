package com.black_dog20.modpacksynchelper.curse;

import com.black_dog20.modpacksynchelper.utils.UrlHelper;
import com.google.gson.Gson;
import org.jsoup.Jsoup;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class CurseHelper {

    public static final String UNKNONW = "UNKNOWN";
    public static final String CURSEFORGE = "https://www.curseforge.com/";
    private static final String API_KEY = "@api_key@";
    private static final Map<Integer, com.black_dog20.modpacksynchelper.curse.CurseProject> PROJECT_CACHE = new HashMap<>();
    private static final Map<Integer, String> FILE_URL_CACHE = new HashMap<>();
    private static final Gson gson = new Gson();


    public static String getCurseProjectUrl(int projectId) {
        try {
            if (PROJECT_CACHE.containsKey(projectId)) {
                return PROJECT_CACHE.get(projectId).getPorjectUrl();
            }

            String json = Jsoup.connect(String.format("https://api.cfwidget.com/%s", projectId))
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.131 Safari/537.36")
                    .timeout(30000)
                    .followRedirects(true)
                    .ignoreContentType(true)
                    .maxBodySize(20000000)//Increase value if download is more than 20MB
                    .execute().body();
            CurseProject project = gson.fromJson(json, CurseProject.class);
            if (project != null) {
                PROJECT_CACHE.put(projectId, project);
                return project.getPorjectUrl();
            } else {
                return CURSEFORGE;
            }
        } catch (Exception e) {
            System.err.println(e.getLocalizedMessage());
            return CURSEFORGE;
        }
    }

    public static String getModName(int projectId, int fileId) {
        try {
            return UrlHelper.getModNameFromUrl(new URL(getCurseDownloadUrl(projectId, fileId)));
        } catch (Exception e) {
            System.err.println(e.getLocalizedMessage());
            return UNKNONW;
        }
    }

    public static String getCurseDownloadUrl(int projectId, int fileId) {
        try {
            if (FILE_URL_CACHE.containsKey(fileId)) {
                return FILE_URL_CACHE.get(fileId);
            }
            String json = Jsoup.connect(String.format("https://api.curseforge.com/v1/mods/%s/files/%s/download-url", projectId, fileId))
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.131 Safari/537.36")
                    .header("X-API-KEY", API_KEY)
                    .timeout(30000)
                    .followRedirects(true)
                    .ignoreContentType(true)
                    .maxBodySize(20000000)//Increase value if download is more than 20MB
                    .execute().body();
            CurseDownloadUrl curseDownloadUrl = gson.fromJson(json, CurseDownloadUrl.class);
            FILE_URL_CACHE.put(fileId, curseDownloadUrl.getData());
            return curseDownloadUrl.getData();
        } catch (Exception e) {
            System.err.println(e.getLocalizedMessage());
            return "";
        }
    }
}
