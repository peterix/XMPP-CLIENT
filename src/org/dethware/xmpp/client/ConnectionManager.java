/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dethware.xmpp.client;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.jivesoftware.smack.*;
import org.jivesoftware.smack.packet.Presence;

/**
 *
 * @author peterix
 */
public class ConnectionManager implements ConnectionListener, SettingsListener
{
    public ConnectionConfiguration config;
    public XMPPConnection connection;

    private String currentStatusMessage;
    private UserStatus desiredStatus;
    private UserStatus currentStatus;
    
    private List <ClConnectionListener> clConListenerList = new CopyOnWriteArrayList<>();;
    
    public void addClConListener(ClConnectionListener l) {
        clConListenerList.add(l);
        System.out.println("Added listener: " + l.hashCode());
    }
    public void removeClConListener(ClConnectionListener l) {
        clConListenerList.remove(l);
        System.out.println("Removed listener: " + l.hashCode());
    }

    private void fireClConListeners(){
        for(ClConnectionListener l : clConListenerList)
        {
            l.connectionChanged(currentStatus, currentStatusMessage);
        }
    }
    
    ConnectionManager()
    {
        AppSettings settings = AppSettings.globalInstance();
        //TODO: set desired status and status message from AppSettings
        desiredStatus = UserStatus.Offline;
        currentStatus = UserStatus.Offline;
        currentStatusMessage = "Offline";
        config = null;
        connection = null;
    }
    
    @Override
    public void SoundSettingsChanged() {
        // we don't care about those
    }
    
    // low level 'disconnect' method
    // throws away all the connection-related objects
    private void disconnect()
    {
        if(connection == null)
            return;
        XMPPClient cl = XMPPClient.globalInstance();
        if(connection.isConnected())
        {
            connection.disconnect();
        }
        connection = null;
        config = null;
    }
    private void setup_connection_objects()
    {
        AppSettings settings = AppSettings.globalInstance();
        disconnect(); // shut down and destroy old connection, if one exists
        
        //System.setProperty("smack.debugEnabled", "true");
        //XMPPConnection.DEBUG_ENABLED = true;
        config = new ConnectionConfiguration(settings.getServerAddress(),
                                             settings.getServerPort(),
                                             settings.getServerDomain());
        //config.setDebuggerEnabled(true);
        config.setSecurityMode(ConnectionConfiguration.SecurityMode.enabled);
        if(settings.isUseSSL())
        {
            config.setSecurityMode(ConnectionConfiguration.SecurityMode.required);
            config.setSocketFactory(new DummySSLSocketFactory());
        }
        if(!settings.isUsePLAIN())
            config.setSASLAuthenticationEnabled(true);
        
        //config.setCompressionEnabled(false);
        connection = new XMPPConnection(config);
    }
    // low level 'connect' method
    // establishes a connection and logs in
    private boolean connect()
    {
        setup_connection_objects();
        XMPPClient cl = XMPPClient.globalInstance();
        AppSettings settings = cl.settings;
        Roster r = connection.getRoster();
        r.addRosterListener(cl.contacts_model);
        connection.getChatManager().addChatListener(cl.conversationFactory);
        try
        {
            // Connect to the server
            connection.connect();
        }
        catch(XMPPException e)
        {
            //default title and icon
            System.err.println("Couldn't connect!");
            System.err.println(e.getMessage());
            return false;
        }
        try
        {
            // Log into the server
            connection.login(settings.getLogin(), settings.getPassword(), settings.getResource());
        }
        catch (XMPPException e)
        {
            //default title and icon
            System.err.println("Couldn't log in!");
            System.err.println(e.getMessage());
            return false;
        }
        // Disconnect from the server
        System.out.println("Connection established!");
        return true;
    }
    
    @Override
    public void ConnectionSettingsChanged() {
        AppSettings settings = AppSettings.globalInstance();
        // raw disconnect
        setup_connection_objects();
        if(settings.getDesiredUserStatus() != UserStatus.Offline)
        {
            if(!connect())
            {
                System.err.println("Failed to connect after connection settings change!");
            }
            else
            {
                currentStatusMessage = settings.getStatusString();
                currentStatus = desiredStatus = settings.getDesiredUserStatus();
                
                Presence p = new Presence(Presence.Type.available,currentStatusMessage,127,presenceModeForUserStatus(currentStatus));
                connection.sendPacket(p);
                
                System.out.println("Status changed to: " + desiredStatus.toString());
                fireClConListeners();
            }
        }
    }
    
    private Presence.Mode presenceModeForUserStatus(UserStatus s)
    {
        switch(s)
        {
            case Away:
                return Presence.Mode.away;
            case DoNotDisturb:
                return Presence.Mode.dnd;
            case ExtendedAway:
                return Presence.Mode.xa;
            case FreeForChat:
                return Presence.Mode.chat;
            default: // available is the default online presence mode
                return Presence.Mode.available;
        }
    }
    
    public boolean SetDesiredStatus(UserStatus status, String statusMessage){
        desiredStatus = status;
        if(currentStatus == status)
            return true;
        // we start in offline mode! we need to connect.
        if(currentStatus == UserStatus.Offline)
        {
            if(connect())
            {
                Presence.Mode m = presenceModeForUserStatus(status);
                // connected and logged in
                // FIXME: actually track user activity and provide a sensible priority number here
                Presence p = new Presence(Presence.Type.available,statusMessage,127,m);
                connection.sendPacket(p);
                currentStatus = desiredStatus;
                System.out.println("Status changed to: " + desiredStatus.toString());
                fireClConListeners();
                return true;
            }
            else
            {
                // we failed to set the status because connection failed :<
                return false;
            }
        }
        // if we are going offline:
        else if(desiredStatus == UserStatus.Offline)
        {
            // fire before disconnecting to allow things to clean up
            System.out.println("Status changed to: Offline");
            currentStatus = desiredStatus;
            connection.disconnect();
            fireClConListeners();
            return true;
        }
        // otherwise, we jsut notify the server of the change and update out status
        else
        {
            Presence.Mode m = presenceModeForUserStatus(status);
            // connected and logged in
            // FIXME: actually track user activity and provide a sensible priority number here
            Presence p = new Presence(Presence.Type.available,statusMessage,127,m);
            connection.sendPacket(p);
            System.out.println("Status changed to:" + m.toString());
            currentStatus = desiredStatus;
            fireClConListeners();
            return true;
        }
    }

    // connection was closed properly
    @Override
    public void connectionClosed() {
        currentStatus = UserStatus.Offline;
    }

    // connection lost abruptly, will retry
    @Override
    public void connectionClosedOnError(Exception excptn) {
        currentStatus = UserStatus.Offline;
    }

    // connection will retry
    @Override
    public void reconnectingIn(int i) {
        // we don't care ... yet
    }

    // connection established after outage
    @Override
    public void reconnectionSuccessful() {
        //TODO: do we have to re-send, re-login?
        currentStatus = desiredStatus;
    }

    // reconnection failed, will retry
    @Override
    public void reconnectionFailed(Exception excptn) {
        // try again ~_~
    }

    @Override
    public void StatusSettingsChanged() {
        AppSettings settings = AppSettings.globalInstance();
        SetDesiredStatus(settings.getDesiredUserStatus(), settings.getStatusString());
    }
}
