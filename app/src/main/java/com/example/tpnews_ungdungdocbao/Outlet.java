package com.example.tpnews_ungdungdocbao;

public class Outlet {
   private String id;
   private String name;
   private String logoLink;

    public Outlet() {
    }

    public Outlet(String id, String name, String logoLink) {
        this.id = id;
        this.name = name;
        this.logoLink = logoLink;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLogoLink() {
        return logoLink;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLogoLink(String logoLink) {
        this.logoLink = logoLink;
    }
}
