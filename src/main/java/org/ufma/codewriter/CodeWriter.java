package org.ufma.codewriter;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class CodeWriter {
    private PrintWriter out;

    public CodeWriter(String filename) throws IOException {
        this.out = new PrintWriter(new FileWriter(filename));
    }

    public void writeArithmetic(String command) {
        if (command.equals("add")) {
            out.println("// add");
            out.println("@SP");
            out.println("AM=M-1"); // Aponta pro segundo numero (Y) e decrementa o SP
            out.println("D=M");   // Guarda Y em D
            out.println("A=A-1"); // Aponta pro primeiro numero (X), que ta logo acima
            out.println("M=M+D"); // Faz X = X + Y
        }
    }

    public void writePushPop(String commandType, String segment, int index) {
        if (commandType.equals("push") && segment.equals("constant")) {
            out.println("// push constant " + index);
            out.println("@" + index);
            out.println("D=A");   // D = constante
            out.println("@SP");
            out.println("A=M");   // Aponta pro topo da pilha
            out.println("M=D");   // Coloca D lá dentro
            out.println("@SP");
            out.println("M=M+1"); // Avança o ponteiro da pilha
        }
    }

    public void close() {
        out.close();
    }
}