package util;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Person{

    private SimpleStringProperty name;
    private SimpleIntegerProperty age;

    Person(String name, int age){
        this.name = new SimpleStringProperty(name);
        this.age = new SimpleIntegerProperty(age);
    }

    public String getName(){
        return name.get();
    }
    public void setName(String value){
        name.set(value);
    }

    public int getAge(){
        return age.get();
    }

    public void setAge(int value){
        age.set(value);
    }
}