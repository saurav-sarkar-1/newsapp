package com.ainews.controller;

import com.ainews.model.NewsArticle;
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

    @Autowired
    public NewsController(NewsService newsService) {
        this.newsService = newsService;
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
}
