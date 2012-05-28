package org.dethware.xmpp.client;

import java.util.EventListener;

public interface ConversationListener extends EventListener
{
    void messageReceived(Conversation conversation, ConversationEntry message);
    void messageSent(Conversation conversation, ConversationEntry message);
}
