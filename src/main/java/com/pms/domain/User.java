package com.pms.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;

@Entity
@org.hibernate.annotations.Entity(selectBeforeUpdate = true)
@Table(name="User")
public final class User {

    @Id
    private String firstName;
    private String lastName;
    private String userName;
    private String password;
    private String role;
    private boolean male;
    private String email;
    private String address;
    private String phone;
    private String skill;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "user_project", catalog = "pms", joinColumns = {
            @JoinColumn(name = "userName", nullable = false, updatable = false) },
            inverseJoinColumns = { @JoinColumn(name = "projectId",
                    nullable = false, updatable = false) })
    private Collection<Project> projects = new ArrayList<Project>();

    //getters and setters
    
    public Collection<Project> getProjects() {
        return projects;
    }
    public void setProjects(Collection<Project> projects) {
        this.projects = projects;
    }
    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public String getLocation() {
        return address;
    }

    public void setLocation(final String location) {
        this.address = location;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(final String phone) {
        this.phone = phone;
    }

    public boolean isMale() {
        return male;
    }

    public void setMale(final boolean male) {
        this.male = male;
    }

    public String getRole() {
        return role;
    }

    public void setRole(final String role) {
        this.role = role;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSkill() {
        return skill;
    }

    public void setSkill(String password) {
        this.password = skill;
    }
}
