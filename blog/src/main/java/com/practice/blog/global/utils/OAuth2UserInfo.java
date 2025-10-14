package com.practice.blog.global.utils;

import java.util.Map;

public class OAuth2UserInfo {

    private Map<String, Object> attributes;

    public OAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public String getNickname() {
        return (String) attributes.get("name");
    }
    public String getEmail() {
        return (String) attributes.get("email");
    }
}
