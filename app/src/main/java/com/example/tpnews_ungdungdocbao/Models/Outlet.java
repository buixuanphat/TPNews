package com.example.tpnews_ungdungdocbao.Models;

import androidx.annotation.NonNull;

public class Outlet {
   private String id;
   private String name;
   private String logo;

    public Outlet() {
    }

    public Outlet(String id, String name, String logo) {
        this.id = id;
        this.name = name;
        this.logo = logo;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLogo() {
        return logo;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }
    @NonNull
    @Override
    public String toString() {
        return name;
    }
}
