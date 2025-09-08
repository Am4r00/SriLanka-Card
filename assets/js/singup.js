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

// Submit demo
const form = document.getElementById('signup');
if (form){
  form.addEventListener('submit', (e)=>{
    e.preventDefault();
    const allValid = form.checkValidity() && checkMatch() && document.getElementById('terms').checked;

    if(!allValid){
      form.reportValidity?.();
      checkMatch();
      return;
    }

    const payload = {
      name: document.getElementById('name').value.trim(),
      email: document.getElementById('email').value.trim(),
      password: pwd.value
    };

    // TODO: Substitua pelo seu endpoint real:
    // fetch('/api/auth/signup', {
    //   method: 'POST',
    //   headers: { 'Content-Type': 'application/json' },
    //   body: JSON.stringify(payload)
    // })
    // .then(r => r.json())
    // .then(data => console.log(data))
    // .catch(err => console.error(err));

    alert('Cadastro enviado! (plugue sua API aqui)');
  });
}
