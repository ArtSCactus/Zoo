package model.entity.animals;

import model.entity.habitat.Habitat;
import model.entity.ration.Ration;
import model.entity.reptilecondition.ReptileCondition;
import model.entity.watcher.Watcher;

import java.util.Date;
import java.util.Objects;

public class Reptile extends Animal {
    private ReptileCondition conditions;
    private Ration ration;
    private Habitat habitat;

    public Reptile(int number, String name, Date birthdayDate, String sex, ReptileCondition conditions, Habitat habitat, Watcher watcher) {
        super(number, name, birthdayDate, sex, watcher);
        this.conditions = conditions;
        this.habitat = habitat;
    }

    public ReptileCondition getConditions() {
        return conditions;
    }

    public void setConditions(ReptileCondition conditions) {
        this.conditions = conditions;
    }

    public Ration getRation() {
        return ration;
    }

    public void setRation(Ration ration) {
        this.ration = ration;
    }

    public Habitat getHabitat() {
        return habitat;
    }

    public void setHabitat(Habitat habitat) {
        this.habitat = habitat;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Reptile)) return false;
        Reptile reptile = (Reptile) o;
        return getConditions().equals(reptile.getConditions()) &&
                getRation().equals(reptile.getRation()) &&
                getHabitat() == reptile.getHabitat();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getConditions(), getRation(), getHabitat());
    }
}
