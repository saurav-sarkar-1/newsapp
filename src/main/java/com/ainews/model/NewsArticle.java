package com.ainews.model;

import java.time.LocalDateTime;

public class NewsArticle {
    private String title;
    private String description;
    private String content;
    private String url;
    private String urlToImage;
    private String author;
    private String source;
    private LocalDateTime publishedAt;
    private String summary;
    private String category;
    private String comments;

    public NewsArticle() {
    }

    public NewsArticle(String title, String description, String content, String url, 
                      String urlToImage, String author, String source, LocalDateTime publishedAt) {
        this.title = title;
        this.description = description;
        this.content = content;
        this.url = url;
        this.urlToImage = urlToImage;
        this.author = author;
        this.source = source;
        this.publishedAt = publishedAt;
        this.summary = description != null && !description.isEmpty() ? description : 
                      (content != null && content.length() > 200 ? content.substring(0, 200) + "..." : content);
    }

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrlToImage() {
        return urlToImage;
    }

    public void setUrlToImage(String urlToImage) {
        this.urlToImage = urlToImage;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(LocalDateTime publishedAt) {
        this.publishedAt = publishedAt;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
