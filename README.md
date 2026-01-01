Aqui está o conteúdo completo para o seu arquivo `README.md`. Ele foi elaborado para atender aos requisitos da entrega do trabalho, descrevendo tecnicamente o app e a API utilizada.

Você pode copiar o código abaixo e colar diretamente em um arquivo chamado `README.md` na raiz do seu projeto no GitHub.

```markdown
# App Background Remover

Este projeto é uma aplicação Android nativa desenvolvida como parte da avaliação da disciplina de Programação para Dispositivos Móveis. O objetivo é implementar um sistema distribuído (Cliente-Servidor) onde o app consome uma API REST pública para realizar o processamento de imagens.

## 📱 Descrição do que foi Programado

O aplicativo funciona como um cliente móvel que interage com um serviço externo de Inteligência Artificial para remover o fundo de fotos automaticamente. A aplicação foi construída utilizando **Kotlin** e **Jetpack Compose**, seguindo as práticas de *Modern Android Development (MAD)*.

### Funcionalidades e Fluxo
A aplicação atende aos requisitos do trabalho (Consumo de API, Persistência Local e Múltiplas Telas) através do seguinte fluxo:

1.  **Galeria Interna (Tela Inicial):**
    * O app lista todas as imagens que já foram processadas e salvas no armazenamento privado do dispositivo.
    * Utiliza `LazyVerticalGrid` para exibição otimizada.
    * Permite visualizar a imagem em tela cheia (usando `FileProvider`) ou iniciar um novo processo.

2.  **Processamento (Tela de Remoção):**
    * O usuário seleciona uma imagem da galeria do celular (usando `Photo Picker`).
    * A imagem é enviada para o servidor via requisição HTTP Multipart.
    * O app gerencia estados de UI (`Idle`, `Loading`, `Success`, `Error`) para feedback visual ao usuário.

3.  **Persistência (Tela de Salvamento):**
    * Após o retorno da API, o usuário visualiza o resultado (imagem recortada).
    * O usuário define um nome personalizado para o arquivo.
    * **Dados Locais:** O app salva a imagem física (`.png`) no armazenamento interno e registra metadados (Nome e Data de Criação) utilizando `SharedPreferences` e serialização JSON.

### Stack Tecnológica
* **Linguagem:** Kotlin
* **Interface:** Jetpack Compose (Material3)
* **Rede:** Retrofit 2 + OkHttp + Gson
* **Assincronismo:** Coroutines & Flow
* **Carregamento de Imagem:** Coil
* **Arquitetura:** MVVM (Model-View-ViewModel)

---

## 🌐 Descrição do Servidor REST

Para este projeto, foi escolhida a **Remove.bg API**, um serviço especializado em segmentação de imagens via IA.

### Por que esta API?
A API foi selecionada por atender ao requisito de processamento remoto complexo, onde o mobile envia um dado bruto (foto) e recebe um dado processado, inviável de ser feito localmente com a mesma precisão e performance sem bibliotecas pesadas.

### Detalhes da Integração

* **Endpoint Base:** `https://api.remove.bg/v1.0/`
* **Rota Utilizada:** `POST /removebg`
* **Autenticação:** Via Header HTTP (`X-Api-Key`).

### Estrutura da Requisição
A comunicação segue o padrão `multipart/form-data`, necessário para envio de arquivos binários.

**Cabeçalhos (Headers):**
```http
X-Api-Key: SUA_CHAVE_API

```

**Corpo (Body):**

1. **`image_file`**: O arquivo de imagem binário (jpg ou png) a ser processado.
2.
**`size`**: Parâmetro configurado como `"auto"` para decisão automática de resolução (conforme documentação oficial ).



### Resposta

* **Sucesso (HTTP 200):** O servidor retorna o corpo da resposta (`ResponseBody`) diretamente como um fluxo de bytes (`image/png` com transparência), que o aplicativo converte para Bitmap e salva localmente.
* **Erro (HTTP 4xx/5xx):** Retorna um JSON detalhando o problema (ex: chave inválida, arquivo não suportado).

---

## 🚀 Como executar o projeto

1. Clone este repositório.
2. Abra o projeto no **Android Studio Ladybug** (ou versão compatível com Compose).
3. Vá até o arquivo `ui/viewmodel/RemoveBackgroundViewModel.kt`.
4. Insira sua chave de API na variável `API_KEY`:
```kotlin
private val API_KEY = "SUA_CHAVE_AQUI"

```


5. Execute o app em um emulador ou dispositivo físico com acesso à internet.

---

### Autoria

Trabalho desenvolvido para a disciplina de Programação para Dispositivos Móveis.

```

```