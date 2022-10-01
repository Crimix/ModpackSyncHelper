package com.black_dog20.modpacksynchelper.utils;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import javax.swing.event.HyperlinkEvent;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Class for url based methods.
 */
public class UrlHelper {

    /**
     * Gets the domain name from the url
     * @param url the url
     * @return the domain name
     */
    public static String getDomainName(String url) {
        try {
            URI uri = new URI(url);
            String domain = uri.getHost();
            domain = domain.toLowerCase().startsWith("www.") ? domain.substring(4) : domain;
            return domain;
        } catch (URISyntaxException e) {
            return "UNKNOWN";
        }
    }

    /**
     * Gets the mod file name from the url
     * @param url the url
     * @return the mod file name
     */
    public static String getModNameFromUrl(URL url) throws URISyntaxException {
        String remoteUrl = url.toURI().getPath();
        return remoteUrl.substring(remoteUrl.lastIndexOf("/") + 1);
    }

    /**
     * Opens the link from a hyperlink event
     * @param event the hyperlink event
     */
    public static void openLink(HyperlinkEvent event) throws URISyntaxException, IOException {
        if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
            openLink(event.getURL());
        }
    }

    /**
     * Opens the url in the users default browser if possible
     * @param url the url to open
     */
    public static void openLink(URL url) throws URISyntaxException, IOException {
        if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().browse(url.toURI());
        }
    }

    /**
     * Fetches the body from the url
     * @param url the url
     * @return the returned body as a string
     */
    public static String fetchFromUrl(String url) throws IOException {
        return setStandardOptions(Jsoup.connect(url))
                .execute().body();
    }

    /**
     * Sets the apps standard options on the Jsoup connection
     * @param connection the Jsoup connection
     * @return a standard configured Jsoup connection
     */
    public static Connection setStandardOptions(Connection connection) {
        return connection.userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.131 Safari/537.36")
                .timeout(30000)
                .followRedirects(true)
                .ignoreContentType(true)
                .maxBodySize(20000000); //Increase value if download is more than 20MB
    }
}
