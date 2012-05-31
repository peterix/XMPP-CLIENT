/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dethware.xmpp.client;

import java.awt.Color;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 *
 * @author peterix
 */
public class HistoryWindow extends javax.swing.JFrame {

    private Conversation myConversation;
    private StyledDocument doc;
    private Style defaultStyle;
    private Style outStyle;
    private Style outStyleBold;
    private Style inStyle;
    private Style inStyleBold;
    
    /**
     * Creates new form HistoryWindow
     */
    public HistoryWindow( Conversation conv ) {
        myConversation = conv;
        initComponents();
        doc = historyPane.getStyledDocument();
        
        // styles for message formatting
        defaultStyle = doc.addStyle("normal",null);
        StyleConstants.setFontSize(defaultStyle, 14);
        
        inStyle = doc.addStyle("in",defaultStyle);
        StyleConstants.setForeground(inStyle, Color.blue);
        
        inStyleBold = doc.addStyle("in_bold", inStyle);
        StyleConstants.setBold(inStyleBold, true);
        
        outStyle = doc.addStyle("out", defaultStyle);
        StyleConstants.setForeground(outStyle, Color.red);
        
        outStyleBold = doc.addStyle("out_bold", outStyle);
        StyleConstants.setBold(outStyleBold, true);
        
        setTitle("History for: " + conv.getContact().getName() + " (" + conv.getJID() + ")");
        displayOldMessages();
    }

        private void printMessageToLog(ConversationEntry ce)
    {
        String who = "Me";
        if(!ce.isSent())
        {
            who = myConversation.getContact().getName();
        }
        long unixtime =  ce.getUnixTimestampCreated();
        Date created = new Date(unixtime * 1000);
        String DateTime = DateFormat.getDateTimeInstance().format(created);

        Style BoldStyle;
        Style NormalStyle;
        if(ce.isSent())
        {
            BoldStyle = outStyleBold;
            NormalStyle = outStyle;
        }
        else
        {
            BoldStyle = inStyleBold;
            NormalStyle = inStyle;
        }
        try {
            doc.insertString(doc.getLength(), "(" + DateTime + ") ", NormalStyle);
            doc.insertString(doc.getLength(), who + ": ", BoldStyle);
            doc.insertString(doc.getLength(), ce.getBody() + "\n", defaultStyle);
        } catch (BadLocationException ex) {
            Logger.getLogger(ConversationPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void displayOldMessages()
    {
        List<ConversationEntry> entryList = myConversation.acquireHistory();
        for(ConversationEntry ce:entryList)
        {
            printMessageToLog(ce);
        }
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        historyScroller = new javax.swing.JScrollPane();
        historyPane = new javax.swing.JTextPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        historyPane.setContentType("text/html");
        historyPane.setEditable(false);
        historyScroller.setViewportView(historyPane);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 577, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(historyScroller, javax.swing.GroupLayout.DEFAULT_SIZE, 553, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 438, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(historyScroller, javax.swing.GroupLayout.DEFAULT_SIZE, 414, Short.MAX_VALUE)
                    .addContainerGap()))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextPane historyPane;
    private javax.swing.JScrollPane historyScroller;
    // End of variables declaration//GEN-END:variables
}