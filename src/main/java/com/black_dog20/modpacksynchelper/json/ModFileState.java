package com.black_dog20.modpacksynchelper.json;

import com.black_dog20.modpacksynchelper.json.api.IMod;

import java.util.Objects;

/**
 * Class that corresponds to my custom json format
 * This represents a plain mod file on the local system that should be either activated or deactivated
 */
public class ModFileState implements IMod {

    private String name;
    private boolean active;

    public ModFileState() {

    }

    public String getName() {
        return name;
    }

    public boolean isActive() {
        return active;
    }

    @Override
    public String getHtmlElementString() {
        return String.format("<li>%s disabled: %s</li>\n", getName(), !isActive());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ModFileState that = (ModFileState) o;
        return active == that.active && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, active);
    }
}
