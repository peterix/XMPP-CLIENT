/**
 * @author Petr Mrázek (xmraze03@stud.fit.vutbr.cz)
 */
package org.dethware.xmpp.client;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.util.StringUtils;



class KnownSubscribe implements Runnable
{
    String fromJID;
    Contact fromContact;
    public KnownSubscribe(String fromJID, Contact fromContact) {
        this.fromJID = fromJID;
        this.fromContact = fromContact;
    }

    @Override
    public void run()
    {
        XMPPClient app = XMPPClient.globalInstance();
        int option = JOptionPane.showConfirmDialog(app.getMainWindow(), "Known contact wants to subscribe: "+ fromContact.getName() +" (" + fromJID+"). Allow?" , "Subscription request!", JOptionPane.YES_NO_OPTION);
        if(option == JOptionPane.YES_OPTION)
        {
            SubscriptionListener.sendPresence ( Presence.Type.subscribed,fromJID);
        }
        else
        {
            SubscriptionListener.sendPresence ( Presence.Type.unsubscribed,fromJID);
        }
    }
}

class UnknownSubscribe implements Runnable
{
    String fromJID;
    Contact fromContact;
    public UnknownSubscribe(String fromJID) {
        this.fromJID = fromJID;
    }

    @Override
    public void run()
    {
        XMPPClient app = XMPPClient.globalInstance();
        int option = JOptionPane.showConfirmDialog(app.getMainWindow(), "A new contact wants to subscribe: " + fromJID+". Allow?" , "Subscription request!", JOptionPane.YES_NO_OPTION);
        if(option == JOptionPane.YES_OPTION)
        {
            AddUserDialog aud = new AddUserDialog(app.getMainWindow(), true, fromJID);
            aud.setVisible(true);
            SubscriptionListener.sendPresence ( Presence.Type.subscribed,fromJID);
        }
        else
        {
            SubscriptionListener.sendPresence ( Presence.Type.unsubscribed,fromJID);
        }
    }
}

public class SubscriptionListener implements PacketListener {

    @Override
    public void processPacket(Packet paramPacket)
    {
        XMPPClient app = XMPPClient.globalInstance();
        Roster r = app.conn_man.connection.getRoster();
        ContactManager cman = app.contacts_model;
        if( !(paramPacket instanceof Presence))
            return;
        // we have a presence packet
        Presence presence = (Presence)paramPacket;
        String email = presence.getFrom();
        String bareAddr = StringUtils.parseBareAddress(email);
        
        Contact c = cman.getContact(bareAddr);
        
        Presence.Type kind = presence.getType();
        
        if( kind == Presence.Type.subscribe )
        {
            if(c != null)
            {
                SwingUtilities.invokeLater( new KnownSubscribe(bareAddr,c));
            }
            else
            {
                SwingUtilities.invokeLater( new UnknownSubscribe(bareAddr));
            }
        }
        else if(kind == Presence.Type.unsubscribe)
        {
            if(c != null)
            {
                System.out.println("Known contact wants to unsubscribe: " + bareAddr);
            }
            else
            {
                System.out.println("New contact wants to unsubscribe: " + bareAddr + " - THIS IS MADNESS!");
            }
            sendPresence(Presence.Type.unsubscribed, bareAddr);
        }
        else if(kind == Presence.Type.unsubscribed)
        {
            if(c != null)
            {
                System.out.println("Known contact has unsubscribed: " + bareAddr);
            }
            else
            {
                System.out.println("New contact has unsubscribed: " + bareAddr + " - THIS IS MADNESS!");
            }
        }
        else if(kind == Presence.Type.subscribed)
        {
            if(c != null)
            {
                //System.out.println("Known contact has subscribed: " + bareAddr);
                // nice. whatever.
                SwingUtilities.invokeLater( new KnownSubscribe(bareAddr,c));
            }
            else
            {
                //System.out.println("New contact has subscribed: " + bareAddr + " -- " + r.getSubscriptionMode());
                SwingUtilities.invokeLater( new UnknownSubscribe(bareAddr));
            }
        }
    }
    public static boolean sendPresence (Presence.Type type ,String JID)
    {
        XMPPClient app = XMPPClient.globalInstance();
        Presence newp = new Presence(type);
        newp.setTo(JID);
        app.getConnection().sendPacket(newp);
        return true;
    }
}
