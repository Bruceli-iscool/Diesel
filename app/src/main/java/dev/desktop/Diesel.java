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
    protected static HashMap<String, Integer> ints = new HashMap<>();
    protected static HashMap<String, String> strings = new HashMap<>();
    protected static HashMap<String, Boolean> booleans = new HashMap<>();
    protected static HashMap<String, HashMap<String, ArrayList<String>>> procedures = new HashMap<>();
    protected static HashMap<String, HashMap<String, ArrayList<String>>> intFunctions = new HashMap<>();
    protected static HashMap<String, HashMap<String, ArrayList<String>>> stringFunctions = new HashMap<>();
    protected static HashMap<String, HashMap<String, ArrayList<String>>> boolFunctions = new HashMap<>();
    protected static int stack = 0;
    protected static int mode = 0;
    protected static ArrayList<String> temp = new ArrayList<>();
    protected static String tempName = "";
    protected static String args = "";

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
                case '(': case ')': case ';': case '=': case '+': case '-': case '*': case '/':
                    if (!z.isEmpty()) {
                        result.add(z);
                        z = "";
                    }
                    result.add(String.valueOf(c));
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
                    if (!z.isEmpty() && !ifString) {
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
        interpret(line, num, strings, ints, booleans);
    }
    public static void interpret(String line, int num, HashMap<String, String> stringVars, HashMap<String, Integer> intVars, HashMap<String, Boolean> boolVars) throws ScriptException {
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
                                String value = "";
                                while (!current.matches(";")) {
                                    value = value + current;
                                    tokens.remove(0);
                                    current = tokens.get(0);
                                }
                                int result = processInt(value, intVars, num);
                                intVars.put(n, result);
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
                            String result = processString(value, stringVars, num);
                            stringVars.put(n, result);
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
                                String value = current;
                                boolVars.put(n, processBool(value, boolVars, num, 1)); 
                                tokens.remove(0);
                                current = tokens.get(0);
                                if (current.matches(";")) {
                                    return;
                                } else {
                                    semicolonError(num);
                                }
                            } else if (current.matches("([A-Za-z0-9\\\\-\\\\_\\=\\!\\&\\|]+)") && !current.matches("true")
                                    && !current.matches("false")) {
                                String value = "";
                                while (!current.matches(";")) {
                                    value = value + current;
                                    tokens.remove(0);
                                    current = tokens.get(0);
                                }
                                boolVars.put(n, processBool(value, boolVars, num, 2));
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
                    tokens.remove(0);
                    current = tokens.get(0);
                    if (current.matches("([a-zA-Z0-9\\_]+)")) {
                        tempName = current;
                        tokens.remove(0);
                        current = tokens.get(0);
                        if (current.matches("\\(")) {
                            tokens.remove(0);
                            current = tokens.get(0);
                            String argString = "";
                            while (!current.matches("\\)") && !current.matches(" ")) {
                                argString += " " + current;
                                tokens.remove(0);
                                current = tokens.get(0);
                            }
                            if (current.matches("\\)")) {
                                mode = 1;
                                stack = 1;
                                args = argString.trim();
                            } else { 
                                System.out.println("Diesel Interpreter Error!: Expected \")\" at line " + num);
                            }
                        }
                    } else {
                        System.err.println("Diesel Interpreter Error!: Invalid indentifier name at line " + num);
                    }
                } else if (intVars.containsKey(current)) {
                    String n = current;
                    tokens.remove(0);
                    current = tokens.get(0);
                    if (current.matches("=")) {
                        tokens.remove(0);
                        current = tokens.get(0);
                        if (current.matches("^[a-zA-Z0-9*/+\\-_]+$")) {
                            String value = "";
                            while (!current.matches(";")) {
                                value = value + current;
                                tokens.remove(0);
                                current = tokens.get(0);
                            }
                            int result = processInt(value, intVars, num);
                            intVars.put(n, result);
                            if (current.matches(";")) {
                                return;
                            } else {
                                semicolonError(num);
                            }
                        } else {
                            System.err.println("Diesel Interpreter Error!: Invalid Value for integer at line " + num);
                        }
                    } else {
                        System.err.println("Diesel Interpreter Error!: Expected \"=\" at line  " + num);                       
                    }
                } else if (stringVars.containsKey(current)) {
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
                        String result = processString(value, stringVars, num);
                        stringVars.put(n, result);
                        if (current.matches(";")) {
                            return;
                        } else {
                            semicolonError(num);
                        }
                    } else {
                        System.err.println("Diesel Interpreter Error!: Expected \"=\" at line  " + num);                       
                    }
                } else if (boolVars.containsKey(current)) {
                    String n = current;
                    tokens.remove(0);
                    current = tokens.get(0);
                    if (current.matches("=")) {
                        tokens.remove(0);
                        current = tokens.get(0);
                            if (current.matches("true") || current.matches("false")) {
                                String value = current;
                                boolVars.put(n, processBool(value, boolVars, num, 1)); 
                                tokens.remove(0);
                                current = tokens.get(0);
                                if (current.matches(";")) {
                                    return;
                                } else {
                                    semicolonError(num);
                                }
                            } else if (current.matches("([A-Za-z0-9\\\\-\\\\_\\=\\!\\&\\|]+)") && !current.matches("true")
                                    && !current.matches("false")) {
                                String value = "";
                                while (!current.matches(";")) {
                                    value = value + current;
                                    tokens.remove(0);
                                    current = tokens.get(0);
                                }
                                boolVars.put(n, processBool(value, boolVars, num, 2));    
                            } else {
                                System.err.println("Diesel Interpreter Error!: Invalid Boolean value at line " + num);
                            }
                        
                    } else {
                        System.err.println("Diesel Interpreter Error!: Expected \"=\" at line  " + num);        
                    }
                } else if (procedures.containsKey(current)) {
                    String n = current;
                    tokens.remove(0);
                    current = tokens.get(0);
                    if (current.matches("\\(")) {
                        tokens.remove(0);
                        current = tokens.get(0);
                        String argString = "";
                        while (!current.matches("\\)") && !current.matches(" ")) {
                            argString += " " + current;
                            tokens.remove(0);
                            current = tokens.get(0);
                        }
                        if (current.matches("\\)")) {
                            tokens.remove(0);
                            current = tokens.get(0);
                            if (current.matches(";")) {
                                HashMap<String, String> tempStrings = new HashMap<>();
                                HashMap<String, Integer> tempIntegers = new HashMap<>();
                                HashMap<String, Boolean> tempBools = new HashMap<>();
                                ArrayList<Integer> modes = new ArrayList<>();
                                ArrayList<String> names = new ArrayList<>();
                                HashMap<String, ArrayList<String>> args2 = procedures.get(n);
                                String args4 = "";
                                for ( String key : args2.keySet() ) {
                                    args4 = key;
                                }
                                String[] e = args4.split(",");
                                for (String i:e) {
                                    String[] arr2 = i.trim().split(" ");
                                    if (arr2[0].equals("int")) {
                                        tempIntegers.put(arr2[1], 0);
                                        modes.add(1);
                                        names.add(arr2[1]);
                                    } else if (arr2[0].equals("String")) {
                                        tempStrings.put(arr2[1], "");
                                        modes.add(2);
                                        names.add(arr2[1]);
                                    } else if (arr2[0].equals("bool")) {
                                        tempBools.put(arr2[1], false); 
                                        modes.add(3);
                                        names.add(arr2[1]);
                                    } else {
                                        System.out.println("Diesel Interpreter Error!: Invalid type at line " + num);                                                     
                                    }
                                }
                                String[] v = argString.split(",");
                                int index = 0;
                                for (String i:v) {
                                    if (modes.get(index) == 1) {
                                        String value = i;
                                        int result = processInt(value, intVars, num);
                                        tempIntegers.put(names.get(index), result);
                                    } else if (modes.get(index) == 2) {
                                        String value = i;
                                        String result = processString(value, stringVars, num);
                                        tempStrings.put(names.get(index), result);
                                    } else if (modes.get(index) == 3) {
                                        String value = i;
                                        Boolean result = false;
                                        if (value.matches("true") || value.matches("false")) {
                                            result = processBool(value, boolVars, num, 1);
                                        } else {
                                            result = processBool(value, boolVars, num, 2);
                                        }
                                        tempBools.put(names.get(index), result);
                                    }
                                    index++;
                                }
                                ArrayList<String> lines = new ArrayList<>();
                                for (ArrayList<String> i:args2.values()) {
                                    lines = i;
                                }
                                for (String k:lines) {
                                    interpret(k, num, tempStrings, tempIntegers, tempBools);
                                }
                            } else {
                                semicolonError(num);
                            }
                        } else {
                            System.out.println("Diesel Interpreter Error!: Expected \")\" at line " + num);                            
                        }
                    } else {
                        System.out.println("Diesel Interpreter Error!: Expected \"(\" at line " + num);                        
                    }
                } else if (current.matches("function")) {
                    tokens.remove(0);
                    current = tokens.get(0);
                    String m = "";
                    if (current.matches("int")) {
                        m = "int";
                    } else if (current.matches("String")) {
                        m = "String";
                    } else if (current.matches("bool")) {
                        m = "bool";
                    }
                    tokens.remove(0);
                    current = tokens.get(0);
                    tempName = current;
                    tokens.remove(0);
                    current = tokens.get(0);
                    if (current.matches("\\(")) {
                        tokens.remove(0);
                        current = tokens.get(0);
                        String argString = "";
                        while (!current.matches("\\)") && !current.matches(" ")) {
                            argString += " " + current;
                            tokens.remove(0);
                            current = tokens.get(0);
                        }
                        if (current.matches("\\)")) {
                            if (m == "int")mode = 2;
                            else if (m=="String")mode = 3;
                            else if (m=="bool")mode = 4;
                            stack = 1;
                            args = argString.trim();
                        } else { 
                            System.out.println("Diesel Interpreter Error!: Expected \")\" at line " + num);
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("Diesel Interpreter Error!: An Unknown Error Occured at line " + num);
            }
        } else if (stack >0 && mode == 1) {
            if (current.matches("end")) {
                stack -= 1;
                HashMap<String, ArrayList<String>> n = new HashMap<>();
                n.put(args, temp);
                procedures.put(tempName, n); 
                tempName = "";
                temp = new ArrayList<>();
                args = "";
            } else {
                temp.add(line);
            }
        } else if (stack>0 && mode == 2) {
            if (current.matches("end")) {
                stack -= 1;
                HashMap<String, ArrayList<String>> n = new HashMap<>();
                n.put(args, temp);
                intFunctions.put(tempName, n); 
                tempName = "";
                temp = new ArrayList<>();
                args = "";                
            } else {
                temp.add(line);
            } 
        } else if (stack>0 && mode == 3) {
                stack -= 1;
                HashMap<String, ArrayList<String>> n = new HashMap<>();
                n.put(args, temp);
                stringFunctions.put(tempName, n); 
                tempName = "";
                temp = new ArrayList<>();
                args = "";
        } else if (stack>0&& mode == 4) {
                stack -= 1;
                HashMap<String, ArrayList<String>> n = new HashMap<>();
                n.put(args, temp);
                boolFunctions.put(tempName, n); 
                tempName = "";
                temp = new ArrayList<>();
                args = "";
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
    public static int processInt(String value, HashMap<String, Integer> intVars, int line) throws ScriptException {
        try {
        for (String var : intVars.keySet()) {
            value = value.replace(var, String.valueOf(intVars.get(var)));
        }
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
        Object l = engine.eval(value);
        return (int) l;
        } catch (Exception e) {
            System.err.println("Diesel Interpreter Error!: Invalid Value for integer at line " + line);
            return 0;
        }
    }
    public static String processString(String value, HashMap<String, String> stringVars, int line) {
        try {
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
                        result += "\"" + stringVars.get(var) + "\"";
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
            return (String) c;
        } catch (Exception e) {
            System.err.println("Diesel Interpreter Error!: Expected \"=\" at line  " + line);
            return "";
        }
    } 
    public static Boolean processBool(String value, HashMap<String, Boolean> boolVars, int line, int mode) {
        try {
            if (mode == 1) {
                return Boolean.parseBoolean(value);
            } else {
                for (String var : boolVars.keySet()) {
                    value = value.replace(var, String.valueOf(boolVars.get(var)));
                }
                return Boolean.parseBoolean(value);
            }
        } catch (Exception e) {
            System.err.println("Diesel Interpreter Error!: Invalid Boolean value at line " + line);
            return false;
        }
    }
}