isAuthenticated();
// Função para atualizar o header
async function updateHeader() {
    const token = getToken();
    const userEmail = localStorage.getItem('userEmail');

    let displayName = userEmail;

    if(token){
        const decoded = decodeToken(token);
        console.log('token decodificado header: ' + decoded);
        if(decoded){
            displayName =
                decoded.nome ||
                decoded.name ||
                decoded.username ||
                decoded.sub ||
                userEmail;
        }
    }
    
    // Elementos do header
    const loginLink = document.querySelector('.login-link');
    const signupButton = document.querySelector('.cta-button');
    const userName = document.querySelector('.user-name');
    const logoutLink = document.querySelector('.logout-link');
    const authContainer = document.querySelector('#auth-container') || document.querySelector('.nav-actions > div');
    
    if (!authContainer) return;
    
    if (isAuthenticated() && userEmail) {
        // Usuário logado - mostrar email e botão de logout
        if (loginLink) loginLink.style.display = 'none';
        if (signupButton && signupButton.closest('a')) {
            signupButton.closest('a').style.display = 'none';
        }
        
        // Limpar container e criar elementos de usuário logado
        authContainer.innerHTML = '';
        
        const userDiv = document.createElement('div');
        userDiv.className = 'user-info';
        userDiv.style.cssText = 'display: flex; align-items: center; gap: 1rem;';
        
        const nameSpan = document.createElement('span');
        nameSpan.addEventListener('click',openUserMenu);

        nameSpan.className = 'user-name';
        nameSpan.textContent =`Bem-vindo, ${displayName}`;
        nameSpan.style.cssText = 'color: #fff; font-weight: 500;';
        userDiv.appendChild(nameSpan);
        
        const logout = document.createElement('a');
        logout.className = 'logout-link';
        logout.href = '#';
        logout.textContent = 'Sair';
        logout.style.cssText = 'color: #ef4444; text-decoration: none; cursor: pointer;';
        logout.addEventListener('click', (e) => {
            e.preventDefault();
            localStorage.removeItem('token');
            localStorage.removeItem('userEmail');
            // Remover cookie também
            document.cookie = 'jwt_token=; path=/; max-age=0; SameSite=Lax';
            window.location.href = '/';
        });
        userDiv.appendChild(logout);
        
        authContainer.appendChild(userDiv);
    } else {
        // Usuário não logado - mostrar login e signup
        if (loginLink) loginLink.style.display = 'block';
        if (signupButton && signupButton.closest('a')) {
            signupButton.closest('a').style.display = 'block';
        }
        
        // Remover elementos de usuário logado
        const userDiv = document.querySelector('.user-info');
        if (userDiv) {
            userDiv.remove();
        }
    }
    
    // Atualizar contador do carrinho
    await updateCartCount();
    await updateAdminmenu();
}

// Função para configurar o redirecionamento do logo
function setupLogoRedirect() {
    const logoLink = document.getElementById('logo-link');
    if (!logoLink) return;
    
    logoLink.addEventListener('click', async function(e) {
        e.preventDefault();
        
        const token = getToken();
        
        if (!token) {
            // Não logado - vai para home normal
            window.location.href = '/';
            return;
        }
        
        // Verificar se é admin
        const admin = await isAdmin();
        if (admin) {
            window.location.href = '/home_admin';
        } else {
            // Usuário normal - vai para home
            window.location.href = '/home';
        }
    });
}

// Inicializar header quando a página carregar
document.addEventListener('DOMContentLoaded', async () => {
    await updateHeader();
    setupLogoRedirect();
    
    // Atualizar quando o carrinho mudar
    const originalAddToCart = window.addToCart;
    if (originalAddToCart) {
        window.addToCart = async function(product) {
            await originalAddToCart(product);
            await updateCartCount();
        };
    }
});

async function updateAdminmenu() {
    const navList = document.querySelector('.nav-links ul');
    if (!navList) return;

    const existingItem = navList.querySelector('li .admin-panel-link');
    const admin = await isAdmin();

    if(admin) {
        if (!existingItem) {
            const li = document.createElement('li');
            const a = document.createElement('a');
            a.href = '/painel-admin';
            a.textContent = 'Painel Admin';
            a.className = 'admin-panel-link';
            li.appendChild(a);
            navList.appendChild(li);
        }
    } else{
        if (existingItem && existingItem.parentElement) {
            existingItem.parentElement.remove();
        }
    }
}

async function openUserMenu(){
    const menu = document.getElementById('user-menu');
    if(!menu) return;

    const token = getToken();
    const userEmail = localStorage.getItem('userEmail');

    let nome = userEmail;
    let email = userEmail;

    if(token){
        const decoded = decodeToken(token);
        nome = decoded?.nome || decoded?.name || decoded?.username || userEmail;
        email = decoded?.sub || decoded?.email || userEmail;
    } else{
        try{
            const me = await apiRequest('/users/me');
            nome = me?.name || nome;
            email = me?.email || email;
        }catch(_){}
    }
    document.getElementById('user-menu-name').textContent = nome || '-';
    document.getElementById('user-menu-email').textContent = email || '-';

    menu.classList.remove('hidden');
}

function closeUserMenu(){
    const menu = document.getElementById('user-menu');
    if(menu) menu.classList.add('hidden');
}
document.addEventListener('click', (e) => {
    if (e.target?.id === 'user-menu-close' || e.target?.id === 'user-menu') {
        closeUserMenu();
    }
});

// Exportar funções
window.updateHeader = updateHeader;
window.isAuthenticated = isAuthenticated;
window.updateCartCount = updateCartCount;

