/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dethware.xmpp.client;

/**
 *
 * @author peterix
 */
public interface SettingsListener {
    public void SoundSettingsChanged();
    public void ConnectionSettingsChanged();
    public void StatusSettingsChanged();
}
