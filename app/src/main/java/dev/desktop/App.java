package dev.desktop;

import javax.script.ScriptException;

public class App {
    public static void main(String[] args) throws ScriptException {
        try {
            Diesel.interpret("int c = 2+4;", 1);
            Diesel.interpret("int b = c+4;", 2);
        } catch (ScriptException e) {
            throw new RuntimeException(e);
        }
        System.out.println(Diesel.intVars);
        if (args.length != 1) {
            System.err.println("Diesel Interpreter Error!: Usage: dsl [filename]");
            System.exit(0);
        } else {
            Diesel.preprocess(args[0]);
        }
    }

}