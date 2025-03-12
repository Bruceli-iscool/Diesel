package dev.desktop;

import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class Diesel {
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