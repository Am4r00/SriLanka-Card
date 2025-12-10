
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
    cardElement.href = '#';
    cardElement.className = 'product-card';
    
    cardElement.addEventListener('click', (e) => {
        e.preventDefault();
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
        showToast('Produto esgotado!', true);
        return;
    }
    
    const quantidade = 1;
    api.addToCart(product.id , quantidade);

    updateCartCount();
    showToast('Produto adicionado ao carrinho!', false);
}

// Exportar funções
window.loadProducts = loadProducts;
window.loadProductDetails = loadProductDetails;
window.addToCart = addToCart;
window.updateCartCount = updateCartCount;
window.formatPrice = formatPrice;

document.addEventListener('DOMContentLoaded', () => {
    updateCartCount();
});




