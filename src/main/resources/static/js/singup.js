// Mostrar/Ocultar senhas (ambos campos)
document.querySelectorAll('.toggle').forEach(btn=>{
  btn.addEventListener('click', ()=>{
    const id = btn.getAttribute('data-target');
    const input = document.getElementById(id);
    if (!input) return;
    const hide = input.type === 'password';
    input.type = hide ? 'text' : 'password';
    btn.setAttribute('aria-label', hide ? 'Ocultar senha' : 'Mostrar senha');
  });
});

// Validação: confirmar senha
const pwd = document.getElementById('password');
const confirmPwd = document.getElementById('confirm');
const err = document.getElementById('matchErr');

function checkMatch(){
  if(!pwd || !confirmPwd || !err) return true;
  if(confirmPwd.value.length === 0){ err.style.display='none'; return true; }
  const ok = pwd.value === confirmPwd.value;
  err.style.display = ok ? 'none' : 'block';
  return ok;
}
if (pwd && confirmPwd){
  pwd.addEventListener('input', checkMatch);
  confirmPwd.addEventListener('input', checkMatch);
}

// Submit com API real
const form = document.getElementById('signup');
if (form){
  form.addEventListener('submit', async (e)=>{
    e.preventDefault();
    const allValid = form.checkValidity() && checkMatch() && document.getElementById('terms').checked;

    if(!allValid){
      form.reportValidity?.();
      checkMatch();
      return;
    }

    const name = document.getElementById('name').value.trim();
    const email = document.getElementById('email').value.trim();
    const password = pwd.value;
    const tipo = document.getElementById('tipo').value;

    try {
      let endpoint, payload;

      if (tipo === 'ADMIN') {
        // Para admin, usar endpoint específico
        endpoint = '/admin/create-user';
        payload = { name, email };
      } else {
        // Para usuário comum
        endpoint = '/users/create-user';
        payload = { name, email, password };
      }

      const response = await fetch(endpoint, {
        method: 'POST',
        headers: { 
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(payload)
      });

      if (response.ok) {
        const data = await response.json();
        console.log('Usuário criado com sucesso:', data);
        
        if (tipo === 'ADMIN') {
          alert(`Admin criado com sucesso!\nEmail: ${email}\nSenha padrão: SenhaFixa123`);
        } else {
          alert(`Usuário criado com sucesso!\nEmail: ${email}`);
        }
        
        // Redirecionar para login
        window.location.href = '/login';
      } else {
        const errorData = await response.json();
        alert(`Erro ao criar usuário: ${errorData.message || 'Erro desconhecido'}`);
      }
    } catch (error) {
      console.error('Erro na requisição:', error);
      alert('Erro ao conectar com o servidor. Verifique sua conexão.');
    }
  });
}
