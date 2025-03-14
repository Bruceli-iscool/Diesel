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
    // might need to change type for arguments
    protected static HashMap<String, HashMap<ArrayList<String>, ArrayList<String>>> procedures = new HashMap<>();
    protected static int stack = 0;
    protected static int mode = 0;
    protected static ArrayList<String> temp = new ArrayList<>();
    protected static String tempName = "";
    @SuppressWarnings("StatementWithEmptyBody")
    public static void preprocess(String filepath) throws ScriptException {
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

    public static void launch(List<String> content) throws ScriptException {
        int i = 0;
        for (String line : content) {
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
            switch (c) {
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
        tokens.add(tokens.size(), "");
        String current = tokens.get(0);
        if (stack < 1) {
            try {
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
                                for (String var : intVars.keySet()) {
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
                        System.err.println("Diesel Interpreter Error!: Invalid indentifier name at line " + num);
                    }
                } else if (current.matches("String")) {
                    tokens.remove(0);
                    current = tokens.get(0);
                    if (current.matches("([A-Za-z0-9\\-\\_]+)")) {
                        String n = current;
                        tokens.remove(0);
                        current = tokens.get(0);
                        if (current.matches("=")) {
                            tokens.remove(0);
                            current = tokens.get(0);
                            String value = "";
                            while (!current.matches(";")) {
                                value = value + current;
                                tokens.remove(0);
                                current = tokens.get(0);
                            }
                            int i = 0;
                            String result = "";
                            boolean u = false;
                            while (i < value.length()) {
                                char c = value.charAt(i);
                                if (c == '"') {
                                    u = !u;
                                }
                                if (u) {
                                    result += c;
                                    i++;
                                    continue;
                                }
                                boolean replaced = false;
                                for (String var : stringVars.keySet()) {
                                    if (value.startsWith(var, i)) {
                                        result += stringVars.get(var);
                                        i += var.length();
                                        replaced = true;
                                        break;
                                    }
                                }
                                if (!replaced) {
                                    result += c;
                                    i++;
                                }
                            }
                            value = result;
                            ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
                            Object c = engine.eval(value);
                            stringVars.put(n, (String) c);
                            if (current.matches(";")) {
                                return;
                            } else {
                                semicolonError(num);
                            }
                        } else {
                            if (current.matches(";")) {
                                tokens.remove(0);
                                stringVars.put(n, "");
                            } else {
                                semicolonError(num);
                            }
                        }
                    } else {
                        System.err.println("Diesel Interpreter Error!: Invalid indentifier name at line " + num);
                    }
                } else if (current.matches("bool")) {
                    tokens.remove(0);
                    current = tokens.get(0);
                    if (current.matches("([A-Za-z0-9\\-\\_]+)")) {
                        String n = current;
                        tokens.remove(0);
                        current = tokens.get(0);
                        if (current.matches("=")) {
                            tokens.remove(0);
                            current = tokens.get(0);
                            if (current.matches("true") || current.matches("false")) {
                                ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
                                Object value = engine.eval(current);
                                boolVars.put(n, Boolean.parseBoolean((String)value));
                                tokens.remove(0);
                                current = tokens.get(0);
                                if (current.matches(";")) {
                                    return;
                                } else {
                                    semicolonError(num);
                                }
                            } else if (current.matches("([A-Za-z0-9\\\\-\\\\_\\=\\!\\&\\|]+)") && !current.matches("true")
                                    && !current.matches("false")) {
                                String value = current;
                                for (String var : boolVars.keySet()) {
                                    value = value.replace(var, String.valueOf(boolVars.get(var)));
                                }
                                boolVars.put(n, Boolean.parseBoolean(value));    
                            } else {
                                System.err.println("Diesel Interpreter Error!: Invalid Boolean value at line " + num);
                            }
                        } else {
                            if (current.matches(";")) {
                                tokens.remove(0);
                                boolVars.put(n, false);
                            } else {
                                semicolonError(num);
                            }
                        }
                    } else {
                        System.err.println("Diesel Interpreter Error!: Invalid indentifier name at line " + num);
                    }
                } else if (current.matches("procedure")) {
                    ArrayList<String> args = new ArrayList<>();
                    tokens.remove(0);
                    current = tokens.get(0);
                    if (current.matches("([a-zA-Z0-9\\_]+)")) {
                        tempName = current;
                        tokens.remove(0);
                        current = tokens.get(0);
                        if (current.matches("\\(")) {
                            // todo for args support
                            tokens.remove(0);
                            current = tokens.get(0);
                            if (current.matches("\\)")) {
                                mode = 1;
                                stack = 1;
                            } else {
                                System.out.println("Diesel Interpreter Error!: Expected \")\" at line " + num);
                            }
                        }
                    } else {
                        System.err.println("Diesel Interpreter Error!: Invalid indentifier name at line " + num);
                    }
                }
            } catch (Exception e) {
                System.err.println("Diesel Interpreter Error!: An Unknown Error Occured at line " + num);
            }
        } else if (stack >0 && mode == 1) {
            if (current.matches("end")) {
                stack -= 1;
                procedures.put(tempName, temp);
                tempName = "";
                temp = new ArrayList<>();
            } else {
                temp.add(line);
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