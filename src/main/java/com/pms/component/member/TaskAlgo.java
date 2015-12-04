package com.pms.component.member;

import com.pms.DashboardUI;
import com.pms.dao.TaskDAO;
import com.pms.domain.Task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by lasindu on 11/17/2015.
 */
public class TaskAlgo {
    public List<Task> getTaskSchedule(List<Task> linearTaskSchedule){//in the initial state members are available for all tasks

        TaskDAO taskDAO = (TaskDAO) DashboardUI.context.getBean("Task");
        ArrayList<Task> addedList = new ArrayList<Task>();
        ArrayList<Integer> ol = new ArrayList<Integer>();
        for(Task as : linearTaskSchedule){
            String [] dependancySize= as.getPreRequisits().split(",");
            if(as.getPreRequisits().equalsIgnoreCase("")){ //dependancySize.length==0
                //start node and update table as start time 0 th time
                as.setStartTime("0");
                //as.setAssignedTo(""); // methanin userwa assign karala danna
                as.setProcessed(true);
                taskDAO.updateTask(as);
                //add to addedlist
                addedList.add(as);

            }else{ //has dependencies >0
                String [] dependencyList= as.getPreRequisits().split(",");
                String heighestDepTask = "";
                int[] sortingArray = new int[dependencyList.length];
                int i=0;
                for(String a:dependencyList){
                    Task tObject = taskDAO.getTaskEstimateTimeByTaskName(a);
                    int starTime = 0;
                    try{
                        starTime = Integer.parseInt(tObject.getStartTime());
                    }catch (Exception e){

                    }
                    sortingArray[i]=(Integer.parseInt(tObject.getEstimateTime()) + starTime);
                    i++;
                }
                Arrays.sort(sortingArray);
                int y= sortingArray[dependencyList.length-1];

                //assign to the highest time dependency
                as.setStartTime(y+"");
                //as.setAssignedTo(""); // methanin userwa assign karala danna
                as.setProcessed(true);
                taskDAO.updateTask(as);
                //add to addedlist
                addedList.add(as);
            }
        }

        List<Task> taskRes = addedList;

        return taskRes;
    }
}
