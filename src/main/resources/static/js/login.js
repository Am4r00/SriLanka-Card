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

// Submit “fake” para demo
const form = document.getElementById('login-form');
if (form) {
  form.addEventListener('submit', (e) => {
    e.preventDefault();
    const email = document.getElementById('email').value.trim();
    const pass  = document.getElementById('password').value.trim();
    if (!email || !pass) {
      alert('Preencha email e senha.');
      return;
    }
    // Aqui você chama sua API real, ex.:
    // fetch('/api/auth/login', { method:'POST', headers:{'Content-Type':'application/json'}, body: JSON.stringify({ email, password: pass }) })
    //   .then(r => r.json()).then(console.log).catch(console.error);

    alert('Login enviado! (plugue sua API aqui)');
  });
}

/* Se quiser fechar o modal e voltar à home:
   document.querySelector('.overlay')?.remove();
   document.body.classList.remove('modal-open');
*/
