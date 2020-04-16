package com.social.englishclass;

public class Upload {
    private String mName;
    private String mUrl;

    public Upload() {
        //empty constructor needed
    }

    public Upload(String name, String Url) {
        if (name.trim().equals("")) {
            name = "No Name";
        }

        mName = name;
        mUrl = Url;
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

}
