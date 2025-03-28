package dev.desktop;

import javax.script.ScriptException;
import java.util.Scanner;

public class App {
    public static void main(String[] args) throws ScriptException {
        try {
            // fix errors
            Diesel.interpret("String c = \"cooler\";", 1);
            Diesel.interpret("int e = 4+4;", 2);
            Diesel.interpret("e = e+ 5;", 30);
            Diesel.interpret("c = c+\"j\";", 3);
            Diesel.interpret("bool a = false;", 70);
            Diesel.interpret("a = 2 == 2;", 80);
            Diesel.interpret("function bool sum()", 4);
            Diesel.interpret("return true;", 5);
            Diesel.interpret("end", 6);
            Diesel.interpret("bool o = sum()!=true;", 60);
            Diesel.interpret("if (true == false) ", 400);
            Diesel.interpret("a = false;", 200);
            Diesel.interpret("end", 100);
        } catch (ScriptException e) {
            throw new RuntimeException(e);
        }

        if (args.length != 1) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Please provide the input file path:");
            String inputFilePath = scanner.nextLine();
            Diesel.preprocess(inputFilePath);
            scanner.close();

            System.out.println(Diesel.ints);
            System.out.println(Diesel.strings);
            System.out.println(Diesel.booleans);
            System.out.println(Diesel.procedures);
            System.out.println(Diesel.intFunctions);
            System.out.println(Diesel.stringFunctions);
            System.out.println(Diesel.boolFunctions);
        } else {
            Diesel.preprocess(args[0]);
        }

    }
}