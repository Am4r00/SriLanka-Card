// Função para mostrar toast (notificação)
function showToast(message, isError = false) {
    // Remover toast anterior se existir
    const existingToast = document.getElementById('toast-notification');
    if (existingToast) {
        existingToast.remove();
    }

    // Criar elemento de toast
    const toast = document.createElement('div');
    toast.id = 'toast-notification';
    toast.textContent = message;
    toast.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        background: ${isError ? '#ef4444' : '#10b981'};
        color: white;
        padding: 16px 24px;
        border-radius: 8px;
        box-shadow: 0 4px 6px rgba(0,0,0,0.2);
        z-index: 10000;
        font-weight: 500;
        max-width: 400px;
        animation: slideInRight 0.3s ease;
    `;

    // Adicionar animação CSS se não existir
    if (!document.getElementById('toast-styles')) {
        const style = document.createElement('style');
        style.id = 'toast-styles';
        style.textContent = `
            @keyframes slideInRight {
                from {
                    transform: translateX(100%);
                    opacity: 0;
                }
                to {
                    transform: translateX(0);
                    opacity: 1;
                }
            }
            @keyframes slideOutRight {
                from {
                    transform: translateX(0);
                    opacity: 1;
                }
                to {
                    transform: translateX(100%);
                    opacity: 0;
                }
            }
        `;
        document.head.appendChild(style);
    }

    document.body.appendChild(toast);

    // Remover após 3 segundos
    setTimeout(() => {
        toast.style.animation = 'slideOutRight 0.3s ease';
        setTimeout(() => toast.remove(), 300);
    }, 3000);
}

const form = document.getElementById('forgot-form');

if (form) {
    form.addEventListener('submit', async (e) => {
        e.preventDefault();

        const emailInput = document.getElementById('email');
        const email = emailInput?.value.trim();

        if (!email) {
            form.reportValidity?.();
            return;
        }

        try {
            const resp = await fetch('/auth/forgot-password', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ email })
            });

            if (!resp.ok) {
                const msg = await resp.text();
                showToast(msg || 'Erro ao enviar código. Verifique o email e tente novamente.', true);
                return;
            }

            showToast('Código enviado! Verifique seu e-mail.');
            setTimeout(() => {
                window.location.href = `verify?email=${encodeURIComponent(email)}`;
            }, 1500);

        } catch (err) {
            console.error('Erro ao enviar código:', err);
            showToast('Erro de rede ao enviar código. Tente novamente.', true);
        }
    });
} else {
    console.error('Form forgot-form não encontrado!');
}
