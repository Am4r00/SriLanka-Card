// Script para gerenciar o header com autenticação JWT

// Função para verificar se o usuário está autenticado
function isAuthenticated() {
    const token = getToken();
    return token !== null && token !== undefined && token !== '';
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

// Função para atualizar o header
function updateHeader() {
    const token = getToken();
    const userEmail = localStorage.getItem('userEmail');
    
    // Encontrar elementos do header
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
        nameSpan.className = 'user-name';
        nameSpan.textContent = userEmail;
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
    updateCartCount();
}

// Função para atualizar contador do carrinho no header
function updateCartCount() {
    const count = api.getCartItemCount();
    const cartLink = document.querySelector('.cart-link');
    
    if (cartLink) {
        // Remover contador existente
        const existingCount = cartLink.querySelector('.cart-count');
        if (existingCount) {
            existingCount.remove();
        }
        
        if (count > 0) {
            const countBadge = document.createElement('span');
            countBadge.className = 'cart-count';
            countBadge.textContent = count;
            countBadge.style.cssText = `
                position: absolute;
                top: -8px;
                right: -8px;
                background: #ef4444;
                color: white;
                border-radius: 50%;
                width: 20px;
                height: 20px;
                display: flex;
                align-items: center;
                justify-content: center;
                font-size: 12px;
                font-weight: bold;
            `;
            cartLink.style.position = 'relative';
            cartLink.appendChild(countBadge);
        }
    }
}

// Função para verificar se o usuário é admin
async function isAdmin() {
    const token = getToken();
    if (!token) return false;
    
    try {
        const decoded = decodeToken(token);
        // Verificar se o token contém a role ADMIN
        if (decoded && decoded.role) {
            const role = decoded.role;
            if (Array.isArray(role)) {
                return role.some(r => {
                    const roleValue = typeof r === 'string' ? r : (r.name || r.toString() || r);
                    return roleValue === 'ADMIN' || roleValue === 'ROLE_ADMIN' || roleValue.includes('ADMIN');
                });
            }
            return role === 'ADMIN' || role === 'ROLE_ADMIN' || role.includes('ADMIN');
        }
        // Verificar se tem funcoes no token
        if (decoded && decoded.funcoes) {
            if (Array.isArray(decoded.funcoes)) {
                return decoded.funcoes.some(f => {
                    const funcaoValue = typeof f === 'string' ? f : (f.name || f.toString() || f);
                    return funcaoValue === 'ADMIN' || funcaoValue === 'ROLE_ADMIN' || funcaoValue.includes('ADMIN');
                });
            }
            return decoded.funcoes.includes('ADMIN') || decoded.funcoes.includes('ROLE_ADMIN');
        }
        
        // Se não encontrou no token, tentar buscar da API
        if (window.api && typeof window.api.getCurrentUser === 'function') {
            try {
                const user = await window.api.getCurrentUser();
                if (user && user.funcoes) {
                    if (Array.isArray(user.funcoes)) {
                        return user.funcoes.some(f => {
                            const funcaoValue = typeof f === 'string' ? f : (f.name || f.toString() || f);
                            return funcaoValue === 'ADMIN' || funcaoValue === 'ROLE_ADMIN' || funcaoValue.includes('ADMIN');
                        });
                    }
                    return user.funcoes.includes('ADMIN') || user.funcoes.includes('ROLE_ADMIN');
                }
            } catch (apiError) {
                console.error('Erro ao buscar usuário da API:', apiError);
            }
        }
    } catch (e) {
        console.error('Erro ao verificar admin:', e);
    }
    return false;
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
document.addEventListener('DOMContentLoaded', () => {
    updateHeader();
    setupLogoRedirect();
    
    // Atualizar quando o carrinho mudar
    const originalAddToCart = window.addToCart;
    if (originalAddToCart) {
        window.addToCart = function(product) {
            originalAddToCart(product);
            updateCartCount();
        };
    }
});

// Exportar funções
window.updateHeader = updateHeader;
window.isAuthenticated = isAuthenticated;

