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

    public void updateUser(Quality quality)
    {
        Session session = getSessionFactory().openSession();
        session.beginTransaction();
        session.update(quality);
        session.getTransaction().commit();
        session.close();
    }
    public int getUserListWithQualityMembers(String uName)
    {
        Session session = getSessionFactory().openSession();
        String HQL_QUERY = "from Quality as quality  where quality.userName='" + uName + "'";
        session.beginTransaction();
        Query query = session.createQuery(HQL_QUERY);
        List<Quality> list = ((org.hibernate.Query) query).list();
        session.close();
        if(list.size()>0)
        {
            return list.get(0).getRate();
        }
        return 0;
    }


    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
}
