package com.ainews.service;

import com.ainews.model.NewsArticle;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class NewsService {
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Value("${news.api.key:${NEWS_API_KEY:your-api-key-here}}")
    private String apiKey;

    @Value("${news.api.url:https://newsapi.org/v2/everything}")
    private String apiUrl;

    public NewsService() {
        this.webClient = WebClient.builder().build();
        this.objectMapper = new ObjectMapper();
    }

    public List<NewsArticle> getAiNews() {
        // Check if API key is configured
        if (apiKey == null || apiKey.isEmpty() || apiKey.equals("your-api-key-here")) {
            System.out.println("NewsAPI key not configured. Using sample data.");
            return getSampleNews();
        }

        try {
            // Query for AI-related news
            String query = "artificial intelligence OR AI development OR machine learning OR deep learning OR neural networks";
            
            String url = apiUrl + "?q=" + java.net.URLEncoder.encode(query, "UTF-8") +
                        "&sortBy=publishedAt" +
                        "&language=en" +
                        "&pageSize=20" +
                        "&apiKey=" + apiKey;

            String response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            List<NewsArticle> articles = parseNewsResponse(response, true);
            
            // If parsing failed or returned empty, use sample data
            if (articles == null || articles.isEmpty()) {
                return getSampleNews();
            }
            
            return articles;
        } catch (Exception e) {
            System.err.println("Error fetching news: " + e.getMessage());
            // Return sample data if API fails
            return getSampleNews();
        }
    }

    private List<NewsArticle> parseNewsResponse(String jsonResponse, boolean isAiNews) {
        List<NewsArticle> articles = new ArrayList<>();
        try {
            JsonNode root = objectMapper.readTree(jsonResponse);
            JsonNode articlesNode = root.get("articles");

            if (articlesNode != null && articlesNode.isArray()) {
                for (JsonNode articleNode : articlesNode) {
                    NewsArticle article = new NewsArticle();
                    article.setTitle(articleNode.path("title").asText("No title"));
                    article.setDescription(articleNode.path("description").asText(""));
                    article.setContent(articleNode.path("content").asText(""));
                    article.setUrl(articleNode.path("url").asText(""));
                    article.setUrlToImage(articleNode.path("urlToImage").asText(""));
                    
                    JsonNode sourceNode = articleNode.path("source");
                    article.setSource(sourceNode.path("name").asText("Unknown"));
                    
                    article.setAuthor(articleNode.path("author").asText("Unknown"));
                    
                    String publishedAtStr = articleNode.path("publishedAt").asText("");
                    if (!publishedAtStr.isEmpty()) {
                        try {
                            LocalDateTime publishedAt = LocalDateTime.parse(
                                publishedAtStr.substring(0, 19),
                                DateTimeFormatter.ISO_LOCAL_DATE_TIME
                            );
                            article.setPublishedAt(publishedAt);
                        } catch (Exception e) {
                            article.setPublishedAt(LocalDateTime.now());
                        }
                    } else {
                        article.setPublishedAt(LocalDateTime.now());
                    }

                    // Generate summary
                    String description = article.getDescription();
                    String content = article.getContent();
                    if (description != null && !description.isEmpty()) {
                        article.setSummary(description.length() > 300 ? description.substring(0, 300) + "..." : description);
                    } else if (content != null && !content.isEmpty()) {
                        article.setSummary(content.length() > 300 ? content.substring(0, 300) + "..." : content);
                    } else {
                        article.setSummary("No summary available");
                    }

                    // Categorize article based on news type
                    if (isAiNews) {
                        article.setCategory(categorizeArticle(article));
                    } else {
                        article.setCategory(categorizeStockMarketArticle(article));
                    }

                    articles.add(article);
                }
            }
        } catch (Exception e) {
            System.err.println("Error parsing news response: " + e.getMessage());
            return isAiNews ? getSampleNews() : getSampleStockMarketNews();
        }
        return articles;
    }

    private String categorizeArticle(NewsArticle article) {
        String text = (article.getTitle() + " " + article.getDescription() + " " + 
                      (article.getContent() != null ? article.getContent() : "")).toLowerCase();
        
        // Product category keywords
        if (text.matches(".*\\b(product|launch|release|announcement|feature|update|version|beta|alpha|roadmap|road map)\\b.*")) {
            return "Product";
        }
        
        // Engineering category keywords
        if (text.matches(".*\\b(engineering|code|programming|algorithm|architecture|framework|library|api|sdk|development|developer|technical|implementation|optimization|performance|scalability|infrastructure|system design)\\b.*")) {
            return "Engineering";
        }
        
        // Strategy category keywords
        if (text.matches(".*\\b(strategy|business|market|investment|funding|acquisition|merger|partnership|enterprise|corporate|executive|ceo|cfo|leadership|vision|plan|initiative|transformation)\\b.*")) {
            return "Strategy";
        }
        
        // Research category keywords
        if (text.matches(".*\\b(research|study|paper|publication|scientific|academic|university|lab|laboratory|experiment|discovery|breakthrough|innovation|patent)\\b.*")) {
            return "Research";
        }
        
        // Industry category keywords
        if (text.matches(".*\\b(industry|sector|market|trend|analysis|report|forecast|outlook|regulation|policy|compliance|standard)\\b.*")) {
            return "Industry";
        }
        
        // Default category
        return "General";
    }

    private List<NewsArticle> getSampleNews() {
        List<NewsArticle> sampleArticles = new ArrayList<>();
        
        NewsArticle article1 = new NewsArticle(
            "OpenAI Releases GPT-4 with Enhanced Capabilities",
            "OpenAI has announced the release of GPT-4, featuring improved reasoning, creativity, and multimodal capabilities. The new model demonstrates significant advances in understanding context and generating human-like responses.",
            "OpenAI's latest model, GPT-4, represents a major leap forward in artificial intelligence technology. With enhanced reasoning abilities and improved multimodal understanding, GPT-4 can process both text and images, opening new possibilities for AI applications across various industries.",
            "https://openai.com/blog/gpt-4",
            "",
            "OpenAI Team",
            "OpenAI",
            LocalDateTime.now().minusHours(2)
        );
        article1.setCategory("Product");

        NewsArticle article2 = new NewsArticle(
            "Google DeepMind Achieves Breakthrough in Protein Folding",
            "DeepMind's AlphaFold has made significant progress in predicting protein structures, which could revolutionize drug discovery and biological research.",
            "The latest version of AlphaFold has demonstrated unprecedented accuracy in protein structure prediction, potentially accelerating the development of new medicines and treatments for various diseases.",
            "https://deepmind.google/discover/blog/alphafold",
            "",
            "DeepMind Research",
            "Google DeepMind",
            LocalDateTime.now().minusHours(5)
        );
        article2.setCategory("Research");

        NewsArticle article3 = new NewsArticle(
            "Microsoft Integrates AI Copilot Across Office Suite",
            "Microsoft has expanded its AI Copilot functionality to all Office applications, enabling users to leverage AI assistance in Word, Excel, PowerPoint, and more.",
            "The integration of AI Copilot across Microsoft Office represents a significant step in making AI tools accessible to everyday users, potentially transforming how people work with productivity software.",
            "https://www.microsoft.com/copilot",
            "",
            "Microsoft News",
            "Microsoft",
            LocalDateTime.now().minusDays(1)
        );
        article3.setCategory("Product");

        NewsArticle article4 = new NewsArticle(
            "New Neural Network Architecture Improves Training Efficiency",
            "Researchers have developed a novel neural network architecture that reduces training time by 40% while maintaining model accuracy.",
            "The new architecture introduces innovative techniques for gradient computation and weight optimization, making it particularly effective for large-scale machine learning projects.",
            "https://example.com/neural-network",
            "",
            "AI Research Team",
            "Tech Journal",
            LocalDateTime.now().minusHours(8)
        );
        article4.setCategory("Engineering");

        NewsArticle article5 = new NewsArticle(
            "AI Industry Sees Record Investment in Q4 2024",
            "Venture capital firms have invested over $5 billion in AI startups this quarter, marking the highest quarterly investment in the sector's history.",
            "The surge in investment reflects growing confidence in AI technologies and their potential to transform various industries from healthcare to finance.",
            "https://example.com/investment",
            "",
            "Financial Reporter",
            "Business News",
            LocalDateTime.now().minusDays(2)
        );
        article5.setCategory("Strategy");

        NewsArticle article6 = new NewsArticle(
            "New AI Regulations Proposed by European Commission",
            "The European Commission has proposed new regulations governing the use of artificial intelligence, focusing on transparency and ethical AI development.",
            "The proposed regulations aim to establish clear guidelines for AI deployment across various sectors, ensuring responsible AI development and usage.",
            "https://example.com/regulations",
            "",
            "Policy Analyst",
            "EU News",
            LocalDateTime.now().minusDays(3)
        );
        article6.setCategory("Industry");

        sampleArticles.add(article1);
        sampleArticles.add(article2);
        sampleArticles.add(article3);
        sampleArticles.add(article4);
        sampleArticles.add(article5);
        sampleArticles.add(article6);

        return sampleArticles;
    }

    public List<NewsArticle> getIndianStockMarketNews() {
        // Note: NewsAPI has limited coverage of Indian stock market news
        // For better results, consider using Indian financial news APIs or RSS feeds
        // For now, we'll try the API but always fallback to comprehensive sample data
        
        // Check if API key is configured
        if (apiKey == null || apiKey.isEmpty() || apiKey.equals("your-api-key-here")) {
            System.out.println("NewsAPI key not configured. Using sample stock market data.");
            return getSampleStockMarketNews();
        }

        try {
            // Try multiple query strategies for better results
            List<NewsArticle> articles = new ArrayList<>();
            
            // Strategy 1: Search for Indian stock market with specific terms (more focused)
            String query1 = "India stock market Sensex Nifty BSE NSE";
            articles = tryFetchNews(query1, false);
            
            // Strategy 2: If first query fails, try with broader terms
            if (articles == null || articles.isEmpty()) {
                String query2 = "Indian equity market OR Indian stocks OR Mumbai stock exchange";
                articles = tryFetchNews(query2, false);
            }
            
            // Strategy 3: Try with major Indian companies
            if (articles == null || articles.isEmpty()) {
                String query3 = "Reliance Industries OR TCS OR Infosys OR HDFC Bank India";
                articles = tryFetchNews(query3, false);
            }
            
            // If all strategies fail or return very few results, use sample data
            if (articles == null || articles.isEmpty() || articles.size() < 3) {
                System.out.println("NewsAPI returned insufficient results for Indian stock market. Using comprehensive sample data.");
                // Merge API results with sample data if we got some results
                List<NewsArticle> sampleData = getSampleStockMarketNews();
                if (articles != null && !articles.isEmpty()) {
                    articles.addAll(sampleData);
                    return articles;
                }
                return sampleData;
            }
            
            return articles;
        } catch (Exception e) {
            System.err.println("Error fetching stock market news: " + e.getMessage());
            // Always return sample data if API fails
            System.out.println("Falling back to sample stock market data due to API error.");
            return getSampleStockMarketNews();
        }
    }

    private List<NewsArticle> tryFetchNews(String query, boolean isAiNews) {
        try {
            String url = apiUrl + "?q=" + java.net.URLEncoder.encode(query, "UTF-8") +
                        "&sortBy=publishedAt" +
                        "&language=en" +
                        "&pageSize=20" +
                        "&apiKey=" + apiKey;

            String response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            List<NewsArticle> articles = parseNewsResponse(response, isAiNews);
            return articles != null && !articles.isEmpty() ? articles : null;
        } catch (Exception e) {
            System.err.println("Query failed: " + query + " - " + e.getMessage());
            return null;
        }
    }

    private List<NewsArticle> tryFetchNewsWithSources(String sources, boolean isAiNews) {
        try {
            // Try using the top-headlines endpoint with sources
            String topHeadlinesUrl = "https://newsapi.org/v2/top-headlines";
            String url = topHeadlinesUrl + "?sources=" + sources +
                        "&pageSize=20" +
                        "&apiKey=" + apiKey;

            String response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            List<NewsArticle> articles = parseNewsResponse(response, isAiNews);
            return articles != null && !articles.isEmpty() ? articles : null;
        } catch (Exception e) {
            System.err.println("Sources query failed: " + sources + " - " + e.getMessage());
            return null;
        }
    }

    private String categorizeStockMarketArticle(NewsArticle article) {
        String text = (article.getTitle() + " " + article.getDescription() + " " + 
                      (article.getContent() != null ? article.getContent() : "")).toLowerCase();
        
        // Market category keywords
        if (text.matches(".*\\b(market|sensex|nifty|index|indices|benchmark|trading|exchange|bse|nse)\\b.*")) {
            return "Market";
        }
        
        // Stocks category keywords
        if (text.matches(".*\\b(stock|stocks|equity|equities|share|shares|ipo|listing|delisting|dividend|bonus)\\b.*")) {
            return "Stocks";
        }
        
        // Economy category keywords
        if (text.matches(".*\\b(economy|economic|gdp|inflation|rbi|reserve bank|monetary policy|fiscal|budget|gst)\\b.*")) {
            return "Economy";
        }
        
        // Companies category keywords
        if (text.matches(".*\\b(company|companies|corporation|reliance|tcs|infosys|hdfc|icici|sbi|tata|adani)\\b.*")) {
            return "Companies";
        }
        
        // Analysis category keywords
        if (text.matches(".*\\b(analysis|analyst|forecast|outlook|prediction|target|rating|upgrade|downgrade|recommendation)\\b.*")) {
            return "Analysis";
        }
        
        // Default category
        return "General";
    }

    private List<NewsArticle> getSampleStockMarketNews() {
        List<NewsArticle> sampleArticles = new ArrayList<>();
        
        NewsArticle article1 = new NewsArticle(
            "Sensex Surges 500 Points, Nifty Tops 22,000 Mark",
            "Indian stock markets opened strong with Sensex gaining over 500 points and Nifty crossing the 22,000 mark for the first time. Banking and IT stocks led the rally.",
            "The benchmark indices continued their upward momentum with strong buying interest across sectors. Banking stocks saw significant gains following positive quarterly results.",
            "https://www.financialexpress.com/market",
            "",
            "Market Reporter",
            "Financial Express",
            LocalDateTime.now().minusHours(1)
        );
        article1.setCategory("Market");

        NewsArticle article2 = new NewsArticle(
            "Reliance Industries Reports Strong Q4 Earnings",
            "Reliance Industries posted better-than-expected quarterly results with revenue growth of 15% and net profit increasing by 22% year-on-year.",
            "The company's diversified portfolio including retail, telecom, and petrochemicals contributed to the strong performance. Analysts have raised their price targets following the results.",
            "https://economictimes.indiatimes.com/markets",
            "",
            "Business Correspondent",
            "Economic Times",
            LocalDateTime.now().minusHours(3)
        );
        article2.setCategory("Companies");

        NewsArticle article3 = new NewsArticle(
            "RBI Keeps Repo Rate Unchanged at 6.5%",
            "The Reserve Bank of India maintained the repo rate at 6.5% in its latest monetary policy review, citing concerns over inflation while supporting economic growth.",
            "The central bank's decision was in line with market expectations. The RBI governor emphasized the need to balance growth and inflation concerns in the current economic environment.",
            "https://www.business-standard.com/economy",
            "",
            "Economics Editor",
            "Business Standard",
            LocalDateTime.now().minusHours(6)
        );
        article3.setCategory("Economy");

        NewsArticle article4 = new NewsArticle(
            "TCS and Infosys Lead IT Sector Rally",
            "IT stocks witnessed strong buying interest with TCS and Infosys gaining over 3% each. The sector is seeing renewed investor confidence on the back of improving global demand.",
            "Technology stocks have been performing well as companies report strong deal wins and improving margins. Analysts expect the momentum to continue in the coming quarters.",
            "https://www.livemint.com/market",
            "",
            "Tech Markets Reporter",
            "Mint",
            LocalDateTime.now().minusHours(8)
        );
        article4.setCategory("Stocks");

        NewsArticle article5 = new NewsArticle(
            "Analysts Upgrade HDFC Bank to Buy Rating",
            "Leading brokerage firms have upgraded HDFC Bank's rating to 'Buy' citing strong fundamentals and attractive valuations. The stock has been underperforming recently.",
            "Multiple analysts have raised their price targets for HDFC Bank, expecting the stock to deliver strong returns over the next 12-18 months based on improving asset quality and growth prospects.",
            "https://www.moneycontrol.com/markets",
            "",
            "Investment Analyst",
            "Moneycontrol",
            LocalDateTime.now().minusDays(1)
        );
        article5.setCategory("Analysis");

        NewsArticle article6 = new NewsArticle(
            "Indian GDP Growth Forecast Revised Upward to 7.5%",
            "International rating agencies have revised India's GDP growth forecast upward to 7.5% for the current fiscal year, citing strong domestic demand and infrastructure spending.",
            "The upward revision reflects confidence in India's economic recovery and growth trajectory. The country continues to be one of the fastest-growing major economies globally.",
            "https://www.thehindubusinessline.com/economy",
            "",
            "Economics Correspondent",
            "The Hindu BusinessLine",
            LocalDateTime.now().minusDays(2)
        );
        article6.setCategory("Economy");

        NewsArticle article7 = new NewsArticle(
            "Adani Group Stocks Rally on Positive Court Ruling",
            "Adani Group companies saw significant gains following a favorable court ruling. The group's flagship companies gained between 5-10% in today's trading session.",
            "The Supreme Court's decision has provided clarity on regulatory matters, leading to renewed investor interest in Adani Group stocks across various sectors.",
            "https://www.cnbctv18.com/market",
            "",
            "Market Analyst",
            "CNBC TV18",
            LocalDateTime.now().minusDays(1)
        );
        article7.setCategory("Stocks");

        NewsArticle article8 = new NewsArticle(
            "NSE Introduces New Derivatives Products",
            "The National Stock Exchange has launched new derivative products to provide more hedging and trading opportunities for investors in the Indian equity market.",
            "The new products include sectoral indices and volatility-based derivatives, expanding the range of instruments available for traders and investors in the Indian market.",
            "https://www.livemint.com/market",
            "",
            "Exchange Reporter",
            "Livemint",
            LocalDateTime.now().minusDays(3)
        );
        article8.setCategory("Market");

        NewsArticle article9 = new NewsArticle(
            "Banking Stocks Gain on Positive Credit Growth Data",
            "Banking sector stocks rallied after data showed strong credit growth of 15% year-on-year, indicating robust economic activity and improving loan demand.",
            "Private sector banks led the gains with HDFC Bank, ICICI Bank, and Axis Bank all trading higher. Public sector banks also saw buying interest.",
            "https://www.businesstoday.in/markets",
            "",
            "Banking Reporter",
            "Business Today",
            LocalDateTime.now().minusHours(4)
        );
        article9.setCategory("Stocks");

        NewsArticle article10 = new NewsArticle(
            "Foreign Investors Turn Net Buyers in Indian Markets",
            "Foreign Institutional Investors (FIIs) turned net buyers in Indian equity markets after several weeks of selling, investing over $500 million in the past week.",
            "The change in sentiment is attributed to improving global risk appetite and attractive valuations in the Indian market. FIIs have been particularly active in IT and banking sectors.",
            "https://economictimes.indiatimes.com/markets",
            "",
            "Market Analyst",
            "ET Markets",
            LocalDateTime.now().minusHours(12)
        );
        article10.setCategory("Market");

        NewsArticle article11 = new NewsArticle(
            "Wipro Announces Major Acquisition in Cloud Services",
            "Wipro has announced the acquisition of a cloud services company for $200 million, expanding its digital transformation capabilities.",
            "The acquisition is expected to strengthen Wipro's position in the cloud services market and add new capabilities in AI and automation solutions.",
            "https://economictimes.indiatimes.com/tech",
            "",
            "Tech Correspondent",
            "Economic Times",
            LocalDateTime.now().minusDays(1)
        );
        article11.setCategory("Companies");

        NewsArticle article12 = new NewsArticle(
            "Rupee Strengthens Against Dollar on Positive Economic Data",
            "The Indian rupee gained against the US dollar, trading at 82.85, supported by strong economic data and foreign fund inflows.",
            "Analysts expect the rupee to remain stable in the near term, with the Reserve Bank of India likely to intervene if volatility increases.",
            "https://www.financialexpress.com/economy",
            "",
            "Currency Analyst",
            "Financial Express",
            LocalDateTime.now().minusHours(6)
        );
        article12.setCategory("Economy");

        sampleArticles.add(article1);
        sampleArticles.add(article2);
        sampleArticles.add(article3);
        sampleArticles.add(article4);
        sampleArticles.add(article5);
        sampleArticles.add(article6);
        sampleArticles.add(article7);
        sampleArticles.add(article8);
        sampleArticles.add(article9);
        sampleArticles.add(article10);
        sampleArticles.add(article11);
        sampleArticles.add(article12);

        // Ensure all articles have categories
        for (NewsArticle article : sampleArticles) {
            if (article.getCategory() == null || article.getCategory().isEmpty()) {
                article.setCategory(categorizeStockMarketArticle(article));
            }
        }

        return sampleArticles;
    }
}
