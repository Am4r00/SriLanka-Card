const form = document.getElementById('signup');
const pwd = document.getElementById('password');
const confirm = document.getElementById('confirm');

// Função para verificar se as senhas coincidem
function checkMatch() {
  const passwordValue = pwd ? pwd.value : '';
  const confirmValue = confirm ? confirm.value : '';
  const matchErr = document.getElementById('matchErr');

  if (matchErr) {
    if (passwordValue && confirmValue && passwordValue !== confirmValue) {
      matchErr.style.display = 'block';
      return false;
    } else {
      matchErr.style.display = 'none';
      return true;
    }
  }
  return true;
}

// Verificar senhas em tempo real
if (pwd && confirm) {
  pwd.addEventListener('input', checkMatch);
  confirm.addEventListener('input', checkMatch);
}

// Mostrar/ocultar senha
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

// Validação do formulário antes de enviar para /users/create-user
if (form) {
  form.addEventListener('submit', async (e) => {
    e.preventDefault();
    const nameInput = document.getElementById('name');
    const emailInput = document.getElementById('email');
    const termsInput = document.getElementById('terms');

    const campos = [nameInput, emailInput, pwd, termsInput];
    let primeiroInvalido = null;

    campos.forEach(el => {
      if (!el) return;

      const valor = el.type === 'checkbox'
        ? el.checked
        : (el.value || '').trim();

      if (!valor) {
        el.classList.add('input-error');
        if (!primeiroInvalido) primeiroInvalido = el;
      } else {
        el.classList.remove('input-error');
      }
    });

    // Se algum campo obrigatório está vazio, bloqueia envio
    if (primeiroInvalido) {
      primeiroInvalido.focus();
      showToast('Preencha todos os campos obrigatórios.', true);
      return;
    }

    // Senhas diferentes
    if (!checkMatch()) {
      showToast('As senhas não coincidem.', true);
      return;
    }

    // Termos não aceitos
    if (!termsInput || !termsInput.checked) {
      showToast('Você precisa aceitar os termos e condições.', true);
      return;
    }

    try {
      const resp = await fetch('/users/create-user', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          name: nameInput.value.trim(),
          email: emailInput.value.trim(),
          password: pwd.value.trim()
        })
      });

      if (!resp.ok) {
        const data = await resp.json().catch(() => ({}));
        const raw = data.message || '';
        let msg = 'Erro ao criar conta. Verifique os dados.';
        if (resp.status === 409) {
          msg = raw || 'Email já está em uso.';
        } else if (resp.status === 400) {
          const lower = raw.toLowerCase();
          if (lower.includes('email') || lower.includes('e-mail')) {
            msg = 'Email inválido. Informe um endereço de email válido.';
          } else if (lower.includes('senha') || lower.includes('password')) {
            msg = 'Senha inválida. Verifique os requisitos.';
          } else if (lower.includes('nome') || lower.includes('name')) {
            msg = 'Nome inválido. Preencha corretamente.';
          } else {
            msg = raw || msg;
          }
        } else {
          msg = raw || msg;
        }
        showToast(msg, true);
        return;
      }

      showToast('Conta criada com sucesso! Redirecionando...', false);
      setTimeout(() => window.location.href = '/login', 800);
    } catch (err) {
      console.error(err);
      showToast('Erro ao comunicar com o servidor. Tente novamente.', true);
    }
  });
} else {
  console.error('Formulário de signup não encontrado!');
}
