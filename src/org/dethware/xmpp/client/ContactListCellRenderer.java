package org.dethware.xmpp.client;



import java.awt.Component;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

public class ContactListCellRenderer extends DefaultTreeCellRenderer {
    public static final ImageIcon CONTACT_CATEGORY_ICON = 
            new ImageIcon(MainWindow.class.getResource("folder-blue.png"));

    public static final ImageIcon CONTACT_ONLINE_ICON = 
            new ImageIcon(MainWindow.class.getResource("user-online.png"));

    public static final ImageIcon CONTACT_FREEFORCHAT_ICON = 
        new ImageIcon(MainWindow.class.getResource("user-ffc.png"));
    
    public static final ImageIcon CONTACT_BUSY_ICON = 
            new ImageIcon(MainWindow.class.getResource("user-busy.png"));

    public static final ImageIcon CONTACT_AWAY_ICON = 
            new ImageIcon(MainWindow.class.getResource("user-away.png"));
    
    public static final ImageIcon CONTACT_XAWAY_ICON = 
            new ImageIcon(MainWindow.class.getResource("user-away-extended.png"));

    public static final ImageIcon CONTACT_OFFLINE_ICON = 
            new ImageIcon(MainWindow.class.getResource("user-offline.png"));

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value,
                boolean sel, boolean expanded, boolean leaf, int row,
                boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf,
                        row, hasFocus);

        if (value instanceof ContactGroup)
        {
            ContactGroup rg = (ContactGroup) value;
            setIcon(CONTACT_CATEGORY_ICON);
            setText(rg.getName());
        }
        else if (value instanceof Contact)
        {
            Contact contact = (Contact) value;
            if (contact.getName() == null)
            {
                    setText(contact.getJID());
            }
            else
            {
                    setText(contact.getName());
            }
            setToolTipText("<html>JID: " + contact.getJID() + "<br />Status: " + contact.getStatus().toString() + "<br />Message:" + contact.getMessage() + "</html>");

            switch(contact.getStatus())
            {
                case Available:
                    setIcon(CONTACT_ONLINE_ICON);
                    break;
                case Away:
                    setIcon(CONTACT_AWAY_ICON);
                    break;
                case DoNotDisturb:
                    setIcon(CONTACT_BUSY_ICON);
                    break;
                case ExtendedAway:
                    setIcon(CONTACT_XAWAY_ICON);
                    break;
                case FreeForChat:
                    setIcon(CONTACT_FREEFORCHAT_ICON);
                    break;
                case Offline:
                    setIcon(CONTACT_OFFLINE_ICON);
                    break;
            }
        }

        return this;
    }

}
