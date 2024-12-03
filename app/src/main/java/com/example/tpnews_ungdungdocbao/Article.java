package com.example.tpnews_ungdungdocbao;

public class Article {
    private String id;
    private String title;
    private String description;
    private String content;
    private String imageUrl;
    private String outlet;
    private String category;

    public Article() {
    }

    public Article(String id, String title, String description, String content, String imageUrl, String outlet, String category) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.content = content;
        this.imageUrl = imageUrl;
        this.outlet = outlet;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public String getOutlet() {
        return outlet;
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

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setOutlet(String outlet) {
        this.outlet = outlet;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
