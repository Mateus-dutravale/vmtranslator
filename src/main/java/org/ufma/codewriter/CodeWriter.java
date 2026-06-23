package org.ufma.codewriter;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class CodeWriter {
    private PrintWriter out;
    private int labelCounter = 0; // Gerador de labels unicas para eq, gt, lt
    private int callCounter = 0;  // Gerador de labels unicas para retornos de funcoes
    private String fileName = ""; // Nome do arquivo atual para variaveis estaticas
    private String currentFunctionName = ""; // Nome da funcao atual para escopo de labels

    public CodeWriter(String filename) throws IOException {
        this.out = new PrintWriter(new FileWriter(filename));
    }

    private String scopedLabel(String label) {
        if (currentFunctionName == null || currentFunctionName.isEmpty()) {
            return label;
        }
        return currentFunctionName + "$" + label;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void writeArithmetic(String command) {
        out.println("// " + command);

        if (command.equals("add") || command.equals("sub") || command.equals("and") || command.equals("or")) {
            out.println("@SP");
            out.println("AM=M-1");
            out.println("D=M");
            out.println("A=A-1");

            if (command.equals("add")) out.println("M=M+D");
            else if (command.equals("sub")) out.println("M=M-D");
            else if (command.equals("and")) out.println("M=M&D");
            else if (command.equals("or"))  out.println("M=M|D");

        } else if (command.equals("neg") || command.equals("not")) {
            out.println("@SP");
            out.println("A=M-1");

            if (command.equals("neg")) out.println("M=-M");
            else if (command.equals("not")) out.println("M=!M");

        } else if (command.equals("eq") || command.equals("gt") || command.equals("lt")) {
            out.println("@SP");
            out.println("AM=M-1");
            out.println("D=M");
            out.println("A=A-1");
            out.println("D=M-D");

            String jumpType = "";
            if (command.equals("eq")) jumpType = "JEQ";
            else if (command.equals("gt")) jumpType = "JGT";
            else if (command.equals("lt")) jumpType = "JLT";

            String labelTrue = "LABEL_TRUE_" + labelCounter;
            String labelFalse = "LABEL_FALSE_" + labelCounter;

            out.println("@" + labelTrue);
            out.println("D;" + jumpType);

            out.println("@SP");
            out.println("A=M-1");
            out.println("M=0");
            out.println("@" + labelFalse);
            out.println("0;JMP");

            out.println("(" + labelTrue + ")");
            out.println("@SP");
            out.println("A=M-1");
            out.println("M=-1");

            out.println("(" + labelFalse + ")");
            labelCounter++;
        }
    }

    public void writePushPop(String commandType, String segment, int index) {
        String baseReg = "";
        if (segment.equals("local")) baseReg = "LCL";
        else if (segment.equals("argument")) baseReg = "ARG";
        else if (segment.equals("this")) baseReg = "THIS";
        else if (segment.equals("that")) baseReg = "THAT";

        if (commandType.equals("push")) {
            if (segment.equals("constant")) {
                out.println("// push constant " + index);
                out.println("@" + index);
                out.println("D=A");
                out.println("@SP");
                out.println("A=M");
                out.println("M=D");
                out.println("@SP");
                out.println("M=M+1");
            } else if (!baseReg.isEmpty()) {
                out.println("// push " + segment + " " + index);
                out.println("@" + baseReg);
                out.println("D=M");
                out.println("@" + index);
                out.println("A=D+A");
                out.println("D=M");
                out.println("@SP");
                out.println("A=M");
                out.println("M=D");
                out.println("@SP");
                out.println("M=M+1");
            } else if (segment.equals("temp")) {
                out.println("// push temp " + index);
                out.println("@" + (5 + index));
                out.println("D=M");
                out.println("@SP");
                out.println("A=M");
                out.println("M=D");
                out.println("@SP");
                out.println("M=M+1");
            } else if (segment.equals("pointer")) {
                out.println("// push pointer " + index);
                out.println("@" + (index == 0 ? "THIS" : "THAT"));
                out.println("D=M");
                out.println("@SP");
                out.println("A=M");
                out.println("M=D");
                out.println("@SP");
                out.println("M=M+1");
            } else if (segment.equals("static")) {
                // CORREÇÃO: Usa o nome do arquivo para rotular a variável estática de forma única
                out.println("// push static " + index);
                out.println("@" + fileName + "." + index);
                out.println("D=M");
                out.println("@SP");
                out.println("A=M");
                out.println("M=D");
                out.println("@SP");
                out.println("M=M+1");
            }
        } else if (commandType.equals("pop")) {
            if (!baseReg.isEmpty()) {
                out.println("// pop " + segment + " " + index);
                out.println("@" + baseReg);
                out.println("D=M");
                out.println("@" + index);
                out.println("D=D+A");
                out.println("@R13");
                out.println("M=D");

                out.println("@SP");
                out.println("AM=M-1");
                out.println("D=M");

                out.println("@R13");
                out.println("A=M");
                out.println("M=D");
            } else if (segment.equals("temp")) {
                out.println("// pop temp " + index);
                out.println("@SP");
                out.println("AM=M-1");
                out.println("D=M");
                out.println("@" + (5 + index));
                out.println("M=D");
            } else if (segment.equals("pointer")) {
                out.println("// pop pointer " + index);
                out.println("@SP");
                out.println("AM=M-1");
                out.println("D=M");
                out.println("@" + (index == 0 ? "THIS" : "THAT"));
                out.println("M=D");
            } else if (segment.equals("static")) {
                // CORREÇÃO: Mapeia pop static de forma limpa por arquivo
                out.println("// pop static " + index);
                out.println("@SP");
                out.println("AM=M-1");
                out.println("D=M");
                out.println("@" + fileName + "." + index);
                out.println("M=D");
            }
        }
    }

    public void writeInit() {
        out.println("// Bootstrap: Inicializa SP em 256");
        out.println("@256");
        out.println("D=A");
        out.println("@SP");
        out.println("M=D");

        // Chama Sys.init implicitamente com 0 argumentos
        writeCall("Sys.init", 0);
    }

    public void writeLabel(String label) {
        out.println("// label " + label);
        out.println("(" + scopedLabel(label) + ")");
    }

    public void writeGoto(String label) {
        out.println("// goto " + label);
        out.println("@" + scopedLabel(label));
        out.println("0;JMP");
    }

    public void writeIf(String label) {
        out.println("// if-goto " + label);

        out.println("@SP");
        out.println("AM=M-1");
        out.println("D=M");

        out.println("@" + scopedLabel(label));
        out.println("D;JNE");
    }

    public void writeFunction(String functionName, int numLocals) {
        out.println("// function " + functionName + " " + numLocals);

        currentFunctionName = functionName;

        out.println("(" + functionName + ")");

        for (int i = 0; i < numLocals; i++) {
            out.println("@0");
            out.println("D=A");
            pushDToStack();
        }
    }

    public void writeCall(String functionName, int numArgs) {
        String returnLabel = functionName + "$ret." + callCounter;
        callCounter++;

        out.println("// call " + functionName + " " + numArgs);

        // push returnLabel
        out.println("@" + returnLabel);
        out.println("D=A");
        pushDToStack();

        // push LCL, ARG, THIS, THAT
        out.println("@LCL");  out.println("D=M"); pushDToStack();
        out.println("@ARG");  out.println("D=M"); pushDToStack();
        out.println("@THIS"); out.println("D=M"); pushDToStack();
        out.println("@THAT"); out.println("D=M"); pushDToStack();

        // ARG = SP - 5 - numArgs
        out.println("@SP");
        out.println("D=M");
        out.println("@5");
        out.println("D=D-A");
        out.println("@" + numArgs);
        out.println("D=D-A");
        out.println("@ARG");
        out.println("M=D");

        // LCL = SP
        out.println("@SP");
        out.println("D=M");
        out.println("@LCL");
        out.println("M=D");

        // goto functionName
        out.println("@" + functionName);
        out.println("0;JMP");

        out.println("(" + returnLabel + ")");
    }

    public void writeReturn() {
        out.println("// return");

        // FRAME = LCL
        out.println("@LCL");
        out.println("D=M");
        out.println("@R14");
        out.println("M=D");

        // RET = *(FRAME - 5)
        out.println("@5");
        out.println("A=D-A");
        out.println("D=M");
        out.println("@R15");
        out.println("M=D");

        // *ARG = pop()
        out.println("@SP");
        out.println("AM=M-1");
        out.println("D=M");

        out.println("@ARG");
        out.println("A=M");
        out.println("M=D");

        // SP = ARG + 1
        out.println("@ARG");
        out.println("D=M+1");
        out.println("@SP");
        out.println("M=D");

        // THAT = *(FRAME-1)
        out.println("@R14");
        out.println("D=M");
        out.println("@1");
        out.println("A=D-A");
        out.println("D=M");
        out.println("@THAT");
        out.println("M=D");

        // THIS = *(FRAME-2)
        out.println("@R14");
        out.println("D=M");
        out.println("@2");
        out.println("A=D-A");
        out.println("D=M");
        out.println("@THIS");
        out.println("M=D");

        // ARG = *(FRAME-3)
        out.println("@R14");
        out.println("D=M");
        out.println("@3");
        out.println("A=D-A");
        out.println("D=M");
        out.println("@ARG");
        out.println("M=D");

        // LCL = *(FRAME-4)
        out.println("@R14");
        out.println("D=M");
        out.println("@4");
        out.println("A=D-A");
        out.println("D=M");
        out.println("@LCL");
        out.println("M=D");

        // goto RET
        out.println("@R15");
        out.println("A=M");
        out.println("0;JMP");
    }

    private void pushDToStack() {
        out.println("@SP");
        out.println("A=M");
        out.println("M=D");
        out.println("@SP");
        out.println("M=M+1");
    }

    public void close() {
        out.close();
    }
}