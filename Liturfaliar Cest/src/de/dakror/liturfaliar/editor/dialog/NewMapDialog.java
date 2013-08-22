package de.dakror.liturfaliar.editor.dialog;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.editor.MapEditor;
import de.dakror.liturfaliar.util.FileManager;
import de.dakror.universion.UniVersion;

public class NewMapDialog
{
  public NewMapDialog(final MapEditor me)
  {
    if (me.mappackdata == null) return;
    final JDialog dialog = new JDialog(me.w, true);
    dialog.addWindowListener(new WindowAdapter()
    {
      @Override
      public void windowClosed(WindowEvent e)
      {
        Viewport.stopMusic();
      }
    });
    dialog.setTitle("Karte erstellen");
    dialog.setSize(400, 170);
    dialog.setResizable(false);
    dialog.setLocationRelativeTo(me.w);
    dialog.setLayout(new BorderLayout());
    dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    JPanel inputs = new JPanel();
    inputs.setLayout(new GridLayout(0, 2));
    inputs.add(new JLabel("Kartenname: "));
    final JTextField name = new JTextField();
    inputs.add(name);
    inputs.add(new JLabel("Hintergrundmusik: "));
    final JComboBox<String> music = new JComboBox<String>();
    music.addItem("Keine Musik");
    for (File f : new File(FileManager.dir, "Music").listFiles())
    {
      if (f.isFile() && f.getName().endsWith(".wav")) music.addItem(f.getName().replace(".wav", ""));
    }
    music.addItemListener(new ItemListener()
    {
      @Override
      public void itemStateChanged(ItemEvent e)
      {
        String item = (String) e.getItem();
        switch (e.getStateChange())
        {
          case ItemEvent.DESELECTED:
          {
            Viewport.stopMusic();
            break;
          }
          case ItemEvent.SELECTED:
          {
            if (!item.equals("Keine Musik")) Viewport.playMusic(item, true, 0.2f);
            break;
          }
        }
      }
    });
    inputs.add(music);
    inputs.add(new JLabel("Friedlich: "));
    final JCheckBox peaceful = new JCheckBox();
    inputs.add(peaceful);
    
    dialog.add(inputs, BorderLayout.PAGE_START);
    JButton create = new JButton("Erstellen");
    create.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent e)
      {
        if (name.getText().length() == 0) return;
        Viewport.stopMusic();
        try
        {
          me.w.setTitle("Liturfaliar Cest MapEditor (" + UniVersion.prettyVersion() + ") - " + me.mappackdata.getString("name") + "/" + name.getText());
          me.mapdata = new JSONObject();
          me.map.removeAll();
          me.selectedtile = null;
          me.msp.setViewportView(me.map);
          me.selectedtile = null;
          me.mapdata.put("music", (!music.getSelectedItem().equals("Keine Musik")) ? music.getSelectedItem() : "");
          me.mapdata.put("name", name.getText());
          me.mapdata.put("tile", new JSONArray());
          me.mapdata.put("peaceful", peaceful.isSelected());
          me.fmenu.setEnabled(true);
          me.omenu.setEnabled(true);
          me.saveMap();
        }
        catch (JSONException e1)
        {
          e1.printStackTrace();
        }
        dialog.dispose();
      }
    });
    dialog.add(create, BorderLayout.PAGE_END);
    dialog.setVisible(true);
  }
}
