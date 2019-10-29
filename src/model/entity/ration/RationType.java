package model.entity.ration;

public enum RationType {
    CHILD("CHILD"), DIETARY("DIETARY"), EXTENDED("Extended");

    private String typeName;

    RationType(String typeName){
        this.typeName=typeName;
    }

    @Override
    public String toString() {
        return typeName;
    }
}
