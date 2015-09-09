package com.pms.component.member;

import com.pms.dao.UserDAO;
import com.pms.domain.Task;
import com.pms.domain.User;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import java.util.List;

/**
 * Created by sandun on 9/1/2015.
 */
public class MemberAssignAlgoritm {

    private SessionFactory sessionFactory;


    public List<User> getMember(){

        List<User> userList = getAvailableAndRelatedMember();


        return userList;
    }

    public List<User> getAvailableAndRelatedMember(){

        UserDAO userObj=new UserDAO();
        List<User> userList = userObj.loadMembers();
        return userList;
    }
    public List<User> checkFutureTask(){

        return null;
    }
    public List<User> getgMember(){

        return null;
    }
    public List<User> getBestRatedMember(){

        return null;
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
}
