# **Tutor AI - Documentação**

**Tutor AI** é um sistema de inteligência artificial que permite ao usuário interagir com o modelo por meio de perguntas. O sistema consulta dados armazenados em um banco de dados, envia essas informações para um modelo de IA para obter respostas e retorna respostas personalizadas, utilizando dados do banco e o modelo OpenAI. O sistema também é implementado com uma interface web via Flask, permitindo comunicação por API.

## **Estrutura do Projeto**

O projeto é dividido em várias partes:

1. **Agente Analisador (`agente_analisador.py`)**: Responsável por buscar dados no banco de dados e interagir com o modelo de IA.
2. **API Flask (`app.py`)**: Um servidor Flask que fornece uma API para o front-end ou outros serviços consumirem.
3. **Gerar Respostas (`gerar_respostas.py`)**: Componente responsável por formatar e enviar as perguntas para o modelo de IA.
4. **Funções principais (`main.py`)**: Arquivo que contém a lógica para interagir com o banco de dados e enviar as respostas para o modelo de IA.

---

## **Requisitos**

- **Python 3.x**
- **Flask** para a API web.
- **SQLAlchemy** para interagir com o banco de dados.
- **psycopg2** para interação com o PostgreSQL.
- **OpenAI Python SDK** (ou uma alternativa para o modelo de IA).
- **requests** para fazer chamadas HTTP para o modelo de IA.

---

## **Instalação**

### 1. **Instalar Dependências**

Certifique-se de que você tem o Python 3 instalado. Em seguida, crie um ambiente virtual e instale as dependências do projeto.

```bash
python3 -m venv venv
source venv/bin/activate  # Para sistemas Unix
venv\Scripts\activate     # Para sistemas Windows
pip install -r requirements.txt
```

# **Tutor AI App**

**Tutor AI App** é uma aplicação web que utiliza a arquitetura de um modelo de IA para responder a perguntas enviadas via API. A aplicação é construída com **Flask** para fornecer uma interface de API e **SQLAlchemy** para interação com um banco de dados SQLite. O sistema responde a perguntas com base nos dados armazenados no banco de dados e interage com um modelo de IA para gerar respostas.

## **Estrutura do Projeto**

O projeto é composto pelas seguintes partes:

1. **API Flask (`app.py`)**: Um servidor Flask que expõe a API de perguntas e respostas.
2. **Modelo de Pergunta e Resposta (`PerguntaResposta` no `app.py`)**: Classe que representa perguntas e respostas armazenadas no banco de dados.
3. **Banco de Dados SQLite**: Utilizado para armazenar perguntas e respostas de forma persistente.
4. **Integração com o Modelo de IA (`gerar_respostas.py`)**: Envia dados para o modelo de IA para gerar respostas baseadas nas perguntas.

2. **Configuração do Banco de Dados**
O projeto utiliza o SQLite como banco de dados para armazenar perguntas e respostas. O banco de dados e a tabela são criados automaticamente ao iniciar o aplicativo Flask.

## Arquivos e Funcionalidades
1. app.py
Este arquivo contém a implementação principal do servidor Flask. Ele expõe uma API para interagir com o sistema e armazenar as perguntas e respostas no banco de dados SQLite.

## Funcionalidades:
Configuração do Banco de Dados: A aplicação utiliza o SQLite para armazenar perguntas e respostas.
Rota /responder: Uma rota POST onde o usuário pode enviar uma pergunta. A aplicação então utiliza a função buscar_resposta para processar a pergunta e retornar uma resposta.
2. gerar_respostas.py
Este arquivo contém a função enviar_para_modelo, que envia as perguntas para o modelo de IA e processa a resposta.

A função enviar_para_modelo envia a pergunta para o modelo de IA com o contexto dos dados armazenados, e retorna a resposta gerada pelo modelo.

## Como Executar o Projeto
Passo 1: Inicializar o Banco de Dados
Ao rodar o servidor Flask pela primeira vez, o banco de dados SQLite será automaticamente criado, e as tabelas necessárias serão geradas.

Passo 2: Iniciar o Servidor Flask
No diretório do projeto, execute:

```bash
python app.py
```
O servidor Flask será iniciado e estará disponível em http://localhost:5000.

Passo 3: Enviar Perguntas para a API
Agora você pode interagir com a API usando requisições POST para o endpoint /responder.

A requisição deve ser enviada com o seguinte corpo JSON:

```json
{
  "pergunta": "Sua pergunta aqui"
}

// A resposta será uma JSON contendo a resposta gerada pelo sistema.
```

Passo 4: Testar Localmente
Você pode testar a API diretamente usando ferramentas como o Postman ou cURL para enviar requisições POST.

## Exemplo de requisição com cURL:

```bash
curl -X POST http://localhost:5000/responder -H "Content-Type: application/json" -d '{"pergunta": "Qual é a capital da França?"}'
```

## Estrutura do Banco de Dados
A aplicação utiliza uma tabela no banco de dados SQLite para armazenar perguntas e respostas. A tabela é definida pela classe PerguntaResposta no código.

## A tabela contém os seguintes campos:

id: Chave primária.
pergunta: A pergunta feita pelo usuário.
resposta: A resposta gerada pelo sistema.

## Licença
Este projeto está licenciado sob a Licença MIT.

## Participantes
O projeto Tutor AI contou com a contribuição das seguintes pessoas:


- *Gustavo Gazi*  [![LinkedIn](https://img.shields.io/badge/LinkedIn-0077B5?style=for-the-badge&logo=linkedin&logoColor=white)](https://www.linkedin.com/in/gustavogazi)


- *Isabelle Cunha*  [![LinkedIn](https://img.shields.io/badge/LinkedIn-0077B5?style=for-the-badge&logo=linkedin&logoColor=white)](https://www.linkedin.com/in/cunha-isabelle/)


- *João Paulo*  [![LinkedIn](https://img.shields.io/badge/LinkedIn-0077B5?style=for-the-badge&logo=linkedin&logoColor=white)](https://www.linkedin.com/in/joaogarridob)


## Contribuições
Sinta-se à vontade para contribuir com o projeto. Para isso, basta fazer um fork, criar uma branch com suas modificações e abrir um pull request.
