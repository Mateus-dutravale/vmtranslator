package org.ufma.main;

import org.ufma.parser.VMParser;
import org.ufma.parser.CommandType;
import org.ufma.codewriter.CodeWriter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
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

            if (!inputPath.getName().endsWith(".vm")) {
                System.out.println("Erro: O arquivo fornecido nao possui a extensao .vm");
                return;
            }

            vmFiles.add(inputPath);
            outputFilename = inputPath.getAbsolutePath().replace(".vm", ".asm");

        } else if (inputPath.isDirectory()) {

            outputFilename =
                    inputPath.getAbsolutePath()
                            + File.separator
                            + inputPath.getName()
                            + ".asm";

            File[] files = inputPath.listFiles();

            if (files != null) {

                // IMPORTANTE:
                // Garante ordem deterministica dos arquivos
                Arrays.sort(files, Comparator.comparing(File::getName));

                for (File file : files) {
                    if (file.isFile() && file.getName().endsWith(".vm")) {
                        vmFiles.add(file);
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

            // Bootstrap apenas quando existir Sys.vm
            if (inputPath.isDirectory()) {

                boolean containsSysVm = false;

                for (File file : vmFiles) {
                    if (file.getName().equalsIgnoreCase("Sys.vm")) {
                        containsSysVm = true;
                        break;
                    }
                }

                if (containsSysVm) {
                    System.out.println("Injetando codigo de Bootstrapping...");
                    writer.writeInit();
                } else {
                    System.out.println(
                            "Diretorio detectado, mas Sys.vm nao foi encontrado. Pulando Bootstrapping..."
                    );
                }
            }

            for (File vmFile : vmFiles) {

                System.out.println("Traduzindo: " + vmFile.getName());

                writer.setFileName(
                        vmFile.getName().replace(".vm", "")
                );

                VMParser parser =
                        new VMParser(vmFile.getAbsolutePath());

                while (parser.hasMoreCommands()) {

                    parser.advance();

                    CommandType type =
                            parser.commandType();

                    switch (type) {

                        case C_PUSH:
                            writer.writePushPop(
                                    "push",
                                    parser.arg1(),
                                    parser.arg2()
                            );
                            break;

                        case C_POP:
                            writer.writePushPop(
                                    "pop",
                                    parser.arg1(),
                                    parser.arg2()
                            );
                            break;

                        case C_ARITHMETIC:
                            writer.writeArithmetic(
                                    parser.arg1()
                            );
                            break;

                        case C_LABEL:
                            writer.writeLabel(
                                    parser.arg1()
                            );
                            break;

                        case C_GOTO:
                            writer.writeGoto(
                                    parser.arg1()
                            );
                            break;

                        case C_IF:
                            writer.writeIf(
                                    parser.arg1()
                            );
                            break;

                        case C_FUNCTION:
                            writer.writeFunction(
                                    parser.arg1(),
                                    parser.arg2()
                            );
                            break;

                        case C_CALL:
                            writer.writeCall(
                                    parser.arg1(),
                                    parser.arg2()
                            );
                            break;

                        case C_RETURN:
                            writer.writeReturn();
                            break;
                    }
                }
            }

            writer.close();

            System.out.println(" TRADUCAO CONCLUIDA COM SUCESSO!");

        } catch (IOException e) {

            System.out.println(
                    "Erro ao processar traducao: "
                            + e.getMessage()
            );
        }
    }
}