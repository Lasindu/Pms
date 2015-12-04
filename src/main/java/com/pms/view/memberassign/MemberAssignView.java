package com.pms.view.memberassign;

import com.pms.component.ViewAllProjects;
import com.pms.component.member.ViewAllAssignedTask;
import com.pms.component.member.ViewAllMember;
import com.pms.component.member.ViewAllTask;
import com.pms.component.member.ViewMember;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

/**
 * Created by Lasindu on 4/1/2015.
 */
public class MemberAssignView extends CssLayout implements View {

    private VerticalLayout mainLayout;
    private Panel mainPanel;

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {
        if(viewChangeEvent.getParameters() != null){
            // split at "/", add each part as a label
            String[] msgs = viewChangeEvent.getParameters().split("/");

            if(msgs.length==1)
            {
                if(msgs[0].equals(""))
                {
                    mainLayout.removeAllComponents();
                    ViewAllTask viewAllTasks=new ViewAllTask();
                    mainLayout.addComponent(viewAllTasks.getAllTasks());

                }
                else
                {
                    mainLayout.removeAllComponents();
                    String memberName=msgs[0].replace("%20", " ");
                    ViewAllAssignedTask assignedTasks=new ViewAllAssignedTask(memberName);
                    mainLayout.addComponent(assignedTasks.getAssignedTask());
                }
            }
        }
    }

    public MemberAssignView()
    {
        addStyleName(ValoTheme.PANEL_BORDERLESS);
        setSizeFull();

        mainPanel= new Panel();
        mainPanel.setSizeFull();
        addComponent(mainPanel);

        mainLayout= new VerticalLayout();
        //mainLayout.setSizeFull();
        mainPanel.setContent(mainLayout);

        ViewAllTask viewAllTasks=new ViewAllTask();
        mainLayout.addComponent(viewAllTasks.getAllTasks());
    }

}
