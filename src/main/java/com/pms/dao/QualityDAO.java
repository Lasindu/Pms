package com.pms.dao;

import com.pms.domain.Quality;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.List;

/**
 * Created by lasindu on 11/27/2015.
 */
public class QualityDAO {

    private SessionFactory sessionFactory;

    public void setQuality(Quality quality)
    {
        Session session = getSessionFactory().openSession();
        session.beginTransaction();
        session.save(quality);
        session.getTransaction().commit();
        session.close();
    }
    public void updateQuality(Quality quality)
    {
        Session session = getSessionFactory().openSession();
        session.beginTransaction();
        session.update(quality);
        session.getTransaction().commit();
        session.close();
    }

    public Quality loadUserQuality(String username)
    {
        Quality quality;
        Session session = getSessionFactory().openSession();
        session.beginTransaction();
        quality=(Quality)session.get(Quality.class,username);
        session.getTransaction().commit();
        session.close();

        return quality;
    }



    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
}
