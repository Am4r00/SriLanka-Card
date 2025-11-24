// Script para gerenciar o painel administrativo

// Variáveis globais
let allUsers = [];
let allProducts = [];

// Função para verificar se o usuário é ADMIN
function checkAdminAccess() {
    const token = getToken();
    if (!token) {
        alert('Você precisa estar logado para acessar esta página.');
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
            alert('Acesso negado. Esta página é apenas para administradores.');
            window.location.href = '/home';
            return false;
        }
        
        console.log('Acesso permitido - usuário é ADMIN');
        return true;
    } catch (error) {
        console.error('Erro ao verificar token:', error);
        console.error('Stack trace:', error.stack);
        alert('Erro ao verificar autenticação. Por favor, faça login novamente.');
        localStorage.removeItem('token');
        localStorage.removeItem('userEmail');
        window.location.href = '/login';
        return false;
    }
}

// Função para decodificar token JWT (básico, sem validação)
function decodeToken(token) {
    try {
        const base64Url = token.split('.')[1];
        const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
        const jsonPayload = decodeURIComponent(atob(base64).split('').map(function(c) {
            return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
        }).join(''));
        return JSON.parse(jsonPayload);
    } catch (e) {
        return null;
    }
}

// Função para alternar entre abas
function switchTab(tabName) {
    // Remover active de todas as abas
    document.querySelectorAll('.tab').forEach(tab => tab.classList.remove('active'));
    document.querySelectorAll('.tab-content').forEach(content => content.classList.remove('active'));
    
    // Adicionar active na aba selecionada
    if (tabName === 'users') {
        document.querySelector('.tab:first-child').classList.add('active');
        document.getElementById('users-tab').classList.add('active');
        loadUsers();
    } else if (tabName === 'products') {
        document.querySelector('.tab:last-child').classList.add('active');
        document.getElementById('products-tab').classList.add('active');
        loadProducts();
    }
}

// ==================== GERENCIAMENTO DE USUÁRIOS ====================

// Carregar lista de usuários
async function loadUsers() {
    try {
        const users = await apiRequest('/users/list');
        allUsers = users;
        renderUsers(users);
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
        
        return `
            <tr>
                <td>${user.id || '-'}</td>
                <td>${user.nome || '-'}</td>
                <td>${user.email || '-'}</td>
                <td>${funcoesHtml || '-'}</td>
                <td>
                    <span class="status-badge status-ativo">Ativo</span>
                </td>
                <td class="actions-cell">
                    <button class="btn-edit" onclick="openEditUserModal(${user.id})" title="Editar">
                        <i class="fas fa-edit"></i>
                    </button>
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

// Abrir modal de edição de usuário
async function openEditUserModal(userId) {
    try {
        const users = await apiRequest('/users/list');
        const user = users.find(u => u.id === userId);
        
        if (!user) {
            alert('Usuário não encontrado');
            return;
        }
        
        document.getElementById('userModalTitle').textContent = 'Editar Usuário';
        document.getElementById('userId').value = user.id;
        document.getElementById('userName').value = user.nome || '';
        document.getElementById('userEmail').value = user.email || '';
        document.getElementById('userPassword').value = ''; // Não preencher senha
        document.getElementById('userPassword').required = false; // Senha opcional na edição
        
        // Definir função (pegar a primeira se for array)
        const funcoes = Array.isArray(user.funcoes) ? user.funcoes : (user.funcoes ? [user.funcoes] : []);
        if (funcoes.length > 0) {
            const funcao = typeof funcoes[0] === 'string' ? funcoes[0] : (funcoes[0].name || funcoes[0]);
            document.getElementById('userRole').value = funcao;
        }
        
        document.getElementById('userModal').style.display = 'block';
    } catch (error) {
        console.error('Erro ao carregar usuário:', error);
        alert('Erro ao carregar dados do usuário');
    }
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
            // Editar usuário - atualizar status se necessário
            // Nota: O backend atual só permite atualizar status via PATCH /admin/update-user/{id}/{status}
            // Para editar outros campos, seria necessário criar um endpoint específico
            if (status) {
                await apiRequest(`/admin/update-user/${userId}/${status}`, {
                    method: 'PATCH'
                });
                alert('Status do usuário atualizado com sucesso!');
            } else {
                alert('Edição completa de usuário ainda não implementada no backend. Apenas status pode ser atualizado.');
            }
            closeUserModal();
            loadUsers();
            return;
        } else {
            // Criar novo usuário
            if (role === 'ADMIN') {
                endpoint = '/admin/create-user';
                payload = { name, email };
            } else if (role === 'ESTOQUISTA') {
                endpoint = '/admin/create-employee';
                payload = { name, email };
            } else {
                endpoint = '/admin/create-user-common';
                payload = { name, email, password };
            }
        }
        
        await apiRequest(endpoint, {
            method: 'POST',
            body: JSON.stringify(payload)
        });
        
        alert('Usuário salvo com sucesso!');
        closeUserModal();
        loadUsers();
    } catch (error) {
        console.error('Erro ao salvar usuário:', error);
        alert(`Erro ao salvar usuário: ${error.message}`);
    }
}

// Deletar usuário
async function deleteUser(userId) {
    if (!confirm('Tem certeza que deseja deletar este usuário?')) {
        return;
    }
    
    try {
        const response = await apiRequest(`/admin/delete-user/${userId}`, {
            method: 'DELETE'
        });
        alert('Usuário deletado com sucesso!');
        loadUsers();
    } catch (error) {
        console.error('Erro ao deletar usuário:', error);
        alert(`Erro ao deletar usuário: ${error.message}`);
    }
}

// Fechar modal de usuário
function closeUserModal() {
    document.getElementById('userModal').style.display = 'none';
    document.getElementById('userPassword').required = true; // Restaurar required
}

// ==================== GERENCIAMENTO DE PRODUTOS ====================

// Carregar lista de produtos
async function loadProducts() {
    try {
        const products = await apiRequest('/cards/listar');
        allProducts = products;
        renderProducts(products);
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
        document.getElementById('productQuantidade').value = product.quantidade || 0;
        document.getElementById('productPromocao').checked = product.promocao || false;
        
        document.getElementById('productModal').style.display = 'block';
    } catch (error) {
        console.error('Erro ao carregar produto:', error);
        alert('Erro ao carregar dados do produto');
    }
}

// Salvar produto (criar ou editar)
async function saveProduct(event) {
    event.preventDefault();
    
    const productId = document.getElementById('productId').value;
    const nome = document.getElementById('productName').value;
    const observacoes = document.getElementById('productObservacoes').value;
    const valor = parseFloat(document.getElementById('productValor').value);
    const quantidade = parseInt(document.getElementById('productQuantidade').value);
    const promocao = document.getElementById('productPromocao').checked;
    
    try {
        if (productId) {
            // Editar produto
            const payload = { nome, observacoes, valor };
            await apiRequest(`/cards/atualizar/${productId}`, {
                method: 'PATCH',
                body: JSON.stringify(payload)
            });
            
            // Atualizar promoção separadamente (endpoint tem /cards duplicado no controller)
            await apiRequest(`/cards/cards/${productId}/promocao/${promocao}`, {
                method: 'PATCH'
            });
        } else {
            // Criar produto
            const payload = { nome, observacoes, valor, quantidade, promocao };
            await apiRequest('/cards/criar-Card', {
                method: 'POST',
                body: JSON.stringify(payload)
            });
        }
        
        alert('Produto salvo com sucesso!');
        closeProductModal();
        loadProducts();
    } catch (error) {
        console.error('Erro ao salvar produto:', error);
        alert(`Erro ao salvar produto: ${error.message}`);
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
        alert('Produto deletado com sucesso!');
        loadProducts();
    } catch (error) {
        console.error('Erro ao deletar produto:', error);
        alert(`Erro ao deletar produto: ${error.message}`);
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
        loadUsers();
    }
});

