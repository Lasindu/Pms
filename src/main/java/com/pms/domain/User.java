package com.pms.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

@Entity
@org.hibernate.annotations.Entity(selectBeforeUpdate = true)
@Table(name="User")
public final class User {

    @Id
    private String userName;
    private String firstName;
    private String lastName;
    private Date dateOfBirth;
    private String email;
    private String password;
    private String contact;
    private String gender;
    private String role;
    private String experience;
    private String technicalSkills;
    private String domainSkills;
    private String isAvailable;
    private String assignedProjectName;

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


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getTechnicalSkills() {
        return technicalSkills;
    }

    public void setTechnicalSkills(String technicalSkills) {
        this.technicalSkills = technicalSkills;
    }

    public String getDomainSkills() {
        return domainSkills;
    }

    public void setDomainSkills(String domainSkills) {
        this.domainSkills = domainSkills;
    }

    public String getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(String isAvailable) {
        this.isAvailable = isAvailable;
    }

    public String getAssignedProjectName() {
        return assignedProjectName;
    }

    public void setAssignedProjectName(String assignedProjectName) {
        this.assignedProjectName = assignedProjectName;
    }
}
