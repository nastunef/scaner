package com.company;


import java.util.ArrayList;
import java.util.Queue;

import com.company.MyScanner;

import static com.company.Constans.*;

//защита от падения конструкции протестить
//посмотреть принты

public class SyntaxAnalize {
    enum DATA_TYPE {TYPE_UNIKNOW, TYPE_INTEGER, TYPE_DOUBLE, TYPE_METHOD, TYPE_CLASS}

    public int tec_iterat;
    private String content;
    private MyScanner sc;
    //тип лексемы
    public int Type_lex;
    public int Type_method;
    public String nameClass;
    //дерево идентификаторов
    public tree TREE = new tree();
    //очередь для классов
    int iForClass;
    public String IdClass[] = new String[5];
    //дерево для поиска
    private tree treeForSearch;

    SyntaxAnalize(String newContent) {
        tec_iterat = 0;
        content = newContent;
        sc = new MyScanner();
        iForClass = 0;

    }

    public void printError(String error) {
        System.out.println(error + sc.str);
    }

    public int SyntaxAnal(ArrayList lex, int i) {
        return 0;
    }

    public void getNextLex() {
        tec_iterat = sc.scanner(content, sc.tek_i);
    }

    public int ItsProgram() {
        getNextLex();
        if (tec_iterat == CLASS) {
            getNextLex();
            if (tec_iterat == MAIN) {
                //добавили маин в дерево
                IdClass[iForClass] = "Main";
                TREE = TREE.SemInclude(CLASS, com.company.DATA_TYPE.TYPE_CLASS, IdClass[iForClass], "MAIN");
                if (TREE == null) {
                    return ERROR;
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
                iForClass++;
                //проверка на существование класса, если нет то внести в дерево
                TREE = TREE.SemInclude(CLASS, com.company.DATA_TYPE.TYPE_CLASS, IdClass[iForClass], sc.nameId);
                if (TREE == null) {
                    return ERROR;
                }
                IdClass[iForClass] = sc.nameId;
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
                TREE = TREE.SetCur();
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
            }
            getNextLex();
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
                return OK;
            } else if (ItsID == ERROR) {
                return ERROR;
            } else {
                //если объявление метода
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
                //проверка на существование функции, если существует, то ошибка если нет то внести!
                TREE = TREE.SemInclude(Type_lex, com.company.DATA_TYPE.TYPE_METHOD, IdClass[iForClass], sc.nameId);
                if (TREE == null) {
                    return ERROR;
                }
                getNextLex();
                if (tec_iterat == CURLY_BRACE_OPEN) {
                    Type_method = Type_lex;
                    getNextLex();
                    if (ItsOperatorAndOperands() == OK) {
                        if (tec_iterat == CURLY_BRACE_CLOSE) {
                            TREE = TREE.SetCur();
                            Type_method = 0;
                            getNextLex();
                            return OK;
                        } else {
                            printError("Ожидалась } в строке ");
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
        if (tec_iterat == VIRGULE) {
            TREE = TREE.SemInclude(Type_lex, com.company.DATA_TYPE.TYPE_PEREMEN, IdClass[iForClass], sc.nameId);
            if (TREE == null) {
                return ERROR;
            }
            getNextLex();
            return ItsID();
        }
        if (tec_iterat == COMMA) {
            TREE = TREE.SemInclude(Type_lex, com.company.DATA_TYPE.TYPE_PEREMEN, IdClass[iForClass], sc.nameId);
            if (TREE == null) {
                return ERROR;
            }
            getNextLex();
            return OK;
        } else if (tec_iterat == ASSIGN) {
            TREE = TREE.SemInclude(Type_lex, com.company.DATA_TYPE.TYPE_PEREMEN, IdClass[iForClass], sc.nameId);
            System.out.println(sc.nameId + '!');
            if (TREE == null) {
                return ERROR;
            }
            getNextLex();
            int newClass = ItsExempleClass();
            if (newClass == OK) {
            } else if (newClass == ERROR) {
                return ERROR;
            } else {
                int ItsV1 = ItsV1();
                if (ItsV1 == OK) {
                } else if (ItsV1 == ERROR) {
                    return ERROR;
                }
            }
            if (tec_iterat == COMMA) {
                getNextLex();
                return OK;
            } else {
                printError("Ожидалось ; в строке ");
                return ERROR;
            }
        } else if (tec_iterat == ROUND_BRACE_OPEN) {
            return NEXT;
        } else return ERROR;

    }

    public int ItsData() {
        int a = ItsList();
        if (a == OK) {
            if (tec_iterat == COMMA) {
                getNextLex();
                return OK;
            } else if (tec_iterat == ROUND_BRACE_OPEN) {
                return NEXT;
            } else if (tec_iterat == ASSIGN) {
                return NEXT;
            } else return ERROR;
        } else if (a == ERROR)
            return ERROR;
        else
            return NEXT;
    }

    public int ItsNameN() {
        //ПРОВЕРКА НА СУЩЕСТВОВАНИЕ ПЕРЕМЕННОЙ
        //перед входом в эту функцию обнулить глобаньную
        //найти в дереве(это может быть функцией)
        if (treeForSearch == null) {
            treeForSearch = TREE;
        }
        if (tec_iterat == ID) {
            tree searchNode = TREE.FindUpOneLevel(TREE, sc.nameId, com.company.DATA_TYPE.TYPE_CLASS);
            getNextLex();
            if (searchNode != null) {
                if (tec_iterat == DOT) {
                    getNextLex();
                    if (tec_iterat != ID) {
                        printError("После точки ожидалось продолжение. Строка ");
                        return ERROR;
                    } else {
                        treeForSearch = searchNode;
                        while (treeForSearch.Left != null) {
                            treeForSearch = treeForSearch.Left;
                        }
                        return ItsNameN();
                    }
                } else {
                    printError("Переменную класса нельзя присвоить. Строка ");
                    return ERROR;
                }

            }
            searchNode = treeForSearch.FindUpOneLevel(treeForSearch, sc.nameId, com.company.DATA_TYPE.TYPE_PEREMEN);
            if (searchNode != null) {
                if (tec_iterat == DOT) {
                    printError(sc.nameId.toString() + " это переменная, послне нее не должно быть точки. Строка ");
                    return ERROR;
                } else if (tec_iterat != CURLY_BRACE_OPEN) {
                    if (Type_lex == WHILE) {
                        Type_lex = searchNode.n.LexType;
                    }
                    if (searchNode.n.LexType == Type_lex) {
                        return OK;
                    } else {
                        printError("Несоответствие типов в строке ");
                        return ERROR;
                    }
                }
            }
            searchNode = treeForSearch.FindUpOneLevel(treeForSearch, sc.nameId, com.company.DATA_TYPE.TYPE_METHOD);
            if (searchNode != null) {
                if (tec_iterat == DOT) {
                    printError(sc.nameId.toString() + " это метод, после метода не должно быть точки. Строка ");
                    return ERROR;
                } else if (tec_iterat == ROUND_BRACE_OPEN) {
                    getNextLex();
                    if (tec_iterat == ROUND_BRACE_CLOSE) {
                        getNextLex();
                        if (Type_lex == WHILE) {
                            Type_lex = searchNode.n.LexType;
                        } else if (Type_lex == searchNode.n.LexType) {
                            return OK;
                        } else {
                            printError("Несоответствие типов в строке ");
                            return ERROR;
                        }
                    } else {
                        printError("Ожидалась ). Строка ");
                        return ERROR;
                    }
                } else {
                    printError("Ожидалось (. Строка ");
                    return ERROR;
                }
            } else {
                printError("Неопределенная переменная. Строка " + sc.nameId);
                return ERROR;
            }
        }
        return NEXT;
    }

    public int ItsName() {
        //ПРОВЕРКА НА СУЩЕСТВОВАНИЕ ПЕРЕМЕННОЙ
        //перед входом в эту функцию обнулить глобаньную
        if (tec_iterat == ID) {
            tree searchNode = TREE.FindUpOneLevel(TREE, sc.nameId, com.company.DATA_TYPE.TYPE_CLASS);
            //найти в дереве(это может быть функцией)
            getNextLex();
            if (tec_iterat == DOT && searchNode.n.LexType == CLASS) {
                //проверить есть ли ответвление вправо если нет, то ошибка, если нет то запомнить в глобальной ответвление
                getNextLex();
                if (ItsName() == OK) {

                    return OK;
                } else {
                    printError("Ожидался идентификатор после точки в строке ");
                    return ERROR;
                }
            }
            return OK;
        }
        return ERROR;
    }

    public int ItsOperatorAndOperands() {
        while (tec_iterat != CURLY_BRACE_CLOSE) {
            if (ItsTypeData() == OK) {
                int a = ItsID();
                if (a == OK) {

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
            a = ItsBreak();
            if (a == OK) {
                return OK;
            } else if (a == ERROR) {
                return ERROR;
            } else {
                if (tec_iterat == ID) {
                    //проверка существования переменной
                    tree Search;
                    if ((Search = TREE.FindUpOneLevel(TREE, sc.nameId, com.company.DATA_TYPE.TYPE_PEREMEN)) == null) {
                        printError("Переменная не объявлена. Строка ");
                        return ERROR;
                    }
                    getNextLex();
                    Type_lex = Search.n.LexType;
                    a = ItsAssignment();
                    if (a == OK) {
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
        }
        return NEXT;
    }

    public int ItsReturn() {
        if (tec_iterat == RETURN) {
            //вернуть тип перменной метода
            if (Type_method == 0) {
                printError("Return является лишним");
                return ERROR;
            }
            Type_lex = Type_method;
            System.out.println(Type_lex);
            getNextLex();
            if (ItsV1() == OK) {
                if (tec_iterat == COMMA) {
                    getNextLex();
                    return OK;
                } else
                    printError("Ожидалась ; в строке ");
            } else
                printError("Ожидалась выражение в строке ");
        }
        return NEXT;
    }

    public int ItsBreak() {
        if (tec_iterat == BREAK) {
            TREE = TREE.goUp(TREE);
            getNextLex();
            if (tec_iterat == COMMA) {
                getNextLex();
                return OK;
            } else
                printError("Ожидалась ; в строке ");
        }
        return NEXT;
    }

    public int ItsWhile() {
        if (tec_iterat == WHILE) {
            Type_lex = WHILE;
            TREE.addWhile(IdClass[iForClass]);
            if (TREE == null) {
                return ERROR;
            }

            getNextLex();
            if (tec_iterat == ROUND_BRACE_OPEN) {
                getNextLex();
                if (ItsV1() == OK) {
                    if (tec_iterat == ROUND_BRACE_CLOSE) {
                        getNextLex();
                        if (tec_iterat == CURLY_BRACE_OPEN) {
                            getNextLex();
                            if (ItsOperatorAndOperands() == OK) {
                            }
                            if (tec_iterat == CURLY_BRACE_CLOSE) {
                                TREE = TREE.SetCur();
                                getNextLex();
                                return OK;
                            } else
                                printError("Ожидалась } в строке ");
                        } else
                            printError("Ожидалась ){ в строке ");
                    }
                } else
                    printError("Ожидалась ( в строке ");
            }
            return ERROR;
        }
        return NEXT;
    }

    public int ItsAssignment() {
        if (tec_iterat == ASSIGN) {
            getNextLex();
            if (ItsV1() == OK) {
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


    public int ItsList() {
        int a = ItsPeremen();
        if (a == OK) {
            if (tec_iterat == VIRGULE) {
                getNextLex();
                return ItsList();
            }
            return OK;
        } else if (a == ERROR)
            return ERROR;
        else return NEXT;
    }

    public int ItsPeremen() {
        if (tec_iterat == ID) {
            //проверить обявлена ли она(2)OK
            if (TREE.DupControl(TREE, sc.nameId, com.company.DATA_TYPE.TYPE_PEREMEN) == 0) {
                //обьявитьOK
                TREE = TREE.SemInclude(Type_lex, com.company.DATA_TYPE.TYPE_PEREMEN, IdClass[iForClass], sc.nameId);
                if (TREE == null) {
                    return ERROR;
                }
            } else {
                printError("Переменная " + sc.nameId + " уже обьявлена. Строка  ");
                return ERROR;
            }
            getNextLex();

            //если знак =
            if (tec_iterat == ASSIGN) {
                getNextLex();

                //проверка на обьявление экземпляра классаOK
                int a = ItsExempleClass();
                if (a == OK) {
                    return OK;
                } else if (a == ERROR) {
                    return ERROR;
                } else {
                    //проверка на присваивание значения переменнойOK
                    int b = ItsV1();
                    //проверить соответсвие типов(3)OK
                    if (b == OK) {
                        return OK;
                    } else if (b == ERROR)
                        return ERROR;
                    else return NEXT;
                }
            }
            return OK;
        } else if (tec_iterat == VIRGULE || tec_iterat == EQUAL || tec_iterat == COMMA)
            return NEXT;
        else return ERROR;
    }

    public int ItsExempleClass() {

        if (tec_iterat == NEW) {
            //проверить есть ли такой класс(2)OK
            if (TREE.SemGetClass(nameClass) == null) {
                printError("Тип данных не определен в строке  ");
                return ERROR;
            }

            getNextLex();
            if (tec_iterat == ID) {
                //проверить правильный ли ид класса при объялении соответвует ли типу(tec_type == ID)(1)OK
                if (nameClass.equals(sc.nameId) == false) {
                    System.out.println(nameClass + sc.nameId);
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


    public int ItsV1() {
        treeForSearch = null;
        int a = ItsElement();
        if (a == OK) {
            if (tec_iterat == EQUAL || tec_iterat == NOT_EQUAL) {
                getNextLex();

            } else {
                int b = ItsV2();
                if (b == NEXT) {
                    //если next то значит нужно выйти из функции
                    return OK;
                } else if (b == ERROR) {
                    return ERROR;
                }
            }
            if (ItsV1() == OK)
                return OK;
            else return ERROR;
        } else if (a == ERROR)
            return ERROR;
        return NEXT;
    }

    public int ItsV2() {
        if (tec_iterat == MORE || tec_iterat == MORE_EQUAL || tec_iterat == LESS
                || tec_iterat == LESS_EQUAL) {
            if (Type_lex == WHILE) {
                getNextLex();
                return OK;
            } else {
                printError(">, >=, <, <= только для цикла while. Строка ");
                return ERROR;
            }

        } else
            return ItsV3();
    }

    public int ItsV3() {
        if (tec_iterat == PLUS || tec_iterat == MINUS) {
            getNextLex();
            return OK;
        } else
            return ItsV4();
    }

    public int ItsV4() {
        if (tec_iterat == STAR || tec_iterat == SLASH) {
            getNextLex();
            return OK;
        }
        return NEXT;
    }

    public int ItsElement() {
        //вот тут обнулить переменную для итснаме
        int itsNameN = ItsNameN();
        if (itsNameN == OK) {
            return OK;
        } else if (itsNameN == ERROR) {
            return ERROR;
        } else {
            int a = ItsConstant();
            if (a == OK) {
                return OK;
            } else if (a == ERROR)
                return ERROR;
        }
        return NEXT;
    }

    public int ItsConstant() {
        if (tec_iterat == TYPE_IN || tec_iterat == TYPE_DOUBL) {
            if ((tec_iterat == TYPE_IN && Type_lex == INT) || (tec_iterat == TYPE_DOUBL && Type_lex == DOUBLE)) {
                getNextLex();
                return OK;
            } else {
                printError("Ошибка. Присваемаемое значение не соответствует типу переменной) строка ");
                return ERROR;
            }
        } else
            return NEXT;
    }
}