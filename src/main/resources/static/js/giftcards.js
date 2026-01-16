let cardSelecionado = null;
let listaCards = [];
let categoriaAtiva = 'todos';
let quantidadeNoCarrinhoPorCardId = {};

document.addEventListener('DOMContentLoaded', () => {
    carregarGiftCards();

    const modal = document.getElementById('gift-modal');
    const closeBtn = document.getElementById('gift-modal-close');
    const addBtn = document.getElementById('gift-modal-add');
    const backdrop = modal ? modal.querySelector('.gift-modal__backdrop') : null;

    if (closeBtn) {
        closeBtn.addEventListener('click', fecharModal);
    }
    if (backdrop) {
        backdrop.addEventListener('click', fecharModal);
    }
    if (addBtn) {
        addBtn.addEventListener('click', adicionarSelecionadoAoCarrinho);
    }

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

    const categoryLinks = document.querySelectorAll('.category-link');
    categoryLinks.forEach(link => {
        link.addEventListener('click', (e) => {
            e.preventDefault();
            const categoria = link.getAttribute('data-categoria');
            selecionarCategoria(categoria);
        });
    });
});

async function carregarGiftCards() {
    try {
        const resp = await fetch('/cards', {
            method: 'GET',
            headers: {
                'Accept': 'application/json'
            }
        });

        console.log('Resposta /cards -> status:', resp.status);

        if (resp.status === 204) {
            listaCards = [];
            aplicarFiltros();
            return;
        }

        if (!resp.ok) {
            console.error('Erro ao buscar cards:', resp.status);
            showToast('Erro ao carregar produtos.', true);
            return;
        }

        listaCards = await resp.json();
        console.log('Cards recebidos:', listaCards);
        quantidadeNoCarrinhoPorCardId = {};
        try {
            const token = typeof getToken === 'function' ? getToken() : null;
            if (token && window.api && typeof window.api.getCart === 'function') {
                const carrinho = await window.api.getCart();
                if (carrinho && carrinho.itens) {
                    carrinho.itens.forEach(item => {
                        const id = item.produtoId;
                        const q = item.quantidade || 0;
                        quantidadeNoCarrinhoPorCardId[id] =
                            (quantidadeNoCarrinhoPorCardId[id] || 0) + q;
                    });
                }
            }
        } catch (e) {
            console.error('Erro ao carregar carrinho para ajuste de estoque:', e);
        }
        aplicarFiltros();
    } catch (err) {
        console.error('Erro de rede ao buscar cards:', err);
        showToast('Falha de comunicação com o servidor.', true);
    }
}

function getCategoria(card) {
    if (!card || card.category == null) {
        return null;
    }

    const rawLower = String(card.category).toLowerCase();

    if (['jogos', 'comida', 'musica', 'servicos'].includes(rawLower)) {
        return rawLower;
    }

    const rawUpper = rawLower.toUpperCase();

    const CATEGORY_MAP = {
        JOGOS: 'jogos',
        COMIDA: 'comida',
        MUSICA: 'musica',
        SERVICOS: 'servicos'
    };

    return CATEGORY_MAP[rawUpper] || rawLower;
}

function selecionarCategoria(categoria) {
    categoriaAtiva = categoria;

    const categoryLinks = document.querySelectorAll('.category-link');
    categoryLinks.forEach(link => {
        if (link.getAttribute('data-categoria') === categoria) {
            link.classList.add('active');
        } else {
            link.classList.remove('active');
        }
    });

    aplicarFiltros();
}

function aplicarFiltros() {
    let cards = [...listaCards];

    if (categoriaAtiva !== 'todos') {
        cards = cards.filter(c => getCategoria(c) === categoriaAtiva);
    }

    const searchInput = document.getElementById('search-input');
    const sortSelect = document.getElementById('sort-select');

    if (searchInput && searchInput.value.trim() !== '') {
        const termo = searchInput.value.trim().toLowerCase();
        cards = cards.filter(c => (c.nome || '').toLowerCase().includes(termo));
    }

    if (sortSelect) {
        if (sortSelect.value === 'preco-asc') {
            cards.sort((a, b) => (a.valor || 0) - (b.valor || 0));
        } else if (sortSelect.value === 'preco-desc') {
            cards.sort((a, b) => (b.valor || 0) - (a.valor || 0));
        }
    }

    renderizarCards(cards);
}

function getImagemCard(card) {
   return resolveProductImage(card);
}

function renderizarCards(cards) {
    const grid = document.getElementById('product-grid');
    if (!grid) return;

    grid.innerHTML = '';

    cards.forEach(card => {
        const cardEl = document.createElement('div');
        cardEl.classList.add('product-card');

        const quantidadeEmCarrinho = quantidadeNoCarrinhoPorCardId[card.id] || 0;
        const restanteParaUsuario = (card.quantidade || 0) - quantidadeEmCarrinho;
        const estoqueDisponivel = restanteParaUsuario > 0;

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

async function adicionarSelecionadoAoCarrinho() {
    if (!cardSelecionado) return;

    const token = getToken();
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
    
        quantidadeNoCarrinhoPorCardId[cardSelecionado.id] =
        (quantidadeNoCarrinhoPorCardId[cardSelecionado.id] || 0) + quantidade;
        aplicarFiltros();

        showToast('Item adicionado ao carrinho!', false);
        fecharModal();

        if (window.updateCartCount && typeof window.updateCartCount === 'function') {
            await window.updateCartCount();
        }
    } catch (err) {
        console.error('Erro de rede ao adicionar ao carrinho:', err);
        showToast('Erro de comunicação com o servidor.', true);
    }
}