package org.dethware.xmpp.client;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import org.jivesoftware.smack.*;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.util.StringUtils;



class ContactRoot
{
    public ContactRoot() {
    }
}


/**
 * Nothing much to see here, move along...
 **/
public class ContactManager implements TreeModel, RosterListener, ClConnectionListener {
    
    private List <TreeModelListener> treeListernerList = new CopyOnWriteArrayList<>();
    
    private ContactRoot root = new ContactRoot();
    private ArrayList <ContactGroup> groups = new ArrayList<>();
    private ArrayList <Contact> contacts = new ArrayList<>();
    ContactGroup unlisted;
    UserStatus oldStatus = UserStatus.Offline;
    
    private void fireTreeReload( boolean flush ){
        XMPPConnection conn = XMPPClient.globalInstance().getConnection();
        Roster r = conn.getRoster();
        TreePath tp = new TreePath(r);
        TreeModelEvent e = new TreeModelEvent(r,tp);
        for(TreeModelListener l : treeListernerList)
        {
            l.treeStructureChanged(e);
        }
    }
    
    private void fireContactUpdate(Contact c){
        List <ContactGroup> grps = c.groups;
        if(grps.isEmpty())
        {
            Object[] path = { root, c };
            TreePath tp = new TreePath(path);
            TreeModelEvent e = new TreeModelEvent(c,tp);
            for(TreeModelListener l : treeListernerList)
            {
                l.treeNodesChanged(e);
            }
        }
        else
        {
            for(ContactGroup grp: grps)
            {
                Object[] path = { root, grp , c };
                TreePath tp = new TreePath(path);
                TreeModelEvent e = new TreeModelEvent(c,tp);
                for(TreeModelListener l : treeListernerList)
                {
                    l.treeNodesChanged(e);
                }
            }
        }
        
    }
    
    // We specify the root directory when we create the model.
    public ContactManager()
    {
        unlisted = new ContactGroup(this, "Unlisted");
        groups.add(unlisted);
    }

    // The model knows how to return the root object of the tree
    @Override
    public synchronized Object getRoot()
    {
        return root;
    }

    // Tell JTree whether an object in the tree is a leaf
    @Override
    public synchronized boolean isLeaf(Object node)
    {
        if(node.getClass() == ContactRoot.class)
            return false;
        else if(node.getClass() == ContactGroup.class)
            return false;
        else if(node.getClass() == Contact.class)
            return true;
        else // whatever it is, we don't want to probe further
            return true;
    }

    // Tell JTree how many children a node has
    @Override
    public synchronized int getChildCount(Object parent) {
        if(parent.getClass() == ContactRoot.class)
        {
            XMPPConnection conn = XMPPClient.globalInstance().getConnection();
            if(conn == null)
                return 0;
            if(!conn.isConnected())
                return 0;
            return groups.size();
        }
        else if(parent.getClass() == ContactGroup.class)
        {
            ContactGroup grp = (ContactGroup) parent;
            return grp.getNum();
        }
        else if(parent.getClass() == RosterEntry.class)
            return 0;
        else // whatever it is, we don't want to probe further
            return 0;
    }

    // Fetch any numbered child of a node for the JTree.
    // Our model returns File objects for all nodes in the tree.  The
    // JTree displays these by calling the File.toString() method.
    @Override
    public synchronized Object getChild(Object parent, int index) {
        if(parent.getClass() == ContactRoot.class)
        {
            return groups.get(index);
        }
        else if(parent.getClass() == ContactGroup.class)
        {
            ContactGroup grp = (ContactGroup) parent;
            return grp.getContact(index);
        }
        else if(parent.getClass() == Contact.class)
            return null;
        else // whatever it is, we don't want to probe further
            return null;
    }

    // Figure out a child's position in its parent node.
    @Override
    public synchronized int getIndexOfChild(Object parent, Object child) {
        if(parent.getClass() == ContactRoot.class)
        {
            return groups.indexOf(child);
        }
        else if(parent.getClass() == ContactGroup.class)
        {
            ContactGroup rg = (ContactGroup) parent;
            return rg.getIndexOf(child);
        }
        else
            return -1;
    }

    // This method is invoked by the JTree only for editable trees.  
    // This TreeModel does not allow editing, so we do not implement 
    // this method.  The JTree editable property is false by default.
    @Override
    public synchronized void valueForPathChanged(TreePath path, Object newvalue) {}

    @Override
    public synchronized void addTreeModelListener(TreeModelListener l) {
        treeListernerList.add(l);
        System.out.println("Added listener: " + l.hashCode());
    }
    @Override
    public synchronized void removeTreeModelListener(TreeModelListener l) {
        treeListernerList.remove(l);
        System.out.println("Removed listener: " + l.hashCode());
    }
    @Override
    public synchronized void entriesAdded(Collection<String> clctn) {
        XMPPConnection conn = XMPPClient.globalInstance().getConnection();
        Roster r = conn.getRoster();
        // for each added entry
        for(String entry: clctn)
        {
            // create a contact for it
            RosterEntry e = r.getEntry(entry);
            Presence p = r.getPresence(e.getUser());
            Contact c = new Contact(this, e);
            c.setStatus(UserStatus.fromPresence(p),p.getStatus());
            contacts.add(c);
            System.out.println(c.getName() + " added:");
            Collection <RosterGroup> grps = e.getGroups();
            if(grps.isEmpty())
            {
                unlisted.add(c);
                System.out.println("Unlisted");
            }
            else for(RosterGroup rg: grps)
            {
                for(ContactGroup cg: groups)
                {
                    if(cg.getName().equals(rg.getName()))
                    {
                        cg.add(c);
                        System.out.println("Group " + cg.getName());
                    }
                }
            }
        }
        fireTreeReload(false);
    }

    @Override
    public synchronized void entriesUpdated(Collection<String> clctn) {
        XMPPConnection conn = XMPPClient.globalInstance().getConnection();
        Roster r = conn.getRoster();
        // for each added entry
        for(String entry: clctn)
        {
            // update contact
            RosterEntry e = r.getEntry(entry);
            Contact c = getContact(entry);
            c.setName(e.getName());
            fireContactUpdate(c);
        }
    }

    @Override
    public synchronized void entriesDeleted(Collection<String> clctn) {
        for(String entry: clctn)
        {
            System.out.println("Deleted: " + entry);
        }
    }

    @Override
    public synchronized void presenceChanged(Presence prsnc) {
        String user = prsnc.getFrom();
        user = StringUtils.parseBareAddress(user);
        UserStatus st = UserStatus.fromPresence(prsnc);
        XMPPConnection conn = XMPPClient.globalInstance().getConnection();
        System.out.println(user + " changed X status to " + st.toString());
        if(!conn.isConnected())
            return;
        Roster r = conn.getRoster();
        Presence bestPresence = r.getPresence(user);
        Contact c = getContact(user);
        if(bestPresence == null)
        {
            //TODO: clear sign that we should reload the roster? Maybe.
        }
        else
        {
            UserStatus st2 = UserStatus.fromPresence(bestPresence);
            System.out.println(c.getName() + " changed status to " + st2.toString());
            XMPPClient app = XMPPClient.globalInstance();
            // going online
            if(c.getStatus() == UserStatus.Offline && st2 != UserStatus.Offline)
            {
                app.soundUtils.playLoginSound();
            }
            // going offline
            else if(c.getStatus() != UserStatus.Offline && st2 == UserStatus.Offline)
            {
                app.soundUtils.playLogoutSound();
            }
            c.setStatus(st2,prsnc.getStatus());
            fireContactUpdate(c);
        }
    }

    @Override
    public synchronized void connectionChanged(UserStatus status, String statusString) {
        System.err.println("Model got connection change event!");
        // went online
        if(oldStatus == UserStatus.Offline)
        {
            if(status != UserStatus.Offline)
            {
                // load up all the groups
                XMPPConnection conn = XMPPClient.globalInstance().getConnection();
                Roster r = conn.getRoster();
                Collection <RosterGroup> allGroups = r.getGroups();
                for(RosterGroup rg: allGroups)
                {
                    ContactGroup cg = new ContactGroup(this, rg.getName());
                    groups.add(cg);
                }
                fireTreeReload(true);
            }
        }
        // went offline
        if(status == UserStatus.Offline)
        {
            // get rid of our stored contacts/groups
            //TODO: notify contacts and groups of their demise?
            groups.clear();
            contacts.clear();
            unlisted.contacts.clear();
            fireTreeReload(true);
        }
        oldStatus = status;
    }

    public synchronized Contact getContact(String otherJID) {
        for(Contact c: contacts)
        {
            if(c.getJID().equals(otherJID))
            {
                return c;
            }
        }
        return null;
    }
    public synchronized ContactGroup getGroup (String name)
    {
        for(ContactGroup g: groups)
        {
            if(g.getName().equals(name))
            {
                return g;
            }
        }
        return null;
    }
}
