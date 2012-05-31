/**
 * @author Petr Mrázek (xmraze03@stud.fit.vutbr.cz)
 */
package org.dethware.xmpp.client;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

public class Conversation implements MessageListener, Comparable<Conversation> {
    // who are we talking to
    private Contact other;
    private final List<ConversationListener> conversationListeners = 
            new ArrayList<>();
    private final ArrayList<ConversationEntry> Messages = new ArrayList<>();
    private int numHistorical = 0;
    private Chat currentChat = null;
    private FileWriter historyWriter;
    
    public Conversation(String otherJID)
    {
        XMPPClient app = XMPPClient.globalInstance();
        other = app.contacts_model.getContact(otherJID);
        String historyFileName = otherJID + ".history";
        // try to read the history file for this conversation
        try{
            FileInputStream fstream = new FileInputStream(historyFileName);
            // Get the object of DataInputStream
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            //Read File Line By Line
            while ((strLine = br.readLine()) != null)
            {
                ConversationEntry ce;
                try
                {
                     ce = new ConversationEntry( strLine );
                }
                // empty line at the end
                catch(Error e)
                {
                    break;
                }
                Messages.add(ce);
                numHistorical ++;
            }
            //Close the input stream
            in.close();
        }
        catch (Exception e)
        {
            System.err.println("Error: " + e.getMessage());
        }
        // open the writer...
        try {
            historyWriter = new FileWriter(historyFileName, true);
        } catch (IOException ex) {
            Logger.getLogger(Conversation.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public List<ConversationEntry> acquireMessagesForPanel()
    {
        int start = Math.max( numHistorical - 10, 0);
        return Messages.subList(start, Messages.size());
    }
    
    public List<ConversationEntry> acquireHistory()
    {
        return Messages.subList(0, numHistorical);
    }
    
    public void markAllMessagesAsHistory()
    {
        for(ConversationEntry ce: Messages)
        {
            ce.setHistorical(true);
        }
    }
    
    public void addConversationListener(ConversationListener l) {
        synchronized(conversationListeners)
        {
            conversationListeners.remove(l);
            conversationListeners.add(l);
        }
    }

    public void removeConversationListener(ConversationListener l) {
            synchronized(conversationListeners) {
                    conversationListeners.remove(l);
            }
    }

    public void fireMessageReceived(ConversationEntry en) {
        synchronized(conversationListeners) {
            for (ConversationListener l : conversationListeners) {
                l.messageReceived(this, en);
            }
        }
    }

    public void fireMessageSent(ConversationEntry en) {
        synchronized(conversationListeners) {
            for (ConversationListener l : conversationListeners) {
                l.messageSent(this, en);
            }
        }
    }

    // arriving message
    @Override
    public void processMessage(Chat chat, Message msg) {
        String s = msg.getBody();
        if(s != null)
        {
            // save to the history file, make sure it gets written to the medium
            ConversationEntry ce = new ConversationEntry(s, false);
            logMessage(ce);
            fireMessageReceived(ce);
        }
    }
    // departing message
    public boolean sendMessage (String body)
    {
        if(currentChat == null)
        {
            XMPPClient app = XMPPClient.globalInstance();
            XMPPConnection conn = app.getConnection();
            if( !conn.isConnected() || !conn.isAuthenticated())
                return false;
            currentChat = conn.getChatManager().createChat(other.getJID(), this);
        }
        try {
            currentChat.sendMessage(body);
        } catch (XMPPException ex) {
            return false;
        }
        ConversationEntry ce = new ConversationEntry(body,true);
        logMessage(ce);
        fireMessageSent(ce);
        return true;
    }
    
    public void setChat(Chat c)
    {
        if(currentChat != null)
        {
            currentChat.removeMessageListener(this);
        }
        currentChat = c;
        if(c != null)
            currentChat.addMessageListener(this);
    }

    /**
     * @return the JID
     */
    public String getJID() {
        return other.getJID();
    }

    private void logMessage(ConversationEntry ce)
    {
        try {
            historyWriter.write(ce.toEncoded());
            historyWriter.flush();
        } catch (IOException ex) {
            Logger.getLogger(Conversation.class.getName()).log(Level.SEVERE, null, ex);
        }
        Messages.add(ce);
    }
    
    @Override
    public int compareTo(Conversation o) {
        return other.compareTo(o.other);
    }
    
    public Contact getContact()
    {
        return other;
    }
}
