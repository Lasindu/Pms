package com.pms.component.CR;

import com.pms.DashboardUI;
import com.pms.component.TaskWindow;
import com.pms.dao.ProjectDAO;
import com.pms.dao.TaskDAO;
import com.pms.dao.UserStoryDAO;
import com.pms.domain.Project;
import com.pms.domain.Task;
import com.pms.domain.User;
import com.pms.domain.UserStory;
import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.server.Responsive;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.vaadin.dialogs.ConfirmDialog;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Amali on 21/11/2015.
 */
public class ViewCRTasksWindow extends CustomComponent {

    public VerticalLayout viewCRTasksLayout;
    private String userRole;
    private String projectName;
    private Button create;
    private Table tasksTable;
    private UserStory userStory;
    private Project project;

    public ViewCRTasksWindow(String projectName) {
        this.projectName = projectName;
        buildViewCRTasks();
    }

    public Component getCRTasks() {
        return viewCRTasksLayout;

    }


    private void buildViewCRTasks() {
        User user = (User) VaadinSession.getCurrent().getAttribute(
                User.class.getName());
        userRole = user.getRole();

        viewCRTasksLayout = new VerticalLayout();
        //viewUserStoryLayout.setCaption("View Project");
        viewCRTasksLayout.setMargin(true);
        viewCRTasksLayout.setSpacing(true);
        viewCRTasksLayout.setSizeFull();


        ProjectDAO projectDAO = (ProjectDAO) DashboardUI.context.getBean("Project");
        project = projectDAO.getProjectFromProjectName(projectName);

        UserStoryDAO userStoryDAO = (UserStoryDAO) DashboardUI.context.getBean("UserStory");
        Collection<UserStory> userStories = userStoryDAO.getAllUserSeriesOfProject(project);

        ArrayList<UserStory> allUserStories = new ArrayList<UserStory>();
        allUserStories.addAll(userStories);
        ArrayList<Task> tempTaskList = new ArrayList<Task>();
        ArrayList<Task> crTasksToDisplay = new ArrayList<Task>();

        for (UserStory userStory1 : allUserStories) {
            tempTaskList.addAll(userStoryDAO.getUserStoryTaskList(userStory1));

            for (Task task : tempTaskList) {
                if (task.isCr()) crTasksToDisplay.add(task);
            }

            tempTaskList.clear();
        }


        HorizontalLayout header = new HorizontalLayout();
        header.addStyleName("viewheader");
        header.setSpacing(true);
        Responsive.makeResponsive(header);

        Label title = new Label("CR Tasks of Project - " + project.getName());
        title.setSizeUndefined();
        title.addStyleName(ValoTheme.LABEL_H1);
        title.addStyleName(ValoTheme.LABEL_NO_MARGIN);
        header.addComponent(title);

        viewCRTasksLayout.addComponent(header);

        /*Label name = new Label("Project Name :   " + project.getName());
        viewCRTasksLayout.addComponent(name);
        Label clientName = new Label("Client Name :   " + project.getClientName());
        viewCRTasksLayout.addComponent(clientName);
        Label description = new Label("Description :   " + project.getDescription());
        viewCRTasksLayout.addComponent(description);
        Label startDate = new Label("Start Date :   " + project.getStartDate());
        viewCRTasksLayout.addComponent(startDate);
        Label deliveredDate = new Label("Delivered Date :   " + project.getDeliveredDate());
        viewCRTasksLayout.addComponent(deliveredDate);*/


            viewCRTasksLayout.addComponent(buildToolbar());

            tasksTable = new Table("");
            tasksTable.addStyleName(ValoTheme.TABLE_NO_HORIZONTAL_LINES);
            tasksTable.addStyleName(ValoTheme.TABLE_COMPACT);
            tasksTable.setSelectable(true);

            tasksTable.addContainerProperty("Index", Integer.class, null);
            tasksTable.addContainerProperty("Name", String.class, null);
            tasksTable.addContainerProperty("UserStory Name", String.class, null);
            tasksTable.addContainerProperty("Priority", Integer.class, null);
            tasksTable.addContainerProperty("Severity", Integer.class, null);
            tasksTable.addContainerProperty("Technical Skills", String.class, null);
            tasksTable.addContainerProperty("Estimate Time", String.class, null);
            tasksTable.addContainerProperty("Assigned To", String.class, null);
            tasksTable.addContainerProperty("Complete Time", String.class, null);
            tasksTable.addContainerProperty("Start Time", String.class, null);

            tasksTable.addContainerProperty("View Task", Button.class, null);
            tasksTable.addContainerProperty("Edit Task", Button.class, null);

            if (userRole.equals("admin") || userRole.equals("pm") || userRole.equals("architect")) {
                tasksTable.addContainerProperty("Remove Task", Button.class, null);
                tasksTable.addContainerProperty("Process CR", Button.class, null);
                tasksTable.addContainerProperty("Allocate Member", Button.class, null);
            }

        tasksTable.setSizeFull();

        int index = 0;
        for (Task task : crTasksToDisplay) {
            index++;


            if (userRole.equals("admin") || userRole.equals("pm") || userRole.equals("architect")) {
                Button removeTaskButton = new Button("Remove Task");
                Button editTaskButton = new Button("Edit Task");
                Button viewTaskButton = new Button("View Task");
                final Button processCRTaskButton = new Button("Process CR");
                final Button allocateMemberButton = new Button("Allocate Member");

                removeTaskButton.setData(task);
                editTaskButton.setData(task);
                viewTaskButton.setData(project.getName() + "/" + task.getUserStory().getName() + "/" + task.getTaskId());
                processCRTaskButton.setData(task);
                allocateMemberButton.setData(task.getUserStory().getName() + "/" + task.getName());

                if (task.isProcessed() && task.getAssignedTo() == null) {
                    tasksTable.addItem(new Object[]{index, task.getName(), task.getUserStory().getName(), task.getPriority(), task.getSeverity(), task.getTechnicalSkill(), task.getEstimateTime(), task.getAssignedTo(), task.getCompleteTime(), task.getStartTime(), viewTaskButton, editTaskButton, removeTaskButton, processCRTaskButton, allocateMemberButton}, index);
                    processCRTaskButton.setEnabled(false);

                } else if (task.isProcessed() && task.getAssignedTo() != null) {
                    tasksTable.addItem(new Object[]{index, task.getName(), task.getUserStory().getName(), task.getPriority(), task.getSeverity(), task.getTechnicalSkill(), task.getEstimateTime(), task.getAssignedTo(), task.getCompleteTime(), task.getStartTime(), viewTaskButton, editTaskButton, removeTaskButton, processCRTaskButton, allocateMemberButton}, index);
                    allocateMemberButton.setEnabled(false);
                    processCRTaskButton.setEnabled(false);

                } else {
                    tasksTable.addItem(new Object[]{index, task.getName(), task.getUserStory().getName(), task.getPriority(), task.getSeverity(), task.getTechnicalSkill(), task.getEstimateTime(), task.getAssignedTo(), task.getCompleteTime(), task.getStartTime(), viewTaskButton, editTaskButton, removeTaskButton, processCRTaskButton, allocateMemberButton}, index);
                    allocateMemberButton.setEnabled(false);
                }

                removeTaskButton.addClickListener(new Button.ClickListener() {
                    public void buttonClick(Button.ClickEvent event) {

                        final Task task = (Task) event.getButton().getData();
                        ConfirmDialog.show(DashboardUI.getCurrent(), "Please Confirm:", "Are you sure you want to delete task named :" + task.getName(),
                                "I am", "Not quite", new ConfirmDialog.Listener() {

                                    public void onClose(ConfirmDialog dialog) {
                                        if (dialog.isConfirmed()) {
                                            // Confirmed to continue
                                            TaskDAO taskDAO = (TaskDAO) DashboardUI.context.getBean("Task");


                                            //remove dependency
                                            String dependencyNameList = task.getDependancy();

                                            if (dependencyNameList != null && !dependencyNameList.isEmpty())
                                                for (String taskName : dependencyNameList.split(",")) {
                                                    Task tempTask = taskDAO.getTaskFromUserStroyNameAndTaskName(userStory.getName(), taskName);

                                                    tempTask.setPreRequisits(tempTask.getPreRequisits().replace(task.getName(), ""));

                                                    if (tempTask.getPreRequisits() != null && !tempTask.getPreRequisits().isEmpty()) {
                                                        if (tempTask.getPreRequisits().startsWith(",,")) {
                                                            tempTask.setPreRequisits(tempTask.getDependancy().substring(1, tempTask.getDependancy().length()));
                                                        } else if (tempTask.getPreRequisits().contains(",,")) {
                                                            tempTask.setPreRequisits(tempTask.getPreRequisits().replace(",,", ","));
                                                        } else if (tempTask.getPreRequisits().endsWith(",")) {
                                                            tempTask.setPreRequisits(tempTask.getPreRequisits().substring(0, tempTask.getPreRequisits().length() - 1));
                                                        }

                                                        if (tempTask.getPreRequisits().isEmpty())
                                                            tempTask.setPreRequisits(null);

                                                    }


                                                    taskDAO.updateTask(tempTask);
                                                }


                                            //remove prerequist
                                            String prerequiestNameList = task.getPreRequisits();

                                            if (prerequiestNameList != null && !prerequiestNameList.isEmpty())
                                                for (String taskName : prerequiestNameList.split(",")) {
                                                    Task tempTask = taskDAO.getTaskFromUserStroyNameAndTaskName(userStory.getName(), taskName);

                                                    tempTask.setDependancy(tempTask.getDependancy().replace(task.getName(), ""));

                                                    if (tempTask.getDependancy() != null && !tempTask.getDependancy().isEmpty()) {

                                                        if (tempTask.getDependancy().startsWith(",")) {
                                                            tempTask.setPreRequisits(tempTask.getDependancy().substring(1, tempTask.getDependancy().length()));
                                                        } else if (tempTask.getDependancy().contains(",,")) {
                                                            tempTask.setDependancy(tempTask.getDependancy().replace(",,", ","));
                                                        } else if (tempTask.getDependancy().endsWith(",")) {
                                                            tempTask.setDependancy(tempTask.getDependancy().substring(0, tempTask.getDependancy().length() - 1));
                                                        }

                                                        if (tempTask.getDependancy().isEmpty())
                                                            tempTask.setDependancy(null);
                                                    }

                                                    taskDAO.updateTask(tempTask);
                                                }


                                            taskDAO.removeTask(task);
                                            Page.getCurrent().reload();

                                        } else {
                                            // User did not confirm

                                        }
                                    }
                                });

                    }
                });

                editTaskButton.addClickListener(new Button.ClickListener() {
                    public void buttonClick(Button.ClickEvent event) {

                        TaskWindow.open((Task) event.getButton().getData());

                    }
                });

                viewTaskButton.addClickListener(new Button.ClickListener() {
                    public void buttonClick(Button.ClickEvent event) {

                        DashboardUI.getCurrent().getNavigator().navigateTo("CR_Handle/" + (String) event.getButton().getData());

                    }
                });


                processCRTaskButton.addClickListener(new Button.ClickListener() {
                    public void buttonClick(Button.ClickEvent event) {

                        allocateMemberButton.setEnabled(true);
                        processCRTaskButton.setEnabled(false);
                        CRProcessing crProcessing = new CRProcessing();
                        crProcessing.processCR(project, (Task) event.getButton().getData());
                        Page.getCurrent().reload();

                        //DashboardUI.getCurrent().getNavigator().navigateTo("CR_Handle/" + (String) event.getButton().getData());

                    }
                });

                allocateMemberButton.addClickListener(new Button.ClickListener() {
                    public void buttonClick(Button.ClickEvent event) {

                        allocateMemberButton.setEnabled(false);
                        DashboardUI.getCurrent().getNavigator().navigateTo("CR_Handle/AllocateMember/" + (String) event.getButton().getData());

                        //CRMemberAllocation crMemberAllocation = new CRMemberAllocation();
                        //crMemberAllocation.checkAvailabilityOfMembers(project, (Task) event.getButton().getData());

                        //DashboardUI.getCurrent().getNavigator().navigateTo("CR_Handle/" + (String) event.getButton().getData());

                    }
                });


            } else {

                Button editTaskButton = new Button("Edit Task");
                Button viewTaskButton = new Button("View Task");

                editTaskButton.setData(task);
                viewTaskButton.setData(project.getName() + "/" + task.getUserStory().getName() + "/" + task.getTaskId());

                tasksTable.addItem(new Object[]{index, task.getName(), task.getUserStory().getUserStoryId(), task.getPriority(), task.getSeverity(), task.getTechnicalSkill(), task.getEstimateTime(), task.getAssignedTo(), task.getCompleteTime(), task.getStartTime(), editTaskButton, viewTaskButton}, index);

                editTaskButton.addClickListener(new Button.ClickListener() {
                    public void buttonClick(Button.ClickEvent event) {

                        TaskWindow.open((Task) event.getButton().getData());

                    }
                });

                viewTaskButton.addClickListener(new Button.ClickListener() {
                    public void buttonClick(Button.ClickEvent event) {
                        DashboardUI.getCurrent().getNavigator().navigateTo("CR_Handle/" + (String) event.getButton().getData());

                    }
                });
            }

        }

        viewCRTasksLayout.addComponent(tasksTable);
        viewCRTasksLayout.setExpandRatio(tasksTable, 1);

    }


    private Component buildToolbar() {
        HorizontalLayout header = new HorizontalLayout();
        header.addStyleName("viewheader");
        header.setSpacing(true);
        Responsive.makeResponsive(header);

        Label title = new Label("Task List");
        title.setSizeUndefined();
        title.addStyleName(ValoTheme.LABEL_H2);
        title.addStyleName(ValoTheme.LABEL_NO_MARGIN);
        header.addComponent(title);

        Label middle = new Label("");
        middle.setSizeUndefined();
        middle.addStyleName(ValoTheme.LABEL_H2);
        middle.addStyleName(ValoTheme.LABEL_NO_MARGIN);
        header.addComponent(middle);

        HorizontalLayout tools = new HorizontalLayout(buildFilter());
        tools.setSpacing(true);
        tools.addStyleName("toolbar");
        header.addComponent(tools);

        return header;
    }

    private Component buildFilter() {
        final TextField filter = new TextField();
        filter.addTextChangeListener(new FieldEvents.TextChangeListener() {
            @Override
            public void textChange(final FieldEvents.TextChangeEvent event) {
                Container.Filterable data = (Container.Filterable) tasksTable.getContainerDataSource();
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
                                || filterByProperty("Name", item,
                                event.getText());

                    }

                    @Override
                    public boolean appliesToProperty(final Object propertyId) {
                        if (propertyId.equals("Index")
                                || propertyId.equals("Name")) {
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
                ((com.vaadin.data.Container.Filterable) tasksTable.getContainerDataSource())
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
