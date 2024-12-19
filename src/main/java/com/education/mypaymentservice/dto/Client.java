package com.education.mypaymentservice.dto;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "clients")
public class Client {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(length = 50, nullable = false)
    private String name;

    @Column(length = 50, nullable = false)
    private String surname;

    @Column(length = 50)
    private String midname;

    @Column()
    private boolean blocked;

    @Column(columnDefinition = "TEXT", nullable = false, unique = true)
    private String phone;

    public Client(String name, String surname, String midname, boolean blocked, String phone) {
        this.name = name;
        this.surname = surname;
        this.midname = midname;
        this.blocked = blocked;
        this.phone = phone;
    }

    public Client(UUID id, String name, String surname, String midname, boolean blocked, String phone) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.midname = midname;
        this.blocked = blocked;
        this.phone = phone;
    }

    public Client() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getMidname() {
        return midname;
    }

    public void setMidname(String midname) {
        this.midname = midname;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}


