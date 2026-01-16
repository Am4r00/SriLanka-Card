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

    const payload = { nome, email };

    try {
        await apiRequest('/users/me', { method: 'PATCH', body: JSON.stringify(payload) });
        localStorage.setItem('userEmail', email);
        await updateHeader();
        showToast('Perfil atualizado com sucesso!', false);
        bloquearEdicao();
    } catch (err) {
        showToast('Erro ao salvar: ' + err.message, true);
    }
}

function habilitarEdicao(){
    document.getElementById('perfil-nome').readOnly = false;
    document.getElementById('perfil-email').readOnly = false;
    document.getElementById('perfil-salvar').disabled = false;
}

function bloquearEdicao(){
    document.getElementById('perfil-nome').readOnly = true;
    document.getElementById('perfil-email').readOnly = true;
    document.getElementById('perfil-salvar').disabled = true;
}

function abrirModalSenha(){
    document.getElementById('modal-senha').style.display = 'grid';
}

function fecharModalSenha(){
    document.getElementById('modal-senha').style.display = 'none';
    document.getElementById('senha-form').reset();
}

async function salvarSenha(e){
    e.preventDefault();
    const senhaAtual = document.getElementById('senha-atual').value;
    const senhaNova = document.getElementById('senha-nova').value;
    const senhaConf = document.getElementById('senha-conf').value;

    if (senhaNova !== senhaConf) {
        showToast('Nova senha e confirmação não conferem.', true);
        return;
    }

    try{
        await apiRequest('/users/me',{
            method:'PATCH',
            body: JSON.stringify({ senhaAtual, novaSenha: senhaNova })
        });
        showToast('Senha atualizada com sucesso!', false);
        fecharModalSenha();
    }catch (err){
        showToast('Erro ao alterar senha: ' + err.message, true);
    }
}
