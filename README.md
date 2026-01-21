# Desafio Back-end Ultralims

Projeto para vaga de Desenvolvedor Back-end Junior na Ultralims. O projeto foi desenvolvido usando Spring Boot, pois é uma stack que estou estudando constantemente.

O sistema controla:

- Cadastro de amostras laboratoriais
- Evolução de status da amostra
- Histórico de alterações de status
- Validações de regras de negócio
- Filtros e ordenação de amostras
- Interface web para gerenciamento de amostras

A aplicação foi desenvolvida com foco em clareza de regras, testabilidade e boas práticas de arquitetura.

---

## Como executar o projeto

Pré-requisitos:
- Docker
- Docker Compose

### Subir a aplicação

```bash
docker compose up --build
```



## Principais Funcionalidades

### Backend

- Cadastro de novas amostras
- Consulta de amostras com paginação
- Busca parcial por código (case-insensitive)
- Atualização de dados da amostra
- Transições controladas de status:
  - PENDENTE
  - EM_ANALISE
  - CONCLUIDA
  - APROVADA
  - REJEITADA
- Registro automático de histórico de status
- Validação de transições inválidas
- Tratamento de exceções específicas de domínio

### Frontend

- Interface responsiva com HTML5 e CSS3
- Listagem de amostras com paginação
- Filtros avançados:
  - Busca por código (parcial)
  - Filtro por status
  - Filtro por período de datas (com padrão: últimos 30 dias)
- Ordenação multi-coluna (ASC, DESC, sem ordenação)
- Criação de novas amostras com data/hora padrão
- Visualização detalhada de cada amostra
- Histórico de alterações de status
- Gerenciamento de status (Atualizar, Aprovar, Rejeitar)

---

## Regras de Negócio

- Não é permitido cadastrar duas amostras com o mesmo código
- Uma amostra segue o caminho pré-definido:
  - PENDENTE -> EM_ANALISE -> CONCLUIDA -> APROVADA/REJEITADA
- Toda mudança de status gera um registro no histórico
- Transições inválidas lançam exceções específicas
- Amostras aprovadas ou rejeitadas não podem ser alteradas
- Datas de coleta não podem ser futuras
- Campos obrigatórios no cadastro: código, tipo de coleta e data de coleta

---

## Arquitetura da Aplicação

### Estrutura Backend

A aplicação segue a arquitetura em camadas com separação clara de responsabilidades:

- **Controller**: expõe a API REST
- **Request**: DTO de requisição
- **Response**: DTO de resposta
- **Service**: contém as regras de negócio e lógica da aplicação
- **Repository**: acesso a dados via Spring Data JPA
- **Entity**: mapeamento das tabelas do banco de dados
- **Exception**: exceções e tratamento de erros de domínio
- **Mapper**: conversão entre entidades e DTOs
- **Specification**: filtros dinâmicos com JPA Specifications

### Estrutura Frontend

- **HTML**: Estrutura semântica da interface
- **CSS**: Estilos responsivos e componentes visuais
- **JavaScript**: Lógica de interação, requisições AJAX e manipulação do DOM

### Stack Tecnológico

- Java 21
- Spring Boot 3.x
- Spring Data JPA
- Maven
- JUnit 5
- Mockito
- HTML5
- CSS3
- JavaScript Vanilla

---

## Endpoints Principais (API REST)

### Criar Amostra

**POST** /amostras

Cria uma nova amostra no sistema.

Body:

```json
{
  "codAmostra": "AM001",
  "tipoColeta": "SANGUE",
  "dataColeta": "2026-01-20T14:30:00"
}
```

Retorno: Status 201 com dados da amostra criada

### Listar Amostras

**GET** /amostras

Retorna lista paginada de amostras com suporte a filtros e ordenação.

Parâmetros de query:

- `page`: número da página (padrão: 0)
- `size`: registros por página (padrão: 10)
- `sort`: ordenação (ex: dataColeta,desc)
- `codAmostra`: busca parcial por código
- `status`: filtro por status (PENDENTE, EM_ANALISE, CONCLUIDA, APROVADA, REJEITADA)
- `inicio`: data de início do período
- `fim`: data de fim do período

Exemplo:

```
GET /amostras?page=0&size=10&sort=dataColeta,desc&codAmostra=AM&status=PENDENTE&inicio=2025-12-20T00:00:00&fim=2026-01-21T23:59:59
```

Retorno: Objeto Page<AmostraResponse> com paginação

### Obter Detalhes da Amostra

**GET** /amostras/{id}

Retorna os detalhes completos de uma amostra incluindo seu histórico de status.

Parâmetro de path:

- `id`: UUID da amostra

Retorno: Status 200 com AmostraDetalheResponse

### Atualizar Amostra

**PUT** /amostras/{id}

Atualiza os dados de uma amostra. Amostras com status APROVADA ou REJEITADA não podem ser alteradas.

Parâmetro de path:

- `id`: UUID da amostra

Body:

```json
{
  "codAmostra": "AM001",
  "tipoColeta": "URINA",
  "dataColeta": "2026-01-20T10:00:00"
}
```

Retorno: Status 200 com mensagem de sucesso

### Atualizar Status

**PUT** /amostras/atualizar/{id}

Avança o status da amostra para o próximo estágio (PENDENTE -> EM_ANALISE -> CONCLUIDA).

Parâmetro de path:

- `id`: UUID da amostra

Retorno: Status 200 com mensagem de sucesso

### Aprovar Amostra

**PUT** /amostras/aprovar/{id}

Aprova uma amostra que está no status CONCLUIDA.

Parâmetro de path:

- `id`: UUID da amostra

Retorno: Status 200 com mensagem de sucesso

### Rejeitar Amostra

**PUT** /amostras/rejeitar/{id}

Rejeita uma amostra que está no status CONCLUIDA.

Parâmetro de path:

- `id`: UUID da amostra

Retorno: Status 200 com mensagem de sucesso

### Deletar Amostra

**DELETE** /amostras/{id}

Remove uma amostra do sistema.

Parâmetro de path:

- `id`: UUID da amostra

Retorno: Status 200 com mensagem de sucesso

---

## Testes Unitários

A aplicação possui testes unitários desenvolvidos com:

- JUnit 5: Framework de testes
- Mockito: Mocagem de dependências

### Tipos de Testes

- Testes positivos: validam o comportamento esperado
- Testes negativos: validam tratamento de erros e exceções

### Rodar os Testes

```bash
mvn test
```

### Executar com Cobertura

```bash
mvn test -Dmode=coverage
```

## Como Compilar e Executar

### Pré-requisitos

- Java 21 ou superior
- Maven 3.9 ou superior

### Compilar

```bash
mvn clean compile
```

### Executar (via Spring Boot)

```bash
mvn spring-boot:run
```

A aplicação iniciará em `http://localhost:8080`

### Executar (via JAR)

```bash
mvn clean package
java -jar target/ultralims-0.0.1-SNAPSHOT.jar
```

### Rodar Testes

```bash
mvn test
```

## Acessando a Aplicação

Após iniciar a aplicação, acesse:

- Interface Web: http://localhost:8080
- API REST: http://localhost:8080/amostras

Na interface web você encontrará:

- Listagem de amostras com paginação
- Busca por código
- Filtros por status e período de datas
- Ordenação das colunas
- Criação de novas amostras
- Visualização de detalhes das amostras
- Gerenciamento de status

## Padrões e Boas Práticas

- Separação de responsabilidades por camadas
- DTOs para comunicação entre camadas
- Tratamento centralizado de exceções
- Validações de entrada em múltiplas camadas
- Transações gerenciadas pelo Spring
- Specifications para queries dinâmicas
- Testes unitários para regras de negócio
