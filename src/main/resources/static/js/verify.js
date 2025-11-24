// ================================
// VERIFY.JS - integrado ao backend
// ================================

// pega email da querystring (?email=...)
const params = new URLSearchParams(window.location.search);
const email = (params.get('email') || '').trim();

const emailText = document.getElementById('emailText');
if (emailText) emailText.textContent = email || '(email não informado)';

// form principal
const form = document.getElementById('verify-form');

if (form) {
    form.addEventListener('submit', async (e) => {
        e.preventDefault();

        const codeEl = document.getElementById('code');
        const code = codeEl?.value.trim();

        // valida 6 dígitos
        if (!code || !/^\d{6}$/.test(code)) {
            codeEl?.reportValidity?.();
            alert('Digite um código válido de 6 dígitos.');
            return;
        }

        if (!email) {
            alert('Email não encontrado. Volte e solicite o código novamente.');
            window.location.href = '/forgot';
            return;
        }

        // ✅ pede nova senha sem precisar mudar o HTML
        const newPassword = prompt('Digite sua nova senha (mínimo 6 caracteres):');
        if (!newPassword || newPassword.trim().length < 6) {
            alert('Senha inválida. Ela precisa ter no mínimo 6 caracteres.');
            return;
        }

        const confirmPassword = prompt('Confirme sua nova senha:');
        if (confirmPassword !== newPassword) {
            alert('As senhas não coincidem.');
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
                alert(msg || 'Código inválido ou expirado. Solicite outro.');
                return;
            }

            alert('Senha alterada com sucesso! Faça login novamente.');
            window.location.href = '/login';

        } catch (err) {
            console.error('Erro ao resetar senha:', err);
            alert('Erro de rede ao resetar senha. Tente novamente.');
        }
    });
}

// ================================
// Reenvio de código com timer
// ================================
const resendLink = document.getElementById('resendLink');
const timer = document.getElementById('timer');

let canResend = true;
let countdown;

function startTimer(seconds) {
    let s = seconds;
    canResend = false;
    if (timer) timer.textContent = `(aguarde ${s}s)`;
    resendLink?.classList.add('disabled');

    countdown = setInterval(() => {
        s -= 1;
        if (timer) timer.textContent = `(aguarde ${s}s)`;

        if (s <= 0) {
            clearInterval(countdown);
            if (timer) timer.textContent = '';
            resendLink?.classList.remove('disabled');
            canResend = true;
        }
    }, 1000);
}

resendLink?.addEventListener('click', async (e) => {
    e.preventDefault();
    if (!canResend) return;

    if (!email) {
        alert('Email não encontrado. Volte e solicite novamente.');
        window.location.href = '/forgot';
        return;
    }

    try {
        const resp = await fetch('/auth/forgot-password', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email })
        });

        if (!resp.ok) {
            const msg = await resp.text();
            alert(msg || 'Erro ao reenviar código.');
            return;
        }

        alert(`Reenviamos o código para ${email}.`);
        startTimer(30);

    } catch (err) {
        console.error('Erro ao reenviar código:', err);
        alert('Erro de rede ao reenviar código.');
    }
});

// estilo simples pro disabled
const style = document.createElement('style');
style.innerHTML = `.disabled{ pointer-events:none; opacity:.6 }`;
document.head.appendChild(style);
