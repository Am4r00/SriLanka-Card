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
                alert(msg || 'Erro ao enviar código. Verifique o email e tente novamente.');
                return;
            }

            alert('Código enviado! Verifique seu e-mail.');
            window.location.href = `verify?email=${encodeURIComponent(email)}`;

        } catch (err) {
            console.error('Erro ao enviar código:', err);
            alert('Erro de rede ao enviar código. Tente novamente.');
        }
    });
} else {
    console.error('Form forgot-form não encontrado!');
}
