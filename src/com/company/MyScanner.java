package com.company;

import static com.company.Constans.*;

public class MyScanner {

    public int tek_i;
    public int str;
    public String nameId;
    public String number;
    int history;
    public int MAXLEX = 15;

    public MyScanner() {

        tek_i = 0;
        str = 1;
    }
    public int getTek_i(){
        return tek_i;
    }
    public void setTek_i(int newTec){
        tek_i = newTec;
    }
    public boolean Num(char t) {
        if (t >= '0' && t <= '9')
            return true;
        else
            return false;
    }

    public boolean HexLetter(char t) {
        if ((t >= 'a' && t <= 'z') || (t >= 'A' && t <= 'Z')) {
            return true;
        } else {
            return false;
        }
    }

    public boolean it_is_int(String lex, int i) {
        if (i + 3 <= lex.length()) {
            if (lex.charAt(i) == 'i' && lex.charAt(i + 1) == 'n' && lex.charAt(i + 2) == 't' && (((lex.length() - 1 >= i + 3)
                    && (Num(lex.charAt(i + 3)) == false)) || (lex.length() == i + 3))) {
                tek_i = i + 3;
                return true;
            }
        }
        return false;
    }
    public boolean it_is_new(String lex, int i) {
        if (i + 3 <= lex.length()) {
            if (lex.charAt(i) == 'n' && lex.charAt(i + 1) == 'e' && lex.charAt(i + 2) == 'w' && (((lex.length() - 1 >= i + 3)
                    && (Num(lex.charAt(i + 3)) == false)) || (lex.length() == i + 3))) {
                tek_i = i + 3;
                return true;
            }
        }
        return false;
    }
    public boolean it_is_double(String lex, int i) {
        if (i + 5 <= lex.length() - 1) {
            if (lex.charAt(i) == 'd' && lex.charAt(i + 1) == 'o' && lex.charAt(i + 2) == 'u' && lex.charAt(i + 3) == 'b' &&
                    lex.charAt(i + 4) == 'l' && lex.charAt(i + 5) == 'e' && (((lex.length() - 1 >= i + 6)
                    && (Num(lex.charAt(i + 6)) == false)) || (lex.length() == i + 6))) {
                tek_i = i + 6;
                return true;
            }
        }
        return false;
    }

    public boolean it_is_return(String lex, int i) {
        if (i + 5 <= lex.length() - 1) {
            if (lex.charAt(i) == 'r' && lex.charAt(i + 1) == 'e' && lex.charAt(i + 2) == 't' && lex.charAt(i + 3) == 'u' &&
                    lex.charAt(i + 4) == 'r' && lex.charAt(i + 5) == 'n' && (((lex.length() - 1 >= i + 6)
                    && (Num(lex.charAt(i + 6)) == false)) || (lex.length() == i + 6))) {
                tek_i = i + 6;
                return true;
            }
        }
        return false;
    }

    public boolean it_is_while(String lex, int i) {
        if (i + 4 <= lex.length() - 1) {
            if (lex.charAt(i) == 'w' && lex.charAt(i + 1) == 'h' && lex.charAt(i + 2) == 'i' && lex.charAt(i + 3) == 'l' &&
                    lex.charAt(i + 4) == 'e' && (((lex.length() - 1 >= i + 5)
                    && (Num(lex.charAt(i + 5)) == false)) || (lex.length() == i + 5))) {
                tek_i = i + 5;
                return true;
            }
        }
        return false;
    }

    public boolean it_is_class(String lex, int i) {
        if (i + 4 <= lex.length() - 1) {
            if (lex.charAt(i) == 'c' && lex.charAt(i + 1) == 'l' && lex.charAt(i + 2) == 'a' && lex.charAt(i + 3) == 's' &&
                    lex.charAt(i + 4) == 's' && (((lex.length() - 1 >= i + 5)
                    && (Num(lex.charAt(i + 5)) == false)) || (lex.length() == i + 5))) {
                tek_i = i + 5;
                return true;
            }
        }
        return false;
    }
    public boolean it_is_break(String lex, int i) {
        if (i + 4 <= lex.length() - 1) {
            if (lex.charAt(i) == 'b' && lex.charAt(i + 1) == 'r' && lex.charAt(i + 2) == 'e' && lex.charAt(i + 3) == 'a' &&
                    lex.charAt(i + 4) == 'k' && (((lex.length() - 1 >= i + 5)
                    && (Num(lex.charAt(i + 5)) == false)) || (lex.length() == i + 5))) {
                tek_i = i + 5;
                return true;
            }
        }
        return false;
    }

    public boolean it_is_main(String lex, int i) {
        if (i + 3 <= lex.length() - 1) {
            if (lex.charAt(i) == 'm' && lex.charAt(i + 1) == 'a' && lex.charAt(i + 2) == 'i' && lex.charAt(i + 3) == 'n' &&
                    (((lex.length() - 1 >= i + 4) && (Num(lex.charAt(i + 4)) == false)) || (lex.length() == i + 4))) {
                tek_i = i + 4;
                return true;
            }
        }
        return false;
    }

    public boolean it_is_id(String lex, int i) {
        int j =0;
        String NameId ="";
        String s;
        while (HexLetter(lex.charAt(i)) || lex.charAt(i) == '_' || Num(lex.charAt(i))) {
            NameId += lex.charAt(i);
            j++;
            if (i + 1 == lex.length()) {
                tek_i = i + 1;
                return true;
            }
            i++;
        }

        nameId = String.valueOf(NameId);
        tek_i = i;
        return true;
    }

    public boolean it_is_double_ex(String lex, int i) {
        if (i >= lex.length()) {
            return false;
        }
        while (Num(lex.charAt(i))) {
            if (i + 1 == lex.length()) {
                tek_i = i + 1;
                return false;
            }
            number+=lex.charAt(i);
            i++;
        }
        if (lex.charAt(i) == 'e') {
            number+=lex.charAt(i);
            if (i + 1 == lex.length()) {
                tek_i = i + 1;
                return true;
            }
            i++;
            if (lex.charAt(i) == '-' || lex.charAt(i) == '+') {
                number+=lex.charAt(i);
                if (i + 1 == lex.length()) {
                    tek_i = i;
                    return true;
                }
                i++;
            }
            while (Num(lex.charAt(i))) {
                number+=lex.charAt(i);
                if (i + 1 == lex.length()) {
                    tek_i = i + 1;
                    return true;
                }
                i++;
            }
            if (HexLetter(lex.charAt(i))) {
                return false;
            } else {
                tek_i = i;
                return true;
            }
        } else return false;
    }
    public int nextProbel(String lex, int i){
        while (lex.charAt(i) == ' ' || lex.charAt(i) == '\n' || lex.charAt(i) == '\t' || lex.charAt(i) == '\r') {
            if(lex.charAt(i) == '\n'){
                str += 1;
            }
            if (i + 1 == lex.length()) {
                tek_i = i + 1;
                return END;
            }
            i++;
        }
        tek_i = i;
        return OK;
    }
    public int scanner(String lex, int i) {
        if (lex.length()<= i)
            return ERROR;
        if(nextProbel(lex, i) == END)
            return END;
        i = tek_i;
        if (lex.charAt(i) == '/') {
            if (i + 1 <= lex.length()) {
                if (lex.charAt(i + 1) == '/') {
                    while (lex.charAt(i) != '\n' || lex.charAt(i) == '\r') {
                        i++;

                        if (i + 1 == lex.length()) {
                            return END;
                        }
                    }

                    if(nextProbel(lex, i) == END)
                        return END;
                    i = tek_i;

                } else {
                    tek_i = i + 1;
                    return SLASH;
                }
            } else {
                tek_i = i + 1;
                return SLASH;
            }
        }
        if (HexLetter(lex.charAt(i))) {
            if (it_is_int(lex, i)) {
                return INT;
            }
            if (it_is_double(lex, i)) {
                return DOUBLE;
            }
            if (it_is_class(lex, i)) {
                return CLASS;
            }
            if (it_is_main(lex, i)) {
                return MAIN;
            }
            if (it_is_while(lex, i)) {
                return WHILE;
            }
            if(it_is_break(lex,i)){
                return BREAK;
            }
            if(it_is_return(lex,i)){
                return RETURN;
            }
            if(it_is_new(lex,i)){
                return NEW;
            }
            if (it_is_id(lex, i)) {
                return ID;
            }

        } else if (Num(lex.charAt(i))) {
             number ="";
            while (Num(lex.charAt(i))) {
                if (i + 1 == lex.length()) {
                    tek_i = i + 1;
                    return TYPE_IN;
                }
                number+=lex.charAt(i);
                i++;
            }
            if (lex.charAt(i) == '.') {
                number+=lex.charAt(i);
                i++;
                if (it_is_double_ex(lex, i)) {
                    return TYPE_DOUBL;
                } else {
                    tek_i = lex.length();
                    return ERROR;
                }
            } else if (HexLetter(lex.charAt(i)) == false) {
                tek_i = i;
                return TYPE_IN;
            } else if (HexLetter(lex.charAt(i)) == true) {
                tek_i = lex.length();
                return ERROR;
            }
        } else if (lex.charAt(i) == '-') {
            tek_i = i + 1;
            return MINUS;
        } else if (lex.charAt(i) == '+') {
            tek_i = i + 1;
            return PLUS;
        }
        if (lex.charAt(i) == '*') {
            tek_i = i + 1;
            return STAR;
        } else if (lex.charAt(i) == '%') {
            tek_i = i + 1;
            return PERCENT;
        } else if (lex.charAt(i) == '.') {
            tek_i = i + 1;
            return DOT;
        } else if (lex.charAt(i) == ';') {
            tek_i = i + 1;
            return COMMA;
        } else if (lex.charAt(i) == ',') {
            tek_i = i + 1;
            return VIRGULE;
        } else if (lex.charAt(i) == '(') {
            tek_i = i + 1;
            return ROUND_BRACE_OPEN;
        } else if (lex.charAt(i) == ')') {
            tek_i = i + 1;
            return ROUND_BRACE_CLOSE;
        } else if (lex.charAt(i) == '{') {
            tek_i = i + 1;
            return CURLY_BRACE_OPEN;
        } else if (lex.charAt(i) == '}') {
            tek_i = i + 1;
            return CURLY_BRACE_CLOSE;
        } else if (lex.charAt(i) == '=') {
            if (i + 1 < lex.length()) {
                if (lex.charAt(i + 1) == '=') {
                    tek_i = i + 2;
                    return EQUAL;
                }
            }
            tek_i = i + 1;
            return ASSIGN;
        } else if (lex.charAt(i) == '>') {
            if (i + 1 < lex.length()) {
                if (lex.charAt(i + 1) == '=') {
                    tek_i = i + 2;
                    return MORE_EQUAL;
                }
            }
            tek_i = i + 1;
            return MORE;
        } else if (lex.charAt(i) == '<') {
            if (i + 1 < lex.length()) {
                if (lex.charAt(i + 1) == '=') {
                    tek_i = i + 2;
                    return LESS_EQUAL;
                }
            }
            tek_i = i + 1;
            return LESS;
        } else if (lex.charAt(i) == '!') {
            if (i + 1 < lex.length()) {
                if (lex.charAt(i + 1) == '=') {
                    tek_i = i + 2;
                    return NOT_EQUAL;
                }
            }
            return ERROR;
        }else{
            tek_i = lex.length();
            return ERROR;
        }
    }
}