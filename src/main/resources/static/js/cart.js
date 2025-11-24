// Ajuste aqui se seu token estiver salvo com outro nome:
const TOKEN_KEY = 'token';

document.addEventListener('DOMContentLoaded', () => {
    carregarCarrinho();
    // ❌ NÃO adicionamos mais listener de finalizarCompra aqui.
    // A tela de pagamento (/payment) é que vai finalizar o pedido.
});

async function carregarCarrinho() {
    const token = localStorage.getItem(TOKEN_KEY);

    if (!token) {
        console.warn('Nenhum token encontrado. Usuário pode não estar logado.');
        window.location.href = '/login';
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

    carrinho.itens.forEach(item => {
        const div = document.createElement('div');
        div.className = 'cart-item';

        const nome = (item.nome || `${item.nome}`).toLowerCase();
        const imgSrc = resolverImagem(item);

        const precoUnit = Number(item.precoUnitario) || 0;
        const subtotal = Number(item.total ?? (precoUnit * (item.quantidade || 0)));

        const precoUnitFmt = precoUnit.toFixed(2).replace('.', ',');
        const subtotalFmt = subtotal.toFixed(2).replace('.', ',');

        div.innerHTML = `
            <img src="${imgSrc}" alt="${nome}" class="item-image" />
            <div class="item-details">
                <span class="item-title">${nome}</span>
                <div class="item-info">
                    <span class="item-size">Tipo: Digital</span>
                    <span class="item-color">Valor unitário: R$ ${precoUnitFmt}</span>
                </div>
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
        `;

        itemsContainer.appendChild(div);
    });

    // ligar eventos dos botões "Remover"
    itemsContainer.querySelectorAll('.remove-item-btn').forEach(btn => {
        btn.addEventListener('click', async (e) => {
            const produtoId = e.currentTarget.getAttribute('data-produto-id');
            await removerItem(produtoId);
            await carregarCarrinho(); // recarrega carrinho depois de remover
        });
    });
}

function resolverImagem(item) {
    const nome = (item.nome || `${item.nome}`).toLowerCase();

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
        return '/img/forza-horizon-5-64md.1200.webp';
    }

    if (nome.includes('ghost of tsushima') || nome.includes('tsushima')) {
        return '/img/Ghost_of_Tsushima_capa.png';
    }

    if (nome.includes('god of war')) {
        return '/img/god-of-war.jpg';
    }

    if (nome.includes('gta')) {
        return '/img/gta.jpg';
    }

    if (nome.includes('red dead')) {
        return '/img/red-dead-2.png';
    }

    if (nome.includes('the last of us')) {
        return '/img/the-last-of-us.jpg';
    }

    if (nome.includes('witcher')) {
        return '/img/the-witcer-3'; // confere o nome/ extensão desse arquivo aí na pasta img
    }

    // 👉 fallback genérico pra qualquer outro gift card / produto
    if (nome.includes('gift') || nome.includes('card')) {
        return '/img/default-card.jpg';
    }

    // 👉 fallback final pra qualquer coisa que não bateu em nada
    return '/img/default-card.jpg';
}

async function removerItem(produtoId) {
    const token = localStorage.getItem(TOKEN_KEY);
    if (!token) return;


        if (!response.ok) {
            console.error('Erro ao remover item:', response.status);
        }
    } catch (err) {
        console.error('Erro de rede ao remover item:', err);
    }
}
