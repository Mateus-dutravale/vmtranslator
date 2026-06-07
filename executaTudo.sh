@echo off
cls
echo ======================================================
echo      COMPILANDO O VMTRANSLATOR (GERANDO .CLASS)
echo ======================================================
:: Cria a pasta bin se ela nao existir para não dar erro no javac
if not exist bin mkdir bin

:: Compila todas as classes Java jogando os binarios na pasta bin
javac -d bin src/main/java/org/ufma/parser/*.java src/main/java/org/ufma/codewriter/*.java src/main/java/org/ufma/main/*.java

if errorlevel 1 (
    echo [ERRO] Falha na compilacao das classes Java. Verifique o codigo.
    pause
    exit /b
)

:: Define o comando de execucao apontando para a classe Main correta
set "JAVA_CMD=java -cp bin org.ufma.main.VMTranslator"

:: CAMINHO ABSOLUTO DAS PASTAS DE TESTE (Ajustado para o padrao Windows)
set "DIR_SIMPLE_ADD=E:\Faculdade\terceiro periodo\Java compilador\Analisador\JackCompilador-Java\vmtranslator\test\projects\7\StackArithmetic\SimpleAdd"

:: CAMINHO DO TEXTCOMPARER DO NAND2TETRIS
set "TEXT_COMPARER=nand2tetris\tools\TextComparer.bat"

echo.
echo ======================================================
echo      1. TRADUZINDO DIRETORIO: SimpleAdd
echo ======================================================
echo Processando pasta: %DIR_SIMPLE_ADD%
%JAVA_CMD% "%DIR_SIMPLE_ADD%"

echo.
echo ======================================================
echo      2. VALIDACAO AUTOMATICA (TEXTCOMPARER)
echo ======================================================

if exist "%TEXT_COMPARER%" (
    echo [VALIDANDO] SimpleAdd...
    call "%TEXT_COMPARER%" "%DIR_SIMPLE_ADD%\SimpleAdd.out" "%DIR_SIMPLE_ADD%\SimpleAdd.cmp"
) else (
    echo [AVISO] O arquivo TextComparer.bat nao foi localizado em %TEXT_COMPARER%
    echo Verifique se a pasta 'nand2tetris' esta na raiz do projeto.
)

echo.
echo ======================================================
echo  PROCESSO CONCLUIDO!
echo ======================================================
pause