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
        subtitleEl.textContent = 'Latest AI Development & Innovation';
    } else {
        titleEl.textContent = '📈 Stock Market News';
        subtitleEl.textContent = 'Latest Indian Market Updates';
    }
}

function setupEventListeners() {
    document.getElementById('refreshBtn').addEventListener('click', () => fetchNews(true));
}

async function fetchNews(forceRefresh = false) {
    const loadingEl = document.getElementById('loading');
    const loadingTextEl = document.getElementById('loadingText');
    const containerEl = document.getElementById('newsContainer');
    
    loadingEl.style.display = 'block';
    containerEl.innerHTML = '';
    selectedCategory = 'All'; // Reset to All on refresh

    // Update loading text based on news type
    if (currentNewsType === 'ai') {
        loadingTextEl.textContent = forceRefresh ? 'Refreshing AI news...' : 'Loading latest AI news...';
    } else {
        loadingTextEl.textContent = forceRefresh ? 'Refreshing stock market news...' : 'Loading latest stock market news...';
    }

    try {
        // Use /refresh endpoint when forceRefresh is true
        let apiUrl;
        if (currentNewsType === 'ai') {
            apiUrl = forceRefresh ? API_URL + '/refresh' : API_URL;
        } else {
            apiUrl = forceRefresh ? STOCK_MARKET_API_URL + '/refresh' : STOCK_MARKET_API_URL;
        }

        const response = await fetch(apiUrl);
        if (!response.ok) {
            throw new Error('Failed to fetch news');
        }
        
        currentArticles = await response.json();
        setupCategoryTabs(currentArticles);
        filterAndRenderNews();

        // Log success on refresh
        if (forceRefresh) {
            console.log('News refreshed successfully at ' + new Date().toLocaleTimeString());
        }
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

    // Validate and prepare URL
    let articleUrl = article.url || '';
    let linkHtml = '';
    
    if (articleUrl && articleUrl.trim() !== '' && articleUrl !== 'null' && 
        (articleUrl.startsWith('http://') || articleUrl.startsWith('https://'))) {
        // URL is valid - use it directly (don't escape HTML for URLs)
        linkHtml = `<a href="${articleUrl}" target="_blank" rel="noopener noreferrer" class="news-link">
                Read Full Article →
            </a>`;
    } else {
        // URL is invalid or missing - show message or disable link
        linkHtml = `<div class="news-link" style="opacity: 0.6; cursor: not-allowed;">
                Article link not available
            </div>`;
    }

    // Create a unique ID for the comment textarea
    const commentId = 'comment-' + btoa(articleUrl).replace(/[^a-zA-Z0-9]/g, '');
    const comments = article.comments || '';

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
            ${linkHtml}

            <div class="comment-section">
                <label class="comment-label" for="${commentId}">
                    💬 Add Your Notes/Comments:
                </label>
                <textarea
                    id="${commentId}"
                    class="comment-input"
                    placeholder="Add your personal notes or comments about this article..."
                    rows="3"
                    data-url="${escapeHtml(articleUrl)}"
                >${escapeHtml(comments)}</textarea>
                <div class="comment-actions">
                    <button onclick="saveComment('${escapeHtml(articleUrl)}', '${commentId}')" class="save-comment-btn">
                        💾 Save Comment
                    </button>
                    <button onclick="clearComment('${escapeHtml(articleUrl)}', '${commentId}')" class="clear-comment-btn">
                        🗑️ Clear
                    </button>
                </div>
            </div>
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

// Comment Management Functions
async function saveComment(articleUrl, commentId) {
    const commentTextarea = document.getElementById(commentId);
    if (!commentTextarea) {
        console.error('Comment textarea not found:', commentId);
        return;
    }

    const comment = commentTextarea.value.trim();

    try {
        const response = await fetch('/api/news/comments', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                articleUrl: articleUrl,
                comment: comment
            })
        });

        if (!response.ok) {
            throw new Error('Failed to save comment');
        }

        const message = await response.text();

        // Show success feedback
        showTemporaryMessage(commentTextarea, '✅ Comment saved!', 'success');

        console.log('Comment saved successfully for:', articleUrl);
    } catch (error) {
        console.error('Error saving comment:', error);
        showTemporaryMessage(commentTextarea, '❌ Failed to save comment', 'error');
    }
}

async function clearComment(articleUrl, commentId) {
    const commentTextarea = document.getElementById(commentId);
    if (!commentTextarea) {
        console.error('Comment textarea not found:', commentId);
        return;
    }

    if (commentTextarea.value.trim() === '') {
        return; // Nothing to clear
    }

    if (!confirm('Are you sure you want to clear this comment?')) {
        return;
    }

    try {
        // Clear the textarea
        commentTextarea.value = '';

        // Delete from backend
        const response = await fetch(`/api/news/comments?url=${encodeURIComponent(articleUrl)}`, {
            method: 'DELETE'
        });

        if (!response.ok) {
            throw new Error('Failed to delete comment');
        }

        showTemporaryMessage(commentTextarea, '✅ Comment cleared!', 'success');

        console.log('Comment deleted successfully for:', articleUrl);
    } catch (error) {
        console.error('Error deleting comment:', error);
        showTemporaryMessage(commentTextarea, '❌ Failed to clear comment', 'error');
    }
}

function showTemporaryMessage(element, message, type) {
    // Create a message element
    const messageDiv = document.createElement('div');
    messageDiv.className = `comment-feedback ${type}`;
    messageDiv.textContent = message;
    messageDiv.style.cssText = `
        margin-top: 8px;
        padding: 8px 12px;
        border-radius: 4px;
        font-size: 0.9rem;
        font-weight: 600;
        background-color: ${type === 'success' ? '#4CAF50' : '#f44336'};
        color: white;
        animation: fadeIn 0.3s ease-in;
    `;

    // Insert after the textarea
    element.parentNode.insertBefore(messageDiv, element.nextSibling);

    // Remove after 3 seconds
    setTimeout(() => {
        messageDiv.style.animation = 'fadeOut 0.3s ease-out';
        setTimeout(() => messageDiv.remove(), 300);
    }, 3000);
}

