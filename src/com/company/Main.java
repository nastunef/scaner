package com.company;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import static com.company.Constans.*;

public class Main {

    public static void main(String[] args) throws IOException {
        //java -cp C:\Users\nastu\OneDrive\Документы\NetBeansProjects\scaner\src com.company.Main
        //java -cp /home/anastasiya/Документы/проекты/scaner/src com.company.Main main.txt
        //java -cp /home/anastasiya/Документы/проекты/scaner/out/production/scaner com.company.Main main.txt
        // java -cp /home/anastasiya/Документы/проекты/scaner/src Constans.java MyScanner.java SyntaxAnalize.java Main.java
        //String fileName = args[0].toString();
        String fileName = "main.txt";
        String contents = readUsingFiles(fileName);

        ArrayList<Integer> codOfLex = new ArrayList();
        boolean SyntaxAnalize = true;
        //MyScanner sc = new MyScanner();
        SyntaxAnalize sa = new SyntaxAnalize(contents);
        ValueType valueType = new ValueType();
        valueType.setValue(1);
        //llk start = new llk(contents);
        //start.start();
        sa.ItsProgram();
        /*
        while(sc.tek_i != contents.length()) {
            int a = sc.scanner(contents, sc.tek_i);
            codOfLex.add(a);
            System.out.print(a + " ");
        }

         */



    }
    private static String readUsingFiles(String fileName) throws IOException {
        return new String(Files.readAllBytes(Paths.get(fileName)));
    }
}

//String fileName = "data.txt";
// читаем файл в строку с помощью класса Files