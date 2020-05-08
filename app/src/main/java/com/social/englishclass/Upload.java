package com.social.englishclass;

public class Upload {
    private String mName;
    private String mUrl;
    private String muserId;
    private String mtoken;

    public Upload() {
        //empty constructor needed
    }

    public Upload(String name, String Url, String userId) {
        if (name.trim().equals("")) {
            name = "No Name";
        }

        mName = name;
        mUrl = Url;
        muserId = userId;
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

//    public String getToken() {
//        return mtoken;
//    }

//    public void setToken(String token) {
//        mtoken = token;
//    }

}
