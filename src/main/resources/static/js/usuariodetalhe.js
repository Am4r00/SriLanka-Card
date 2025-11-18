:root {
    --primary-color: #6a1b9a; /* Roxo principal */
    --secondary-color: #9c27b0; /* Roxo mais claro */
    --background-gradient: linear-gradient(135deg, #4b0082, #8a2be2); /* Gradiente de fundo */
    --delete-color: #e53935; /* Vermelho para deletar */
    --white-color: #ffffff;
    --text-color: #333;
    --card-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
}

body {
    font-family: 'Poppins', sans-serif;
    margin: 0;
    padding: 0;
    background: var(--background-gradient); /* Fundo similar ao da imagem */
    display: flex;
    justify-content: center;
    align-items: flex-start;
    min-height: 100vh;
    padding-top: 50px;
}

.container {
    background-color: var(--white-color);
    border-radius: 15px;
    box-shadow: var(--card-shadow);
    padding: 30px;
    width: 100%;
    max-width: 900px;
    margin-bottom: 50px;
}

header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 30px;
}

.logo h2 {
    color: var(--primary-color);
    font-weight: 700;
    margin: 0;
}

.search-container {
    margin-bottom: 20px;
    display: flex;
    gap: 10px;
}

.search-input {
    width: 100%;
    padding: 10px 15px;
    border: 1px solid #ccc;
    border-radius: 8px;
    font-size: 1em;
    outline-color: var(--primary-color);
}

.btn {
    padding: 10px 20px;
    border: none;
    border-radius: 8px;
    font-weight: 600;
    cursor: pointer;
    transition: background-color 0.3s;
}

.btn-primary {
    background-color: var(--primary-color);
    color: var(--white-color);
}

.btn-primary:hover {
    background-color: var(--secondary-color);
}

/* --- Tabela de Usuários --- */
.user-table {
    width: 100%;
    border-collapse: collapse;
}

.user-table th, .user-table td {
    text-align: left;
    padding: 12px 15px;
    border-bottom: 1px solid #eee;
}

.user-table th {
    background-color: #f8f8f8;
    color: var(--text-color);
    font-weight: 600;
}

.user-table tr:hover {
    background-color: #fafafa;
}

.actions-cell {
    white-space: nowrap;
    width: 1%;
}

.action-icon {
    background: none;
    border: none;
    cursor: pointer;
    margin-left: 10px;
    font-size: 1.1em;
    transition: color 0.3s;
}

.edit-icon {
    color: var(--primary-color);
}
.edit-icon:hover {
    color: var(--secondary-color);
}

.delete-icon {
    color: var(--delete-color);
}
.delete-icon:hover {
    color: darkred;
}

/* --- Modal de Edição (Popup) --- */
.modal {
    display: none;
    position: fixed;
    z-index: 1000; /* Garante que fique acima de tudo */
    left: 0;
    top: 0;
    width: 100%;
    height: 100%;
    overflow: auto;
    background-color: rgba(0,0,0,0.4);
    padding-top: 60px;
}

.modal-content {
    background-color: var(--white-color);
    margin: 5% auto;
    padding: 30px;
    border-radius: 10px;
    width: 80%;
    max-width: 500px;
    box-shadow: var(--card-shadow);
}

.close-btn {
    color: #aaa;
    float: right;
    font-size: 28px;
    font-weight: bold;
}

.close-btn:hover,
.close-btn:focus {
    color: #000;
    text-decoration: none;
    cursor: pointer;
}

.form-group {
    margin-bottom: 15px;
}

.form-group label {
    display: block;
    margin-bottom: 5px;
    font-weight: 600;
    color: var(--text-color);
}

.form-group input {
    width: 100%;
    padding: 10px;
    border: 1px solid #ccc;
    border-radius: 5px;
    box-sizing: border-box;
}