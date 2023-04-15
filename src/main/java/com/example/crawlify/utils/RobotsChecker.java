package com.example.crawlify.utils;

import crawlercommons.robots.BaseRobotRules;
import crawlercommons.robots.SimpleRobotRulesParser;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

public class RobotsChecker {
    public static boolean areRobotsAllowed(String url) {
        String baseUrl = getBaseUrl(url);
        String robotsUrl = baseUrl + "/robots.txt";
        byte[] content;
        try {
            URLConnection connection = new URL(robotsUrl).openConnection();
            connection.setRequestProperty("User-Agent", "CrawlerBot/1.0");
            content = IOUtils.toByteArray(connection);
        } catch (IOException e) {
            // Return true if an error occurs while fetching the robots.txt file
            return true;
        }
        SimpleRobotRulesParser parser = new SimpleRobotRulesParser();
        BaseRobotRules rules = parser.parseContent(robotsUrl, content, "text/plain", "CrawlifyBot");
        return rules.isAllowed(url);
    }

    private static String getBaseUrl(String url) {
        // Extract the protocol and host from the URL
        // For example, "http://www.example.com/path/to/page.html" -> "http://www.example.com"
        try {
            URL uri = new URL(url);
            return uri.getProtocol() + "://" + uri.getHost();
        } catch (IOException e) {
            return null;
        }
    }
}