// Helper para fazer requisições autenticadas
const API_BASE_URL = '';

// Função para obter o token do localStorage
function getToken() {
    return localStorage.getItem('token');
}

// Função para fazer requisições autenticadas
async function apiRequest(url, options = {}) {
    const token = getToken();
    
    const defaultHeaders = {
        'Content-Type': 'application/json',
    };
    
    if (token) {
        defaultHeaders['Authorization'] = `Bearer ${token}`;
    }
    
    const config = {
        ...options,
        headers: {
            ...defaultHeaders,
            ...options.headers,
        },
    };
    
    try {
        console.log('Requisição:', url, 'Headers:', config.headers);
        const response = await fetch(API_BASE_URL + url, config);
        
        console.log('Status da resposta:', response.status, response.statusText);
        
        if (response.status === 401) {
            // Token inválido ou expirado
            localStorage.removeItem('token');
            localStorage.removeItem('userEmail');
            // Remover cookie também
            document.cookie = 'jwt_token=; path=/; max-age=0; SameSite=Lax';
            window.location.href = '/login';
            return null;
        }
        
        if (!response.ok) {
            let errorMessage = `Erro ${response.status}`;
            try {
                const errorText = await response.text();
                if (errorText) {
                    try {
                        const errorData = JSON.parse(errorText);
                        errorMessage = errorData.message || errorData.error || errorText;
                    } catch {
                        errorMessage = errorText || `Erro ${response.status}: ${response.statusText}`;
                    }
                }
            } catch (e) {
                errorMessage = `Erro ${response.status}: ${response.statusText}`;
            }
            throw new Error(errorMessage);
        }
        
        // Tentar fazer parse JSON
        // Se a resposta estiver vazia (como em DELETE), retornar null
        try {
            const text = await response.text();
            // Se o texto estiver vazio, retornar null
            if (!text || text.trim() === '') {
                return null;
            }
            // Tentar fazer parse JSON
            try {
                return JSON.parse(text);
            } catch (parseError) {
                // Se não conseguir fazer parse, pode ser que não seja JSON válido
                // Retornar null para respostas vazias ou inválidas
                if (parseError.message && parseError.message.includes('Unexpected end of JSON input')) {
                    return null;
                }
                throw parseError;
            }
        } catch (error) {
            // Se houver erro ao ler o texto, retornar null (resposta vazia)
            if (error.message && error.message.includes('Unexpected end of JSON input')) {
                return null;
            }
            throw error;
        }
    } catch (error) {
        console.error('Erro na requisição:', error);
        throw error;
    }
}

// Funções específicas da API
const api = {
    // Autenticação
    login: async (email, password) => {
        return await apiRequest('/auth/login', {
            method: 'POST',
            body: JSON.stringify({ email, password }),
        });
    },
    
    // Cards/Produtos
    listarCards: async () => {
        return await apiRequest('/cards/listar');
    },
    
    buscarCard: async (id) => {
        const cards = await api.listarCards();
        return cards.find(c => c.id === id);
    },
    
    // Usuário
    getCurrentUser: async () => {
        return await apiRequest('/users/me');
    },
    
    listarUsuarios: async () => {
        return await apiRequest('/users/list');
    },
    
    // Carrinho (usando localStorage por enquanto)
    getCart: () => {
        const cart = localStorage.getItem('cart');
        return cart ? JSON.parse(cart) : [];
    },
    
    addToCart: (product) => {
        const cart = api.getCart();
        const existingItem = cart.find(item => item.id === product.id);
        
        if (existingItem) {
            existingItem.quantity += 1;
        } else {
            cart.push({ ...product, quantity: 1 });
        }
        
        localStorage.setItem('cart', JSON.stringify(cart));
        return cart;
    },
    
    removeFromCart: (productId) => {
        const cart = api.getCart().filter(item => item.id !== productId);
        localStorage.setItem('cart', JSON.stringify(cart));
        return cart;
    },
    
    updateCartQuantity: (productId, quantity) => {
        const cart = api.getCart();
        const item = cart.find(item => item.id === productId);
        
        if (item) {
            if (quantity <= 0) {
                return api.removeFromCart(productId);
            }
            item.quantity = quantity;
        }
        
        localStorage.setItem('cart', JSON.stringify(cart));
        return cart;
    },
    
    clearCart: () => {
        localStorage.removeItem('cart');
    },
    
    getCartTotal: () => {
        const cart = api.getCart();
        return cart.reduce((total, item) => {
            const price = item.valor || item.preco || 0;
            const quantity = item.quantity || 1;
            return total + (price * quantity);
        }, 0);
    },
    
    getCartItemCount: () => {
        const cart = api.getCart();
        return cart.reduce((total, item) => total + (item.quantity || 1), 0);
    }
};

// Exportar para uso global
window.api = api;
window.apiRequest = apiRequest;
window.getToken = getToken;

