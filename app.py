from flask import Flask, request, jsonify
from flask_sqlalchemy import SQLAlchemy
from openai import OpenAI
import logging
import requests

from main import buscar_resposta

app = Flask(__name__)

# Configuração do banco de dados
app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///xxxxxxxx.db'
app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False
db = SQLAlchemy(app)

# Configuração do logging
logging.basicConfig(level=logging.INFO)

# Modelo para armazenar perguntas e respostas
class PerguntaResposta(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    pergunta = db.Column(db.String(255), unique=True, nullable=False)
    resposta = db.Column(db.Text, nullable=False)

# Cria o banco de dados e a tabela se não existirem
with app.app_context():
    db.create_all()

# Configuração do cliente OpenAI
client = OpenAI(base_url="http://localhost:1234/v1/chat/completions", api_key="lm-studio")

@app.route('/responder', methods=['POST'])
def responder():
    try:
        data = request.get_json()  # Captura o JSON enviado na requisição
        logging.info(f"Dados recebidos: {data}")

        pergunta = data.get('pergunta', '').strip()  # Extrai a pergunta do JSON
        if not pergunta:
            return jsonify({'resposta': 'Pergunta não pode estar vazia.'}), 400

        # Aqui você chama a função buscar_resposta
        resposta = buscar_resposta(pergunta)

        return jsonify({'resposta': resposta}), 200  # Retorna a resposta

    except Exception as e:
        logging.error(f"Ocorreu um erro: {str(e)}")
        return jsonify({'resposta': f"Ocorreu um erro: {str(e)}"}), 500

if __name__ == '__main__':
    try:
        app.run(host='0.0.0.0', port=5000)
    except Exception as e:
        logging.error(f"Ocorreu um erro ao iniciar o servidor: {e}")
