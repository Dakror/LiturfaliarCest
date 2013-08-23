package de.dakror.liturfaliar.editor.dialog;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.json.JSONException;
import org.json.JSONObject;

import de.dakror.liturfaliar.editor.MapEditor;
import de.dakror.universion.UniVersion;

public class NewMapPackDialog
{
  public NewMapPackDialog(final MapEditor me)
  {
    final JDialog dialog = new JDialog(me.w, true);
    dialog.setTitle("Kartenpaket erstellen");
    dialog.setSize(400, 170);
    dialog.setResizable(false);
    dialog.setLocationRelativeTo(me.w);
    dialog.setLayout(new BorderLayout());
    dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    JPanel inputs = new JPanel();
    inputs.setLayout(new GridLayout(0, 2));
    inputs.add(new JLabel("Paketname: "));
    final JTextField name = new JTextField();
    inputs.add(name);
    dialog.add(inputs, BorderLayout.PAGE_START);
    JButton create = new JButton("Erstellen");
    create.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent e)
      {
        try
        {
          me.w.setTitle("Liturfaliar Cest MapEditor (" + UniVersion.prettyVersion() + ") - " + name.getText());
          me.mappackdata = new JSONObject();
          me.mappackdata.put("name", name.getText());
          me.mappackdata.put("init", new JSONObject());
          me.mappackdata.put("version", System.currentTimeMillis());
          me.mmenu.setEnabled(true);
          me.saveMapPack();
        }
        catch (JSONException e1)
        {
          e1.printStackTrace();
        }
        dialog.dispose();
        me.openMapPack(name.getText());
      }
    });
    dialog.add(create, BorderLayout.PAGE_END);
    dialog.setVisible(true);
  }
}
