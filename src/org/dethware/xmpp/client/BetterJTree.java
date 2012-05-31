/**
 * @author Petr Mrázek (xmraze03@stud.fit.vutbr.cz)
 */
package org.dethware.xmpp.client;

import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.TreeNode;


public class BetterJTree extends JTree {

    public BetterJTree(TreeNode rootNode) {
        super(rootNode);
        init();
    }
    public BetterJTree() {
        init();
    }

    private void init() {
        addMouseListener(new MouseAdapter(){
            @Override
            public void mousePressed(MouseEvent e)
            {
                if (SwingUtilities.isLeftMouseButton(e))
                {
                    int closestRow = getClosestRowForLocation(e.getX(), e.getY());
                    Rectangle closestRowBounds = getRowBounds(closestRow);
                    if(e.getY() >= closestRowBounds.getY() && e.getY() < closestRowBounds.getY() + closestRowBounds.getHeight())
                    {
                        if(e.getX() > closestRowBounds.getX() && closestRow < getRowCount())
                            setSelectionRow(closestRow);
                    } else
                        setSelectionRow(-1);
                }
            }
        });
    }
}