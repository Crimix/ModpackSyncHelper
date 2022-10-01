package com.black_dog20.modpacksynchelper.curse;

import com.black_dog20.modpacksynchelper.curse.model.CurseFilesResponse;
import com.black_dog20.modpacksynchelper.curse.model.CurseModsResponse;
import com.black_dog20.modpacksynchelper.curse.model.CurseProject;
import com.black_dog20.modpacksynchelper.json.CurseDownload;
import com.black_dog20.modpacksynchelper.utils.ProgressBarUtil;
import com.black_dog20.modpacksynchelper.utils.UrlHelper;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Helper class to interact with curseforge
 */
public class CurseHelper {

    public static final String UNKNONW = "UNKNOWN";
    public static final String CURSEFORGE_URL = "https://www.curseforge.com/";
    public static final String CURSEFORGE_DOMAIN = "curseforge.com";
    private static final String API_KEY = "@api_key@";
    private static final String API_URL = "https://api.curseforge.com/v1";
    private static final Map<Integer, CurseProject> PROJECT_CACHE = new HashMap<>(); //Cache to limit the times I need to ask the API
    private static final Gson gson = new Gson();

    /**
     * Fetch the main metadata for the curse mods which should be downloaded
     * @param curseDownloadList the list of mods to download
     */
    public static void fetchMetadata(List<CurseDownload> curseDownloadList) {
        if (curseDownloadList.isEmpty()) {
            return;
        }
        System.out.println("Fetching all Curse metadata");
        List<List<Integer>> modIds = curseDownloadList.stream()
                .map(CurseDownload::getProjectId)
                .distinct()
                .collect(Collectors.collectingAndThen(Collectors.toList(), CurseHelper::partition));

        Map<Integer, CurseProject> curseProjects = ProgressBarUtil.wrapWithProgressBar(modIds.stream(), "Main data")
                .map(CurseHelper::fetchMetadataOperation)
                .flatMap(Collection::stream)
                .collect(Collectors.toMap(CurseProject::getId, Function.identity()));

        PROJECT_CACHE.putAll(curseProjects);

        List<List<Integer>> fileIds = curseDownloadList.stream()
                .map(CurseDownload::getFileId)
                .distinct()
                .collect(Collectors.collectingAndThen(Collectors.toList(), CurseHelper::partition));

        ProgressBarUtil.wrapWithProgressBar(fileIds.stream(), "File data")
                .map(CurseHelper::fetchFileOperation)
                .flatMap(Collection::stream)
                .forEach(CurseHelper::addModFileToProjectCache);
        System.out.println("Done fetching all Curse metadata");
    }

    private static String buildApiUrl(String apiPart) {
        return String.format("%s/%s", API_URL, apiPart);
    }

    private static <T> List<List<T>> partition(List<T> list) {
        return Lists.partition(list, 50);
    }

    private static List<CurseProject> fetchMetadataOperation(List<Integer> modIds) {
        try {
            String json = UrlHelper.setStandardOptions(Jsoup.connect(buildApiUrl("mods")))
                    .method(Connection.Method.POST)
                    .requestBody(gson.toJson(Map.of("modIds", modIds)))
                    .header("X-API-KEY", API_KEY)
                    .header("Content-Type", "application/json;charset=UTF-8")
                    .execute().body();
            CurseModsResponse curseModsResponse = gson.fromJson(json, CurseModsResponse.class);
            return curseModsResponse.getData();
        } catch (Exception e) {
            System.err.println(String.format("Failed to fetch curseforge main data because %s", e.getLocalizedMessage()));
            return Collections.emptyList();
        }
    }

    private static List<CurseProject.ModFile> fetchFileOperation(List<Integer> fileIds) {
        try {
            String json = UrlHelper.setStandardOptions(Jsoup.connect(buildApiUrl("mods/files")))
                    .method(Connection.Method.POST)
                    .requestBody(gson.toJson(Map.of("fileIds", fileIds)))
                    .header("X-API-KEY", API_KEY)
                    .header("Content-Type", "application/json;charset=UTF-8")
                    .execute().body();
            CurseFilesResponse curseFilesResponse = gson.fromJson(json, CurseFilesResponse.class);
            return curseFilesResponse.getData();
        } catch (Exception e) {
            System.err.println(String.format("Failed to fetch curseforge file data because %s", e.getLocalizedMessage()));
            return Collections.emptyList();
        }
    }

    private static void addModFileToProjectCache(CurseProject.ModFile modFile) {
        PROJECT_CACHE.get(modFile.getModId()).addModFile(modFile);
    }

    /**
     * Gets the curseforge project page url for the specific mod
     * @param curseDownload the mod to download
     * @return curseforge project page url for the specific mod
     */
    public static String getCurseProjectUrl(CurseDownload curseDownload) {
        try {
            return PROJECT_CACHE.get(curseDownload.getProjectId()).getProjectUrl();
        } catch (Exception e) {
            System.err.println(String.format("Failed to get project url for %s because %s", curseDownload.getProjectId(), e.getLocalizedMessage()));
            return CURSEFORGE_URL;
        }
    }

    /**
     * Gets the name of the mod from the url
     * @param curseDownload the mod to download
     * @return the name of the mod from the url
     */
    public static String getModName(CurseDownload curseDownload) {
        try {
            return UrlHelper.getModNameFromUrl(new URL(getCurseDownloadUrl(curseDownload)));
        } catch (Exception e) {
            System.err.println(String.format("Failed to get mod name for %s because %s", curseDownload.getProjectId(), e.getLocalizedMessage()));
            return UNKNONW;
        }
    }

    /**
     * Gets the download url for the specific mod file from curseforge
     * @param curseDownload the mod to download
     * @return the download url for the specific mod file
     */
    public static String getCurseDownloadUrl(CurseDownload curseDownload) {
        try {
            return PROJECT_CACHE.get(curseDownload.getProjectId()).getModFile(curseDownload.getFileId()).getDownloadUrl();
        } catch (Exception e) {
            System.err.println(String.format("Failed to get mod download url for %s because %s", curseDownload.getProjectId(), e.getLocalizedMessage()));
            return "";
        }
    }
}
