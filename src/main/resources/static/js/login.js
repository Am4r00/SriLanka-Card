// Mostrar/ocultar senha
const toggle = document.querySelector('.toggle');
const pwd = document.getElementById('password');
const eye = document.getElementById('eye');

if (toggle && pwd && eye) {
  toggle.addEventListener('click', () => {
    const isHidden = pwd.type === 'password';
    pwd.type = isHidden ? 'text' : 'password';
    toggle.setAttribute('aria-label', isHidden ? 'Ocultar senha' : 'Mostrar senha');
    eye.innerHTML = isHidden
      ? '<path d="M17.94 17.94A10.94 10.94 0 0 1 12 20c-7 0-11-8-11-8a21.8 21.8 0 0 1 5.06-6.88M9.9 4.24A10.94 10.94 0 0 1 12 4c7 0 11 8 11 8a21.7 21.7 0 0 1-4.87 6.82M1 1l22 22" />'
      : '<path d="M1 12s4-7 11-7 11 7 11 7-4 7-11 7S1 12 1 12Z"/><circle cx="12" cy="12" r="3"/>';
  });
}

// Submit com API real
const form = document.getElementById('login-form');
if (form) {
  form.addEventListener('submit', async (e) => {
    e.preventDefault();
    const email = document.getElementById('email').value.trim();
    const pass  = document.getElementById('password').value.trim();
    
    if (!email || !pass) {
      showToast('Preencha email e senha.', true);
      return;
    }

    try {
      const response = await fetch('/auth/login', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({ 
          email: email, 
          password: pass 
        })
      });

      if (response.ok) {



        const data = await response.json();

        localStorage.setItem('token', data.token);
        localStorage.setItem('userEmail', email);

        document.cookie = `jwt_token=${data.token}; path=/; max-age=${60 * 60 * 10}; SameSite=Lax`;

        let isAdmin = false;
        
        if (data.funcoes) {

          if (Array.isArray(data.funcoes)) {
            console.log('Funções é um array:', data.funcoes);

            data.funcoes.forEach((f, index) => {
              console.log(`Função[${index}]:`, f, 'Tipo:', typeof f);
            });
            
            isAdmin = data.funcoes.some(f => {

              const funcaoValue = typeof f === 'string' ? f : (f.name || f.toString() || f);
              console.log('Comparando função:', funcaoValue, 'com ADMIN');
              const match = funcaoValue === 'ADMIN' || funcaoValue === 'ROLE_ADMIN' || funcaoValue.includes('ADMIN');
              console.log('Match:', match);
              return match;
            });
            console.log('Verificação array - isAdmin:', isAdmin);
          } 

          else if (typeof data.funcoes === 'string') {
            console.log('Funções é uma string:', data.funcoes);
            isAdmin = data.funcoes.includes('ADMIN') || data.funcoes === 'ADMIN';
            console.log('Verificação string - isAdmin:', isAdmin);
          }

          else if (typeof data.funcoes === 'object' && data.funcoes !== null) {
            const funcoesArray = Object.values(data.funcoes);
            isAdmin = funcoesArray.some(f => {
              const funcaoValue = typeof f === 'string' ? f : (f.name || f.toString() || f);
              return funcaoValue === 'ADMIN' || funcaoValue.includes('ADMIN');
            });
            console.log('Verificação objeto - isAdmin:', isAdmin);
          }
        } else {
          console.warn('⚠️ Funções não encontradas na resposta!');
        }

        if (isAdmin) {
          console.log('Redirecionando para /home_admin');
          showToast('Login realizado com sucesso! Redirecionando...');
          setTimeout(() => {
            window.location.href = '/home_admin';
          }, 1000);
        } else {
          console.log('Redirecionando para /home');
          showToast('Login realizado com sucesso!');
          setTimeout(() => {
            window.location.href = '/home';
          }, 1000);
        }
      } else {
          const errorData = await response.json();
          if (response.status === 403 && (errorData.message === 'USER_INACTIVE' || errorData.errorCode === 'USER_INACTIVE')) {
              abrirModalReativacao(email);
              return;
          }
          showToast(`Erro no login: ${errorData.message || 'Credenciais inválidas'}`, true);

      }
    } catch (error) {
      console.error('Erro na requisição:', error);
      showToast('Erro ao conectar com o servidor. Verifique sua conexão.', true);
    }
  });
}

function abrirModalReativacao(email) {
    const modal = document.getElementById('modal-reativar');
    const emailInput = document.getElementById('reativar-email');
    if (!modal || !emailInput) return;
    emailInput.value = email || '';
    modal.style.display = 'grid'; // ou 'block', conforme seu CSS .overlay
}

function fecharReativar(e) {
    if (e) e.preventDefault();
    const modal = document.getElementById('modal-reativar');
    if (modal) modal.style.display = 'none';
}

async function enviarCodigoAtivacao() {
    const email = document.getElementById('reativar-email').value.trim();
    if (!email) { showToast('Informe o email.', true); return; }
    try {
        await fetch('/users/send-activation-code', {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({ email })
        });
        showToast('Código enviado para seu email.', false);
    } catch (err) {
        showToast('Erro ao enviar código.', true);
    }
}

async function reenviarAtivacao(e) {
    e.preventDefault();
    const email = document.getElementById('reativar-email').value.trim();
    const code  = document.getElementById('reativar-code').value.trim();
    if (!email || !code) { showToast('Preencha email e código.', true); return; }
    try {
        await fetch('/users/activate', {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({ email, code })
        });
        showToast('Conta ativada! Agora faça login.', false);
        fecharReativar();
    } catch (err) {
        showToast('Erro ao ativar conta.', true);
    }
}
