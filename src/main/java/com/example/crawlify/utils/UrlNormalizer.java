package com.example.crawlify.utils;

import java.net.URI;
import java.net.URL;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

public class UrlNormalizer {
    public static String normalize(String urlString) throws MalformedURLException, URISyntaxException {
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