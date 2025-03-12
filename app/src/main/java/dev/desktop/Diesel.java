package dev.desktop;

import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

public class Diesel {
	// store variables
	protected static HashMap<String, Integer> intVars = new HashMap<String, Integer>();
	protected static HashMap<String, String> stringVars = new HashMap<String, String>();
	protected static HashMap<String, Boolean> boolVars = new HashMap<String, Boolean>();
	public static void preprocess(String filepath) {
		// preprocess, read file, process comments, etc
        File saveFile = new File(filepath);
        List<String> content = new ArrayList<String>();
        try (Scanner scanner = new Scanner(saveFile)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                // comments
                if (first(line.trim()).equals("//")) {
                    continue;
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
	public static void launch(List<String> content) {
        int i = 0;
        for (String line:content) {
            i++;
            interpret(line, i);
        }
	}
    public static void interpret(String line, int num) {
        line = line.trim();
        if (line.startsWith("int ")) {
            try {
                String m = line.replace("int ", "");
                if (line.contains("=")) {
                    if (line.endsWith(";")) {
                        String[] arr = m.split("=");
                        String n = arr[0].replace(" ", "");
                        int value = Integer.parseInt(arr[1].replace(";", "").replace(" ", "").replace("=", ""));
                        intVars.put(n, value);
                    } else {
                        semiColonError(num);
                    }
                } else {
                    if (line.endsWith(";")) {
                        m = m.replace(";", "");
                        intVars.put(m, 0);
                    } else {
                        semiColonError(num);
                    }
                }
            } catch (Exception e) {
                System.err.println("Diesel Interpreter Error!: An Unknown Error occured at line " + num);
            }
        } else if (line.startsWith("String")) {
            try {
                String n = line.replace("String", "");
                if (n.contains("=")) {
                    if (line.endsWith(";")) {
                        String[] arr = n.split("=");
                        String m = arr[0].replace(" ", "");
                        int value = arr[1].trim().replaceAll('"', "").replace(";", "");
                        stringVars.put(m, value);
                    } else {
                        semiColonError
                    }
                } else {
                    if (n.contains(";")) {
                        n = n.replace(";", "");
                        stringvars.put(n, "");
                    } else {
                        semiColonError(num);
                    }
                }
            } catch (exception e) {
                system.err.println("diesel interpreter error!: an unknown error occured at line " + num);
            }
        }
    }
	// helper functions
	public static string first(string str) {          
		if(str.length()<2){
		    return str;
		}
		else{
		    return str.substring(0,2);
		}
	}
    public static void semicolonerror(int line) {
        system.err.println("diesel interpreter error!: expected ';' at line " + line);
    }
}