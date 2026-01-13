document.addEventListener('DOMContentLoaded', async () => {
   await updateHeader();
   await carregarPerfil();
   document.getElementById('perfil-form').addEventListener('submit',salvarPerfil);
});

async function carregarPerfil(){
    try{
        const me = await apiRequest('/users/me');
        document.getElementById('perfil-nome').value = me.name || me.nome || '';
        document.getElementById('perfil-email').value = me.email || '';
    }catch (e) {
        showToast('Erro ao carregar o perfil ', true);
    }
}

async function salvarPerfil(e) {
    e.preventDefault();
    const nome = document.getElementById('perfil-nome').value;
    const email = document.getElementById('perfil-email').value;
    const senhaAtual = document.getElementById('perfil-senha-atual').value;
    const senhaNova = document.getElementById('perfil-senha-nova').value;
    const senhaConf = document.getElementById('perfil-senha-conf').value;

    if (senhaNova || senhaConf) {
        if (senhaNova !== senhaConf) {
            showToast('Nova senha e confirmação não conferem.', true);
            return;
        }
        if (!senhaAtual) {
            showToast('Informe a senha atual para trocar a senha.', true);
            return;
        }
    }

    const payload = { nome, email };
    if (senhaNova) {
        payload.senhaAtual = senhaAtual;
        payload.novaSenha = senhaNova;
    }

    try {
        await apiRequest('/users/me', { method: 'PATCH', body: JSON.stringify(payload) });
        localStorage.setItem('userEmail', email);
        await updateHeader();
        showToast('Perfil atualizado com sucesso!', false);
    } catch (err) {
        showToast('Erro ao salvar: ' + err.message, true);
    }
}
