package com.pms.component.CR;

import com.pms.DashboardUI;
import com.pms.dao.UserDAO;
import com.pms.dao.UserStoryDAO;
import com.pms.domain.Project;
import com.pms.domain.Task;
import com.pms.domain.User;
import com.pms.domain.UserStory;
import com.vaadin.data.Property;
import com.vaadin.server.Responsive;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Amali on 11/11/2015.
 */
public class ViewCRSelectionWindow extends CustomComponent {

    public VerticalLayout viewInitialCRAddingLayout;
    public List<UserStory> userStories;
    private ArrayList<String> crProperties;
    private String userRole;

    public ViewCRSelectionWindow() {
        buildInitialCRAddingView();
    }

    public Component getInitialCRAddingView() {
        return viewInitialCRAddingLayout;
    }


    private void buildInitialCRAddingView() {

        User user = (User) VaadinSession.getCurrent().getAttribute(
                User.class.getName());

        userRole = user.getRole();
        UserDAO userDAO = (UserDAO) DashboardUI.context.getBean("User");
        //used session user to get the user projects
        final List<Project> projectList = new ArrayList();
        User projectsLoadedUser = userDAO.loadUserProjects(user);
        projectList.addAll(projectsLoadedUser.getProjects());

        viewInitialCRAddingLayout = new VerticalLayout();
        viewInitialCRAddingLayout.setMargin(true);
        viewInitialCRAddingLayout.setSpacing(true);
        viewInitialCRAddingLayout.setSizeFull();

        final Label title = new Label("Change Request Details");
        title.addStyleName(ValoTheme.LABEL_H2);
        title.setSizeFull();
        viewInitialCRAddingLayout.addComponent(title);

        if (userRole.equals("admin") || userRole.equals("pm")) {
            //Hold the formlayout
            final Panel crDetailContainer = new Panel();
            crDetailContainer.addStyleName(ValoTheme.PANEL_BORDERLESS);
            crDetailContainer.addStyleName(ValoTheme.PANEL_WELL);
            crDetailContainer.addStyleName("crPanelStyle");

            //contain type of CR
            final FormLayout crTypeDetails = new FormLayout();
            crTypeDetails.setSizeFull();
            crTypeDetails.setMargin(true);

            final ComboBox crProject = new ComboBox("Select Required Project : ");
            crProject.setTextInputAllowed(false);
            crProject.setRequired(true);

            final OptionGroup crType = new OptionGroup("Select Change Request type you want to see : ");
            crType.addItem("User Story");
            crType.addItem("Task");
            crType.setRequired(true);
            crType.setNullSelectionAllowed(true);

            crTypeDetails.addComponent(crProject);
            crTypeDetails.addComponent(crType);

            crDetailContainer.setContent(crTypeDetails);
            viewInitialCRAddingLayout.addComponent(crDetailContainer);

            final HorizontalLayout bottom = new HorizontalLayout();
            bottom.addStyleName("initialBottomStyle");
            bottom.setWidth("250px");
            crDetailContainer.addStyleName(ValoTheme.LAYOUT_CARD);
            bottom.setSpacing(true);

            final Button confirmCRType = new Button("Confirm");
            final Button cancelCRType = new Button("Clear");
            confirmCRType.addStyleName(ValoTheme.BUTTON_PRIMARY);
            cancelCRType.addStyleName(ValoTheme.BUTTON_PRIMARY);
            bottom.addComponent(confirmCRType);
            bottom.addComponent(cancelCRType);
            viewInitialCRAddingLayout.addComponent(bottom);
            viewInitialCRAddingLayout.setComponentAlignment(bottom,Alignment.MIDDLE_LEFT);

            Responsive.makeResponsive(viewInitialCRAddingLayout,crTypeDetails,crType,confirmCRType,cancelCRType,bottom,crProject,title);

            //show projects which are related to user
            for (int i = 0; i < projectList.size(); i++) {
                crProject.addItem(projectList.get(i).getName());
            }
           //Event listners for adding CR and Cancel
            confirmCRType.addClickListener(new Button.ClickListener() {
                public void buttonClick(Button.ClickEvent event) {

                    if ((crProject.isEmpty() || crType.isEmpty())  == true) {

                        Notification.show("Please Add All the Details to Proceed", Notification.Type.HUMANIZED_MESSAGE);
                    } else {
                        DashboardUI.getCurrent().getNavigator().navigateTo("CR_Handle/" + crProject.getValue()+"/"+crType.getValue());
                    }
                }
            });

            cancelCRType.addClickListener(new Button.ClickListener() {
                public void buttonClick(Button.ClickEvent event) {
                    crProject.clear();
                    crType.clear();
                }
            });
        } else
            viewInitialCRAddingLayout.addComponent(new Label("You are not Authorized User"));
    }
}























