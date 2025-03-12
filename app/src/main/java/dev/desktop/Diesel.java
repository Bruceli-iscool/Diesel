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
                        String n = arr[0].trim();
                        int value = Integer.parseInt(arr[1].replace(";", "").trim());
                        intVars.put(n, value);
                    } else {
                        semicolonError(num);
                    }
                } else {
                    if (line.endsWith(";")) {
                        m = m.replace(";", "");
                        intVars.put(m, 0);
                    } else {
                        semicolonError(num);
                    }
                }
            } catch (Exception e) {
                System.err.println("Diesel Interpreter Error!: An Unknown Error occurred at line " + num);
            }
        } else if (line.startsWith("String ")) {
            try {
                String n = line.replace("String ", "").trim();
                if (n.contains("=")) {
                    if (line.endsWith(";")) {
                        String[] arr = n.split("=");
                        String m = arr[0].trim();
                        String value = arr[1].replace(";", "").trim().replace("\"", "");
                        stringVars.put(m, value);
                    } else {
                        semicolonError(num);
                    }
                } else {
                    if (n.endsWith(";")) {
                        n = n.replace(";", "");
                        stringVars.put(n, "");
                    } else {
                        semicolonError(num);
                    }
                }
            } catch (Exception e) {
                System.err.println("Diesel Interpreter Error!: An Unknown Error occurred at line " + num);
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