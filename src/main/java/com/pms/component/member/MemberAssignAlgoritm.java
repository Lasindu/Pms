package com.pms.component.member;

import com.pms.DashboardUI;
import com.pms.dao.UserDAO;
import com.pms.domain.Task;
import com.pms.domain.User;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

/**
 * Created by sandun on 9/1/2015.
 */
public class MemberAssignAlgoritm {

    private SessionFactory sessionFactory;

    public List<User> getAvailableAndRelatedMember(String memberType,UserDAO uDao) {

        List<User> userRes = uDao.loadSelectedMembers(memberType);
        
        return userRes;
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
