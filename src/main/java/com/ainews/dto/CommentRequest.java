package com.ainews.dto;

public class CommentRequest {
    private String articleUrl;
    private String comment;

    public CommentRequest() {
    }

    public CommentRequest(String articleUrl, String comment) {
        this.articleUrl = articleUrl;
        this.comment = comment;
    }

    public String getArticleUrl() {
        return articleUrl;
    }

    public void setArticleUrl(String articleUrl) {
        this.articleUrl = articleUrl;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}

