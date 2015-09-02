package com.pms.component.member;

import com.pms.DashboardUI;
import com.pms.component.ProjectWindow;
import com.pms.dao.ProjectDAO;
import com.pms.dao.UserDAO;
import com.pms.domain.Project;
import com.pms.domain.User;
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
import java.util.Iterator;
import java.util.List;

/**
 * Created by Lasindu on 7/4/2015.
 */
public class ViewAllMember {
    public VerticalLayout viewMemberLayout;
    private Table viewMemberTable;
    private Button create;
    private String userRole;

    public ViewAllMember()
    {
        buildViewAllMembers();

    }
    public Component getAllMembers()
    {
        return viewMemberLayout;
    }
    private void buildViewAllMembers()
    {
        User user = (User) VaadinSession.getCurrent().getAttribute(
                User.class.getName());

        userRole=user.getRole();

        viewMemberLayout = new VerticalLayout();
        //viewProjectLayout.setCaption("View Project");
        viewMemberLayout.setMargin(true);
        viewMemberLayout.setSpacing(true);
        viewMemberLayout.setSizeFull();

        viewMemberLayout.addComponent(buildToolbar());

        //Member table
        viewMemberTable= new Table("");
        viewMemberTable.addStyleName(ValoTheme.TABLE_NO_HORIZONTAL_LINES);
        viewMemberTable.addStyleName(ValoTheme.TABLE_COMPACT);
        viewMemberTable.setSelectable(true);

        viewMemberTable.addContainerProperty("Index", Integer.class, null);
        viewMemberTable.addContainerProperty("Username",  String.class, null);
        viewMemberTable.addContainerProperty("Email", String.class, null);
        viewMemberTable.addContainerProperty("First name", String.class, null);
        viewMemberTable.addContainerProperty("Last Name", String.class, null);
        viewMemberTable.addContainerProperty("Location", String.class, null);
        viewMemberTable.addContainerProperty("Phone Number", String.class, null);
        viewMemberTable.addContainerProperty("Role", String.class, null);

        if(userRole.equals("admin")||userRole.equals("pm"))
        {
            viewMemberTable.addContainerProperty("Remove Member", Button.class, null);
            viewMemberTable.addContainerProperty("Edit Member", Button.class, null);
        }

        viewMemberTable.addContainerProperty("View Member", Button.class, null);
        viewMemberTable.setSizeFull();

        //List<User> projectList = new ArrayList();
        UserDAO userDAO = (UserDAO) DashboardUI.context.getBean("User");
        //used session user to get the user projects
        List<User> memberList = new ArrayList();
        //User memberLoadedUser= userDAO.loadMembers();
        memberList = userDAO.loadMembers();
        //System.out.print(memberLoadedUser);
        //memberList.addAll(memberLoadedUser.getProjects());

        for(int x=0;x<memberList.size();x++)
        {
            int index=x+1;

            if (userRole.equals("admin")||userRole.equals("pm"))
            {
                Button removeProjectButton=new Button("Remove Member");
                Button editProjectButton=new Button("Edit Member");
                Button viewMemberButton=new Button("View Member");
                removeProjectButton.setData(memberList.get(x));
                editProjectButton.setData(memberList.get(x));
                viewMemberButton.setData(memberList.get(x).getUserName());

                viewMemberTable.addItem(new Object[] {index,memberList.get(x).getUserName(),memberList.get(x).getEmail(),memberList.get(x).getFirstName(),memberList.get(x).getLastName(),memberList.get(x).getLocation(),memberList.get(x).getPhone(),memberList.get(x).getRole(),removeProjectButton,editProjectButton,viewMemberButton},index);

                removeProjectButton.addClickListener(new Button.ClickListener() {
                    public void buttonClick(Button.ClickEvent event) {
                        /*
                        final Project project=(Project)event.getButton().getData();

                        ConfirmDialog.show(DashboardUI.getCurrent(), "Please Confirm:", "Are you sure you want to delete Member named :" + project.getName(),
                                "I am", "Not quite", new ConfirmDialog.Listener() {

                                    public void onClose(ConfirmDialog dialog) {
                                        if (dialog.isConfirmed()) {
                                            // Confirmed to continue


                                            //remove project form all users // this is only way to remove many to many mapping

                                            ProjectDAO projectDAO = (ProjectDAO) DashboardUI.context.getBean("Project");

                                            //have to load project users because of lazy retrival object then while creating new project it will not add to local session
                                            //so if delete project when same session project creation then it will give exeption to solve that need to load project users
                                            Project usersLoadedProject = projectDAO.loadProjectUsers(project);


                                            Collection<User> users = usersLoadedProject.getUsers();


                                            UserDAO userDAO = (UserDAO) DashboardUI.context.getBean("User");
                                            Iterator iter = users.iterator();
                                            while (iter.hasNext()) {
                                                User user = (User) iter.next();
                                                user.getProjects().remove(usersLoadedProject);
                                                userDAO.updateUser(user);
                                            }


                                            projectDAO.removeProject(project);
                                            Page.getCurrent().reload();

                                        } else {
                                            // User did not confirm

                                        }
                                    }
                                });
                        */
                    }
                });


                editProjectButton.addClickListener(new Button.ClickListener() {
                    public void buttonClick(Button.ClickEvent event) {

                        //ProjectWindow.open((Project)event.getButton().getData());


                    }
                });

                viewMemberButton.addClickListener(new Button.ClickListener() {
                    public void buttonClick(Button.ClickEvent event) {
                        System.out.println("res" +Page.getCurrent().getUriFragment());
                        DashboardUI.getCurrent().getNavigator().navigateTo("Member/" + (String) event.getButton().getData());


                    }
                });
            }
            else
            {

                Button viewProjectButton = new Button("View Project");
                viewProjectButton.setData(memberList.get(x).getUserName());


                viewProjectButton.addClickListener(new Button.ClickListener() {
                    public void buttonClick(Button.ClickEvent event) {

                        //DashboardUI.getCurrent().getNavigator().navigateTo("Schedule_Task/" + (String) event.getButton().getData());


                    }
                });

            }


        }

        viewMemberLayout.addComponent(viewMemberTable);
        viewMemberLayout.setExpandRatio(viewMemberTable,1);


    }

    private Component buildToolbar() {
        HorizontalLayout header = new HorizontalLayout();
        header.addStyleName("viewheader");
        header.setSpacing(true);
        Responsive.makeResponsive(header);

        Label title = new Label("Member List");
        title.setSizeUndefined();
        title.addStyleName(ValoTheme.LABEL_H1);
        title.addStyleName(ValoTheme.LABEL_NO_MARGIN);
        header.addComponent(title);


        if(userRole.equals("admin")||userRole.equals("pm")) {
            create = buildCreateReport();
            HorizontalLayout tools = new HorizontalLayout(buildFilter(),
                    create);
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

        final Button createNewProjectButton = new Button("Add New Member");
        createNewProjectButton.addClickListener(new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {
                User user = new User();
                user.setFirstName("");
                MemberWindow.open(user);


            }
        });


        return createNewProjectButton;
    }

    private Component buildFilter() {
        final TextField filter = new TextField();
        filter.addTextChangeListener(new FieldEvents.TextChangeListener() {
            @Override
            public void textChange(final FieldEvents.TextChangeEvent event) {
                Container.Filterable data = (Container.Filterable) viewMemberTable.getContainerDataSource();
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

        filter.setInputPrompt("Filter Member");
        filter.setIcon(FontAwesome.SEARCH);
        filter.addStyleName(ValoTheme.TEXTFIELD_INLINE_ICON);
        filter.addShortcutListener(new ShortcutListener("Clear",
                ShortcutAction.KeyCode.ESCAPE, null) {
            @Override
            public void handleAction(final Object sender, final Object target) {
                filter.setValue("");
                ((com.vaadin.data.Container.Filterable) viewMemberTable.getContainerDataSource())
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
