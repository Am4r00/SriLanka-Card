const PAYMENT_OK_KEY = window.PAYMENT_OK_KEY;

document.addEventListener('DOMContentLoaded', () => {
    carregarCarrinhoPagamento();

    const btn = document.getElementById('btn-finalizar-pagamento');
    if (btn) {
        btn.addEventListener('click', onConcluirPagamento);
    }
});

async function carregarCarrinhoPagamento() {
    const token = getToken();

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
        const imgSrc = resolvProductImage(item);

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

function onConcluirPagamento(event) {
    event.preventDefault();

    if (!validarCamposCartao()) return;

    const token = getToken();
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

async function removerItem(produtoId) {
    const token = getToken();
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
const cardNumberInput = document.getElementById('card-number');

cardNumberInput.addEventListener('input', function(event){
   let value = event.target.value;

   value = value.replace(/\D/g, '');
   value = value.slice(0,16);
   value = value.replace(/(\d{4})(?=\d)/g, '$1 ');
   event.target.value = value;


});

