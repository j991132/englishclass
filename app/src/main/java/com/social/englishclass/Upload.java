package com.social.englishclass;

public class Upload {
    private String mName;

    public Upload() {
        //empty constructor needed
    }

    public Upload(String name) {
        if (name.trim().equals("")) {
            name = "No Name";
        }

        mName = name;

    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

}
