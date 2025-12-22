// Helper para fazer requisições autenticadas
const API_BASE_URL = '';

// Função para fazer requisições autenticadas
async function apiRequest(url, options = {}) {
    const token = (typeof getToken === 'function')
    ? getToken()
    : localStorage.getItem('token');

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
        return await apiRequest('/cards');
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
    
    // Carrinho - busca do backend
    getCart: async () => {
        try {
            const cart = await apiRequest('/api/carrinho');
            return cart || { itens: [], valorTotal: 0, quantidade: 0 };
        } catch (error) {
            console.error('Erro ao buscar carrinho:', error);
            return { itens: [], valorTotal: 0, quantidade: 0 };
        }
    },
    
    addToCart: async (productId, quantidade = 1) => {
        return await apiRequest('/api/carrinho', {
            method: 'POST',
            body: JSON.stringify({ id: productId, quantidade})
        });
    },
    
    removeFromCart: async (produtoId) => {
        return await apiRequest(`/api/carrinho/itens/${produtoId}`, {
            method: 'DELETE'
        });
    },
    
    clearCart: async () => {
        return await apiRequest('/api/carrinho', {
            method: 'DELETE'
        });
    },
    
    getCartTotal: async () => {
        try {
            const totals = await apiRequest('/api/carrinho/total');
            return totals || { valorTotal: 0, quantidadeTotal: 0 };
        } catch (error) {
            console.error('Erro ao buscar totais do carrinho:', error);
            return { valorTotal: 0, quantidadeTotal: 0 };
        }
    },
    
    // Função auxiliar para compatibilidade (retorna contagem do backend)
    getCartItemCount: async () => {
        try {
            const totals = await api.getCartTotal();
            return totals.quantidadeTotal || 0;
        } catch (error) {
            return 0;
        }
    }
};

// Exportar para uso global
window.api = api;
window.apiRequest = apiRequest;