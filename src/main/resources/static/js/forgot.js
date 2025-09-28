const form = document.getElementById('forgot-form');

if (form) {
  form.addEventListener('submit', (e) => {
    e.preventDefault();
    const email = document.getElementById('email').value.trim();
    if (!email) {
      form.reportValidity?.();
      return;
    }

    // TODO: Chamar sua API real para envio do código:
    // fetch('/api/auth/forgot', { method:'POST', headers:{'Content-Type':'application/json'}, body: JSON.stringify({ email }) })
    //   .then(r => r.json())
    //   .then(() => window.location.href = `verify.html?email=${encodeURIComponent(email)}`)
    //   .catch(err => console.error(err));

    // Demo:
    window.location.href = `verify.html?email=${encodeURIComponent(email)}`;
  });
}
