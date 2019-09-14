package com.example.applicationingsw.model;

public class Customer {
    private String name;
    private int  id;
    private String surname;
    private String address;
    private String email;
    private String gender;
    private String city;

    public Customer(int id, String name, String surname, String address, String email, String gender,
                    String city) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.address = address;
        this.email = email;
        this.gender = gender;
        this.city = city;
    }

    public Customer( String name, String surname, String address, String email, String gender,
                    String city) {
        this.name = name;
        this.surname = surname;
        this.address = address;
        this.email = email;
        this.gender = gender;
        this.city = city;
    }


    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = (id);
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = (name);
    }
    public String getSurname() {
        return surname;
    }
    public void setSurname(String surname) {
        this.surname = (surname);
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = (address);
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = (email);
    }

    public String getGender() {
        return gender;
    }
    public void setGender(String gender) {
        this.gender = (gender);
    }
    public String getCity() {
        return city
                ;
    }
    public void setCity(String city) {
        this.city = (city);
    }
    @Override
    public String toString(){
        return getId()+getName()+getSurname();
    }
}
