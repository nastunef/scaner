package com.company;

import com.company.Constans;

import java.util.ArrayList;
import java.util.LinkedList;

enum DATA_TYPE {TYPE_UNIKNOW, TYPE_INTEGER, TYPE_DOUBLE, TYPE_METHOD, TYPE_CLASS, TYPE_PEREMEN, TYPE_WHILE}

class Node {
    int LexType;// тип лексемы
    String IdClassLex; //идентификатор класса, к которому принадлежит лексема
    DATA_TYPE TypeOfLex;   // вид лексемы-конструкции
    String NameLex;//имя лексемы - идентификатора

    Node() { }

    Node(int LT, DATA_TYPE ToL, String IC, String NL) {
        LexType = LT;
        TypeOfLex = ToL;
        IdClassLex = IC;
        NameLex = NL;
    }

    Node copy() {
        return new Node(LexType, TypeOfLex, IdClassLex, NameLex);
    }
}

public class tree {

    Node n;
    tree Up, Left, Right;
    int index_s;

    tree(tree l, tree r, tree u, Node data) {

        Up = u;
        Left = l;
        Right = r;
        n = data.copy();
    }

    tree() {
        index_s = 0;
        n = new Node();
        Up = null;
        Left = null;
        Right = null;
        n = new Node();
    }

    public static tree Cur;
    public static LinkedList<tree> v = new LinkedList<>();

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
        while ((i != null) && (!nameLex.equals(i.n.NameLex))){
            i = i.Up;
        }
        return i;
    }

    tree FindRigthLeft(tree From, int TypeLexId) {
        // поиск прямых потомков заданной вершины From
        tree i = From.Right; // текущая вершина поиска
        while ((i != null) && (TypeLexId != i.n.LexType)) {
            i = i.Left;
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
    tree FindInMethod(tree From, String nameLex, DATA_TYPE typeOfLex){
        tree i = From;
        while((i!=null) && i.n.TypeOfLex != DATA_TYPE.TYPE_METHOD){
            if (nameLex.equals(i.n.NameLex) && i.n.TypeOfLex == typeOfLex) {
                return i;
            }
            i = i.Up;
        }
        return null;
    }
    tree FindInClass(tree From, String nameLex, DATA_TYPE typeOfLex){
        tree i = From;
        String nameClass = i.n.IdClassLex;
        while((i!=null) &&  i.n.IdClassLex.equals(nameClass)){
            if (nameLex.equals(i.n.NameLex ) && i.n.TypeOfLex == typeOfLex ) {
                return i;
            }
            i = i.Up;
        }
        return null;

    }


    tree FindUpTwoLevel(tree From, String nameLex, DATA_TYPE typeOfLex){
        tree i = From;
        while ((i != null) && (i.Up.Right != i)) {
            if (nameLex.equals(i.n.NameLex) && typeOfLex == i.n.TypeOfLex) {
                return i;
            }
            i = i.Up;
        }
        if (i != null) {
            while ((i != null) && (i.Up.Right != i)) {
                if (nameLex.equals(i.n.NameLex) && typeOfLex == i.n.TypeOfLex) {
                    return i;
                }
                i = i.Up;
            }
        }
        return null;
    }
    tree goUp(tree From){
        tree v = From;
        while(v!=v.Up.Right && v!=null){
            v.Print();
            v = v.Up;
        }
        Cur = v.Up;
        return v.Up;
    }
    void Print() {
        //Отладка
        System.out.println("Вершина с данными %s ----->" + n.NameLex);
        if (Left != null) System.out.println("слева данные %s" + Left.n.NameLex);
        if (Right != null) System.out.println("справа данные %s" + Right.n.NameLex);
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
        return Cur;
    }

    tree GetCur() {
        // получить значение текущего узла дерева
        return Cur;
    }
    tree addWhile(String IdClass){
        Node b = new Node(Constans.WHILE, DATA_TYPE.TYPE_WHILE, IdClass, "");

        return addMethodOrClass(b);
    }
    tree addClassMain(){
        Node main = new Node(Constans.CLASS,DATA_TYPE.TYPE_CLASS,"Main","Main");
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
        if(b.TypeOfLex == DATA_TYPE.TYPE_CLASS) {
            nameClass = b.NameLex;
        }else {
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

    tree SemInclude(int TypeLexA, DATA_TYPE t, String IdClass, String nameLex) {
        // занесение идентификатора a в таблицу с типом t
        //если нашли уже в дереве

        if(t == DATA_TYPE.TYPE_CLASS){
            if(Cur == null){
                Cur = new tree();
            }
            if(FindUp(Cur,nameLex) == null){ }
            else {
                PrintError("Повторное описание класса", nameLex);
                return null;
            }
        }else {
            if (DupControl(Cur, nameLex, t) == 1) {
                PrintError("Повторное описание идентификатора ", nameLex);
                return null;
            }
        }

        Node b = new Node(TypeLexA, t, IdClass, nameLex);
        if (t == DATA_TYPE.TYPE_PEREMEN) {
            return addPeremen(b);
        } else{
            return addMethodOrClass(b);
        }
    }

    void SemSetType(tree Addr, DATA_TYPE t) {
        // установить тип t для переменной по адресу Addr
        Addr.n.TypeOfLex = t;
    }

    tree SemGetType(String NameLex) {
        // найти в таблице переменную с именем TypeLexA
        // и вернуть ссылку на соответствующий элемент дерева
        tree v = FindUp(Cur, NameLex);
        if (v == null) {
            PrintError("Отсутствует описание идентификатора ", NameLex);
        }
        if (v.n.TypeOfLex == DATA_TYPE.TYPE_DOUBLE || v.n.TypeOfLex == DATA_TYPE.TYPE_INTEGER) {
            PrintError("Неверное использование вызова метода ", NameLex);
        }
        return v;
    }

    DATA_TYPE typeLex(String NameLex) {
        tree search = SemGetType(NameLex);
        if (search != null)
            return search.n.TypeOfLex;
        else
            return DATA_TYPE.TYPE_UNIKNOW;
    }


    tree SemGetMethod(String NameLex) {
        // найти в таблице метод с именем TypeLexA
        // и вернуть ссылку на соответствующий элемент дерева
        tree v = FindUp(Cur, NameLex);
        if (v == null) {
            PrintError("Отсутствует описание функции ", NameLex);
        }
        if (v.n.TypeOfLex != DATA_TYPE.TYPE_METHOD) {
           return null;
        }
        return v;
    }
    tree SemGetClass(String NameLex) {
        // найти в таблице класс с именем TypeLexA
        // и вернуть ссылку на соответствующий элемент дерева
        tree v = FindUpOneLevel(Cur, NameLex,DATA_TYPE.TYPE_CLASS);
        if (v == null) {
            PrintError("Отсутствует описание класса ", NameLex);
        }else
            if (v.n.TypeOfLex != DATA_TYPE.TYPE_CLASS) {
               return null;
            }
        return v;
    }
    tree GetClass(tree From,String nameClass){
        tree Search = FindUp(From, nameClass);
        if(Search != null){
            return Search.Right;
        }
        return null;

    }

    int DupControl(tree Addr, String nameLex, DATA_TYPE t) {
        // проверка идентификатора TypeLexA на повторное описание внутри блока
        if (FindUpOneLevel(Addr, nameLex, t) == null) return 0;
        return 1;
    }

    void exitMetodOrClass() {
        index_s--;
    }

}