// Elementos do DOM
const cartItemsContainer = document.getElementById('cart-items');
const cartHeader = document.getElementById('cart-title');
const cartTotalLabel = document.getElementById('cart-total-label');
let cartQuantidade = 0;
let cartValor = 0;

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

    const checkoutBtn = document.getElementById('checkout-button');
    if (checkoutBtn) {
        checkoutBtn.addEventListener('click', (e) => {
            if (cartQuantidade <= 0 || cartValor <= 0) {
                e.preventDefault();
                e.stopPropagation();
                showToast('Seu carrinho está vazio. Adicione itens antes de finalizar.', true);
            }
        });
    }
});

async function carregarCarrinho() {
    const token = getToken();

    if (!token) {
        console.warn('Nenhum token encontrado. Usuário pode não estar logado.');
        window.location.href = '/login';
        return;
    }
    
    try {
        const carrinho = await api.getCart();
        
        if (!carrinho || !carrinho.itens || carrinho.itens.length === 0) {
            cartQuantidade = 0;
            cartValor = 0;
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
            cartQuantidade = totalItems;
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
        cartValor = Number(carrinho.valorTotal) || 0;
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
    const imgSrc = resolveProductImage(item);
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

async function removerItem(produtoId) {
    const token = getToken();
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
