// Ajuste aqui se seu token estiver salvo com outro nome:
const TOKEN_KEY = 'token';

// Elementos do DOM
const cartItemsContainer = document.getElementById('cart-items');
const cartHeader = document.getElementById('cart-title');
const cartTotalLabel = document.getElementById('cart-total-label');

document.addEventListener('DOMContentLoaded', () => {
    carregarCarrinho();
    
    // Adicionar evento ao botão de fechar do carrinho
    const closeBtn = document.getElementById('cart-close-btn');
    if (closeBtn) {
        closeBtn.addEventListener('click', () => {
            // Voltar para a página anterior
            if (window.history.length > 1) {
                window.history.back();
            } else {
                // Se não houver histórico, redirecionar para home
                window.location.href = '/';
            }
        });
    }
});

async function carregarCarrinho() {
    const token = localStorage.getItem(TOKEN_KEY);

    if (!token) {
        console.warn('Nenhum token encontrado. Usuário pode não estar logado.');
        window.location.href = '/login';
        return;
    }
    
    try {
        const carrinho = await api.getCart();
        
        if (!carrinho || !carrinho.itens || carrinho.itens.length === 0) {
            if (cartItemsContainer) {
                cartItemsContainer.innerHTML = '<div class="empty-cart">Seu carrinho está vazio.</div>';
            }
            if (cartHeader) {
                cartHeader.textContent = 'Meu Carrinho (0)';
            }
            if (cartTotalLabel) {
                cartTotalLabel.textContent = 'Total: R$ 0,00';
            }
            return;
        }
        
        // Atualizar título com quantidade total
        if (cartHeader) {
            const totalItems = carrinho.quantidade || carrinho.itens.reduce((sum, item) => sum + (item.quantidade || 1), 0);
            cartHeader.textContent = `Meu Carrinho (${totalItems})`;
        }
        
        // Limpar container e renderizar itens
        if (cartItemsContainer) {
            cartItemsContainer.innerHTML = '';
            
            carrinho.itens.forEach(item => {
                const cartItem = createCartItemElement(item);
                cartItemsContainer.appendChild(cartItem);
            });
        }
        
        // Atualizar total
        updateCartTotal(carrinho.valorTotal || 0);
    } catch (error) {
        console.error('Erro ao carregar carrinho:', error);
        if (cartItemsContainer) {
            cartItemsContainer.innerHTML = '<div class="empty-cart">Erro ao carregar carrinho. Tente novamente.</div>';
        }
    }
}

// Função para criar elemento de item do carrinho
function createCartItemElement(item) {
    const cartItem = document.createElement('div');
    cartItem.className = 'cart-item';
    cartItem.dataset.productId = item.produtoId;
    
    const nome = item.nome || 'Produto sem nome';
    const imgSrc = resolverImagem(item);
    const precoUnit = Number(item.precoUnitario) || 0;
    const quantidade = item.quantidade || 1;
    const subtotal = Number(item.total) || (precoUnit * quantidade);
    
    const precoUnitFmt = precoUnit.toFixed(2).replace('.', ',');
    const subtotalFmt = subtotal.toFixed(2).replace('.', ',');
    
    cartItem.innerHTML = `
        <img src="${imgSrc}" alt="${nome}" class="item-image" />
        <div class="item-details">
            <span class="item-title">${nome}</span>
            <div class="item-meta">
                <span class="item-info">Tipo: Digital</span>
                <span class="item-info">Preço unitário: R$ ${precoUnitFmt}</span>
                <span class="item-info">Quantidade: ${quantidade}</span>
            </div>
        </div>
        <div class="item-actions">
            <div class="item-prices">
                <span class="current-price">R$ ${subtotalFmt}</span>
            </div>
            <button class="remove-item-btn" data-produto-id="${item.produtoId}">
                Remover
            </button>
        </div>
    `;
    
    // Adicionar evento de remover
    const removeBtn = cartItem.querySelector('.remove-item-btn');
    if (removeBtn) {
        removeBtn.addEventListener('click', async (e) => {
            e.preventDefault();
            const produtoId = e.currentTarget.getAttribute('data-produto-id');
            await removerItem(produtoId);
        });
    }
    
    return cartItem;
}

function resolverImagem(item) {
    const nome = (item.nome || '').toLowerCase();

    // 👉 Gift cards / serviços
    if (nome.includes('apple')) {
        return '/img/apple-gift-card.png';
    }

    if (nome.includes('steam')) {
        return '/img/steam-gift-card.png';
    }

    if (nome.includes('playstation') || nome.includes('psn') || nome.includes('ps4') || nome.includes('ps5')) {
        return '/img/playstation-gift-card.png';
    }

    if (nome.includes('xbox') || nome.includes('xbox card')) {
        return '/img/xbox.png';
    }

    if (nome.includes('airbnb')) {
        return '/img/airbnb.png';
    }

    if (nome.includes('ifood')) {
        return '/img/ifood.png';
    }

    if (nome.includes('netflix')) {
        return '/img/netflix.jpg';
    }

    if (nome.includes('paramount')) {
        return '/img/paramount.jpg';
    }

    if (nome.includes('spotify')) {
        return '/img/spotify.png';
    }

    if (nome.includes('uber')) {
        return '/img/uber.png';
    }

    if (nome.includes('shopee')) {
        return '/img/shopee.jpg';
    }

    // 👉 Jogos específicos
    if (nome.includes('cyberpunk')) {
        return '/img/cyberpunk.png';
    }

    if (nome.includes('fc 26') || nome.includes('fc26') || nome.includes('ea fc')) {
        return '/img/fc26.jpg';
    }

    if (nome.includes('forza')) {
        return '/img/forza-horizon-5.webp';
    }

    if (nome.includes('ghost of tsushima') || nome.includes('tsushima')) {
        return '/img/Ghost_of_Tsushima_capa.png';
    }

    if (nome.includes('god of war')) {
        return '/img/god_of_war.jpg';
    }

    if (nome.includes('gta')) {
        return '/img/GTA_V1.jpg';
    }

    if (nome.includes('red dead')) {
        return '/img/red_dead_2.png';
    }

    if (nome.includes('the last of us')) {
        return '/img/the_last_of_us.jpg';
    }

    if (nome.includes('witcher')) {
        return '/img/the_witcher_3.png';
    }

    // 👉 fallback genérico pra qualquer outro gift card / produto
    if (nome.includes('gift') || nome.includes('card')) {
        return '/img/steam-gift-card.png';
    }

    // 👉 fallback final pra qualquer coisa que não bateu em nada
    return '/img/steam-gift-card.png';
}

async function removerItem(produtoId) {
    const token = localStorage.getItem(TOKEN_KEY);
    if (!token) {
        alert('Você precisa estar logado para remover itens.');
        return;
    }

    try {
        await api.removeFromCart(produtoId);
        // Recarregar carrinho após remover
        await carregarCarrinho();
    } catch (err) {
        console.error('Erro de rede ao remover item:', err);
        alert('Erro ao remover item do carrinho. Tente novamente.');
    }
}

function updateCartTotal(valorTotal) {
    if (cartTotalLabel) {
        const valorFormatado = valorTotal.toFixed(2).replace('.', ',');
        cartTotalLabel.textContent = `Total: R$ ${valorFormatado}`;
    }
}

// Exportar funções para uso global
window.carregarCarrinho = carregarCarrinho;
window.removerItem = removerItem;
