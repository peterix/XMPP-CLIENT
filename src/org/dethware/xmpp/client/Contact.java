/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dethware.xmpp.client;

import java.util.ArrayList;
import java.util.Collection;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;
import org.jivesoftware.smack.packet.RosterPacket;
import org.jivesoftware.smack.util.StringUtils;

/**
 *
 * @author peterix
 */
public class Contact implements Comparable<Contact> {
    private String name;
    private String JID;
    private UserStatus status;
    private String statusMesage;
    private ContactManager cm;
    private RosterPacket.ItemStatus subStatus;
    private RosterPacket.ItemType subType;
    public ArrayList <ContactGroup> groups = new ArrayList<>();
    Contact(ContactManager parent, RosterEntry re)
    {
        name = re.getName();
        JID = StringUtils.parseBareAddress(re.getUser());
        // contact starts as offline when created
        status = UserStatus.Offline;
        Collection <RosterGroup> grps = re.getGroups();
        for(RosterGroup rg: grps)
        {
            ContactGroup cg = parent.getGroup(rg.getName());
            groups.add(cg);
        }
        cm = parent;
    }
    Conversation getConversation()
    {
        XMPPClient app = XMPPClient.globalInstance();
        return app.conversationFactory.getConversationForJID(JID);
    }
    // contact has changed status
    public void setStatus(UserStatus newStatus, String message)
    {
        // TODO: notify listeners
        status = newStatus;
        if(message != null)
            statusMesage = message;
    }
    public void setSubStatus(RosterPacket.ItemType type, RosterPacket.ItemStatus status)
    {
        subStatus = status;
        subType = type;
    }
    // new nick is set
    public void setName(String newName)
    {
        // TODO: notify listeners
        name = newName;
    }
    public UserStatus getStatus()
    {
        return status;
    }
    public RosterPacket.ItemStatus getSubStatus()
    {
        return subStatus;
    }
    public RosterPacket.ItemType getSubType()
    {
        return subType;
    }        
    public String getName()
    {
        return name;
    }
    public String getJID()
    {
        return JID;
    }

    @Override
    public int compareTo(Contact o) {
        return JID.compareTo(o.getJID());
    }

    String getMessage() {
        return statusMesage;
    }
}
