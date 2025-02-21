# Documentação da API

## Introdução
Esta documentação descreve os endpoints disponíveis na API do sistema, abrangendo as operações de obtenção de empresas pendentes, consulta de status de processamento e geração de relatórios.

## Endpoints

### 1. Pending API
**Base URL:** `/api/pendente`

#### **Obter todas as empresas pendentes**
- **Método:** `GET`
- **Descrição:** Retorna uma lista de empresas pendentes a partir da API externa.
- **Resposta:**
    - `200 OK` - Lista de empresas pendentes no formato JSON.

**Exemplo de resposta:**
```json
[
  {
    "hash": "1234bb13-23d8-4831-ad8a-1234531064c",
    "company": "Arth Tech Ltda."
  },
  {
    "hash": "5678bb13-23d8-4831-ad8a-5678931064c",
    "company": "JSON Tech Ltda."
  }
]
```

---

### 2. Processing API
**Base URL:** `/api/lot`

#### **Consultar status de processamento por hash**
- **Método:** `GET`
- **Parâmetros:**
    - `hash` (String) - Identificador único da empresa.
- **Descrição:** Retorna o status de processamento da empresa associada ao hash fornecido.
- **Resposta:**
    - `200 OK` - Status do processamento no formato JSON.
    - `404 Not Found` - Caso o hash não seja encontrado.

**Exemplo de requisição:**
```
GET /api/lot/1234bb13-23d8-4831-ad8a-1234531064c
```

**Exemplo de resposta:**
```json
{
  "company": "Arth Tech Ltda.",
  "runtime": "3 minutos",
  "processing": "10%",
  "status": "Running"
}
```

---

### 3. Report API
**Base URL:** `/api/rel`

#### **Gerar relatórios de empresas processadas**
- **Método:** `GET`
- **Descrição:** Gera um relatório detalhado das empresas já processadas.
- **Resposta:**
    - `200 OK` - Relatório gerado no formato JSON.

**Exemplo de resposta:**
```json
{
  "empresas": [
    {
      "country": "brasil",
      "count_employee": 167,
      "size_file": "150.62 mb",
      "runtime": "15 minutos",
      "count_employee_city": {
        "city": "Porto Alegre",
        "state": "RS",
        "kids": 12,
        "young": 55,
        "adult": 100
      },
      "count_employee_state": {
        "state": "RS",
        "kids": 12,
        "young": 55,
        "adult": 100
      }
    }
  ]
}
```

---

## 1. ApiService
### Descrição
O `ApiService` é responsável por buscar empresas pendentes em uma API externa e iniciar o processamento dessas empresas.

### Dependências
- `RestTemplate`: Utilizado para fazer chamadas HTTP para a API externa.
- `ProcessingService`: Usado para iniciar o processamento das empresas obtidas.
- `@Value("${api.external.pending-url}")`: Define a URL da API externa a ser consumida.

### Métodos
#### `fetchPendingCompanies() : List<CompanyDto>`
Busca dados de empresas pendentes na API externa e inicia o processamento.
- Desativa a validação SSL.
- Faz a requisição GET na API externa.
- Converte a resposta para uma lista de `CompanyDto`.
- Inicia o processamento da empresa no `ProcessingService`.

#### `disableSSLCertificateValidation()`
Desativa a validação de certificados SSL para permitir conexões HTTPs não seguras.

---
## 2. ProcessingService
### Descrição
O `ProcessingService` é responsável por processar os dados de empresas, incluindo download e parsing de arquivos CSV.

### Dependências
- `FileDownloader`: Responsável por baixar arquivos CSV.
- `CsvParser`: Responsável por analisar os dados do CSV.
- `@Value("${processing.file.temp-dir}")`: Define o diretório temporário para armazenamento dos arquivos baixados.

### Métodos
#### `startProcessing(ExternalApiResponse.CompanyData company)`
Processa os dados de uma empresa de forma assíncrona.
- Atualiza o status do processamento.
- Faz download do arquivo CSV.
- Analisa os dados e os armazena.
- Atualiza a progressão e status do processamento.

#### `getProcessingStatus(String hash) : ProcessingStatusDto`
Retorna o status atual do processamento de uma empresa.

#### `getProcessedEmployees(String hash) : List<EmployeeDto>`
Retorna os empregados processados para uma determinada empresa.

#### `getAllProcessingContexts() : Map<String, ProcessingContext>`
Retorna todos os contextos de processamento armazenados.

---
## 3. ReportService
### Descrição
O `ReportService` gera relatórios com base nos dados processados das empresas.

### Dependências
- `ProcessingService`: Utilizado para obter os dados das empresas processadas.

### Métodos
#### `generateReport() : ReportDto`
Gera um relatório consolidado das empresas processadas.
- Agrupa os dados por país.
- Calcula a quantidade de empregados por cidade e estado mais populosos.
- Retorna um objeto `ReportDto` contendo os dados gerados.

# Documentação dos Services

## 1. ApiService
### Descrição
O `ApiService` é responsável por buscar empresas pendentes em uma API externa e iniciar o processamento dessas empresas.

### Dependências
- `RestTemplate`: Utilizado para fazer chamadas HTTP para a API externa.
- `ProcessingService`: Usado para iniciar o processamento das empresas obtidas.
- `@Value("${api.external.pending-url}")`: Define a URL da API externa a ser consumida.

### Métodos
#### `fetchPendingCompanies() : List<CompanyDto>`
Busca dados de empresas pendentes na API externa e inicia o processamento.
- Desativa a validação SSL.
- Faz a requisição GET na API externa.
- Converte a resposta para uma lista de `CompanyDto`.
- Inicia o processamento da empresa no `ProcessingService`.

#### `disableSSLCertificateValidation()`
Desativa a validação de certificados SSL para permitir conexões HTTPs não seguras.


---
## 4. DTOs
### 4.1 CompanyDto
#### Descrição
Representa os dados básicos de uma empresa.

#### Campos
- `hash` (String) - Identificador único da empresa.
- `company` (String) - Nome da empresa.

---
### 4.2 EmployeeDto
#### Descrição
Representa os dados de um empregado, extraídos de um arquivo CSV.

#### Campos
- `fullName` (String) - Nome completo do empregado.
- `ssn` (String) - Número de segurança social.
- `email` (String) - Endereço de e-mail.
- `phone` (String) - Número de telefone.
- `address` (String) - Endereço residencial.
- `city` (String) - Cidade.
- `state` (String) - Estado.
- `zipCode` (String) - Código postal.
- `dateOfBirth` (String) - Data de nascimento no formato M/d/yyyy, d/M/yyyy ou yyyy-MM-dd.

#### Métodos auxiliares
- `getAge()` - Calcula a idade do empregado com base na data de nascimento.
- `getAgeGroup()` - Retorna a faixa etária do empregado (`kids`, `young`, `adult`).

---
### 4.3 ProcessingStatusDto
#### Descrição
Representa o status de processamento de uma empresa.

#### Campos
- `company` (String) - Nome da empresa.
- `runtime` (String) - Tempo de execução do processamento.
- `processing` (String) - Status detalhado do processamento.
- `status` (String) - Status geral (`PENDING`, `RUNNING`, `COMPLETED`).

#### Enum `Status`
- `PENDING` - Processamento pendente.
- `RUNNING` - Processamento em andamento.
- `COMPLETED` - Processamento concluído.

---
### 4.4 ReportDto
#### Descrição
Representa um relatório consolidado das empresas processadas.

#### Campos
- `empresas` (List<CountryReport>) - Lista de relatórios por país.

#### Subclasses
- **`CountryReport`**
    - `country` (String) - Nome do país.
    - `count_employee` (int) - Número total de empregados.
    - `size_file` (String) - Tamanho do arquivo processado.
    - `runtime` (String) - Tempo de execução.
    - `count_employee_city` (CityEmployeeCount) - Contagem de empregados por cidade.
    - `count_employee_state` (StateEmployeeCount) - Contagem de empregados por estado.

- **`CityEmployeeCount`**
    - `city` (String) - Nome da cidade.
    - `state` (String) - Nome do estado.
    - `kids` (int) - Número de crianças.
    - `young` (int) - Número de jovens.
    - `adult` (int) - Número de adultos.

- **`StateEmployeeCount`**
    - `state` (String) - Nome do estado.
    - `kids` (int) - Número de crianças.
    - `young` (int) - Número de jovens.
    - `adult` (int) - Número de adultos.


