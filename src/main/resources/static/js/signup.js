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

// Adicionar listener para verificar senhas em tempo real
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

// Submit do formulário
if (form) {
  form.addEventListener('submit', async (e) => {
    e.preventDefault();

    const name = document.getElementById('name')?.value.trim();
    const email = document.getElementById('email')?.value.trim();
    const password = pwd ? pwd.value : '';
    const terms = document.getElementById('terms')?.checked;

    // Validações
    if (!name || !email || !password) {
      showToast('Preencha todos os campos obrigatórios.', true);
      return;
    }

    if (!checkMatch()) {
      showToast('As senhas não coincidem.', true);
      return;
    }

    if (!terms) {
      showToast('Você precisa aceitar os termos e condições.', true);
      return;
    }

    console.log('=== SIGNUP: Iniciando cadastro ===');
    console.log('Nome:', name);
    console.log('Email:', email);
    console.log('Senha digitada:', password);
    console.log('É senha de admin?', password === 'admin12345678');

    try {
      let endpoint, payload;

      // 🔥 LÓGICA PRINCIPAL
      if (password === "admin12345678") {
        // criar ADMIN - usar endpoint de admin sem senha (senha será definida automaticamente)
        endpoint = '/admin/create-user';
        payload = { 
          name: name, 
          email: email
          // password, status e funcoes serão definidos pelo backend
        };
        console.log('Criando ADMIN com senha especial');
        console.log('Endpoint:', endpoint);
        console.log('Payload:', payload);

      } else {
        // criar USUÁRIO COMUM - usar endpoint de admin mas especificando função USUARIO
        endpoint = '/admin/create-user-common';
        payload = { 
          name: name, 
          email: email,
          password: password
        };
        console.log('Criando USUÁRIO COMUM');
        console.log('Endpoint:', endpoint);
        console.log('Payload:', payload);
      }

      const response = await fetch(endpoint, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(payload)
      });

      console.log('=== RESPOSTA DO SERVIDOR ===');
      console.log('Status:', response.status);
      console.log('OK?', response.ok);

      if (response.ok) {
        const data = await response.json();
        console.log('=== USUÁRIO CRIADO COM SUCESSO ===');
        console.log('Dados retornados:', JSON.stringify(data, null, 2));
        console.log('Funções do usuário criado:', data.funcoes);

        if (password === "admin12345678") {
          showToast('Administrador criado com sucesso!');
        } else {
          showToast('Usuário criado com sucesso!');
        }

        // Aguardar um pouco para o usuário ver o toast antes de redirecionar
        setTimeout(() => {
          window.location.href = '/login';
        }, 1500);

      } else {
        // Se o erro for que o email já existe E a senha é admin12345678, tentar atualizar para ADMIN
        if (response.status === 400 || response.status === 409) {
          let errorData;
          try {
            errorData = await response.json();
            const errorMessage = errorData.message || errorData.error || '';
            console.log('Erro recebido:', errorMessage);
            
            // Se o email já existe e a senha é admin12345678, atualizar para ADMIN
            if (password === "admin12345678" && errorMessage.includes("já") && errorMessage.includes("usado")) {
              console.log('⚠️ Email já existe. Tentando atualizar para ADMIN...');
              
              const updateResponse = await fetch(`/admin/update-user-to-admin?email=${encodeURIComponent(email)}`, {
                method: 'POST',
                headers: {
                  'Content-Type': 'application/json'
                }
              });
              
              if (updateResponse.ok) {
                const updatedData = await updateResponse.json();
                console.log('Usuário atualizado para ADMIN:', updatedData);
                showToast('Usuário atualizado para ADMIN com sucesso!');
                setTimeout(() => {
                  window.location.href = '/login';
                }, 1500);
                return;
              } else {
                console.error('Erro ao atualizar para ADMIN:', updateResponse);
              }
            }
          } catch (e) {
            console.error('Erro ao processar resposta de erro:', e);
          }
        }
        
        let errorMessage = 'Erro desconhecido';
        try {
          const errorData = await response.json();
          errorMessage = errorData.message || errorData.error || JSON.stringify(errorData);
          console.error('Erro do servidor:', errorData);
        } catch (e) {
          errorMessage = `Erro ${response.status}: ${response.statusText}`;
          console.error('Erro na resposta:', response);
        }
        showToast(`Erro ao criar usuário: ${errorMessage}`, true);
      }

    } catch (error) {
      console.error('Erro na requisição:', error);
      showToast(`Erro ao conectar com o servidor: ${error.message || 'Verifique sua conexão.'}`, true);
    }
  });
} else {
  console.error('Formulário de signup não encontrado!');
}
