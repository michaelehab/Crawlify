package com.example.crawlify.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

public class RobotsCheckerTest {

    private RobotsChecker robotsChecker;

    @BeforeEach
    public void setUp() {
        robotsChecker = new RobotsChecker();
    }

    @Test
    public void testIsUrlAllowedByRobotsTxt() {
        boolean result1 = robotsChecker.isUrlAllowedByRobotsTxt("https://www.google.com");
        boolean result2 = robotsChecker.isUrlAllowedByRobotsTxt("https://www.mozilla.org/en-US/");
        boolean result3 = robotsChecker.isUrlAllowedByRobotsTxt("https://www.microsoft.com/en-us/");
        boolean result4 = robotsChecker.isUrlAllowedByRobotsTxt("https://www.facebook.com/");
        Assertions.assertTrue(result1);
        Assertions.assertTrue(result2);
        Assertions.assertTrue(result3);
        Assertions.assertFalse(result4);
    }

    @Test
    public void testGetDisallowedLinks() {
        List<String> urlsToTest = Arrays.asList(
                "https://www.google.com",
                "https://www.mozilla.org/en-US/",
                "https://www.microsoft.com/en-us/",
                "https://www.facebook.com/"
        );
        for (String url : urlsToTest) {
            robotsChecker.isUrlAllowedByRobotsTxt(url); // fetch the robots.txt file
            Assertions.assertNotNull(robotsChecker.getDisallowedLinks(url));
        }
    }
}