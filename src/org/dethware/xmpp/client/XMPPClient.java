package org.dethware.xmpp.client;

import org.jivesoftware.smack.XMPPConnection;

public class XMPPClient
{
    // a singleton emerges
    static private XMPPClient _global = null;
    static public XMPPClient globalInstance()
    {
        if (_global == null)
        {
            _global = new XMPPClient();
        }
        return _global;
    }
    public XMPPClient()
    {
        settings = AppSettings.globalInstance();
        soundUtils = new SoundUtils();
        settings.AddListener(soundUtils);
        settings.loadFromFile();
        
        contacts_model = new ContactManager();

        conn_man = new ConnectionManager();
        conn_man.addClConListener(contacts_model);
        settings.AddListener(conn_man);
        
        conversationFactory = new ConversationFactory();
        conn_man.addClConListener(conversationFactory);
        
        
        // uloz stav/konfiguraci pri ukonceni
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                // cerna magie
                conn_man.SetDesiredStatus(UserStatus.Offline, "exitus");
                settings.saveToFile();
            }
        }));
    }
    
    public ConnectionManager conn_man;
    public AppSettings settings;
    public ContactManager contacts_model;
    public ConversationFactory conversationFactory;
    public SoundUtils soundUtils;
    private MainWindow mainWindow = null;
    
    private synchronized void setMainWindow(MainWindow w)
    {
        mainWindow = w;
    }
    public synchronized MainWindow getMainWindow()
    {
        return mainWindow;
    }
    
    public XMPPConnection getConnection()
    {
        return conn_man.connection;
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /*
         * Set the Nimbus look and feel
         */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /*
         * If Nimbus (introduced in Java SE 6) is not available, stay with the
         * default look and feel. For details see
         * http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /*
         * Load application settings, set up 
         */
        final XMPPClient root = globalInstance();
        /*
         * Create and display the form
         */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                MainWindow w = new MainWindow();
                w.setVisible(true);
                root.setMainWindow(w);
            }
        });
    }
}