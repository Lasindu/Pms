package com.pms.component.member;

import com.pms.DashboardUI;
import com.pms.dao.ProjectDAO;
import com.pms.dao.UserDAO;
import com.pms.domain.Project;
import com.pms.domain.User;
import com.vaadin.server.Responsive;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

/**
 * Created by sandun on 8/15/2015.
 */
public class ViewMember extends CustomComponent {

    private String userRole;
    private Table userStoryTable;
    private String userName;
    private Button createUserStory;
    private User user;
    public VerticalLayout viewProjectLayout;

    public ViewMember(String uName)
    {
        this.userName=uName;
        buildViewMember();

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
        UserDAO userDAO = (UserDAO) DashboardUI.context.getBean("User");
        user = userDAO.loadUserDetails(userName);


        HorizontalLayout header = new HorizontalLayout();
        header.addStyleName("viewheader");
        header.setSpacing(true);
        Responsive.makeResponsive(header);

        Label title = new Label(user.getFirstName()+" "+user.getLastName());
        title.setSizeUndefined();
        title.addStyleName(ValoTheme.LABEL_H1);
        title.addStyleName(ValoTheme.LABEL_NO_MARGIN);
        header.addComponent(title);

        viewProjectLayout.addComponent(header);

        Label deliveredDate=new Label("Username :   "+user.getUserName());
        viewProjectLayout.addComponent(deliveredDate);
        Label name=new Label("Email :   "+user.getEmail());
        viewProjectLayout.addComponent(name);
        Label clientName= new Label("Mobile :   "+user.getPhone());
        viewProjectLayout.addComponent(clientName);
        Label description=new Label("Address :   "+user.getLocation());
        viewProjectLayout.addComponent(description);
        Label startDate=new Label("Role :   "+user.getRole());
        viewProjectLayout.addComponent(startDate);

    }

}
