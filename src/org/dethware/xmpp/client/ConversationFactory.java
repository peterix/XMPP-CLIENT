/**
 * @author Petr Mrázek (xmraze03@stud.fit.vutbr.cz)
 */
package org.dethware.xmpp.client;

import java.util.Map;
import java.util.TreeMap;
import javax.swing.SwingUtilities;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.util.StringUtils;

public class ConversationFactory implements ChatManagerListener, ClConnectionListener, MessageListener{
    private Map <String, Conversation> allConversations = new TreeMap<>();
    
    public Conversation getConversationForJID(String JID)
    {
        // either get existing conversation, or create a new one
        Conversation conv = allConversations.get(JID);
        if(conv == null)
            conv = new Conversation(JID);
        allConversations.put(JID, conv);
        return conv;
    }

    public ConversationFactory() {
    }

    @Override
    public void chatCreated(Chat chat, boolean createdLocally)
    {
        // a new conversation! How exciting :D
        if(!createdLocally)
        {
            String other = chat.getParticipant();
            other = StringUtils.parseBareAddress(other);
            System.out.println( other + " created a chat with us!" );
            Conversation conv = getConversationForJID(other);
            conv.setChat(chat);
        }
        chat.addMessageListener(this);
    }

    @Override
    public void connectionChanged(UserStatus status, String statusString) {
        // invalidate all chats when offline
        if(status == UserStatus.Offline)
        {
            for (Map.Entry<String, Conversation> entry : allConversations.entrySet())
            {
                entry.getValue().setChat(null);
            }
        }
    }
    
    class MyRunnable implements Runnable
    {
        Chat chat;
        MyRunnable(Chat c)
        {
            this.chat = c;
        }
        @Override
        public void run() {
            XMPPClient app = XMPPClient.globalInstance();
            app.soundUtils.playMessageSound();
            MainWindow w = app.getMainWindow();
            String temp = chat.getParticipant();
            Conversation c = getConversationForJID(StringUtils.parseBareAddress(temp));
            w.showConversation(c,true);
        }
    }
    
    @Override
    public void processMessage(Chat chat, Message msg) {
        String from = msg.getFrom();
        if(from != null && msg.getBody() != null)
        {
            SwingUtilities.invokeLater(new MyRunnable(chat));
        }
    }
    
}
