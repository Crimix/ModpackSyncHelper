package com.black_dog20.modpacksynchelper.curse.model;

import java.util.List;

/**
 * This class corresponds to the json result of call to <a href="https://docs.curseforge.com/?http#get-files">Get Files CF Core API</a>
 * But only the fields that I am interested in
 */
public class CurseFilesResponse {

    private List<CurseProject.ModFile> data;

    public CurseFilesResponse() {
    }

    public List<CurseProject.ModFile> getData() {
        return data;
    }
}
