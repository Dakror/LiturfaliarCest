package de.dakror.liturfaliar.editor.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SpringLayout;

import layout.SpringUtilities;
import de.dakror.liturfaliar.editor.MapEditor;
import de.dakror.liturfaliar.settings.Attributes;
import de.dakror.liturfaliar.settings.CFG;
import de.dakror.liturfaliar.settings.Attributes.Attr;

public class AttributesDialog
{
  public AttributesDialog(final MapEditor me, Attributes exist, final boolean range)
  {
    final JDialog attrFrame = new JDialog(me.w);
    attrFrame.setTitle("Attributs-Bearbeitung");
    attrFrame.setResizable(false);
    attrFrame.setAlwaysOnTop(true);
    attrFrame.setModal(true);
    attrFrame.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    
    final JSpinner[] spinners = new JSpinner[Attr.values().length * ((range) ? 2 : 1)];
    
    JPanel panel = new JPanel(new SpringLayout());
    
    for (int i = 0; i < Attr.values().length; i++)
    {
      JLabel label = new JLabel(Attr.values()[i].getText() + ":");
      panel.add(label);
      
      JSpinner spinner = new JSpinner(new SpinnerNumberModel(exist.getAttribute(Attr.values()[i]).getValue(), -1000.0, 1000.0, 1.0));
      spinners[i * ((range) ? 2 : 1)] = spinner;
      CFG.p(i * ((range) ? 2 : 1));
      panel.add(spinner);
      
      if (range)
      {
        spinner = new JSpinner(new SpinnerNumberModel(exist.getAttribute(Attr.values()[i]).getMaximum(), -1000.0, 1000.0, 1.0));
        spinners[i * ((range) ? 2 : 1) + 1] = spinner;
        panel.add(spinner);
      }
    }
    
    panel.add(new JLabel());
    panel.add(new JLabel());
    
    final JButton attrOk = new JButton("OK");
    attrOk.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent e)
      {
        me.tmpAttr = new Attributes();
        for (int i = 0; i < Attr.values().length; i++)
        {
          me.tmpAttr.getAttribute(Attr.values()[i]).setValue(Double.valueOf(spinners[i * ((range) ? 2 : 1)].getValue().toString()));
          if (!range) me.tmpAttr.getAttribute(Attr.values()[i]).setMaximum(Double.valueOf(spinners[i * ((range) ? 2 : 1)].getValue().toString()));
          else me.tmpAttr.getAttribute(Attr.values()[i]).setMaximum(Double.valueOf(spinners[i * ((range) ? 2 : 1) + 1].getValue().toString()));
        }
        attrFrame.dispose();
      }
    });
    panel.add(attrOk);
    
    SpringUtilities.makeCompactGrid(panel, Attr.values().length + 1, (range) ? 3 : 2, 6, 6, 6, 6);
    
    attrFrame.setContentPane(panel);
    
    attrFrame.pack();
    attrFrame.setLocationRelativeTo(me.w);
    attrFrame.setVisible(true);
  }
}
