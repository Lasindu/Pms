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
    private String userName;
    private int reopenDefects;// dev
    private int foundDefects;
    private int userReview;
    private int uatDefects; //qa
    private int reportedBugs;
    private int testSuits;
    private int writtenTestSuits;
    private int learningCapacity;//common
    private int dedicationToWork;
    private int rate;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getReopenDefects() {
        return reopenDefects;
    }

    public void setReopenDefects(int reopenDefects) {
        this.reopenDefects = reopenDefects;
    }

    public int getFoundDefects() {
        return foundDefects;
    }

    public void setFoundDefects(int foundDefects) {
        this.foundDefects = foundDefects;
    }

    public int getUserReview() {
        return userReview;
    }

    public void setUserReview(int userReview) {
        this.userReview = userReview;
    }

    public int getUatDefects() {
        return uatDefects;
    }

    public void setUatDefects(int uatDefects) {
        this.uatDefects = uatDefects;
    }

    public int getReportedBugs() {
        return reportedBugs;
    }

    public void setReportedBugs(int reportedBugs) {
        this.reportedBugs = reportedBugs;
    }

    public int getTestSuits() {
        return testSuits;
    }

    public void setTestSuits(int testSuits) {
        this.testSuits = testSuits;
    }

    public int getWrittenTestSuits() {
        return writtenTestSuits;
    }

    public void setWrittenTestSuits(int writtenTestSuits) {
        this.writtenTestSuits = writtenTestSuits;
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
