package com.ainews.model;

import java.time.LocalDateTime;

public class JobPosting {
    private String title;
    private String company;
    private String location;
    private String description;
    private String url;
    private String jobType; // Full-time, Part-time, Contract, etc.
    private String experienceLevel; // Entry, Mid, Senior
    private String salary;
    private LocalDateTime postedAt;
    private String source; // LinkedIn, Indeed, GitHub Jobs, etc.
    private String category;
    private String logoUrl;
    private String[] skills;
    private String comments;

    public JobPosting() {
    }

    public JobPosting(String title, String company, String location, String description,
                     String url, String jobType, String experienceLevel, String salary,
                     LocalDateTime postedAt, String source) {
        this.title = title;
        this.company = company;
        this.location = location;
        this.description = description;
        this.url = url;
        this.jobType = jobType;
        this.experienceLevel = experienceLevel;
        this.salary = salary;
        this.postedAt = postedAt;
        this.source = source;
    }

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getJobType() {
        return jobType;
    }

    public void setJobType(String jobType) {
        this.jobType = jobType;
    }

    public String getExperienceLevel() {
        return experienceLevel;
    }

    public void setExperienceLevel(String experienceLevel) {
        this.experienceLevel = experienceLevel;
    }

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }

    public LocalDateTime getPostedAt() {
        return postedAt;
    }

    public void setPostedAt(LocalDateTime postedAt) {
        this.postedAt = postedAt;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String[] getSkills() {
        return skills;
    }

    public void setSkills(String[] skills) {
        this.skills = skills;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}

