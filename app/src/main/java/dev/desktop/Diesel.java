package dev.desktop;

import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

public class Diesel {
	// store variables
	private static HashMap<String, Integer> intVars = new HashMap<String, Integer>();
	private static HashMap<String, String> stringVars = new HashMap<String, String>();
	private static HashMap<String, Boolean> boolVars = new HashMap<String, Boolean>();
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
        for (String line:content) {
            interpret(line);
        }
	}
    public static void interpret(String line) {
        line = line.trim();
        if (line.startsWith("int ")) {
            String m = line.replace("int ", "");
            if (line.contains("=")) {

            } else {
                if (line.contains(";")) {
                    m.replace(";", "")
                    intVars.put(m, 0);
                } else {

                }
            }
        }
    }
	// helper function
	public static String first(String str) {          
		if(str.length()<2){
		    return str;
		}
		else{
		    return str.substring(0,2);
		}
	}
}