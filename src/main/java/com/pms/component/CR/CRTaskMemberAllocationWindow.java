package com.pms.component.CR;

import com.pms.DashboardUI;
import com.pms.dao.TaskDAO;
import com.pms.dao.UserDAO;
import com.pms.dao.UserStoryDAO;
import com.pms.domain.Task;
import com.pms.domain.User;
import com.pms.domain.UserStory;
import com.vaadin.data.Container;
import com.vaadin.data.Item;
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

import java.util.*;

/**
 * Created by amali on 17/11/2015.
 */
public class CRTaskMemberAllocationWindow {
    public VerticalLayout viewCRTaskMemberAllocationLayout;
    private Table viewCRTaskTable;
    private Button viewAssignedTask;
    private String userRole;

    private Task selectedCRTask;
    private UserStory selectedUserStory;

    @PropertyId("memberName")
    private Select memberName;

    public CRTaskMemberAllocationWindow(String userStory, String task) {
        TaskDAO taskDAO = (TaskDAO) DashboardUI.context.getBean("Task");
        selectedCRTask = taskDAO.getTaskFromUserStroyNameAndTaskName(userStory, task);
        selectedUserStory = selectedCRTask.getUserStory();
        buildViewCRTaskMemberAllocation();
    }

    public Component getCRTaskAllocatedMembers() {
        return viewCRTaskMemberAllocationLayout;
    }

    private void buildViewCRTaskMemberAllocation() {

        final UserDAO userDAO = (UserDAO) DashboardUI.context.getBean("User");
        final TaskDAO taskDAO = (TaskDAO) DashboardUI.context.getBean("Task");

        final User user = (User) VaadinSession.getCurrent().getAttribute(
                User.class.getName());

        userRole = user.getRole();

        viewCRTaskMemberAllocationLayout = new VerticalLayout();
        viewCRTaskMemberAllocationLayout.setMargin(true);
        viewCRTaskMemberAllocationLayout.setSpacing(true);
        viewCRTaskMemberAllocationLayout.setSizeFull();

        viewCRTaskMemberAllocationLayout.addComponent(buildToolbar());

        viewCRTaskTable = new Table("");
        viewCRTaskTable.addStyleName(ValoTheme.TABLE_NO_HORIZONTAL_LINES);
        viewCRTaskTable.addStyleName(ValoTheme.TABLE_COMPACT);
        viewCRTaskTable.setSelectable(true);

        viewCRTaskTable.addContainerProperty("Index", Integer.class, null);
        viewCRTaskTable.addContainerProperty("Task Name", String.class, null);
        viewCRTaskTable.addContainerProperty("Description", String.class, null);
        viewCRTaskTable.addContainerProperty("Date", String.class, null);
        viewCRTaskTable.addContainerProperty("Technical Skills", String.class, null);
        viewCRTaskTable.addContainerProperty("Assign To", Select.class, null);
        viewCRTaskTable.addContainerProperty("Save Task", Button.class, null);

        viewCRTaskTable.setSizeFull();

        int index = 1;

        if (userRole.equals("admin") || userRole.equals("pm")) {
            final Button saveCRTaskButton = new Button("Save Member");
            saveCRTaskButton.setData(selectedCRTask);

            memberName = new Select("Select Member");
            memberName.setNullSelectionAllowed(false);

            List<User> userList = checkAvailabilityOfMembers(selectedCRTask);

            if (selectedCRTask.getAssignedTo() == null)
                memberName.addItem("");
            else
                memberName.addItem(selectedCRTask.getAssignedTo());

            for (int i = 0; i < userList.size(); i++) {
                memberName.addItem(userList.get(i).getUserName());
            }

            if (userList.size() > 0) {
                memberName.setValue(memberName.getItemIds().iterator().next());
            }
            if (selectedCRTask.getAssignedTo() != null) {
                memberName.setValue(memberName.getItemIds().iterator().next());
            }

            viewCRTaskTable.addItem(new Object[]{index, selectedCRTask.getName(), selectedCRTask.getDescription(), selectedCRTask.getDate(), selectedCRTask.getTechnicalSkill(), memberName, saveCRTaskButton}, index);

            saveCRTaskButton.addClickListener(new Button.ClickListener() {
                public void buttonClick(Button.ClickEvent event) {

                    int rowId = (Integer) viewCRTaskTable.firstItemId();
                    Item item = viewCRTaskTable.getItem(rowId);
                    Task task = (Task) event.getButton().getData();
                    String assignedTo = item.getItemProperty("Assign To").getValue().toString().trim().toLowerCase();

                    if (assignedTo.isEmpty()) {
                        Notification.show("Please Select Member", Notification.Type.ERROR_MESSAGE);
                    } else {

                        task.setAssignedTo(assignedTo);
                        task.setIsAssigned("1");
                        taskDAO.updateTask(task);

                        Page.getCurrent().reload();
                    }

                }
            });
        }

        viewCRTaskMemberAllocationLayout.addComponent(viewCRTaskTable);
        viewCRTaskMemberAllocationLayout.setExpandRatio(viewCRTaskTable, 1);
        viewCRTaskMemberAllocationLayout.setImmediate(true);

    }

    private Component buildToolbar() {
        HorizontalLayout header = new HorizontalLayout();
        header.addStyleName("viewheader");
        header.setSpacing(true);
        Responsive.makeResponsive(header);

        Label title = new Label("CR Task Member Allocation");
        title.setSizeUndefined();
        title.addStyleName(ValoTheme.LABEL_H1);
        title.addStyleName(ValoTheme.LABEL_NO_MARGIN);
        header.addComponent(title);


        if (userRole.equals("admin") || userRole.equals("pm")) {
            viewAssignedTask = buildCreateReport();
            HorizontalLayout tools = new HorizontalLayout(buildFilter(),
                    viewAssignedTask);
            tools.setSpacing(true);
            tools.addStyleName("toolbar");
            header.addComponent(tools);
        } else {
            HorizontalLayout tools = new HorizontalLayout(buildFilter());
            tools.setSpacing(true);
            tools.addStyleName("toolbar");
            header.addComponent(tools);

        }

        return header;
    }

    public List<User> checkAvailabilityOfMembers(Task taskToFindMembers) {

        final UserStoryDAO userStoryDAO = (UserStoryDAO) DashboardUI.context.getBean("UserStory");
        final UserDAO userDAO = (UserDAO) DashboardUI.context.getBean("User");
        final TaskDAO taskDAO = (TaskDAO) DashboardUI.context.getBean("Task");

        List<User> matchedMembers;

        UserStory crTaskUserStory = taskToFindMembers.getUserStory();

        Collection<Task> updatedAssignedTaskList = userStoryDAO.getUserStoryAssignedTaskList(crTaskUserStory);

        //getting cr start and end time
        int updatedCRTaskStartTime = Integer.parseInt(taskToFindMembers.getStartTime());
        int updatedCRTaskEndTime = updatedCRTaskStartTime + Integer.parseInt(taskToFindMembers.getEstimateTime());

        List<User> workingMembersWithinSprint = new ArrayList<User>();
        List<User> otherAvailableMembersWithinSprint = new ArrayList<User>();

        for (Task task1 : updatedAssignedTaskList) {

            //get task start and end time
            int taskStart = Integer.parseInt(task1.getStartTime());
            int taskEnd = taskStart + Integer.parseInt(task1.getEstimateTime());

            if (((taskStart < updatedCRTaskStartTime) && (taskEnd <= updatedCRTaskStartTime))
                    || ((taskStart >= updatedCRTaskEndTime) && (taskEnd > updatedCRTaskEndTime))) {
                otherAvailableMembersWithinSprint.add(userDAO.loadUserDetails(task1.getAssignedTo()));
            } else {
                workingMembersWithinSprint.add(userDAO.loadUserDetails(task1.getAssignedTo()));
            }
        }

        //remove duplicates in other available users and add those into a map
        HashMap<String, User> otherMemberListWithoutDuplicates = new HashMap<String, User>();

        for (User user : otherAvailableMembersWithinSprint) {
            if (!otherMemberListWithoutDuplicates.containsKey(user.getUserName())) {
                otherMemberListWithoutDuplicates.put(user.getUserName(), user);
            }
        }

        if (workingMembersWithinSprint.size() != 0) {

            //remove duplicates in working users within sprint and add those into a map
            HashMap<String, User> workingMemberListWithoutDuplicates = new HashMap<String, User>();

            for (User user : workingMembersWithinSprint) {
                if (!workingMemberListWithoutDuplicates.containsKey(user.getUserName())) {
                    workingMemberListWithoutDuplicates.put(user.getUserName(), user);
                }
            }

            //remove working members within sprint from the other available list
            for (Map.Entry<String, User> me : workingMemberListWithoutDuplicates.entrySet())
                if (otherMemberListWithoutDuplicates.containsKey(me.getKey())) {
                    otherMemberListWithoutDuplicates.remove(me.getKey());
                }

            matchedMembers = skillsChecker(otherMemberListWithoutDuplicates, taskToFindMembers);

        } else {
            System.out.println("No working members within sprint and all other members are available for doing CR Task");
            matchedMembers = skillsChecker(otherMemberListWithoutDuplicates, taskToFindMembers);
        }

        return matchedMembers;

    }


    public List<User> skillsChecker(HashMap<String, User> availableMembers, Task taskWithoutMember) {

        List<User> skillMatchedMembers = new ArrayList<User>();

        String[] skillList = taskWithoutMember.getTechnicalSkill().toLowerCase().split(",");
        int numSkills = skillList.length;
        int count = 0;

        for (Map.Entry<String, User> entry : availableMembers.entrySet()) {

            for (String skill : skillList) {
                String[] skillListInMember = entry.getValue().getTechnicalSkills().toLowerCase().split(",");
                for (String skill1 : skillListInMember) {
                    if (skill1.contentEquals(skill)) {
                        count++;
                        break;
                    }
                }
            }

            if (count == numSkills) {
                //System.out.println("All skills matched in : " + entry.getValue().getUserName());
                skillMatchedMembers.add(entry.getValue());
            }
            count = 0;
        }

        return skillMatchedMembers;
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

    private Component buildFilter() {
        final TextField filter = new TextField();
        filter.addTextChangeListener(new FieldEvents.TextChangeListener() {
            @Override
            public void textChange(final FieldEvents.TextChangeEvent event) {
                Container.Filterable data = (Container.Filterable) viewCRTaskTable.getContainerDataSource();
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
                ((com.vaadin.data.Container.Filterable) viewCRTaskTable.getContainerDataSource())
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
