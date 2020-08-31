package com.social.englishclass;

import android.util.Log;

public class chartdata implements Comparable<chartdata>{


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


    @Override
    public int compareTo(chartdata chartdata) {
           /*
        if (Integer.parseInt(this.filename.substring(0,4)+this.filename.substring(6,8)+this.filename.substring(10,12)) < Integer.parseInt(chartdata.filename.substring(0,4)+chartdata.filename.substring(6,8)+chartdata.filename.substring(10,12))) {
            Log.e("파일명 인트화",""+ this.filename.substring(0,4)+this.filename.substring(6,8)+this.filename.substring(10,12));
            return -1;

        } else if (Integer.parseInt(this.filename.substring(0,4)+this.filename.substring(6,8)+this.filename.substring(10,12)) == Integer.parseInt(chartdata.filename.substring(0,4)+chartdata.filename.substring(6,8)+chartdata.filename.substring(10,12))) {

            return 0;

        } else {

            return 1;
    }

         */

        return filename.compareTo(chartdata.getfilename());


    }
}

