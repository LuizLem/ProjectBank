# Guia de Configuração: Sistema de Login

## 📋 Pré-requisitos

Antes de iniciar a aplicação, você precisa ter instalado:

1. **Java 17** ou superior
2. **Maven 3.6+**
3. **PostgreSQL 12+**
4. **Git**

---

## 🗄️ Passo 1: Criar o Banco de Dados PostgreSQL

Você precisa criar um banco de dados chamado `demo_db` no PostgreSQL.

### Opção A: Usar pgAdmin (Interface Gráfica)
1. Abra pgAdmin
2. Clique com botão direito em "Databases"
3. Selecione "Create" → "Database"
4. Digite o nome: `demo_db`
5. Clique em "Save"

### Opção B: Usar Terminal/PowerShell

```bash
# Abra o terminal do PostgreSQL
psql -U postgres

# Dentro do psql, execute:
CREATE DATABASE demo_db;

# Para verificar se foi criado:
\l

# Para sair:
\q
```

**Resultado esperado:**
```
 demo_db | postgres | UTF8 | en_US.UTF-8 | en_US.UTF-8 |
```

---

## ⚙️ Passo 2: Configurar o Arquivo `application.properties`

Abra o arquivo `src/main/resources/application.properties` e adicione as seguintes configurações:

```properties
# ==========================================
# BANCO DE DADOS - PostgreSQL
# ==========================================
spring.datasource.url=jdbc:postgresql://localhost:5432/demo_db
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.datasource.driver-class-name=org.postgresql.Driver

# ==========================================
# JPA / HIBERNATE
# ==========================================
# Atualiza o banco automaticamente baseado nas entidades
spring.jpa.hibernate.ddl-auto=update

# Não mostra SQL no console (em desenvolvimento pode colocar true)
spring.jpa.show-sql=false

# Dialeto específico do PostgreSQL
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# Formata o SQL para ficar mais legível
spring.jpa.properties.hibernate.format_sql=true

# ==========================================
# JWT - Autenticação
# ==========================================
# Chave secreta para assinar os tokens (NUNCA coloque no GitHub!)
# IMPORTANTE: Mude isso em produção para uma chave muito aleatória e segura!
app.jwt.secret=sua_chave_super_secreta_desenvolvimento_minimo_32_caracteres_aleatorios

# Tempo de expiração do token em milissegundos
# 3600000 = 1 hora
# 86400000 = 24 horas
# 604800000 = 7 dias
app.jwt.expiration=3600000

# ==========================================
# LOGGING
# ==========================================
# Nível de log geral
logging.level.root=INFO

# Nível de log específico da aplicação (mostra mais detalhes)
logging.level.com.example.demo=DEBUG

# Formato do log
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss} - %logger{36} - %msg%n

# ==========================================
# SERVER
# ==========================================
# Porta da aplicação
server.port=8080

# Contexto raiz
server.servlet.context-path=/
```

---

## 🚀 Passo 3: Build do Projeto

Abra o terminal na pasta do projeto e execute:

```bash
mvn clean install
```

**O que isso faz:**
- `clean` → Remove arquivos compilados anteriores
- `install` → Baixa dependências e compila o projeto

**Tempo esperado:** 2-5 minutos (primeira vez é mais lento)

**Resultado esperado:**
```
BUILD SUCCESS
```

---

## ▶️ Passo 4: Executar a Aplicação

```bash
mvn spring-boot:run
```

Ou execute a classe `DemoApplication.java` direto do VS Code clicando no botão ▶️

**Resultado esperado no console:**
```
2024-11-18 10:30:00 - com.example.demo.DemoApplication - Started DemoApplication in 5.123 seconds (JVM running for 5.456)
```

---

## 🧪 Passo 5: Testar os Endpoints

Use **Postman**, **Insomnia** ou **REST Client** (extensão do VS Code) para testar:

### **1. Health Check** ✅
```
GET http://localhost:8080/api/auth/health
```

**Resposta esperada (200 OK):**
```
Auth service is running
```

---

### **2. Registrar Novo Usuário** ✅
```
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
  "name": "João Silva",
  "email": "joao@example.com",
  "password": "senha123",
  "passwordConfirm": "senha123"
}
```

**Resposta esperada (201 CREATED):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "name": "João Silva",
  "email": "joao@example.com",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "expiresIn": 3600
}
```

---

### **3. Fazer Login** ✅
```
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "email": "joao@example.com",
  "password": "senha123"
}
```

**Resposta esperada (200 OK):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "name": "João Silva",
  "email": "joao@example.com",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "expiresIn": 3600
}
```

---

### **4. Buscar Dados do Usuário** ✅
```
GET http://localhost:8080/api/users/{id}

Exemplo:
GET http://localhost:8080/api/users/550e8400-e29b-41d4-a716-446655440000
```

**Resposta esperada (200 OK):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "name": "João Silva",
  "email": "joao@example.com",
  "active": true,
  "createdAt": "2024-11-18T10:30:00",
  "updatedAt": "2024-11-18T10:30:00"
}
```

---

## ❌ Possíveis Erros e Soluções

### **Erro: "Connection refused"**
```
Caused by: java.net.ConnectException: Connection refused
```
**Solução:** PostgreSQL não está rodando
- Windows: Abra "Services" e inicie "postgresql-x64-XX"
- Mac: `brew services start postgresql`
- Linux: `sudo systemctl start postgresql`

---

### **Erro: "database "demo_db" does not exist"**
```
Caused by: org.postgresql.util.PSQLException: ERROR: database "demo_db" does not exist
```
**Solução:** Você esqueceu de criar o banco
- Siga o Passo 1 novamente

---

### **Erro: "password authentication failed"**
```
Caused by: org.postgresql.util.PSQLException: FATAL: password authentication failed for user "postgres"
```
**Solução:** Senha do PostgreSQL está errada
- Verifique se `spring.datasource.password=postgres` está correto
- Tente resetar a senha do PostgreSQL

---

### **Erro: "Validation failed for query for method"**
```
java.lang.IllegalArgumentException: Validation failed for query for method public
```
**Solução:** Erro de validação nos DTOs
- Verifique se os campos obrigatórios estão sendo enviados
- Exemplo: Email deve ser válido, senha mínimo 6 caracteres

---

## 📝 Checklist de Funcionamento

- [ ] PostgreSQL está instalado e rodando
- [ ] Banco de dados `demo_db` foi criado
- [ ] `application.properties` foi configurado
- [ ] `mvn clean install` executou com sucesso
- [ ] Aplicação está rodando (sem erros no console)
- [ ] Health check retorna sucesso
- [ ] Conseguiu registrar um usuário
- [ ] Conseguiu fazer login
- [ ] Token JWT foi gerado

---

## 🔐 Segurança - IMPORTANTE!

⚠️ **Nunca faça isso em produção:**
- Usar `ddl-auto=update` (use `validate`)
- Deixar `show-sql=true`
- Colocar a chave JWT no GitHub
- Usar senha padrão do PostgreSQL

---

## 📚 Próximos Passos

1. **Implementar JWT Filter** → Validar token nas requisições
2. **Implementar Exception Handler Global** → Tratamento de erros
3. **Implementar CORS Config** → Permitir requisições do Angular
4. **Criar testes unitários** → Garantir qualidade
5. **Implementar Refresh Token** → Renovar token expirado

---

## 🆘 Precisa de Ajuda?

Se algo não funcionar:
1. Verifique o console da aplicação
2. Procure por mensagens de erro
3. Verifique o `application.properties`
4. Confirme que PostgreSQL está rodando
5. Tente fazer `mvn clean install` novamente
