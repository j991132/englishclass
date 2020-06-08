package com.social.englishclass;

public class Upload {
    private String mName;
    private String mUrl;
    private String muserId;
    private String mtoken;
    private String mfilepath;

    public Upload() {
        //empty constructor needed
    }

    public Upload(String name, String Url, String userId, String filepath) {
        if (name.trim().equals("")) {
            name = "No Name";
        }

        mName = name;
        mUrl = Url;
        muserId = userId;
        mfilepath = filepath;
//        mtoken = token;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String Url) {
        mUrl = Url;
    }

    public String getUserId() {
        return muserId;
    }

    public void setUserId(String userId) {
        muserId = userId;
    }

    public String getFilepath() {
        return mfilepath;
    }

    public void setFilepath(String filepath) {
        mfilepath = filepath;
    }

//    public String getToken() {
//        return mtoken;
//    }

//    public void setToken(String token) {
//        mtoken = token;
//    }

}
