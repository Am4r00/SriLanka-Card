// Script para gerenciar o carrinho

// Função para renderizar itens do carrinho
function renderCartItems() {
    const cartItemsContainer = document.querySelector('.cart-items');
    const cartHeader = document.querySelector('.cart-header h2');
    
    if (!cartItemsContainer) {
        console.error('Container de itens do carrinho não encontrado');
        return;
    }
    
    const cart = api.getCart();
    
    if (!cart || cart.length === 0) {
        cartItemsContainer.innerHTML = '<div class="empty-cart">Seu carrinho está vazio.</div>';
        if (cartHeader) {
            cartHeader.textContent = 'Meu Carrinho (0)';
        }
        return;
    }
    
    if (cartHeader) {
        const totalItems = cart.reduce((sum, item) => sum + (item.quantity || 1), 0);
        cartHeader.textContent = `Meu Carrinho (${totalItems})`;
    }
    
    cartItemsContainer.innerHTML = '';
    
    cart.forEach(item => {
        const cartItem = createCartItemElement(item);
        cartItemsContainer.appendChild(cartItem);
    });
    
    updateCartTotal();
}

// Função para criar elemento de item do carrinho
function createCartItemElement(item) {
    const cartItem = document.createElement('div');
    cartItem.className = 'cart-item';
    cartItem.dataset.productId = item.id;
    
    // Determinar imagem
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
    
    let imageUrl = '/img/steam-gift-card.png';
    const itemNameLower = item.nome.toLowerCase();
    for (const [key, url] of Object.entries(imageMap)) {
        if (itemNameLower.includes(key)) {
            imageUrl = url;
            break;
        }
    }
    
    const price = item.valor || item.preco || 0;
    const quantity = item.quantity || 1;
    const totalPrice = price * quantity;
    const promoBadge = item.promocao ? '<span class="discount-badge">10% OFF</span>' : '';
    
    cartItem.innerHTML = `
        <img src="${imageUrl}" alt="${item.nome}" class="item-image" onerror="this.src='/img/steam-gift-card.png'">
        <div class="item-details">
            <span class="item-title">${item.nome}</span>
            <div class="item-meta">
                <span class="item-rating">5.0 ⭐</span>
                <span class="item-reviews">15 Avaliações</span>
            </div>
            <div class="item-info">
                <span class="item-size">Tipo: Digital</span>
                <span class="item-color">Valor: ${formatPrice(price)}</span>
            </div>
            <span class="free-shipping">Frete Grátis</span>
            ${promoBadge}
        </div>
        <div class="item-actions">
            <div class="item-prices">
                <span class="current-price">${formatPrice(totalPrice)}</span>
            </div>
            <div class="item-controls">
                <div class="quantity-control">
                    <button onclick="updateQuantity(${item.id}, ${quantity - 1})">-</button>
                    <span>${quantity}</span>
                    <button onclick="updateQuantity(${item.id}, ${quantity + 1})">+</button>
                </div>
                <button class="remove-item-btn" onclick="removeFromCart(${item.id})">
                    Remover
                </button>
            </div>
        </div>
    `;
    
    return cartItem;
}

// Função para atualizar quantidade
function updateQuantity(productId, newQuantity) {
    if (newQuantity <= 0) {
        removeFromCart(productId);
    } else {
        api.updateCartQuantity(productId, newQuantity);
        renderCartItems();
        updateCartCount();
    }
}

// Função para remover do carrinho
function removeFromCart(productId) {
    api.removeFromCart(productId);
    renderCartItems();
    updateCartCount();
}

// Função para atualizar total do carrinho
function updateCartTotal() {
    const total = api.getCartTotal();
    const totalElement = document.querySelector('.cart-total');
    if (totalElement) {
        totalElement.textContent = `Total: ${formatPrice(total)}`;
    }
}

// Inicializar carrinho quando a página carregar
document.addEventListener('DOMContentLoaded', () => {
    renderCartItems();
    updateCartCount();
});

// Exportar funções
window.renderCartItems = renderCartItems;
window.updateQuantity = updateQuantity;
window.removeFromCart = removeFromCart;




