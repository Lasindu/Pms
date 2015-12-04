/*
 * Copyright 2014 Tomi Virtanen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pms.component.member.taskganntchart;

import com.pms.DashboardUI;
import com.pms.component.ganttchart.GanttListener;
import com.pms.dao.TaskDAO;
import com.pms.dao.UserStoryDAO;
import com.pms.domain.Project;
import com.pms.domain.Task;
import com.pms.domain.UserStory;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.ClientConnector;
import com.vaadin.server.Sizeable;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import org.tltv.gantt.Gantt;
import org.tltv.gantt.client.shared.Step;
import com.pms.component.ganttchart.util.UriFragmentWrapperFactory;
import com.pms.component.ganttchart.util.Util;

/**
 * Created by lasindu on 11/17/2015.
 */

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Calendar;

public class TaskGanttChart  {

    private Gantt gantt;

    private Project project;

    private NativeSelect localeSelect;
    private NativeSelect reso;

    private DateField start;
    private DateField end;

    private SimpleDateFormat dateFormat = new SimpleDateFormat(
            "MMM dd HH:mm:ss zzz yyyy");

    private HorizontalLayout controls;

    private GanttListener ganttListener;

    private ClientConnector.AttachListener ganttAttachListener = new ClientConnector.AttachListener() {

        @Override
        public void attach(ClientConnector.AttachEvent event) {
            syncLocaleAndTimezone();
        }
    };

    private ClickListener createStepClickListener = new ClickListener() {

        @Override
        public void buttonClick(ClickEvent event) {
            Step newStep = new Step();
            Date now = new Date();
            newStep.setStartDate(now.getTime());
            newStep.setEndDate(now.getTime() + (7 * 24 * 3600000));
            //openStepEditor(newStep);
        }

    };

    private ValueChangeListener startDateValueChangeListener = new ValueChangeListener() {

        @Override
        public void valueChange(ValueChangeEvent event) {
            gantt.setStartDate((Date) event.getProperty().getValue());
        }
    };

    private ValueChangeListener endDateValueChangeListener = new ValueChangeListener() {

        @Override
        public void valueChange(ValueChangeEvent event) {
            gantt.setEndDate((Date) event.getProperty().getValue());
        }
    };

    private ValueChangeListener resolutionValueChangeListener = new ValueChangeListener() {

        @Override
        public void valueChange(ValueChangeEvent event) {
            org.tltv.gantt.client.shared.Resolution res = (org.tltv.gantt.client.shared.Resolution) event
                    .getProperty().getValue();
            if (validateResolutionChange(res)) {
                gantt.setResolution(res);
            }
        }

    };

    private ValueChangeListener localeValueChangeListener = new ValueChangeListener() {

        @Override
        public void valueChange(ValueChangeEvent event) {
            gantt.setLocale((Locale) event.getProperty().getValue());

            syncLocaleAndTimezone();
        }
    };

    private ValueChangeListener timezoneValueChangeListener = new ValueChangeListener() {

        @Override
        public void valueChange(ValueChangeEvent event) {
            String tzId = (String) event.getProperty().getValue();
            if ("Default".equals(tzId)) {
                gantt.setTimeZone(null);
            } else {
                gantt.setTimeZone(TimeZone.getTimeZone(tzId));
            }
            syncLocaleAndTimezone();
        }
    };


    public Component init(Project project) {

        this.project= project;

        ganttListener = null;
        createGantt(project);

        //MenuBar menu = controlsMenuBar();
        Panel controls = createControls();

        TabSheet tabsheet = new TabSheet();
        tabsheet.setSizeFull();

        //to show table
        Component wrapper = UriFragmentWrapperFactory.wrapByUriFragment("table", gantt);
        if (wrapper instanceof GanttListener) {
            ganttListener = (GanttListener) wrapper;
        }

        final VerticalLayout layout = new VerticalLayout();
        layout.setStyleName("demoContentLayout");
        layout.setSizeFull();
        //layout.addComponent(menu);
        layout.addComponent(controls);
        layout.addComponent(wrapper);
        layout.setExpandRatio(wrapper, 1);

        return  layout;
    }

    private void createGantt(Project project) {

        gantt = new Gantt();
        gantt.setWidth(100, Sizeable.Unit.PERCENTAGE);
        gantt.setHeight(400, Sizeable.Unit.PIXELS);
        gantt.setResizableSteps(false);
        gantt.setMovableSteps(true);
        gantt.addAttachListener(ganttAttachListener);
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        gantt.setStartDate(cal.getTime());

        cal.add(Calendar.HOUR, 80);
        gantt.setEndDate(cal.getTime());
        cal.setTime(new Date());

        final TaskDAO taskDAO = (TaskDAO) DashboardUI.context.getBean("Task");
        UserStoryDAO userStoryDAO = (UserStoryDAO) DashboardUI.context.getBean("UserStory");

        UserStory userStory=userStoryDAO.getCurrentWorkingUserStory(project);

        List<Task> taskMap = null; //taskDAO.getAllTasksForUSerStory(userStory);
        if(userStory != null){
            taskMap = taskDAO.getAllTasksForUSerStory(userStory);

            Step previosStep=null;

            //System.out.println(taskMap.toString());

            long startTime = 0;
            for(int i = 0 ;i<taskMap.size();i++) {

                Task task1 =taskMap.get(i);

                Step step1 = new Step(task1.getAssignedTo());
                step1.setDescription(task1.getName());
                if(i==0){
                    startTime = cal.getTime().getTime();
                }
                //System.out.println("Start Time : " + (startTime+Integer.parseInt(task1.getStartTime())*3600*1000) +"/ Start Hour : " +(startTime+Integer.parseInt(task1.getStartTime())*3600*1000)/3600000);
                //System.out.println("Get Start Time : "+task1.getStartTime());
                //System.out.println("Get Estimate Time : "+task1.getEstimateTime());

                step1.setStartDate(startTime+Integer.parseInt(task1.getStartTime())*3600*1000);
                cal.add(Calendar.HOUR, Integer.parseInt(task1.getEstimateTime()));
                step1.setEndDate(startTime+Integer.parseInt(task1.getStartTime())*3600*1000 + Integer.parseInt(task1.getEstimateTime())*3600*1000);//cal.getTime().getTime()
                //System.out.println("End Time : "+startTime+Integer.parseInt(task1.getStartTime())*3600*1000 + Integer.parseInt(task1.getEstimateTime())*3600*1000 +"/End Hour : " + (startTime+Integer.parseInt(task1.getStartTime())*3600*1000 + Integer.parseInt(task1.getEstimateTime())*3600*1000)/3600000);
                //Change color of background according to state of task
                if(task1.getState().equals("#F5A9F2"))
                {
                    step1.setBackgroundColor("#0040FF");
                }
                else if(task1.getState().equals("working"))
                {
                    step1.setBackgroundColor("#00FF40");
                }
                else if(task1.getState().equals("done"))
                {
                    step1.setBackgroundColor("#FF00FF");
                }

                if(task1.isCr()) step1.setBackgroundColor("FF3300");

                if(previosStep==null)
                {
                    gantt.addStep(step1);
                    previosStep=step1;
                }
                else
                {
                    step1.setPredecessor(previosStep);
                    gantt.addStep(step1);
                    previosStep=step1;
                }
            }
        }
        gantt.addMoveListener(new Gantt.MoveListener() {

            @Override
            public void onGanttMove(Gantt.MoveEvent event) {
                Date start = new Date(event.getStartDate());
                Date end = new Date(event.getEndDate());

                dateFormat.setTimeZone(gantt.getTimeZone());

                Notification.show("Moved " + event.getStep().getCaption()
                                + " to Start Date: " + dateFormat.format(start)
                                + " End Date: " + dateFormat.format(end),
                        Notification.Type.TRAY_NOTIFICATION);
            }
        });
    }

    private void syncLocaleAndTimezone() {
        start.removeValueChangeListener(startDateValueChangeListener);
        end.removeValueChangeListener(endDateValueChangeListener);
        try {
            start.setLocale(gantt.getLocale());
            start.setTimeZone(gantt.getTimeZone());
            start.setValue(gantt.getStartDate());
            end.setLocale(gantt.getLocale());
            end.setTimeZone(gantt.getTimeZone());
            end.setValue(gantt.getEndDate());
        } finally {
            start.addValueChangeListener(startDateValueChangeListener);
            end.addValueChangeListener(endDateValueChangeListener);
        }
        dateFormat = new SimpleDateFormat("MMM dd HH:mm:ss zzz yyyy",
                gantt.getLocale());
    }

    private Panel createControls() {
        Panel panel = new Panel();
        panel.setWidth(100, Sizeable.Unit.PERCENTAGE);

        controls = new HorizontalLayout();
        controls.setSpacing(true);
        controls.setMargin(true);
        panel.setContent(controls);

        start = createStartDateField();
        end = createEndDateField();

        Button createStep = new Button("Create New Step...",
                createStepClickListener);

        HorizontalLayout heightAndUnit = new HorizontalLayout(
                Util.createHeightEditor(gantt),
                Util.createHeightUnitEditor(gantt));

        HorizontalLayout widthAndUnit = new HorizontalLayout(
                Util.createWidthEditor(gantt),
                Util.createWidthUnitEditor(gantt));

        reso = new NativeSelect("Resolution");
        reso.setNullSelectionAllowed(false);
        reso.addItem(org.tltv.gantt.client.shared.Resolution.Hour);
        reso.addItem(org.tltv.gantt.client.shared.Resolution.Day);
        reso.addItem(org.tltv.gantt.client.shared.Resolution.Week);
        reso.setValue(gantt.getResolution());
        reso.setImmediate(true);
        reso.addValueChangeListener(resolutionValueChangeListener);

        localeSelect = new NativeSelect("Locale") {
            @Override
            public void attach() {
                super.attach();

                if (getValue() == null) {
                    // use default locale
                    setValue(gantt.getLocale());
                    addValueChangeListener(localeValueChangeListener);
                }
            }
        };
        localeSelect.setNullSelectionAllowed(false);
        for (Locale l : Locale.getAvailableLocales()) {
            localeSelect.addItem(l);
            localeSelect.setItemCaption(l, l.getDisplayName(DashboardUI.getCurrent().getUI().getLocale()));
        }
        localeSelect.setImmediate(true);

        String[] zones = new String[] { "GMT-0", "GMT-1", "GMT-2", "GMT-3",
                "GMT-4", "GMT-5", "GMT-6", "GMT-7", "GMT-8", "GMT-9", "GMT-10",
                "GMT-11", "GMT-12", "GMT+1", "GMT+2", "GMT+3", "GMT+4",
                "GMT+5", "GMT+6", "GMT+7", "GMT+8", "GMT+9", "GMT+10",
                "GMT+11", "GMT+12", "GMT+13", "GMT+14" };
        NativeSelect timezoneSelect = new NativeSelect("Timezone");
        timezoneSelect.setNullSelectionAllowed(false);
        timezoneSelect.addItem("Default");
        timezoneSelect.setItemCaption("Default", "Default ("
                + TimeZone.getDefault().getDisplayName() + ")");
        for (String timezoneId : zones) {
            TimeZone tz = TimeZone.getTimeZone(timezoneId);
            timezoneSelect.addItem(timezoneId);
            timezoneSelect.setItemCaption(timezoneId,
                    tz.getDisplayName(DashboardUI.getCurrent().getUI().getLocale()));
        }
        timezoneSelect.setValue("Default");
        timezoneSelect.setImmediate(true);
        timezoneSelect.addValueChangeListener(timezoneValueChangeListener);

        controls.addComponent(start);
        controls.addComponent(end);
        controls.addComponent(reso);
        //controls.addComponent(localeSelect);
        //controls.addComponent(timezoneSelect);
        //controls.addComponent(heightAndUnit);
        //controls.addComponent(widthAndUnit);
        //controls.addComponent(createStep);
        // controls.setComponentAlignment(createStep, Alignment.MIDDLE_LEFT);
        //controls.setComponentAlignment(widthAndUnit, Alignment.MIDDLE_LEFT);

        return panel;
    }

    private DateField createStartDateField() {
        DateField f = new DateField("Start date");
        f.setResolution(Resolution.SECOND);
        f.setImmediate(true);
        f.addValueChangeListener(startDateValueChangeListener);
        return f;
    }

    private DateField createEndDateField() {
        DateField f = new DateField("End date");
        f.setResolution(Resolution.SECOND);
        f.setImmediate(true);
        f.addValueChangeListener(endDateValueChangeListener);
        return f;
    }

    private boolean validateResolutionChange(
            final org.tltv.gantt.client.shared.Resolution res) {
        long max = 5 * 12 * 4 * 7 * 24 * 3600000L;
        if (res == org.tltv.gantt.client.shared.Resolution.Hour
                && (gantt.getEndDate().getTime() - gantt.getStartDate()
                .getTime()) > max) {

            // revert to previous resolution
            setResolution(gantt.getResolution());

            // make user to confirm hour resolution, if the timeline range is
            // more than one week long.
            Util.showConfirmationPopup(
                    "Timeline range is a quite long for hour resolution. Rendering may be slow. Continue anyway?",
                    new Runnable() {

                        @Override
                        public void run() {
                            setResolution(res);
                            gantt.setResolution(res);
                        }
                    });
            return false;
        }
        return true;
    }

    private void setResolution(
            org.tltv.gantt.client.shared.Resolution resolution) {
        reso.removeValueChangeListener(resolutionValueChangeListener);
        try {
            reso.setValue(resolution);
        } finally {
            reso.addValueChangeListener(resolutionValueChangeListener);
        }
    }

}
