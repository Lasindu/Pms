package com.pms.component.member;

import com.pms.DashboardUI;
import com.pms.dao.TaskDAO;
import com.pms.dao.UserDAO;
import com.pms.domain.Task;
import com.pms.domain.User;
import com.vaadin.server.Responsive;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lasindu on 11/10/2015.
 */
public class ViewAssignedTask extends CustomComponent {

    private String userRole;
    private Table myTaskTable;
    private String userName;
    private Button competeTask;
    public VerticalLayout viewProjectLayout;

    public ViewAssignedTask(String uName)
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

        Label deliveredDate=new Label("Username :   ");
        viewProjectLayout.addComponent(deliveredDate);
        Label name=new Label("Email :   ");
        viewProjectLayout.addComponent(name);

        myTaskTable= new Table("");
        //viewProjectTable.addStyleName(ValoTheme.TABLE_BORDERLESS);
        myTaskTable.addStyleName(ValoTheme.TABLE_NO_HORIZONTAL_LINES);
        myTaskTable.addStyleName(ValoTheme.TABLE_COMPACT);
        myTaskTable.setSelectable(true);

        myTaskTable.addContainerProperty("Index", Integer.class, null);
        myTaskTable.addContainerProperty("Task Name",  String.class, null);
        myTaskTable.addContainerProperty("Discription", String.class, null);
        myTaskTable.addContainerProperty("Complete Task", Button.class, null);
        myTaskTable.setSizeFull();

        TaskDAO taskDAO = (TaskDAO) DashboardUI.context.getBean("Task");
        List<Task> taskList = new ArrayList();
        taskList = taskDAO.getAllTasks();

        for(int x=0;x<taskList.size();x++) {
            int index = x + 1;

            Button completeTaskButton=new Button("Complete");
            completeTaskButton.setData(taskList.get(x));

            myTaskTable.addItem(new Object[] {index,taskList.get(x).getName(),taskList.get(x).getDescription(),completeTaskButton},index);

            completeTaskButton.addClickListener(new Button.ClickListener() {
                public void buttonClick(Button.ClickEvent event) {

                    //save member query

                }
            });
        }

        viewProjectLayout.addComponent(myTaskTable);
        viewProjectLayout.setExpandRatio(myTaskTable,1);

    }

}
