# 🏛️ Tradutor de Máquina Virtual - VMTranslator (Nand2Tetris)

Projeto desenvolvido para a disciplina de **Compiladores** - Universidade Federal do Maranhão (UFMA).

## 👥 Integrantes e Matrículas

* **Gabryella Cruz Sousa** - Matrícula: `20250013701`
* **Mateus Dutra Vale** - Matrícula: `20250071302`

## 💻 Linguagem e Tecnologias

* **Linguagem:** Java
* **Ferramentas:** IntelliJ IDEA / Git
* **Projeto Base:** Projeto 7 do curso **Nand2Tetris** (Unidade 1 - Virtual Machine I: Stack Arithmetic)

## 📂 Estrutura do Projeto

A organização do repositório garante que os scripts de teste funcionem de forma relativa, mantendo as ferramentas de validação integradas:

```plaintext
vmtranslator/
├── bin/                    # Binários compilados (.class)
├── nand2tetris/            # Ferramentas e gabaritos oficiais
├── src/main/java/          # Código-fonte modularizado por pacotes
│   └── org/ufma/
│       ├── main/           # Orquestrador (VMTranslator.java)
│       ├── parser/         # Analisador sintático (VMParser.java, CommandType.java)
│       └── codewriter/     # Gerador de código (CodeWriter.java)
├── test/                   # Pastas de teste oficiais (projects/7/)
├── .gitignore              # Arquivos ignorados pelo Git
├── README.md               # Documentação
└── ExecutaTestesVM.bat     # Script de automação e compilação do projeto
```
# 🚀 Instruções para Compilar e Executar

## 1. Pré-requisitos

* Java JDK instalado (**versão 11 ou superior** recomendada);
* Sistema Operacional **Windows** (necessário para executar o script `.bat` de validação).

---

## 2. Compilação

A partir da raiz do projeto, navegue até a pasta das classes e compile:

```bash
cd src/main/java
javac -d ../../../bin org/ufma/parser/*.java org/ufma/codewriter/*.java org/ufma/main/*.java
```

---

## 3. Execução Manual

O tradutor processa **diretórios completos**.

O programa gerará um arquivo `.asm` unificado contendo o código Assembly Hack no mesmo diretório de origem.

```bash
# Estando na raiz do projeto
java -cp bin org.ufma.main.VMTranslator "./test/projects/7/StackArithmetic/SimpleAdd"
```

---

# ✅ Validação e Testes Oficiais

Para garantir a geração correta do código, desenvolvemos um script de automação que processa os programas de teste automaticamente.

Na raiz do projeto, execute:

```bash
.\ExecutaTestesVM.bat
```

## 1. O que o script realiza

* **Build Automático:** compila todas as classes Java presentes em `src/main/java`, gerando os arquivos `.class` na pasta `bin`.

* **Processamento em Lote:** percorre os diretórios configurados em `test/projects/7/` (`SimpleAdd` e `BasicTest`).

* **Geração Assembly:** executa o tradutor para ler os arquivos `.vm` da pasta e produzir um único arquivo `.asm` correspondente.

* **Auditoria Automatizada:** utiliza o `TextComparer` oficial do Nand2Tetris para comparar o arquivo gerado com o gabarito esperado.

---

## 2. Emulação do Código Gerado

Para validar o código produzido pelo tradutor:

1. Abra o **`CPUEmulator.bat`**, localizado em `nand2tetris/tools`;
2. Acesse **File → Load Script**;
3. Carregue o script de teste desejado, por exemplo:

```
test/projects/7/MemoryAccess/BasicTest/BasicTest.tst
```

4. Ajuste a velocidade para **Fast**;
5. Execute pressionando **F5**.

O emulador irá executar o código Assembly no hardware virtual e gerar automaticamente o arquivo `.out`, utilizado para validação.

---

# ⚙️ Detalhamento dos Componentes

## `CodeWriter.java`

É o núcleo responsável pela geração do código Assembly.

Suas responsabilidades incluem:

* Converter instruções da máquina virtual em Assembly Hack;
* Manipular diretamente o `Stack Pointer (SP)`;
* Resolver operações envolvendo `LCL`, `ARG`, `THIS` e `THAT`;
* Gerenciar o segmento `temp`;
* Calcular offsets em tempo de execução para operações `push` e `pop`.

---

## `VMParser.java`

Responsável pela análise sintática dos arquivos `.vm`.

Entre suas funções estão:

* Leitura sequencial das instruções;
* Remoção de comentários e espaços em branco;
* Identificação do tipo do comando (`C_PUSH`, `C_POP`, `C_ARITHMETIC`, etc.);
* Extração dos argumentos utilizados pelo tradutor.

---

## `CommandType.java`

Enumeração responsável por categorizar os comandos suportados pela especificação da máquina virtual.

Sua utilização simplifica a estrutura de decisão do tradutor principal, tornando o código mais organizado e seguro.

---

## `VMTranslator.java`

Classe principal da aplicação.

Implementa a lógica responsável por:

* Identificar automaticamente se o caminho informado corresponde a um único arquivo ou a um diretório;
* Percorrer diretórios completos;
* Processar sequencialmente todos os arquivos `.vm`;
* Acionar os módulos de parsing e geração de código.

---

# ✨ Destaques da Implementação

* **Geração de Labels Únicas:** implementação de um contador incremental para criação de rótulos exclusivos utilizados pelas operações relacionais (`eq`, `gt` e `lt`), evitando colisões durante os saltos condicionais (`JEQ`, `JGT` e `JLT`).

* **Tratamento de Ponteiros e Temporários:** utilização do registrador virtual `R13` como armazenamento intermediário seguro durante operações complexas de `pop` envolvendo endereçamento indireto.

* **Modularidade e Portabilidade:** todos os caminhos do projeto utilizam referências relativas dinâmicas (`%~dp0`), eliminando dependências de caminhos absolutos específicos da máquina de desenvolvimento.

* **Automação Completa:** integração do processo de **compilação**, **tradução** e **validação** em um único script executável (`ExecutaTestesVM.bat`), simplificando o fluxo de testes.
