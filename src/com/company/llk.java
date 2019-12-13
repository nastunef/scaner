package com.company;

import java.util.Vector;
import static com.company.Constans.*;
public class llk {

    public int tec_iterat;
    public String content;
    public MyScanner sc;
    public Vector<Integer> stack;

    llk(String newContent){
        sc = new MyScanner();
        content = newContent;
        stack = new Vector<Integer>();
        getNextLex();


    }
    public void printError(String error) {
        System.out.println(error + sc.str);
    }

    public void getNextLex() {
        tec_iterat = sc.scanner(content, sc.tek_i);
    }
    public int start(){
        if(tec_iterat == CLASS) {
            getNextLex();
            addInStack(T_CLASS);
            return ll1();
        } else {
            printError("Ожидался класс main в строке ");
            return ERROR;
        }
    }
    public int  getNextFromStack(){
        int next = stack.firstElement();
        stack.remove(0);
        return next;
    }
    public void addInStack(int element){
        stack.add(0,element);
    }

    int ll1(){
        while(tec_iterat!=END && tec_iterat != ERROR) {
            int choise = getNextFromStack();
            switch (choise) {
                case T_CLASS:
                    if (tec_iterat == ID || tec_iterat == MAIN) {
                        addInStack(CURLY_BRACE_CLOSE);
                        addInStack(T_OPISANIE);
                        addInStack(CURLY_BRACE_OPEN);
                        addInStack(tec_iterat);
                    } else {
                        printError("Ожидался идентификатор класса в строке ");
                        return ERROR;
                    }
                    break;

                case T_OPISANIE:
                    if (tec_iterat == DOUBLE || tec_iterat == INT || tec_iterat == ID) {
                        int tec_type = tec_iterat;
                        getNextLex();
                        if (tec_iterat == ID) {
                            getNextLex();
                            if (tec_iterat == ROUND_BRACE_OPEN && tec_type !=ID) {
                                addInStack(T_OPISANIE);
                                addInStack(T_METHOD);
                            } else if (tec_iterat == COMMA || tec_iterat == ASSIGN || tec_iterat == VIRGULE) {
                                addInStack(T_OPISANIE);
                                addInStack(T_DATA);
                            } else {
                                printError("Недопустимый символ в строке ");
                                return ERROR;
                            }
                        } else {
                            printError("Ожидался идентификатор в строке ");
                            return ERROR;
                        }
                    } else if (tec_iterat == CLASS) {
                        getNextLex();
                        addInStack(T_OPISANIE);
                        addInStack(T_CLASS);
                    }
                    break;

                case T_METHOD:
                    addInStack(CURLY_BRACE_CLOSE);
                    addInStack(T_OPERATOR_AND_OPERAND);
                    addInStack(CURLY_BRACE_OPEN);
                    addInStack(ROUND_BRACE_CLOSE);
                    addInStack(ROUND_BRACE_OPEN);
                    break;

                case T_DATA:
                    if(tec_iterat == COMMA){
                        addInStack(tec_iterat);
                    }else if(tec_iterat == VIRGULE){
                        addInStack(T_SPISOK);
                        addInStack(tec_iterat);
                    }else if(tec_iterat == ASSIGN){
                        addInStack(T_ASSIGN);
                        addInStack(tec_iterat);
                    }
                    break;
                case T_ASSIGN:
                    if(tec_iterat == NEW){
                        addInStack(COMMA);
                        addInStack(T_NEW_ID);
                    } else {
                        addInStack(COMMA);
                        addInStack(T_V);
                    }
                    break;
                case T_SPISOK:
                    if(tec_iterat == ID){
                        addInStack(T_DATA);
                        addInStack(tec_iterat);
                    }
                    break;

                case T_RETURN:
                    addInStack(COMMA);
                    addInStack(T_V);
                    addInStack(RETURN);
                    break;

                case T_WHILE:
                    addInStack(CURLY_BRACE_CLOSE);
                    addInStack(T_OPERATOR_AND_OPERAND);
                    addInStack(CURLY_BRACE_OPEN);
                    addInStack(ROUND_BRACE_CLOSE);
                    addInStack(T_V);
                    addInStack(ROUND_BRACE_OPEN);
                    addInStack(WHILE);
                    break;

                case T_NEW_ID:
                    addInStack(ROUND_BRACE_CLOSE);
                    addInStack(ROUND_BRACE_OPEN);
                    addInStack(ID);
                    addInStack(NEW);

                case T_OPERATOR_AND_OPERAND:
                    if(tec_iterat == WHILE) {
                        addInStack(T_OPERATOR_AND_OPERAND);
                        addInStack(T_WHILE);
                    }
                    else if(tec_iterat == RETURN){
                        addInStack(T_OPERATOR_AND_OPERAND);
                        addInStack(T_RETURN);
                    }
                    else if(tec_iterat == DOUBLE || tec_iterat == INT|| tec_iterat == ID){
                        addInStack(T_OPERATOR_AND_OPERAND);
                        addInStack(T_ASSIGMENT);
                    }
                    break;

                case T_V:
                    if(tec_iterat == MINUS || tec_iterat == PLUS){
                        getNextLex();
                    }
                    addInStack(T_V1);
                    addInStack(T_A);
                    break;

                case  T_V1:
                    if(tec_iterat == EQUAL){
                        addInStack(T_V1);
                        addInStack(T_A);
                        addInStack(EQUAL);

                    }else if(tec_iterat == NOT_EQUAL){
                        addInStack(T_V1);
                        addInStack(T_A);
                        addInStack(NOT_EQUAL);
                    }else {
                        addInStack(T_V2);
                    }
                    break;

                case T_V2:
                    if(tec_iterat != MORE && tec_iterat != MORE_EQUAL && tec_iterat != LESS && tec_iterat != LESS_EQUAL){
                        break;
                    }
                    addInStack(T_V2);
                    addInStack(T_A);
                    addInStack(tec_iterat);
                    break;

                case T_A:
                    addInStack(T_A1);
                    addInStack(T_C);
                    break;

                case T_A1:
                    if(tec_iterat == PLUS || tec_iterat == MINUS){
                        addInStack(T_A1);
                        addInStack(T_C);
                        addInStack(tec_iterat);
                    }
                    break;

                case T_C:
                    addInStack(T_C1);
                    addInStack(T_F);
                    break;

                case T_C1:
                    if(tec_iterat == STAR || tec_iterat == SLASH){
                        addInStack(T_C1);
                        addInStack(T_F);
                        addInStack(tec_iterat);
                    }
                    break;
                case T_F:
                    if(tec_iterat == ROUND_BRACE_OPEN){
                        addInStack(ROUND_BRACE_CLOSE);
                        addInStack(T_V);
                        addInStack(ROUND_BRACE_OPEN);
                    } else if(tec_iterat == TYPE_IN || tec_iterat == TYPE_DOUBL){
                        addInStack(tec_iterat);
                    }else if(tec_iterat == ID){
                        addInStack(T_W);
                        addInStack(T_NAME);
                    }
                    break;
                case T_W:
                    if(tec_iterat == ROUND_BRACE_OPEN){
                        addInStack(ROUND_BRACE_CLOSE);
                        addInStack(ROUND_BRACE_OPEN);
                    }
                    break;
                case T_NAME:
                    if(tec_iterat == ID){
                        addInStack(T_NAME2);
                        addInStack(ID);
                    }else {
                        printError("Ошибка в строке ");
                        return ERROR;
                    }
                    break;

                case T_NAME2:
                    if(tec_iterat == DOT){
                        addInStack(T_NAME);
                        addInStack(DOT);
                    }
                    break;

                case T_ASSIGMENT:
                    if(tec_iterat == DOUBLE || tec_iterat == INT|| tec_iterat == ID){
                        int type = tec_iterat;
                        getNextLex();
                        if(tec_iterat == ID){
                              getNextLex();
                        }else if (type != ID){
                            printError("Ошибка в строке ");
                            return ERROR;
                        }
                        if (tec_iterat == COMMA || tec_iterat == ASSIGN || tec_iterat == VIRGULE) {
                            addInStack(T_DATA);
                        } else {
                            printError("Недопустимый символ в строке ");
                            return ERROR;
                        }
                    }
                    break;
                default:
                    if(tec_iterat == choise){
                        getNextLex();
                    }
                    else {
                        printError("Ошибка в строке ");
                        return ERROR;
                    }
            }

        }
        return OK;
    }

}
