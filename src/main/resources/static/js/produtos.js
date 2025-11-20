// Script para gerenciar produtos/cards

// Função para formatar preço
function formatPrice(value) {
    return new Intl.NumberFormat('pt-BR', {
        style: 'currency',
        currency: 'BRL'
    }).format(value);
}

// Função para criar card de produto
function createProductCard(card) {
    const cardElement = document.createElement('a');
    cardElement.href = `/produtoDetalhe?id=${card.id}`;
    cardElement.className = 'product-card';
    
    // Determinar imagem baseado no nome (fallback)
    const imageMap = {
        'spotify': '/img/spotify.png',
        'shopee': '/img/shopee.jpg',
        'steam': '/img/steam-gift-card.png',
        'ifood': '/img/ifood.png',
        'apple': '/img/apple-gift-card.png',
        'uber': '/img/uber.png',
        'netflix': '/img/netflix.jpg',
        'playstation': '/img/playstation-gift-card.png',
        'paramount': '/img/paramount.jpg',
        'xbox': '/img/xbox.png',
        'airbnb': '/img/airbnb.png',
        'gta': '/img/GTA_V1.jpg',
        'cyberpunk': '/img/cyberpunk.png',
        'witcher': '/img/the_witcher_3.png',
        'eafc': '/img/fc26.jpg',
        'red dead': '/img/red_dead_2.png',
        'forza': '/img/forza-horizon-5.webp',
        'god of war': '/img/god_of_war.jpg',
        'last of us': '/img/the_last_of_us.jpg',
        'ghost': '/img/Ghost_of_Tsushima_capa.png'
    };
    
    let imageUrl = '/img/steam-gift-card.png'; // default
    const cardNameLower = card.nome.toLowerCase();
    for (const [key, url] of Object.entries(imageMap)) {
        if (cardNameLower.includes(key)) {
            imageUrl = url;
            break;
        }
    }
    
    const promoBadge = card.promocao ? '<span class="promo-badge">PROMOÇÃO</span>' : '';
    const oldPrice = card.promocao ? `<span class="old-price">${formatPrice(card.valor * 1.1)}</span>` : '';
    
    cardElement.innerHTML = `
        <img src="${imageUrl}" alt="${card.nome}" class="product-logo" onerror="this.src='/img/steam-gift-card.png'">
        <div class="product-details">
            <h3>${card.nome}</h3>
            <div class="price-container">
                ${oldPrice}
                <p class="current-price">${formatPrice(card.valor)}</p>
            </div>
            ${card.quantidade > 0 ? '' : '<span class="out-of-stock">Esgotado</span>'}
            ${promoBadge}
        </div>
    `;
    
    // Adicionar evento de clique para adicionar ao carrinho
    cardElement.addEventListener('click', (e) => {
        // Permitir navegação normal
        // O carrinho será gerenciado na página de detalhes
    });
    
    return cardElement;
}

// Função para carregar e exibir produtos
async function loadProducts(containerSelector, filter = null) {
    const container = document.querySelector(containerSelector);
    if (!container) {
        console.error(`Container não encontrado: ${containerSelector}`);
        return;
    }
    
    try {
        // Mostrar loading
        container.innerHTML = '<div class="loading">Carregando produtos...</div>';
        
        const cards = await api.listarCards();
        
        if (!cards || cards.length === 0) {
            container.innerHTML = '<div class="no-products">Nenhum produto encontrado.</div>';
            return;
        }
        
        // Filtrar produtos se necessário
        let filteredCards = cards;
        if (filter) {
            filteredCards = cards.filter(filter);
        }
        
        // Limpar container
        container.innerHTML = '';
        
        // Criar e adicionar cards
        filteredCards.forEach(card => {
            const cardElement = createProductCard(card);
            container.appendChild(cardElement);
        });
        
    } catch (error) {
        console.error('Erro ao carregar produtos:', error);
        container.innerHTML = '<div class="error">Erro ao carregar produtos. Tente novamente mais tarde.</div>';
    }
}

// Função para buscar produto por ID
async function loadProductDetails(productId) {
    try {
        const cards = await api.listarCards();
        const card = cards.find(c => c.id === productId);
        return card;
    } catch (error) {
        console.error('Erro ao buscar produto:', error);
        return null;
    }
}

// Função para adicionar ao carrinho
function addToCart(product) {
    if (!product || product.quantidade <= 0) {
        alert('Produto esgotado!');
        return;
    }
    
    api.addToCart({
        id: product.id,
        nome: product.nome,
        valor: product.valor,
        observacoes: product.observacoes,
        promocao: product.promocao
    });
    
    // Atualizar contador do carrinho no header
    updateCartCount();
    
    // Mostrar feedback
    showNotification('Produto adicionado ao carrinho!');
}

// Função para atualizar contador do carrinho
function updateCartCount() {
    const count = api.getCartItemCount();
    const cartCountElement = document.querySelector('.cart-count');
    if (cartCountElement) {
        cartCountElement.textContent = count;
        cartCountElement.style.display = count > 0 ? 'block' : 'none';
    }
}

// Função para mostrar notificação
function showNotification(message, type = 'success') {
    // Criar elemento de notificação
    const notification = document.createElement('div');
    notification.className = `notification ${type}`;
    notification.textContent = message;
    notification.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        background: ${type === 'success' ? '#10b981' : '#ef4444'};
        color: white;
        padding: 16px 24px;
        border-radius: 8px;
        box-shadow: 0 4px 6px rgba(0,0,0,0.1);
        z-index: 10000;
        animation: slideIn 0.3s ease;
    `;
    
    document.body.appendChild(notification);
    
    setTimeout(() => {
        notification.style.animation = 'slideOut 0.3s ease';
        setTimeout(() => notification.remove(), 300);
    }, 3000);
}

// Exportar funções
window.loadProducts = loadProducts;
window.loadProductDetails = loadProductDetails;
window.addToCart = addToCart;
window.updateCartCount = updateCartCount;
window.formatPrice = formatPrice;

// Inicializar contador do carrinho quando a página carregar
document.addEventListener('DOMContentLoaded', () => {
    updateCartCount();
});



