@echo off
cls
echo ======================================================
echo      COMPILANDO O VMTRANSLATOR (GERANDO .CLASS)
echo ======================================================
if not exist bin mkdir bin

javac -d bin src/main/java/org/ufma/parser/*.java src/main/java/org/ufma/codewriter/*.java src/main/java/org/ufma/main/*.java

if errorlevel 1 (
    echo [ERRO] Falha na compilacao das classes Java. Verifique o codigo.
    pause
    exit /b
)

set "JAVA_CMD=java -cp bin org.ufma.main.VMTranslator"
set "TEXT_COMPARER=%~dp0nand2tetris\tools\TextComparer.bat"

:: MAPEAMENTO DOS DIRETORIOS DE TESTE
set "DIR_SIMPLE_ADD=%~dp0test\projects\7\StackArithmetic\SimpleAdd"
set "DIR_BASIC_TEST=%~dp0test\projects\7\MemoryAccess\BasicTest"

echo.
echo ======================================================
echo      1. TRADUZINDO OS DIRETORIOS (.VM -> .ASM)
echo ======================================================
echo [PROCESSANDO] "%DIR_SIMPLE_ADD%"
%JAVA_CMD% "%DIR_SIMPLE_ADD%"

echo.
echo [PROCESSANDO] "%DIR_BASIC_TEST%"
%JAVA_CMD% "%DIR_BASIC_TEST%"

echo.
echo ======================================================
echo      2. VALIDACAO AUTOMATICA (TEXTCOMPARER)
echo ======================================================

if exist "%TEXT_COMPARER%" (
    echo [VALIDANDO] SimpleAdd...
    call "%TEXT_COMPARER%" "%DIR_SIMPLE_ADD%\SimpleAdd.out" "%DIR_SIMPLE_ADD%\SimpleAdd.cmp"

    echo.
    echo [VALIDANDO] BasicTest...
    call "%TEXT_COMPARER%" "%DIR_BASIC_TEST%\BasicTest.out" "%DIR_BASIC_TEST%\BasicTest.cmp"
) else (
    echo [AVISO] O arquivo TextComparer.bat nao foi localizado em "%TEXT_COMPARER%"
    echo Verifique se a pasta 'nand2tetris' esta na raiz do projeto.
)

echo.
echo ======================================================
echo  PROCESSO CONCLUIDO DA SUITE DE TESTES!
echo ======================================================
pause