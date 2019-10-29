package model.entity.ration;

public class Ration {
    private int number;
    private String name;
    private RationType type;

    public Ration(int number, String name, RationType type) {
        this.number = number;
        this.name = name;
        this.type = type;
    }

    public int getNumber() {
        return number;
    }

    public String getName() {
        return name;
    }

    public RationType getType() {
        return type;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(RationType type) {
        this.type = type;
    }
}
