package com.pms.domain;

import javax.persistence.*;
import java.util.Comparator;

/**
 * Created by Upulie on 4/14/2015.
 */

@Entity
@org.hibernate.annotations.Entity(selectBeforeUpdate = true)
@Table(name="Task")
public class Task {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private int taskId;
    private String name;
    private String description;
    private String date;
    private int priority;
    private int severity;
    private String preRequisits;
    private String dependancy;
    private String memberType;
    private String domainSkill;
    private String technicalSkill;
    private String estimateTime;
    private String assignedTo;
    private String completeTime;
    private boolean isCr;
    private int isComplete;
	private String state;
	private String startTime;
    private String isAssigned;
    private boolean isProcessed = false;
    private int rowId;

    @ManyToOne()
    @JoinColumn(name="userStoryId")
    private UserStory userStory;

    //getters and setters

    public String getPreRequisits() {
        return preRequisits;
    }

    public void setPreRequisits(String preRequisits) {
        this.preRequisits = preRequisits;
    }

    public String getDependancy() {
        return dependancy;
    }

    public void setDependancy(String dependancy) {
        this.dependancy = dependancy;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public String getDomainSkill() {
        return domainSkill;
    }

    public void setDomainSkill(String domainSkill) {
        this.domainSkill = domainSkill;
    }

    public String getTechnicalSkill() {
        return technicalSkill;
    }

    public void setTechnicalSkill(String technicalSkill) {
        this.technicalSkill = technicalSkill;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getSeverity() {
        return severity;
    }

    public void setSeverity(int severity) {
        this.severity = severity;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getEstimateTime() {
        return estimateTime;
    }

    public void setEstimateTime(String estimateTime) {
        this.estimateTime = estimateTime;
    }

    public String getMemberType() {
        return memberType;
    }

    public void setMemberType(String memberType) {
        this.memberType = memberType;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }

    public String getCompleteTime() {
        return completeTime;
    }

    public void setCompleteTime(String completeTime) {
        this.completeTime = completeTime;
    }

    public boolean isCr() {
        return isCr;
    }

    public void setCr(boolean isCr) {
        this.isCr = isCr;
    }

    public int getIsComplete() {
        return isComplete;
    }

    public void setIsComplete(int isComplete) {
        this.isComplete = isComplete;
    }

    public String getIsAssigned() {
        return isAssigned;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setIsAssigned(String isAssigned) {
        this.isAssigned = isAssigned;
    }

    public UserStory getUserStory() {
        return userStory;
    }

    public void setUserStory(UserStory userStory) {
        this.userStory = userStory;
    }	
	public String getState() {
        return state;
    }
    public void setState(String state) {
        this.state = state;
    }

    public boolean isProcessed() {
        return isProcessed;
    }

    public void setProcessed(boolean isProcessed) {
        this.isProcessed = isProcessed;
    }

    public int getRowId() {
        return rowId;
    }

    public void setRowId(int rowId) {
        this.rowId = rowId;
    }
}
