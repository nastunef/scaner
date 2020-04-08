package com.company;

import java.util.LinkedList;
import java.util.Vector;
import static com.company.Constans.*;
public class llk {
    private int MAX = 1000;
    private int tec_iterat;
    private String content;
    private MyScanner sc;
    private Vector<Integer> stack;
    //стек указателей на вершины семантического дерева
    private static LinkedList<tree> MyTree = new LinkedList<>();
    //указатель стека вершин
    private int ukTree;
    //стек типов
    private Vector<Integer> typeStack;
    //флаг описания данных под вопросом
    private boolean flagData;
    //корень семантического дерева
    private tree root;
    //последний отсканированный тип
    private int currentType;
    //идентификатор
    int currentId;
    //тип константы
    int currentTypeConst;
    //сама константа
    int currentConst;
    boolean wasConst;
    int typeConst;

    llk(String newContent){
        sc = new MyScanner();
        content = newContent;
        stack = new Vector<>();
        getNextLex();
        Node n = new Node();
        root = new tree(null,null,null,n);
        tree.setCur(root);
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
            int choice = getNextFromStack();
            switch (choice) {
                case T_CLASS:
                    if (tec_iterat == ID || tec_iterat == MAIN) {
                        addInStack(DEL_END_LEVEL);
                        addInStack(CURLY_BRACE_CLOSE);
                        addInStack(T_OPISANIE);
                        addInStack(DEL_NEW_LEVEL);
                        addInStack(CURLY_BRACE_OPEN);
                        addInStack(DEL_SET_CLASS);
                        addInStack(DEL_FIND);
                        addInStack(tec_iterat);
                    } else {
                        printError("Ожидался идентификатор класса в строке ");
                        return ERROR;
                    }
                    break;

                case T_OPISANIE:
                    if (tec_iterat == DOUBLE || tec_iterat == INT || tec_iterat == ID) {
                        //сохраняем тип переменной
                        currentType = tec_iterat;
                        getNextLex();
                        if (tec_iterat == ID) {
                            //сохранить айди
                            getNextLex();
                            if (tec_iterat == ROUND_BRACE_OPEN && currentType !=ID) {
                                addInStack(T_OPISANIE);
                                addInStack(T_METHOD);
                                addInStack(DEL_SAVE_TYPE);
                            } else if (tec_iterat == COMMA || tec_iterat == ASSIGN || tec_iterat == VIRGULE) {
                                addInStack(T_OPISANIE);
                                addInStack(T_DATA);
                                addInStack(DEL_SAVE_TYPE);
                                if(currentType == ID){
                                    addInStack(DEL_CHECK_TYPE_CLASS);
                                }
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
                    addInStack(DEL_END_LEVEL);
                    addInStack(CURLY_BRACE_CLOSE);
                    addInStack(T_OPERATOR_AND_OPERAND);
                    addInStack(DEL_NEW_LEVEL);
                    addInStack(CURLY_BRACE_OPEN);
                    addInStack(ROUND_BRACE_CLOSE);
                    addInStack(ROUND_BRACE_OPEN);
                    addInStack(DEL_PUSH);
                    addInStack(DEL_SET_FUNC);
                    addInStack(DEL_FIND);
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
                        addInStack(DEL_PUSH);
                        addInStack(DEL_SET_VAR);
                        addInStack(DEL_FIND);
                    }
                    break;
                case T_ASSIGN:
                    if(tec_iterat == NEW){
                        addInStack(COMMA);
                        addInStack(DEL_MATCH_LEFT);
                        addInStack(T_NEW_ID);
                    } else {
                        addInStack(COMMA);
                        addInStack(DEL_MATCH_LEFT);
                        addInStack(T_V);
                    }
                    break;
                case T_SPISOK:
                    if(tec_iterat == ID){
                        addInStack(T_DATA);
                        addInStack(DEL_PUSH);
                        addInStack(DEL_SET_VAR);
                        addInStack(DEL_FIND);
                        addInStack(tec_iterat);
                    }
                    break;

                case T_RETURN:
                    addInStack(COMMA);
                    addInStack(DEL_CHECK_RETURN);
                    addInStack(T_V);
                    addInStack(RETURN);
                    break;

                case T_WHILE:
                    addInStack(DEL_END_LEVEL);
                    addInStack(CURLY_BRACE_CLOSE);
                    addInStack(T_OPERATOR_AND_OPERAND);
                    addInStack(DEL_NEW_LEVEL);
                    addInStack(CURLY_BRACE_OPEN);
                    addInStack(ROUND_BRACE_CLOSE);
                    addInStack(T_V);
                    addInStack(ROUND_BRACE_OPEN);
                    addInStack(WHILE);
                    break;

                case T_NEW_ID:
                    addInStack(ROUND_BRACE_CLOSE);
                    addInStack(ROUND_BRACE_OPEN);
                    addInStack(DEL_CHECK_TYPE_CLASS);
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
                    else if(tec_iterat == ID){
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
                        addInStack(DEL_MATCH);
                        addInStack(T_A);
                        addInStack(EQUAL);

                    }else if(tec_iterat == NOT_EQUAL){
                        addInStack(T_V1);
                        addInStack(DEL_MATCH);
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
                    addInStack(DEL_MATCH);
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
                        addInStack(DEL_MATCH);
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
                        addInStack(DEL_MATCH);
                        addInStack(T_F);
                        addInStack(tec_iterat);
                    }
                    break;
                case T_F:
                    if(tec_iterat == ROUND_BRACE_OPEN){
                        addInStack(ROUND_BRACE_CLOSE);
                        addInStack(T_V);
                        addInStack(ROUND_BRACE_OPEN);
                    } else if(tec_iterat == TYPE_IN){
                        addInStack(tec_iterat);
                        addInStack(DEL_PUSH_INT_CONST);
                    } else if( tec_iterat == TYPE_DOUBL){
                        addInStack(tec_iterat);
                        addInStack(DEL_PUSH_DOUBLE_CONST);
                    }else if(tec_iterat == ID){
                        addInStack(T_NAME);
                    }
                    break;
                case T_W:
                    if(tec_iterat == ROUND_BRACE_OPEN){
                        addInStack(DEL_FIND_IN_TREE_FUNC);
                        addInStack(ROUND_BRACE_CLOSE);
                        addInStack(ROUND_BRACE_OPEN);
                    }else {
                        addInStack(DEL_FIND_IN_TREE_VAR);
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
                    else {
                        addInStack(DEL_PUSH);
                        addInStack(T_W);
                    }
                    break;

                case T_ASSIGMENT:
                        if(tec_iterat == ID){
                            addInStack(DEL_ITS_VAR);
                            addInStack(T_NAME);
                        }else{
                            printError("Должно быть присваивание. Ошибка в строке ");
                            return ERROR;
                        }
                        if (tec_iterat == ASSIGN) {
                            addInStack(T_ASSIGN);
                            addInStack(tec_iterat);
                        } else {
                            printError("Недопустимый символ в строке ");
                            return ERROR;
                        }

                    break;
                default:
                    if(tec_iterat == choice){
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
