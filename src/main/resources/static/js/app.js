const API_URL = '/api/news';
const STOCK_MARKET_API_URL = '/api/news/stock-market';

let currentArticles = [];
let selectedCategory = 'All';
let currentNewsType = 'ai'; // 'ai' or 'stock-market'

// Initialize app
document.addEventListener('DOMContentLoaded', () => {
    setupTopLevelTabs();
    fetchNews();
    setupEventListeners();
});

function setupTopLevelTabs() {
    const topTabs = document.querySelectorAll('.top-tab');
    topTabs.forEach(tab => {
        tab.addEventListener('click', () => {
            const newsType = tab.dataset.newsType;
            currentNewsType = newsType;
            selectedCategory = 'All'; // Reset category when switching news type
            
            // Update active tab
            topTabs.forEach(t => t.classList.remove('active'));
            tab.classList.add('active');
            
            // Update header
            updateHeader(newsType);
            
            // Fetch news for selected type
            fetchNews();
        });
    });
}

function updateHeader(newsType) {
    const titleEl = document.getElementById('mainTitle');
    const subtitleEl = document.getElementById('mainSubtitle');
    
    if (newsType === 'ai') {
        titleEl.textContent = '🤖 AI News';
        subtitleEl.textContent = 'Latest AI Development & Industry News';
    } else {
        titleEl.textContent = '📈 Indian Stock Market';
        subtitleEl.textContent = 'Latest Stock Market News & Updates';
    }
}

function setupEventListeners() {
    document.getElementById('refreshBtn').addEventListener('click', fetchNews);
}

async function fetchNews() {
    const loadingEl = document.getElementById('loading');
    const loadingTextEl = document.getElementById('loadingText');
    const containerEl = document.getElementById('newsContainer');
    
    loadingEl.style.display = 'block';
    containerEl.innerHTML = '';
    selectedCategory = 'All'; // Reset to All on refresh

    // Update loading text based on news type
    if (currentNewsType === 'ai') {
        loadingTextEl.textContent = 'Loading latest AI news...';
    } else {
        loadingTextEl.textContent = 'Loading latest stock market news...';
    }

    try {
        const apiUrl = currentNewsType === 'ai' ? API_URL : STOCK_MARKET_API_URL;
        const response = await fetch(apiUrl);
        if (!response.ok) {
            throw new Error('Failed to fetch news');
        }
        
        currentArticles = await response.json();
        setupCategoryTabs(currentArticles);
        filterAndRenderNews();
    } catch (error) {
        console.error('Error fetching news:', error);
        showError('Failed to load news. Please try again later.');
    } finally {
        loadingEl.style.display = 'none';
    }
}

function setupCategoryTabs(articles) {
    const tabsContainer = document.getElementById('categoryTabs');
    const categories = ['All'];
    
    // Extract unique categories
    articles.forEach(article => {
        const category = article.category || 'General';
        if (!categories.includes(category)) {
            categories.push(category);
        }
    });
    
    // Create category tabs
    tabsContainer.innerHTML = categories.map(category => 
        `<button class="category-tab ${category === selectedCategory ? 'active' : ''}" data-category="${category}">${category}</button>`
    ).join('');
    
    // Show tabs
    tabsContainer.style.display = 'flex';
    
    // Add event listeners to tabs
    tabsContainer.querySelectorAll('.category-tab').forEach(tab => {
        tab.addEventListener('click', () => {
            selectedCategory = tab.dataset.category;
            tabsContainer.querySelectorAll('.category-tab').forEach(t => t.classList.remove('active'));
            tab.classList.add('active');
            filterAndRenderNews();
        });
    });
}

function filterAndRenderNews() {
    const filteredArticles = selectedCategory === 'All' 
        ? currentArticles 
        : currentArticles.filter(article => (article.category || 'General') === selectedCategory);
    renderNews(filteredArticles);
}

function renderNews(articles) {
    const container = document.getElementById('newsContainer');
    
    if (!articles || articles.length === 0) {
        container.innerHTML = `
            <div class="empty-state">
                <p>No news articles found in this category. Try selecting a different category.</p>
            </div>
        `;
        return;
    }

    container.innerHTML = articles.map(article => createNewsCard(article)).join('');
}

function createNewsCard(article) {
    const publishedDate = article.publishedAt 
        ? formatDate(article.publishedAt) 
        : 'Date not available';
    
    const author = article.author && article.author !== 'Unknown' 
        ? `<div class="news-author">By ${escapeHtml(article.author)}</div>` 
        : '';
    
    const image = article.urlToImage && article.urlToImage !== 'null' && article.urlToImage.trim() !== ''
        ? `<img src="${escapeHtml(article.urlToImage)}" alt="${escapeHtml(article.title)}" class="news-image" onerror="this.style.display='none'">`
        : '';

    const category = article.category || 'General';
    const categoryClass = category.toLowerCase();

    return `
        <div class="news-card">
            ${image}
            <span class="news-category ${categoryClass}">${escapeHtml(category)}</span>
            <div class="news-header">
                <h2 class="news-title">${escapeHtml(article.title)}</h2>
                <div class="news-meta">
                    <span class="news-source">${escapeHtml(article.source || 'Unknown Source')}</span>
                    <span class="news-date">${publishedDate}</span>
                </div>
            </div>
            <div class="news-summary">${escapeHtml(article.summary || article.description || 'No summary available')}</div>
            ${author}
            <a href="${escapeHtml(article.url)}" target="_blank" rel="noopener noreferrer" class="news-link">
                Read Full Article →
            </a>
        </div>
    `;
}

function formatDate(dateString) {
    try {
        const date = new Date(dateString);
        const now = new Date();
        const diffMs = now - date;
        const diffMins = Math.floor(diffMs / 60000);
        const diffHours = Math.floor(diffMs / 3600000);
        const diffDays = Math.floor(diffMs / 86400000);

        if (diffMins < 1) {
            return 'Just now';
        } else if (diffMins < 60) {
            return `${diffMins} minute${diffMins > 1 ? 's' : ''} ago`;
        } else if (diffHours < 24) {
            return `${diffHours} hour${diffHours > 1 ? 's' : ''} ago`;
        } else if (diffDays < 7) {
            return `${diffDays} day${diffDays > 1 ? 's' : ''} ago`;
        } else {
            return date.toLocaleDateString('en-US', { 
                year: 'numeric', 
                month: 'short', 
                day: 'numeric' 
            });
        }
    } catch (e) {
        return 'Date not available';
    }
}

function escapeHtml(text) {
    if (!text) return '';
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

function showError(message) {
    const container = document.getElementById('newsContainer');
    container.innerHTML = `
        <div class="error-message">
            <p>${escapeHtml(message)}</p>
        </div>
    `;
}
