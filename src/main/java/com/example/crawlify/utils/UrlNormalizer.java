package com.example.crawlify.utils;

import java.net.URI;
import java.net.URL;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UrlNormalizer {

    private static final Pattern URL_PATTERN = Pattern.compile("^(https?://)?(www\\.)?[a-zA-Z0-9]+(\\.[a-zA-Z0-9]+)+([/?#].*)?$");

    public static String normalize(String urlString) throws MalformedURLException, URISyntaxException {
        // Validate the URL
        Matcher matcher = URL_PATTERN.matcher(urlString);
        if (!matcher.matches()) {
            throw new MalformedURLException("Invalid URL: " + urlString);
        }

        // Convert the string to a URL object
        URL url = new URL(urlString);

        // Get the scheme, host, port, path and query components
        String scheme = url.getProtocol().toLowerCase();
        String host = url.getHost().toLowerCase();
        int port = url.getPort();
        String path = url.getPath();
        String query = url.getQuery();

        // Create a URI object from the components
        URI uri = new URI(scheme, null, host, port, path, query, null);

        // Normalize the URI
        uri = uri.normalize();

        // Return the normalized URI as a string
        return uri.toString();
    }

}