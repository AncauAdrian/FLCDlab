package com.company;

import java.io.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final List<String> operators = new LinkedList<>();
    private static final List<String> reservedWords = new LinkedList<>();
    private static final String[] separators = {",", ";", "{", "}", "(", ")", " ", "\""};
    private static final HashMap<Integer> symbolTable = new HashMap<>();
    private static final List<Pair<String, Pair<Integer, Integer>>> pif = new LinkedList<>();

    private enum TokenType {
        CONSTANT_NUMBER,
        CONSTANT_STRING,
        IDENTIFIER,
        RESERVED,
        OPERATOR,
        SEPARATOR
    }

    private static int findFirstSeparator(String string) {
        int position = -1, newPos;
        for (String separator : separators) {
            newPos = string.indexOf(separator);
            if (newPos != -1)
                if (position == -1)
                    position = newPos;
                else if (newPos < position)
                    position = newPos;
        }
        return position;
    }

    private static void readTokensList(String path) {
        try {
            File tokensFile = new File("tokens.in");
            Scanner reader = new Scanner(tokensFile);

            // Read the tokens (each is separated by a white space)

            String line = reader.nextLine();

            // Store operators
            operators.addAll(Arrays.asList(line.split("\\s+")));

            // skip separators
            reader.nextLine();
            line = reader.nextLine();

            // Store reserved words
            reservedWords.addAll(Arrays.asList(line.split("\\s+")));

            reader.close();
        } catch (FileNotFoundException e) {
            System.out.println("Could not open file: tokens.in");
            e.printStackTrace();
        }
    }

    public static TokenType identifyToken(String token) {
        if(reservedWords.contains(token))
            return TokenType.RESERVED;

        if(operators.contains(token))
            return TokenType.OPERATOR;

        for (String separator :
                separators) {
            if(separator.equals(token))
                return TokenType.SEPARATOR;
        }

        if(token.matches("^[+-]?([1-9][0-9]*(\\.[0-9]*[1-9])?)$"))
            return TokenType.CONSTANT_NUMBER;

        if(token.matches("^\".*\"$"))
            return TokenType.CONSTANT_STRING;

        if(token.matches("^[_A-z]+[0-9_A-z]*$"))
            return TokenType.IDENTIFIER;

        return null;
    }

    public static boolean processToken(String token) {
        TokenType type = identifyToken(token);
        if(type == null)
            return false;

        if(type == TokenType.IDENTIFIER) {
            Pair<Integer, Integer> position = symbolTable.search(token);
            if(symbolTable.search(token) == null) {
                pif.add(new Pair<>("ID", symbolTable.add(token, 0)));
            }

            pif.add(new Pair<>("ID", position));
            return true;
        }

        if(type == TokenType.CONSTANT_NUMBER || type == TokenType.CONSTANT_STRING) {
            pif.add(new Pair<>("CONST", symbolTable.add(token, null)));
            return true;
        }

        if(type == TokenType.RESERVED || type == TokenType.OPERATOR || type == TokenType.SEPARATOR) {
            pif.add(new Pair<>(token, null));
            return true;
        }

        return false;
    }

    public static void readProgram(String path) {
        Scanner codeReader;
        try {
            File codeFile = new File(path);
            codeReader = new Scanner(codeFile);
        } catch (FileNotFoundException e) {
            System.out.println("Could not open program file: " + path);
            e.printStackTrace();
            return;
        }

        System.out.println("\n\nReading Program File: " + path + "\n");
        boolean error = false;
        int lineNumber = 0;
        String errorLine = "", errorReason = "";
        while (codeReader.hasNextLine() && !error) {
            // Read next line and strip leading and trailing whitespace characters
            String line = codeReader.nextLine().strip();
            errorLine = line;
//            System.out.println(line.strip());

            if (line.compareTo("{") == 0 || line.compareTo("}") == 0) {
//                System.out.println("token: " + line);
                processToken(line);
                continue;
            }

            int position = findFirstSeparator(line);

            if (position == -1) {
//                System.out.println("!!!!! is this registered in PIF? - " + line);
                if(!processToken(line)) {
                    error = true;
                    errorReason = "Could not identify token: " + line;
                    break;
                }
            } else {
//                System.out.println("tokens need separation: " + line);
                String token;
                char separator;
                while (!line.isBlank() && position != -1) {
                    token = line.substring(0, position).strip();
                    if (!token.isBlank()) {
//                        System.out.println("token: " + token);
                        if(!processToken(token)) {
                            error = true;
                            errorReason = "Could not identify token: " + token;
                            break;
                        }
                    }

                    separator = line.charAt(position);
                    if (separator == '\"') {
                        int lastPosition = line.substring(position + 1).indexOf('\"');
                        if (lastPosition == -1 || lastPosition == line.length()) {
                            error = true;
                            errorReason = "No closing brackets for Character String";
                        }

                        String str_token = line.substring(position, lastPosition + 2);
//                        System.out.println("token: " + str_token);

                        if(!processToken(str_token)) {
                            error = true;
                            errorReason = "Could not identify token: " + token;
                            break;
                        }

                        line = line.substring(lastPosition + 2);
                        continue;
                    }

                    if (separator != ' ') {
//                        System.out.println("token: " + separator);
                        if(!processToken(String.valueOf(separator))) {
                            error = true;
                            errorReason = "Could not identify token: " + token;
                            break;
                        }
                    }

                    line = line.substring(position + 1);

                    position = findFirstSeparator(line);
                }

//                System.out.println("last token: " + line);
            }

            lineNumber++;
        }
        if(error) {
            System.out.println("Error at line number >>> " + lineNumber);
            System.out.println(errorLine);
            System.out.println("Reason - " + errorReason);
        }
        else System.out.println("Program is Lexically Correct");

        codeReader.close();
    }

    static void writeSymTable() {
        try {
            File symTableFile = new File("ST.out");
            if(symTableFile.createNewFile())
                System.out.println("Created file " + symTableFile.getName());

            FileWriter fileWriter = new FileWriter(symTableFile);
            fileWriter.write("The SymTable is implemented by a HashMap using linked list conflict resolution\n");
            fileWriter.write(symbolTable.toString());

            fileWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void writePif() {
        try {
            File pifFile = new File("PIF.out");
            FileWriter pifWriter = new FileWriter(pifFile);

            if(pifFile.createNewFile())
                System.out.println("Created file " + pifFile.getName());

            for (Pair<String, Pair<Integer, Integer>> e:
                    pif) {
                pifWriter.write(e.toString() + '\n');
            }
            pifWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        readTokensList("tokens.in");
        if (operators.isEmpty() || reservedWords.isEmpty())
            return;

        System.out.println("Successfully read tokens.in file!\nOperators: " + operators);
        System.out.println("Reserved Words: " + reservedWords);
        System.out.println("Separators: " + Arrays.toString(separators));

        readProgram(args[0]);
        writeSymTable();
        writePif();
    }
}
