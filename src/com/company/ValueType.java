package com.company;


class ValueType<T>
{
    private T value;

    public T getValue() {
       return this.value;
    }

    public String toString() {
        return "{" + value + "}";
    }

    public void setValue(T value) {
        this.value = value;
    }




}