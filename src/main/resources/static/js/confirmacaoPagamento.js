const PAYMENT_OK_KEY = window.PAYMENT_OK_KEY;

document.addEventListener('DOMContentLoaded', () => {
    finalizarPedidoAoAbrir();
});

async function finalizarPedidoAoAbrir() {
    const token = getToken();

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