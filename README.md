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
