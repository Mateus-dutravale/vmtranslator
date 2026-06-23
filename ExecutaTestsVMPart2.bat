@echo off
cls
echo ======================================================
echo      COMPILANDO O VMTRANSLATOR (PARTE 2)
echo ======================================================
if not exist bin mkdir bin

javac -d bin src/main/java/org/ufma/parser/*.java src/main/java/org/ufma/codewriter/*.java src/main/java/org/ufma/main/*.java

if errorlevel 1 (
    echo [ERRO] Falha na compilacao. Verifique o codigo.
    pause
    exit /b
)

set "JAVA_CMD=java -cp bin org.ufma.main.VMTranslator"
:: Caminhos das ferramentas do Nand2Tetris
set "CPU_EMULATOR=%~dp0nand2tetris\tools\CPUEmulator.bat"
set "TEXT_COMPARER=%~dp0nand2tetris\tools\TextComparer.bat"

:: MAPEAMENTO DOS DIRETORIOS DE TESTE DO PROJETO 8
set "DIR_BASIC_LOOP=%~dp0test\projects\8\ProgramFlow\BasicLoop"
set "DIR_FIB_SERIES=%~dp0test\projects\8\ProgramFlow\FibonacciSeries"
set "DIR_SIMPLE_FUNC=%~dp0test\projects\8\FunctionCalls\SimpleFunction"
set "DIR_NESTED_CALL=%~dp0test\projects\8\FunctionCalls\NestedCall"
set "DIR_FIB_ELEMENT=%~dp0test\projects\8\FunctionCalls\FibonacciElement"
set "DIR_STATICS_TEST=%~dp0test\projects\8\FunctionCalls\StaticsTest"

echo.
echo ======================================================
echo      1. TRADUZINDO OS DIRETORIOS (.VM -^> .ASM)
echo ======================================================
%JAVA_CMD% "%DIR_BASIC_LOOP%"
%JAVA_CMD% "%DIR_FIB_SERIES%"
%JAVA_CMD% "%DIR_SIMPLE_FUNC%"
%JAVA_CMD% "%DIR_NESTED_CALL%"
%JAVA_CMD% "%DIR_FIB_ELEMENT%"
%JAVA_CMD% "%DIR_STATICS_TEST%"

echo.
echo ======================================================
echo      2. EXECUTANDO OS TESTES NO CPU EMULATOR (.TST -^> .OUT)
echo ======================================================
if exist "%CPU_EMULATOR%" (
    echo [EXECUTANDO SIMULACOES...]
    call "%CPU_EMULATOR%" "%DIR_BASIC_LOOP%\BasicLoop.tst"
    call "%CPU_EMULATOR%" "%DIR_FIB_SERIES%\FibonacciSeries.tst"
    call "%CPU_EMULATOR%" "%DIR_SIMPLE_FUNC%\SimpleFunction.tst"
    call "%CPU_EMULATOR%" "%DIR_NESTED_CALL%\NestedCall.tst"
    call "%CPU_EMULATOR%" "%DIR_FIB_ELEMENT%\FibonacciElement.tst"
    call "%CPU_EMULATOR%" "%DIR_STATICS_TEST%\StaticsTest.tst"
) else (
    echo [ERRO] CPUEmulator.bat nao localizado em %CPU_EMULATOR%
    pause
    exit /b
)

echo.
echo ======================================================
echo      3. VALIDACAO AUTOMATICA (TEXTCOMPARER)
echo ======================================================

if exist "%TEXT_COMPARER%" (
    echo [PROGRAM FLOW]
    call "%TEXT_COMPARER%" "%DIR_BASIC_LOOP%\BasicLoop.out" "%DIR_BASIC_LOOP%\BasicLoop.cmp"
    call "%TEXT_COMPARER%" "%DIR_FIB_SERIES%\FibonacciSeries.out" "%DIR_FIB_SERIES%\FibonacciSeries.cmp"

    echo.
    echo [FUNCTION CALLS]
    call "%TEXT_COMPARER%" "%DIR_SIMPLE_FUNC%\SimpleFunction.out" "%DIR_SIMPLE_FUNC%\SimpleFunction.cmp"
    call "%TEXT_COMPARER%" "%DIR_NESTED_CALL%\NestedCall.out" "%DIR_NESTED_CALL%\NestedCall.cmp"
    call "%TEXT_COMPARER%" "%DIR_FIB_ELEMENT%\FibonacciElement.out" "%DIR_FIB_ELEMENT%\FibonacciElement.cmp"
    call "%TEXT_COMPARER%" "%DIR_STATICS_TEST%\StaticsTest.out" "%DIR_STATICS_TEST%\StaticsTest.cmp"
) else (
    echo [AVISO] TextComparer.bat nao localizado.
)

echo.
echo ======================================================
echo  PROCESSO CONCLUIDO DA PARTE 2!
echo ======================================================
pause