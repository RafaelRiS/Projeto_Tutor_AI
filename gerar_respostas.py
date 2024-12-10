import requests

def enviar_para_modelo(conteudo, pergunta):
    url = "http://localhost:1234/v1/chat/completions"

    # Montar o payload para a requisição
    payload = {
        "model": "xxxxxxxxxxxxxxxxxxxxx",
        "messages": [
            {"role": "system",
             "content": (f"Contexto: '{conteudo}'. Não diga a pergunta na resposta por favor.")},
            {"role": "user", "content": f"Por favor USE SOMENTE AS INFORMAÇÕES CONTIDAS NO CONTEXTO FORNECIDO e não printe a pergunta, não use expressões como 'No contexto fornecido' e 'Como foi mencionado anteriormente no contexto'. Se possível apresente um exemplo de código em python e responda a Pergunta '{pergunta}'. Responda estritamente com base no contexto, não use o termo `Ah, essa é fácil`, `Para responder sua pergunta...`, 'Vamos resolver sua pergunta' ou `Em relação...`, não usar a palavra 'contexto', responda somente em português, seja claro e objetivo e use emojis para deixar a resposta mais convidativa, NÃO USE * de forma alguma."}
        ],
        "temperature": 0.8,  # Mantenha a temperatura um pouco mais alta para maior criatividade
        "max_tokens": 20000,
    }

    # Enviar a requisição POST
    response = requests.post(url, json=payload)

    # Retornar a resposta do modelo
    if response.status_code == 200:
        resposta = response.json().get('choices', [{}])[0].get('message', {}).get('content', '').strip()

        if resposta:
            return resposta
        else:
            return "Desculpe, não consegui gerar uma resposta."
    else:
        return "Erro ao acessar o modelo."



