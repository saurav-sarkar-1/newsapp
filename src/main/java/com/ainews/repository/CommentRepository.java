package com.ainews.repository;

import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class CommentRepository {
    // Using ConcurrentHashMap for thread-safe operations
    // Key: article URL, Value: user comment
    private final Map<String, String> commentsStore = new ConcurrentHashMap<>();

    /**
     * Save or update a comment for a specific article URL
     * @param articleUrl The URL of the article
     * @param comment The user's comment
     */
    public void saveComment(String articleUrl, String comment) {
        if (articleUrl != null && !articleUrl.isEmpty()) {
            commentsStore.put(articleUrl, comment);
        }
    }

    /**
     * Retrieve the comment for a specific article URL
     * @param articleUrl The URL of the article
     * @return The comment, or empty string if not found
     */
    public String getComment(String articleUrl) {
        if (articleUrl == null || articleUrl.isEmpty()) {
            return "";
        }
        return commentsStore.getOrDefault(articleUrl, "");
    }

    /**
     * Delete a comment for a specific article URL
     * @param articleUrl The URL of the article
     */
    public void deleteComment(String articleUrl) {
        if (articleUrl != null && !articleUrl.isEmpty()) {
            commentsStore.remove(articleUrl);
        }
    }

    /**
     * Get the total number of comments stored
     * @return The count of comments
     */
    public int getCommentCount() {
        return commentsStore.size();
    }

    /**
     * Clear all comments
     */
    public void clearAllComments() {
        commentsStore.clear();
    }
}

