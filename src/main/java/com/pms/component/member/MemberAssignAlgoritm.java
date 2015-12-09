package com.pms.component.member;

import com.pms.DashboardUI;
import com.pms.component.member.taskganntchart.TaskTime;
import com.pms.dao.TaskDAO;
import com.pms.dao.UserDAO;
import com.pms.domain.Task;
import com.pms.domain.User;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lasindu on 8/15/2015.
 */
public class MemberAssignAlgoritm {

    private SessionFactory sessionFactory;

    public List<User> getAvailableAndRelatedMember(Task task,String proName, UserDAO uDao, TaskDAO taskDAO) {
        int estimateTime,startTime = 0;
        List<User> userRes = uDao.loadSelectedMembers(task.getTechnicalSkill(),proName,task.getDomainSkill());

        List<User> filteredRes = new ArrayList<User>();
        User user = new User();
        boolean isFree=false, isAssign = false ;
        int startTm, endTm;
        List<TaskTime> tList = new ArrayList<TaskTime>();

        if(task.getStartTime() != null){
            startTime = Integer.parseInt(task.getStartTime());
        }

        estimateTime = Integer.parseInt(task.getEstimateTime());


        for(int i=0;i<userRes.size();i++){
            user = userRes.get(i);
            isAssign = taskDAO.getAvailableUser(user.getUserName());
            if(isAssign){
                tList = taskDAO.getFreeUser(user.getUserName()); //check start time and estimate time for assigned situations

                for(int j=0;j<tList.size();j++){
                    int st = startTime;
                    int et = st + estimateTime;

                    int listst = 0,listet = 0;
                    try{
                        listst = tList.get(j).getStartTime();
                    }catch(Exception e){

                    }
                    listet = tList.get(j).getEndTime();

                    if(listst >= et){
                        //free
                        isFree = true;
                    }else{
                        if(st >= listet){
                            //free
                            isFree = true;
                        }else{
                            //bussy
                            isFree = false;
                            break;
                        }
                    }
                }
                if(isFree){
                    filteredRes.add(userRes.get(i));
                }
            }else{
                filteredRes.add(userRes.get(i));
            }
        }
        return filteredRes;
    }

}
