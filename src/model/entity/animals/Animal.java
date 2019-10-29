package model.entity.animals;

import model.entity.watcher.Watcher;

import java.util.Date;

public abstract class Animal {
    /**Animal's unique number.
     *
     */
    private int number;
    /** Animal's name.
     *
     */
    private String name;
    /**Animal's birthday date.
     *
     */
    private Date birthdayDate;
    /** Animal's sex.
     * true - male, false - female.
     */
    private String sex;
    /**Current watcher fo this animal.
     *
     */
    private Watcher watcher;


    public Animal(int number, String name, Date birthdayDate, String sex, Watcher watcher) {
        this.number = number;
        this.name = name;
        this.birthdayDate = birthdayDate;
        this.sex = sex;
        this.watcher = watcher;
    }
    public Animal(int number, String name, Date birthdayDate, String sex) {
        this.number = number;
        this.name = name;
        this.birthdayDate = birthdayDate;
        this.sex = sex;
    }

    public int getNumber() {
        return number;
    }

    public String getName() {
        return name;
    }

    public Date getBirthdayDate() {
        return birthdayDate;
    }

    public String getSex() {
        return sex;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBirthdayDate(Date birthdayDate) {
        this.birthdayDate = birthdayDate;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public Watcher getWatcher() {
        return watcher;
    }

    /**Setting new watcher to this animal.
     *
     * While setting watcher to this object adds this
     * animal to the watchingAnimals list in Watcher class.
     * @see Watcher
     * @param watcher
     */
    public void setWatcher(Watcher watcher) {
        this.watcher = watcher;
        watcher.addAnimal(this);
    }

    public abstract int hashCode();
}
