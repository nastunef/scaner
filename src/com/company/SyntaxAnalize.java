package com.company;


import java.util.ArrayList;

import static com.company.Constans.*;


public class SyntaxAnalize {

    public int tec_iterat;
    private String content;
    private MyScanner sc;

    //тип лексемы
    public int Type_lex;
    public int Type_method;
    public String nameClass;
    //дерево идентификаторов
    public tree TREE = new tree();
    private tree methodTree;
    //очередь для классов
    int iForClass;
    public String IdClass[] = new String[5];
    //дерево для поиска
    private tree treeForSearch;
    private boolean roundBrace;
    private int flInt;
    private boolean returnOK;


    private void initExp() {
        treeForSearch = null;

    }

    SyntaxAnalize(String newContent) {
        tec_iterat = 0;
        content = newContent;
        sc = new MyScanner();
        iForClass = 0;
        flInt = 1;

    }

    public void printError(String error) {
        System.out.println(error + sc.str);
    }

    public void getNextLex() {
        sc.history = tec_iterat;
        tec_iterat = sc.scanner(content, sc.tek_i);

    }

    public int ItsProgram() {
        getNextLex();
        if (tec_iterat == CLASS) {
            getNextLex();
            if (tec_iterat == MAIN) {
                //добавили маин в дерево
                IdClass[iForClass] = "Main";
                if (flInt == 1) {
                    TREE = TREE.addClassMain();
                    if (TREE == null) {
                        return ERROR;
                    }
                }
                getNextLex();
                while (true) {
                    int a = ItsDescriptions();
                    if (a == END) {
                        System.out.println("Конец программы");
                        return END;
                    }
                    if (a == ERROR) {
                        printError("Ошибка компиляции в строке");
                        return ERROR;
                    }
                }
            } else {
                printError("Ожидалось MAIN в строке");
                return ERROR;
            }
        } else {
            printError("Ожидалось обьявление class main в строке");
            return ERROR;
        }
    }

    public int ItsClass() {
        if (tec_iterat == CLASS) {
            getNextLex();
            if (tec_iterat == ID) {

                //проверка на существование класса, если нет то внести в дерево
                if (flInt == 1) {
                    TREE = TREE.SemInclude(CLASS, com.company.DATA_TYPE.TYPE_CLASS, IdClass[iForClass], sc.nameId);
                    iForClass++;


                    if (TREE == null) {
                        return ERROR;
                    }
                    IdClass[iForClass] = sc.nameId;
                }
                getNextLex();
                int a = ItsDescriptions();
                if (a == END) {
                    return OK;
                } else if (a == ERROR)
                    return ERROR;
            } else {
                printError("Ожидался идентификатор (название класса) в строке");
                return ERROR;
            }
            iForClass--;
            return OK;
        }
        return NEXT;
    }

    public int ItsDescriptions() {
        //используется только в классах
        if (tec_iterat == CURLY_BRACE_OPEN) {
            getNextLex();
            int a;
            while (true) {
                a = ItsOneDescription();
                if (a == NEXT)
                    break;
                else if (a == OK) {
                } else return ERROR;
            }
            if (tec_iterat == CURLY_BRACE_CLOSE) {
                //if(flInt == 1) {

                TREE = TREE.SetCur();
                iForClass--;
                //}
                getNextLex();
                return END;//END
            } else {
                printError("Ожидалось } в строке ");
                return ERROR;
            }
        }
        return END;
    }

    public int ItsTypeData() {
        if (tec_iterat == INT || tec_iterat == DOUBLE || tec_iterat == ID) {
            Type_lex = tec_iterat;
            if (tec_iterat == ID) {
                nameClass = sc.nameId;
                Type_lex = OBJECT_CLASS;
                //int save_iter = sc.getTek_i()-1;
                getNextLex();
                if (tec_iterat == ID) {
                    //проверка классса есть ли такой класс
                    if (flInt == 1) {
                        if (TREE.SemGetClass(nameClass) == null) {
                            printError("Класс " + nameClass + " не объявлен. Строка ");
                            return ERROR;
                        }
                    }

                } else {
                    //если это просто переменная  то проверяем объявлена она
                    tree searchNode = treeForSearch.FindInClass(treeForSearch, sc.nameId, com.company.DATA_TYPE.TYPE_PEREMEN);
                    if (searchNode == null) {
                        return ERROR;
                    } else {
                        //или нет
                        //если она объявлена и это переменная класса, то нужно сохранить nameclass
                        nameClass = searchNode.n.IdClassLex;
                        return NEXT;
                    }

                    //tec_iterat = sc.history;

                }
            } else if (tec_iterat == INT) {
                nameClass = "int";
                getNextLex();
            } else {
                getNextLex();
                nameClass = "double";
            }

            return OK;
        } else
            return NEXT;
    }

    private int ItsOneDescription() {
        //объявление типа
        if (ItsTypeData() == OK) {
            int ItsID = ItsID();
            //если объявление переменной
            if (ItsID == OK) {
                if (tec_iterat == COMMA) {
                    getNextLex();
                    return OK;
                } else {
                    printError("ожидалось ; в строке ");
                    return ERROR;
                }
            } else if (ItsID == ERROR) {
                return ERROR;
            } else {
                //если объявление метода
                flInt = 0;
                int a = ItsMethod();
                if (a == OK) {

                    //проверка существует ли такой метод
                    return OK;
                } else if (a == NEXT)
                    return NEXT;
                else
                    return ERROR;
            }
        } else {
            int a = ItsClass();
            if (a == OK) {
                return OK;
            } else if (a == ERROR)
                return ERROR;
            else
                return NEXT;
        }
    }

    public int ItsMethod() {
        if (tec_iterat == ROUND_BRACE_OPEN) {
            getNextLex();

            if (tec_iterat == ROUND_BRACE_CLOSE) {
                if (flInt == 0) {
                    //проверка на существование функции, если существует, то ошибка если нет то внести!
                    TREE = TREE.SemInclude(Type_lex, com.company.DATA_TYPE.TYPE_METHOD, IdClass[iForClass], sc.nameId);
                    if (TREE == null) {
                        return ERROR;
                    }
                    //запомнить указатель на метод
                    TREE.n.uk_iterat = sc.getTek_i() - 1;
                    if (sc.nameId.equals("Main")) {
                        flInt = 1;
                    }

                }

                getNextLex();
                if (tec_iterat == CURLY_BRACE_OPEN) {
                    Type_method = Type_lex;
                    getNextLex();
                    if (ItsOperatorAndOperands() == OK) {

                        if (tec_iterat == CURLY_BRACE_CLOSE) {
                            if (flInt == 0) {
                                TREE = TREE.SetCur();
                            }
                            //TREE = TREE.Up;
                            Type_method = 0;

                            getNextLex();
                            return OK;
                        } else {
                            printError("Ошибка в строке  ");
                            return ERROR;
                        }
                    } else {
                        return ERROR;
                    }
                } else {
                    printError("Ожидался блок операторов и операндов в строке");
                    return ERROR;
                }
            } else {
                printError("Ожидалось () в строке");
                return ERROR;
            }
        } else
            return NEXT;
    }

    public int ItsID() {
        if (tec_iterat == ID) {
            getNextLex();
        }
        if (tec_iterat == VIRGULE || tec_iterat == COMMA || tec_iterat == ASSIGN) {
            if (flInt == 1) {
                if (Type_lex == OBJECT_CLASS) {
                    TREE = TREE.SemInclude(Type_lex, com.company.DATA_TYPE.TYPE_PEREMEN, nameClass, sc.nameId);

                } else {
                    TREE = TREE.SemInclude(Type_lex, com.company.DATA_TYPE.TYPE_PEREMEN, IdClass[iForClass], sc.nameId);
                }
                if (TREE == null) {
                    return ERROR;
                }
            }

            if (tec_iterat == COMMA) {
                return OK;
            } else if (tec_iterat == VIRGULE) {
                getNextLex();
                return ItsID();
            } else {
                getNextLex();
                int newClass = ItsExempleClass();
                if (newClass == OK) {
                } else if (newClass == ERROR) {
                    return ERROR;
                } else {
                    Node val = new Node();
                    val.valueT.setValue(0);
                    int ItsV1 = expression1(val);
                    if (ItsV1 == OK) {
                        if (flInt == 1) {
                            addValue(TREE, val);
                        }

                    } else if (ItsV1 == ERROR) {
                        return ERROR;
                    }
                }
                if (tec_iterat == COMMA) {
                    return OK;
                } else {
                    printError("Ожидалось ; в строке ");
                    return ERROR;
                }
            }

        } else if (tec_iterat == ROUND_BRACE_OPEN) {
            return NEXT;
        } else
            return ERROR;
    }

    public int ItsNameN(Node val) {
        //ПРОВЕРКА НА СУЩЕСТВОВАНИЕ ПЕРЕМЕННОЙ
        //перед входом в эту функцию обнулить глобаньную
        //найти в дереве(это может быть функцией)
        if (treeForSearch == null) {
            treeForSearch = TREE;
        }

        if (tec_iterat == ID) {
            if (flInt == 0) {
                getNextLex();
                return OK;
            }
            tree searchNode = null;
            if (flInt == 1) {
                searchNode = treeForSearch.FindInClass(treeForSearch, sc.nameId, com.company.DATA_TYPE.TYPE_PEREMEN);
                if (searchNode == null) {
                    searchNode = treeForSearch.FindInClass(treeForSearch, sc.nameId, com.company.DATA_TYPE.TYPE_OBJECT_CLASS);

                }
                if (searchNode != null && searchNode.n.valueT.getValue() != null) {
                    val.valueT.setValue(searchNode.n.valueT.getValue());
                    val.LexType = searchNode.n.LexType;
                } else {
                    val.valueT.setValue(null);
                    val.LexType = TYPE_UNIKNOW;
                }
            }
            getNextLex();

            if ((searchNode != null && searchNode.n.TypeOfLex == com.company.DATA_TYPE.TYPE_OBJECT_CLASS && flInt == 1) || flInt == 0) {
                if (tec_iterat == DOT) {
                    getNextLex();
                    if (tec_iterat != ID) {
                        printError("После точки ожидалось продолжение. Строка ");
                        return ERROR;
                    } else {
                        if (flInt == 1) {
                            treeForSearch = TREE.GetClass(TREE, searchNode.n.IdClassLex);
                            while (treeForSearch.Left != null) {
                                treeForSearch = treeForSearch.Left;
                            }
                        }
                        return ItsNameN(val);
                    }
                } else if (flInt == 1) {
                    printError("Переменную класса нельзя присвоить. Строка ");
                    return ERROR;
                }
            }
            if (tec_iterat != ROUND_BRACE_OPEN) {
                //searchNode = treeForSearch.FindUpOneLevel(treeForSearch, sc.nameId, com.company.DATA_TYPE.TYPE_PEREMEN);
                if ((searchNode != null && flInt == 1) || flInt == 0) {
                    if (tec_iterat == DOT) {
                        printError(sc.nameId + " это переменная, послне нее не должно быть точки. Строка ");
                        return ERROR;
                    } else if (tec_iterat != CURLY_BRACE_OPEN) {
                        if (flInt == 1) {
                            if (Type_lex == WHILE) {
                                Type_lex = searchNode.n.LexType;
                            }
                        }
                        if (searchNode != null && (searchNode.n.LexType == Type_lex ||
                                (searchNode.n.LexType == INT || searchNode.n.LexType == DOUBLE))) {
                            if (tec_iterat == ROUND_BRACE_CLOSE && roundBrace) {
                                getNextLex();
                                roundBrace = false;
                            }
                            return OK;
                        } else {
                            printError("1Несоответствие типов в строке ");
                            return ERROR;
                        }
                    }
                }
            }
            if (flInt == 1) {
                searchNode = treeForSearch.FindInClass(treeForSearch, sc.nameId, com.company.DATA_TYPE.TYPE_METHOD);
                methodTree = searchNode;
            }
            if ((searchNode != null && flInt == 1) || flInt == 0) {
                if (tec_iterat == DOT) {
                    printError(sc.nameId + " это метод, после метода не должно быть точки. Строка ");
                    return ERROR;
                } else if (tec_iterat == ROUND_BRACE_OPEN) {
                    if (flInt == 0) {
                        getNextLex();
                        if (tec_iterat == ROUND_BRACE_CLOSE) {
                            getNextLex();
                            return OK;
                        } else {
                            printError("Ожидалась ). Строка ");
                            return ERROR;
                        }

                    } else {
                        int save = sc.getTek_i();
                        sc.setTek_i(searchNode.n.uk_iterat);
                        tree saveTREE = TREE;
                        TREE = searchNode;
                        System.out.println("Вызов функции " + sc.nameId);
                        returnOK = false;

                        if (ItsMethod() == ERROR) {
                            return ERROR;
                        }
                        flInt=1;
                        //val.valueT.setValue(TREE.n.valueT.getValue());
                        //val.LexType = TREE.n.LexType;
                        TREE = saveTREE;
                        sc.setTek_i(save);
                        getNextLex();
                        val.valueT.setValue(searchNode.n.valueT.getValue());
                        val.LexType = searchNode.n.LexType;
                        System.out.println("Функция " + searchNode.n.NameLex + " вернула " + val.getValueT());
                        returnOK = false;
                        getNextLex();

                    }
                }
            } else {
                printError("Неопределенная переменная. Строка " + sc.nameId);
                return ERROR;
            }

        }
        return NEXT;
    }

    public int ItsOperatorAndOperands() {
        while (tec_iterat != CURLY_BRACE_CLOSE) {
            if (ItsTypeData() == OK) {
                int a = ItsID();
                if (a == OK) {
                    if (tec_iterat == COMMA) {
                        getNextLex();
                    } else {
                        printError("Ожидалось ; в строке ");
                        return ERROR;
                    }

                } else if (a == ERROR) {
                    printError("Ожидалось описание переменной в строке ");
                    return ERROR;
                } else if (a == NEXT) {
                    printError("Описание метода неуместно. Строка ");
                    return ERROR;
                }
            } else {
                int itsOperator = ItsOperator();
                if (itsOperator == OK) {
                } else if (itsOperator == ERROR) {
                    return ERROR;
                } else if (tec_iterat == CLASS) {
                    getNextLex();
                    printError("Описание класса недопустимо! в строке");
                    return ERROR;
                }
            }
        }
        return OK;

    }

    public int ItsOperator() {

        int a = ItsReturn();
        if (a == OK) {
            return OK;
        } else if (a == ERROR) {
            return ERROR;
        } else {
            if (tec_iterat == ASSIGN) {
                //проверка существования переменной
                tree Search = null;
                if (flInt == 1) {
                    if ((Search = TREE.FindInClass(TREE, sc.nameId, com.company.DATA_TYPE.TYPE_PEREMEN)) == null) {
                        printError("Переменная не объявлена. Строка ");
                        return ERROR;
                    }
                }

                if (Search != null) {
                    Type_lex = Search.n.LexType;
                }
                Node val = new Node();
                a = ItsAssignment(val);
                if (a == OK) {
                    if (flInt == 1) {
                        addValue(Search, val);
                    }
                    return OK;
                }
            } else if (a == ERROR) {
                return ERROR;
            } else {
                a = ItsWhile();
                if (a == OK) {
                    return OK;
                } else if (a == ERROR)
                    return ERROR;

            }
        }
        return NEXT;
    }

    private void addValue(tree search, Node val) {
        if (val.LexType == search.n.LexType) {
            search.n.valueT.setValue(val.valueT.getValue());
        } else if (search.n.LexType == DOUBLE && val.LexType == INT) {
            double res = Double.valueOf((int) val.valueT.getValue());
            search.n.valueT.setValue(res);
        } else if (search.n.LexType == INT && val.LexType == DOUBLE) {
            double res = (double) val.valueT.getValue();
            int res1 = (int) res;
            search.n.valueT.setValue(res1);
        } else if (val.LexType == TYPE_UNIKNOW) {

        } else {
            printError("Несоответсвие типов в строке ");
        }
        //search.Print();
    }

    public int ItsReturn() {
        if (tec_iterat == RETURN) {
            //вернуть тип перменной метода

            Type_lex = Type_method;
            getNextLex();
            Node val = new Node();
            if (expression1(val) == OK) {
                if (tec_iterat == COMMA) {
                    //записать итог

                    if (flInt == 1) {
                        if(returnOK == false) {
                            addValue(methodTree, val);
                            returnOK = true;
                            flInt = 0;
                        }
                    }
                    getNextLex();
                    return OK;
                } else
                    printError("Ожидалась ; в строке ");
            } else
                printError("Ожидалась выражение в строке ");
        }
        return NEXT;
    }


    public int ItsWhile() {
        if (tec_iterat == WHILE) {
            Type_lex = WHILE;
            if (flInt == 1) {
                TREE.addWhile(IdClass[iForClass]);
                if (TREE == null) {
                    return ERROR;
                }
            }
            getNextLex();

            if (tec_iterat == ROUND_BRACE_OPEN) {
                int save_sc = sc.getTek_i();

                //сохраняем указатель в тексте
                int localFlInt = flInt;
                start:
                do {
                    getNextLex();

                    Node val = new Node();
                    int v = expression1(val);
                    //вычисляем выражение в скобках
                    //меняем флаг
                    if (v != OK && flInt == 1) {
                        flInt = 0;
                    }

                    if (tec_iterat == ROUND_BRACE_CLOSE) {
                        getNextLex();
                        if (tec_iterat == CURLY_BRACE_OPEN) {
                            getNextLex();
                            if (ItsOperatorAndOperands() == OK) {
                            }

                            if (tec_iterat == CURLY_BRACE_CLOSE) {

                                //восстанавливаем указатель
                                if (flInt == 0) {
                                    if (flInt == 1) {
                                        TREE = TREE.SetCur();
                                        TREE = TREE.CurUp();
                                    }
                                    flInt = localFlInt;
                                    getNextLex();
                                    return OK;
                                } else {
                                    sc.setTek_i(save_sc);
                                }

                            } else
                                printError("Ожидалась } в строке ");
                        } else
                            printError("Ожидалась ){ в строке ");
                    } else
                        printError("Ожидалась ( в строке ");


                } while (true);

            }
            return ERROR;
        }
        return NEXT;
    }

    public int ItsAssignment(Node val) {
        if (tec_iterat == ASSIGN) {
            getNextLex();
            if (expression1(val) == OK) {
                if (tec_iterat == COMMA) {
                    getNextLex();
                    return OK;
                } else {
                    printError("Ожидалось ; в строке ");
                    return ERROR;
                }
            } else
                printError("Ожидалось  выражение после = в строке ");
            return ERROR;
        } else
            return NEXT;
    }


    public int ItsExempleClass() {
        if (tec_iterat == NEW) {
            getNextLex();
            if (tec_iterat == ID) {

                //проверить правильный ли ид класса при объялении соответвует ли типу(tec_type == ID)(1)OK
                if (nameClass.equals(sc.nameId) == false) {
                    printError("Ошибка при объявлении экземпляра класса в строке ");
                    return ERROR;
                }

                getNextLex();
                if (tec_iterat == ROUND_BRACE_OPEN) {
                    getNextLex();
                    if (tec_iterat == ROUND_BRACE_CLOSE) {
                        getNextLex();
                        return OK;
                    } else {
                        printError("Ожидался ) в строке ");
                        return ERROR;
                    }
                } else {
                    printError("Ожидался ( в строке ");
                    return ERROR;
                }
            } else {
                printError("Ожидался идентификатор после new в строке ");
                return ERROR;
            }
        }
        return NEXT;
    }


    public int expression1(Node val) {
        initExp();
        if (expression2(val) != ERROR) {
            while (tec_iterat == EQUAL || tec_iterat == NOT_EQUAL) {
                int operator = tec_iterat;
                getNextLex();
                Node val1 = new Node();
                val1.valueT.setValue(0);
                if (expression2(val1) == ERROR) {
                    return ERROR;
                }
                if (operation(val, val1, operator) == ERROR) {
                    return ERROR;
                }
            }
        } else
            return ERROR;
        return OK;
    }

    public int expression2(Node val) {
        if (expression3(val) != ERROR) {
            while (tec_iterat == MORE || tec_iterat == MORE_EQUAL ||
                    tec_iterat == LESS || tec_iterat == LESS_EQUAL) {
                int operator = tec_iterat;
                getNextLex();
                Node val1 = new Node();
                val1.valueT.setValue(0);
                if (expression3(val1) == ERROR) {
                    return ERROR;
                }
                if (operation(val, val1, operator) == ERROR) {
                    return ERROR;
                }
            }
        } else
            return ERROR;
        return OK;
    }

    public int expression3(Node val) {
        if (expression4(val) != ERROR) {
            while (tec_iterat == PLUS || tec_iterat == MINUS) {
                int operator = tec_iterat;
                getNextLex();
                Node val1 = new Node();
                val1.valueT.setValue(0);
                if (expression4(val1) == ERROR) {
                    return ERROR;
                }
                if (operation(val, val1, operator) == ERROR) {
                    return ERROR;
                }

            }
        } else
            return ERROR;
        return OK;
    }

    public int expression4(Node val) {
        if (ItsElement(val) == ERROR) {
            return ERROR;
        }

        while (tec_iterat == SLASH || tec_iterat == STAR) {
            int operator = tec_iterat;
            getNextLex();

            Node val1 = new Node();
            val1.valueT.setValue(0);
            if (ItsElement(val1) == ERROR) {
                return ERROR;
            }
            if (operation(val, val1, operator) == ERROR) {
                return ERROR;
            }

        }
        return OK;
    }

    public int ItsElement(Node val) {

        int itsNameN = ItsNameN(val);
        if (itsNameN == OK) {
            return OK;
        } else if (itsNameN == ERROR) {
            return ERROR;
        } else {
            int a = ItsConstant(val);
            if (a == OK) {
                return OK;
            } else if (a == ERROR) {
                return ERROR;
            } else {
                if (tec_iterat == ROUND_BRACE_OPEN) {
                    getNextLex();
                    if (expression1(val) == ERROR) {
                        return ERROR;
                    }
                    if (tec_iterat == ROUND_BRACE_CLOSE) {
                        getNextLex();
                    }
                    return OK;
                }
            }
        }
        return NEXT;
    }

    public int ItsConstant(Node val) {
        if (tec_iterat == TYPE_IN || tec_iterat == TYPE_DOUBL) {
            if (tec_iterat == TYPE_IN) {
                val.valueT.setValue(Integer.parseInt(sc.number));
                val.LexType = INT;
            } else if (tec_iterat == TYPE_DOUBL) {
                val.valueT.setValue(Double.parseDouble(sc.number));
                val.LexType = DOUBLE;
            }

            getNextLex();
            return OK;
        } else
            return NEXT;
    }

    public int operation(Node n1, Node n2, int typeOperation) {
        if (flInt == 1) {
            int type1 = n1.LexType;
            int type2 = n2.LexType;
            if (type1 == TYPE_UNIKNOW || type2 == TYPE_UNIKNOW || (type1 != INT && type1 != DOUBLE)) {
                return ERROR;
            } else {
                switch (typeOperation) {
                    case PLUS:
                        switch (type1) {
                            case INT:
                                switch (type2) {
                                    case INT:
                                        int sum = (int) n1.valueT.getValue();
                                        sum += (int) n2.valueT.getValue();
                                        n1.valueT.setValue(sum);
                                        break;
                                    case DOUBLE:
                                        double sumID = 0;
                                        sumID += (int) n1.valueT.getValue();
                                        double ti2 = (double) n2.valueT.getValue();
                                        sumID += ti2;
                                        n1.valueT.setValue(sumID);
                                        n1.LexType = DOUBLE;
                                        break;
                                    default:
                                        break;
                                }

                                break;
                            case DOUBLE:
                                switch (type2) {
                                    case INT:
                                        double sumDI = (double) n1.valueT.getValue();
                                        int t2 = (int) n2.valueT.getValue();
                                        sumDI += (double) t2;
                                        n1.valueT.setValue(sumDI);
                                        break;
                                    case DOUBLE:
                                        double sumDD = (double) n1.valueT.getValue();
                                        double t2d = (double) n2.valueT.getValue();
                                        sumDD += t2d;
                                        n1.valueT.setValue(sumDD);
                                        break;
                                    default:
                                        break;
                                }

                                break;
                        }
                        break;
                    case MINUS:
                        switch (type1) {
                            case INT:
                                switch (type2) {
                                    case INT:
                                        int min = (int) n1.valueT.getValue();
                                        min -= (int) n2.valueT.getValue();
                                        n1.valueT.setValue(min);
                                        break;
                                    case DOUBLE:
                                        double minID = 0;
                                        minID += (int) n1.valueT.getValue();
                                        double ti2 = (double) n2.valueT.getValue();
                                        minID -= (int) ti2;
                                        n1.valueT.setValue(minID);
                                        n1.LexType = DOUBLE;
                                        break;
                                    default:
                                        break;
                                }

                                break;
                            case DOUBLE:
                                switch (type2) {
                                    case INT:
                                        double minDI = (double) n1.valueT.getValue();
                                        int t2 = (int) n2.valueT.getValue();
                                        minDI -= Double.valueOf(t2);
                                        n1.valueT.setValue(minDI);
                                        break;
                                    case DOUBLE:
                                        double minDD = (double) n1.valueT.getValue();
                                        double t2d = (double) n2.valueT.getValue();
                                        minDD -= t2d;
                                        n1.valueT.setValue(minDD);
                                        break;
                                    default:
                                        break;
                                }

                                break;
                        }
                        break;
                    case STAR:
                        switch (type1) {
                            case INT:
                                switch (type2) {
                                    case INT:
                                        int star = (int) n1.valueT.getValue();
                                        star *= (int) n2.valueT.getValue();
                                        n1.valueT.setValue(star);
                                        break;
                                    case DOUBLE:
                                        double starI = 0;
                                        starI += (int) n1.valueT.getValue();
                                        double t2 = (double) n2.valueT.getValue();
                                        starI *= (int) t2;
                                        int res = (int) starI;
                                        n1.valueT.setValue(res);
                                        break;
                                    default:
                                        break;

                                }
                                break;
                            case DOUBLE:
                                switch (type2) {
                                    case INT:
                                        double star = (double) n1.valueT.getValue();
                                        int t2 = (int) n2.valueT.getValue();
                                        star *= t2;
                                        n1.valueT.setValue(star);
                                        break;
                                    case DOUBLE:
                                        double starDD = (double) n1.valueT.getValue();
                                        double t2I = (double) n2.valueT.getValue();
                                        starDD *= t2I;
                                        n1.valueT.setValue(starDD);
                                        break;
                                    default:
                                        break;

                                }

                                break;
                        }

                        break;
                    case SLASH:
                        switch (type1) {
                            case INT:
                                switch (type2) {
                                    case INT:
                                        if ((int) n2.valueT.getValue() == 0) {
                                            printError("деление на 0 в строке ");
                                        } else {
                                            double minI = 0;
                                            minI += (int) n1.valueT.getValue();
                                            minI /= (int) n2.valueT.getValue();
                                            n1.valueT.setValue(minI);
                                            n1.LexType = DOUBLE;
                                        }
                                        break;
                                    case DOUBLE:
                                        if ((int) n2.valueT.getValue() == 0.0) {
                                            printError("деление на 0 в строке ");
                                        } else {
                                            double minID = 0.0;
                                            minID += (int) n1.valueT.getValue();
                                            double t2 = (double) n2.valueT.getValue();
                                            minID /= (int) t2;
                                            n1.valueT.setValue(minID);
                                        }
                                        break;
                                    default:
                                        break;
                                }

                                break;
                            case DOUBLE:
                                switch (type2) {
                                    case INT:
                                        if ((int) n2.valueT.getValue() == 0) {
                                            printError("деление на 0 в строке ");
                                        } else {
                                            double min = (double) n1.valueT.getValue();
                                            int t2 = (int) n2.valueT.getValue();
                                            min /= Double.valueOf(t2);
                                            n1.valueT.setValue(min);
                                        }
                                        break;
                                    case DOUBLE:
                                        if ((double) n2.valueT.getValue() == 0.0) {
                                            printError("деление на 0 в строке ");
                                        } else {
                                            double minDD = (double) n1.valueT.getValue();
                                            minDD /= (double) n2.valueT.getValue();
                                            n1.valueT.setValue(minDD);
                                        }
                                        break;
                                    default:
                                        break;
                                }


                                break;
                        }

                        break;
                    case EQUAL:
                            //double a = (double)n1.getValueT();
                            //printError(n2.getValueT().toString());

                        if (n1.valueT.getValue().equals( n2.valueT.getValue())) {
                            printError("Цикл");
                            return OK;
                        } else {
                            return ERROR;
                        }
                    case NOT_EQUAL:
                        if (!n1.valueT.getValue().equals( n2.valueT.getValue())) {
                            printError("Цикл");
                            return OK;
                        } else {
                            return ERROR;
                        }
                    case MORE:
                        double a = (double)n1.valueT.getValue();
                        double b = (double)  n2.valueT.getValue();
                        if (a > b){
                            printError("Цикл");
                            return OK;
                        } else {
                            return ERROR;
                        }
                    case MORE_EQUAL:
                        double a1 = (double)n1.valueT.getValue();
                        double b1 = (double)  n2.valueT.getValue();
                        if (a1 >= b1){
                            printError("Цикл");
                            return OK;
                        } else {
                            return ERROR;
                        }
                    case LESS_EQUAL:
                        double a2 = (double)n1.valueT.getValue();
                        double b2 = (double)  n2.valueT.getValue();
                        if (a2 <= b2){
                            printError("Цикл");
                            return OK;
                        } else {
                            return ERROR;
                        }
                    case LESS:
                        double a3 = (double)n1.valueT.getValue();
                        double b3 = (double)  n2.valueT.getValue();
                        if (a3 < b3){
                            printError("Цикл");
                            return OK;
                        } else {
                            return ERROR;
                        }


                    default:
                        break;
                }
            }

        }
        return OK;
    }
}