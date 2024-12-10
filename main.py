import psycopg2
from gerar_respostas import enviar_para_modelo
from agente_analisador import agente_analisador



def conectar_db():
    try:
        conn = psycopg2.connect(
            dbname='xxxxxxx',
            user='xxxxxxxx',
            password='xxxxxxxxxx',
            host='xxxxxxxx',
            port='0000'
        )
        print("Conexão ao banco de dados estabelecida com sucesso.")
        return conn
    except Exception as e:
            print(f"Erro ao conectar ao banco de dados: {e}")
            return None


def buscar_resposta(pergunta):
    conn = conectar_db()
    if conn is None:
        return "Erro ao conectar ao banco de dados."

    try:
        cursor = conn.cursor()
        print("Buscando conteúdo na tabela xxxxxxx...")
        cursor.execute("SELECT conteúdo FROM xxxxxxxxxx;")
        conteudos_pocket = [item[0] for item in cursor.fetchall()]
        print(f"Conteúdos encontrados (xxxxx): {len(conteudos_pocket)}")

        print("Buscando conteúdo na tabela xxxxxxxxx...")
        cursor.execute("SELECT conteúdo FROM xxxxxxxxxxxxxx;")
        conteudos_ai = [item[0] for item in cursor.fetchall()]
        print(f"Conteúdos encontrados (AI): {len(conteudos_ai)}")

        # Concatenando todos os conteúdos em uma única lista
        todos_conteudos = conteudos_pocket + conteudos_ai

        # Se não houver conteúdo encontrado, retorne uma mensagem padrão
        if not todos_conteudos:
            return "Desculpe, não consegui encontrar o conteúdo que você procura."

        print("Enviando conteúdos para o modelo...")
        resposta_final = enviar_para_modelo("\n".join(todos_conteudos), pergunta)
        print("Resposta recebida do modelo.")

        # Formatar a resposta para manter emojis
        resposta_final = resposta_final.strip()
        return resposta_final

    except Exception as e:
        print(f"Erro ao buscar resposta: {e}")
        return "Erro ao buscar resposta."

    finally:
        cursor.close()
        conn.close()


if __name__ == '__main__':
    try:
        while True:  # Mantém o loop rodando
            pergunta = input('Digite aqui sua pergunta/ necessidade (ou "sair" para encerrar):\n')
            if pergunta.lower() == "sair":
                print("Encerrando o programa.")
                break  # Sai do loop se o usuário digitar "sair"
            resposta = buscar_resposta(pergunta)
            print("Resposta:", resposta)

    except Exception as e:
        print(f"Ocorreu um erro ao executar o main.py: {e}")