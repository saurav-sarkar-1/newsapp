package com.ainews.controller;

import com.ainews.dto.CommentRequest;
import com.ainews.model.NewsArticle;
import com.ainews.repository.CommentRepository;
import com.ainews.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/news")
@CrossOrigin(origins = "*")
public class NewsController {
    private final NewsService newsService;
    private final CommentRepository commentRepository;

    @Autowired
    public NewsController(NewsService newsService, CommentRepository commentRepository) {
        this.newsService = newsService;
        this.commentRepository = commentRepository;
    }

    @GetMapping
    public ResponseEntity<List<NewsArticle>> getAiNews() {
        List<NewsArticle> articles = newsService.getAiNews();
        return ResponseEntity.ok(articles);
    }

    @GetMapping("/refresh")
    public ResponseEntity<List<NewsArticle>> refreshNews() {
        List<NewsArticle> articles = newsService.getAiNews();
        return ResponseEntity.ok(articles);
    }

    @GetMapping("/stock-market")
    public ResponseEntity<List<NewsArticle>> getStockMarketNews() {
        List<NewsArticle> articles = newsService.getIndianStockMarketNews();
        return ResponseEntity.ok(articles);
    }

    @GetMapping("/stock-market/refresh")
    public ResponseEntity<List<NewsArticle>> refreshStockMarketNews() {
        List<NewsArticle> articles = newsService.getIndianStockMarketNews();
        return ResponseEntity.ok(articles);
    }

    @PostMapping("/comments")
    public ResponseEntity<String> saveComment(@RequestBody CommentRequest request) {
        try {
            if (request.getArticleUrl() == null || request.getArticleUrl().isEmpty()) {
                return ResponseEntity.badRequest().body("Article URL is required");
            }
            commentRepository.saveComment(request.getArticleUrl(), request.getComment());
            return ResponseEntity.ok("Comment saved successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to save comment: " + e.getMessage());
        }
    }

    @GetMapping("/comments")
    public ResponseEntity<String> getComment(@RequestParam String url) {
        try {
            String comment = commentRepository.getComment(url);
            return ResponseEntity.ok(comment);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("");
        }
    }

    @DeleteMapping("/comments")
    public ResponseEntity<String> deleteComment(@RequestParam String url) {
        try {
            commentRepository.deleteComment(url);
            return ResponseEntity.ok("Comment deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to delete comment: " + e.getMessage());
        }
    }
}
