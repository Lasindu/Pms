package com.pms.component.member;

import com.pms.DashboardUI;
import com.pms.dao.ProjectDAO;
import com.pms.dao.QualityDAO;
import com.pms.dao.UserDAO;
import com.pms.domain.*;
import com.sun.java.swing.plaf.windows.resources.windows;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.PropertyId;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.Page;
import com.vaadin.server.Responsive;
import com.vaadin.server.Sizeable;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.Position;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Created by lasindu on 7/15/2015.
 */
public class MemberWindow extends Window {

    private Collection<User> userCollection;

    private final BeanFieldGroup<User> fieldGroup;
    private User user;
    private boolean editmode=false;

    @PropertyId("userName")
    private TextField userName;
    @PropertyId("firstName")
    private TextField firstName;
    @PropertyId("lastName")
    private TextField lastName;
    @PropertyId("upload")
    private Upload upload;
    @PropertyId("dateOfBirth")
    private PopupDateField dateOfBirth;
    @PropertyId("email")
    private TextField email;
    @PropertyId("password")
    private TextField password;
    @PropertyId("contact")
    private TextField contact;
    @PropertyId("gender")
    private Select gender;
    @PropertyId("role")
    private Select role;
    @PropertyId("experience")
    private TextArea experience;
    @PropertyId("technicalSkills")
    private Select technicalSkill;
    @PropertyId("domainSkills")
    private Select domainSkill;
    @PropertyId("assignedProjectName")
    private Select projectList;


    private MemberWindow(User user)
    {
        this.user=user;

        if(!user.getFirstName().isEmpty())
        {
            editmode=true;

        }


        addStyleName("profile-window");
        Responsive.makeResponsive(this);

        setModal(true);
        setCloseShortcut(ShortcutAction.KeyCode.ESCAPE, null);
        setResizable(false);
        setClosable(false);
        setHeight(90.0f, Unit.PERCENTAGE);

        VerticalLayout content = new VerticalLayout();
        content.setSizeFull();
        content.setMargin(new MarginInfo(true, false, false, false));
        setContent(content);

        TabSheet detailsWrapper = new TabSheet();
        detailsWrapper.setSizeFull();
        detailsWrapper.addStyleName(ValoTheme.TABSHEET_PADDED_TABBAR);
        detailsWrapper.addStyleName(ValoTheme.TABSHEET_ICONS_ON_TOP);
        detailsWrapper.addStyleName(ValoTheme.TABSHEET_CENTERED_TABS);
        content.addComponent(detailsWrapper);
        content.setExpandRatio(detailsWrapper, 1f);

        detailsWrapper.addComponent(buildProject());
        content.addComponent(buildFooter());

        fieldGroup = new BeanFieldGroup<User>(User.class);
        fieldGroup.bindMemberFields(this);
        fieldGroup.setItemDataSource(user);
    }


    private Component buildProject()
    {
        FormLayout content = new FormLayout();
        content.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
        content.setCaption("Member");
        content.setMargin(new MarginInfo(true, true, true, true));
        content.setSpacing(true);


        userName = new TextField("User Name");
        userName.setNullRepresentation("");
        userName.setRequired(true);
        userName.setRequiredError("User Name is Required");
        content.addComponent(userName);

        firstName = new TextField("First Name");
        firstName.setNullRepresentation("");
        content.addComponent(firstName);

        lastName = new TextField("Last Name");
        lastName.setNullRepresentation("");
        content.addComponent(lastName);

        upload = new Upload("Upload Profile Image", new Upload.Receiver() {
            @Override
            public OutputStream receiveUpload(String s, String s1) {
                return null;
            }
        });
        content.addComponent(upload);

        dateOfBirth = new PopupDateField("Date Of birth");
        dateOfBirth.setValue(new Date());
        dateOfBirth.setDateFormat("yyyy-MM-dd");
        content.addComponent(dateOfBirth);

        email = new TextField("Email");
        email.setNullRepresentation("");
        content.addComponent(email);

        password = new TextField("Password");
        password.setNullRepresentation("");
        content.addComponent(password);

        contact = new TextField("Contact");
        contact.setNullRepresentation("");
        content.addComponent(contact);

        gender = new Select("Select Gender");
        //role.setNullRepresentation("");
        gender.addItem("Male");
        gender.addItem("Female");
        content.addComponent(gender);

        role = new Select("Select Role");
        role.addItem("admin");
        role.addItem("project manager");
        role.addItem("team leader");
        role.addItem("software engineer");
        role.addItem("quality engineer");
        content.addComponent(role);

        experience = new TextArea("Experience");
        experience.setNullRepresentation("");
        content.addComponent(experience);

        UserDAO userDAO= (UserDAO) DashboardUI.context.getBean("User");
        ProjectDAO projectDAO = (ProjectDAO) DashboardUI.context.getBean("Project");

        technicalSkill = new Select("Select Technical Skill");
        List<TechnicalSkill> techSkill = userDAO.loadTechSkills();
        for(int i=0;i<techSkill.size();i++){
            technicalSkill.addItem(techSkill.get(i).getTechName());
        }
        content.addComponent(technicalSkill);

        domainSkill = new Select("Select Domain Skill");
        List<DomainSkill> domSkill = userDAO.loadDomSkills();
        for(int i=0;i<domSkill.size();i++){
            domainSkill.addItem(domSkill.get(i).getDomName());
        }
        content.addComponent(domainSkill);

        projectList = new Select("Assign to project");
        List<Project> projects = projectDAO.getAllProjects();
        for(int i=0;i<projects.size();i++){
            projectList.addItem(projects.get(i).getName());
        }
        content.addComponent(projectList);

        return content;

    }
    private Component buildFooter()
    {
        HorizontalLayout footer= new HorizontalLayout();
        footer.addStyleName(ValoTheme.WINDOW_BOTTOM_TOOLBAR);
        footer.setWidth(100.0f, Unit.PERCENTAGE);


        Button cancel= new Button("Cancel");
        cancel.addClickListener(new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {
                close();

            }
        });

        Button submit;
        if(editmode)
        {
            submit= new Button("Update Member");

        }
        else
        {
            submit= new Button("Create Member");

        }

        submit.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                try {
                    fieldGroup.commit();
                    User user;
                    user =fieldGroup.getItemDataSource().getBean();

                    if (editmode)
                    {
                        UserDAO userDAO= (UserDAO) DashboardUI.context.getBean("User");
                        userDAO.updateUser(user);

                        Notification success = new Notification(
                                "Member updated successfully");
                        success.setDelayMsec(2000);
                        success.setStyleName("bar success small");
                        success.setPosition(Position.BOTTOM_CENTER);
                        success.show(Page.getCurrent());

                    }
                    else
                    {
                        UserDAO userDAO= (UserDAO) DashboardUI.context.getBean("User");
                        QualityDAO qualityDAO= (QualityDAO) DashboardUI.context.getBean("Quality");

                        userDAO.setUser(user);

                        Quality quality = new Quality();
                        quality.setUserName(user.getUserName());
                        qualityDAO.setQuality(quality);

                        Notification success = new Notification(
                                "Member Created successfully");
                        success.setDelayMsec(2000);
                        success.setStyleName("bar success small");
                        success.setPosition(Position.BOTTOM_CENTER);
                        success.show(Page.getCurrent());

                    }
                    //UserDAO userDAO= (UserDAO) DashboardUI.context.getBean("User");
                    //userDAO.updateUser(user);

                    // getUI().getNavigator().navigateTo("/");
                    Page.getCurrent().reload();

                } catch (FieldGroup.CommitException e) {
                    Notification.show("Error while creating member",
                            Notification.Type.ERROR_MESSAGE);
                }

            }
        });

        footer.addComponent(submit);
        footer.addComponent(cancel);

        footer.setExpandRatio(cancel,1);

        footer.setComponentAlignment(cancel, Alignment.TOP_RIGHT);
        footer.setComponentAlignment(submit, Alignment.TOP_RIGHT);


        return footer;

    }



    public static void open(User user) {
        Window w = new MemberWindow(user);
        UI.getCurrent().addWindow(w);
        w.focus();
    }
}
