package com.pms.dao;

import com.pms.component.member.taskganntchart.TaskTime;
import com.pms.domain.Project;
import com.pms.domain.Task;
import com.pms.domain.UserStory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.*;

/**
 * Created by Upulie on 6/2/2015.
 */
public class TaskDAO {

    private SessionFactory sessionFactory;

    public Task getTaskByTaskId(int id)
    {
        Session session = getSessionFactory().openSession();
        String HQL_QUERY = "from Task as task  where task.id=" + id  ;
        Query query = session.createQuery(HQL_QUERY);
        List<Task> list = ((org.hibernate.Query) query).list();
        session.close();
        if(list.size()>0)
        {
            return list.get(0);
        }
        return null;

    }
    public Task getTaskEstimateTimeByTaskName(String tName)
    {
        Session session = getSessionFactory().openSession();
        String HQL_QUERY = "from Task as task  where task.name='"+tName+"'";
        Query query = session.createQuery(HQL_QUERY);
        List<Task> list = ((org.hibernate.Query) query).list();
        session.close();
        if(list.size()>0)
        {
            return list.get(0);
        }
        return null;

    }

    public void removeTask(Task task)
    {
        Session session = getSessionFactory().openSession();
        session.beginTransaction();
        session.delete(task);
        session.getTransaction().commit();
        session.close();

    }
    public List<Task> getMyTaskList(String mName)
    {
        Session session = getSessionFactory().openSession();
        String HQL_QUERY = "from Task as task  where task.assignedTo='"+mName+"'";
        Query query = session.createQuery(HQL_QUERY);
        List<Task> list = ((org.hibernate.Query) query).list();
        session.close();
        if(list.size()>0)
        {
            return list;
        }
        return null;

    }
    public List<Task> getAllTasks()
    {
        String st = "done";
        Session session = getSessionFactory().openSession();
        String HQL_QUERY = "from Task as task where task.state != '"+st+"' order by task.id desc";
        Query query = session.createQuery(HQL_QUERY);
        List<Task> list = ((org.hibernate.Query) query).list();
        session.close();
        return list;
    }
    public List<Task> getAllTasksForUSerStory(UserStory us)
    {
        Session session = getSessionFactory().openSession();
        String HQL_QUERY = "from Task as task where task.userStory = '"+us.getUserStoryId()+"'";
        Query query = session.createQuery(HQL_QUERY);
        List<Task> list = ((org.hibernate.Query) query).list();
        session.close();
        return list;
    }
    public List<Task> getAllAssignedTasks()
    {
        String st="done";
        Session session = getSessionFactory().openSession();
        String HQL_QUERY = "from Task where state = '"+st+"'";
        Query query = session.createQuery(HQL_QUERY);
        List<Task> list = ((org.hibernate.Query) query).list();
        session.close();
        return list;
    }
    public boolean getAvailableUser(String userName){
        String st = "done";
        Session session = getSessionFactory().openSession();
        String HQL_QUERY = "from Task as task  where task.assignedTo='"+userName+"' and task.state != '"+st+"'";
        Query query = session.createQuery(HQL_QUERY);
        List<Task> list = ((org.hibernate.Query) query).list();
        session.close();
        if(list.size()>0)
        {
            return true;
        }
        return false;
    }
    public List<TaskTime> getFreeUser(String userName){

        List<TaskTime> tTList = new ArrayList<TaskTime>();

        Session session = getSessionFactory().openSession();
        String HQL_QUERY = "from Task as task  where task.assignedTo='"+userName+"'";
        Query query = session.createQuery(HQL_QUERY);
        List<Task> list = ((org.hibernate.Query) query).list();
        session.close();
        int starTime = 0;
        if(list.size()>0)
        {
            for(int j=0;j<list.size();j++)
            {
                TaskTime tt = new TaskTime();
                int estiTime = Integer.parseInt(list.get(j).getEstimateTime());
                try{
                    starTime = Integer.parseInt(list.get(j).getStartTime());
                }catch (Exception e){

                }

                tt.setEndTime(starTime+estiTime);
                tt.setStartTime(starTime);
                tTList.add(tt);
            }

        }
        return tTList;
    }

    public List<Task> getMyTasks(String userName)
    {
        Session session = getSessionFactory().openSession();
        String HQL_QUERY = "from Task";// as tast  where tast.name='" + userName + "'";
        Query query = session.createQuery(HQL_QUERY);
        List<Task> list = ((org.hibernate.Query) query).list();
        session.close();
        return list;
    }
    public void updateTask(Task task)
    {
        Session session = getSessionFactory().openSession();
        session.beginTransaction();
        session.update(task);
        session.getTransaction().commit();
        session.close();
    }


    public Task getTaskFromUserStroyNameAndTaskName(String userStoryName,String taskName)
    {
        Session session = getSessionFactory().openSession();
        String HQL_QUERY = "from UserStory as userStory  where userStory.name='" + userStoryName + "'";
        Query query = session.createQuery(HQL_QUERY);
        List<UserStory> list = ((org.hibernate.Query) query).list();

        if(list.size()>0) {

            Collection<Task> userStoryTasks=list.get(0).getUserStoryTasks();

            for(Task task:userStoryTasks)
            {
                if (task.getName().equals(taskName))
                {
                    session.close();
                    return task;

                }

            }
            return null;
        }

        return null;
    }
















    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }


}
