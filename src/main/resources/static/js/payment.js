const TOKEN_KEY = 'token';
const PAYMENT_OK_KEY = 'payment_validated'; // flag simples pra evitar acesso direto na confirmação

document.addEventListener('DOMContentLoaded', () => {
    carregarCarrinhoPagamento();

    const btn = document.getElementById('btn-finalizar-pagamento');
    if (btn) {
        btn.addEventListener('click', onConcluirPagamento);
    }
});

async function carregarCarrinhoPagamento() {
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
        renderizarCarrinhoPagamento(carrinho);

    } catch (err) {
        console.error('Erro de rede ao buscar carrinho:', err);
    }
}

function renderizarCarrinhoPagamento(carrinho) {
    const itemsContainer = document.getElementById('payment-items');
    const countEl = document.getElementById('payment-item-count');
    const totalEl = document.getElementById('payment-total-label');
    const finalizarBtn = document.getElementById('btn-finalizar-pagamento');

    if (!itemsContainer) return;

    itemsContainer.innerHTML = '';

    const quantidadeTotal = Number(carrinho.quantidade) || 0;
    const valorTotal = Number(carrinho.valorTotal) || 0;

    if (countEl) countEl.textContent = `${quantidadeTotal} item(ns)`;
    if (totalEl) totalEl.textContent = `Total: R$ ${valorTotal.toFixed(2).replace('.', ',')}`;

    if (finalizarBtn) finalizarBtn.disabled = quantidadeTotal === 0;

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

        const nome = (item.nome || '').toLowerCase();
        const imgSrc = resolverImagem(item);

        const precoUnit = Number(item.precoUnitario) || 0;
        const subtotal = Number(item.total ?? (precoUnit * (item.quantidade || 0)));

        const precoUnitFmt = precoUnit.toFixed(2).replace('.', ',');
        const subtotalFmt = subtotal.toFixed(2).replace('.', ',');

        div.innerHTML = `
            <div class="item-image-name">
                <img src="${imgSrc}" alt="${nome}" class="item-image">
                <div class="item-details">
                    <span class="item-name">${item.nome}</span>
                </div>
            </div>

            <div class="item-actions">
                <span class="original-price">Preço unitário: R$ ${precoUnitFmt}</span>
                <span class="discount-price">Subtotal: R$ ${subtotalFmt}</span>

                <a href="#" class="action-link remove-item-btn" data-produto-id="${item.produtoId}">
                    Remover
                </a>

                <div class="quantity-control">
                    <span>Qtd: ${item.quantidade}</span>
                </div>
            </div>
        `;

        itemsContainer.appendChild(div);
    });

    itemsContainer.querySelectorAll('.remove-item-btn').forEach(btn => {
        btn.addEventListener('click', async (e) => {
            e.preventDefault();
            const produtoId = e.currentTarget.getAttribute('data-produto-id');
            await removerItem(produtoId);
            await carregarCarrinhoPagamento();
        });
    });
}

function validarCamposCartao() {
    const isCard = document.getElementById('credit-card')?.checked;

    // se for PIX, não valida campos de cartão
    if (!isCard) return true;

    limparErrosCamposCartao();

    const campos = [
        { id: 'name' },
        { id: 'card-number' },
        { id: 'expiry-date' },
        { id: 'cvv' }
    ];

    let primeiroInvalido = null;

    campos.forEach(c => {
        const el = document.getElementById(c.id);
        const valor = el?.value?.trim();

        if (!valor) {
            if (el) el.classList.add('input-error');
            if (!primeiroInvalido) primeiroInvalido = el;
        }
    });

    if (primeiroInvalido) {
        showToast('Preencha todos os campos do cartão: Nome, Número, Validade e CVV.');
        primeiroInvalido.focus();
        return false;
    }

    return true;
}

function limparErrosCamposCartao() {
    ['name', 'card-number', 'expiry-date', 'cvv'].forEach(id => {
        const el = document.getElementById(id);
        if (el) el.classList.remove('input-error');
    });
}

/**
 * ✅ NOVO FLUXO:
 * - valida campos
 * - não chama /finalizar aqui
 * - só redireciona pra tela de confirmação
 */
function onConcluirPagamento(event) {
    event.preventDefault();

    if (!validarCamposCartao()) return;

    const token = localStorage.getItem(TOKEN_KEY);
    if (!token) {
        showToast('Você precisa estar logado para concluir o pagamento.');
        window.location.href = '/login';
        return;
    }

    // marca que passou pela validação do payment
    sessionStorage.setItem(PAYMENT_OK_KEY, 'true');

    // redireciona pra confirmação (lá é que finaliza)
    window.location.href = '/confirmacaoPagamento';
}

/* =======================
   TOAST SIMPLES
======================= */
function showToast(message) {
    let container = document.getElementById('toast-container');

    if (!container) {
        container = document.createElement('div');
        container.id = 'toast-container';
        container.style.position = 'fixed';
        container.style.top = '20px';
        container.style.right = '20px';
        container.style.zIndex = '9999';
        container.style.display = 'flex';
        container.style.flexDirection = 'column';
        container.style.gap = '10px';
        document.body.appendChild(container);
    }

    const toast = document.createElement('div');
    toast.textContent = message;

    toast.style.background = '#ff4d4f';
    toast.style.color = '#fff';
    toast.style.padding = '12px 16px';
    toast.style.borderRadius = '8px';
    toast.style.fontSize = '14px';
    toast.style.fontWeight = '600';
    toast.style.boxShadow = '0 6px 18px rgba(0,0,0,0.2)';
    toast.style.opacity = '0';
    toast.style.transform = 'translateY(-8px)';
    toast.style.transition = 'all .25s ease';

    container.appendChild(toast);

    requestAnimationFrame(() => {
        toast.style.opacity = '1';
        toast.style.transform = 'translateY(0)';
    });

    setTimeout(() => {
        toast.style.opacity = '0';
        toast.style.transform = 'translateY(-8px)';
        setTimeout(() => toast.remove(), 250);
    }, 3000);
}

/* =======================
   IMAGENS
======================= */
function resolverImagem(item) {
    const nome = (item.nome || '').toLowerCase();

    if (nome.includes('apple')) return '/img/apple-gift-card.png';
    if (nome.includes('steam')) return '/img/steam-gift-card.png';
    if (nome.includes('playstation') || nome.includes('psn') || nome.includes('ps4') || nome.includes('ps5')) {
        return '/img/playstation-gift-card.png';
    }
    if (nome.includes('xbox')) return '/img/xbox.png';
    if (nome.includes('airbnb')) return '/img/airbnb.png';
    if (nome.includes('ifood')) return '/img/ifood.png';
    if (nome.includes('netflix')) return '/img/netflix.jpg';
    if (nome.includes('paramount')) return '/img/paramount.jpg';
    if (nome.includes('spotify')) return '/img/spotify.png';
    if (nome.includes('uber')) return '/img/uber.png';
    if (nome.includes('shopee')) return '/img/shopee.jpg';

    if (nome.includes('cyberpunk')) return '/img/cyberpunk.png';
    if (nome.includes('fc 26') || nome.includes('fc26') || nome.includes('ea fc')) return '/img/fc26.jpg';
    if (nome.includes('forza')) return '/img/forza-horizon-5-64md.1200.webp';
    if (nome.includes('ghost of tsushima') || nome.includes('tsushima')) return '/img/Ghost_of_Tsushima_capa.png';
    if (nome.includes('god of war')) return '/img/god-of-war.jpg';
    if (nome.includes('gta')) return '/img/gta.jpg';
    if (nome.includes('red dead')) return '/img/red-dead-2.png';
    if (nome.includes('the last of us')) return '/img/the-last-of-us.jpg';
    if (nome.includes('witcher')) return '/img/the-witcer-3';

    if (nome.includes('gift') || nome.includes('card')) return '/img/default-card.jpg';
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
