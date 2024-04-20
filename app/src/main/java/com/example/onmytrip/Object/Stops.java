package com.example.onmytrip.Object;

public class Stops {

    private String name;
    private int number;

    public void setName(String name){
        this.name = name;
    }

    public void setNumber(int number){
        this.number = number;
    }

    public String getName(){
        return name;
    }

    public int getNumber(){
        return number;
    }

    public String toString(){

        return "Stop Name : " + name + " " + "Number : " + number;
    }

}
