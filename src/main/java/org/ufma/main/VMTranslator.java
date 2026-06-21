package org.ufma.main;

import org.ufma.parser.VMParser;
import org.ufma.parser.CommandType;
import org.ufma.codewriter.CodeWriter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class VMTranslator {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Erro: Forneca o caminho de um arquivo .vm ou de um diretorio.");
            return;
        }

        File inputPath = new File(args[0]);
        List<File> vmFiles = new ArrayList<>();
        String outputFilename;

        if (inputPath.isFile()) {
            if (inputPath.getName().endsWith(".vm")) {
                vmFiles.add(inputPath);
                outputFilename = inputPath.getAbsolutePath().replace(".vm", ".asm");
            } else {
                System.out.println("Erro: O arquivo fornecido nao possui a extensao .vm");
                return;
            }
        } else if (inputPath.isDirectory()) {
            String path = inputPath.getAbsolutePath();
            outputFilename = path + File.separator + inputPath.getName() + ".asm";

            File[] files = inputPath.listFiles();
            if (files != null) {
                for (File f : files) {
                    if (f.isFile() && f.getName().endsWith(".vm")) {
                        vmFiles.add(f);
                    }
                }
            }
        } else {
            System.out.println("Erro: Caminho invalido.");
            return;
        }

        if (vmFiles.isEmpty()) {
            System.out.println("Nenhum arquivo .vm encontrado para traduzir.");
            return;
        }

        try {
            CodeWriter writer = new CodeWriter(outputFilename);
            System.out.println("Gerando arquivo unificado: " + outputFilename);

            // Injeta o Bootstrap antes de traduzir os arquivos se for um diretorio
            if (inputPath.isDirectory()) {
                System.out.println("Injetando codigo de Bootstrapping...");
                writer.writeInit();
            }

            for (File vmFile : vmFiles) {
                // Informa ao CodeWriter o nome do arquivo atual para as variaveis estaticas
                writer.setFileName(vmFile.getName());

                System.out.println("Traduzindo: " + vmFile.getName());
                VMParser parser = new VMParser(vmFile.getAbsolutePath());

                while (parser.hasMoreCommands()) {
                    parser.advance();
                    CommandType type = parser.commandType();

                    if (type == CommandType.C_PUSH) {
                        writer.writePushPop("push", parser.arg1(), parser.arg2());
                    } else if (type == CommandType.C_POP) {
                        writer.writePushPop("pop", parser.arg1(), parser.arg2());
                    } else if (type == CommandType.C_ARITHMETIC) {
                        writer.writeArithmetic(parser.arg1());
                    } else if (type == CommandType.C_LABEL) {
                        writer.writeLabel(parser.arg1());
                    } else if (type == CommandType.C_GOTO) {
                        writer.writeGoto(parser.arg1());
                    } else if (type == CommandType.C_IF) {
                        writer.writeIf(parser.arg1());
                    } else if (type == CommandType.C_FUNCTION) {
                        writer.writeFunction(parser.arg1(), parser.arg2());
                    } else if (type == CommandType.C_RETURN) {
                        writer.writeReturn();
                    } else if (type == CommandType.C_CALL) {
                        writer.writeCall(parser.arg1(), parser.arg2());
                    }
                }
            }

            writer.close();
            System.out.println("=========================================");
            System.out.println(" TRADUCAO CONCLUIDA COM SUCESSO!");
            System.out.println("=========================================");

        } catch (IOException e) {
            System.out.println("Erro ao processar traducao: " + e.getMessage());
        }
    }
}