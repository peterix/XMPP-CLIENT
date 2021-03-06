/**
 * @author Petr Mrázek (xmraze03@stud.fit.vutbr.cz)
 */
package org.dethware.xmpp.client;

import org.jivesoftware.smack.packet.Presence;

/**
 * enum for our and other user's presence status
 * @author peterix
 */
public enum UserStatus
{
    Offline,     // type unavailable, no mode
    // type available:
    FreeForChat, // mode chat
    Available,   // mode available (default)
    Away,        // mode away
    DoNotDisturb,// mode dnd
    ExtendedAway;// mode xa
    static UserStatus fromPresence(Presence p)
    {
        if(!p.isAvailable())
        {
            return UserStatus.Offline;
        }
        else
        {
            // special case
            if(p.getMode() == null)
                return UserStatus.Available;
            else switch(p.getMode())
            {
                case chat:
                    return UserStatus.FreeForChat;
                case available:
                    return UserStatus.Available;
                case away:
                    return UserStatus.Away;
                case xa:
                    return UserStatus.ExtendedAway;
                case dnd:
                    return UserStatus.DoNotDisturb;
            }
        }
        return UserStatus.Offline;
    }
}
