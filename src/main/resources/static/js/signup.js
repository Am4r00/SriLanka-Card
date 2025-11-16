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

  try {
    let endpoint, payload;

    // 🔥 LÓGICA PRINCIPAL
    if (password === "admin12345678") {
      // criar ADMIN
      endpoint = '/admin/create-user';
      payload = { name, email };

    } else {
      // criar USUÁRIO COMUM
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

      if (password === "admin12345678") {
        alert(`Administrador criado!\nEmail: ${email}\nSenha fixa: admin12345678`);
      } else {
        alert(`Usuário criado com sucesso!\nEmail: ${email}`);
      }

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
