<div align="center">

# 🚜 Sistema de Checklist de Empilhadeiras

**Aplicação web completa para controle de vistorias diárias de empilhadeiras em ambientes operacional.**

Garante conformidade operacional com bloqueio automático de equipamentos reprovados, rastreabilidade de inspeções e controle de acesso por perfis.

[![Java](https://img.shields.io/badge/Java-17-ED8B00?style=flat-square&logo=openjdk&logoColor=white)](https://openjdk.org/projects/jdk/17/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.4-6DB33F?style=flat-square&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Angular](https://img.shields.io/badge/Angular-21-DD0031?style=flat-square&logo=angular&logoColor=white)](https://angular.dev/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-4169E1?style=flat-square&logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?style=flat-square&logo=docker&logoColor=white)](https://docs.docker.com/compose/)

</div>

---

## 📋 Sobre o Projeto

Este sistema resolve um problema real de segurança operacional: **garantir que nenhuma empilhadeira com defeito seja operada**. O operador preenche o checklist antes de cada turno, e se qualquer item impeditivo (freio, cinto, extintor, etc.) estiver não conforme, a empilhadeira é **bloqueada automaticamente** no sistema, impedindo novos checklists até que um supervisor a libere.

### Funcionalidades principais

- ✅ **Login com perfis** — Operador, Supervisor e Administrador com permissões distintas
- ✅ **Checklist de vistoria** — 5 itens conformes + 16 itens impeditivos de segurança
- ✅ **Bloqueio automático** — empilhadeira bloqueada ao reprovar checklist
- ✅ **Rastreabilidade** — histórico completo por operador, equipamento e período
- ✅ **Rate limiting** — proteção contra ataques de força bruta no login
- ✅ **JWT stateless** — autenticação sem estado, pronta para escalar
- ✅ **Containerizado** — sobe com um único comando Docker

---

## 🖥️ Telas do Sistema

### Tela de Login
> Autenticação com RE (Registro de Empregado) e senha. Animação de logo em vídeo. Navegação por teclado (Enter avança entre campos).

![Login](./.github/screenshots/login.png)

### Formulário de Checklist
> Preenchimento guiado com 5 seções: seleção de empilhadeira, dados da vistoria, itens conformes, itens impeditivos e observações.

![Checklist](.github/screenshots/checklist.svg)

### Alerta de Item Impeditivo
> Ao marcar qualquer item impeditivo como "Não Conforme", um banner vermelho aparece dinamicamente com aviso de bloqueio automático.

![Alerta](.github/screenshots/alerta-imperativo.svg)

> **💡 Nota:** As telas acima são geradas diretamente da aplicação rodando localmente. Siga o guia de instalação abaixo para ver ao vivo.

---

## 🛠️ Tecnologias Utilizadas

### Backend
| Tecnologia | Versão | Função |
|---|---|---|
| Java | 17 | Linguagem principal |
| Spring Boot | 3.4 | Framework web e IoC |
| Spring Security | 6 | Autenticação e autorização |
| Spring Data JPA | 3.4 | ORM e acesso a dados |
| jjwt | 0.12.6 | Geração e validação de tokens JWT |
| Bucket4j | 8.14 | Rate limiting por IP |
| BCrypt | strength 12 | Hash seguro de senhas |
| Lombok | latest | Redução de boilerplate |

### Frontend
| Tecnologia | Versão | Função |
|---|---|---|
| Angular | 21.1 | Framework SPA |
| Angular Material | 21.1 | Componentes UI (Material Design) |
| TypeScript | 5.9 | Linguagem tipada |
| Reactive Forms | — | Formulários reativos com validação |
| RxJS | 7.8 | Programação reativa / Observables |

### Infraestrutura
| Tecnologia | Função |
|---|---|
| PostgreSQL 16 | Banco de dados relacional |
| Docker + Compose | Containerização e orquestração |
| Nginx Alpine | Servidor web / proxy reverso |
| Maven 3.9 | Build do backend |

### Arquitetura de segurança implementada
- 🔐 JWT com algoritmo HS256 explícito
- 🛡️ Rate limiting — 5 tentativas de login por IP por minuto
- 🔒 BCrypt com strength 12 (4× mais lento que o padrão)
- 🚫 Headers HTTP de segurança (HSTS, CSP, X-Frame-Options)
- 🔍 Proteção contra IDOR em endpoints de checklist
- 🌐 CORS restrito a origens explícitas
- 📦 Nginx com `server_tokens off` e limites de timeout

---

## 🏗️ Arquitetura

```
┌─────────────────────────────────────────────────────────┐
│                     Usuário (Browser)                    │
└─────────────────────────┬───────────────────────────────┘
                          │ HTTP :80
                          ▼
┌─────────────────────────────────────────────────────────┐
│               Nginx (Frontend Container)                 │
│  • Serve Angular SPA (HTML/JS/CSS)                       │
│  • Proxy /api/* → backend:8080                          │
│  • Security headers (HSTS, CSP, X-Frame-Options)        │
└─────────────────────────┬───────────────────────────────┘
                          │ HTTP interno
                          ▼
┌─────────────────────────────────────────────────────────┐
│           Spring Boot (Backend Container :8080)          │
│  • REST API — /api/*                                     │
│  • JWT Authentication Filter                             │
│  • Rate Limit Filter (Bucket4j)                         │
│  • Spring Security (RBAC por perfil)                    │
└─────────────────────────┬───────────────────────────────┘
                          │ JDBC
                          ▼
┌─────────────────────────────────────────────────────────┐
│            PostgreSQL 16 (DB Container :5432)            │
│  • Porta vinculada em 127.0.0.1 (não pública)           │
│  • Volume persistente                                    │
└─────────────────────────────────────────────────────────┘
```

### Estrutura de pacotes do Backend
```
backend/src/main/java/com/deicmar/checklist/
├── config/         → DataInitializer (seed de dados)
├── controller/     → Endpoints REST (Auth, Checklist, Empilhadeira, Usuario)
├── dto/            → Request e Response objects
├── exception/      → BusinessException, GlobalExceptionHandler
├── mapper/         → Conversão Entity ↔ DTO
├── model/
│   ├── entity/     → Checklist, Empilhadeira, ItemChecklist, Usuario
│   └── enums/      → Perfil, Turno, TipoItem, StatusItem, ResultadoChecklist
├── repository/     → Interfaces Spring Data JPA
├── security/       → JwtUtil, JwtFilter, RateLimitFilter, SecurityConfig
└── service/        → Regras de negócio (Auth, Checklist, Empilhadeira, Usuario)
```

---

## 🚀 Como Rodar

### Opção 1 — Docker (recomendado, zero configuração)

**Pré-requisitos:**
- [Docker Desktop](https://www.docker.com/products/docker-desktop/) instalado e rodando

**Passo a passo:**

```bash
# 1. Clone o repositório
git clone https://github.com/seu-usuario/checklist-empilhadeiras.git
cd checklist-empilhadeiras

# 2. Suba todos os containers
docker-compose up --build
```

> ⏱️ **Primeira execução:** o Maven baixa as dependências e o npm instala os pacotes. Aguarde 3–5 minutos.

```bash
# 3. Quando aparecer "Started ChecklistApplication", acesse:
#    http://localhost
```

**Para parar:**
```bash
docker-compose down
```

**Para parar e apagar os dados do banco:**
```bash
docker-compose down -v
```

---

### Opção 2 — Desenvolvimento local (backend + frontend separados)

**Pré-requisitos:**
- Java 17+ (`java -version`)
- Maven 3.9+ (`mvn -version`)
- Node.js 20+ (`node -v`)
- PostgreSQL rodando localmente

**1. Banco de dados**
```sql
-- No psql ou DBeaver:
CREATE DATABASE checklist_db;
```

**2. Backend**
```bash
cd backend

# Configure a conexão (opcional — o padrão é localhost:5432/checklist_db)
# Edite src/main/resources/application-dev.properties se necessário

mvn spring-boot:run
# API disponível em: http://localhost:8080/api
```

**3. Frontend**
```bash
cd frontend
npm install
npm start
# App disponível em: http://localhost:4200
```

> O frontend em modo dev aponta para `http://localhost:8080/api` (configurado em `src/environments/environment.ts`).

---

## 🔑 Usuários Padrão

> Criados automaticamente na primeira execução.

| RE | Senha | Perfil | Acesso | Permissões |
|---|---|---|---|---|
| `ADMIN` | `admin123` | Administrador | Painel `/admin` | Acesso total — todos os painéis, cria/inativa usuários |
| `GER001` | `senha123` | Gerente de Mecânica | Painel `/admin` | Status e bloqueio/desbloqueio de empilhadeiras, alertas |
| `SUP001` | `senha123` | Supervisor Operacional | Painel `/admin` | Checklists por turno, acompanhamento de operadores |
| `SUP002` | `senha123` | Supervisor Operacional | Painel `/admin` | Checklists por turno, acompanhamento de operadores |
| `OPR001` | `senha123` | Operador | `/checklist` | Cria e consulta seus próprios checklists |
| `OPR002` | `senha123` | Operador | `/checklist` | Cria e consulta seus próprios checklists |
| `313682` | `senha123` | Operador | `/checklist` | Cria e consulta seus próprios checklists |

> ⚠️ **Troque as senhas após o primeiro acesso** em ambientes de produção.

---

## 🖥️ Painel Admin — Tempo Real

Usuários com perfil gerencial são redirecionados automaticamente para `/admin` após o login.

### Perfis e permissões no painel

| Perfil | Empilhadeiras | Desbloquear | Checklists | Usuários | Config |
|---|---|---|---|---|---|
| **Admin** | ✅ Todas | ✅ | ✅ | ✅ | ✅ |
| **Gerente Mecânica** | ✅ Todas | ✅ | ❌ | ❌ | ❌ |
| **Supervisor Operacional** | 🔒 Só bloqueadas | ❌ | ✅ | ❌ | ❌ |
| **Supervisor** | 🔒 Só bloqueadas | ❌ | ✅ | ❌ | ❌ |

### Tempo real com SSE (Server-Sent Events)

O painel recebe atualizações instantâneas sem polling:

- **`empilhadeira_bloqueada`** — ao reprovar um checklist ou bloqueio manual
- **`empilhadeira_desbloqueada`** — ao liberar pelo Gerente/Admin
- **`checklist_salvo`** — ao salvar qualquer vistoria (aprovado ou reprovado)

Os cards de resumo, a tabela de status e a lista de últimas vistorias atualizam automaticamente.


---

## 📡 Endpoints da API

Base URL: `http://localhost:8080/api`

### Autenticação
| Método | Rota | Acesso | Descrição |
|---|---|---|---|
| `POST` | `/auth/login` | Público | Login — retorna token JWT |

### Checklists
| Método | Rota | Acesso | Descrição |
|---|---|---|---|
| `POST` | `/checklists` | Autenticado | Criar checklist |
| `GET` | `/checklists` | Admin/Supervisor | Listar todos |
| `GET` | `/checklists/{id}` | Autenticado | Buscar por ID |
| `GET` | `/checklists/operador/{id}` | Autenticado | Listar por operador |
| `GET` | `/checklists/empilhadeira/{id}` | Admin/Supervisor | Listar por equipamento |
| `GET` | `/checklists/periodo?dataInicio=&dataFim=` | Admin/Supervisor | Filtrar por período |

### Empilhadeiras
| Método | Rota | Acesso | Descrição |
|---|---|---|---|
| `GET` | `/empilhadeiras` | Público | Listar todas |
| `GET` | `/empilhadeiras/disponiveis` | Público | Listar disponíveis (não bloqueadas) |
| `POST` | `/empilhadeiras` | Admin/Supervisor | Cadastrar |
| `PATCH` | `/empilhadeiras/{id}/bloquear` | Admin/Supervisor | Bloquear equipamento |
| `PATCH` | `/empilhadeiras/{id}/desbloquear` | Admin/Supervisor | Desbloquear |
| `DELETE` | `/empilhadeiras/{id}` | Admin | Inativar |

---

## 🚀 Deploy na Railway

### Pré-requisitos
- Conta em [railway.app](https://railway.app)
- CLI instalada: `npm install -g @railway/cli`
- Repositório no GitHub

### Passo a passo

**1. Criar projeto na Railway**
```bash
railway login
railway init
```

**2. Provisionar PostgreSQL**

No painel Railway: `New Service → Database → PostgreSQL`

A Railway injeta `DATABASE_URL` automaticamente no formato `postgres://user:pass@host:port/db`.

**3. Configurar variáveis de ambiente**

No painel: `Service (backend) → Variables → RAW Editor`

```env
SPRING_PROFILES_ACTIVE=prod
DATABASE_URL=jdbc:postgresql://HOST:PORT/DB   # converta de postgres:// para jdbc:postgresql://
DB_USERNAME=postgres
DB_PASSWORD=SENHA_DO_RAILWAY
JWT_SECRET=$(openssl rand -hex 64)
CORS_ORIGINS=https://SEU_FRONTEND.up.railway.app
SWAGGER_ENABLED=false
SEED_ADMIN_SENHA=troque_no_primeiro_acesso
SEED_DEFAULT_SENHA=troque_no_primeiro_acesso
JAVA_TOOL_OPTIONS=-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+UseG1GC
```

> ⚠️ O Railway fornece a URL no formato `postgres://...`. Você precisa converter manualmente para `jdbc:postgresql://...` ao copiar.

**4. Deploy automático**

Configure o deploy automático via GitHub:
- Railway: `Settings → Source → Connect GitHub repo`
- Cada push na branch `main` dispara um novo deploy automaticamente.

**5. Comandos manuais**

```bash
# Build e deploy
cd backend
railway up --service backend

# Ver logs em tempo real
railway logs --service backend

# Abrir a aplicação
railway open
```

### Health check

A Railway usa `/api/actuator/health` para verificar se o serviço está saudável.

### Swagger UI (desenvolvimento)

```
https://SEU_BACKEND.up.railway.app/api/swagger-ui.html
```

> Desabilitado em produção por padrão (`SWAGGER_ENABLED=false`).


---

## 🔐 Variáveis de Ambiente

O sistema funciona **sem configuração adicional** em desenvolvimento (valores padrão embutidos). Para produção, crie um arquivo `.env` na raiz:

```bash
cp .env.example .env
# Edite o .env com valores seguros
```

| Variável | Padrão (dev) | Descrição |
|---|---|---|
| `DB_USERNAME` | `postgres` | Usuário do PostgreSQL |
| `DB_PASSWORD` | `admin` | Senha do PostgreSQL |
| `JWT_SECRET` | *(chave de dev)* | Chave HMAC-SHA256 (mín. 32 chars) |
| `JWT_EXPIRATION` | `86400000` | Expiração do token em ms (24h) |
| `DDL_AUTO` | `update` | Use `validate` em produção |
| `SEED_ADMIN_SENHA` | `admin123` | Senha inicial do admin |
| `SEED_DEFAULT_SENHA` | `senha123` | Senha inicial dos operadores |

> 🔑 **Gere um JWT_SECRET seguro:**
> ```bash
> openssl rand -hex 64
> ```

---

## 🗄️ Modelo de Dados

```
usuarios
├── id, re (único), nome_completo
├── senha (BCrypt), perfil (ADMIN/SUPERVISOR/OPERADOR)
└── ativo (soft delete)

empilhadeiras
├── id, modelo, tipo, capacidade
├── bloqueada (boolean), motivo_bloqueio
└── ativa (soft delete)

checklists
├── id, data, hora_vistoria, turno (A/B/C)
├── horimetro_inicial, horimetro_final
├── operador_id (FK), empilhadeira_id (FK)
├── resultado (APROVADO/REPROVADO)
└── observacao_geral

itens_checklist
├── id, descricao
├── tipo (CONFORME/IMPEDITIVO)
├── status (OK/NAO_CONFORME)
├── observacao
└── checklist_id (FK)
```

---

## 🧩 Regras de Negócio

1. **Bloqueio automático** — ao salvar um checklist com qualquer item do tipo `IMPEDITIVO` marcado como `NAO_CONFORME`, a empilhadeira é bloqueada automaticamente e fica indisponível para novos checklists.

2. **Um checklist por dia** — não é possível criar dois checklists para a mesma empilhadeira na mesma data.

3. **IDOR protegido** — operadores só conseguem criar ou visualizar seus próprios checklists; tentativas de acessar checklists de outros operadores retornam erro.

4. **Soft delete** — usuários e empilhadeiras nunca são deletados fisicamente, apenas marcados como inativos.

---


## 📄 Licença

Este projeto está sob a licença MIT. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.

---

<div align="center">

Desenvolvido por **Marcelo Santos** · [LinkedIn](https://linkedin.com/in/seu-perfil) · [GitHub](https://github.com/seu-usuario)

</div>
"# ckecklist_empilhadeira_pp"  
