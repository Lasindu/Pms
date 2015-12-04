package com.pms.component.member;

import com.pms.DashboardUI;
import com.pms.dao.ProjectDAO;
import com.pms.dao.TaskDAO;
import com.pms.dao.UserDAO;
import com.pms.domain.*;
import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.PropertyId;
import com.vaadin.data.validator.IntegerRangeValidator;
import com.vaadin.server.*;
import com.vaadin.shared.Position;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lasindu on 8/15/2015.
 */
public class ViewMember extends CustomComponent {

    private String userRole;
    private Table myTaskTable;
    private Label noAssignTask;
    private String userName;
    private Button competeTask;
    public VerticalLayout viewProjectLayout;

    private final BeanFieldGroup<Quality> fieldGroup;
    @PropertyId("bugsReopen")
    private Select ReopenDefects;
    @PropertyId("bugsAfterRelease")
    private Select foundDefects;
    @PropertyId("timelyReview")
    private Select userReview;
    @PropertyId("learningCapacity")
    private Select learningCapacity;
    @PropertyId("dedicationToWork")
    private Select dedicationToWork;
    //@PropertyId("userName")
    private Button submiRate;

    //for qa
    @PropertyId("bugsReopen")
    private Select uatDeffects;
    @PropertyId("bugsAfterRelease")
    private Select reportedBugs;
    @PropertyId("timelyReview")
    private Select testSuits;
    @PropertyId("dedicationToWork")
    private Select writtenTestSuits;
    @PropertyId("learningCapacity")
    private Select learningCapacityQa;
    //@PropertyId("userName")
    private Button submiRateQa;

    public ViewMember(String uName)
    {
        this.userName=uName;
        buildViewMember();
        fieldGroup = new BeanFieldGroup<Quality>(Quality.class);
    }


    public Component getMember()
    {
        return viewProjectLayout;
    }




    private void buildViewMember() {

        User user = (User) VaadinSession.getCurrent().getAttribute(
                User.class.getName());
        userRole = user.getRole();

        viewProjectLayout = new VerticalLayout();
        viewProjectLayout.setMargin(true);
        viewProjectLayout.setSpacing(true);
        viewProjectLayout.setSizeFull();


        //get project object prom the database
        final UserDAO userDAO = (UserDAO) DashboardUI.context.getBean("User");
        final ProjectDAO projectDAO = (ProjectDAO) DashboardUI.context.getBean("Project");
        user = userDAO.loadUserDetails(userName);


        HorizontalLayout header = new HorizontalLayout();
        header.addStyleName("viewheader");
        header.setSpacing(true);
        Responsive.makeResponsive(header);

        Label title = new Label(user.getFirstName()+" "+user.getLastName());
        title.setSizeUndefined();
        title.addStyleName(ValoTheme.LABEL_H1);
        title.addStyleName(ValoTheme.LABEL_NO_MARGIN);
        viewProjectLayout.addComponent(title);//header.addComponent(title);

        //viewProjectLayout.addComponent(header);

        Image proImg=new Image("",new ThemeResource("img/profilepic1.png"));
        Image proImg1=new Image("",new ThemeResource("img/profilepic1.png"));
        Image space1=new Image("",new ThemeResource("img/space1.png"));
        Image space2=new Image("",new ThemeResource("img/space2.png"));
        Image space3=new Image("",new ThemeResource("img/space1.png"));
        Image space4=new Image("",new ThemeResource("img/space2.png"));
        Label uName=new Label("Username :   "+user.getUserName());
        Label uName1=new Label("Username :   "+user.getUserName());
        Label breake1=new Label("");
        Label breake2=new Label("");
        Label breake3=new Label("");
        Label breake4=new Label("");

        Label email=new Label("Email :   "+user.getEmail());
        Label mobile= new Label("Mobile :   "+user.getContact());
        Label role=new Label("Role :   "+user.getRole());
        Label rate=new Label("Overall Rating :   "+9);
        Label dateOfBirth=new Label("Date of Birth :   "+user.getDateOfBirth());
        Label experience=new Label("Experience :   "+user.getExperience());
        Label project=new Label("Working Project :   "+user.getAssignedProjectName());
        Label dSkill=new Label("Domain Skill :   "+user.getDomainSkills());
        Label tSkill=new Label("Technical Skill :   "+user.getTechnicalSkills());

        Label email1=new Label("Email :   "+user.getEmail());
        Label mobile1= new Label("Mobile :   "+user.getContact());
        Label role1=new Label("Role :   "+user.getRole());
        Label rate1=new Label("Overall Rating :   "+10);
        Label dateOfBirth1=new Label("Date of Birth :   "+user.getDateOfBirth());
        Label project1=new Label("Working Project :   "+user.getAssignedProjectName());
        Label dSkill1=new Label("Domain Skill :   "+user.getDomainSkills());
        Label tSkill1=new Label("Technical Skill :   "+user.getTechnicalSkills());

        VerticalLayout userDetailss = new VerticalLayout(breake1,breake2,uName,dateOfBirth,email,mobile,role,rate,project,dSkill,tSkill);
        VerticalLayout userDetailss2 = new VerticalLayout(breake3,breake4,uName1,dateOfBirth1,email1,mobile1,role1,rate1,project1,dSkill1,tSkill1);
        HorizontalLayout ratingFormHoriLayout = new HorizontalLayout(proImg,space1,userDetailss,space2,buildRatingForm());
        HorizontalLayout ratingFormHoriLayoutForQa = new HorizontalLayout(proImg1,space3,userDetailss2,space4,buildRatingFormForQa());
        if(user.getRole().equalsIgnoreCase("project manager") || user.getRole().equalsIgnoreCase("software engineer") || user.getRole().equalsIgnoreCase("team leader")){
            viewProjectLayout.addComponent(ratingFormHoriLayout);
        }else if(user.getRole().equalsIgnoreCase("quality engineer")){
            viewProjectLayout.addComponent(ratingFormHoriLayoutForQa);
        }
        viewProjectLayout.addComponent(experience);
        //viewProjectLayout.addComponent(buildRatingForm());


        myTaskTable= new Table("");
        //viewProjectTable.addStyleName(ValoTheme.TABLE_BORDERLESS);
        myTaskTable.addStyleName(ValoTheme.TABLE_NO_HORIZONTAL_LINES);
        myTaskTable.addStyleName(ValoTheme.TABLE_COMPACT);
        myTaskTable.setSelectable(true);

        myTaskTable.addContainerProperty("Index", Integer.class, null);
        myTaskTable.addContainerProperty("Task Name",  String.class, null);
        myTaskTable.addContainerProperty("Project", String.class, null);
        myTaskTable.addContainerProperty("Estimate Time", String.class, null);
        if(userRole.equalsIgnoreCase("admin") ||userRole.equalsIgnoreCase("pm")){
            myTaskTable.addContainerProperty("Duration", TextField.class, null);
        }
        myTaskTable.addContainerProperty("Complete Task", Button.class, null);
        myTaskTable.setSizeFull();

        final TaskDAO taskDAO = (TaskDAO) DashboardUI.context.getBean("Task");
        List<Task> taskList = new ArrayList();
        taskList = taskDAO.getMyTaskList(userName);
        if(taskList!=null){
            for(int x=0;x<taskList.size();x++) {
                int index = x + 1;

                UserStory us = taskList.get(x).getUserStory();
                String pro = projectDAO.getProjectIdFromUserStoryName(us.getName());

                Button completeTaskButton=new Button("Complete");
                TextField durationTextField = new TextField("1");
                if(taskList.get(x).getCompleteTime()!= null){
                    durationTextField.setValue(taskList.get(x).getCompleteTime());

                }else{
                    durationTextField.setValue("0");
                }

                if(userRole.equalsIgnoreCase("admin")){// && userRole.equalsIgnoreCase("pm")
                    durationTextField.setReadOnly(false);
                }
                taskList.get(x).setSeverity(x+1);
                completeTaskButton.setData(taskList.get(x));

                if(userRole.equalsIgnoreCase("admin") || userRole.equalsIgnoreCase("pm")){
                    myTaskTable.addItem(new Object[]{index, taskList.get(x).getName(),pro, taskList.get(x).getEstimateTime(), durationTextField, completeTaskButton},index);
                }
                completeTaskButton.addClickListener(new Button.ClickListener() {
                    public void buttonClick(Button.ClickEvent event) {

                        //save member query
                        Task task = (Task)event.getButton().getData();
                        //String duration = task.getCompleteTime(); // get duration from textfield - not completed

                        int rowId = ((Task)event.getButton().getData()).getSeverity();
                        Item item = myTaskTable.getItem(rowId);
                        String duration=item.getItemProperty("Duration").getValue().toString().trim();

                        task.setCompleteTime(duration);
                        task.setSeverity(0);
                        taskDAO.updateTask(task);
                        if(duration==null){
                            duration = "0";
                        }
                        Notification success = new Notification(
                                "Duration for task is "+duration);
                        success.setDelayMsec(2000);
                        success.setStyleName("bar success small");
                        success.setPosition(Position.BOTTOM_CENTER);
                        success.show(Page.getCurrent());
                    }
                });
            }

            viewProjectLayout.addComponent(myTaskTable);
            viewProjectLayout.setExpandRatio(myTaskTable,1);
        }else{
            noAssignTask = new Label("No Assigned Tasks");
            viewProjectLayout.addComponent(noAssignTask);
        }
    }

    private FormLayout buildRatingForm() {

        FormLayout formLayout = new FormLayout();

        ReopenDefects = new Select("Reopen Defects :");
        for(int i=1;i<=10;i++){
            ReopenDefects.addItem(i);
        }
        formLayout.addComponent(ReopenDefects);
        foundDefects = new Select("Found Defects :");
        for(int i=1;i<=10;i++){
            foundDefects.addItem(i);
        }
        formLayout.addComponent(foundDefects);
        userReview = new Select("User Review :");
        for(int i=1;i<=10;i++){
            userReview.addItem(i);
        }
        formLayout.addComponent(userReview);
        learningCapacity = new Select("Learning Capacity :");
        for(int i=1;i<=10;i++){
            learningCapacity.addItem(i);
        }
        formLayout.addComponent(learningCapacity);
        dedicationToWork = new Select("Dedication To Work :");
        for(int i=1;i<=10;i++){
            dedicationToWork.addItem(i);
        }

        formLayout.addComponent(dedicationToWork);
        submiRate = new Button("Submit");
        formLayout.addComponent(submiRate);

        submiRate.addClickListener(new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {
                UserDAO userDAO= (UserDAO) DashboardUI.context.getBean("User");
                Quality quality = new Quality();
                int drd = Integer.parseInt(ReopenDefects.getValue().toString());
                int dfd = Integer.parseInt(foundDefects.getValue().toString());
                int dur = Integer.parseInt(userReview.getValue().toString());
                int dlc = Integer.parseInt(learningCapacity.getValue().toString());
                int dtw = Integer.parseInt(dedicationToWork.getValue().toString());
                quality.setUserName(userName);
                quality.setReopenDefects(drd);
                quality.setFoundDefects(dfd);
                quality.setUserReview(dur);
                quality.setLearningCapacity(dlc);
                quality.setDedicationToWork(dtw);

                //userDAO.updateQuality(quality);
            }
        });
        return formLayout;
    }
    private FormLayout buildRatingFormForQa() {

        FormLayout formLayout = new FormLayout();

        uatDeffects = new Select("UAT Defects :");
        for(int i=1;i<=10;i++){
            uatDeffects.addItem(i);
        }
        formLayout.addComponent(uatDeffects);
        reportedBugs = new Select("Reported Bugs :");
        for(int i=1;i<=10;i++){
            reportedBugs.addItem(i);
        }
        formLayout.addComponent(reportedBugs);
        testSuits = new Select("Executed Test Suits :");
        for(int i=1;i<=10;i++){
            testSuits.addItem(i);
        }
        formLayout.addComponent(testSuits);
        writtenTestSuits = new Select("Written Test Suits :");
        for(int i=1;i<=10;i++){
            writtenTestSuits.addItem(i);
        }
        formLayout.addComponent(writtenTestSuits);
        learningCapacityQa = new Select("Learning Capacity :");
        for(int i=1;i<=10;i++){
            learningCapacityQa.addItem(i);
        }

        formLayout.addComponent(learningCapacityQa);

        submiRateQa = new Button("Submit");
        formLayout.addComponent(submiRateQa);

        submiRateQa.addClickListener(new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {
                UserDAO userDAO= (UserDAO) DashboardUI.context.getBean("User");
                Quality quality = new Quality();
                int ud = Integer.parseInt(uatDeffects.getValue().toString());
                int rb = Integer.parseInt(reportedBugs.getValue().toString());
                int ts = Integer.parseInt(testSuits.getValue().toString());
                int wts = Integer.parseInt(writtenTestSuits.getValue().toString());
                int lc = Integer.parseInt(learningCapacityQa.getValue().toString());
                int dtw = Integer.parseInt(dedicationToWork.getValue().toString());

                quality.setUserName(userName);
                quality.setUatDefects(ud);
                quality.setReportedBugs(rb);
                quality.setTestSuits(ts);
                quality.setWrittenTestSuits(wts);
                quality.setLearningCapacity(lc);
                quality.setDedicationToWork(dtw);

                //userDAO.updateQuality(quality);
            }
        });
        return formLayout;
    }

}








































