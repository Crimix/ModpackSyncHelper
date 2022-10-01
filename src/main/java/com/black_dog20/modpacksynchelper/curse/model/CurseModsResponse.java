package com.black_dog20.modpacksynchelper.curse.model;

import java.util.List;

/**
 * This class corresponds to the json result of call to <a href="https://docs.curseforge.com/?http#get-mods">Get Mods CF Core API</a>
 * But only the fields that I am interested in
 */
public class CurseModsResponse {

    private List<CurseProject> data;

    public CurseModsResponse() {
    }

    public List<CurseProject> getData() {
        return data;
    }
}
