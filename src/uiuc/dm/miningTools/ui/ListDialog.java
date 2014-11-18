/*
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
// Updated to support multi selected
package uiuc.dm.miningTools.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

public class ListDialog extends JDialog
        implements ActionListener {

    private static final int DIALOG_WIDTH = 300;
    private static final int DIALOG_HEIGHT = 500;
    private static ListDialog dialog;
    private static ArrayList<String> selectedStrings;
    private JList list;

    public static ArrayList<String> showDialog(Component frameComp,
            Component locationComp,
            String labelText,
            String title,
            String[] possibleValues,
            int[] defaultSelectedIndices,
            String longValue) {
        selectedStrings = new ArrayList<String>();
        Frame frame = JOptionPane.getFrameForComponent(frameComp);
        dialog = new ListDialog(frame,
                locationComp,
                labelText,
                title,
                possibleValues,
                defaultSelectedIndices,
                longValue);
        dialog.setLocation((frameComp.getX() + frameComp.getWidth()), frameComp.getY());
       // dialog.setBounds(frameComp.getX(), frameComp.getY(), DIALOG_WIDTH, DIALOG_HEIGHT);
        dialog.setVisible(true);
        return selectedStrings;
    }

    private void setValues(int[] defaultSelectedIndices) {
        list.setSelectedIndices(defaultSelectedIndices);
    }

    private ListDialog(Frame frame,
            Component locationComp,
            String labelText,
            String title,
            String[] data,
            int[] defaultSelectedIndices,
            String longValue) {
        super(frame, title, true);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent event) {
                List<String> selectedStrs = list.getSelectedValuesList();
                if (selectedStrs.size() < 2) {
                    JOptionPane.showMessageDialog(ListDialog.dialog, "Please select two or more animals.");
                } else {
                    ListDialog.selectedStrings.addAll(selectedStrs);
                    ListDialog.dialog.setVisible(false);
                }
            }
        });

        //Create and initialize the buttons.
        final JButton setButton = new JButton("Set");

        setButton.setActionCommand("Set");
        setButton.addActionListener(this);
        getRootPane().setDefaultButton(setButton);

        //main part of the dialog
        list = new JList(data) {
            //Subclass JList to workaround bug 4832765, which can cause the
            //scroll pane to not let the user easily scroll up to the beginning
            //of the list.  An alternative would be to set the unitIncrement
            //of the JScrollBar to a fixed value. You wouldn't get the nice
            //aligned scrolling, but it should work.
            @Override
            public int getScrollableUnitIncrement(Rectangle visibleRect,
                    int orientation,
                    int direction) {
                int row;
                if (orientation == SwingConstants.VERTICAL
                        && direction < 0 && (row = getFirstVisibleIndex()) != -1) {
                    Rectangle r = getCellBounds(row, row);
                    if ((r.y == visibleRect.y) && (row != 0)) {
                        Point loc = r.getLocation();
                        loc.y--;
                        int prevIndex = locationToIndex(loc);
                        Rectangle prevR = getCellBounds(prevIndex, prevIndex);

                        if (prevR == null || prevR.y >= r.y) {
                            return 0;
                        }
                        return prevR.height;
                    }
                }
                return super.getScrollableUnitIncrement(
                        visibleRect, orientation, direction);
            }
        };

        list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        if (longValue
                != null) {
            list.setPrototypeCellValue(longValue); //get extra space
        }

        list.setLayoutOrientation(JList.HORIZONTAL_WRAP);

        list.setVisibleRowCount(
                -1);
        list.addMouseListener(
                new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (e.getClickCount() == 2) {
                            setButton.doClick(); //emulate button click
                        }
                    }
                });
        JScrollPane listScroller = new JScrollPane(list);

        listScroller.setPreferredSize(
                new Dimension(DIALOG_WIDTH, DIALOG_HEIGHT));
        listScroller.setAlignmentX(LEFT_ALIGNMENT);
        //Create a container so that we can add a title around
        //the scroll pane.  Can't add a title directly to the
        //scroll pane because its background would be white.
        //Lay out the label and scroll pane from top to bottom.
        JPanel listPane = new JPanel();

        listPane.setLayout(
                new BoxLayout(listPane, BoxLayout.PAGE_AXIS));
        JLabel label = new JLabel(labelText);

        label.setLabelFor(list);

        label.setToolTipText(labelText);

        label.setSize(
                25, 11);
        listPane.add(label);

        listPane.add(Box.createRigidArea(new Dimension(0, 5)));
        listPane.add(listScroller);

        listPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        //Lay out the buttons from left to right.
        JPanel buttonPane = new JPanel();

        buttonPane.setLayout(
                new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
        buttonPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        buttonPane.add(Box.createHorizontalGlue());

        buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonPane.add(setButton);
        //Put everything together, using the content pane's BorderLayout.
        Container contentPane = getContentPane();

        contentPane.add(listPane, BorderLayout.CENTER);

        contentPane.add(buttonPane, BorderLayout.PAGE_END);

        //Initialize values.
        setValues(defaultSelectedIndices);

        pack();

        setLocationRelativeTo(locationComp);
    } //Handle clicks on the Set and Cancel buttons.

    @Override
    public void actionPerformed(ActionEvent e) {
        if ("Set".equals(e.getActionCommand())) {
            List<String> selectedStrs = list.getSelectedValuesList();
        //    if (selectedStrs.size() < 2) {
                //JOptionPane.showMessageDialog(this, "Please select two or more animals.");
          //  } else {
                ListDialog.selectedStrings.addAll(selectedStrs);
                ListDialog.dialog.setVisible(false);
           // }
        }
    }
}