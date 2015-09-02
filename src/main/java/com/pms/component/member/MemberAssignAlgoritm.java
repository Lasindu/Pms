package com.pms.component.member;

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


    public List<User> getMember(Task task){

        List<User> list = getAvailableAndRelatedMember(task.getMemberType());
        for(int i=0;i<list.size();i++){
            // implementing
        }

        return null;
    }

    public List<User> getAvailableAndRelatedMember(String memberType){

        return null;
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
