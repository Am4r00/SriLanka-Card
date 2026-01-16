document.addEventListener('DOMContentLoaded', () => {
    finalizarPedidoAoAbrir();
});

async function finalizarPedidoAoAbrir() {
    const token = getToken();

    if (!token) {
        showToast('Você precisa estar logado.', true);
        window.location.href = '/login';
        return;
    }

    const ok = sessionStorage.getItem(PAYMENT_OK_KEY);
    if (!ok) {
        console.warn('Confirmação acessada sem marcação de pagamento válido.');
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
            return;
        }

        let msg = 'Erro ao finalizar a compra. Tente novamente.';
        if (resp.status === 400) {
            try {
                const text = await resp.text();
                if (text) msg = text;
            } catch (e) {
                console.error('Erro ao ler mensagem de erro da finalização:', e);
            }
        }

        console.error('Erro ao finalizar compra. Status:', resp.status);
        showToast(msg, true);

    } catch (e) {
        console.error('Erro de rede ao finalizar pedido:', e);
        showToast('Erro de comunicação com o servidor.', true);
    }
}
