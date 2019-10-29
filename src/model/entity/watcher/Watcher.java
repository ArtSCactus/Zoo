package model.entity.watcher;

import model.entity.animals.Animal;

import java.util.ArrayList;
import java.util.List;

public class Watcher {
    private String name;
    private List<Animal> watchingAnimals;

    public Watcher(String name) {
        this.name = name;
        this.watchingAnimals = new ArrayList<>();
    }

    public Watcher(String name, List<Animal> watchingAnimals) {
        this.name = name;
        this.watchingAnimals = watchingAnimals;
    }

    public String getName() {
        return name;
    }

    public List<Animal> getWatchingAnimals() {
        return watchingAnimals;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setWatchingAnimals(List<Animal> watchingAnimals) {
        this.watchingAnimals = watchingAnimals;
    }

    /**Adds animal to watchingAnimals list.
     *
     * Does not setting watcher to given animal.
     *
     * @param animal
     */
    public void addAnimal(Animal animal){
        watchingAnimals.add(animal);
    }
}
