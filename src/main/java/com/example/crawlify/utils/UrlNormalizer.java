package com.example.crawlify.utils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UrlNormalizer {

    private static final Pattern URL_PATTERN = Pattern.compile(
            "^(https?|ftp)://" // scheme
                    + "([a-z0-9]+(-[a-z0-9]+)*\\.)+[a-z]{2,}" // hostname
                    + "(:\\d+)?" // port
                    + "(/[^?#]*)?" // path
                    + "(\\?[^#]*)?" // query
                    + "(#.*)?$"); // fragment

    public static String normalize(String urlString) throws URISyntaxException {
        // Normalize the URL string
        urlString = urlString.trim().replaceAll("\\s+", " ");

        // Check if the URL is valid
        Matcher matcher = URL_PATTERN.matcher(urlString);
        if (!matcher.matches()) {
            throw new URISyntaxException(urlString, "Invalid URL format");
        }

        // Parse the URL into a URI object
        URI uri = new URI(urlString);

        // If the URI is a relative URI, resolve it against a dummy base URI
        if (!uri.isAbsolute()) {
            uri = new URI("http://dummy.com").resolve(uri);
        }

        // Normalize the URI
        uri = uri.normalize();

        // Convert the URI back to a string

        return uri.toString();
    }
}