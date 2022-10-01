package com.black_dog20.modpacksynchelper.modrinth;

import com.black_dog20.modpacksynchelper.json.ModrinthDownload;
import com.black_dog20.modpacksynchelper.modrinth.model.ModrinthProject;
import com.black_dog20.modpacksynchelper.utils.ProgressBarUtil;
import com.black_dog20.modpacksynchelper.utils.UrlHelper;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.jsoup.Jsoup;

import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Helper class to interact with modrinth
 */
public class ModrinthHelper {

    public static final String UNKNONW = "UNKNOWN";
    public static final String MODRINTH_DOMAIN = "modrinth.com";
    private static final String API_URL = "https://api.modrinth.com/v2";
    private static final Map<String, ModrinthProject> PROJECT_CACHE = new HashMap<>(); //Cache to limit the times I need to ask the API
    private static final Gson gson = new Gson();


    /**
     * Fetch the main metadata for the modrinth mods which should be downloaded
     * @param modrinthDownloads the list of mods to download
     */
    public static void fetchMetadata(List<ModrinthDownload> modrinthDownloads) {
        if (modrinthDownloads.isEmpty()) {
            return;
        }
        System.out.println("Fetching all Modrinth metadata");
        List<List<String>> projectIds = modrinthDownloads.stream()
                .map(ModrinthDownload::getProjectId)
                .distinct()
                .collect(Collectors.collectingAndThen(Collectors.toList(), ModrinthHelper::partition));

        Map<String, ModrinthProject> modrinthProjects = ProgressBarUtil.wrapWithProgressBar(projectIds.stream(), "Main data")
                .map(ModrinthHelper::fetchMetadataOperation)
                .flatMap(Collection::stream)
                .collect(Collectors.toMap(ModrinthProject::getId, Function.identity()));

        PROJECT_CACHE.putAll(modrinthProjects);

        List<List<String>> fileIds = modrinthDownloads.stream()
                .map(ModrinthDownload::getVersionId)
                .distinct()
                .collect(Collectors.collectingAndThen(Collectors.toList(), ModrinthHelper::partition));

        ProgressBarUtil.wrapWithProgressBar(fileIds.stream(), "File data")
                .map(ModrinthHelper::fetchFileOperation)
                .flatMap(Collection::stream)
                .forEach(ModrinthHelper::addVersionToProjectCache);
        System.out.println("Done fetching all Modrinth metadata");
    }

    private static <T> List<List<T>> partition(List<T> list) {
        return Lists.partition(list, Integer.MAX_VALUE); //Modrinth does not seem to have a page system
    }

    private static String buildApiUrl(String apiPart) {
        return String.format("%s/%s", API_URL, apiPart);
    }

    private static String buildIdPart(List<String> ids) {
        String idParam = ids.stream()
                .map(id -> String.format("\"%s\"", id))
                .map(id -> URLEncoder.encode(id, StandardCharsets.UTF_8))
                .collect(Collectors.joining(",", "[", "]"));
        return String.format("ids=%s", idParam);
    }

    private static List<ModrinthProject> fetchMetadataOperation(List<String> projectIds) {
        try {
            String json = UrlHelper.setStandardOptions(Jsoup.connect(buildApiUrl(String.format("projects?%s", buildIdPart(projectIds)))))
                    .userAgent("Crimix/ModpackSyncHelper (https://github.com/Crimix/ModpackSyncHelper)") //Modrinth requires a specific user agent
                    .header("Content-Type", "application/json;charset=UTF-8")
                    .execute().body();
            Type listType = new TypeToken<ArrayList<ModrinthProject>>(){}.getType();
            return gson.fromJson(json, listType);
        } catch (Exception e) {
            System.err.println(String.format("Failed to fetch modrinth main data because %s", e.getLocalizedMessage()));
            return Collections.emptyList();
        }
    }

    private static List<ModrinthProject.Version> fetchFileOperation(List<String> fileIds) {
        try {
            String json = UrlHelper.setStandardOptions(Jsoup.connect(buildApiUrl(String.format("versions?%s", buildIdPart(fileIds)))))
                    .userAgent("Crimix/ModpackSyncHelper (https://github.com/Crimix/ModpackSyncHelper)") //Modrinth requires a specific user agent
                    .header("Content-Type", "application/json;charset=UTF-8")
                    .execute().body();
            Type listType = new TypeToken<ArrayList<ModrinthProject.Version>>(){}.getType();
            return gson.fromJson(json, listType);
        } catch (Exception e) {
            System.err.println(String.format("Failed to fetch modrinth file data because %s", e.getLocalizedMessage()));
            return Collections.emptyList();
        }
    }

    private static void addVersionToProjectCache(ModrinthProject.Version version) {
        PROJECT_CACHE.get(version.getProjectId()).addVersion(version);
    }

    /**
     * Gets the modrinth project page url for the specific mod
     * @param modrinthDownload the mod to download
     * @return modrinth project page url for the specific mod
     */
    public static String getProjectUrl(ModrinthDownload modrinthDownload) {
        return String.format("https://modrinth.com/mod/%s", modrinthDownload.getProjectId());
    }

    /**
     * Gets the name of the mod from the url
     * @param modrinthDownload the mod to download
     * @return the name of the mod from the url
     */
    public static String getModName(ModrinthDownload modrinthDownload) {
        try {
            return PROJECT_CACHE.get(modrinthDownload.getProjectId()).getPrimaryModFile(modrinthDownload.getVersionId()).getFilename();
        } catch (Exception e) {
            System.err.println(String.format("Failed to get mod name for %s because %s", modrinthDownload.getProjectId(), e.getLocalizedMessage()));
            return UNKNONW;
        }
    }

    /**
     * Gets the download url for the specific mod file from modrinth
     * @param modrinthDownload the mod to download
     * @return the download url for the specific mod file
     */
    public static String getDownloadUrl(ModrinthDownload modrinthDownload) {
        try {
            return PROJECT_CACHE.get(modrinthDownload.getProjectId()).getPrimaryModFile(modrinthDownload.getVersionId()).getUrl();
        } catch (Exception e) {
            System.err.println(String.format("Failed to get download url for %s because %s", modrinthDownload.getProjectId(), e.getLocalizedMessage()));
            return "";
        }
    }
}
