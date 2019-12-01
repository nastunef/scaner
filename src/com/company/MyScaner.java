package com.company;

import static com.company.Constans.*;

public class MyScaner {

    public int tek_i;

    public MyScaner() {
        tek_i = 0;
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

    public boolean it_is_main(String lex, int i) {
        if (i + 3 <= lex.length() - 1) {
            if (lex.charAt(i) == 'M' && lex.charAt(i + 1) == 'a' && lex.charAt(i + 2) == 'i' && lex.charAt(i + 3) == 'n' &&
                    (((lex.length() - 1 >= i + 4) && (Num(lex.charAt(i + 4)) == false)) || (lex.length() == i + 4))) {
                tek_i = i + 4;
                return true;
            }
        }
        return false;
    }

    public boolean it_is_id(String lex, int i) {
        while (HexLetter(lex.charAt(i)) || lex.charAt(i) == '_' || Num(lex.charAt(i))) {
            if (i + 1 == lex.length()) {
                tek_i = i + 1;
                return true;
            }
            i++;
        }
        tek_i = i;
        return true;
    }

    public boolean it_is_double_ex(String lex, int i) {
        if (i <= lex.length()) {
            return false;
        }
        while (Num(lex.charAt(i))) {
            if (i + 1 == lex.length()) {
                tek_i = i + 1;
                return false;
            }
            i++;
        }
        if (lex.charAt(i) == 'e') {
            if (i + 1 == lex.length()) {
                tek_i = i + 1;
                return true;
            }
            i++;
            if (lex.charAt(i) == '-' || lex.charAt(i) == '+') {
                if (i + 1 == lex.length()) {
                    tek_i = i;
                    return true;
                }
                i++;
            }
            while (Num(lex.charAt(i))) {
                if (i + 1 == lex.length()) {
                    tek_i = i + 1;
                    return true;
                }
                i++;
            }
            if (HexLetter(lex.charAt(i))) {
                return false;
            } else {
                tek_i = i + 1;
                return true;
            }
        } else return false;
    }

    public String scanner(String lex, int i) {
        while (lex.charAt(i) == ' ' || lex.charAt(i) == '\n' || lex.charAt(i) == '\t' || lex.charAt(i) == '\r') {
            if (i + 1 == lex.length()) {
                tek_i = i + 1;
                return "END";
            }
            i++;
        }
        if (lex.charAt(i) == '/') {
            if (i + 1 <= lex.length()) {
                if (lex.charAt(i + 1) == '/') {
                    while (lex.charAt(i) != '\n' || lex.charAt(i) == '\r') {
                        i++;
                        if (i + 1 == lex.length()) {
                            return "END";
                        }
                    }
                } else {
                    tek_i = i + 1;
                    return "SLASH";
                }
            } else {
                tek_i = i + 1;
                return "SLASH";
            }
        }
        if (HexLetter(lex.charAt(i))) {
            if (it_is_int(lex, i)) {
                return "INT";
            }
            if (it_is_double(lex, i)) {
                return "DOUBLE";
            }
            if (it_is_class(lex, i)) {
                return "CLASS";
            }
            if (it_is_main(lex, i)) {
                return "MAIN";
            }
            if (it_is_while(lex, i)) {
                return "WHILE";
            }
            if (it_is_id(lex, i)) {
                return "ID";
            }
        } else if (Num(lex.charAt(i))) {
            while (Num(lex.charAt(i))) {
                if (i + 1 == lex.length()) {
                    tek_i = i + 1;
                    return "TYPE_IN";
                }
                i++;
            }
            if (lex.charAt(i) == '.') {
                i++;
                if (it_is_double_ex(lex, i)) {
                    return "TYPE_DOUBL";
                } else {
                    tek_i = lex.length();
                    return "ERROR";
                }
            } else if (HexLetter(lex.charAt(i)) == false) {
                tek_i = i;
                return "TYPE_IN";
            } else if (HexLetter(lex.charAt(i)) == true) {
                tek_i = lex.length();
                return "ERROR";
            }
        } else if (lex.charAt(i) == '-') {
            tek_i = i + 1;
            return "MINUS";
        } else if (lex.charAt(i) == '+') {
            tek_i = i + 1;
            return "PLUS";
        }
        if (lex.charAt(i) == '*') {
            tek_i = i + 1;
            return "STAR";
        } else if (lex.charAt(i) == '%') {
            tek_i = i + 1;
            return "PERCENT";
        } else if (lex.charAt(i) == '.') {
            tek_i = i + 1;
            return "DOT";
        } else if (lex.charAt(i) == ';') {
            tek_i = i + 1;
            return "COMMA";
        } else if (lex.charAt(i) == ',') {
            tek_i = i + 1;
            return "VIRGULE";
        } else if (lex.charAt(i) == '(') {
            tek_i = i + 1;
            return "ROUND_BRACE_OPEN";
        } else if (lex.charAt(i) == ')') {
            tek_i = i + 1;
            return "ROUND_BRACE_CLOSE";
        } else if (lex.charAt(i) == '{') {
            tek_i = i + 1;
            return "CURLY_BRACE_OPEN";
        } else if (lex.charAt(i) == '}') {
            tek_i = i + 1;
            return "CURLY_BRACE_CLOSE";
        } else if (lex.charAt(i) == '=') {
            if (i + 1 < lex.length()) {
                if (lex.charAt(i + 1) == '=') {
                    tek_i = i + 2;
                    return "EQUAL";
                }
            }
            tek_i = i + 1;
            return "ASSIGN";
        } else if (lex.charAt(i) == '>') {
            if (i + 1 < lex.length()) {
                if (lex.charAt(i + 1) == '=') {
                    tek_i = i + 2;
                    return "MORE_EQUAL";
                }
            }
            tek_i = i + 1;
            return "MORE";
        } else if (lex.charAt(i) == '<') {
            if (i + 1 < lex.length()) {
                if (lex.charAt(i + 1) == '=') {
                    tek_i = i + 2;
                    return "LESS_EQUAL";
                }
            }
            tek_i = i + 1;
            return "LESS";
        } else {
            tek_i = lex.length();
            return "ERROR";
        }
    }
}
