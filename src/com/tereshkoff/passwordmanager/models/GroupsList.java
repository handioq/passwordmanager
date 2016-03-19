package com.tereshkoff.passwordmanager.models;

import java.util.ArrayList;
import java.util.List;

public class GroupsList {

    List<Group> groups = new ArrayList<Group>();

    public GroupsList(List<Group> groups) {
        this.groups = groups;
    }

    public GroupsList()
    {
        //read and parse groups from file
    }

    public Group getGroupByName(String name)
    {
        Group outGroup = new Group();

        for(Group group : groups)
        {
            if (group.getName().equals(name))
                outGroup = group;
        }

        return outGroup;
    }

    public void add(Group group)
    {
        groups.add(group);
    }

    public List<Group> getGroups()
    {
        return groups;
    }

    public void save()
    {

    }

    public PasswordList getAllPasswords()
    {
        PasswordList passwordList = new PasswordList();

        for(Group group : groups)
        {
            for(Password password : group.getPasswordList().getPasswordList())
            {
                passwordList.add(password);
            }
        }

        return passwordList;
    }

}
