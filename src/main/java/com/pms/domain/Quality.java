package com.pms.domain;

/**
 * Created by sandun on 8/15/2015.
 */


import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;

@Entity
@org.hibernate.annotations.Entity(selectBeforeUpdate = true)
@Table(name="Quality")
public class Quality {
    @Id
    private String qualityId;
    private String memberId;
    private String rate;

    public String getIdd() {
        return qualityId;
    }

    public void setIdd(String qid) {
        this.qualityId = qid;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String lasName) {
        this.memberId = lasName;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }
}
