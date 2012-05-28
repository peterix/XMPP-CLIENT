/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dethware.xmpp.client;

import java.util.Scanner;
import org.jivesoftware.smack.util.Base64;

public class ConversationEntry
{
    // create a new chat entry
    public ConversationEntry(String contents, boolean sent) {
        unixTimestampCreated = System.currentTimeMillis() / 1000L;
        this.body = contents;
        this.sent = sent;
        this.historical = false;
    }
    // a chat entry loaded from before
    public ConversationEntry(String encoded) throws Error
    {
        Scanner s = new Scanner(encoded);
        if(!s.hasNext())
            throw new Error("final");
        unixTimestampCreated = s.nextLong();
        sent = s.nextBoolean();
        body = new String(Base64.decode(s.next()));
        historical = true;
    }
    public String toEncoded ()
    {
        return unixTimestampCreated + " " + sent + " " + Base64.encodeBytes(body.getBytes(),Base64.DONT_BREAK_LINES) + "\n";
    }
    private long unixTimestampCreated;
    private String body;
    private boolean sent;
    private boolean historical;

    /**
     * @return the unixTimestampCreated
     */
    public long getUnixTimestampCreated() {
        return unixTimestampCreated;
    }

    /**
     * @param unixTimestampCreated the unixTimestampCreated to set
     */
    public void setUnixTimestampCreated(long unixTimestampCreated) {
        this.unixTimestampCreated = unixTimestampCreated;
    }

    /**
     * @return the body
     */
    public String getBody() {
        return body;
    }

    /**
     * @param body the body to set
     */
    public void setBody(String contents) {
        this.body = contents;
    }

    /**
     * @return the sent
     */
    public boolean isSent() {
        return sent;
    }

    /**
     * @param sent the sent to set
     */
    public void setSent(boolean sent) {
        this.sent = sent;
    }

    /**
     * @return the historical
     */
    public boolean isHistorical() {
        return historical;
    }

    /**
     * @param historical the historical to set
     */
    public void setHistorical(boolean historical) {
        this.historical = historical;
    }
}