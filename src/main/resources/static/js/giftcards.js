// js/giftcards.js

const TOKEN_KEY = 'token';

let cardSelecionado = null;
let listaCards = []; // vamos guardar os cards aqui para poder filtrar/ordenar

document.addEventListener('DOMContentLoaded', () => {
    carregarGiftCards();

    const modal = document.getElementById('gift-modal');
    const closeBtn = document.getElementById('gift-modal-close');
    const backdrop = modal.querySelector('.gift-modal__backdrop');
    const addBtn = document.getElementById('gift-modal-add');

    closeBtn.addEventListener('click', fecharModal);
    backdrop.addEventListener('click', fecharModal);
    addBtn.addEventListener('click', adicionarSelecionadoAoCarrinho);

    // hooks para pesquisa / ordenação (se quiser usar depois)
    const searchInput = document.getElementById('search-input');
    const searchBtn = document.getElementById('search-btn');
    const sortSelect = document.getElementById('sort-select');

    if (searchBtn) {
        searchBtn.addEventListener('click', () => aplicarFiltros());
    }
    if (searchInput) {
        searchInput.addEventListener('keyup', (e) => {
            if (e.key === 'Enter') aplicarFiltros();
        });
    }
    if (sortSelect) {
        sortSelect.addEventListener('change', () => aplicarFiltros());
    }
});

/* ================================
 * 1) Buscar cards no backend
 * ================================ */
async function carregarGiftCards() {
    try {
        const resp = await fetch('/cards/listar', {
            method: 'GET',
            headers: {
                'Accept': 'application/json'
            }
        });

        if (!resp.ok) {
            console.error('Erro ao buscar cards:', resp.status);
            showToast('Erro ao carregar produtos.', true);
            return;
        }

        listaCards = await resp.json();
        aplicarFiltros(); // primeira renderização
    } catch (err) {
        console.error('Erro de rede ao buscar cards:', err);
        showToast('Falha de comunicação com o servidor.', true);
    }
}

function aplicarFiltros() {
    let cards = [...listaCards];

    const searchInput = document.getElementById('search-input');
    const sortSelect = document.getElementById('sort-select');

    // filtro por texto
    if (searchInput && searchInput.value.trim() !== '') {
        const termo = searchInput.value.trim().toLowerCase();
        cards = cards.filter(c => (c.nome || '').toLowerCase().includes(termo));
    }

    // ordenação
    if (sortSelect) {
        if (sortSelect.value === 'preco-asc') {
            cards.sort((a, b) => (a.valor || 0) - (b.valor || 0));
        } else if (sortSelect.value === 'preco-desc') {
            cards.sort((a, b) => (b.valor || 0) - (a.valor || 0));
        }
    }

    renderizarCards(cards);
}

/* ================================
 * 2) Renderizar cards na tela
 * ================================ */

// js/giftcards.js

function getImagemCard(card) {
    const nome = (card.nome || '').toLowerCase();

    // Gift Cards e Serviços
    if (nome.includes('psn') || nome.includes('playstation')) {
        return '/img/playstation-gift-card.png';
    }
    if (nome.includes('xbox')) {
        return '/img/xbox.png';
    }
    if (nome.includes('ifood')) {
        return '/img/ifood.png';
    }
    if (nome.includes('steam')) {
        return '/img/steam-gift-card.png';
    }
    if (nome.includes('apple') || nome.includes('itunes')) {
        return '/img/apple-gift-card.png';
    }
    if (nome.includes('netflix')) {
        return '/img/netflix.jpg';
    }
    if (nome.includes('spotify')) {
        return '/img/spotify.png';
    }
    if (nome.includes('airbnb')) {
        return '/img/airbnb.png';
    }
    if (nome.includes('uber')) {
        return '/img/uber.png';
    }
    if (nome.includes('shopee')) {
        return '/img/shopee.jpg';
    }
    if (nome.includes('paramount')) {
        return '/img/paramount.jpg';
    }
    // Jogos
    if (nome.includes('gta')) {
        return '/img/GTA_V1.jpg';
    }
    if (nome.includes('cyberpunk')) {
        return '/img/cyberpunk.png';
    }
    if (nome.includes('forza')) {
        return '/img/forza-horizon-5.webp';
    }
    if (nome.includes('god') || nome.includes('gow')) {
        return '/img/god_of_war.jpg';
    }
    if (nome.includes('the last of us') || nome.includes('tlou')) {
        return '/img/the_last_of_us.png';
    }
    if (nome.includes('witcher')) {
        return '/img/the_witcher_3.png';
    }
    if (nome.includes('red dead') || nome.includes('rdr')) {
        return '/img/red_dead_2.png';
    }
    if (nome.includes('ghost of tsushima') || nome.includes('ghost') || nome.includes('tsushima')) {
        return '/img/Ghost_of_Tsushima_capa.png';
    }
    if (nome.includes('fc') || nome.includes('fifa') || nome.includes('26')) {
        return '/img/fc26.jpg';
    }

    // Se não encontrar nenhuma correspondência
    return '/img/fallback.png'; // Garanta que essa imagem exista
}

function renderizarCards(cards) {
    const grid = document.getElementById('product-grid');
    if (!grid) return;

    grid.innerHTML = '';

    cards.forEach(card => {
        const cardEl = document.createElement('div');
        cardEl.classList.add('product-card');

        const estoqueDisponivel = card.quantidade && card.quantidade > 0;

        if (!estoqueDisponivel) {
            cardEl.classList.add('product-card--disabled');
        }

        const imgSrc = getImagemCard(card);

        cardEl.innerHTML = `
      <div class="product-card__image">
        <img src="${imgSrc}" alt="${card.nome}">
      </div>
      <div class="product-card__info">
        <h4>${card.nome}</h4>
        <p class="product-card__preco">R$ ${Number(card.valor).toFixed(2)}</p>
        ${
            estoqueDisponivel
                ? `<button type="button" class="product-card__btn">Ver detalhes</button>`
                : `<span class="tag-esgotado">Esgotado</span>`
        }
      </div>
    `;

        if (estoqueDisponivel) {
            cardEl.addEventListener('click', () => abrirModal(card));

            const btn = cardEl.querySelector('.product-card__btn');
            if (btn) {
                btn.addEventListener('click', (e) => {
                    e.stopPropagation();
                    abrirModal(card);
                });
            }
        }

        grid.appendChild(cardEl);
    });
}

/* ================================
 * 3) Modal de detalhes
 * ================================ */

function abrirModal(card) {
    cardSelecionado = card;

    document.getElementById('gift-modal-nome').textContent = card.nome;
    document.getElementById('gift-modal-descricao').textContent =
        card.observacoes || 'Este gift card não possui descrição cadastrada.';
    document.getElementById('gift-modal-preco').textContent =
        'R$ ' + Number(card.valor).toFixed(2);

    const qtdInput = document.getElementById('gift-modal-quantidade');
    qtdInput.value = 1;

    const imgEl = document.getElementById('gift-modal-img');
    imgEl.src = getImagemCard(card);

    const modal = document.getElementById('gift-modal');
    modal.classList.remove('hidden');
}

function fecharModal() {
    const modal = document.getElementById('gift-modal');
    modal.classList.add('hidden');
    cardSelecionado = null;
}

/* ===========================================
 * 4) Adicionar o card selecionado ao carrinho
 * =========================================== */

async function adicionarSelecionadoAoCarrinho() {
    if (!cardSelecionado) return;

    const token = localStorage.getItem(TOKEN_KEY);
    if (!token) {
        showToast('Você precisa estar logado para comprar.', true);
        window.location.href = '/login';
        return;
    }

    const qtdInput = document.getElementById('gift-modal-quantidade');
    let quantidade = parseInt(qtdInput.value, 10);

    if (isNaN(quantidade) || quantidade <= 0) {
        quantidade = 1;
    }

    try {
        const resp = await fetch('/api/carrinho', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + token
            },
            body: JSON.stringify({
                id: cardSelecionado.id,
                quantidade: quantidade
            })
        });

        if (!resp.ok) {
            const texto = await resp.text();
            console.error('Erro ao adicionar ao carrinho:', resp.status, texto);
            showToast('Não foi possível adicionar este item.', true);
            return;
        }

        showToast('Item adicionado ao carrinho!', false);
        fecharModal();
    } catch (err) {
        console.error('Erro de rede ao adicionar ao carrinho:', err);
        showToast('Erro de comunicação com o servidor.', true);
    }
}

/* ================================
 * 5) Toast (mensagens rápidas)
 * ================================ */

function showToast(msg, isError) {
    const toast = document.getElementById('toast');
    if (!toast) return;

    toast.textContent = msg;

    toast.classList.remove('hidden', 'toast--error', 'show');
    if (isError) {
        toast.classList.add('toast--error');
    }

    // reflow
    void toast.offsetWidth;

    toast.classList.add('show');

    setTimeout(() => {
        toast.classList.remove('show');
    }, 2500);
}
