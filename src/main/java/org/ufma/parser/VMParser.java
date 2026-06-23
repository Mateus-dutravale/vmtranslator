package org.ufma.parser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class VMParser {

    private final List<String[]> commands;
    private int currentIndex;
    private String[] currentCommand;

    public VMParser(String filename) throws IOException {
        this.commands = new ArrayList<>();
        this.currentIndex = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;

            while ((line = br.readLine()) != null) {

                line = line.trim();

                // Ignora linhas vazias e comentários completos
                if (line.isEmpty() || line.startsWith("//")) {
                    continue;
                }

                // Remove comentários no final da linha
                int commentIndex = line.indexOf("//");
                if (commentIndex != -1) {
                    line = line.substring(0, commentIndex).trim();
                }

                if (!line.isEmpty()) {
                    commands.add(line.split("\\s+"));
                }
            }
        }
    }

    public boolean hasMoreCommands() {
        return currentIndex < commands.size();
    }


    public void advance() {
        if (hasMoreCommands()) {
            currentCommand = commands.get(currentIndex++);
        }
    }

    public CommandType commandType() {
        String cmd = currentCommand[0];

        switch (cmd) {
            case "push":
                return CommandType.C_PUSH;

            case "pop":
                return CommandType.C_POP;

            case "label":
                return CommandType.C_LABEL;

            case "goto":
                return CommandType.C_GOTO;

            case "if-goto":
                return CommandType.C_IF;

            case "function":
                return CommandType.C_FUNCTION;

            case "call":
                return CommandType.C_CALL;

            case "return":
                return CommandType.C_RETURN;

            default:
                return CommandType.C_ARITHMETIC;
        }
    }

    public String arg1() {
        CommandType type = commandType();

        if (type == CommandType.C_RETURN) {
            throw new IllegalStateException("C_RETURN não possui arg1()");
        }

        if (type == CommandType.C_ARITHMETIC) {
            return currentCommand[0];
        }

        return currentCommand[1];
    }

    public int arg2() {
        // Apenas para push/pop nesta fase
        return Integer.parseInt(currentCommand[2]);
    }
}