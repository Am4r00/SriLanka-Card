# 🎁 SriLanka Card

<div align="center">

![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.6-brightgreen?style=for-the-badge&logo=spring)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue?style=for-the-badge&logo=postgresql)
![Thymeleaf](https://img.shields.io/badge/Thymeleaf-3.1-green?style=for-the-badge)
![JWT](https://img.shields.io/badge/JWT-Auth-purple?style=for-the-badge)

**Plataforma de venda de Gift Cards e Jogos Digitais**

[Funcionalidades](#-funcionalidades) • [Tecnologias](#-tecnologias) • [Instalação](#-instalação) • [Testes](#-testes)

</div>

---

## 📋 Sobre o Projeto

O **SriLanka Card** é uma plataforma e-commerce completa para venda de gift cards e jogos digitais. Desenvolvido como projeto acadêmico, oferece uma experiência de compra moderna e intuitiva, com sistema de autenticação JWT, painel administrativo e gestão completa de produtos e usuários.

### 🎯 Principais Características

- 🛒 **Carrinho de Compras** integrado com backend
- 🔐 **Autenticação JWT** com roles (ADMIN, USUARIO)
- 📦 **Gestão de Produtos** com categorização automática
- 👥 **Painel Administrativo** completo
- 💳 **Sistema de Gift Codes** com geração automática
- 🧾 **Checkout Completo** com histórico de pedidos e e-mail contendo os códigos comprados
- 🪪 **Ativação e Perfil** com reenvio de código, atualização de dados e troca de senha
- 📧 **Envio de E-mails** para boas-vindas e recuperação de senha
- 🎨 **Interface Moderna** com design responsivo

---

## 🛠 Tecnologias

### Backend
- **Java 21** - Linguagem principal
- **Spring Boot 3.5.6** - Framework
- **Spring Security** - Autenticação e autorização
- **Spring Data JPA** - Persistência de dados
- **PostgreSQL** - Banco de dados
- **JWT (jjwt 0.11.5)** - Tokens de autenticação
- **Lombok** - Redução de boilerplate
- **Spring Mail** - Envio de e-mails
- **Bean Validation** - Validação de dados

### Frontend
- **Thymeleaf** - Template engine
- **HTML5 / CSS3** - Estrutura e estilização
- **JavaScript (ES6+)** - Interatividade
- **Fetch API** - Comunicação com backend

### Testes
- **JUnit 5** - Framework de testes
- **Mockito** - Mocking de dependências
- **Spring Security Test** - Testes de segurança
- **Spring Boot Test** - Testes de integração

### Ferramentas
- **Maven** - Gerenciamento de dependências
- **Git** - Controle de versão

---

## 📦 Pré-requisitos

Antes de começar, certifique-se de ter instalado:

- ☕ **Java 21** ou superior
- 🐘 **PostgreSQL 15** ou superior
- 🔧 **Maven 3.6+**
- 🌐 **Navegador moderno** (Chrome, Firefox, Edge)

---

## 🚀 Instalação Passo a Passo

### 1️⃣ Clone o Repositório

```bash
git clone <url-do-repositorio>
cd SriLanka-Card
```

### 2️⃣ Configure o Banco de Dados

1. Abra o PostgreSQL e crie um novo banco de dados:

```sql
CREATE DATABASE srilankaCard;
```

2. Edite o arquivo `src/main/resources/application.properties` e ajuste as credenciais:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/srilankaCard
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha
```

### 3️⃣ Configure o E-mail (Opcional)

Para funcionalidades de e-mail funcionarem, edite `application.properties`:

```properties
spring.mail.username=seu_email@gmail.com
spring.mail.password=sua_senha_app
```

> 💡 **Dica**: Use uma senha de aplicativo do Gmail se usar autenticação de dois fatores.

### 4️⃣ Compile o Projeto

```bash
mvn clean install
```

### 5️⃣ Execute a Aplicação

```bash
mvn spring-boot:run
```

Ou execute diretamente a classe `SriLankaCardApplication.java` na sua IDE.

### 6️⃣ Acesse a Aplicação

Abra seu navegador e acesse:

```
http://localhost:8080
```

---

## 🧪 Testes

### Executar Todos os Testes

```bash
mvn test
```

### Executar Testes Específicos

```bash
# Testes de Controller
mvn test -Dtest=UserControllerTest
mvn test -Dtest=AuthControllerTest

# Testes de Service
mvn test -Dtest=UserServiceImpleTest
mvn test -Dtest=AuthServiceTest
```

### Executar Testes com Cobertura

```bash
mvn clean test
```

### Estrutura de Testes

```
src/test/java/com/SriLankaCard/
├── config/
│   └── TestSecurityConfig.java      # Configuração de segurança para testes
├── controller/
│   ├── UserControllerTest.java     # Testes do UserController
│   └── AuthControllerTest.java     # Testes do AuthController
├── service/
│   ├── UserServiceImpleTest.java   # Testes do UserService
│   └── AuthServiceTest.java        # Testes do AuthService
└── SriLankaCardApplicationTests.java # Testes da aplicação
```

### Perfil de Teste

Os testes utilizam o perfil `test` que carrega uma configuração de segurança permissiva (`TestSecurityConfig`), permitindo que os testes funcionem sem autenticação real.

---

## 📁 Estrutura do Projeto

```
SriLanka-Card/
├── src/
│   ├── main/
│   │   ├── java/com/SriLankaCard/
│   │   │   ├── config/              # Configurações (Security, JWT)
│   │   │   ├── controller/          # Controllers REST e Web
│   │   │   ├── dto/                 # Data Transfer Objects
│   │   │   ├── entity/              # Entidades JPA
│   │   │   ├── exception/           # Exceções customizadas
│   │   │   ├── mapper/              # Mappers (Entity ↔ DTO)
│   │   │   ├── repository/          # Repositories JPA
│   │   │   ├── service/             # Lógica de negócio
│   │   │   └── utils/               # Utilitários
│   │   └── resources/
│   │       ├── static/              # Arquivos estáticos
│   │       │   ├── css/             # Estilos
│   │       │   ├── js/              # Scripts JavaScript
│   │       │   └── img/             # Imagens
│   │       ├── templates/           # Templates Thymeleaf
│   │       └── application.properties
│   └── test/                        # Testes
├── pom.xml                          # Dependências Maven
└── README.md
```

---

## 🎨 Funcionalidades

### 👤 Autenticação e Autorização

- ✅ **Cadastro de Usuários** (comum e admin)
- ✅ **Login com JWT**
- ✅ **Recuperação de Senha** (código por e-mail)
- ✅ **Redefinição de Senha**
- ✅ **Ativação/Reativação de Conta** via código de 6 dígitos (expira em 15 min)
- ✅ **Perfil e Senha do Próprio Usuário** via `/users/me`
- ✅ **Roles**: ADMIN, USUARIO

### 🛍 E-commerce

- ✅ **Catálogo de Produtos** (Gift Cards e Jogos)
- ✅ **Categorização Automática** (Jogos, Comida, Música, Serviços)
- ✅ **Carrinho de Compras** integrado
- ✅ **Sistema de Estoque** (Gift Codes)
- ✅ **Busca e Filtros**
- ✅ **Checkout** valida estoque de Gift Codes, cria Pedido e envia seriais por e-mail

### 👨‍💼 Painel Administrativo

- ✅ **Gestão de Usuários** (CRUD completo)
- ✅ **Gestão de Produtos** (CRUD completo)
- ✅ **Geração Automática de Gift Codes**
- ✅ **Controle de Estoque**
- ✅ **Atualização de Status de Usuários**
- ✅ **Histórico de Pedidos** para admins

### 📧 E-mail

- ✅ **E-mail de Boas-vindas**
- ✅ **Confirmação de Pedido** com seriais dos Gift Codes comprados
- ✅ **Código de Recuperação de Senha**
- ✅ **Aviso de Alteração de Perfil/Senha**

---

## 🔌 API Endpoints

### Autenticação e Recuperação

| Método | Endpoint | Descrição | Autenticação |
|--------|----------|-----------|--------------|
| POST | `/auth/login` | Login de usuário | ❌ Público |
| POST | `/auth/forgot-password` | Solicitar código de recuperação | ❌ Público |
| POST | `/auth/reset-password` | Redefinir senha | ❌ Público |

### Ativação de Conta

| Método | Endpoint | Descrição | Autenticação |
|--------|----------|-----------|--------------|
| POST | `/users/send-activation-code` | Enviar/reenviar código de ativação (15 min de validade) | ❌ Público (e-mail já cadastrado) |
| POST | `/users/activate` | Ativar ou reativar conta com código | ❌ Público |

### Usuários

| Método | Endpoint | Descrição | Autenticação |
|--------|----------|-----------|--------------|
| GET | `/users/list` | Listar todos os usuários | ✅ Admin |
| GET | `/users/me` | Obter usuário atual | ✅ Autenticado |
| PATCH | `/users/me` | Atualizar dados e senha do logado | ✅ Autenticado |
| POST | `/users/create-user` | Criar usuário comum (form) | ❌ Público |

### Administração

| Método | Endpoint | Descrição | Autenticação |
|--------|----------|-----------|--------------|
| POST | `/admin/create-user` | Criar usuário com roles | ✅ Admin |
| PUT  | `/admin/update-user/{id}` | Atualizar usuário | ✅ Admin |
| PATCH | `/admin/update-user/{id}/{status}` | Atualizar status | ✅ Admin |
| DELETE | `/admin/delete-user/{id}` | Deletar usuário | ✅ Admin |
| POST | `/admin/gift-codes/gerar` | Gerar Gift Codes para um card | ✅ Admin |

### Produtos (Cards)

| Método | Endpoint | Descrição | Autenticação |
|--------|----------|-----------|--------------|
| GET | `/cards` | Listar todos os cards | ❌ Público |
| GET | `/cards/categoria/{categoria}` | Listar cards por categoria | ❌ Público |
| GET | `/cards/promocao?ativa={true|false}` | Listar cards em promoção | ❌ Público |
| GET | `/cards/{id}` | Buscar card por ID | ❌ Público |
| POST | `/cards/criar-Card` | Criar novo card | ✅ Admin |
| PATCH | `/cards/atualizar/{id}` | Atualizar card | ✅ Admin |
| DELETE | `/cards/deletar/{id}` | Deletar card | ✅ Admin |
| PATCH | `/cards/{id}/promocao/{promo}` | Atualizar promoção | ✅ Admin |

### Carrinho

| Método | Endpoint | Descrição | Autenticação |
|--------|----------|-----------|--------------|
| GET | `/api/carrinho` | Obter carrinho do usuário | ✅ Autenticado |
| POST | `/api/carrinho` | Adicionar item ao carrinho | ✅ Autenticado |
| DELETE | `/api/carrinho/itens/{produtoId}` | Remover item | ✅ Autenticado |
| DELETE | `/api/carrinho` | Limpar carrinho | ✅ Autenticado |
| GET | `/api/carrinho/total` | Obter totais | ✅ Autenticado |

### Pedidos

| Método | Endpoint | Descrição | Autenticação |
|--------|----------|-----------|--------------|
| POST | `/api/pedidos/finalizar` | Finalizar pedido com base no carrinho do usuário | ✅ Autenticado |
| GET | `/api/pedidos/historico` | Histórico de pedidos do usuário logado | ✅ Autenticado |
| GET | `/api/pedidos/admin/historico` | Histórico completo para admins | ✅ Admin |

---

## 🔐 Autenticação JWT

### Como Funciona

1. **Login**: Usuário faz login e recebe um token JWT
2. **Armazenamento**: Token salvo no `localStorage` e cookie
3. **Requisições**: Token enviado no header `Authorization: Bearer <token>`
4. **Validação**: `JwtAuthFilter` valida o token em cada requisição

### Roles Disponíveis

- **ADMIN**: Acesso completo ao sistema
- **USUARIO**: Acesso às compras

### Criar Primeiro Admin

O sistema cria um admin padrão na inicialização usando as variáveis:

```
DEFAULT_ADMIN_EMAIL
DEFAULT_ADMIN_PASSWORD
```

Se já existir usuário com esse e-mail, nada é alterado. Após subir, acesse o painel admin e crie novos administradores apenas pela aba dedicada (não há senha mágica).

---

## 💳 Checkout e Pedidos

- O fluxo de pagamento roda em `/payment` (requer login), carrega o carrinho via `/api/carrinho` e direciona para `/confirmacaoPagamento`.
- Ao abrir a tela de confirmação é chamado `POST /api/pedidos/finalizar`, que valida estoque, cria o pedido e marca Gift Codes como `VENDIDO`.
- Um e-mail é enviado ao comprador com o resumo da compra e os seriais de cada Gift Code; o carrinho é limpo após a conclusão.
- Histórico para o usuário: `GET /api/pedidos/historico` (renderizado em `/meus-pedidos`).
- Histórico para admins: `GET /api/pedidos/admin/historico` (aba "Pedidos" do painel admin).

---

## 🎯 Categorias de Produtos

Os produtos são categorizados automaticamente baseado no nome:

- **🎮 Jogos**: Cyberpunk, GTA, The Witcher, Red Dead, etc.
- **🍔 Comida**: iFood, Uber Eats, Rappi
- **🎵 Música**: Spotify, Apple Music, Deezer
- **🔧 Serviços**: Steam, Netflix, Xbox, PlayStation, etc.

---

## 🗄 Banco de Dados

### Principais Tabelas

- `users` - Usuários do sistema
- `users_funcoes` - Roles dos usuários
- `cards` - Produtos (Gift Cards e Jogos)
- `gift_codes` - Códigos de gift cards
- `carrinhos` - Carrinhos de compra
- `itens_carrinho` - Itens do carrinho
- `pedidos` - Pedidos realizados
- `itens_pedido` - Itens dos pedidos

### Configuração do Schema

O Hibernate está configurado com `ddl-auto=update`, então as tabelas são criadas/atualizadas automaticamente.

---

## 🎨 Páginas Disponíveis

| Rota | Descrição | Autenticação |
|------|-----------|--------------|
| `/` ou `/home` | Página inicial | ❌ Público |
| `/login` | Página de login | ❌ Público |
| `/signup` | Página de cadastro | ❌ Público |
| `/forgot` | Recuperação de senha | ❌ Público |
| `/verify` | Verificar código de recuperação | ❌ Público |
| `/reset-password` | Criar nova senha | ❌ Público |
| `/contato` | Página de contato | ❌ Público |
| `/faq` | Perguntas frequentes | ❌ Público |
| `/sobre` | Sobre o projeto | ❌ Público |
| `/giftcard` | Catálogo de produtos | ❌ Público |
| `/cart` | Carrinho de compras | ✅ Autenticado |
| `/payment` | Checkout | ✅ Autenticado |
| `/confirmacaoPagamento` | Confirmação e disparo do pedido | ✅ Autenticado |
| `/meu-perfil` | Gestão do próprio perfil e senha | ✅ Autenticado |
| `/meus-pedidos` | Histórico de pedidos do usuário | ✅ Autenticado |
| `/home_admin` | Dashboard admin | ✅ Admin |
| `/painel-admin` | Painel admin (usuários, produtos, gift codes, pedidos) | ✅ Admin |
| `/erro` | Página de erro | ❌ Público |

---

## 🧩 Scripts JavaScript Principais

### `api.js`
- Funções de requisição HTTP
- Gerenciamento de token
- API do carrinho

### `header.js`
- Atualização dinâmica do header
- Verificação de autenticação
- Redirecionamento por role

### `giftcards.js`
- Renderização de produtos
- Filtros por categoria
- Modal de detalhes

### `cart.js`
- Carregamento do carrinho
- Adição/remoção de itens
- Cálculo de totais

### `payment.js`
- Carrega itens do carrinho no checkout
- Validação de dados de pagamento e remoção de itens antes da compra

### `confirmacaoPagamento.js`
- Dispara `POST /api/pedidos/finalizar` e limpa o carrinho após sucesso
- Garante que apenas fluxos válidos de pagamento cheguem à confirmação

### `meu-perfil.js`
- Busca e atualiza dados do usuário logado (`/users/me`)
- Troca de senha com validação e feedback visual

### `meus-pedidos.js`
- Lista histórico de pedidos do usuário
- Exibe itens e totais com datas formatadas

### `painel-admin.js`
- Controle de usuários (listagem, criação, exclusão)
- Gestão de produtos e promoções
- Geração de Gift Codes e visualização de pedidos

---

## 🔧 Configurações Importantes

### Porta do Servidor

Padrão: `8080`

Para alterar, edite `application.properties`:

```properties
server.port=8081
```

### Banco de Dados

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/srilankaCard
spring.datasource.username=postgres
spring.datasource.password=sua_senha
```

### JWT

O JWT é configurado automaticamente pelo Spring Security. O token expira em 10 horas.

### Admin padrão

Um administrador é criado na inicialização (exceto no perfil `test`) com as credenciais abaixo (sobrescreva via ambiente):

```properties
app.default-admin.email=${DEFAULT_ADMIN_EMAIL:admin@srilankacard.com}
app.default-admin.password=${DEFAULT_ADMIN_PASSWORD:admin12345678}
```

---

## 🐛 Troubleshooting

### Erro: "Porta 8080 já está em uso"

```bash
# Windows
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# Linux/Mac
lsof -ti:8080 | xargs kill -9
```

Ou altere a porta no `application.properties`.

### Erro: "Banco de dados não encontrado"

1. Verifique se o PostgreSQL está rodando
2. Confirme se o banco `srilankaCard` existe
3. Verifique as credenciais em `application.properties`

### Erro: "Token inválido"

1. Limpe o `localStorage`: `localStorage.clear()`
2. Faça login novamente
3. Verifique se o token não expirou (10 horas)

---

## 📝 Desenvolvimento

### Estrutura de Commits

```
feat: Nova funcionalidade
fix: Correção de bug
docs: Documentação
style: Formatação
refactor: Refatoração
test: Testes
chore: Manutenção
```

### Branches

- `main` - Código de produção
- `develop` - Desenvolvimento
- `feature/*` - Novas funcionalidades

---

## 👥 Contribuindo

1. Fork o projeto
2. Crie uma branch (`git checkout -b feature/MinhaFeature`)
3. Commit suas mudanças (`git commit -m 'feat: Adiciona MinhaFeature'`)
4. Push para a branch (`git push origin feature/MinhaFeature`)
5. Abra um Pull Request

---

## 📄 Licença

Este projeto é acadêmico e foi desenvolvido para fins educacionais.

---

## 👨‍💻 Desenvolvedores

Projeto desenvolvido como trabalho acadêmico.

---

## 🙏 Agradecimentos

- Spring Boot Community
- PostgreSQL Team
- Todos os mantenedores das bibliotecas open-source utilizadas

---

<div align="center">

**Feito com ❤️ usando Spring Boot**

[⬆ Voltar ao topo](#-srilanka-card)

</div>

