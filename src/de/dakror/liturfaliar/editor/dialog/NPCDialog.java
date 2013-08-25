package de.dakror.liturfaliar.editor.dialog;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SpringLayout;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import layout.SpringUtilities;

import org.json.JSONArray;

import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.editor.MapEditor;
import de.dakror.liturfaliar.editor.NPCButton;
import de.dakror.liturfaliar.item.Equipment;
import de.dakror.liturfaliar.map.creature.NPC;
import de.dakror.liturfaliar.settings.Attributes;
import de.dakror.liturfaliar.settings.CFG;

public class NPCDialog
{
  public NPCDialog(final MapEditor me, final NPCButton exist, final boolean spawner)
  {
    if (me.NPCframe == null)
    {
      me.NPCframe = new JDialog(me.w, spawner);
      me.NPCframe.setTitle("NPC-Bearbeitung" + ((exist != null && !spawner) ? " - NPC #" + exist.ID : ""));
      me.NPCframe.addWindowListener(new WindowAdapter()
      {
        @Override
        public void windowClosed(WindowEvent e)
        {
          me.NPCframe = null;
          me.cursor = null;
          me.map.repaint();
        }
      });
      me.NPCframe.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      if (!spawner) me.NPCframe.setAlwaysOnTop(true);
      me.NPCframe.setResizable(false);
    }
    
    if (exist != null) me.NPCattr = exist.attributes;
    else me.NPCattr = new Attributes();
    
    JPanel p = new JPanel(new SpringLayout());
    
    if (!spawner)
    {
      p.add(new JLabel("X-Position:"));
      me.NPCx = new JTextField(15);
      if (exist != null) me.NPCx.setText(exist.x + "");
      p.add(me.NPCx);
      
      p.add(new JLabel("Y-Position:"));
      me.NPCy = new JTextField(15);
      if (exist != null) me.NPCy.setText(exist.y + "");
      
      p.add(me.NPCy);
      
      p.add(new JLabel("Blickrichtung:"));
      me.NPCdir = new JComboBox<String>(new String[] { "Unten", "Links", "Rechts", "Oben" });
      if (exist != null) me.NPCdir.setSelectedIndex(exist.dir);
      
      me.NPCdir.addItemListener(new ItemListener()
      {
        
        @Override
        public void itemStateChanged(ItemEvent e)
        {
          if (e.getStateChange() == ItemEvent.SELECTED) updateNPCDialogPreview(me);
        }
      });
      p.add(me.NPCdir);
    }
    
    p.add(new JLabel("Name:"));
    me.NPCname = new JTextField(15);
    if (exist != null) me.NPCname.setText(exist.name);
    
    p.add(me.NPCname);
    
    p.add(new JLabel("Sprite:"));
    me.NPCsprite = new JComboBox<String>(NPC.CHARS);
    if (exist != null) me.NPCsprite.setSelectedItem(exist.sprite);
    
    else me.NPCsprite.setSelectedIndex(0);
    
    me.NPCsprite.addItemListener(new ItemListener()
    {
      @Override
      public void itemStateChanged(ItemEvent e)
      {
        if (e.getStateChange() == ItemEvent.SELECTED) updateNPCDialogPreview(me);
      }
    });
    
    p.add(me.NPCsprite);
    
    p.add(new JLabel("Vorschau:"));
    me.NPCpreview = new JLabel();
    me.NPCpreview.setPreferredSize(new Dimension(32, 48));
    updateNPCDialogPreview(me);
    p.add(me.NPCpreview);
    
    p.add(new JLabel("Bewegungsgeschwindigkeit:"));
    me.NPCspeed = new JSpinner(new SpinnerNumberModel(1.0, 0, 20, 0.1));
    if (exist != null) me.NPCspeed.setValue(exist.speed);
    
    p.add(me.NPCspeed);
    
    p.add(new JLabel("zufällige Bewegung:"));
    me.NPCmove = new JCheckBox();
    if (exist != null) me.NPCmove.setSelected(exist.move);
    
    me.NPCmove.addChangeListener(new ChangeListener()
    {
      @Override
      public void stateChanged(ChangeEvent e)
      {
        me.NPCmoveT.setEnabled(((JCheckBox) e.getSource()).isSelected());
      }
    });
    p.add(me.NPCmove);
    
    p.add(new JLabel("Zufallsbewegung-Interval. (ms):"));
    me.NPCmoveT = new JSpinner(new SpinnerNumberModel(3000, 0, 1000000000, 100));
    if (exist != null) me.NPCmoveT.setValue(exist.moveT);
    
    me.NPCmoveT.setEnabled(me.NPCmove.isSelected());
    p.add(me.NPCmoveT);
    
    p.add(new JLabel("zufälliges Blicken:"));
    me.NPClook = new JCheckBox();
    if (exist != null) me.NPClook.setSelected(exist.look);
    
    me.NPClook.addChangeListener(new ChangeListener()
    {
      
      @Override
      public void stateChanged(ChangeEvent e)
      {
        me.NPClookT.setEnabled(((JCheckBox) e.getSource()).isSelected());
      }
    });
    p.add(me.NPClook);
    
    p.add(new JLabel("Zufallsblicken-Interval. (ms):"));
    me.NPClookT = new JSpinner(new SpinnerNumberModel(3000, 0, 1000000000, 100));
    if (exist != null) me.NPClookT.setValue(exist.lookT);
    
    me.NPClookT.setEnabled(me.NPClook.isSelected());
    p.add(me.NPClookT);
    
    p.add(new JLabel("Künstliche Intelligenz:"));
    me.NPCai = new JComboBox<String>(new String[] { "MeleeAI" }); // TODO: Keep in sync
    if (exist != null) me.NPCai.setSelectedItem(exist.ai);
    p.add(me.NPCai);
    
    p.add(new JLabel("immer feindlich:"));
    me.NPChostile = new JCheckBox();
    if (exist != null) me.NPChostile.setSelected(exist.hostile);
    p.add(me.NPChostile);
    
    p.add(new JLabel("Attribute:"));
    JButton attr = new JButton("Bearbeiten");
    attr.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent e)
      {
        new AttributesDialog(me, me.NPCattr, false);
        me.NPCattr = me.tmpAttr;
      }
    });
    p.add(attr);
    
    p.add(new JLabel());
    me.NPCok = new JButton((spawner) ? "OK" : "Platzieren");
    me.NPCok.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent e)
      {
        if (spawner)
        {
          Equipment eq = (me.spawnerNPC != null) ? me.spawnerNPC.getEquipment() : new Equipment();
          me.spawnerNPC = new NPCButton(0, 0, me.NPCpreview.getPreferredSize().width - CFG.BOUNDMALUS, me.NPCpreview.getPreferredSize().height - CFG.BOUNDMALUS, 0, me.NPCname.getText(), me.NPCsprite.getSelectedItem().toString(), (double) me.NPCspeed.getValue(), me.NPCmove.isSelected(), me.NPClook.isSelected(), (int) me.NPCmoveT.getValue(), (int) me.NPClookT.getValue(), ((ImageIcon) me.NPCpreview.getIcon()).getImage(), me.NPChostile.isSelected(), -1, me.NPCai.getSelectedItem().toString(), me);
          me.spawnerNPC.attributes = me.NPCattr;
          me.spawnerNPC.setEquipment(eq);
          
          me.NPCframe.dispose();
        }
        else
        {
          JSONArray talk = null;
          Equipment equipment = null;
          if (exist != null)
          {
            talk = exist.talk;
            equipment = exist.getEquipment();
            
            if (me.NPClastID == exist.ID + 1) me.NPClastID--;
            
            me.map.remove(exist);
          }
          NPCButton b = me.addNPC(null);
          if (talk != null) b.talk = talk;
          
          if (equipment != null) b.setEquipment(equipment);
          
          new NPCDialog(me, b, spawner);
        }
      }
    });
    p.add(me.NPCok);
    
    SpringUtilities.makeCompactGrid(p, 12 + ((!spawner) ? 3 : 0), 2, 6, 6, 6, 6);
    
    me.NPCframe.setContentPane(p);
    me.NPCframe.pack();
    me.NPCframe.setLocationRelativeTo(me.w);
    me.NPCframe.setVisible(true);
  }
  
  private void updateNPCDialogPreview(MapEditor me)
  {
    String sprite = me.NPCsprite.getSelectedItem().toString();
    BufferedImage image = (BufferedImage) Viewport.loadImage("char/chars/" + sprite + ".png");
    me.NPCpreview.setPreferredSize(new Dimension(image.getWidth() / 4, image.getHeight() / 4));
    me.NPCpreview.setIcon(new ImageIcon(image.getSubimage(0, image.getHeight() / 4 * ((me.NPCdir != null) ? me.NPCdir.getSelectedIndex() : 0), image.getWidth() / 4, image.getHeight() / 4)));
    me.NPCframe.invalidate();
    me.NPCframe.pack();
  }
}
