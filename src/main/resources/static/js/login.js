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
      alert('Preencha email e senha.');
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
        
        // Salvar token no localStorage
        localStorage.setItem('token', data.token);
        localStorage.setItem('userEmail', email);
        
        // Mostrar informações do login
        console.log('Login realizado com sucesso:', data);
        alert(`Login realizado com sucesso!\nToken: ${data.token.substring(0, 50)}...`);
        
        // Redirecionar para home ou página admin se for admin
        if (data.user && data.user.funcoes && data.user.funcoes.includes('ADMIN')) {
          window.location.href = '/home_admin';
        } else {
          window.location.href = '/home';
        }
      } else {
        const errorData = await response.json();
        alert(`Erro no login: ${errorData.message || 'Credenciais inválidas'}`);
      }
    } catch (error) {
      console.error('Erro na requisição:', error);
      alert('Erro ao conectar com o servidor. Verifique sua conexão.');
    }
  });
}

/* Se quiser fechar o modal e voltar à home:
   document.querySelector('.overlay')?.remove();
   document.body.classList.remove('modal-open');
*/
