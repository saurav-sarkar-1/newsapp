# AI News App

A Java Spring Boot application that fetches and displays the latest news about AI Development and Industry. The app shows news articles with summaries and provides links to the original sources.

## Features

- ✅ Fetches latest AI-related news from NewsAPI
- ✅ Displays news articles with titles, summaries, and metadata
- ✅ Shows article source, author, and publication date
- ✅ Clickable links to original news sources
- ✅ Beautiful, responsive UI with gradient design
- ✅ Refresh functionality to get latest news
- ✅ Fallback to sample data if API is unavailable

## Tech Stack

- **Backend**: Java 17 with Spring Boot 3.2.0
- **Frontend**: Vanilla JavaScript, HTML5, CSS3
- **Build Tool**: Maven
- **News API**: NewsAPI.org (free tier available)

## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- NewsAPI key (free at https://newsapi.org/)

## Installation

1. Clone or navigate to the project directory:
```bash
cd ai-news-app
```

2. Get a free API key from [NewsAPI](https://newsapi.org/):
   - Sign up for a free account
   - Get your API key from the dashboard

3. Configure the API key:
   
   **Option 1: Update application.properties**
   ```properties
   news.api.key=your-actual-api-key-here
   ```

   **Option 2: Use environment variable**
   ```bash
   export NEWS_API_KEY=your-actual-api-key-here
   ```
   Then update the code to read from environment variable.

   **Option 3: Pass as system property**
   ```bash
   mvn spring-boot:run -Dnews.api.key=your-actual-api-key-here
   ```

4. Build the project:
```bash
mvn clean install
```

## Running the Application

### Using Maven

```bash
mvn spring-boot:run
```

Or with API key:
```bash
mvn spring-boot:run -Dnews.api.key=your-actual-api-key-here
```

The application will start on `http://localhost:8081`

### Using Java

After building:
```bash
java -jar target/ai-news-app-1.0.0.jar
```

## API Endpoints

- `GET /api/news` - Get all AI news articles
- `GET /api/news/refresh` - Refresh and get latest news

## Usage

1. **Access the Application**: Open your browser and navigate to `http://localhost:8081`
2. **View News**: The app automatically loads the latest AI news articles
3. **Read Articles**: Click "Read Full Article →" to navigate to the original source
4. **Refresh News**: Click the "🔄 Refresh News" button to get the latest articles

## News Sources

The app fetches news from various sources including:
- TechCrunch
- The Verge
- Wired
- MIT Technology Review
- And many more tech and AI-focused publications

## Query Parameters

The app searches for news using the following query:
- "artificial intelligence OR AI development OR machine learning OR deep learning OR neural networks"

## Fallback Mode

If the NewsAPI is unavailable or the API key is not configured, the app will display sample AI news articles to demonstrate the functionality.

## Project Structure

```
ai-news-app/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/ainews/
│   │   │       ├── AiNewsApplication.java
│   │   │       ├── controller/
│   │   │       │   └── NewsController.java
│   │   │       ├── model/
│   │   │       │   ├── NewsArticle.java
│   │   │       │   └── NewsResponse.java
│   │   │       └── service/
│   │   │           └── NewsService.java
│   │   └── resources/
│   │       ├── application.properties
│   │       └── static/
│   │           ├── index.html
│   │           ├── css/
│   │           │   └── styles.css
│   │           └── js/
│   │               └── app.js
│   └── test/
├── pom.xml
└── README.md
```

## Notes

- The free tier of NewsAPI has rate limits (100 requests per day)
- For production use, consider caching news articles
- The app uses sample data as fallback if the API fails
- News articles are sorted by publication date (newest first)

## Development

To run in development mode with hot reload:

```bash
mvn spring-boot:run
```

Spring Boot DevTools is included, which will automatically restart the application when code changes are detected.

## Building for Production

```bash
mvn clean package
```

This will create a JAR file in the `target` directory that can be run independently.

## License

This project is open source and available for educational purposes.
