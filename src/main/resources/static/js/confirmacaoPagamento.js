const TOKEN_KEY = 'token';
const PAYMENT_OK_KEY = 'payment_validated';

document.addEventListener('DOMContentLoaded', () => {
    finalizarPedidoAoAbrir();
});

async function finalizarPedidoAoAbrir() {
    const token = localStorage.getItem(TOKEN_KEY);

    if (!token) {
        showToast('Você precisa estar logado.');
        window.location.href = '/login';
        return;
    }

    // evita acesso direto sem passar pelo payment
    const ok = sessionStorage.getItem(PAYMENT_OK_KEY);
    if (!ok) {
        showToast('Confirmação inválida. Volte ao pagamento.');
        window.location.href = '/payment';
        return;
    }

    try {
        const resp = await fetch('/api/pedidos/finalizar', {
            method: 'POST',
            headers: {
                'Authorization': 'Bearer ' + token,
                'Accept': 'application/json'
            }
        });

        if (resp.ok) {
            sessionStorage.removeItem(PAYMENT_OK_KEY);
            return; // fica na página
        }

        if (resp.status === 400) {
            const msg = await resp.text();
            showToast(msg || 'Não foi possível finalizar a compra.');
            window.location.href = '/payment';
            return;
        }

        console.error('Erro ao finalizar compra. Status:', resp.status);
        showToast('Erro ao finalizar a compra. Tente novamente.');
        window.location.href = '/payment';

    } catch (e) {
        console.error('Erro de rede ao finalizar pedido:', e);
        showToast('Erro de comunicação com o servidor.');
        window.location.href = '/payment';
    }
}

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
