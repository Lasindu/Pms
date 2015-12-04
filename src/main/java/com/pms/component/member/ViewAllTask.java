package com.pms.component.member;

import com.pms.DashboardUI;
import com.pms.component.member.taskganntchart.TaskGanttChart;
import com.pms.dao.ProjectDAO;
import com.pms.dao.TaskDAO;
import com.pms.dao.UserDAO;
import com.pms.dao.UserStoryDAO;
import com.pms.domain.Project;
import com.pms.domain.Task;
import com.pms.domain.User;
import com.pms.domain.UserStory;
import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.fieldgroup.PropertyId;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.server.Responsive;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lasindu on 7/11/2015.
 */
public class ViewAllTask {
    public VerticalLayout viewTaskLayout;
    private Table viewTaskTable;
    private Button viewAssignedTask,reSchedule;
    private ComboBox proList;
    private String userRole;

    @PropertyId("memberName")
    private Select memberName;

    public ViewAllTask()
    {
        buildViewAllTasks();

    }

    public Component getAllTasks()
    {
        return viewTaskLayout;
    }


    private void buildViewAllTasks()
    {

        final TaskDAO taskDAO = (TaskDAO) DashboardUI.context.getBean("Task");
        final UserDAO userDAO = (UserDAO) DashboardUI.context.getBean("User");
        final ProjectDAO projectDAO = (ProjectDAO) DashboardUI.context.getBean("Project");

        final User user = (User) VaadinSession.getCurrent().getAttribute(
                User.class.getName());

        userRole=user.getRole();

        viewTaskLayout = new VerticalLayout();
        //viewProjectLayout.setCaption("View Project");
        viewTaskLayout.setMargin(true);
        viewTaskLayout.setSpacing(true);
        viewTaskLayout.setSizeFull();

        viewTaskLayout.addComponent(buildToolbar());


        viewTaskTable= new Table("");
        //viewProjectTable.addStyleName(ValoTheme.TABLE_BORDERLESS);
        viewTaskTable.addStyleName(ValoTheme.TABLE_NO_HORIZONTAL_LINES);
        viewTaskTable.addStyleName(ValoTheme.TABLE_COMPACT);
        viewTaskTable.setSelectable(true);

        viewTaskTable.addContainerProperty("Index", Integer.class, null);
        viewTaskTable.addContainerProperty("Task Name",  String.class, null);
        viewTaskTable.addContainerProperty("Project", String.class, null);
        viewTaskTable.addContainerProperty("Date", String.class, null);
        viewTaskTable.addContainerProperty("Technical Skill", String.class, null);
        viewTaskTable.addContainerProperty("Domain Skill", String.class, null);
        viewTaskTable.addContainerProperty("Assign To", Select.class, null);
        viewTaskTable.addContainerProperty("Assign Task", Button.class, null);

        viewTaskTable.setSizeFull();


        List<Task> taskList = new ArrayList();
        taskList = taskDAO.getAllTasks();

        for(int x=0;x<taskList.size();x++)
        {
            int index=x+1;

            if (userRole.equals("admin")||userRole.equals("pm"))
            {
                final Button saveTaskButton=new Button("Assign");
                taskList.get(x).setRowId(index);
                saveTaskButton.setData(taskList.get(x));

                // Not developed algorithm yet for getting member
                memberName = new Select("Select Member");
                memberName.setNullSelectionAllowed(false);

                MemberAssignAlgoritm algoObj = new MemberAssignAlgoritm();
                UserStory us = taskList.get(x).getUserStory();
                String pro = projectDAO.getProjectIdFromUserStoryName(us.getName());
                List<User> userList = algoObj.getAvailableAndRelatedMember(taskList.get(x),pro,userDAO,taskDAO);

                if(taskList.get(x).getAssignedTo() != null){
                    memberName.addItem(taskList.get(x).getAssignedTo());
                }else{
                    memberName.addItem("");
                }

                for(int i=0;i<userList.size();i++){
                    memberName.addItem(userList.get(i).getUserName());
                }

                if(userList.size()>0) {
                    memberName.setValue(memberName.getItemIds().iterator().next());
                }
                if(taskList.get(x).getAssignedTo()!=null){
                    memberName.setValue(memberName.getItemIds().iterator().next());
                }
                viewTaskTable.addItem(new Object[]{index, taskList.get(x).getName(), pro, taskList.get(x).getDate(), taskList.get(x).getTechnicalSkill(),taskList.get(x).getDomainSkill(), memberName, saveTaskButton}, index);
                saveTaskButton.addClickListener(new Button.ClickListener() {
                    public void buttonClick(Button.ClickEvent event) {

                        int rowId = ((Task)event.getButton().getData()).getRowId();
                        Item item = viewTaskTable.getItem(rowId);
                        Task task = (Task)event.getButton().getData();
                        String assignedTo=item.getItemProperty("Assign To").getValue().toString().trim();

                        task.setAssignedTo(assignedTo);
                        task.setIsAssigned("1");
                        taskDAO.updateTask(task);

                        Page.getCurrent().reload();

                    }
                });
            }
        }
        //Get All project list for combobox
        final List<Project> projectList = new ArrayList();
        User projectsLoadedUser= userDAO.loadUserProjects(user);
        projectList.addAll(projectsLoadedUser.getProjects());

        //project list for reschedule
        proList = buildProjectList();
        proList.setTextInputAllowed(true);

        for(int x=0;x<projectList.size();x++)
        {
            proList.addItem(projectList.get(x).getName());
        }
        proList.addListener(new Property.ValueChangeListener() {
            private static final long serialVersionUID = -5188369735622627751L;

            public void valueChange(Property.ValueChangeEvent event) {
                if (proList.getValue() != null) {
                    Project project;
                    for(Project project1:projectList)
                    {
                        if(project1.getName().equals(proList.getValue()))
                        {
                            project = project1;
                            final TaskDAO taskDAO = (TaskDAO) DashboardUI.context.getBean("Task");
                            UserStoryDAO userStoryDAO = (UserStoryDAO) DashboardUI.context.getBean("UserStory");
                            ProjectDAO projectDAO = (ProjectDAO) DashboardUI.context.getBean("Project");

                            TaskAlgo taskAlgoObj = new TaskAlgo();
                            UserStory userStory=userStoryDAO.getCurrentWorkingUserStory(projectDAO.getProjectFromProjectName(project.getName()));
                            List<Task> taskMap = taskDAO.getAllTasksForUSerStory(userStory);
                            List<Task> linearTaskSchedule = taskAlgoObj.getTaskSchedule(taskMap);
                        }
                    }
                }
            }

        });

        HorizontalLayout tools2 = new HorizontalLayout(proList);
        tools2.setSpacing(true);
        tools2.addStyleName("toolbar");
        viewTaskLayout.addComponent(tools2);

        viewTaskLayout.addComponent(viewTaskTable);
        viewTaskLayout.setExpandRatio(viewTaskTable,1);

        //Gantt chart

        //project list for gantt chart
        final ComboBox selectProject= new ComboBox("Select Project :");
        selectProject.setTextInputAllowed(false);

        for(int x=0;x<projectList.size();x++)
        {
            selectProject.addItem(projectList.get(x).getName());
        }

        selectProject.addListener(new Property.ValueChangeListener() {
            private static final long serialVersionUID = -5188369735622627751L;

            public void valueChange(Property.ValueChangeEvent event) {
                if (selectProject.getValue() != null) {
                    Project project;
                    for(Project project1:projectList)
                    {
                        if(project1.getName().equals(selectProject.getValue()))
                        {
                            project = project1;
                            TaskGanttChart ganntChart = new TaskGanttChart();
                            viewTaskLayout.addComponent(ganntChart.init(project));
                        }
                    }
                }
            }

        });
        viewTaskLayout.setImmediate(true);
        viewTaskLayout.addComponent(selectProject);

    }

    private Component buildToolbar() {
        HorizontalLayout header = new HorizontalLayout();
        header.addStyleName("viewheader");
        header.setSpacing(true);
        Responsive.makeResponsive(header);

        Label title = new Label("Task List For Member Asigning");
        title.setSizeUndefined();
        title.addStyleName(ValoTheme.LABEL_H1);
        title.addStyleName(ValoTheme.LABEL_NO_MARGIN);
        header.addComponent(title);


        if(userRole.equals("admin")||userRole.equals("pm")) {
            viewAssignedTask = buildCreateReport();

            HorizontalLayout tools = new HorizontalLayout(buildFilter(),viewAssignedTask);
            tools.setSpacing(true);
            tools.addStyleName("toolbar");
            header.addComponent(tools);

        }
        else
        {
            HorizontalLayout tools = new HorizontalLayout(buildFilter());
            tools.setSpacing(true);
            tools.addStyleName("toolbar");
            header.addComponent(tools);

        }

        return header;
    }

    private Button buildCreateReport() {

        final Button viewAssignTask = new Button("View Assigned Task");
        viewAssignTask.addClickListener(new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {
                //view assign task button click
                DashboardUI.getCurrent().getNavigator().navigateTo("Member_Assign/" + "ViewAssignedTask");
            }
        });


        return viewAssignTask;
    }
    private ComboBox buildProjectList() {

        final ProjectDAO projectDAO = (ProjectDAO) DashboardUI.context.getBean("Project");

        final List<Project> projectList = new ArrayList();
        projectList.addAll(projectDAO.getAllProjects());

        final ComboBox selectProject= new ComboBox("Reschedule...");
        //selectProject.getItem(0);
        selectProject.setTextInputAllowed(true);

        for(int x=0;x<projectList.size();x++)
        {
            selectProject.addItem(projectList.get(x).getName());
        }
        return selectProject;
    }

    private Button buildReschedule() {

        final Button viewAssignTask = new Button("Reschedule");
        viewAssignTask.addClickListener(new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {
                //view assign task button click
                final TaskDAO taskDAO = (TaskDAO) DashboardUI.context.getBean("Task");
                UserStoryDAO userStoryDAO = (UserStoryDAO) DashboardUI.context.getBean("UserStory");
                ProjectDAO projectDAO = (ProjectDAO) DashboardUI.context.getBean("Project");

                TaskAlgo taskAlgoObj = new TaskAlgo();
                //List<Task> res = taskDAO.getAllTasks();
                //Only for first project.develop for all the projects
                //UserStory userStory=userStoryDAO.getCurrentWorkingUserStory(projectDAO.getProjectFromProjectName(""));
                //List<Task> taskMap = taskDAO.getAllTasksForUSerStory(userStory);
                //List<Task> linearTaskSchedule = taskAlgoObj.getTaskSchedule(taskMap);
            }
        });
        return viewAssignTask;
    }

    private Component buildFilter() {
        final TextField filter = new TextField();
        filter.addTextChangeListener(new FieldEvents.TextChangeListener() {
            @Override
            public void textChange(final FieldEvents.TextChangeEvent event) {
                Container.Filterable data = (Container.Filterable) viewTaskTable.getContainerDataSource();
                data.removeAllContainerFilters();
                data.addContainerFilter(new Container.Filter() {
                    @Override
                    public boolean passesFilter(final Object itemId,
                                                final Item item) {

                        if (event.getText() == null
                                || event.getText().equals("")) {
                            return true;
                        }

                        return filterByProperty("Index", item,
                                event.getText())
                                || filterByProperty("Project", item,
                                event.getText());

                    }

                    @Override
                    public boolean appliesToProperty(final Object propertyId) {
                        if (propertyId.equals("Index")
                                || propertyId.equals("Project")) {
                            return true;
                        }
                        return false;
                    }
                });
            }
        });

        filter.setInputPrompt("Filter");
        filter.setIcon(FontAwesome.SEARCH);
        filter.addStyleName(ValoTheme.TEXTFIELD_INLINE_ICON);
        filter.addShortcutListener(new ShortcutListener("Clear",
                ShortcutAction.KeyCode.ESCAPE, null) {
            @Override
            public void handleAction(final Object sender, final Object target) {
                filter.setValue("");
                ((com.vaadin.data.Container.Filterable) viewTaskTable.getContainerDataSource())
                        .removeAllContainerFilters();
            }
        });
        return filter;
    }

    private boolean filterByProperty(final String prop, final Item item,
                                     final String text) {
        if (item == null || item.getItemProperty(prop) == null
                || item.getItemProperty(prop).getValue() == null) {
            return false;
        }
        String val = item.getItemProperty(prop).getValue().toString().trim()
                .toLowerCase();
        if (val.contains(text.toLowerCase().trim())) {
            return true;
        }
        return false;
    }
}
