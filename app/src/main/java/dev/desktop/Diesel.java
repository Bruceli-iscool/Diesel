package dev.desktop;

import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class Diesel {
	// store variables
	protected static HashMap<String, Integer> intVars = new HashMap<>();
	protected static HashMap<String, String> stringVars = new HashMap<>();
	protected static HashMap<String, Boolean> boolVars = new HashMap<>();
	@SuppressWarnings("StatementWithEmptyBody")
    public static void preprocess(String filepath) throws ScriptException{
		// preprocess, read file, process comments, etc
        File saveFile = new File(filepath);
        List<String> content = new ArrayList<>();
        try (Scanner scanner = new Scanner(saveFile)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                // comments
                if (first(line.trim()).equals("//")) {
                    // skip
                } else {
                    content.add(line);
                }
        }
        } catch (FileNotFoundException n) {
            System.err.println("Diesel Interpreter Error!: File not Found!");
            System.exit(64);
        }
        launch(content);
	}
	public static void launch(List<String> content) throws ScriptException{
        int i = 0;
        for (String line:content) {
            i++;
            interpret(line, i);
        }
	}
        public static ArrayList<String> lex(String input) {
        // list of tokens in String
        ArrayList<String> result = new ArrayList<String>();
        String z = "";
        // detect if a String is created or not
        boolean ifString = false;
        // ifs are to end strings if it hits a token
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            switch(c) {
                case '(':
                    if (!z.isEmpty()) {
                        result.add(z);
                        z = "";
                    } 
                    result.add("(");
                    break;
                case ')':
                    if (!z.isEmpty()) {
                        result.add(z);
                        z = "";
                    } 
                    result.add(")");
                    break;
                case ';':
                    if (!z.isEmpty()) {
                        result.add(z);
                        z = "";
                    }
                    result.add(";");
                    break;
                case '=':
                    if (!z.isEmpty()) {
                        result.add(z);
                        z = "";
                    }
                    result.add("=");
                    break;
                case '"':
                    if (!z.isEmpty() && ifString) {
                        ifString = false;
                        result.add(z);
                        z = "";
                    } else {
                        ifString = true;
                    }
                    result.add("\"");
                    break;
                case ' ':
                    if (!z.isEmpty() && ifString == false) {
                        result.add(z);
                        z = "";
                    } else if (ifString) {
                        z += c;
                    }
                    break;
                default:
                    z += c;
                    break;
            }
        }

        if (!z.isEmpty()) {
            result.add(z);
        }
        
        return result;
    }
    public static void interpret(String line, int num) throws ScriptException {
        ArrayList<String> tokens = lex(line);
        String current = tokens.get(0);
        if (current.matches("int")) {
            tokens.remove(0);
            current = tokens.get(0);
            if (current.matches("([A-Za-z0-9\\-\\_]+)")) {
                String n = current;
                tokens.remove(0);
                current = tokens.get(0);
                if (current.matches("=")) {
                    tokens.remove(0);
                    current = tokens.get(0);
                    if (current.matches("^[a-zA-Z0-9*/+\\-_]+$")) {
                        String value = current;
                        for (String var:intVars.keySet()) {
                        	value = value.replace(var, String.valueOf(intVars.get(var)));
                        }
                        ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
                        Object l = engine.eval(value);
                        intVars.put(n, (int) l);
                        tokens.remove(0);
                        current = tokens.get(0);
                        if (current.matches(";")) {
                            return;
                        } else {
                            semicolonError(num);
                        }
                        // finish semicolon handling and debug
                    } else {
                        System.err.println("Diesel Interpreter Error!: Invalid Value for integer at line " + num);
                    }
                } else {
                    if (current.matches(";")) {
                        tokens.remove(0);
                        intVars.put(n, 0);
                        return;
                    } else {
                        semicolonError(num);
                    }
                }
            } else {
                System.err.println("Diesel Interpreter Error!: Invalid Variable name at line " + num);
            }
        } else if (current.matches("String")) {
            tokens.remove(0);
            current = tokens.get(0);
            if (current.matches("([A-Za-z0-9\\-\\_]+)")) {
                String n = current;
                tokens.remove(0);
                current = tokens.get(0);
                // todo
            } else {
                System.err.println("Diesel Interpreter Error!: Invalid Variable name at line " + num);
            }
        }
    }
    // Helper functions
    public static String first(String str) {          
        if (str.length() < 2) {
            return str;
        } else {
            return str.substring(0, 2);
        }
    }

    public static void semicolonError(int line) {
        System.err.println("Diesel Interpreter Error!: Expected ';' at line " + line);
    }
}