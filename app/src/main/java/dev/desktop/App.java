package dev.desktop;

import javax.script.ScriptException;

public class App {
    public static void main(String[] args) throws ScriptException {
        try {
            // fix errors
            Diesel.interpret("String c = \"cooler\";", 1);
            Diesel.interpret("int b = 4+4;", 2);
            Diesel.interpret("b = b+ 5;", 30);
            Diesel.interpret("c = c+\"j\";", 3);
            Diesel.interpret("bool a = false;", 70);
            Diesel.interpret("a = 1 == 2;", 80);
            Diesel.interpret("procedure l(int l, int b)", 4);
            Diesel.interpret("bool k = 2!=3;", 5);
            Diesel.interpret("end", 6);
        } catch (ScriptException e) {
            throw new RuntimeException(e);
        }
        System.out.println(Diesel.intVars);
        System.out.println(Diesel.stringVars);
        System.out.println(Diesel.boolVars);
        System.out.println(Diesel.procedures);
        if (args.length != 1) {
            System.err.println("Diesel Interpreter Error!: Usage: dsl [filename]");
            System.exit(0);
        } else {
            Diesel.preprocess(args[0]);
        }
    }

}