package com.pms.domain;

/**
 * Created by lasindu on 8/15/2015.
 */


import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;

@Entity
@org.hibernate.annotations.Entity(selectBeforeUpdate = true)
@Table(name="Quality")
public class Quality {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private int qId;
    private String userName;
    private int timeCompletion;
    private int bugsReopen;
    private int bugsAfterRelease;
    private int timelyReview;
    private int learningCapacity;
    private int dedicationToWork;
    private int rate;

    public int getqId() {
        return qId;
    }

    public void setqId(int qId) {
        this.qId = qId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String un) {
        this.userName = un;
    }

    public int getBugsReopen() {
        return bugsReopen;
    }

    public void setBugsReopen(int bugsReopen) {
        this.bugsReopen = bugsReopen;
    }

    public int getTimeCompletion() {
        return timeCompletion;
    }

    public void setTimeCompletion(int timeCompletion) {
        this.timeCompletion = timeCompletion;
    }

    public int getBugsAfterRelease() {
        return bugsAfterRelease;
    }

    public void setBugsAfterRelease(int bugsAfterRelease) {
        this.bugsAfterRelease = bugsAfterRelease;
    }

    public int getTimelyReview() {
        return timelyReview;
    }

    public void setTimelyReview(int timelyReview) {
        this.timelyReview = timelyReview;
    }

    public int getLearningCapacity() {
        return learningCapacity;
    }

    public void setLearningCapacity(int learningCapacity) {
        this.learningCapacity = learningCapacity;
    }

    public int getDedicationToWork() {
        return dedicationToWork;
    }

    public void setDedicationToWork(int dedicationToWork) {
        this.dedicationToWork = dedicationToWork;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }
}
