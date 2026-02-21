package com.contact.model;

import java.time.LocalDate;

public class Person {
    private Integer idperson;
    private String lastname;
    private String firstname;
    private String nickname;
    private String phoneNumber;
    private String address;
    private String emailAddress;
    private LocalDate birthDate;

    public Person() {}

    public Person(String lastname, String firstname, String nickname) {
        this.lastname = lastname;
        this.firstname = firstname;
        this.nickname = nickname;
    }

    // ALL GETTERS AND SETTERS
    public Integer getIdperson() { return idperson; }
    public void setIdperson(Integer id) { this.idperson = id; }
    
    public String getLastname() { return lastname; }
    public void setLastname(String lastname) { this.lastname = lastname; }
    
    public String getFirstname() { return firstname; }
    public void setFirstname(String firstname) { this.firstname = firstname; }
    
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phone) { this.phoneNumber = phone; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    public String getEmailAddress() { return emailAddress; }
    public void setEmailAddress(String email) { this.emailAddress = email; }
    
    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate date) { this.birthDate = date; }

    public String getFullName() { return firstname + " " + lastname; }

    @Override
    public String toString() {
        return "Person{id=" + idperson + ", name='" + getFullName() + "'}";
    }
}