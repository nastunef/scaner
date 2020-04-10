package com.company;

import java.util.LinkedList;
import static com.company.Constans.*;
public class llk {
    //текущая позиция в файле
    private int tec_iterat;
    private String content;
    private MyScanner sc;
    private LinkedList<Integer> stack = new LinkedList<>();

    //стек указателей на вершины семантического дерева
    private static LinkedList<tree> MyTree = new LinkedList<>();
    //стек типов
    private LinkedList<Integer> typeStack = new LinkedList<>();
    //стек переменных
    private LinkedList<Node> varStack = new LinkedList<>();
    //корень семантического дерева
    private tree root;
    //последний отсканированный тип
    private int currentType;
    //сама константа
    String currentConst;
    //тип данных(функция, переменная, экземпляр класса, класс)
    //необходимо при поиске
    DATA_TYPE data_type;
    //для поиска в дереве
    tree search;
    //ID почледний считанный
    String nameID;
    //ID класса(при объявлении переменной класса)
    String nameClass;

    llk(String newContent){
        sc = new MyScanner();
        content = newContent;

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
        if(tec_iterat == ID){
            nameID = sc.nameId;
        }else if(tec_iterat == TYPE_IN || tec_iterat == TYPE_DOUBL){
            currentConst = sc.number;
        }else if(tec_iterat == MAIN){
            nameID ="Main";
        }
    }

    public int start(){
        if(tec_iterat == CLASS) {
            getNextLex();
            addInStack(T_CLASS);
            int result = ll1();
            if(result == ERROR){
                return ERROR;
            }
            else {
                root.Pr();
                return OK;
            }
        } else {
            printError("Ожидался класс main в строке ");
            return ERROR;
        }
    }
    public int  getNextFromStack(){
        int next = stack.getFirst();
        stack.removeFirst();
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
                    data_type = DATA_TYPE.TYPE_CLASS;
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
                        if(tec_iterat == ID){
                            data_type = DATA_TYPE.TYPE_OBJECT_CLASS;
                            nameClass = nameID;
                            currentType = tec_iterat;
                            getNextLex();
                            if(tec_iterat == ASSIGN){
                                break;
                            }
                        }else {
                            data_type = DATA_TYPE.TYPE_PEREMEN;
                            currentType = tec_iterat;
                            getNextLex();
                        }

                        if (tec_iterat == ID) {
                            getNextLex();
                            if (tec_iterat == ROUND_BRACE_OPEN && currentType !=ID) {
                                addInStack(T_OPISANIE);
                                addInStack(T_METHOD);
                                addInStack(DEL_SAVE_TYPE);
                            } else if (tec_iterat == COMMA || tec_iterat == ASSIGN || tec_iterat == VIRGULE) {
                                addInStack(T_OPISANIE);
                                addInStack(T_DATA);
                                addInStack(DEL_SET_VAR);
                                addInStack(DEL_SAVE_TYPE);
                                if(currentType == ID){
                                    addInStack(DEL_CHECK_TYPE_CLASS);
                                }
                            } else {
                                printError("Недопустимый символ" + tec_iterat + " в строке ");
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
                    data_type = DATA_TYPE.TYPE_METHOD;
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
                        getNextLex();
                        addInStack(T_DATA);
                        addInStack(DEL_PUSH);
                        addInStack(DEL_SET_VAR);
                        addInStack(DEL_FIND);
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
                    typeStack.add(ID);

                case T_OPERATOR_AND_OPERAND:
                    if(tec_iterat == WHILE) {
                        addInStack(T_OPERATOR_AND_OPERAND);
                        addInStack(T_WHILE);
                    }
                    else if(tec_iterat == RETURN){
                        addInStack(T_OPERATOR_AND_OPERAND);
                        addInStack(T_RETURN);
                    }
                    else if(tec_iterat == ID || tec_iterat == ASSIGN){
                        addInStack(T_OPERATOR_AND_OPERAND);
                        addInStack(T_ASSIGMENT);
                    }else if(tec_iterat == INT ||tec_iterat == DOUBLE){
                        addInStack(T_OPERATOR_AND_OPERAND);
                        addInStack(T_OPISANIE);
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
                        data_type = DATA_TYPE.TYPE_PEREMEN;
                        addInStack(DEL_FIND_IN_TREE_VAR);
                    }
                    break;
                case T_NAME:
                    if(tec_iterat == ID){
                        addInStack(T_NAME2);
                        addInStack(ID);
                    }else {
                        printError("Ошибка в строке " + tec_iterat);
                        return ERROR;
                    }
                    break;

                case T_NAME2:
                    if(tec_iterat == DOT){
                        addInStack(T_NAME);
                        addInStack(DOT);
                        addInStack(DEL_FIND_IN_TREE_VAR);
                        data_type = DATA_TYPE.TYPE_OBJECT_CLASS;
                    }
                    else {
                        addInStack(DEL_PUSH);
                        addInStack(T_W);
                    }
                    break;

                case T_ASSIGMENT:
                        if(tec_iterat == ID){
                            getNextLex();
                        }else if(tec_iterat!=ASSIGN){
                            printError("Должно быть присваивание. Ошибка в строке ");
                            return ERROR;
                        }
                        if (tec_iterat == ASSIGN) {
                            addInStack(T_ASSIGN);
                            addInStack(tec_iterat);

                        } else if(tec_iterat == DOT) {
                            addInStack(T_NAME2);
                        }else{
                            printError("Недопустимый символ " + tec_iterat + " в строке ");
                            return ERROR;
                        }
                    break;

                case DEL_CHECK_RETURN:
                    //проверяем ретерн
                    if(typeStack.size() < 2){
                        printError("При сравнивании типов во время return произошла ошибка");
                        return ERROR;
                    }
                    int oneType = typeStack.getLast();
                    typeStack.removeLast();
                    int twoType = typeStack.getLast();
                    typeStack.removeLast();
                    if(oneType!= twoType){
                        printError("Возвращаемое значение не соответсвует типу функции");
                    }
                    break;
                case DEL_CHECK_TYPE_CLASS:
                    //при объявлении переменной класса проверяем есть ли такой класс
                    if(search == null){
                        search = root;
                    }
                    if(nameClass == null){
                        nameClass = nameID;
                    }
                    search = search.FindClass(root,nameClass);
                    if(search == null){
                        printError("Класса " + nameClass + " не существует");
                        return ERROR;
                    }
                    break;
                case DEL_END_LEVEL:
                    //выход с уровня
                    printError("Вышли из }");
                    root = MyTree.getLast();
                    root.Left = new tree();
                    MyTree.removeLast();
                    break;
                case DEL_NEW_LEVEL:
                    printError("Добавили ответвление в дереве({)");
                    //переход на новый уровень
                    MyTree.add(root);
                    Node n = new Node();
                    n.NameLex = "";
                    n.TypeOfLex = DATA_TYPE.TYPE_UNIKNOW;
                    root.setRight(n);
                    root = root.Right;
                    root.Up = MyTree.getLast();
                    break;
                case DEL_FIND:
                    printError("Проверка "+nameID + " на уникальность в строке ");
                    if(search == null){
                        search = root;
                    }
                    search = search.FindInClass(search,nameID,data_type);
                    if(search!=null){
                        printError(nameID + " уже объявлено. Ошибка в строке ");
                        return ERROR;
                    }
                    break;
                case DEL_FIND_IN_TREE_FUNC:
                    printError("Поиск вызываемой функции в строке ");
                    search = search.FindInClass(search,nameID,DATA_TYPE.TYPE_METHOD);
                    if(search==null){
                        printError("Функция не существует. Ошибка в стркое ");
                        return ERROR;
                    }
                    typeStack.add(search.n.LexType);
                    break;
                case DEL_FIND_IN_TREE_VAR:
                    printError("Поиск переменной " + nameID +" из строки ");
                    search = root;
                    search = search.FindUpOneLevel(search,nameID,data_type);
                    if(search==null){
                        printError("Переменная не существует. Ошибка в строке ");
                        return ERROR;
                    }
                    if(data_type == DATA_TYPE.TYPE_OBJECT_CLASS){
                        printError(" Переменная является экземпляром класса, переходим на ее ветку, для дальнейшего поиска. Строка ");
                        search = search.FindClass(search,nameClass);
                        search = search.Right;
                        while (search.Left!=null){
                            search = search.Left;
                        }
                    }
                    break;
                case DEL_ITS_VAR:
                    printError("Проверка. Является ли слева от равно переменная. Строка ");
                    Node var = varStack.getLast();
                    if(var.TypeOfLex != DATA_TYPE.TYPE_PEREMEN){
                        printError("Невозможно присвоить значение не переменной");
                        return ERROR;
                    }
                    break;

                case DEL_MATCH:
                    if(typeStack.size() < 2){
                        printError("При сравнивании типов произошла ошибка");
                        return ERROR;
                    }
                    oneType = typeStack.getLast();
                    typeStack.removeLast();
                    twoType = typeStack.getLast();
                    typeStack.removeLast();
                    if(oneType == DOUBLE){
                        if(twoType == INT || twoType == DOUBLE){
                            typeStack.add(DOUBLE);
                        }else {
                            printError("Ошибка в присваивании типов, что то не так с типами");
                            return ERROR;
                        }
                    }else if(twoType == DOUBLE){
                        if(oneType == INT || oneType == DOUBLE){
                            typeStack.add(DOUBLE);
                        }else {
                            printError("Ошибка в присваивании типов, что то не так с типами");
                            return ERROR;
                        }
                    }else {
                        typeStack.add(INT);
                    }
                    break;
                case DEL_MATCH_LEFT:
                    if(typeStack.size() < 2){
                        printError("При сравнивании типов произошла ошибка");
                        return ERROR;
                    }
                    oneType = typeStack.getLast();
                    typeStack.removeLast();
                    twoType = typeStack.getLast();
                    typeStack.removeLast();
                    if(twoType == oneType && oneType == ID){
                        typeStack.add(twoType);
                    }
                    else if(oneType == ID || twoType == ID){
                        printError("Несоответсвие типов в строке ");
                        return ERROR;
                    }else {
                        if(oneType == DOUBLE || twoType == DOUBLE){
                            typeStack.add(DOUBLE);
                        }else {
                            typeStack.add(INT);
                        }
                    }
                    break;
                case DEL_PUSH_DOUBLE_CONST:
                    printError("Добавляем константу " + currentConst + "в стек. Строка " );
                    Node nodeConstDouble = new Node();
                    ValueType nal = new ValueType();
                    nal.setValue(Double.parseDouble(currentConst));
                    nodeConstDouble.setValueT(nal);
                    varStack.add(nodeConstDouble);
                    typeStack.add(DOUBLE);
                    break;
                case DEL_PUSH_INT_CONST:
                    printError("Добавляем константу " + currentConst + "в стек. Строка " );
                    Node nodeConstInt = new Node();
                    nal = new ValueType();
                    nal.setValue(Integer.parseInt(currentConst));
                    nodeConstInt.setValueT(nal);
                    varStack.add(nodeConstInt);
                    typeStack.add(INT);
                    break;
                case DEL_PUSH:

                    if(search != null){
                        varStack.add(search.n);
                        search = null;
                    }else {
                        varStack.add(root.n);
                    }
                    break;
                case DEL_SAVE_TYPE:
                    typeStack.add(currentType);
                    printError("Сохранен тип в строке ");
                    break;
                case DEL_SET_CLASS:
                    Node newClass = new Node(CLASS, DATA_TYPE.TYPE_CLASS, nameID);
                    root = root.addNode(root, newClass);
                    printError("Добавлен класс в строке ");
                    break;
                case DEL_SET_FUNC:
                    Node newFunc = new Node(currentType,DATA_TYPE.TYPE_METHOD,nameID);
                    root = root.addNode(root, newFunc);
                    printError("Добавлена функция в строке ");
                    break;
                case DEL_SET_VAR:
                    Node newVar = new Node(currentType,data_type,nameID);
                    root = root.addNode(root, newVar);
                    printError("Добавлена переменная " + nameID +" в строке ");
                    break;
                default:
                    if(tec_iterat == choice){
                        getNextLex();
                    }
                    else {
                        printError("Ошибка в строке " + tec_iterat);
                        return ERROR;
                    }
            }
        }
        return OK;
    }
}
