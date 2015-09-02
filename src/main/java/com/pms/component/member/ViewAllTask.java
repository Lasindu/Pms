package com.pms.component.member;

import com.pms.DashboardUI;
import com.pms.component.ProjectWindow;
import com.pms.dao.ProjectDAO;
import com.pms.dao.TaskDAO;
import com.pms.dao.UserDAO;
import com.pms.domain.Project;
import com.pms.domain.Task;
import com.pms.domain.User;
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
import org.vaadin.dialogs.ConfirmDialog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Created by lasindu on 7/11/2015.
 */
public class ViewAllTask {
    public VerticalLayout viewTaskLayout;
    private Table viewTaskTable;
    private Button create;
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


        User user = (User) VaadinSession.getCurrent().getAttribute(
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
        viewTaskTable.addContainerProperty("Name",  String.class, null);
        viewTaskTable.addContainerProperty("Complete Time", String.class, null);
        viewTaskTable.addContainerProperty("Estimate time", String.class, null);
        viewTaskTable.addContainerProperty("Member Type", String.class, null);
        viewTaskTable.addContainerProperty("Assign To", Select.class, null);
        viewTaskTable.addContainerProperty("Save Task", Button.class, null);

        viewTaskTable.setSizeFull();



        TaskDAO taskDAO = (TaskDAO) DashboardUI.context.getBean("Task");
        List<Task> projectList = new ArrayList();
        projectList = taskDAO.getAllTasks();

        for(int x=0;x<projectList.size();x++)
        {
            int index=x+1;

            if (userRole.equals("admin")||userRole.equals("pm"))
            {
                Button saveTaskButton=new Button("Save Member");
                saveTaskButton.setData(projectList.get(x));
                // Not developed algorithm yet for getting member
                memberName = new Select("Select Member");
                memberName.addItem("Member 1");
                memberName.addItem("Member 2");
                memberName.addItem("Member 3");
                memberName.addItem("Member 4");

                viewTaskTable.addItem(new Object[] {index,projectList.get(x).getName(),projectList.get(x).getCompleteTime(),projectList.get(x).getEstimateTime(),projectList.get(x).getMemberType(),memberName,saveTaskButton},index);

                saveTaskButton.addClickListener(new Button.ClickListener() {
                    public void buttonClick(Button.ClickEvent event) {

                    //save member query

                    }
                });
            }
        }



        viewTaskLayout.addComponent(viewTaskTable);
        viewTaskLayout.setExpandRatio(viewTaskTable,1);

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

        final Button createNewProjectButton = new Button("View Assigned Task");
        createNewProjectButton.addClickListener(new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {

            }
        });


        return createNewProjectButton;
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
