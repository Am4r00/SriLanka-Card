// Variáveis globais
let allUsers = [];
let allProducts = [];
let allOrders = [];

// Função para verificar se o usuário é ADMIN
function checkAdminAccess() {
    const token = getToken();
    if (!token) {
        showToast(`Você precisa estar logado para acessar esta página.`, true);
        window.location.href = '/login';
        return false;
    }

    try {
        const decoded = decodeToken(token);
        console.log('Token decodificado:', decoded);

        if (!decoded) {
            throw new Error('Token inválido');
        }

        // O campo role pode vir como array (Set<Funcao> serializado) ou string
        const role = decoded.role || decoded.funcao;
        console.log('Role encontrado no token:', role, 'Tipo:', typeof role);

        let isAdmin = false;

        if (Array.isArray(role)) {
            // Se for array, verificar se contém ADMIN
            isAdmin = role.some(r => {
                const roleValue = typeof r === 'string' ? r : (r.name || r.toString() || r);
                return roleValue === 'ADMIN' || roleValue === 'ROLE_ADMIN' || roleValue.includes('ADMIN');
            });
            console.log('Verificação array - isAdmin:', isAdmin);
        } else if (typeof role === 'string') {
            // Se for string, verificar diretamente
            isAdmin = role === 'ADMIN' || role === 'ROLE_ADMIN' || role.includes('ADMIN');
            console.log('Verificação string - isAdmin:', isAdmin);
        } else if (role && typeof role === 'object') {
            // Se for objeto, verificar valores
            const roleValues = Object.values(role);
            isAdmin = roleValues.some(r => {
                const roleValue = typeof r === 'string' ? r : (r.name || r.toString() || r);
                return roleValue === 'ADMIN' || roleValue === 'ROLE_ADMIN' || roleValue.includes('ADMIN');
            });
            console.log('Verificação objeto - isAdmin:', isAdmin);
        }

        if (!isAdmin) {
            console.log('Acesso negado - usuário não é ADMIN');
            showToast('Acesso negado. Esta página é apenas para administradores.', true);
            window.location.href = '/home';
            return false;
        }

        console.log('Acesso permitido - usuário é ADMIN');
        return true;
    } catch (error) {
        console.error('Erro ao verificar token:', error);
        console.error('Stack trace:', error.stack);
        showToast('Erro ao verificar autenticação. Por favor, faça login novamente.', true);
        localStorage.removeItem('token');
        localStorage.removeItem('userEmail');
        window.location.href = '/login';
        return false;
    }
}

// Função para alternar entre abas
function switchTab(tabName) {
    document.querySelectorAll('.tab').forEach(tab => {
        tab.classList.toggle('active', tab.dataset.tab === tabName);
    });
    document.querySelectorAll('.tab-content').forEach(content => {
        content.classList.toggle('active', content.id === `${tabName}-tab`);
    });

    if (tabName === 'users') {
        loadUsers();
    } else if (tabName === 'products') {
        loadProducts();
    } else if(tabName === 'codes'){
        loadCodesTab();
    } else if(tabName === 'orders'){
        loadOrders();
    }
}

// Carregar lista de usuários
async function loadUsers() {
    try {
        const users = await apiRequest('/users/list');
        allUsers = users;
        renderUsers(users);
        console.log('Lista de usuários renderizada');
    } catch (error) {
        console.error('Erro ao carregar usuários:', error);
        document.getElementById('userTableBody').innerHTML = `
            <tr>
                <td colspan="6" style="text-align: center; padding: 20px; color: #e53935;">
                    <i class="fas fa-exclamation-triangle"></i> Erro ao carregar usuários: ${error.message}
                </td>
            </tr>
        `;
    }
}

// Renderizar usuários na tabela
function renderUsers(users) {
    const tbody = document.getElementById('userTableBody');

    if (!users || users.length === 0) {
        tbody.innerHTML = `
            <tr>
                <td colspan="6" style="text-align: center; padding: 20px;">
                    Nenhum usuário encontrado.
                </td>
            </tr>
        `;
        return;
    }

    tbody.innerHTML = users.map(user => {
        const funcoes = Array.isArray(user.funcoes) ? user.funcoes : (user.funcoes ? [user.funcoes] : []);
        const funcoesHtml = funcoes.map(f => {
            const funcao = typeof f === 'string' ? f : (f.name || f);
            return `<span class="funcoes-badge">${funcao}</span>`;
        }).join('');

        const status = user.status || 'ATIVO';
        const statusClass = status === 'ATIVO' ? 'status-ativo' : 'status-inativo';
        const statusText = status === 'ATIVO' ? 'Ativo' : 'Inativo';

        return `
            <tr>
                <td>${user.id || '-'}</td>
                <td>${user.nome || '-'}</td>
                <td>${user.email || '-'}</td>
                <td>${funcoesHtml || '-'}</td>
                <td>
                    <span class="status-badge ${statusClass}">${statusText}</span>
                </td>
                <td class="actions-cell">
                    <button class="action-icon delete-icon" onclick="deleteUser(${user.id})" title="Deletar">
                        <i class="fas fa-trash"></i>
                    </button>
                </td>
            </tr>
        `;
    }).join('');
}

// Filtrar usuários
function filterUsers() {
    const searchTerm = document.getElementById('searchUserInput').value.toLowerCase();
    const filtered = allUsers.filter(user =>
        (user.nome && user.nome.toLowerCase().includes(searchTerm)) ||
        (user.email && user.email.toLowerCase().includes(searchTerm))
    );
    renderUsers(filtered);
}

// Abrir modal de criação de usuário
function openCreateUserModal() {
    document.getElementById('userModalTitle').textContent = 'Criar Usuário';
    document.getElementById('userForm').reset();
    document.getElementById('userId').value = '';
    document.getElementById('userModal').style.display = 'block';
}

// Salvar usuário (criar ou editar)
async function saveUser(event) {
    event.preventDefault();

    const userId = document.getElementById('userId').value;
    const name = document.getElementById('userName').value;
    const email = document.getElementById('userEmail').value;
    const password = document.getElementById('userPassword').value;
    const role = document.getElementById('userRole').value;
    const status = document.getElementById('userStatus').value;

    try {
        let endpoint, payload;

        if (userId) {
            showToast('Edição de usuários desabilitada por segurança.', true);
            closeUserModal();
            return;
        } else {
            if (!password || password.trim() === '') {
                showToast('Senha é obrigatória para criar usuários.', true);
                return;
            }
            endpoint = '/admin/create-user';
            payload = { name, email, password, status, funcoes: [role] };
        }

        await apiRequest(endpoint, {
            method: 'POST',
            body: JSON.stringify(payload)
        });

        showToast('Usuário salvo com sucesso! ', false);
        closeUserModal();
        // Limpar cache e recarregar lista
        allUsers = [];
        await loadUsers();
    } catch (error) {
        console.error('Erro ao salvar usuário:', error);
        showToast(`Erro ao salvar usuário: ${error.message}`, true);
    }
}

// Deletar usuário
async function deleteUser(userId) {
    if (!confirm('Tem certeza que deseja deletar este usuário?')) {
        return;
    }

    try {
        const response =         await apiRequest(`/admin/delete-user/${userId}`, {
            method: 'DELETE'
        });
        showToast('Usuário deletado com sucesso!', false);
        // Limpar cache e recarregar lista
        allUsers = [];
        await loadUsers();
    } catch (error) {
        console.error('Erro ao deletar usuário:', error);
        showToast(`Erro ao deletar usuário: ${error.message}`, true);
    }
}

// Fechar modal de usuário
function closeUserModal() {
    document.getElementById('userModal').style.display = 'none';
    document.getElementById('userPassword').required = true; // Restaurar required
}

// Carregar lista de produtos
async function loadProducts() {
    try {
        const products = await apiRequest('/cards')
        allProducts = products || [];
        renderProducts(products);

        if(document.querySelector('[data-tab="codes"].active')){
            renderProducts(allProducts);}

        console.log('Lista de produtos renderizada');
        return allProducts;

        console.log('Lista de produtos renderizada');
    } catch (error) {
        console.error('Erro ao carregar produtos:', error);
        document.getElementById('productTableBody').innerHTML = `
            <tr>
                <td colspan="7" style="text-align: center; padding: 20px; color: #e53935;">
                    <i class="fas fa-exclamation-triangle"></i> Erro ao carregar produtos: ${error.message}
                </td>
            </tr>
        `;
    }
}

async function loadCodesTab(){
    if(!allProducts || allProducts.length === 0){
        await loadProducts();
    }

    renderCodeProducts(allProducts);
}

async function loadOrders(){
    try{
        const pedidos = await apiRequest('/api/pedidos/admin/historico');
        allOrders = pedidos || [];
        renderOrders(allOrders);
    }catch (e){
        console.error('Erro ao carregar pedidos:', e);
        document.getElementById('ordersTableBody').innerHTML = `
            <tr>
                <td colspan="5" style="text-align:center;padding:20px;color:#e53935;">
                    <i class="fas fa-exclamation-triangle"></i> Erro ao carregar pedidos: ${e.message}
                </td>
            </tr>
        `;
    }
}

function renderOrders(orders){
    const tbody = document.getElementById('ordersTableBody');
    if(!tbody) return;

    if(!orders || orders.length === 0){
        tbody.innerHTML = `
            <tr>
                <td colspan="5" style="text-align:center;padding:20px;">
                    Nenhum pedido encontrado.
                </td>
            </tr>
        `;
        return;
    }

    tbody.innerHTML = orders.map(p => {
        const valor = p.valorTotal ? `R$ ${p.valorTotal.toFixed(2)}` : '-';
        const data = p.criadoEm ? new Date(p.criadoEm).toLocaleString('pt-BR') : '-';
        return `
            <tr>
                <td>${p.id || '-'}</td>
                <td>${p.usuarioId || '-'}</td>
                <td>${valor}</td>
                <td>${p.quantidadeTotal || 0}</td>
                <td>${data}</td>
            </tr>
        `;
    }).join('');
}

function renderCodeProducts(products){
    const tbody = document.getElementById('codeTableBody');

    if(!products || products.length === 0){
        tbody.innerHTML = `
            <tr><td colspan="4" style="text-align:center;padding:20px;">
                Nenhum produto encontrado.
            </td></tr>`;
        return;
    }

    tbody.innerHTML = products.map(p => `
        <tr>
            <td>${p.id || '-'}</td>
            <td>${p.nome || '-'}</td>
            <td>${p.quantidade || 0}</td>
            <td>
    <div class="code-actions">
        <input type="number" min="1" value="1" id="code-qty-${p.id}" class="code-input">
        <button class="btn btn-success" onclick="generateCodes(${p.id})">
            Gerar
        </button>
    </div>
</td>
        </tr>
    `).join('');
}

async function generateCodes(cardId){
    const input = document.getElementById(`code-qty-${cardId}`);
    const quantidade = parseInt(input?.value,10);

    if (!quantidade || quantidade <= 0) {
        showToast('Informe uma quantidade válida para gerar codes.', true);
        return;
    }

    try{
        await apiRequest('/admin/gift-codes/gerar',{
            method: 'POST',
            body: JSON.stringify({cardId, quantidade})
        });
        showToast('Codes gerados com sucesso !', false);

        const produtos = await loadProducts();
        renderCodeProducts(produtos);
    }catch (e) {
        showToast(`Erro ao gerar codes: ${e.message}`, true);
    }
}

// Renderizar produtos na tabela
function renderProducts(products) {
    const tbody = document.getElementById('productTableBody');

    if (!products || products.length === 0) {
        tbody.innerHTML = `
            <tr>
                <td colspan="7" style="text-align: center; padding: 20px;">
                    Nenhum produto encontrado.
                </td>
            </tr>
        `;
        return;
    }

    tbody.innerHTML = products.map(product => {
        const valor = product.valor ? `R$ ${product.valor.toFixed(2)}` : '-';
        const promocao = product.promocao ?
            '<span class="status-badge status-ativo">Sim</span>' :
            '<span class="status-badge status-inativo">Não</span>';

        return `
            <tr>
                <td>${product.id || '-'}</td>
                <td>${product.nome || '-'}</td>
                <td>${product.observacoes || '-'}</td>
                <td>${valor}</td>
                <td>${product.quantidade || 0}</td>
                <td>${promocao}</td>
                <td class="actions-cell">
                    <button class="btn-edit" onclick="openEditProductModal(${product.id})" title="Editar">
                        <i class="fas fa-edit"></i>
                    </button>
                    <button class="action-icon delete-icon" onclick="deleteProduct(${product.id})" title="Deletar">
                        <i class="fas fa-trash"></i>
                    </button>
                </td>
            </tr>
        `;
    }).join('');
}

// Filtrar produtos
function filterProducts() {
    const searchTerm = document.getElementById('searchProductInput').value.toLowerCase();
    const filtered = allProducts.filter(product =>
        product.nome && product.nome.toLowerCase().includes(searchTerm)
    );
    renderProducts(filtered);
}

// Abrir modal de criação de produto
function openCreateProductModal() {
    document.getElementById('productModalTitle').textContent = 'Criar Produto';
    document.getElementById('productForm').reset();
    document.getElementById('productId').value = '';
    document.getElementById('productModal').style.display = 'block';
}

// Abrir modal de edição de produto
async function openEditProductModal(productId) {
    try {
        const product = await apiRequest(`/cards/${productId}`);

        document.getElementById('productModalTitle').textContent = 'Editar Produto';
        document.getElementById('productId').value = product.id;
        document.getElementById('productName').value = product.nome || '';
        document.getElementById('productObservacoes').value = product.observacoes || '';
        document.getElementById('productValor').value = product.valor || 0;
        document.getElementById('productPromocao').checked = product.promocao || false;

        document.getElementById('productModal').style.display = 'block';
    } catch (error) {
        console.error('Erro ao carregar produto:', error);
        showToast('Erro ao carregar dados do produto', true);
    }
}

// Salvar produto (criar ou editar)
async function saveProduct(event) {
    event.preventDefault();

    const productId = document.getElementById('productId').value;
    const nome = document.getElementById('productName').value;
    const observacoes = document.getElementById('productObservacoes').value;
    const valor = parseFloat(document.getElementById('productValor').value);
    const category = document.getElementById('productCategory').value
    const promocao = document.getElementById('productPromocao').checked;

    if (!category) {
        showToast('Selecione uma categoria antes de salvar o produto.', true);
        return;
    }

    try {
        if (productId) {
            // Editar produto
            const payload = { nome, observacoes, valor };
            const updatedProduct = await apiRequest(`/cards/atualizar/${productId}`, {
                method: 'PATCH',
                body: JSON.stringify(payload)
            });
            console.log('Produto atualizado:', updatedProduct);

            await apiRequest(`/cards/${productId}/promocao/${promocao}`, {
                method: 'PATCH'
            });

            console.log('Promoção atualizada');
        } else {
            const payload = {
                nome,
                observacoes,
                valor,
                category: category,
                promocao
            };

            console.log('Payload enviado:', payload);
            console.log('Token atual:', getToken() ? 'Token presente' : 'Token ausente');

            // Verificar token antes de enviar
            const token = getToken();
            if (!token) {
                showToast('Você precisa estar logado para criar produtos. Redirecionando para login...', true);
                window.location.href = '/login';
                return;
            }

            // Decodificar token para verificar role
            try {
                const decoded = decodeToken(token);
                console.log('Token decodificado:', decoded);
                console.log('Role no token:', decoded.role);
            } catch (e) {
                console.error('Erro ao decodificar token:', e);
            }

            const response = await apiRequest('/cards/criar-Card', {
                method: 'POST',
                body: JSON.stringify(payload)
            });

            console.log('Resposta do servidor:', response);
        }

        showToast('Produto salvo com sucesso!', false);
        closeProductModal();
        // Limpar cache e recarregar lista
        allProducts = [];
        await loadProducts();
    } catch (error) {
        console.error('Erro ao salvar produto:', error);
        showToast(`Erro ao salvar produto: ${error.message}`, true);
    }
}

// Deletar produto
async function deleteProduct(productId) {
    if (!confirm('Tem certeza que deseja deletar este produto?')) {
        return;
    }

    try {
        await apiRequest(`/cards/deletar/${productId}`, {
            method: 'DELETE'
        });
        showToast('Produto deletado com sucesso!', false);
        allProducts = [];
        await loadProducts();
    } catch (error) {
        console.error('Erro ao deletar produto:', error);
        showToast(`Erro ao deletar o produto: ${error.message}`, true);

    }
}

// Fechar modal de produto
function closeProductModal() {
    document.getElementById('productModal').style.display = 'none';
}

// Fechar modais ao clicar fora
window.onclick = function(event) {
    const userModal = document.getElementById('userModal');
    const productModal = document.getElementById('productModal');
    if (event.target === userModal) {
        closeUserModal();
    }
    if (event.target === productModal) {
        closeProductModal();
    }
}

// Carregar dados ao inicializar
document.addEventListener('DOMContentLoaded', () => {
    // Verificar se o usuário é ADMIN antes de carregar dados
    if (checkAdminAccess()) {
        switchTab('users');
    }
});
