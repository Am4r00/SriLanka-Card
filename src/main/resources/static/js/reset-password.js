// ================================
// RESET-PASSWORD.JS - Criar nova senha
// ================================

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

// Pegar email e código da querystring
const params = new URLSearchParams(window.location.search);
const email = (params.get('email') || '').trim();
const code = (params.get('code') || '').trim();

// Verificar se email e código foram fornecidos
if (!email || !code) {
    showToast('Email ou código não encontrado. Volte e solicite o código novamente.', true);
    setTimeout(() => {
        window.location.href = '/forgot';
    }, 2000);
}

// Elementos do formulário
const form = document.getElementById('reset-password-form');
const newPasswordInput = document.getElementById('newPassword');
const confirmPasswordInput = document.getElementById('confirmPassword');
const matchErr = document.getElementById('matchErr');

// Função para verificar se as senhas coincidem
function checkPasswordMatch() {
    const newPassword = newPasswordInput?.value || '';
    const confirmPassword = confirmPasswordInput?.value || '';
    
    if (matchErr) {
        if (confirmPassword && newPassword !== confirmPassword) {
            matchErr.style.display = 'block';
            return false;
        } else {
            matchErr.style.display = 'none';
            return true;
        }
    }
    return true;
}

// Adicionar listeners para verificar senhas em tempo real
if (newPasswordInput && confirmPasswordInput) {
    newPasswordInput.addEventListener('input', checkPasswordMatch);
    confirmPasswordInput.addEventListener('input', checkPasswordMatch);
}

// Mostrar/ocultar senha (se houver botões de toggle)
const toggles = document.querySelectorAll('.toggle[data-target]');
toggles.forEach(toggle => {
    toggle.addEventListener('click', () => {
        const targetId = toggle.getAttribute('data-target');
        const targetInput = document.getElementById(targetId);
        if (targetInput) {
            const isHidden = targetInput.type === 'password';
            targetInput.type = isHidden ? 'text' : 'password';
            toggle.setAttribute('aria-label', isHidden ? 'Ocultar senha' : 'Mostrar senha');
        }
    });
});

// Submit do formulário
if (form) {
    form.addEventListener('submit', async (e) => {
        e.preventDefault();

        const newPassword = newPasswordInput?.value.trim() || '';
        const confirmPassword = confirmPasswordInput?.value.trim() || '';

        // Validações
        if (!newPassword || newPassword.length < 6) {
            showToast('A senha deve ter no mínimo 6 caracteres.', true);
            return;
        }

        if (!checkPasswordMatch()) {
            showToast('As senhas não coincidem.', true);
            return;
        }

        if (!email || !code) {
            showToast('Email ou código não encontrado. Volte e solicite novamente.', true);
            setTimeout(() => {
                window.location.href = '/forgot';
            }, 2000);
            return;
        }

        try {
            const resp = await fetch('/auth/reset-password', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    email,
                    code,
                    newPassword
                })
            });

            if (!resp.ok) {
                const msg = await resp.text();
                showToast(msg || 'Código inválido ou expirado. Solicite outro.', true);
                return;
            }

            showToast('Senha alterada com sucesso! Redirecionando para login...');
            setTimeout(() => {
                window.location.href = '/login';
            }, 1500);

        } catch (err) {
            console.error('Erro ao resetar senha:', err);
            showToast('Erro de rede ao resetar senha. Tente novamente.', true);
        }
    });
} else {
    console.error('Formulário reset-password-form não encontrado!');
}

