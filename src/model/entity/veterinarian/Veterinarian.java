package model.entity.veterinarian;

import model.entity.animals.Animal;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Veterinarian {
    private int privateNumber;
    private String name;
    private Date birthday;
    private int phoneNumber;
    private boolean familyStatus;
    private List<Animal> pinnedAnimals;

    public Veterinarian(int privateNumber, String name, Date birthday, int phoneNumber, boolean familyStatus, List<Animal> pinnedAnimals) {
        this.privateNumber = privateNumber;
        this.name = name;
        this.birthday = birthday;
        this.phoneNumber = phoneNumber;
        this.familyStatus = familyStatus;
        this.pinnedAnimals = pinnedAnimals;
    }

    /**Pinned animals will be initialized with empty ArrayList.
     *
     * @param privateNumber - employee
     * @param name - employee's name
     * @param birthday - birthday date
     * @param phoneNumber - phone number
     * @param familyStatus - married or not
     */
    public Veterinarian(int privateNumber, String name, Date birthday, int phoneNumber, boolean familyStatus){
        this.privateNumber = privateNumber;
        this.name = name;
        this.birthday = birthday;
        this.phoneNumber = phoneNumber;
        this.familyStatus = familyStatus;
        pinnedAnimals = new ArrayList<>();
    }

    public int getPrivateNumber() {
        return privateNumber;
    }

    public String getName() {
        return name;
    }

    public Date getBirthday() {
        return birthday;
    }

    public int getPhoneNumber() {
        return phoneNumber;
    }

    public boolean isFamilyStatus() {
        return familyStatus;
    }

    public List<Animal> getPinnedAnimals() {
        return pinnedAnimals;
    }

    public void setPrivateNumber(int privateNumber) {
        this.privateNumber = privateNumber;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public void setPhoneNumber(int phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setFamilyStatus(boolean familyStatus) {
        this.familyStatus = familyStatus;
    }

    public void setPinnedAnimals(List<Animal> pinnedAnimals) {
        this.pinnedAnimals = pinnedAnimals;
    }
}
