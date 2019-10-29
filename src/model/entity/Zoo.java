package model.entity;

import model.entity.animals.Animal;
import model.entity.veterinarian.Veterinarian;
import model.entity.watcher.Watcher;

import java.util.ArrayList;
import java.util.List;

public class Zoo {
    private List<Animal> animals;
    private List<Watcher> watchers;
    private List<Veterinarian> veterinarians;

    /**A default initialization of animals list as ArrayList.
     *
     * @param watchers
     * @param veterinarians
     */
    public Zoo(List<Watcher> watchers, List<Veterinarian> veterinarians){
        animals = new ArrayList<>();
        this.watchers = watchers;
        this.veterinarians = veterinarians;
    }

    /**A default initialization of all lists as ArrayList.
     *
     */
    public Zoo(){
        animals = new ArrayList<>();
        watchers = new ArrayList<>();
        veterinarians = new ArrayList<>();
    }

    public Zoo(List<Animal> animals, List<Watcher> watchers, List<Veterinarian> veterinarians) {
        this.animals = animals;
        this.watchers = watchers;
        this.veterinarians = veterinarians;
    }

    /**Auto-generated getter.
     *
     * @return
     */
    public List<Animal> getAnimals() {
        return animals;
    }

    /**Auto-generated getter.
     *
     * @return
     */
    public List<Watcher> getWatchers() {
        return watchers;
    }

    /**Auto-generated getter.
     *
     * @return
     */
    public List<Veterinarian> getVeterinarians() {
        return veterinarians;
    }

    /**Auto-generated setter.
     *
     * @param animals
     */
    public void setAnimals(List<Animal> animals) {
        this.animals = animals;
    }

    /**Auto-generated setter.
     *
     * @param watchers
     */
    public void setWatchers(List<Watcher> watchers) {
        this.watchers = watchers;
    }

    /**Auto-generated setter.
     *
     * @param veterinarians
     */
    public void setVeterinarians(List<Veterinarian> veterinarians) {
        this.veterinarians = veterinarians;
    }

    /**Adds new animal.
     *
     * @param animal
     */
    public void addAnimal(Animal animal){
        animals.add(animal);
    }

    /**Adds new animal to the list and pinns watcher to this animal.
     *
     * @param animal - new animal
     * @param watcher - watcher that will be pinned to this animal.
     */
    public void addAnimal(Animal animal, Watcher watcher){
        animals.add(animal);
        animal.setWatcher(watcher);
    }

    public void addAnimal (Animal animal, Watcher watcher, Veterinarian veterinarian){

    }

}
