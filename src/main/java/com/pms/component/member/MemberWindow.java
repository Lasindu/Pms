package com.pms.component.member;

import com.pms.DashboardUI;
import com.pms.dao.ProjectDAO;
import com.pms.dao.UserDAO;
import com.pms.domain.Project;
import com.pms.domain.User;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

/**
 * Created by lasindu on 7/15/2015.
 */
public class MemberWindow extends Window{

    private Collection<User> userCollection;

    private final BeanFieldGroup<User> fieldGroup;
    private User user;
    private boolean editmode=false;

    @PropertyId("username")
    private TextField userName;
    @PropertyId("email")
    private TextField email;
    @PropertyId("firstname")
    private TextField firstName;
    @PropertyId("lastname")
    private TextField lastName;
    @PropertyId("role")
    private Select role;
    @PropertyId("skill")
    private Select skill;
    //private TextField role;
    @PropertyId("discription")
    private TextArea discription;
    @PropertyId("technology")
    private TextArea technology;
    @PropertyId("dateofbirth")
    private PopupDateField dateOfBirth;
    @PropertyId("multipleselect")
    private ListSelect select;


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
        userName.setRequiredError("Required This");
        content.addComponent(userName);

        firstName = new TextField("First Name");
        firstName.setNullRepresentation("");
        content.addComponent(firstName);

        lastName = new TextField("Last Name");
        lastName.setNullRepresentation("");
        content.addComponent(lastName);

        email = new TextField("Email");
        email.setNullRepresentation("");
        content.addComponent(email);

        dateOfBirth = new PopupDateField  ("Date Of birth");
        dateOfBirth.setValue(new Date());
        dateOfBirth.setDateFormat("yyyy-MM-dd");
        content.addComponent(dateOfBirth);

        role = new Select("Select Role");
        //role.setNullRepresentation("");
        role.addItem("project manager");
        role.addItem("team leader");
        role.addItem("software engineer");
        role.addItem("quality engineer");
        content.addComponent(role);

        discription = new TextArea("Address");
        discription.setNullRepresentation("");
        content.addComponent(discription);

        skill = new Select("Select Skill");
        skill.addItem("Java");
        skill.addItem("C# .NET");
        skill.addItem("VB .NET");
        skill.addItem("PHP");
        content.addComponent(skill);


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
                        user=(User) VaadinSession.getCurrent().getAttribute(
                                User.class.getName());
                        //we have to use this method for create new project because of  many to many mapping
                        UserDAO userDAO = (UserDAO) DashboardUI.context.getBean("User");

                        userDAO.updateUser(user);

                        Notification success = new Notification(
                                "Member Created successfully");
                        success.setDelayMsec(2000);
                        success.setStyleName("bar success small");
                        success.setPosition(Position.BOTTOM_CENTER);
                        success.show(Page.getCurrent());

                    }
                    UserDAO userDAO= (UserDAO) DashboardUI.context.getBean("User");
                    userDAO.updateUser(user);

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
