package com.black_dog20.modpacksynchelper.utils;

import javax.swing.event.HyperlinkEvent;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;

public class UrlHelper {

    private static Map<String, String> DOMAIN_MAP = Map.of(
            "media.forgecdn.net", "curseforge.com" //Because we need to get the real download link in the JSON
    );


    public static String getDomainName(String url) {
        try {
            URI uri = new URI(url);
            String domain = uri.getHost();
            domain = domain.toLowerCase().startsWith("www.") ? domain.substring(4) : domain;
            return DOMAIN_MAP.getOrDefault(domain, domain);
        } catch (URISyntaxException e) {
            return "UNKNOW";
        }
    }

    public static void openLink(HyperlinkEvent event) throws URISyntaxException, IOException {
        if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
            openLink(event.getURL());
        }
    }

    public static void openLink(URL url) throws URISyntaxException, IOException {
        if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().browse(url.toURI());
        }
    }
}
