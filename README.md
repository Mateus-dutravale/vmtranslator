# 🏛️ Tradutor de Máquina Virtual Completo - VMTranslator (Nand2Tetris)

Projeto desenvolvido para a disciplina de **Compiladores** da **Universidade Federal do Maranhão (UFMA)**.

---

# 👥 Integrantes e Matrículas

* **Gabryella Cruz Sousa** - Matrícula: `20250013701`
* **Mateus Dutra Vale** - Matrícula: `20250071302`

---

# 💻 Linguagem e Tecnologias

* **Linguagem:** Java
* **Ferramentas:** IntelliJ IDEA / Git
* **Projeto Base:** Projetos 7 e 8 do curso **Nand2Tetris** (Virtual Machine I & II - Aritmética, Controle de Fluxo e Subrotinas)

---

# 📂 Estrutura do Projeto

A organização do repositório garante que os scripts de teste funcionem utilizando caminhos relativos, mantendo as ferramentas oficiais de validação integradas e oferecendo suporte ao processamento de múltiplos arquivos `.vm` em um único diretório.

```plaintext
vmtranslator/
├── bin/                      # Binários compilados (.class)
├── nand2tetris/              # Ferramentas e gabaritos oficiais do curso
├── src/main/java/            # Código-fonte modularizado por pacotes
│   └── org/ufma/
│       ├── main/             # Orquestrador principal (VMTranslator.java)
│       ├── parser/           # Analisador sintático (VMParser.java, CommandType.java)
│       └── codewriter/       # Gerador de código Assembly (CodeWriter.java)
├── test/                     # Pastas de teste oficiais (projects/7 e projects/8)
├── .gitignore                # Arquivos ignorados pelo Git
├── README.md                 # Documentação do projeto
└── ExecutaTestesVMPart2.bat  # Script de compilação automática e testes da Parte 2
```

---

# 🚀 Instruções para Compilar e Executar

## 1. Pré-requisitos

* Java JDK instalado (**versão 11 ou superior** recomendada);
* Sistema Operacional **Windows** (necessário para executar o script `.bat` de automação).

---

## 2. Compilação

A partir da raiz do projeto, compile todas as classes e direcione os binários para a pasta `bin`:

```bash
mkdir bin

javac -d bin src/main/java/org/ufma/parser/*.java \
src/main/java/org/ufma/codewriter/*.java \
src/main/java/org/ufma/main/*.java
```

---

## 3. Execução Manual

O tradutor possui suporte inteligente para **arquivos individuais** ou **diretórios completos**. Quando um diretório é informado, todos os arquivos `.vm` encontrados são unificados em um único arquivo `.asm`.

```bash
# Processando um diretório complexo
# Gera FibonacciElement.asm na própria pasta de origem

java -cp bin org.ufma.main.VMTranslator "./test/projects/8/FunctionCalls/FibonacciElement"
```

---

# ✅ Validação e Testes Oficiais (Parte 2)

Para validar corretamente a geração do código de controle de fluxo e chamadas de subrotinas, utilize o script de automação responsável pela compilação e execução da suíte oficial de testes do Projeto 8.

Na raiz do projeto, execute:

```bash
.\ExecutaTestesVMPart2.bat
```

## 1. O que o script realiza

* **Build Automático:** limpa e recompila todas as classes do projeto na pasta `bin`;

* **Processamento em Lote:** percorre sequencialmente os diretórios de teste de fluxo (`BasicLoop` e `FibonacciSeries`) e de subrotinas (`SimpleFunction`, `NestedCall`, `FibonacciElement` e `StaticsTest`);

* **Injeção de Bootstrapping:** insere automaticamente a rotina de inicialização do sistema antes da tradução dos diretórios;

* **Auditoria Automatizada:** utiliza o `TextComparer` oficial do Nand2Tetris para validar os arquivos gerados comparando-os com os respectivos gabaritos `.cmp`.

---

## 2. Emulação do Código Gerado

Para produzir os arquivos `.out` utilizados na validação bit a bit:

1. Abra o **CPUEmulator.bat**, localizado em `nand2tetris/tools`;
2. Acesse **File → Load Script**;
3. Carregue o arquivo de teste desejado (por exemplo, `FibonacciElement.tst`);
4. Ajuste a velocidade para **Fast** ou **No Animation**;
5. Execute pressionando **F5**.

O emulador executará o código Assembly simulado e gerará automaticamente o arquivo `.out` correspondente.

---

# ⚙️ Detalhamento dos Componentes

## `VMTranslator.java`

Classe principal da aplicação.

É responsável por:

* Gerenciar os caminhos de entrada fornecidos pelo usuário;
* Identificar arquivos individuais ou diretórios completos;
* Organizar a lista de arquivos `.vm` a serem processados;
* Acionar automaticamente a rotina de bootstrapping quando necessário;
* Orquestrar todo o ciclo de tradução.

---

## `CodeWriter.java`

Núcleo responsável pela geração do código Assembly Hack.

Entre suas funções estão:

* Traduzir comandos da máquina virtual para Assembly;
* Manipular diretamente o `Stack Pointer (SP)`;
* Gerenciar o escopo das variáveis do segmento `static` utilizando o nome do arquivo corrente;
* Gerar labels exclusivos durante o processo de compilação.

---

## `VMParser.java`

Responsável pela análise sintática dos arquivos de entrada.

Suas principais funções incluem:

* Leitura sequencial das instruções;
* Remoção de espaços em branco desnecessários;
* Eliminação de comentários completos ou ao final das linhas;
* Extração dos comandos e respectivos argumentos para consumo do tradutor.

---

## `CommandType.java`

Enumeração responsável pela classificação abstrata dos comandos suportados pela máquina virtual, incluindo:

* Operações aritméticas;
* Comandos `push` e `pop`;
* Controle de fluxo;
* Chamadas e retornos de subrotinas.

---

# ✨ Destaques da Implementação da Parte 2

* **Bootstrapping Nativo:** implementação da rotina de inicialização responsável por posicionar a pilha no endereço `RAM[256]` e realizar automaticamente a chamada da função `Sys.init`.

* **Salvamento de Contexto Eficiente:** o mecanismo de `call` empilha dinamicamente o endereço de retorno e os ponteiros dos segmentos `LCL`, `ARG`, `THIS` e `THAT`, permitindo que o comando `return` restaure integralmente o contexto da função chamadora.

* **Geração de Labels Únicas:** utilização de contadores incrementais para criação de rótulos exclusivos em loops, desvios condicionais e retornos de subrotinas, evitando colisões no código Assembly unificado.

* **Mapeamento de Múltiplos Arquivos:** isolamento das variáveis pertencentes ao segmento `static`, renomeando-as dinamicamente com base no nome do arquivo `.vm` de origem, garantindo independência entre módulos.
