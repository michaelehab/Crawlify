package com.example.crawlify.utils;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class RobotsChecker {

    private final HashMap<String, ArrayList<String>> allDisallowedLinks;

    public RobotsChecker() {
        allDisallowedLinks = new HashMap<>();
    }

    private void fetchDisallowedLinks(String urlToCheck) throws IOException {
        URL url = new URL(urlToCheck);
        String protocol = url.getProtocol();
        String serverName = url.getHost();
        url = new URL(protocol + "://" + serverName + "/robots.txt");
        StringBuilder robotCommandsString = new StringBuilder();
        if (allDisallowedLinks.get(url.toString()) != null) {
            return;
        }
        ArrayList<String> disallowedLinks = new ArrayList<>();
        try (DataInputStream theBody = new DataInputStream(url.openStream())) {
            String readLine;
            while ((readLine = theBody.readLine()) != null) {
                robotCommandsString.append(readLine).append("\n");
            }
        } catch (IOException e) {
            throw new IOException("Error happened while trying to read the content of '" + urlToCheck + "': " + e.getMessage());
        }

        String[] robotCommands = robotCommandsString.toString().split("\n");
        boolean userAgentStatus = false;
        for (String command : robotCommands) {
            String line = command.trim();
            if (line.startsWith("User-agent:")) {
                userAgentStatus = line.contains("*");
            } else if (line.startsWith("Disallow:") && userAgentStatus) {
                if (line.length() >= 11) {
                    try {
                        String disallowedDirectories = line.substring(10).trim();
                        String disallowedUrl = protocol + "://" + serverName + disallowedDirectories;
                        disallowedLinks.add(disallowedUrl);
                    } catch (Exception e) {
                        throw new MalformedURLException("Error happened while trying to open '" + urlToCheck + "': " + e.getMessage());
                    }
                }
            }
        }

        allDisallowedLinks.put(url.toString(), disallowedLinks);
    }

    public ArrayList<String> getDisallowedLinks(String url) {
        try {
            URL targetUrl = new URL(url);
            String protocol = targetUrl.getProtocol();
            String serverName = targetUrl.getHost();
            targetUrl = new URL(protocol + "://" + serverName + "/robots.txt");
            return allDisallowedLinks.get(targetUrl.toString());
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid URL: " + url);
        }
    }

    public synchronized boolean isUrlAllowedByRobotsTxt(String url) {
        try {
            fetchDisallowedLinks(url);
            ArrayList<String> disallowedLinks = getDisallowedLinks(url);
            for (String disallowedLink : disallowedLinks) {
                if (url.contains(disallowedLink))
                    return false;
            }
            return true;
        } catch (IOException e) {
            return true;
        }
    }
}