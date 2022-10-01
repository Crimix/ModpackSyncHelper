package com.black_dog20.modpacksynchelper.json;

import com.black_dog20.modpacksynchelper.json.api.IMod;

import java.util.Objects;

/**
 * Class that corresponds to my custom json format
 * This represents a plain mod file on the local system, this is used for deletion of unwanted mods
 */
public class ModFile implements IMod {

    private String name;

    public ModFile() {

    }

    public String getName() {
        return name;
    }

    @Override
    public String getHtmlElementString() {
        return String.format("<li>%s</li>\n", getName());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ModFile modFile = (ModFile) o;
        return Objects.equals(name, modFile.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
