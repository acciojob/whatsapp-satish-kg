package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class WhatsappRepository {

    //Assume that each user belongs to at most one group
    //You can use the below mentioned hashmaps or delete these and create your own.

    private HashMap<Group, List<User>> groupUserMap;
    private HashMap<Group, List<Message>> groupMessageMap;
    private HashMap<Message, User> senderMap;
    private HashMap<Group, User> adminMap;
    private HashMap<String, User> userData;
    private int customGroupCount;
    private int messageId;

    public WhatsappRepository(){
        this.groupMessageMap = new HashMap<Group, List<Message>>();
        this.groupUserMap = new HashMap<Group, List<User>>();
        this.senderMap = new HashMap<Message, User>();
        this.adminMap = new HashMap<Group, User>();
        this.userData = new HashMap<>();
        this.customGroupCount = 0;
        this.messageId = 0;
    }

    public boolean isNewUser(String mobile) {
        if(userData.containsKey(mobile)) return false;
        return true;
    }

    public void createUser(String name, String mobile) {
        userData.put(mobile, new User(name, mobile));
    }

    public String changeAdmin(User approver, User user, Group group) throws Exception{
        if(!groupUserMap.containsKey(group)) throw new Exception("Group does not exist");
        if(!adminMap.get(group).equals(approver)) throw new Exception("Approver does not have rights");
        if(!this.userExistsInGroup(group, user)) throw  new Exception("User is not a participant");

        adminMap.put(group, user);
        return "SUCCESS";
    }

    public Group createGroup(List<User> users) {
        if(users.size() == 2) return this.createPersonalChat(users);

        this.customGroupCount++;
        String groupName = "Group " + this.customGroupCount;
        Group group = new Group(groupName, users.size());
        groupUserMap.put(group, users);
        adminMap.put(group, users.get(0));
        return group;
    }

    public Group createPersonalChat(List<User> users) {
        String groupName = users.get(1).getName();
        Group personalGroup = new Group(groupName, 2);
        groupUserMap.put(personalGroup, users);
        return personalGroup;
    }

    public int createMessage(String content){
        this.messageId++;
        Message message = new Message(messageId, content, new Date());
        return this.messageId;
    }

    public int sendMessage(Message message, User sender, Group group) throws Exception{
        if(!groupUserMap.containsKey(group)) throw new Exception("Group does not exist");
        if(!this.userExistsInGroup(group, sender)) throw  new Exception("You are not allowed to send message");

        List<Message> messages = new ArrayList<>();
        if(groupMessageMap.containsKey(group)) messages = groupMessageMap.get(group);

        messages.add(message);
        groupMessageMap.put(group, messages);
        return messages.size();
    }

    public boolean userExistsInGroup(Group group, User sender) {
        List<User> users = groupUserMap.get(group);
        for(User user: users) {
            if(user.equals(sender)) return true;
        }

        return false;
    }
}
/*
Whatsapp Impl

-WhatsApp is a messaging service that allows users to create and manage groups, users, and messages. The application can be implemented using the User, Message, and Group models.

-Note that all-argument constructors should be manually written for User and Group. For Message, the timestamp should be set as the current date.

-It has several functionalities that allow users to:

-Create a user by providing a name and mobile number. If the mobile number already exists in the database, the application will throw an exception.

-Create a group by providing a list of users. The list should contain at least 2 users, where the first user is the admin.

A group has exactly one admin.
A user can belong to exactly one group and has a unique name.
If there are only 2 users, the group is a personal chat, and the group name should be kept as the name of the second user (other than the admin).
If there are 2 or more users, the name of the group will be "Group count".
Note that a personal chat is not considered a group and the count is not updated for personal chats.
For example: Consider userList1 = {Alex, Bob, Charlie}, userList2 = {Dan, Evan}, userList3 = {Felix, Graham, Hugh}. If createGroup is called for these userLists in the same order, their group names would be "Group 1", "Evan", and "Group 2" respectively.
Create a message by providing the message content. The i^{th} created message has message id 'i'. For example if three messages are created, then the IDs of the messages would be 1, 2, and 3, irrespective of their sender and the groups they are sent to.

Send a message by providing the message, sender, and group.

If the mentioned group does not exist, the application will throw an exception.
If the sender is not a member of the group, the application will throw an exception.
If the message is sent successfully, the application will return the final number of messages in that group.
Change the admin of a group by providing the approver, user, and group.

If the mentioned group does not exist, the application will throw an exception.
If the approver is not the current admin of the group, the application will throw an exception.
If the user is not a part of the group, the application will throw an exception.
If all the conditions are met, it will change the admin of the group to "user" and return "SUCCESS". Note that the admin rights are transferred from the approver to the user in this case.
Remove a user from a group by providing the user.

If the user is not found in any group, the application will throw an exception.
If the user is found in a group and is the admin, the application will throw an exception.
If the user is not the admin, the application will remove the user from the group, remove all its messages from all the databases, and update relevant attributes accordingly.
If the user is removed successfully, the application will return (the updated number of users in the group + the updated number of messages in the group + the updated number of overall messages across all groups).
Find messages by providing a start date, end date, and K.

It will find the Kth latest message between the start and end (excluding start and end).
If the number of messages between the given time is less than K, the application will throw an exception.
The controller and models have been defined for the application. You need to implement the service layer and repositories (using hashmaps).

Note:

Instead of autowiring, use ClassName className = new ClassName();
The last two functionalities (remove a user and find messages) are non-scorable and are added as bonus problems.
Do not change the name of any existing function/class.
Avoid using the lombok library. Define getters and setters manually.
 */