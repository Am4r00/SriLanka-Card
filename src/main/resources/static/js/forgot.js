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
