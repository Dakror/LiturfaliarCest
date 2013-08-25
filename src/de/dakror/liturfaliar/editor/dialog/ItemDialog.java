package de.dakror.liturfaliar.editor.dialog;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Comparator;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SpringLayout;

import layout.SpringUtilities;
import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.editor.MapEditor;
import de.dakror.liturfaliar.item.Categories;
import de.dakror.liturfaliar.item.Item;
import de.dakror.liturfaliar.item.Types;
import de.dakror.liturfaliar.item.action.EmptyAction;
import de.dakror.liturfaliar.item.action.ItemAction;
import de.dakror.liturfaliar.item.action.PotionAction;
import de.dakror.liturfaliar.item.action.WeaponAction;
import de.dakror.liturfaliar.settings.Attributes;
import de.dakror.liturfaliar.settings.DamageType;
import de.dakror.liturfaliar.util.FileManager;

public class ItemDialog
{
  public ItemDialog(final MapEditor me, final Item exist)
  {
    if (exist != null)
    {
      me.tmpAttributes = exist.getAttributes();
      me.tmpRequires = exist.getRequirements();
    }
    
    final JDialog itemFrame = new JDialog(me.w, true);
    itemFrame.setTitle("Item-Bearbeitung");
    itemFrame.setResizable(false);
    itemFrame.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    
    final JPanel p = new JPanel(new SpringLayout());
    
    JLabel l = new JLabel("Icon-X:");
    p.add(l);
    final JSpinner ix = new JSpinner(new SpinnerNumberModel(0, 0, 16, 1));
    if (exist != null) ix.setValue(exist.getIconPoint().x);
    p.add(ix);
    
    l = new JLabel("Icon-Y:");
    p.add(l);
    final JSpinner iy = new JSpinner(new SpinnerNumberModel(0, 0, 629, 1));
    if (exist != null) iy.setValue(exist.getIconPoint().y);
    p.add(iy);
    
    l = new JLabel("Korrektur-X:");
    p.add(l);
    final JSpinner cx = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
    if (exist != null) cx.setValue(exist.getCorrectionX());
    p.add(cx);
    
    l = new JLabel("Korrektur-Y:");
    p.add(l);
    final JSpinner cy = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
    if (exist != null) cy.setValue(exist.getCorrectionY());
    p.add(cy);
    
    l = new JLabel("Name:");
    p.add(l);
    final JTextField name = new JTextField(15);
    if (exist != null) name.setText(exist.getName());
    p.add(name);
    
    final JLabel preview = new JLabel();
    Image body1 = Viewport.loadImage("char/skin/man_f.png");
    Image body2 = Viewport.loadImage("char/skin/man_b.png");
    BufferedImage bi = new BufferedImage(body1.getWidth(null), body1.getHeight(null), BufferedImage.TYPE_INT_ARGB);
    Graphics g = bi.getGraphics();
    g.drawImage(body2, 0, 0, null);
    g.drawImage(body1, 0, 0, null);
    preview.setIcon(new ImageIcon(bi));
    preview.setPreferredSize(new Dimension(body1.getWidth(null), body1.getHeight(null)));
    
    l = new JLabel("Char-Pfad:");
    p.add(l);
    final JComboBox<String> path = new JComboBox<String>();
    p.add(path);
    
    p.add(new JLabel());
    p.add(preview);
    
    l = new JLabel("Typ:");
    p.add(l);
    Types[] types = Types.values();
    Arrays.sort(types, new Comparator<Types>()
    {
      @Override
      public int compare(Types o1, Types o2)
      {
        return o1.name().compareTo(o2.name());
      }
    });
    final JComboBox<Types> type = new JComboBox<Types>(types);
    
    path.addItemListener(new ItemListener()
    {
      @Override
      public void itemStateChanged(ItemEvent e)
      {
        if (e.getStateChange() != ItemEvent.SELECTED) return;
        
        Image body1 = Viewport.loadImage("char/skin/man_f.png");
        Image body2 = Viewport.loadImage("char/skin/man_b.png");
        Image part = Viewport.loadImage("char/" + ((Types) type.getSelectedItem()).getCategory().name().toLowerCase() + "/" + path.getSelectedItem().toString() + ".png");
        if (part == null) part = Viewport.loadImage("char/" + ((Types) type.getSelectedItem()).getCategory().name().toLowerCase() + "/" + path.getSelectedItem().toString() + "_f.png");
        
        BufferedImage bi = new BufferedImage(body1.getWidth(null), body1.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics g = bi.getGraphics();
        if (!((Types) type.getSelectedItem()).equals(Types.SKIN)) g.drawImage(body2, 0, 0, null);
        if (!((Types) type.getSelectedItem()).equals(Types.SKIN)) g.drawImage(body1, 0, 0, null);
        g.drawImage(part, 0, 0, null);
        preview.setIcon(new ImageIcon(bi));
      }
    });
    
    
    type.addItemListener(new ItemListener()
    {
      @Override
      public void itemStateChanged(ItemEvent e)
      {
        Types i = (Types) e.getItem();
        path.removeAllItems();
        if (Arrays.asList(Categories.EQUIPS).contains(i.getCategory()))
        {
          String[] parts = FileManager.getCharParts(i.getCategory().name().toLowerCase());
          for (String part : parts)
          {
            if (part.indexOf("_b.png") > -1 || part.indexOf("_m.png") > -1) continue;
            path.addItem(part.replace("_f.png", "").replace(".png", ""));
          }
          if (exist != null && exist.getCharPath() != null && exist.getType().equals(i)) path.setSelectedItem(exist.getCharPath());
        }
      }
    });
    if (exist != null) type.setSelectedItem(exist.getType());
    p.add(type);
    
    l = new JLabel("Attribute:");
    p.add(l);
    JButton btn = new JButton("Bearbeiten");
    btn.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent e)
      {
        new AttributesDialog(me, (me.tmpAttributes != null) ? me.tmpAttributes : new Attributes(), false);
        me.tmpAttributes = me.tmpAttr;
      }
    });
    p.add(btn);
    
    l = new JLabel("Requirements:");
    p.add(l);
    btn = new JButton("Bearbeiten");
    btn.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent e)
      {
        new AttributesDialog(me, (me.tmpRequires != null) ? me.tmpRequires : new Attributes(), false);
        me.tmpRequires = me.tmpAttr;
      }
    });
    p.add(btn);
    
    me.actionSettings = new JPanel(new SpringLayout());
    
    final String[] actions = new String[] { "EmptyAction", "PotionAction", "WeaponAction" }; // TODO: Keep in sync with available item actions. SkillAction is excluded, it's only for native purpose
    l = new JLabel("Action:");
    p.add(l);
    final JComboBox<String> action = new JComboBox<String>(actions);
    action.addItemListener(new ItemListener()
    {
      @Override
      public void itemStateChanged(ItemEvent e)
      {
        if (e.getStateChange() != ItemEvent.SELECTED) return;
        
        JPanel labels = new JPanel(new SpringLayout());
        JPanel panel = new JPanel(new SpringLayout());
        
        switch (e.getItem().toString())
        {
          case "PotionAction":
          {
            labels.add(new JLabel("Target:"));
            me.potionTarget = new JTextField("CASTER");
            me.potionTarget.setColumns(15);
            if (exist != null && exist.getAction() instanceof PotionAction) me.potionTarget.setText(((PotionAction) exist.getAction()).getTarget());
            panel.add(me.potionTarget);
            
            labels.add(new JLabel("Attribute:"));
            JButton btn = new JButton("Bearbeiten");
            if (me.potionAttributes == null && exist != null && exist.getAction() instanceof PotionAction) me.potionAttributes = ((PotionAction) exist.getAction()).getChanges();
            btn.addActionListener(new ActionListener()
            {
              @Override
              public void actionPerformed(ActionEvent e)
              {
                new AttributesDialog(me, (me.potionAttributes != null) ? me.potionAttributes : new Attributes(), false);
                me.potionAttributes = me.tmpAttr;
              }
            });
            panel.add(btn);
            
            labels.add(new JLabel("Schadens-Typ:"));
            me.potionDamageType = new JComboBox<DamageType>(DamageType.values());
            panel.add(me.potionDamageType);
            
            SpringUtilities.makeCompactGrid(labels, 3, 1, 6, 12, 6, 12);
            SpringUtilities.makeCompactGrid(panel, 3, 1, 6, 6, 6, 6);
            break;
          }
          case "WeaponAction":
          {
            labels.add(new JLabel("Attribute:"));
            JButton btn = new JButton("Bearbeiten");
            if (me.weaponAttributes == null && exist != null && exist.getAction() instanceof WeaponAction) me.weaponAttributes = ((WeaponAction) exist.getAction()).getEffect();
            btn.addActionListener(new ActionListener()
            {
              @Override
              public void actionPerformed(ActionEvent e)
              {
                new AttributesDialog(me, (me.weaponAttributes != null) ? me.weaponAttributes : new Attributes(), true);
                me.weaponAttributes = me.tmpAttr;
              }
            });
            panel.add(btn);
            
            labels.add(new JLabel("Schadens-Typ:"));
            me.weaponDamageType = new JComboBox<DamageType>(DamageType.values());
            if (exist != null && exist.getAction() instanceof WeaponAction) me.weaponDamageType.setSelectedItem(((WeaponAction) exist.getAction()).getDamageType());
            panel.add(me.weaponDamageType);
            
            SpringUtilities.makeCompactGrid(labels, 2, 1, 6, 6, 6, 6);
            SpringUtilities.makeCompactGrid(panel, 2, 1, 6, 6, 6, 6);
            break;
          }
        }
        p.remove(22);
        p.remove(me.actionSettings);
        SpringUtilities.makeCompactGrid(p, 11, 2, 6, 6, 6, 6);
        p.add(labels, 22);
        p.add(panel, 23);
        me.actionSettings = panel;
        SpringUtilities.makeCompactGrid(p, 13, 2, 6, 6, 6, 6);
        itemFrame.pack();
      }
    });
    
    p.add(action);
    
    p.add(new JLabel());
    p.add(me.actionSettings);
    
    p.add(new JLabel());
    JButton ok = new JButton("OK");
    ok.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent e)
      {
        ItemAction ia = new EmptyAction();
        
        if (action.getSelectedItem().equals("PotionAction")) ia = new PotionAction(me.potionTarget.getText(), me.potionAttributes, (DamageType) me.potionDamageType.getSelectedItem());
        
        else if (action.getSelectedItem().equals("WeaponAction")) ia = new WeaponAction(me.weaponAttributes, (DamageType) me.weaponDamageType.getSelectedItem());
        
        String charPath = "";
        if (path.getSelectedItem() != null) charPath = path.getSelectedItem().toString().replace("_f.png", "").replace("_b.png", "").replace("_m.png", "").replace(".png", "");
        
        me.tmpItem = new Item((Types) type.getSelectedItem(), name.getText(), (int) ix.getValue(), (int) iy.getValue(), (int) cx.getValue(), (int) cy.getValue(), charPath, me.tmpAttributes, me.tmpRequires, ia, 1);
        
        itemFrame.dispose();
      }
    });
    p.add(ok);
    
    if (exist != null) action.setSelectedItem(exist.getAction().getClass().getSimpleName());
    
    SpringUtilities.makeCompactGrid(p, 13, 2, 6, 6, 6, 6);
    
    itemFrame.setContentPane(p);
    
    itemFrame.pack();
    itemFrame.setLocationRelativeTo(me.w);
    itemFrame.setVisible(true);
  }
}
