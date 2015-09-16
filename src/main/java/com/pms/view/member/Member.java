package com.pms.view.member;

import com.pms.component.ViewAllProjects;
import com.pms.component.ViewProject;
import com.pms.component.ViewTask;
import com.pms.component.ViewUserStory;
import com.pms.component.member.ViewAllMember;
import com.pms.component.member.ViewMember;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

/**
 * Created by Lasindu on 7/4/2015.
 */
public class Member extends CssLayout implements View  {

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
                    ViewAllMember viewMembers=new ViewAllMember();
                    mainLayout.addComponent(viewMembers.getAllMembers());

                }
                else
                {
                    mainLayout.removeAllComponents();
                    String memberName=msgs[0].replace("%20", " ");
                    ViewMember viewMember=new ViewMember(memberName);
                    mainLayout.addComponent(viewMember.getMember());

                }

            }

        }

    }

    public Member()
    {
        addStyleName(ValoTheme.PANEL_BORDERLESS);
        setSizeFull();

        mainPanel= new Panel();
        mainPanel.setSizeFull();
        addComponent(mainPanel);

        mainLayout= new VerticalLayout();
        //mainLayout.setSizeFull();
        mainPanel.setContent(mainLayout);
        ViewAllMember viewMembers=new ViewAllMember();
        mainLayout.addComponent(viewMembers.getAllMembers());

    }

}
