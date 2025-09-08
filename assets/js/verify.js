// Preenche o email na frase, lendo querystring (?email=...)
const params = new URLSearchParams(window.location.search);
const email = params.get('email') || '';
const emailText = document.getElementById('emailText');
if (emailText) emailText.textContent = email;

// Validação do código e submit demo
const form = document.getElementById('verify-form');
if (form) {
  form.addEventListener('submit', (e) => {
    e.preventDefault();
    const codeEl = document.getElementById('code');
    const code = codeEl.value.trim();

    // verifica 6 dígitos
    if (!/^\d{6}$/.test(code)) {
      codeEl.reportValidity?.();
      alert('Digite um código válido de 6 dígitos.');
      return;
    }

    // TODO: Chamar sua API para validar o código
    // fetch('/api/auth/verify-code', { method:'POST', headers:{'Content-Type':'application/json'}, body: JSON.stringify({ email, code }) })
    //   .then(r => r.json())
    //   .then(() => window.location.href = 'reset.html') // caso tenha a página de redefinir
    //   .catch(err => console.error(err));

    alert('Código validado! (plugue sua API aqui)');
  });
}

// Reenvio de código com timer (exemplo 30s)
const resendLink = document.getElementById('resendLink');
const timer = document.getElementById('timer');
let canResend = true;
let countdown;

function startTimer(seconds){
  let s = seconds;
  canResend = false;
  if (timer) timer.textContent = `(aguarde ${s}s)`;
  resendLink?.classList.add('disabled');
  countdown = setInterval(()=>{
    s -= 1;
    if (timer) timer.textContent = `(aguarde ${s}s)`;
    if (s <= 0) {
      clearInterval(countdown);
      if (timer) timer.textContent = '';
      resendLink?.classList.remove('disabled');
      canResend = true;
    }
  }, 1000);
}

resendLink?.addEventListener('click', (e)=>{
  e.preventDefault();
  if (!canResend) return;

  // TODO: Chamar API de reenvio
  // fetch('/api/auth/resend-code', { method:'POST', headers:{'Content-Type':'application/json'}, body: JSON.stringify({ email }) })

  alert(`Reenviamos o código para ${email || 'seu email'}.`);
  startTimer(30);
});

// estilo mínimo para "disabled" via CSS inline opcional
const style = document.createElement('style');
style.innerHTML = `.disabled{ pointer-events:none; opacity:.6 }`;
document.head.appendChild(style);
