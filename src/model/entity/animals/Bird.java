package model.entity.animals;


import model.entity.habitat.Habitat;
import model.entity.ration.Ration;
import model.entity.watcher.Watcher;
import model.entity.wintering.Wintering;

import java.util.Date;
import java.util.Objects;

public class Bird extends Animal {
private Wintering wintering;
private Ration ration;
private Habitat habitat;

    public Bird(int number, String name, Date birthdayDate, String sex, Wintering wintering, Ration ration, Habitat habitat, Watcher watcher) {
        super(number, name, birthdayDate, sex, watcher);
        this.wintering = wintering;
        this.ration = ration;
        this.habitat=habitat;
    }
    public Bird(int number, String name, Date birthdayDate, String sex) {
        super(number, name, birthdayDate, sex);
        this.wintering = wintering;
    }

    public Wintering getWintering() {
        return wintering;
    }

    public void setWintering(Wintering wintering) {
        this.wintering = wintering;
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
        if (!(o instanceof Bird)) return false;
        Bird bird = (Bird) o;
        return getWintering().equals(bird.getWintering()) &&
                getRation().equals(bird.getRation()) &&
                getHabitat() == bird.getHabitat();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getWintering(), getRation(), getHabitat());
    }
}
