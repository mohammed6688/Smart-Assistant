package com.mocomp.smartassistant;

public class Word {
    String eng;
    String ar;

    public Word(String eng, String ar) {
        this.eng = eng;
        this.ar = ar;
    }

    public String getEng() {
        return eng;
    }

    public void setEng(String eng) {
        this.eng = eng;
    }

    public String getAr() {
        return ar;
    }

    public void setAr(String ar) {
        this.ar = ar;
    }
}
