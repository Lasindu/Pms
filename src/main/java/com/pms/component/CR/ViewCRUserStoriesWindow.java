package com.pms.component.CR;

import com.pms.DashboardUI;
import com.pms.component.UserStoryWindow;
import com.pms.dao.ProjectDAO;
import com.pms.dao.UserStoryDAO;
import com.pms.domain.Project;
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

import java.util.Collection;

/**
 * Created by Amali on 21/11/2015.
 */
public class ViewCRUserStoriesWindow extends CustomComponent {

    public VerticalLayout viewCRUserStoryLayout;
    private Table userStoryTable;
    private String userRole;
    private String projectName;
    private Project project;

    public ViewCRUserStoriesWindow(String projectName) {
        this.projectName = projectName;
        buildViewCRUserStory();
    }

    public Component getCRUserStories() {
        return viewCRUserStoryLayout;
    }

    private void buildViewCRUserStory() {
        User user = (User) VaadinSession.getCurrent().getAttribute(
                User.class.getName());
        userRole = user.getRole();


        viewCRUserStoryLayout = new VerticalLayout();
        viewCRUserStoryLayout.setMargin(true);
        viewCRUserStoryLayout.setSpacing(true);
        viewCRUserStoryLayout.setSizeFull();

        ProjectDAO projectDAO = (ProjectDAO) DashboardUI.context.getBean("Project");
        project = projectDAO.getProjectFromProjectName(projectName);
        UserStoryDAO userStoryDAO = (UserStoryDAO) DashboardUI.context.getBean("UserStory");

        HorizontalLayout header = new HorizontalLayout();
        header.addStyleName("viewheader");
        header.setSpacing(true);
        Responsive.makeResponsive(header);

        Label title = new Label("CR User Stories of Project - " + project.getName());
        title.setSizeUndefined();
        title.addStyleName(ValoTheme.LABEL_H1);
        title.addStyleName(ValoTheme.LABEL_NO_MARGIN);
        header.addComponent(title);

        viewCRUserStoryLayout.addComponent(header);

        /*Label name = new Label("Project Name :   " + project.getName());
        viewCRUserStoryLayout.addComponent(name);
        Label clientName = new Label("Client Name :   " + project.getClientName());
        viewCRUserStoryLayout.addComponent(clientName);
        Label description = new Label("Description :   " + project.getDescription());
        viewCRUserStoryLayout.addComponent(description);
        Label startDate = new Label("Start Date :   " + project.getStartDate());
        viewCRUserStoryLayout.addComponent(startDate);
        Label deliveredDate = new Label("Delivered Date :   " + project.getDeliveredDate());
        viewCRUserStoryLayout.addComponent(deliveredDate);*/

        viewCRUserStoryLayout.addComponent(buildToolbar());

        userStoryTable = new Table("");
        userStoryTable.addStyleName(ValoTheme.TABLE_NO_HORIZONTAL_LINES);
        userStoryTable.addStyleName(ValoTheme.TABLE_COMPACT);
        userStoryTable.setSelectable(true);

        userStoryTable.addContainerProperty("Index", Integer.class, null);
        userStoryTable.addContainerProperty("User Story Name", String.class, null);
        userStoryTable.addContainerProperty("Priority", Integer.class, null);
        userStoryTable.addContainerProperty("Description", String.class, null);
        userStoryTable.addContainerProperty("Domain", String.class, null);
        userStoryTable.addContainerProperty("Assigned Sprint", Integer.class, null);

        userStoryTable.addContainerProperty("View User Story", Button.class, null);

        if (userRole.equals("admin") || userRole.equals("pm") || userRole.equals("architect")) {
            userStoryTable.addContainerProperty("Remove User Story", Button.class, null);
            userStoryTable.addContainerProperty("Edit User Story", Button.class, null);
            userStoryTable.addContainerProperty("Process CR", Button.class, null);
        }


        userStoryTable.setSizeFull();

        Collection<UserStory> crUserStories = userStoryDAO.getCRUserStoryList(project);

        int index = 0;
        for (final UserStory userStory : crUserStories) {
            index++;

            if (userRole.equals("admin") || userRole.equals("pm") || userRole.equals("architect")) {
                Button removeUserStoryButton = new Button("Remove UserStory");
                Button editUserStoryButton = new Button("Edit UserStory");
                Button viewUserStoryButton = new Button("View UserStory");
                Button processCRUserStoryButton = new Button("Process CR");

                removeUserStoryButton.setData(userStory);
                viewUserStoryButton.setData(userStory.getProject().getName() + "/" + userStory.getName());
                editUserStoryButton.setData(userStory);
                //processCRUserStoryButton.setData();

                userStoryTable.addItem(new Object[]{index, userStory.getName(), userStory.getPriority(), userStory.getDescription(), userStory.getDomain(), userStory.getAssignedSprint(),viewUserStoryButton, removeUserStoryButton, editUserStoryButton,processCRUserStoryButton}, index);


                removeUserStoryButton.addClickListener(new Button.ClickListener() {
                    public void buttonClick(Button.ClickEvent event) {

                        final UserStory userStory = (UserStory) event.getButton().getData();
                        ConfirmDialog.show(DashboardUI.getCurrent(), "Please Confirm:", "Are you sure you want to delete userStory named :" + userStory.getName(),
                                "I am", "Not quite", new ConfirmDialog.Listener() {

                                    public void onClose(ConfirmDialog dialog) {
                                        if (dialog.isConfirmed()) {

                                            // Confirmed to continue

                                            UserStoryDAO userStoryDAO = (UserStoryDAO) DashboardUI.context.getBean("UserStory");


                                            //remove dependency
                                            String dependencyNameList = userStory.getDependancy();

                                            if (dependencyNameList != null && !dependencyNameList.isEmpty())
                                                for (String userStoryName : dependencyNameList.split(",")) {
                                                    UserStory tempUserStory = userStoryDAO.getUserStoryFormProjectNameAndUserStoryName(userStory.getProject().getName(), userStoryName);

                                                    tempUserStory.setPreRequisits(tempUserStory.getPreRequisits().replace(userStory.getName(), ""));

                                                    if (tempUserStory.getPreRequisits() != null && !tempUserStory.getPreRequisits().isEmpty()) {
                                                        if (tempUserStory.getPreRequisits().startsWith(",")) {
                                                            tempUserStory.setPreRequisits(tempUserStory.getDependancy().substring(1, tempUserStory.getDependancy().length()));
                                                        } else if (tempUserStory.getPreRequisits().contains(",,")) {
                                                            tempUserStory.setPreRequisits(tempUserStory.getPreRequisits().replace(",,", ","));
                                                        } else if (tempUserStory.getPreRequisits().endsWith(",")) {
                                                            tempUserStory.setPreRequisits(tempUserStory.getPreRequisits().substring(0, tempUserStory.getPreRequisits().length() - 1));
                                                        }

                                                        if (tempUserStory.getPreRequisits().isEmpty())
                                                            tempUserStory.setPreRequisits(null);

                                                    }


                                                    userStoryDAO.updateUserStory(tempUserStory);
                                                }

                                            //remove prerequist
                                            String prerequiestNameList = userStory.getPreRequisits();

                                            if (prerequiestNameList != null && !prerequiestNameList.isEmpty())
                                                for (String userStoryName : prerequiestNameList.split(",")) {
                                                    UserStory tempUserStory = userStoryDAO.getUserStoryFormProjectNameAndUserStoryName(userStory.getProject().getName(), userStoryName);

                                                    tempUserStory.setDependancy(tempUserStory.getDependancy().replace(userStory.getName(), ""));

                                                    if (tempUserStory.getDependancy() != null && !tempUserStory.getDependancy().isEmpty()) {

                                                        if (tempUserStory.getDependancy().startsWith(",")) {
                                                            tempUserStory.setPreRequisits(tempUserStory.getDependancy().substring(1, tempUserStory.getDependancy().length()));
                                                        } else if (tempUserStory.getDependancy().contains(",,")) {
                                                            tempUserStory.setDependancy(tempUserStory.getDependancy().replace(",,", ","));
                                                        } else if (tempUserStory.getDependancy().endsWith(",")) {
                                                            tempUserStory.setDependancy(tempUserStory.getDependancy().substring(0, tempUserStory.getDependancy().length() - 1));
                                                        }

                                                        if (tempUserStory.getDependancy().isEmpty())
                                                            tempUserStory.setDependancy(null);
                                                    }

                                                    userStoryDAO.updateUserStory(tempUserStory);
                                                }


                                            userStoryDAO.removeUserStory(userStory);
                                            Page.getCurrent().reload();

                                        } else {
                                            // User did not confirm

                                        }
                                    }
                                });

                    }
                });


                editUserStoryButton.addClickListener(new Button.ClickListener() {
                    public void buttonClick(Button.ClickEvent event) {

                        UserStoryWindow.open((UserStory) event.getButton().getData());

                    }
                });

                viewUserStoryButton.addClickListener(new Button.ClickListener() {
                    public void buttonClick(Button.ClickEvent event) {

                        DashboardUI.getCurrent().getNavigator().navigateTo("CR_Handle/" + (String) event.getButton().getData());

                    }
                });

                processCRUserStoryButton.addClickListener(new Button.ClickListener() {
                    public void buttonClick(Button.ClickEvent event) {

                        //DashboardUI.getCurrent().getNavigator().navigateTo("CR_Handle/" + (String) event.getButton().getData());

                    }
                });


            } else {
                Button viewUserStoryButton = new Button("View UserStory");
                viewUserStoryButton.setData(userStory.getProject().getName() + "/" + userStory.getName());


                userStoryTable.addItem(new Object[]{index, userStory.getName(), userStory.getPriority(), userStory.getDescription(), userStory.getDomain(), userStory.getAssignedSprint(), viewUserStoryButton}, index);


                viewUserStoryButton.addClickListener(new Button.ClickListener() {
                    public void buttonClick(Button.ClickEvent event) {

                        DashboardUI.getCurrent().getNavigator().navigateTo("CR_Handle/" + (String) event.getButton().getData());

                    }
                });

            }


        }

        viewCRUserStoryLayout.addComponent(userStoryTable);
        viewCRUserStoryLayout.setExpandRatio(userStoryTable, 1);

    }


    private Component buildToolbar() {
        HorizontalLayout header = new HorizontalLayout();
        header.addStyleName("viewheader");
        header.setSpacing(true);
        Responsive.makeResponsive(header);

        Label title = new Label("UserStory List");
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
                Container.Filterable data = (Container.Filterable) userStoryTable.getContainerDataSource();
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
                                || filterByProperty("User Story Name", item,
                                event.getText());

                    }

                    @Override
                    public boolean appliesToProperty(final Object propertyId) {
                        if (propertyId.equals("Index")
                                || propertyId.equals("User Story Name")) {
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
                ((com.vaadin.data.Container.Filterable) userStoryTable.getContainerDataSource())
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
