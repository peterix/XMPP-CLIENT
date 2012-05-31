/**
 * @author Petr Mrázek (xmraze03@stud.fit.vutbr.cz)
 */
package org.dethware.xmpp.client;

public interface ClConnectionListener {
    public void connectionChanged(UserStatus status, String statusString);
}
