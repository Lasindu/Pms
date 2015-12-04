package com.pms.domain;

import javax.persistence.*;

/**
 * Created by lasindu on 11/17/2015.
 */

@Entity
@org.hibernate.annotations.Entity(selectBeforeUpdate = true)
@Table(name="DomainSkill")
public class DomainSkill {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private int domId;
    private String domName;

    public int getDomId() {
        return domId;
    }

    public void setDomId(int domId) {
        this.domId = domId;
    }

    public String getDomName() {
        return domName;
    }

    public void setDomName(String domName) {
        this.domName = domName;
    }
}
