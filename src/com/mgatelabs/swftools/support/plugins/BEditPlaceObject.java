// Set the Package Location
package com.mgatelabs.swftools.support.plugins;

// Get Color

import com.mgatelabs.swftools.exploit.hook.BlockEditInterface;
import com.mgatelabs.swftools.support.swf.io.*;
import com.mgatelabs.swftools.support.swf.objects.FMatrix;
import com.mgatelabs.swftools.support.swf.tags.PlaceObject;
import com.mgatelabs.swftools.support.swf.tags.TBlock;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

// Flash IO
// Get Exploit Hooks
// Get the Tags

public class BEditPlaceObject implements Plugin, BlockPlugin {
    // Plugin Information

    public String getName() {
        return "Edit PlaceObject";
    }

    public int getType() {
        return BLOCK;
    }

    // Menu Options
    public String getMenuName() {
        return "PlaceObject";
    }

    public String getMenuPath() {
        return "Edit:Tag";
    }

    // Plugin Tests
    public boolean isApplicable(int id) {
        return (id == 4 || id == 26 || id == 70);
    }

    // All Work Takes Place Here
    public boolean work(BlockEditInterface bei) {
        // Get the Block
        TBlock aBlock = bei.getSelectedBlock();
        int poType = 1;

        // Check for Null
        if (aBlock == null) {
            return false;
        }

        // Get the Data
        byte[] data = aBlock.getData();

        // Read the Block & Get the Tag
        // Get the Bits
        BitInput bi = new BitInput(new ByteArrayInputStream(data));
        // Get the Structures
        BitLibrary bl = new BitLibrary(bi);
        // Get the Type Reader
        TypeReader tr = new TypeReader(bl, bi, bei.getFlashVersion());
        // Get the Tag Reader
        TagReader tag = new TagReader(tr, bl, bi, bei.getFlashVersion(), new FlashFilter());

        // Storage
        PlaceObject aPlaceObject = null;

        // Try to Parse the Data
        try {
            if (aBlock.getId() == 4) {
                // Place Object 1
                aPlaceObject = tag.parsePlaceObject(data.length);
            } else {
                poType = aBlock.getId() == 26 ? 2 : 3;
                // Place Object 2 & 3
                aPlaceObject = tag.parsePlaceObject2(poType);
            }
        } catch (Exception ex) {
            return false;
        }

        BEditPlaceObjectDialog bpod = new BEditPlaceObjectDialog(bei.getFrame(), aPlaceObject);

        if (bpod.po != null) {
            // Save the Current Object to Bits
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                BitOutput bo = new BitOutput(baos);
                TypeWriter tw = new TypeWriter(bo, bei.getFlashVersion());
                tw.PlaceObject(bpod.po, poType);

                byte[] dataNew = baos.toByteArray();

                aBlock.setData(dataNew);
            } catch (Exception ex) {
                System.out.println(ex);
                return false;
            }
        }

        return true;
    }

    private class BEditPlaceObjectDialog extends JDialog {
        private JPanel master;
        private BEditPlaceObjectDialog myself;

      	/*
          PlaceFlagHasClipActions			UB[1]														Has Clip Actions (added to Flash 5)
      	PlaceFlagReserved					UB[1]														Reserved Flags     
      	PlaceFlagHasName					UB[1]														Has name     
      	PlaceFlagHasRatio					UB[1]														Has ratio     
      	PlaceFlagHasColorTransform		UB[1]														Has color transform     
      	PlaceFlagHasMatrix				UB[1]														Has matrix     
      	PlaceFlagHasCharacter			UB[1]														Places a character     
      	PlaceFlagMove						UB[1]														Defines a character to be moved
      	*/

        public JCheckBox checkMove;
        public JCheckBox checkCharacter;
        public JCheckBox checkMatrix;
        public JCheckBox checkColor;
        public JCheckBox checkRatio;
        public JCheckBox checkName;
        public JCheckBox checkClip;

        public JTextField txtDepth;

        public JTextField txtCharID;

        public JTextField txtMatSx;
        public JTextField txtMatSy;
        public JTextField txtMatS0;
        public JTextField txtMatS1;
        public JTextField txtMatTx;
        public JTextField txtMatTy;

        public JTextField txtRatio;

        public JTextField txtName;

        public JTextField txtClip;

        public PlaceObject po;

        private ExploitActionListener eal;

        private JButton butCancel, butSave;

        public BEditPlaceObjectDialog(JFrame owner, PlaceObject po) {
            super(owner, "PlaceObject Editor", true);
            this.po = po;
            myself = this;
            eal = new ExploitActionListener();

            buildPanel();

            this.setContentPane(master);

            this.pack();

            this.show();
        }

        public void buildPanel() {
            master = new JPanel();
            master.setLayout(new BoxLayout(master, BoxLayout.Y_AXIS));

            // Checkbox Portion
            JPanel checkBoxes = new JPanel();
            checkBoxes.setLayout(new BoxLayout(checkBoxes, BoxLayout.Y_AXIS));
            checkMove = new JCheckBox("Has Movement?", po.getPlaceFlagMove());
            checkCharacter = new JCheckBox("Has Character ID?", po.getPlaceFlagHasCharacter());
            checkMatrix = new JCheckBox("Has Matrix?", po.getPlaceFlagHasMatrix());
            checkColor = new JCheckBox("Has Color Transform?", po.getPlaceFlagHasColorTransform());
            checkRatio = new JCheckBox("Has Ratio?", po.getPlaceFlagHasRatio());
            checkName = new JCheckBox("Has Name?", po.getPlaceFlagHasName());
            checkClip = new JCheckBox("Has Clipping Depth?", po.getPlaceFlagHasClipActions());
            checkBoxes.add(checkMove);
            checkBoxes.add(checkCharacter);
            checkBoxes.add(checkMatrix);
            checkBoxes.add(checkColor);
            checkBoxes.add(checkRatio);
            checkBoxes.add(checkName);
            checkBoxes.add(checkClip);
            master.add(checkBoxes);

            // Depth
            JPanel depthFields = xPane();
            txtDepth = new JTextField("");
            depthFields.add(new JLabel("Depth"));
            depthFields.add(txtDepth);
            txtDepth.setText("" + po.getDepth());
            master.add(depthFields);

            // Character ID
            JPanel characterFields = xPane();
            txtCharID = new JTextField("");
            characterFields.add(new JLabel("Character ID"));
            characterFields.add(txtCharID);
            if (po.getPlaceFlagHasCharacter()) {
                txtCharID.setText("" + po.getID());
            }
            master.add(characterFields);

            // Matrix Portion
            JPanel matFields = new JPanel();
            matFields.setLayout(new BoxLayout(matFields, BoxLayout.Y_AXIS));

            txtMatSx = new JTextField("1.0");
            txtMatSy = new JTextField("1.0");
            txtMatS0 = new JTextField("0.0");
            txtMatS1 = new JTextField("0.0");
            txtMatTx = new JTextField("0");
            txtMatTy = new JTextField("0");

            if (po.getPlaceFlagHasMatrix()) {
                txtMatSx.setText("" + po.getMatrix().getScaleX());
                txtMatSy.setText("" + po.getMatrix().getScaleY());

                txtMatS0.setText("" + po.getMatrix().getRotateSkew0());
                txtMatS0.setText("" + po.getMatrix().getRotateSkew1());

                txtMatTx.setText("" + po.getMatrix().getTranslateX());
                txtMatTy.setText("" + po.getMatrix().getTranslateY());
            }

            JPanel JPMatScale = xPane();
            matFields.add(JPMatScale);

            JPanel JPMatSkew = xPane();
            matFields.add(JPMatSkew);

            JPanel JPMatTranslate = xPane();
            matFields.add(JPMatTranslate);

            JPMatScale.add(new JLabel("SX"));
            JPMatScale.add(txtMatSx);
            JPMatScale.add(new JLabel("SY"));
            JPMatScale.add(txtMatSy);

            JPMatSkew.add(new JLabel("S0"));
            JPMatSkew.add(txtMatS0);
            JPMatSkew.add(new JLabel("S1"));
            JPMatSkew.add(txtMatS1);

            JPMatTranslate.add(new JLabel("TX"));
            JPMatTranslate.add(txtMatTx);
            JPMatTranslate.add(new JLabel("TY"));
            JPMatTranslate.add(txtMatTy);
            master.add(new JLabel("Matrix"));
            master.add(matFields);

            // Ratio
            JPanel ratioFields = xPane();
            txtRatio = new JTextField("0");
            ratioFields.add(new JLabel("Ratio"));
            ratioFields.add(txtRatio);
            if (po.getPlaceFlagHasRatio()) {
                txtRatio.setText("" + po.getRatio());
            }
            master.add(ratioFields);

            // Name
            JPanel nameFields = xPane();
            txtName = new JTextField("");
            nameFields.add(new JLabel("Name"));
            nameFields.add(txtName);
            if (po.getPlaceFlagHasName()) {
                txtName.setText("" + po.getName());
            }
            master.add(nameFields);

            // Clip
            JPanel clipFields = xPane();
            txtClip = new JTextField("");
            clipFields.add(new JLabel("Clip Depth"));
            clipFields.add(txtClip);
            if (po.getPlaceFlagHasClipActions()) {
                txtClip.setText("" + po.getClipDepth());
            }
            master.add(clipFields);

            // Buttons
            JPanel buttonFields = xPane();
            butCancel = new JButton("Cancel");
            butCancel.addActionListener(eal);
            butSave = new JButton("Save");
            butSave.addActionListener(eal);
            buttonFields.add(butCancel);
            buttonFields.add(butSave);
            master.add(buttonFields);
        }

        private JPanel xPane() {
            JPanel t = new JPanel();
            t.setLayout(new BoxLayout(t, BoxLayout.X_AXIS));
            return t;
        }

        private class ExploitActionListener implements ActionListener {
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == butCancel) {
                    po = null;
                    myself.dispose();
                } else if (e.getSource() == butSave) {
                    // Verify
                    int errorCount = 0;

                    // Place Object
                    PlaceObject pn = new PlaceObject();

                    // Test the Depth
                    try {
                        int i = Integer.parseInt(txtDepth.getText());
                        pn.setDepth(i);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "Depth is not a number", "Error", JOptionPane.ERROR_MESSAGE);
                        errorCount++;
                    }

                    // Check move flag
                    if (checkMove.isSelected()) {
                        pn.setPlaceFlagMove();
                    }

                    // Check Character Flag
                    if (checkCharacter.isSelected()) {
                        // Test if txtCharID is a Integer
                        try {
                            int i = Integer.parseInt(txtCharID.getText());
                            pn.setPlaceFlagHasCharacter();
                            pn.setID(i);
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(null, "Character ID is not a number", "Error", JOptionPane.ERROR_MESSAGE);
                            errorCount++;
                        }
                    }

                    if (checkMatrix.isSelected()) {
                        try {
                            float sx = Float.parseFloat(txtMatSx.getText());
                            float sy = Float.parseFloat(txtMatSy.getText());
                            float s0 = Float.parseFloat(txtMatS0.getText());
                            float s1 = Float.parseFloat(txtMatS1.getText());
                            long tx = Long.parseLong(txtMatTx.getText());
                            long ty = Long.parseLong(txtMatTy.getText());

                            pn.setPlaceFlagHasMatrix();

                            FMatrix aMatrix = new FMatrix(sx, sy, s0, s1, tx, ty);

                            pn.setMatrix(aMatrix);
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(null, "Error with Matrix information", "Error", JOptionPane.ERROR_MESSAGE);
                            errorCount++;
                        }
                    }

                    if (checkColor.isSelected()) {

                    }

                    if (checkRatio.isSelected()) {
                        try {
                            int i = Integer.parseInt(txtRatio.getText());
                            pn.setPlaceFlagHasRatio();
                            pn.setRatio(i);
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(null, "Ratio is not a number", "Error", JOptionPane.ERROR_MESSAGE);
                            errorCount++;
                        }
                    }

                    if (checkName.isSelected()) {
                        pn.setPlaceFlagHasName();
                        pn.setName(txtName.getText());
                    }

                    if (checkClip.isSelected()) {
                        try {
                            int i = Integer.parseInt(txtClip.getText());
                            pn.setPlaceFlagHasClipActions();
                            pn.setClipDepth(i);
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(null, "Ratio is not a number", "Error", JOptionPane.ERROR_MESSAGE);
                            errorCount++;
                        }
                    }

                    if (errorCount == 0) {
                        po = pn;
                        myself.dispose();
                    }
                }
            }
        }
    }
}