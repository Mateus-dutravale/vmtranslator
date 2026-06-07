package org.ufma.codewriter;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class CodeWriter {
    private PrintWriter out;
    private int labelCounter = 0; // Gerador de labels unicas para eq, gt, lt

    public CodeWriter(String filename) throws IOException {
        this.out = new PrintWriter(new FileWriter(filename));
    }

    public void writeArithmetic(String command) {
        out.println("// " + command);

        if (command.equals("add") || command.equals("sub") || command.equals("and") || command.equals("or")) {
            // Operações binárias: tiram dois valores da pilha e cospem um
            out.println("@SP");
            out.println("AM=M-1");
            out.println("D=M"); // D = Y
            out.println("A=A-1"); // RAM[SP-1] é o X

            if (command.equals("add")) out.println("M=M+D");
            else if (command.equals("sub")) out.println("M=M-D");
            else if (command.equals("and")) out.println("M=M&D");
            else if (command.equals("or"))  out.println("M=M|D");

        } else if (command.equals("neg") || command.equals("not")) {
            // Operações unárias: modificam apenas o valor do topo da pilha
            out.println("@SP");
            out.println("A=M-1");

            if (command.equals("neg")) out.println("M=-M");
            else if (command.equals("not")) out.println("M=!M");

        } else if (command.equals("eq") || command.equals("gt") || command.equals("lt")) {
            // Operações de comparação com Saltos (Jumps)
            out.println("@SP");
            out.println("AM=M-1");
            out.println("D=M"); // D = Y
            out.println("A=A-1"); // A aponta para X
            out.println("D=M-D"); // D = X - Y

            String jumpType = "";
            if (command.equals("eq")) jumpType = "JEQ";
            else if (command.equals("gt")) jumpType = "JGT";
            else if (command.equals("lt")) jumpType = "JLT";

            String labelTrue = "LABEL_TRUE_" + labelCounter;
            String labelFalse = "LABEL_FALSE_" + labelCounter;

            out.println("@" + labelTrue);
            out.println("D;" + jumpType); // Se a condição for atendida, pula pro TRUE

            // Caso seja FALSO: coloca 0 no topo da pilha
            out.println("@SP");
            out.println("A=M-1");
            out.println("M=0");
            out.println("@" + labelFalse);
            out.println("0;JMP"); // Pula o bloco TRUE

            // Caso seja VERDADEIRO: coloca -1 (true na VM do Nand2Tetris)
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
                // Segmentos básicos: local, argument, this, that
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
                // Segmento temp mapeia fixo de RAM[5] a RAM[12]
                out.println("// push temp " + index);
                out.println("@" + (5 + index));
                out.println("D=M");
                out.println("@SP");
                out.println("A=M");
                out.println("M=D");
                out.println("@SP");
                out.println("M=M+1");
            } else if (segment.equals("pointer")) {
                // pointer 0 -> THIS (RAM[3]), pointer 1 -> THAT (RAM[4])
                out.println("// push pointer " + index);
                out.println("@" + (index == 0 ? "THIS" : "THAT"));
                out.println("D=M");
                out.println("@SP");
                out.println("A=M");
                out.println("M=D");
                out.println("@SP");
                out.println("M=M+1");
            } else if (segment.equals("static")) {
                // Mapeia em variáveis estáticas (partindo do endereço RAM[16])
                out.println("// push static " + index);
                out.println("@" + (16 + index));
                out.println("D=M");
                out.println("@SP");
                out.println("A=M");
                out.println("M=D");
                out.println("@SP");
                out.println("M=M+1");
            }
        } else if (commandType.equals("pop")) {
            if (!baseReg.isEmpty()) {
                // Segmentos básicos: local, argument, this, that
                out.println("// pop " + segment + " " + index);
                out.println("@" + baseReg);
                out.println("D=M");
                out.println("@" + index);
                out.println("D=D+A");
                out.println("@R13");
                out.println("M=D"); // Salva o endereço alvo em R13

                out.println("@SP");
                out.println("AM=M-1");
                out.println("D=M"); // D recebe o valor do topo da pilha

                out.println("@R13");
                out.println("A=M");
                out.println("M=D"); // Salva o valor no endereço alvo
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
                out.println("// pop static " + index);
                out.println("@SP");
                out.println("AM=M-1");
                out.println("D=M");
                out.println("@" + (16 + index));
                out.println("M=D");
            }
        }
    }

    public void close() {
        out.close();
    }
}