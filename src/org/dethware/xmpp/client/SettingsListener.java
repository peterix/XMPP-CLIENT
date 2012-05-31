/**
 * @author Petr Mrázek (xmraze03@stud.fit.vutbr.cz)
 */
package org.dethware.xmpp.client;

public interface SettingsListener {
    public void SoundSettingsChanged();
    public void ConnectionSettingsChanged();
    public void StatusSettingsChanged();
}
