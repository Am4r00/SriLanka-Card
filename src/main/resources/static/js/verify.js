const params = new URLSearchParams(window.location.search);
const email = (params.get('email') || '').trim();

const emailText = document.getElementById('emailText');
if (emailText) emailText.textContent = email || '(email não informado)';

const form = document.getElementById('verify-form');

if (form) {
    form.addEventListener('submit', async (e) => {
        e.preventDefault();

        const codeEl = document.getElementById('code');
        const code = codeEl?.value.trim();

        if (!code || !/^\d{6}$/.test(code)) {
            codeEl?.reportValidity?.();
            showToast('Digite um código válido de 6 dígitos.', true);
            return;
        }

        if (!email) {
            showToast('Email não encontrado. Volte e solicite o código novamente.', true);
            setTimeout(() => {
                window.location.href = '/forgot';
            }, 1500);
            return;
        }

        window.location.href = `/reset-password?email=${encodeURIComponent(email)}&code=${encodeURIComponent(code)}`;
    });
}

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
        showToast('Email não encontrado. Volte e solicite novamente.', true);
        setTimeout(() => {
            window.location.href = '/forgot';
        }, 1500);
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
            showToast(msg || 'Erro ao reenviar código.', true);
            return;
        }

        showToast(`Código reenviado para ${email}.`);
        startTimer(30);

    } catch (err) {
        console.error('Erro ao reenviar código:', err);
        showToast('Erro de rede ao reenviar código.', true);
    }
});

const style = document.createElement('style');
style.innerHTML = `.disabled{ pointer-events:none; opacity:.6 }`;
document.head.appendChild(style);
