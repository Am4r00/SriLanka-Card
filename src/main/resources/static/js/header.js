isAuthenticated();
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

    const loginLink = document.querySelector('.login-link');
    const signupButton = document.querySelector('.cta-button');
    const userName = document.querySelector('.user-name');
    const logoutLink = document.querySelector('.logout-link');
    const authContainer = document.querySelector('#auth-container') || document.querySelector('.nav-actions > div');
    
    if (!authContainer) return;
    
    if (isAuthenticated() && userEmail) {
        if (loginLink) loginLink.style.display = 'none';
        if (signupButton && signupButton.closest('a')) {
            signupButton.closest('a').style.display = 'none';
        }
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
        
        authContainer.appendChild(userDiv);
    } else {
        if (loginLink) loginLink.style.display = 'block';
        if (signupButton && signupButton.closest('a')) {
            signupButton.closest('a').style.display = 'block';
        }
        const userDiv = document.querySelector('.user-info');
        if (userDiv) {
            userDiv.remove();
        }
    }

    await updateCartCount();
    await updateAdminmenu();
}

function setupLogoRedirect() {
    const logoLink = document.getElementById('logo-link');
    if (!logoLink) return;
    
    logoLink.addEventListener('click', async function(e) {
        e.preventDefault();
        
        const token = getToken();
        
        if (!token) {
            window.location.href = '/';
            return;
        }

        const admin = await isAdmin();
        if (admin) {
            window.location.href = '/home_admin';
        } else {
            window.location.href = '/home';
        }
    });
}
document.addEventListener('DOMContentLoaded', async () => {
    await updateHeader();
    setupLogoRedirect();
    setupCartGuard();
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

function logoutUser(){
    localStorage.removeItem('token');
    localStorage.removeItem('userEmail');
    document.cookie = 'jwt_token=; path=/; max-age=0; SameSite=Lax';
    window.location.href = '/';
}

window.updateHeader = updateHeader;
window.isAuthenticated = isAuthenticated;
window.updateCartCount = updateCartCount;
window.logoutUser = logoutUser;

function setupCartGuard(){
    const cartLink = document.querySelector('.cart-link');
    if(!cartLink) return;
    cartLink.addEventListener('click',(e)=>{
        const token = getToken();
        if(!token){
            e.preventDefault();
            showToast('Faça login para acessar o carrinho.', true);
            setTimeout(()=> window.location.href='/login', 800);
        }
    });
}

