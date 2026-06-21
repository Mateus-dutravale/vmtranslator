package org.ufma.codewriter;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class CodeWriter {
    private PrintWriter out;
    private int labelCounter = 0;
    private int callCounter = 0; // Gerador de labels unicas para retornos de funcao
    private String currentFileName = ""; // Guarda o nome do arquivo para as variaveis estaticas

    public CodeWriter(String filename) throws IOException {
        this.out = new PrintWriter(new FileWriter(filename));
    }

    public void setFileName(String fileName) {
        this.currentFileName = fileName.replace(".vm", "");
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
                // Atualizado para usar o nome do arquivo atual
                out.println("// push static " + index);
                out.println("@" + currentFileName + "." + index);
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
                // Atualizado para usar o nome do arquivo atual
                out.println("// pop static " + index);
                out.println("@SP");
                out.println("AM=M-1");
                out.println("D=M");
                out.println("@" + currentFileName + "." + index);
                out.println("M=D");
            }
        }
    }

    // --- NOVOS MÉTODOS DE FLUXO E FUNÇÃO (PARTE DA GABY) ---

    public void writeLabel(String label) {
        out.println("// label " + label);
        out.println("(" + label + ")");
    }

    public void writeGoto(String label) {
        out.println("// goto " + label);
        out.println("@" + label);
        out.println("0;JMP");
    }

    public void writeIf(String label) {
        out.println("// if-goto " + label);
        out.println("@SP");
        out.println("AM=M-1");
        out.println("D=M");
        out.println("@" + label);
        out.println("D;JNE");
    }

    public void writeFunction(String functionName, int numLocals) {
        out.println("// function " + functionName + " " + numLocals);
        out.println("(" + functionName + ")");
        for (int i = 0; i < numLocals; i++) {
            out.println("@0");
            out.println("D=A");
            out.println("@SP");
            out.println("A=M");
            out.println("M=D");
            out.println("@SP");
            out.println("M=M+1");
        }
    }

    public void writeReturn() {
        out.println("// return");
        out.println("@LCL");
        out.println("D=M");
        out.println("@R13"); // R13 guarda o FRAME (LCL base)
        out.println("M=D");

        out.println("@5");
        out.println("A=D-A");
        out.println("D=M");
        out.println("@R14"); // R14 guarda o RET (Endereço de retorno)
        out.println("M=D");

        out.println("@SP"); // *ARG = pop()
        out.println("AM=M-1");
        out.println("D=M");
        out.println("@ARG");
        out.println("A=M");
        out.println("M=D");

        out.println("@ARG"); // SP = ARG + 1
        out.println("D=M+1");
        out.println("@SP");
        out.println("M=D");

        String[] segments = {"THAT", "THIS", "ARG", "LCL"};
        for (int i = 0; i < 4; i++) {
            out.println("@R13");
            out.println("AM=M-1");
            out.println("D=M");
            out.println("@" + segments[i]);
            out.println("M=D");
        }

        out.println("@R14"); // goto RET
        out.println("A=M");
        out.println("0;JMP");
    }

    public void writeInit() {
        out.println("// Bootstrap: Inicializa SP em 256");
        out.println("@256");
        out.println("D=A");
        out.println("@SP");
        out.println("M=D");

        writeCall("Sys.init", 0);
    }

    public void writeCall(String functionName, int numArgs) {
        String returnAddress = functionName + "$ret." + callCounter++;
        out.println("// call " + functionName + " " + numArgs);

        out.println("@" + returnAddress);
        out.println("D=A");
        out.println("@SP");
        out.println("A=M");
        out.println("M=D");
        out.println("@SP");
        out.println("M=M+1");

        String[] segments = {"LCL", "ARG", "THIS", "THAT"};
        for (String seg : segments) {
            out.println("@" + seg);
            out.println("D=M");
            out.println("@SP");
            out.println("A=M");
            out.println("M=D");
            out.println("@SP");
            out.println("M=M+1");
        }

        out.println("@SP");
        out.println("D=M");
        out.println("@" + (numArgs + 5));
        out.println("D=D-A");
        out.println("@ARG");
        out.println("M=D");

        out.println("@SP");
        out.println("D=M");
        out.println("@LCL");
        out.println("M=D");

        writeGoto(functionName);

        out.println("(" + returnAddress + ")");
    }

    public void close() {
        out.close();
    }
}