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

    try {
        const response = await fetch('/api/carrinho', {
            method: 'GET',
            headers: {
                'Authorization': 'Bearer ' + token,
                'Accept': 'application/json'
            }
        });

        if (!response.ok) {
            console.error('Erro ao buscar carrinho:', response.status);
            return;
        }

        const carrinho = await response.json();
        renderizarCarrinho(carrinho);

    } catch (err) {
        console.error('Erro de rede ao buscar carrinho:', err);
    }
}

function renderizarCarrinho(carrinho) {
    const itemsContainer = document.getElementById('cart-items');
    const titleEl = document.getElementById('cart-title');
    const totalLabel = document.getElementById('cart-total-label');
    const checkoutButton = document.getElementById('checkout-button');

    if (!itemsContainer) return;

    // limpa tudo antes de renderizar
    itemsContainer.innerHTML = '';

    const quantidadeTotal = Number(carrinho.quantidade) || 0;
    const valorTotal = Number(carrinho.valorTotal) || 0;

    // título: Meu Carrinho (X)
    if (titleEl) {
        titleEl.textContent = `Meu Carrinho (${quantidadeTotal})`;
    }

    // total
    if (totalLabel) {
        totalLabel.textContent = `Total: R$ ${valorTotal.toFixed(2).replace('.', ',')}`;
    }

    // habilita/desabilita botão comprar
    if (checkoutButton) {
        checkoutButton.disabled = quantidadeTotal === 0;
    }

    // se não tiver itens:
    if (!carrinho.itens || carrinho.itens.length === 0) {
        const emptyMsg = document.createElement('p');
        emptyMsg.textContent = 'Seu carrinho está vazio.';
        emptyMsg.classList.add('empty-cart-message');
        itemsContainer.appendChild(emptyMsg);
        return;
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
            <div class="item-actions">
                <div class="item-prices">
                    <span class="current-price">Subtotal: R$ ${subtotalFmt}</span>
                </div>
                <div class="item-controls">
                    <div class="quantity-control">
                        <span>Qtd: ${item.quantidade}</span>
                    </div>
                    <button class="remove-item-btn" data-produto-id="${item.produtoId}">
                        Remover
                    </button>
                </div>
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

    try {
        const response = await fetch(`/api/carrinho/itens/${produtoId}`, {
            method: 'DELETE',
            headers: {
                'Authorization': 'Bearer ' + token
            }
        });

        if (!response.ok) {
            console.error('Erro ao remover item:', response.status);
        }
    } catch (err) {
        console.error('Erro de rede ao remover item:', err);
    }
}
