package com.pms.dao;

import com.pms.domain.User;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import java.util.List;

/**
 * Created by Upulie on 6/7/2015.
 */
public class UserDAO {

    private SessionFactory sessionFactory;



    public void updateUser(User user)
    {
        Session session = getSessionFactory().openSession();
        session.beginTransaction();
        session.update(user);
        session.getTransaction().commit();
        session.close();
    }
    public void setUser(User user)
    {
        Session session = getSessionFactory().openSession();
        session.beginTransaction();
        session.save(user);
        session.getTransaction().commit();
        session.close();
    }

    public User loadUserProjects(User user)
    {
        User user1;
        Session session = getSessionFactory().openSession();
        session.beginTransaction();
        user1=(User)session.get(User.class,user.getUserName());
        int x=user1.getProjects().size();
        session.getTransaction().commit();
        session.close();

        return user1;
    }

    public User loadUserDetails(String username)
    {
        User user1;
        Session session = getSessionFactory().openSession();
        session.beginTransaction();
        user1=(User)session.get(User.class,username);
        session.getTransaction().commit();
        session.close();

        return user1;
    }

    public List<User> loadMembers()
    {
        Session session = getSessionFactory().openSession();
        session.beginTransaction();
        List<User> list=(List<User>)session.createCriteria(User.class).list();;
        session.getTransaction().commit();
        session.close();
        return list;
    }
    public List<User> loadSelectedMembers(String mType)
    {
        Session session = getSessionFactory().openSession();
        String HQL_QUERY = "from User as user  where user.technicalSkills='" + mType + "'";
        session.beginTransaction();
        Query query = session.createQuery(HQL_QUERY);
        List<User> list = ((org.hibernate.Query) query).list();
        session.getTransaction().commit();
        session.close();
        return list;
    }
















    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
}
