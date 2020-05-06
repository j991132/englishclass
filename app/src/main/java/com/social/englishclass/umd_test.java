package com.social.englishclass;

public class umd_test {
    private String stress;
    private String accent;
    private String speed;
    private String pronunciation;

    public umd_test() {}
    public umd_test(String stress, String accent, String speed, String pronunciation) {
        this.stress = stress;
        this.accent = accent;
        this.speed = speed;
        this.pronunciation = pronunciation;
    }

    public void setstress(String stress) {
        this.stress = stress;
    }

    public void setaccent(String accent) {
        this.accent = accent;
    }
    public void setspeed(String speed) {
        this.speed = speed;
    } public void setpronunciation(String pronunciation) {
        this.pronunciation = pronunciation;
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
}
