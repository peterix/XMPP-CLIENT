/**
 * @author Petr Mrázek (xmraze03@stud.fit.vutbr.cz)
 */
package org.dethware.xmpp.client;

import java.awt.Component;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import org.jivesoftware.smack.packet.RosterPacket;

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
            StringBuilder tooltipBuilder = new StringBuilder();
            tooltipBuilder.append("<html>").append("JID: ").append(contact.getJID()).append("<br />Status: ").append(contact.getStatus().toString()).append("<br />");
            if(contact.getMessage() != null)
                tooltipBuilder.append("Message:").append(contact.getMessage()).append("<br />");
            tooltipBuilder.append("Subscription type: ").append(contact.getSubType().toString()).append("<br />");
            RosterPacket.ItemStatus stat = contact.getSubStatus();
            if(stat == RosterPacket.ItemStatus.SUBSCRIPTION_PENDING)
            {
                tooltipBuilder.append("Has requested to be subscribed.<br />");
            }
            else if (stat ==  RosterPacket.ItemStatus.UNSUBSCRIPTION_PENDING)
            {
                tooltipBuilder.append("Has requested to be unsubscribed.<br />");
            }
            tooltipBuilder.append("</html>");
            setToolTipText(tooltipBuilder.toString());
                    
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
