/**
 * @author Petr Mrázek (xmraze03@stud.fit.vutbr.cz)
 */
package org.dethware.xmpp.client;

import java.io.File;
import java.net.URL;
import kuusisto.tinysound.Sound;
import kuusisto.tinysound.TinySound;

/**
 *
 * @author peterix
 */
public class SoundUtils implements SettingsListener{

    double volume = 1.0;
    String msgSoundFileName;
    String loginSoundFileName;
    String logoutSoundFileName;
    Sound msgSound;
    Sound loginSound;
    Sound logoutSound;
    Sound testSound;
    long timeSuspendEnd = 0;

    public SoundUtils() {
        TinySound.init();
        testSound = loadSound("Default", "Im-Message-In.wav");
    }
    
    // suspends login/logout sounds for 5 seconds
    public void suspendLoginSounds()
    {
        timeSuspendEnd = (System.currentTimeMillis() / 1000L) + 5;
    }
    
    public void playTestSound(double volume)
    {
        testSound.play(volume);
    }
    
    public void playMessageSound()
    {
        if(msgSound != null)
            msgSound.play(volume);
    }
    public void playLoginSound()
    {
        if(loginSound == null)
            return;
        long time = (System.currentTimeMillis() / 1000L);
        if(time >= timeSuspendEnd)
            loginSound.play(volume);
    }

    public void playLogoutSound()
    {
        if(logoutSound == null)
            return;
        long time = (System.currentTimeMillis() / 1000L);
        if(time >= timeSuspendEnd)
            logoutSound.play(volume);
    }

  
    private Sound loadSound(String fileName, String resourceName)
    {
        Sound ret = null;
        switch (fileName) {
            case "None":
                ret = null;
                break;
            case "Default":
                URL resource = getClass().getResource(resourceName);
                if(resource == null)
                    ret = null;
                else
                    ret = TinySound.loadSound(resource);
                break;
            default:
                File f = new File(msgSoundFileName);
                if(f.exists())
                {
                    ret = TinySound.loadSound(f);
                }
                break;
        }
        return ret;
    }
    
    @Override
    public void SoundSettingsChanged()
    {
        AppSettings settings = AppSettings.globalInstance();
        volume = settings.getSoundVolume();
        String newFile = settings.getMessageSoundFile();
        if(!newFile.equals(msgSoundFileName))
        {
            msgSoundFileName = newFile;
            if(msgSound != null)
            {
                msgSound.unload();
            }
            msgSound = loadSound(msgSoundFileName, "Im-Message-In.wav");
        }
        newFile = settings.getLoginSoundFile();
        if(!newFile.equals(loginSoundFileName))
        {
            loginSoundFileName = newFile;
            if(loginSound != null)
            {
                loginSound.unload();
            }
            loginSound = loadSound(loginSoundFileName, "Im-Contact-In.wav");
        }
        newFile = settings.getLogoutSoundFile();
        if(!newFile.equals(logoutSoundFileName))
        {
            logoutSoundFileName = newFile;
            if(logoutSound != null)
            {
                logoutSound.unload();
            }
            logoutSound = loadSound(logoutSoundFileName, "Im-Contact-Out.wav");
        }
    }

    @Override
    public void ConnectionSettingsChanged() {
    }

    @Override
    public void StatusSettingsChanged() {
    }
}
