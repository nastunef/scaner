package com.company;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {

    // читаем файл в строку с помощью класса Files
    private static String readUsingFiles(String fileName) throws IOException {
        return new String(Files.readAllBytes(Paths.get(fileName)));
    }
    public static void main(String[] args) throws IOException {
	// write your code here
        MyScanner sc = new MyScanner();
        String fileName = "data.txt";

        // читаем файл в строку с помощью класса Files
        String contents = readUsingFiles(fileName);

        String lex = "12.12e+1  2 ldkf1.; /double class int Publish()";

        while(sc.tek_i != contents.length()) {
            int a = sc.scanner(contents, sc.tek_i);
            if(a == 100){
                System.out.println("Неизвестный символ!");
            }
            System.out.println(a);
        }

    }
}
