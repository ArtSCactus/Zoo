package model.entity.habitat;

public enum Habitat {
    TRPOICS("Tropics",""), DESERT("Desert", "A very dry zone, with temperature more than 30 degrees"),
    FOREST("Forest",""), TUNDRA("Tundra", "A zone with not much snow and temperature that nearby 0"),
    ARCTIC("Arctic", "A zone with temperature below 0 and a big amount of snow.");

private String typeName;
private String description;

    Habitat(String typeName, String description) {
        this.typeName=typeName;
        this.description=description;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
