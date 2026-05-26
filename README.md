# 🎓 ACADEMO - Gamificação Acadêmica

![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![SQLite](https://img.shields.io/badge/SQLite-07405E?style=for-the-badge&logo=sqlite&logoColor=white)
![Material Design](https://img.shields.io/badge/Material%20Design-757575?style=for-the-badge&logo=material-design&logoColor=white)

**Academo** é um aplicativo Android nativo que transforma o aprendizado em uma experiência gamificada. Cada questão respondida concede XP, evolui o nível do usuário e desbloqueia ranks mais altos indo de SS até D.

> Projeto final desenvolvido para a disciplina de **Desenvolvimento Mobile**.

---

## 📱 Funcionalidades

| Funcionalidade | Descrição |
|----------------|-----------|
| 🎮 **Sistema de Progressão** | XP acumulável com level up automático |
| 🏆 **Ranks Dinâmicos** | SS, S, A, B, C, D com pesos específicos |
| ⏱️ **Pontuação com Bônus** | Respostas rápidas concedem pontos extras |
| 📂 **Importação de Níveis** | Suporte a JSON customizado, qualquer professor pode criar seu próprio quiz |
| 💾 **Persistência Total** | SQLite com histórico de best score, rank máximo, tentativas e última data |
| 🔍 **Filtros Avançados** | Organize níveis por Rank, Tentativas, Dificuldade ou Total de Questões |
| 👤 **Perfil Customizável** | Foto de perfil com armazenamento interno |
| 🔐 **Autenticação** | Sistema de login e recuperação de senha |

---

## 🛠️ Stack Tecnológica

| Tecnologia | Aplicação no Projeto |
|------------|----------------------|
| 🧠 **Java** | Desenvolvimento da lógica principal, Activities e sistema de progressão |
| 🗄️ **SQLite** | Persistência local de usuários, autenticação e histórico de níveis |
| 📱 **RecyclerView + CardView** | Renderização dinâmica e otimizada da lista de níveis |
| 🎨 **Material Design Components** | Interface moderna seguindo padrões visuais do Android |
| ⏲️ **CountDownTimer** | Controle de tempo e bônus por velocidade nas questões |
| 💾 **SharedPreferences** | Persistência de sessão e preferências do usuário |
| 📥 **JSON Parser** | Sistema data-driven para importação de níveis customizados |
| 🖼️ **Image Handling + Internal Storage** | Armazenamento local e gerenciamento da foto de perfil |
| 🧩 **XML Layouts** | Construção responsiva das interfaces do aplicativo |
| 🔄 **Intents & Activity Navigation** | Navegação entre telas e transferência de dados |


### Estrutura do Banco de Dados

| Tabela | Função |
|--------|--------|
| `users` | Dados do usuário |
| `auth` | Autenticação  com chave estrangeira |
| `user_levels_history` | Histórico por nível |

---

## 🎮 Como Funciona o Jogo

1. **Selecione um nível** na tela inicial
2. **Responda às questões** dentro do tempo limite
3. **Quanto mais rápido, maior a pontuação**
4. **Ao final, ganhe XP e suba de rank**

### Sistema de Ranks

| Rank | Performance | Cor |
|------|-------------|-----|
| **SS** | 100%| 🟡 Dourado |
| **S**  | 85%+ | 🟠 Laranja |
| **A**  | 70%+ | 🟣 Roxo |
| **B**  | 55%+ | 🔵 Azul |
| **C**  | 40%+ | ⚪ Cinza |
| **D**  | Abaixo de 40% | ⚫ Cinza escuro |

### Multiplicador por Dificuldade

| Dificuldade | Tempo por Questão | Multiplicador de Rank |
|-------------|-------------------|----------------------|
| Easy | 12 segundos | 1.0x |
| Medium | 9 segundos | 1.15x |
| Hard | 6 segundos | 1.3x |

---

## 📥 Importação de Níveis Customizados

O Academo permite que professores ou usuários criem seus próprios níveis em formato JSON.

### Estrutura do JSON:

```json
[
  {
    "type": "multiple_choice",
    "text": "Qual é a saída do código `System.out.println(2 + 3 * 4);`?",
    "options": ["14", "20", "24", "11"],
    "correctAnswer": "14"
  },
  {
    "type": "true_false",
    "text": "O Java é uma linguagem compilada e interpretada.",
    "options": ["true", "false"],
    "correctAnswer": "true"
  },
  {
    "type": "text_input",
    "text": "Qual palavra-chave é usada para criar uma classe em Java?",
    "options": [],
    "correctAnswer": "class"
  }
]
```

## 📝 Tipos de Questão Suportados

| Tipo | Descrição | Campo `options` |
|------|-----------|-----------------|
| `multiple_choice` | 4 opções de resposta | Array com 4 strings |
| `true_false` | Verdadeiro ou Falso | Array com `"true"` / `"false"` |
| `text_input` | Resposta aberta | Array vazio |

## Como Executar o Projeto

### Pré-requisitos
- Android Studio Hedgehog ou superior
- JDK 17+
- Dispositivo ou emulador Android (API 24+)

### Passos
```bash
git clone https://github.com/RMD663/Academo.git
```

Abra o projeto no Android Studio e clique em Run.

## 👥 Autores

| Nome | Papel |
|------|-------|
| **Ryan Damasceno** | Desenvolvimento Mobile, Arquitetura, SQLite, Game Design |
| **Wesley Silva** | JSON Structure, UI/UX |

### 📄 [Licença](LICENSE)
Este projeto está sob a licença GPL v3. Consulte o arquivo LICENSE para mais informações.
