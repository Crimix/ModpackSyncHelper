package com.black_dog20.modpacksynchelper.curse;

import java.util.Map;

public class CurseProject {
    private int id;
    private String title;
    private Map<String, String> urls;

    public CurseProject() {
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getPorjectUrl() {
        return urls.values().stream()
                .findFirst()
                .orElse(CurseHelper.CURSEFORGE);
    }
}
