package com.userInformation.registrationForm.controller;

import jakarta.servlet.http.HttpServletRequest;

public class Utility {
    public static String getSiteURL(HttpServletRequest request) {
        String siteURL = request.getRequestURL().toString();
        return siteURL.substring(0, siteURL.lastIndexOf("/"));
    }
}
