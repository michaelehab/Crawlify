package com.example.crawlify.utils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class UrlNormalizer {

    // Set of query parameters to keep
    private static final Set<String> KEEP_QUERY_PARAMS = new HashSet<>(Arrays.asList("utm_source", "utm_medium", "utm_campaign"));

    public static String normalize(String url) {
        try {
            // Step 1: Remove URL fragment identifiers
            URI uri = new URI(url);
            uri = new URI(uri.getScheme(), uri.getAuthority(), uri.getPath(), null, uri.getFragment());

            // Step 2: Resolve relative URLs
            uri = uri.normalize();

            // Step 3: Remove unnecessary query parameters
            String query = uri.getQuery();
            if (query != null) {
                StringBuilder sb = new StringBuilder();
                for (String param : query.split("&")) {
                    String name = param.split("=")[0];
                    if (KEEP_QUERY_PARAMS.contains(name)) {
                        sb.append(param).append("&");
                    }
                }
                if (sb.length() > 0) {
                    sb.setLength(sb.length() - 1);
                    uri = new URI(uri.getScheme(), uri.getAuthority(), uri.getPath(), sb.toString(), uri.getFragment());
                } else {
                    uri = new URI(uri.getScheme(), uri.getAuthority(), uri.getPath(), null, uri.getFragment());
                }
            }

            // Step 4: Standardize domain names
            String host = uri.getHost();
            if (host.startsWith("www.")) {
                host = host.substring(4);
            }
            host = host.toLowerCase();
            uri = new URI(uri.getScheme(), host, uri.getPath(), uri.getQuery(), uri.getFragment());

            // Step 5: Encode special characters
            String normalizedUrl = uri.toASCIIString();
            return normalizedUrl;
        } catch (URISyntaxException e) {
            // Return original URL if it cannot be parsed
            return url;
        }
    }
}