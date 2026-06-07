package org.ufma.main;

import org.ufma.parser.VMParser;
import org.ufma.parser.CommandType;
import org.ufma.codewriter.CodeWriter;
import java.io.IOException;

public class VMTranslator {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Erro: Passe o arquivo .vm como argumento.");
            return;
        }

        String input = args[0];
        String output = input.replace(".vm", ".asm");

        try {
            VMParser parser = new VMParser(input);
            CodeWriter writer = new CodeWriter(output);

            while (parser.hasMoreCommands()) {
                parser.advance();
                CommandType type = parser.commandType();

                if (type == CommandType.C_PUSH) {
                    writer.writePushPop("push", parser.arg1(), parser.arg2());
                } else if (type == CommandType.C_POP) {
                    writer.writePushPop("pop", parser.arg1(), parser.arg2());
                } else if (type == CommandType.C_ARITHMETIC) {
                    writer.writeArithmetic(parser.arg1());
                }
            }

            writer.close();
            System.out.println("Pronto! Arquivo gerado em: " + output);

        } catch (IOException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }
}