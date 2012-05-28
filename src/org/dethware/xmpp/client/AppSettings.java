/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dethware.xmpp.client;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 *
 * @author peterix
 */
public class AppSettings {
    // singleton instance
    static private AppSettings _global = null;
    private List <SettingsListener> list;
    private boolean connectionChanged;
    private boolean soundChanged;
    private boolean statusChanged;
    
    // this notifies listeners of changes.
    // Done this way to batch the changes so connection settings don't change
    // variable by variable.
    public void NotifyListeners()
    {
        for (Iterator<SettingsListener> i=list.iterator(); i.hasNext(); )
        {
            SettingsListener l = i.next();
            //try {
                if(soundChanged)
                    l.SoundSettingsChanged();
                if(connectionChanged)
                    l.ConnectionSettingsChanged();
                if(statusChanged)
                    l.StatusSettingsChanged();
            //}
            /*
            catch (RuntimeException e) {
                System.err.println("Unexpected exception in listener:\n" + e.getMessage());
                i.remove();
            }
            * 
            */
        }
        soundChanged = false;
        connectionChanged = false;
        statusChanged = false;
    }
    
    public boolean AddListener(SettingsListener l)
    {
        return list.add(l);
    }
    
    public boolean RemoveListener(SettingsListener l)
    {
        return list.remove(l);
    }
    
    // Sound
    private String messageSoundFile;
    private String loginSoundFile;
    private String logoutSoundFile;
    private double soundVolume;

    /**
     * @return the sound filename
     */
    public String getMessageSoundFile() {
        return messageSoundFile;
    }

    /**
     * @param soundFile the new sound filename
     */
    public void setMessageSoundFile(String soundFile) {
        if(!soundFile.equals(this.messageSoundFile))
            soundChanged = true;
        this.messageSoundFile = soundFile;
    }

    /**
     * @return the sound filename
     */
    public String getLoginSoundFile() {
        return loginSoundFile;
    }
    
    /**
     * @param soundFile the new sound filename
     */
    public void setLoginSoundFile(String soundFile) {
        if(!soundFile.equals(this.loginSoundFile))
            soundChanged = true;
        this.loginSoundFile = soundFile;
    }
    
    /**
     * @return the sound filename
     */
    public String getLogoutSoundFile() {
        return logoutSoundFile;
    }
    
    /**
     * @param soundFile the new sound filename
     */
    public void setLogoutSoundFile(String soundFile) {
        if(!soundFile.equals(this.logoutSoundFile))
            soundChanged = true;
        this.logoutSoundFile = soundFile;
    }
    
    /**
     * @return the volume
     */
    public double getSoundVolume() {
        return soundVolume;
    }

    /**
     * @param soundVolume the volume
     */
    public void setSoundVolume(double soundVolume) {
        if(this.soundVolume != soundVolume)
            soundChanged = true;
        this.soundVolume = soundVolume;
    }

    /**
     * @return the preset
     */
    public PresetType getPreset() {
        return preset;
    }

    /**
     * @param preset the preset to set
     */
    public void setPreset(PresetType preset) {
        if(this.preset != preset)
            connectionChanged = true;
        this.preset = preset;
    }

    /**
     * @return the serverAddress
     */
    public String getServerAddress() {
        return serverAddress;
    }

    /**
     * @param serverAddress the serverAddress to set
     */
    public void setServerAddress(String serverAddress) {
        if(!serverAddress.equals(this.serverAddress))
            connectionChanged = true;
        this.serverAddress = serverAddress;
    }

    /**
     * @return the serverPort
     */
    public int getServerPort() {
        return serverPort;
    }

    /**
     * @param serverPort the serverPort to set
     */
    public void setServerPort(int serverPort) {
        if(this.serverPort != serverPort)
            connectionChanged = true;
        this.serverPort = serverPort;
    }

    /**
     * @return the serverDomain
     */
    public String getServerDomain() {
        return serverDomain;
    }

    /**
     * @param serverDomain the serverDomain to set
     */
    public void setServerDomain(String serverDomain) {
        if(!serverDomain.equals(this.serverDomain))
            connectionChanged = true;
        this.serverDomain = serverDomain;
    }

    /**
     * @return the useSSL
     */
    public boolean isUseSSL() {
        return useSSL;
    }

    /**
     * @param useSSL the useSSL to set
     */
    public void setUseSSL(boolean useSSL) {
        if(useSSL != this.useSSL)
            connectionChanged = true;
        this.useSSL = useSSL;
    }

    /**
     * @return the usePLAIN
     */
    public boolean isUsePLAIN() {
        return usePLAIN;
    }

    /**
     * @param usePLAIN the usePLAIN to set
     */
    public void setUsePLAIN(boolean usePLAIN) {
        if(usePLAIN != this.usePLAIN)
            connectionChanged = true;
        this.usePLAIN = usePLAIN;
    }

    /**
     * @return the login
     */
    public String getLogin() {
        return login;
    }

    /**
     * @param login the login to set
     */
    public void setLogin(String login) {
        if(!login.equals(this.login))
            connectionChanged = true;
        this.login = login;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        if(!password.equals(this.password))
            connectionChanged = true;
        this.password = password;
    }

    /**
     * @return the resource
     */
    public String getResource() {
        return resource;
    }

    /**
     * @param resource the resource to set
     */
    public void setResource(String resource) {
        if(!resource.equals(this.resource))
            connectionChanged = true;
        this.resource = resource;
    }

    /**
     * @return the desiredUserStatus
     */
    public UserStatus getDesiredUserStatus() {
        return desiredUserStatus;
    }

    /**
     * @param desiredUserStatus the desiredUserStatus to set
     */
    public void setDesiredUserStatus(UserStatus desiredUserStatus) {
        if(this.desiredUserStatus != desiredUserStatus)
            statusChanged = true;
        this.desiredUserStatus = desiredUserStatus;
    }

    /**
     * @return the statusString
     */
    public String getStatusString() {
        return statusString;
    }

    /**
     * @param statusString the statusString to set
     */
    public void setStatusString(String statusString) {
        if(!statusString.equals(this.statusString))
            statusChanged = true;
        this.statusString = statusString;
    }
    
    // Connection
    public enum PresetType { None, Google }
    private PresetType preset;
    private String serverAddress;
    private int serverPort;
    private String serverDomain;
    private boolean useSSL;
    private boolean usePLAIN;
    
    // Login
    private String login;
    private String password;
    private String resource;
    
    // Current desired status / status message
    private UserStatus desiredUserStatus;
    private String statusString;
    
    public final void saveToFile()
    {
        Properties props = new Properties();
        props.setProperty("messageSoundFile", getMessageSoundFile());
        props.setProperty("loginSoundFile", getLoginSoundFile());
        props.setProperty("logoutSoundFile", getLogoutSoundFile());
        props.setProperty("soundVolume", Double.toString(getSoundVolume()));
        props.setProperty("preset", preset.toString());
        props.setProperty("serverAddress", getServerAddress());
        props.setProperty("serverPort", Integer.toString(getServerPort()));
        props.setProperty("serverDomain", getServerDomain());
        props.setProperty("useSSL", Boolean.toString(isUseSSL()));
        props.setProperty("usePLAIN", Boolean.toString(isUsePLAIN()));
        props.setProperty("login", getLogin());
        props.setProperty("password", getPassword());
        props.setProperty("resource", getResource());
        props.setProperty("desiredUserStatus", desiredUserStatus.toString());
        props.setProperty("statusString", statusString);
        try
        {
            OutputStream file = new FileOutputStream(new File ("XMPP-Client.properties"));
            props.store(file, "Something awful this way comes...");
        }
        catch(IOException e)
        {
            System.out.println("error" + e);
        }
    }
    public final void loadFromFile()
    {
        Properties props = new Properties();
        try
        {
            InputStream file = new FileInputStream(new File("XMPP-Client.properties")) ;
            props.load(file);
        } 
        catch(IOException e)
        {
            System.out.println("error" + e);
        }
        
        String temp;
        temp = props.getProperty("messageSoundFile");
        if(temp != null)
            setMessageSoundFile(temp);
        temp = props.getProperty("loginSoundFile");
        if(temp != null)
            setLoginSoundFile(temp);
        temp = props.getProperty("logoutSoundFile");
        if(temp != null)
            setLogoutSoundFile(temp);

        temp = props.getProperty("soundVolume");
        if(temp != null)
        {
            try
            {
                setSoundVolume(Double.parseDouble(temp));
            }
            catch (NumberFormatException e)
            {
            }
        }

        temp = props.getProperty("preset");
        if(temp != null)
        {
            if(temp.equals("Google"))
                setPreset(PresetType.Google);
            else
                setPreset(PresetType.None);
        }
        
        temp = props.getProperty("serverAddress");
        if(temp != null)
            setServerAddress(temp);

        temp = props.getProperty("serverDomain");
        if(temp != null)
            setServerDomain(temp);

        temp = props.getProperty("serverPort");
        if(temp != null)
        {
            try
            {
                setServerPort(Integer.parseInt(temp));
            }
            catch (NumberFormatException e)
            {
            }
        }

        temp = props.getProperty("useSSL");
        if(temp != null)
        {
            setUseSSL(Boolean.parseBoolean(temp));
        }

        temp = props.getProperty("usePLAIN");
        if(temp != null)
        {
            setUsePLAIN(Boolean.parseBoolean(temp));
        }

        temp = props.getProperty("login");
        if(temp != null)
            setLogin(temp);
        temp = props.getProperty("resource");
        if(temp != null)
            setResource(temp);
        temp = props.getProperty("password");
        if(temp != null)
            setPassword(temp);
        
        temp = props.getProperty("desiredUserStatus");
        if(temp != null)
        {
            try
            {
                setDesiredUserStatus(UserStatus.valueOf(temp));
            }
            catch(IllegalArgumentException e)
            {
                // throw away, we keep the default value
            }
        }
        temp = props.getProperty("statusString");
        if(temp != null)
            setStatusString(temp);
    }
    
    public final void setDefaults()
    {
        setMessageSoundFile("Default");
        setLoginSoundFile("Default");
        setLogoutSoundFile("Default");
        setSoundVolume(1.0);
        
        setPreset(PresetType.None);
        setServerAddress("");
        setServerPort(5222);
        setServerDomain("");
        setUseSSL(false);
        setUsePLAIN(true);
        
        setLogin("");
        setPassword("");
        setResource("");
        
        setDesiredUserStatus(UserStatus.Offline);
        setStatusString("Offline");
    }
    
    static public AppSettings globalInstance(){
        if (_global == null) {
            _global = new AppSettings();
        }
        return _global;
    }
   
    public AppSettings()
    {
        list = new ArrayList <>();
        statusChanged = false;
        connectionChanged = false;
        statusChanged = false;
        setDefaults();
    }
}
