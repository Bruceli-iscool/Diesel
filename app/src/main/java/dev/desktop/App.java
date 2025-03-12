package dev.desktop;

public class App {
    public static void main(String[] args) {
        Diesel.interpret("String a  = \"cool stuff\";", 1);
        System.out.println(Diesel.stringVars);
        if (args.length < 1 || args.length > 1) {
            System.err.println("Diesel Interpreter Error!: Usage: dsl [filename]");
            System.exit(0);
        } else {
            Diesel.preprocess(args[0]);
        }
    }

}