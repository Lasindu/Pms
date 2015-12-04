package com.pms.domain;

import javax.persistence.*;

/**
 * Created by lasindu on 11/17/2015.
 */
@Entity
@org.hibernate.annotations.Entity(selectBeforeUpdate = true)
@Table(name="TechnicalSkill")
public class TechnicalSkill {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private int techId;
    private String techName;

    public String getTechName() {
        return techName;
    }

    public void setTechName(String techName) {
        this.techName = techName;
    }

    public int getTechId() {
        return techId;
    }

    public void setTechId(int techId) {
        this.techId = techId;
    }
}
