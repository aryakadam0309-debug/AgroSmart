package com.agrosmart.models;

import java.io.Serializable;

public class NewsItem implements Serializable {
    private Long id;
    private String title;
    private String shortDescription;
    private String fullContent;
    private String category; // Farming, Crops, Government, Markets, Agri-Tech, Weather
    private String imageUrl;
    private String sourceName;
    private String publishedDate;

    public NewsItem() {}

    public NewsItem(Long id, String title, String shortDescription, String fullContent, String category, String imageUrl, String sourceName, String publishedDate) {
        this.id = id;
        this.title = title;
        this.shortDescription = shortDescription;
        this.fullContent = fullContent;
        this.category = category;
        this.imageUrl = imageUrl;
        this.sourceName = sourceName;
        this.publishedDate = publishedDate;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getShortDescription() { return shortDescription; }
    public void setShortDescription(String shortDescription) { this.shortDescription = shortDescription; }

    public String getFullContent() { return fullContent; }
    public void setFullContent(String fullContent) { this.fullContent = fullContent; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getSourceName() { return sourceName; }
    public void setSourceName(String sourceName) { this.sourceName = sourceName; }

    public String getPublishedDate() { return publishedDate; }
    public void setPublishedDate(String publishedDate) { this.publishedDate = publishedDate; }
}
