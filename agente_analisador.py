from openai import OpenAI
import requests


def agente_analisador(conteudos):
    # Configuração do cliente apontando para o servidor local
    client = OpenAI(base_url="http://localhost:1234/v1/chat/completions", api_key="lm-studio")

    # Lista para armazenar o histórico de mensagens
    mensagens = []

    # Função para gerar resposta com base no conteúdo
    def gerar_resposta(conteudo, pergunta):
        # Adiciona o conteúdo inicial como uma mensagem do sistema
        if not mensagens:  # Apenas adiciona a mensagem do sistema uma vez
            mensagens.append({"role": "system", "content": "Sempre responda com base no conteúdo fornecido."})

        # Montar a mensagem do usuário
        mensagens.append({"role": "user", "content": f"{conteudo}\n\nPergunta: {pergunta}"})

        # Enviar as mensagens acumuladas para o modelo
        response = requests.post(
            "http://localhost:1234/v1/chat/completions",
            json={"messages": mensagens},  # Envia o histórico de mensagens
            headers={"Content-Type": "application/json"}
        )

        if response.status_code == 200:
            resposta = response.json().get("resposta")  # Ajuste conforme o que o servidor retorna

            # Armazena a resposta do modelo no histórico
            mensagens.append({"role": "assistant", "content": resposta})
            return resposta
        else:
            return f"Erro: {response.status_code} - {response.text}"

    # Exemplo de uso da função
    for conteudo in conteudos:
        resposta = gerar_resposta(conteudo, "Me diga os tipos de dados, por favor.")
        print(resposta)

        # Pergunta adicional baseada na resposta anterior
        nova_pergunta = "Pode me dar um exemplo dos tipos de dados mencionados?"
        resposta_nova = gerar_resposta(conteudo, nova_pergunta)
        print(resposta_nova)
