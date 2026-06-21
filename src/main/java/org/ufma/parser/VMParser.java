package org.ufma.parser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class VMParser {
    private List<String[]> commands;
    private int currentIndex;
    private String[] currentCommand;

    public VMParser(String filename) throws IOException {
        this.commands = new ArrayList<>();
        this.currentIndex = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Remove espaços nas pontas e ignora linhas vazias ou comentários completos
                line = line.trim();
                if (line.isEmpty() || line.startsWith("//")) {
                    continue;
                }

                // Remove comentários que ficam no final da linha de comando
                if (line.contains("//")) {
                    line = line.split("//")[0].trim();
                }

                // Divide a linha por espaços (trata múltiplos espaços seguidos)
                commands.add(line.split("\\s+"));
            }
        }
    }

    public boolean hasMoreCommands() {
        return currentIndex < commands.size();
    }

    public void advance() {
        currentCommand = commands.get(currentIndex);
        currentIndex++;
    }

    public CommandType commandType() {
        String cmd = currentCommand[0];
        if (cmd.equals("push")) return CommandType.C_PUSH;
        else if (cmd.equals("pop")) return CommandType.C_POP;
        else if (cmd.equals("label")) return CommandType.C_LABEL;
        else if (cmd.equals("goto")) return CommandType.C_GOTO;
        else if (cmd.equals("if-goto")) return CommandType.C_IF;
        else if (cmd.equals("function")) return CommandType.C_FUNCTION;
        else if (cmd.equals("call")) return CommandType.C_CALL;
        else if (cmd.equals("return")) return CommandType.C_RETURN;
        else return CommandType.C_ARITHMETIC;
    }

    public String arg1() {
        if (commandType() == CommandType.C_ARITHMETIC) {
            return currentCommand[0]; // Retorna o próprio comando (ex: "add")
        }
        return currentCommand[1]; // Retorna o segmento (ex: "local")
    }

    public int arg2() {
        // Apenas para push/pop nesta fase
        return Integer.parseInt(currentCommand[2]);
    }
}