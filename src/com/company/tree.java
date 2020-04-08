package com.company;

import java.util.LinkedList;
import java.util.Set;

import static com.company.Constans.*;

enum DATA_TYPE {TYPE_UNIKNOW, TYPE_METHOD, TYPE_CLASS, TYPE_PEREMEN, TYPE_OBJECT_CLASS}

class Node {

    int LexType;// тип лексемы(int double)
    String IdClassLex; //идентификатор класса, к которому принадлежит лексема
    DATA_TYPE TypeOfLex;   // вид лексемы-конструкции
    String NameLex;//имя лексемы - идентификатор
    int uk_iterat;

    ValueType valueT; // значение переменной

    public ValueType getValueT() {
        return valueT;
    }

    public void setValueT(ValueType valueT) {
        this.valueT = valueT;
    }

    Node() {
        valueT = new ValueType();
    }

    Node(int LT, DATA_TYPE ToL, String IC, String NL) {
        LexType = LT;
        TypeOfLex = ToL;
        IdClassLex = IC;
        NameLex = NL;
        valueT = new ValueType();
    }

    Node(int LT, DATA_TYPE ToL, String IC, String NL, double newValue) {
        LexType = LT;
        TypeOfLex = ToL;
        IdClassLex = IC;
        NameLex = NL;
        valueT = new ValueType();
        valueT.setValue(newValue);
    }

    Node copy() {
        return new Node(LexType, TypeOfLex, IdClassLex, NameLex);
    }

}

public class tree {

    Node n;
    tree Up, Left, Right;


    tree(tree l, tree r, tree u, Node data) {

        Up = u;
        Left = l;
        Right = r;
        n = data.copy();
    }

    tree() {
        n = new Node();
        Up = null;
        Left = null;
        Right = null;
    }

    public static tree getCur() {
        return Cur;
    }

    public static void setCur(tree cur) {
        Cur = cur;
    }

    public static tree Cur;
    public static LinkedList<tree> v = new LinkedList<>();

    public void Pr() {
        System.out.println("Печать всего");
        tree begin = Cur;
        while (begin.Up != null) {
            begin = begin.Up;
        }
        begin.Print();
    }

    // ФУНКЦИИ ОБРАБОТКИ БИНАРНОГО ДЕРЕВА
    public void setLeft(Node Data) {
        tree a = new tree(null, null, this, Data);
        Left = a;
    }

    public void setRight(Node Data) {
        tree a = new tree(null, null, this, Data);
        Right = a;

    }

    tree FindUp(tree From, String nameLex) {
        //Поиск данных в дереве до его корня вверх по связям
        tree i = From;
        while ((i != null) && (!nameLex.equals(i.n.NameLex))) {
            i = i.Up;
        }
        return i;
    }

    tree FindUpOneLevel(tree From, String nameLex, DATA_TYPE typeOfLex) {
        tree i = From;
        while ((i != null) && (i.Up.Right != i)) {
            if (nameLex.equals(i.n.NameLex) && typeOfLex == i.n.TypeOfLex) {
                return i;
            }
            i = i.Up;
        }
        return null;
    }

    tree FindInClass(tree From, String nameLex, DATA_TYPE typeOfLex) {
        tree i = From;
        String nameClass = i.n.IdClassLex;
        while ((i != null)) {
            if (nameLex.equals(i.n.NameLex) && i.n.TypeOfLex == typeOfLex) {
                return i;
            }
            i = i.Up;
        }
        return null;
    }

    tree goUp(tree From) {
        tree v = From;
        while (v != v.Up.Right && v != null) {
            v.Print();
            v = v.Up;
        }
        Cur = v.Up;
        return v.Up;
    }

    void Print() {
        //Отладка

        System.out.println("Вершина с данными: " + n.NameLex);
        System.out.println("Значение: "+ n.getValueT());
        System.out.println("Тип данных: " + n.LexType);
        if (Left != null) System.out.println("слева данные " + Left.n.NameLex);
        if (Right != null) System.out.println("справа данные " + Right.n.NameLex);
        System.out.println("\n");
        if (Left != null) Left.Print();
        if (Right != null) Right.Print();
    }

    void PrintError(String s, String a) {
        System.out.println(s + a);
    }

    // СЕМАНТИЧЕСКИЕ ПОДПРОГРАММЫ
    tree SetCur() {
        // установить текущий узел дерева
        Cur = v.getLast();
        v.removeLast();
        /*if (Cur.n.TypeOfLex == DATA_TYPE.TYPE_UNIKNOW) {
            return Cur.Up;
        }*/
        return Cur;
    }

    public tree CurUp() {
        Cur = Cur.Up;
        return Cur;
    }

    tree addWhile(String IdClass) {
        Node b = new Node(Constans.TYPE_UNIKNOW, DATA_TYPE.TYPE_UNIKNOW, IdClass, "");
        return addMethodOrClass(b);
    }

    tree addClassMain() {
        Node main = new Node(Constans.CLASS, DATA_TYPE.TYPE_CLASS, "Main", "Main");
        Cur = new tree();
        return addMethodOrClass(main);
    }

    tree addMethodOrClass(Node b) {

        Cur.setLeft(b);
        // сделали вершину - метод/класс
        Cur = Cur.Left;
        // это точка возврата после выхода из метода/класса
        v.addLast(Cur);
        String nameClass;
        if (b.TypeOfLex == DATA_TYPE.TYPE_CLASS) {
            nameClass = b.NameLex;
        } else {
            nameClass = b.IdClassLex;
        }
        Node c = new Node(Constans.EMPTY, DATA_TYPE.TYPE_UNIKNOW, nameClass, "");
        Cur.setRight(c);
        // сделали пустую вершину  для вложеных объявлений
        Cur = Cur.Right;
        return v.getLast();
    }

    tree addPeremen(Node b) {
        // сделали вершину - переменную
        Cur.setLeft(b);
        Cur = Cur.Left;
        return Cur;
    }
    private tree cloneBrunch(Node b, tree copy) {

        Cur.setLeft(b);
        // сделали вершину - объект класса
        Cur = Cur.Left;
        // это точка возврата после выхода из метода/класса
        v.addLast(Cur);
        copy.Up = Cur;
        Cur.Right = copy;
        //SetCur();

        return v.getLast();
    }

    tree SemInclude(int TypeLexA, DATA_TYPE t, String IdClass, String nameLex) {
        // занесение идентификатора a в таблицу с типом t
        //если нашли уже в дереве
        PrintError("Внесли ", nameLex);
        if (t == DATA_TYPE.TYPE_CLASS) {
            if (Cur == null) {
                Cur = new tree();
            }
            if (FindUp(Cur, nameLex) == null) {
            } else {
                PrintError("Повторное описание класса", nameLex);
                return null;
            }
        } else {
            if (DupControl(Cur, nameLex, t) == 1) {
                PrintError("Повторное описание идентификатора ", nameLex);
                return null;
            }
        }

        Node b = new Node(TypeLexA, t, IdClass, nameLex);

        if (TypeLexA == OBJECT_CLASS) {
            b = new Node(TypeLexA,DATA_TYPE.TYPE_OBJECT_CLASS,IdClass,nameLex);
            return addObjectClass(b);
        } else if (t == DATA_TYPE.TYPE_PEREMEN) {
            return addPeremen(b);

        } else {
            return addMethodOrClass(b);
        }
    }

    private tree addObjectClass(Node b) {
        tree copy = cloneBrunchClass(b.IdClassLex);
        if (copy == null) {
            PrintError("Класс не сущестсвует", b.IdClassLex);
            return null;
        }
        return cloneBrunch(b, copy);
    }



    private tree cloneBrunchClass(String idClassLex) {
        tree findClass = SemGetClass(idClassLex);
        if (findClass != null && findClass.Right != null) {
            tree copyTree = new tree();
            copyTree = copyWithParent(findClass.Right, copyTree);
            return copyTree;
        }
        return null;
    }

    public tree copyWithParent(tree parentOverride, tree newTree) {
        Node out = new Node(parentOverride.n.LexType, parentOverride.n.TypeOfLex, parentOverride.n.IdClassLex,
                parentOverride.n.NameLex);
        newTree.n = out;
        newTree.Left = new tree();
        if (parentOverride.Left != null) {
            newTree.Left = copyWithParent(parentOverride.Left, newTree.Left);
        }
        return newTree;
    }

    tree SemGetClass(String NameLex) {
        // найти в таблице класс с именем TypeLexA
        // и вернуть ссылку на соответствующий элемент дерева
        tree v = FindInClass(Cur,NameLex,DATA_TYPE.TYPE_CLASS);
        //tree v = FindUpOneLevel(Cur, NameLex, DATA_TYPE.TYPE_CLASS);
        if (v == null) {
            PrintError("Отсутствует описание класса ", NameLex);
        } else if (v.n.TypeOfLex != DATA_TYPE.TYPE_CLASS) {
            return null;
        }
        return v;
    }

    tree GetClass(tree From, String nameClass) {
        tree Search = FindUp(From, nameClass);
        if (Search != null) {
            return Search.Right;
        }
        return null;
    }

    int DupControl(tree Addr, String nameLex, DATA_TYPE t) {
        // проверка идентификатора TypeLexA на повторное описание внутри блока
        if (FindUpOneLevel(Addr, nameLex, t) == null) return 0;
        return 1;
    }


}