package com.example.tpnews_ungdungdocbao;

public class Article {
    private String id;
    private String title;
    private String description;
    private String content;
    private String image;
    private String outletName;
    private String outletLogo;
    private String category;

    public Article() {
    }

    public Article(String id, String title, String description, String content, String image, String outletName, String outletLogo, String category) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.content = content;
        this.image = image;
        this.outletName = outletName;
        this.outletLogo = outletLogo;
        this.category = category;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getContent() {
        return content;
    }

    public String getImage() {
        return image;
    }

    public String getOutletName() {
        return outletName;
    }

    public String getOutletLogo() {
        return outletLogo;
    }

    public String getCategory() {
        return category;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setOutletName(String outletName) {
        this.outletName = outletName;
    }

    public void setOutletLogo(String outletLogo) {
        this.outletLogo = outletLogo;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
