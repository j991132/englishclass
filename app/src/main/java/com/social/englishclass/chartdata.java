package com.social.englishclass;

public class chartdata {


    private String stress;
    private String accent;
    private String speed;
    private String pronunciation;
    private String filename;

    public chartdata() {
    }

    public chartdata(String stress, String accent, String speed, String pronunciation, String filename) {
        this.stress = stress;
        this.accent = accent;
        this.speed = speed;
        this.pronunciation = pronunciation;
        this.filename = filename;
    }

    public void setstress(String stress) {
        this.stress = stress;
    }

    public void setaccent(String accent) {
        this.accent = accent;
    }

    public void setspeed(String speed) {
        this.speed = speed;
    }

    public void setpronunciation(String pronunciation) {
        this.pronunciation = pronunciation;
    }
    public void setfilename(String filename) {
        this.filename = filename;
    }

    public String getstress() {
        return stress;
    }

    public String getaccent() {
        return accent;
    }

    public String getspeed() {
        return speed;
    }

    public String getpronunciation() {
        return pronunciation;
    }
    public String getfilename() {
        return filename;
    }


}
