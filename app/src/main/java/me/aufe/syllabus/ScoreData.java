package me.aufe.syllabus;

public class ScoreData {
    private String name;
    private String org;
    private String sno;

    public ScoreData(String name, String org, String sno) {
        this.name = name;
        this.org = org;
        this.sno = sno;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrg() {
        return org;
    }

    public void setOrg(String org) {
        this.org = org;
    }

    public String getSno() {
        return sno;
    }

    public void setSno(String sno) {
        this.sno = sno;
    }
}
