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
  form.addEventListener('submit', (e) => {
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
      e.preventDefault();
      primeiroInvalido.focus();
      showToast('Preencha todos os campos obrigatórios.', true);
      return;
    }

    // Senhas diferentes
    if (!checkMatch()) {
      e.preventDefault();
      showToast('As senhas não coincidem.', true);
      return;
    }

    // Termos não aceitos
    if (!termsInput || !termsInput.checked) {
      e.preventDefault();
      showToast('Você precisa aceitar os termos e condições.', true);
      return;
    }

    // Se chegou aqui, NÃO chamamos e.preventDefault()
    // O formulário vai ser enviado normalmente para /users/create-user
    // e o back-end (UserController.createUser) cuida do cadastro.
  });
} else {
  console.error('Formulário de signup não encontrado!');
}
