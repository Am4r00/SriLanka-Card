const TOKEN_KEY = 'token';
const PAYMENT_OK_KEY = 'payment_validated';

function getToken(){
    try{
        return localStorage.getItem('token');
    } catch (e){
        console.error('Erro ao ler token do localStorage: ' + e);
        return null;
    }
}

function clearAuth(){
    try{
        localStorage.removeItem('token');
        localStorage.removeItem('userEmail');
        document.cookie = 'jwt_token=; path=/; max-age=0; SameSite=Lax';
    }catch (e) {
        console.error('Erro ao limpar autenticação: ' + e);
    }
}

function decodeToken(token){
    if(!token || typeof  token !== 'string') return null;

    try{
        const parts = token.split('.');
        if(parts.length < 2) return null;


        const base64Url = parts[1];
        const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
        const jsonPayload = decodeURIComponent(
            atob(base64)
                .split('')
                .map(c => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
                .join('')
        );
        return JSON.parse(jsonPayload);
    } catch (e){
        console.error('Erro ao decodificar o token: ' + e);
        return null;
    }
}

function isAuthenticated(){
    return !!getToken();
}

function normalizeRole(val){
    if(!val) return '';
    if(typeof val == 'string') return val;
    if(typeof  val == 'object' && typeof val.name === 'string') return val.name;

    return String(val);
}

function hasAdminRole(raw){
    if(!raw) return false;

    if(Array.isArray(raw)){
        return raw.some(v => hasAdminRole(v));
    }

    const roleStr = normalizeRole(raw);
    return(
        roleStr === 'ADMIN' ||
        roleStr === 'ROLE_ADMIN' ||
        roleStr.includes('ADMIN')
    );
}

async function isAdmin(){
    const token = getToken();

    if(!token) return false;

try{
        const decoded = decodeToken(token);

        if(decoded){
            if(decoded.role && hasAdminRole(decoded.role)){
                return true;
            }

            if(decoded.funcoes && hasAdminRole(decoded.funcoes)){
                return true;
            }
        }
        if(window.api && typeof window.api.getCurrentUser === 'function'){
            try{
                const user = await window.api.getCurrentUser();
                if(user && user.funcoes && hasAdminRole(user.funcoes)){
                    return true;
                }
            }  catch (e) {
                console.error('Erro ao buscar usuário na API (isAdmin):', e);
            }
        }
    }catch (e) {
        console.error('Erro ao verificar admin: ' + e);
    }
    return false;
}

function showToast(msg,isError = false){

    const existingToast = document.getElementById('toast-notification');
    if(existingToast){
        existingToast.remove();
    }

    const toast = document.createElement('div');
    toast.id = 'toast-notification';
    toast.textContent = msg;
    toast.style.cssText = `
    position: fixed;
    top: 20px;
    right: 20px;
    background: ${isError ? '#ef4444' : '#10b981'};
    color: white;
    padding: 16px 24px;
    border-radius: 8px;
    box-shadow: 0 4px 6px rgba(0,0,0,0.2);
    z-index: 10000;
    font-weight: 500;
    max-width: 400px;
    animation: slideInRight 0.3s ease;
  `;

    if(!document.getElementById('toast-styles')){
        const style = document.createElement('style');
        style.id = 'toast-styles';
        style.textContent = `
      @keyframes slideInRight {
        from { transform: translateX(100%); opacity: 0; }
        to   { transform: translateX(0);   opacity: 1; }
      }
      @keyframes slideOutRight {
        from { transform: translateX(0);   opacity: 1; }
        to   { transform: translateX(100%); opacity: 0; }
      }
    `;
        document.head.appendChild(style);
    }

    document.body.appendChild(toast);

    setTimeout(() =>{
        toast.style.animation = 'slideOutRight 0.3s ease';
        setTimeout(() => toast.remove(),300);
    }, 3000);
}

function resolveProductImage(itemOrName){
    const nome = (
        typeof itemOrName === 'string'
        ? itemOrName
        : (itemOrName && itemOrName.nome) || ''
    ).toLowerCase();

    if(!nome) return '/img/steam-gift-card.png';

    if (nome.includes('apple')) return '/img/apple-gift-card.png';
    if (nome.includes('steam')) return '/img/steam-gift-card.png';
    if (nome.includes('playstation') || nome.includes('psn') || nome.includes('ps4') || nome.includes('ps5')) {
        return '/img/playstation-gift-card.png';
    }
    if (nome.includes('xbox')) return '/img/xbox.png';
    if (nome.includes('airbnb')) return '/img/airbnb.png';
    if (nome.includes('ifood')) return '/img/ifood.png';
    if (nome.includes('netflix')) return '/img/netflix.jpg';
    if (nome.includes('paramount')) return '/img/paramount.jpg';
    if (nome.includes('spotify')) return '/img/spotify.png';
    if (nome.includes('uber')) return '/img/uber.png';
    if (nome.includes('shopee')) return '/img/shopee.jpg';
    if (nome.includes('rappi')) return '/img/rappi.png';
    if (nome.includes('youtube')) return '/img/youtube.png';
    if (nome.includes('hbomax') || nome.includes('hbo max')) return '/img/hbomax.webp';
    if (nome.includes('google play') || nome.includes('gloogleplay') || nome.includes('googleplay')) {
        return '/img/gloogleplay.webp';
    }
    if (nome.includes('disney')) return '/img/disney.jpeg';
    if (nome.includes('deezer')) return '/img/Deezer.jpg';
    if (nome.includes('99')) return '/img/99-food.png';

    if (nome.includes('cyberpunk')) return '/img/cyberpunk.png';
    if (nome.includes('fc 26') || nome.includes('fc26') || nome.includes('ea fc') || nome.includes('eafc')) {
        return '/img/fc26.jpg';
    }
    if (nome.includes('forza')) return '/img/forza-horizon-5.webp';
    if (nome.includes('ghost of tsushima') || nome.includes('tsushima') || nome.includes('ghost')) {
        return '/img/Ghost_of_Tsushima_capa.png';
    }
    if (nome.includes('god of war')) return '/img/god_of_war.jpg';
    if (nome.includes('gta')) return '/img/GTA_V1.jpg';
    if (nome.includes('red dead')) return '/img/red_dead_2.png';
    if (nome.includes('the last of us') || nome.includes('last of us')) {
        return '/img/the_last_of_us.jpg';
    }
    if (nome.includes('witcher')) return '/img/the_witcher_3.png';

    if (nome.includes('gift') || nome.includes('card')) {
        return '/img/steam-gift-card.png';
    }

    return '/img/steam-gift-card.png';
}

async function updateCartCount(){
    if(!window.api || typeof window.api.getCartItemCount !== 'function')
        return;

try {
    const count = await window.api.getCartItemCount();
    const cartLink = document.querySelector('.cart-link');

    if (cartLink) {
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
} catch (error) {
    console.error('Erro ao atualizar contador do carrinho:', error);
}
}

window.TOKEN_KEY = TOKEN_KEY;
window.PAYMENT_OK_KEY = PAYMENT_OK_KEY;
window.getToken = getToken;
window.clearAuth = clearAuth;
window.decodeToken = decodeToken;
window.isAuthenticated = isAuthenticated;
window.isAdmin = isAdmin;
window.showToast = showToast;
window.resolveProductImage = resolveProductImage;
window.updateCartCount = updateCartCount;