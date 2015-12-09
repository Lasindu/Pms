package com.pms.component.CR;

import com.pms.DashboardUI;
import com.pms.component.member.MemberAssignAlgoritm;
import com.pms.dao.TaskDAO;
import com.pms.dao.UserDAO;
import com.pms.dao.UserStoryDAO;
import com.pms.domain.Project;
import com.pms.domain.Task;
import com.pms.domain.User;
import com.pms.domain.UserStory;
import com.vaadin.ui.CustomComponent;

import java.util.*;

/**
 * Created by Amali on 5/11/2015.
 */
public class CRProcessing extends CustomComponent {

    List<User> membersMatchedWithCurProject;
    List<User> datesMatchedWithCurProject;
    List<User> skillsMatchedWithCurProject;
    HashMap<Integer, Integer> taskWithEndTime;
    ArrayList<Task> pendingTaskList;
    ArrayList<Task> completedAndWorkingTaskList;
    List<Task> completedCRDependencies;
    List<Task> pendingCRDependencies;
    List<Task> completedCRPrerequisites;
    List<Task> pendingCRPrerequisites;
    ArrayList<Task> allTaskCollection;
    private int oldSprintEndTime = 0;
    private Task newTask;
    private UserStory selectedUserStory;
    private int countDependencies, countPrerequisites;

    public CRProcessing() {

        taskWithEndTime = new HashMap<Integer, Integer>();
        pendingTaskList = new ArrayList<Task>();
        completedAndWorkingTaskList = new ArrayList<Task>();
        completedCRDependencies = new ArrayList<Task>();
        pendingCRDependencies = new ArrayList<Task>();
        completedCRPrerequisites = new ArrayList<Task>();
        pendingCRPrerequisites = new ArrayList<Task>();
        allTaskCollection = new ArrayList<Task>();

    }

    public void processCR(Project project, Task task) {

        selectedUserStory = task.getUserStory();
        newTask = task;

        UserStoryDAO userStoryDAO = (UserStoryDAO) DashboardUI.context.getBean("UserStory");

        //get task list without CR
        Collection<Task> tasksSet = userStoryDAO.getUserStoryTaskListWithoutCRTasks(selectedUserStory);
        allTaskCollection.addAll(tasksSet);

        for (Task task1 : allTaskCollection) {
            //taskMap.put(task1.getTaskId(), task1);
            //get tasks with their end time
            int taskEndTime = Integer.parseInt(task1.getStartTime()) + Integer.parseInt(task1.getEstimateTime());

            if (oldSprintEndTime <= taskEndTime) oldSprintEndTime = taskEndTime;
            taskWithEndTime.put(task1.getTaskId(), taskEndTime);

            if (task1.getState().toLowerCase().equalsIgnoreCase("initial"))
                pendingTaskList.add(task1);
            else
                completedAndWorkingTaskList.add(task1);
        }

        switch (checkStateOfPrerequisitesAndDependenciesInCR(pendingTaskList)) {

            case 1:
                System.out.println("All the dependencies or prerequisites are in the pending task list");
                updateCRDependentTaskPath(newTask);
                break;
            case 2:
                System.out.println("All the dependencies or prerequisites are in the completed task list");
                break;
            case 3:
                System.out.println("All the dependencies in pending task list and all prerequisites are in the completed task list");
                break;
            case 4:
                System.out.println("No dependencies and some prerequisites in pending task list and some prerequisites are in the completed task list");
                break;
            case 5:
                System.out.println("No prerequisites and some dependencies in pending task list and some dependencies are in the completed task list");
                break;
            case 6:
                System.out.println("All the prerequisites and some dependencies are in the completed list and some dependencies in pending task list");
                break;
            case 7:
                System.out.println("Some prerequisites are in the completed list and some prerequisites and all the dependencies in pending task list");
                break;
            case 8:
                System.out.println("No prerequisites and dependencies for the CR task");
                break;
        }

    }

    public int checkStateOfPrerequisitesAndDependenciesInCR(ArrayList<Task> tasksToBeCompleted) {

        countDependencies = 0;
        countPrerequisites = 0;

        int depSize = 0, preSize = 0;
        boolean isDepComplete = false;
        boolean isPreComplete = false;

        TaskDAO taskDAO = (TaskDAO) DashboardUI.context.getBean("Task");

        if (newTask.getDependancy() != null && !newTask.getDependancy().isEmpty()) {
            String[] crDependencies = newTask.getDependancy().split(",");
            depSize = crDependencies.length;

            for (String dependency : crDependencies) {
                for (Task task : tasksToBeCompleted) {
                    if (task.getName().equalsIgnoreCase(dependency)) {
                        countDependencies++;
                        isDepComplete = true;
                        pendingCRDependencies.add(task);
                    }
                }

                if (!isDepComplete) {
                    completedCRDependencies.add(taskDAO.getTaskFromUserStroyNameAndTaskName
                            (selectedUserStory.getName().toString(), dependency));
                }
            }
        } else
            countDependencies = -1;  //no dependencies

        if (newTask.getPreRequisits() != null && !newTask.getPreRequisits().isEmpty()) {
            String[] crPrerequisites = newTask.getPreRequisits().split(",");
            preSize = crPrerequisites.length;

            for (String prerequisite : crPrerequisites) {
                for (Task task : tasksToBeCompleted) {
                    if (task.getName().equalsIgnoreCase(prerequisite)) {
                        countPrerequisites++;
                        isPreComplete = true;
                        pendingCRPrerequisites.add(task);
                    }
                }

                if (!isPreComplete) {
                    completedCRPrerequisites.add(taskDAO.getTaskFromUserStroyNameAndTaskName
                            (selectedUserStory.getName().toString(), prerequisite));
                }
            }
        } else
            countPrerequisites = -1;  //no prerequisites


        if (((countDependencies == depSize) && (countPrerequisites == preSize))
                || ((countDependencies == -1) && (countPrerequisites == preSize)) ||
                ((countDependencies == depSize) && (countPrerequisites == -1)))
            return 1;
        else if (((countDependencies | countPrerequisites) == 0) || ((countDependencies == -1) && (countPrerequisites == 0)) ||
                ((countDependencies == 0) && (countPrerequisites == -1)))
            return 2;
        else if ((countDependencies == depSize) && (countPrerequisites == 0))
            return 3;
        else if ((countDependencies == -1) && (countPrerequisites != preSize) && (countPrerequisites != 0))
            return 4;
        else if ((countPrerequisites == -1) && (countDependencies != depSize) && (countDependencies != 0))
            return 5;
        else if ((countPrerequisites == 0) && (countDependencies != depSize) && (countDependencies != 0))
            return 6;
        else if ((countPrerequisites != 0) && (countPrerequisites != preSize) && (countDependencies == depSize))
            return 7;
        else //(countDependencies & countPrerequisites) == -1)
            return 8;
    }

    //case 1 will be considered further. there are 3 conditions
    //Get task list which are needed to be rescheduled
    public ArrayList<Task> getTaskListToBeRescheduled(Task startTask) {

        //this tasklist will contain crtaskDependencies
        ArrayList<Task> taskList = new ArrayList<Task>();

        TaskDAO taskDAO = (TaskDAO) DashboardUI.context.getBean("Task");
        UserStoryDAO userStoryDAO = (UserStoryDAO) DashboardUI.context.getBean("UserStory");

        int taskStartTime = getTaskStartTime(startTask);

        if (oldSprintEndTime < (taskStartTime + Integer.parseInt(startTask.getEstimateTime()))) {
            System.out.println("CR will complete after the sprint end date. So it should be shift to the next sprint");

            /*PrioritizeUserStories prioritizeUserStories = new PrioritizeUserStories();
                Map<Integer, UserStory> prioritizeUSList = prioritizeUserStories.getPrioritizeUserStoryMap(selectedUserStory.getProject());

                for (int i = 0; i < prioritizeUSList.size(); i++) {
                    if (prioritizeUSList.get(i).getState() == "pending") { //assign 1st pending userStory
                        newTask.setUserStory(prioritizeUSList.get(i));
                        break;
                    }
                }*/

            startTask.setStartTime("0");

            //manually set for other userstory
            startTask.setUserStory(userStoryDAO.getUserStoryFormProjectNameAndUserStoryName("UK Emergency", "USNEw"));
            //if (startTask.getState().contentEquals("initial")) startTask.setState("pending");
            startTask.setProcessed(true);
            taskDAO.updateTask(startTask);
            taskWithEndTime.put(startTask.getTaskId(), -1);


            // at least one prerequisite of the current task is not in the current user story.Task has shifted to other userstory
            // so this task should also shift to that userstory

        } else if (taskStartTime == -1) {

            startTask.setStartTime("0");

            //manually set for other userstory
            startTask.setUserStory(userStoryDAO.getUserStoryFormProjectNameAndUserStoryName("UK Emergency", "USNEw"));
            //if (startTask.getState().contentEquals("initial")) startTask.setState("pending");
            startTask.setProcessed(true);
            taskDAO.updateTask(startTask);
            taskWithEndTime.put(startTask.getTaskId(), taskStartTime);

        } else {

            //set cr start time and update it
            startTask.setStartTime(Integer.toString(taskStartTime));
            //if (startTask.getState().contentEquals("initial")) startTask.setState("pending");
            startTask.setProcessed(true);
            taskDAO.updateTask(startTask);

            taskWithEndTime.put(startTask.getTaskId(), taskStartTime + Integer.parseInt(startTask.getEstimateTime()));
        }

        //check that task is already in the task collection
        boolean isTaskAvailable = false;
        int index = -1;
        for (Task task : allTaskCollection) {
            index++;
            if (task.getTaskId() == startTask.getTaskId()) {
                isTaskAvailable = true;
                break;
            }
        }

        if (isTaskAvailable) {
            allTaskCollection.remove(index);
            allTaskCollection.add(index, startTask);
        } else {
            allTaskCollection.add(startTask);
        }

        //need to check and add the dependencies of the CR
        if (startTask.getDependancy() != null && !startTask.getDependancy().isEmpty()) {
            String[] depList = startTask.getDependancy().split(",");

            for (String dep : depList) {
                Task task = taskDAO.getTaskFromUserStroyNameAndTaskName(selectedUserStory.getName(), dep);
                taskList.add(task);
            }
        }
        return taskList;
    }

    //start with CR task. newTask is the CR Task
    public void updateCRDependentTaskPath(Task changingTask) {

        ArrayList<Task> taskToBeConsidered = getTaskListToBeRescheduled(changingTask);

        if (taskToBeConsidered.size() != 0) {
            for (Task task : taskToBeConsidered) {

                if (task != null)
                    updateCRDependentTaskPath(task);
            }
        }

    }

    //case 1 wil be considered further. there are 3 conditions
    //need to give task which needs to find start time
    public int getTaskStartTime(Task taskWithNoStartTime) {

        //need to put this time into DB
        int taskEndTime = 0;
        boolean prerequisiteIsInCurrentUserStory = true;

        ArrayList<String> taskPrerequisitesList;

        try {
            //consider taskWithNoStartTime
            if (taskWithNoStartTime.getPreRequisits() != null || !taskWithNoStartTime.getPreRequisits().isEmpty() || taskWithNoStartTime.getPreRequisits() != " ") {
                String[] taskPrerequisites = taskWithNoStartTime.getPreRequisits().split(",");
                for (String prerequisite : taskPrerequisites) {

                    if (!prerequisiteIsInCurrentUserStory) break;

                    for (Task task : allTaskCollection) {

                        if (task.getName().equalsIgnoreCase(prerequisite)) {

                            //because userstory can be changed in the CR handling process
                            if (task.getUserStory().getUserStoryId() == taskWithNoStartTime.getUserStory().getUserStoryId()) {

                                int prerequisiteTaskEndTime = taskWithEndTime.get(task.getTaskId());
                                if (taskEndTime < prerequisiteTaskEndTime) taskEndTime = prerequisiteTaskEndTime;

                            } else {
                                prerequisiteIsInCurrentUserStory = false;
                                taskEndTime = -1;
                                break;
                            }
                        }
                    }
                }
            } else {
                System.out.println("No prerequisites");
                return 0;
            }
        } catch (Exception e) {
            System.out.println("No prerequisites");
            return 0;
        }
        return taskEndTime;

    }

        /*public boolean checkSprintIsStarted() {

        boolean isSprintStarted = false;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        currentDate = LocalDate.now();
        System.out.println("Current Date : " + currentDate);

        try {
            Date d1 = sdf.parse(sprintStartDate);
            sprintSD = d1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();   //convert date into local date

            if (currentDate.isAfter(sprintSD)) {
                isSprintStarted = true;
                System.out.println("Sprint has started");
            } else {
                isSprintStarted = false;
                System.out.println("Sprint hasn't started");
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }

        if (isSprintStarted) return true;
        else return false;
    }

    public int getSprintEstimatedTime(Map<Integer, Task> prioritizedTaskList) {

        int estimatedSprintTimeDuration = 0;

        for (int i = 1; i <= prioritizedTaskList.size(); i++) {
            estimatedSprintTimeDuration += Integer.parseInt(prioritizedTaskList.get(i).getEstimateTime());
        }

        return estimatedSprintTimeDuration;
    }*/

    /*public void checkAvailabilityOfMembers() {

        List<User> availableMembersInProject = new ArrayList<User>();
        List<User> availableMembersInOtherProjects = new ArrayList<User>();

        UserDAO userDAO = (UserDAO) DashboardUI.context.getBean("User");
        List<User> allMembers = userDAO.loadMembers();

        for (User user : allMembers) {
            if (user.isAvailability()) {
                if (user.getProjectId() == selectedUserStory.getProject().getProjectId()) {
                    availableMembersInProject.add(user);
                } else {
                    availableMembersInOtherProjects.add(user);
                }
            }
        }

        if (!availableMembersInProject.isEmpty()) {

            System.out.println("Matched Members with Project: "
                    + availableMembersInProject.size() + "\n");

            //First check the dates and then check skills of the selected members
            checkLastAvailableDateAndSkills(availableMembersInProject);

            //check last available date with sprint start date
            if (!datesMatchedWithCurProject.isEmpty()) {

                if (!skillsMatchedWithCurProject.isEmpty()) {
                    System.out.println("perfectly skill matched members : " + skillsMatchedWithCurProject.size());
                } else {
                    System.out.println("Skilled not matched perfectly. Assign training period");
                    //assignTrainingPeriod();
                }
            }
        }
        //check other projects
        if (availableMembersInProject.isEmpty() || datesMatchedWithCurProject.isEmpty()) {
            System.out.println("No matched members in the project");
            System.out.println("\nChecking other projects...\n");

            checkLastAvailableDateAndSkills(availableMembersInOtherProjects);

            if (!datesMatchedWithCurProject.isEmpty()) {

                if (!skillsMatchedWithCurProject.isEmpty()) {
                    System.out.println("perfectly skill matched members : " + skillsMatchedWithCurProject.size());
                } else {
                    System.out.println("Skilled not matched perfectly. Assign training period");
                    //assignTrainingPeriod();
                }
            }


        }
    }


    public void checkLastAvailableDateAndSkills(List<User> membersInSelectedProject) {

        boolean isDateAvailable = false;
        boolean isSkillsMatchPerfectly = false;
        StringBuilder sb = new StringBuilder();

        for (User member : membersInSelectedProject) {
            isDateAvailable = lastAvailableDateChecker(member);
            if (isDateAvailable) {
                datesMatchedWithCurProject.add(member);
                isSkillsMatchPerfectly = skillsChecker(member);
                if (isSkillsMatchPerfectly)
                    skillsMatchedWithCurProject.add(member);
                else {
                    sb.append(member.getMemberId() + " ");
                }
            } else {
                System.out.println("Date is not ok");
            }
        }
    }

    public boolean lastAvailableDateChecker(User member) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        Calendar cal = Calendar.getInstance();

        try {

            Date d1 = sdf.parse(member.getLastAvailableDate().toString());
            LocalDate memberLastAvailableDate = d1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();   //convert date into local date

            if (memberLastAvailableDate.isAfter(sprintSD)) {
                System.out.println(member.getLastAvailableDate() + " is after the sprint start date :"
                        + sprintSD);
                return true;

            } else {
                System.out.println(member.getLastAvailableDate() + " is before the sprint start date :"
                        + sprintSD);
                return false;
            }

        } catch (Exception e) {
            System.out.println("Error with getting dates");
            return false;
        }
    }

    public boolean skillsChecker(User member) {

        String[] skillList = newTask.getTechnicalSkills().split(",");
        int numSkills = skillList.length;
        int count = 0;

        for (String skill : skillList) {

            if (member.getTechnicalSkills().toLowerCase().contains(skill.toLowerCase())) { // check skills
                count++;
            }
        }

        System.out.println(count + " : skills matched in " + member.getUserName());

        if (count == numSkills)
            return true;
        else
            return false;
    }*/

    public void assignTrainingPeriod() {

        /*int days = 0;
        System.out.println("All skills aren't matched with anyone in the project");

        Scanner in = new Scanner(System.in);
        System.out.println("\nEnter one to give training (days) : "
                + sb.toString());
        String line = in.nextLine();

        for (User mem : datesMatchedWithCurProject) {
            if (mem.memId.equals(line)) {

                System.out.println("Enter training period (days) :");
                days = Integer.parseInt(in.nextLine());
                System.out
                        .println("----------------------------------------------");
                System.out.println("Training is assigned to: " + mem.memId
                        + " with " + days + " days.");
                System.out
                        .println("----------------------------------------------");
            }

        }

        if (days == 0)
            System.out.println("Training is assigned to: Null");*/

    }

}
























