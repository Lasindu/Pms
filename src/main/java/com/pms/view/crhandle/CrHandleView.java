package com.pms.view.crhandle;

import com.pms.component.CR.CRTaskMemberAllocationWindow;
import com.pms.component.CR.ViewCRSelectionWindow;
import com.pms.component.CR.ViewCRTasksWindow;
import com.pms.component.CR.ViewCRUserStoriesWindow;
import com.pms.component.ViewTask;
import com.pms.component.ViewUserStory;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import java.util.ArrayList;


/**
 * Created by Amali on 4/11/2015.
 */
public class CrHandleView extends VerticalLayout implements View {

    private final VerticalLayout root;
    private ArrayList<String> addedCRDetails = new ArrayList<String>();

    public CrHandleView() {
        addStyleName(ValoTheme.PANEL_BORDERLESS);
        setSizeFull();
        root = new VerticalLayout();
        root.setSizeFull();
        root.setMargin(true);
        root.addStyleName("dashboard-view");
        addComponent(root);

        ViewCRSelectionWindow viewCRView = new ViewCRSelectionWindow();
        root.addComponent(viewCRView.getInitialCRAddingView());

    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {

        if (viewChangeEvent.getParameters() != null) {
            // split at "/", add each part as a label
            String[] msgs = viewChangeEvent.getParameters().split("/");

            if (msgs.length == 1) {
                if (msgs[0].equals("")) {
                    root.removeAllComponents();
                    ViewCRSelectionWindow viewCRView = new ViewCRSelectionWindow();
                    root.addComponent(viewCRView.getInitialCRAddingView());
                }
            }

            if (msgs.length == 2) {
                root.removeAllComponents();
                String projectName = msgs[0].replace("%20", " ");
                String secondName = msgs[1].replace("%20", " ");

                if (secondName.contentEquals("User Story")) {
                    ViewCRUserStoriesWindow crUserStoriesWindow = new ViewCRUserStoriesWindow(projectName);
                    root.addComponent(crUserStoriesWindow.getCRUserStories());
                } else if (secondName.contentEquals("Task")) {
                    ViewCRTasksWindow viewCRTasksWindow = new ViewCRTasksWindow(projectName);
                    root.addComponent(viewCRTasksWindow.getCRTasks());
                } else {
                    String userStoryName = secondName;
                    ViewUserStory viewUserStory = new ViewUserStory(projectName, userStoryName);
                    root.addComponent(viewUserStory.getUserStory());
                }
            }

            if (msgs.length == 3) {

                if (msgs[0].contentEquals("AllocateMember")) {
                    root.removeAllComponents();
                    CRTaskMemberAllocationWindow crTaskMemberAllocationWindow = new CRTaskMemberAllocationWindow(msgs[1], msgs[2]);
                    root.addComponent(crTaskMemberAllocationWindow.getCRTaskAllocatedMembers());

                } else {
                    root.removeAllComponents();
                    String taskId = msgs[2].replace("%20", " ");
                    ViewTask viewTask = new ViewTask(taskId);
                    root.addComponent(viewTask.getTask());
                }
            }


            if (msgs.length == 4) {
                root.removeAllComponents();
                String projectName = msgs[0].replace("%20", " ");
                String userStoryName = msgs[1].replace("%20", " ");
                String taskName = msgs[2].replace("%20", " ");
                //CRAddingWindow crAddingWindow = new CRAddingWindow();
                //root.addComponent(crProcessingView.getCRProcessingView());

            }

        }

    }

}
