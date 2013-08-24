package de.dakror.liturfaliar.editor.dialog;

import java.awt.Dimension;

import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JList;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.json.JSONException;

import de.dakror.liturfaliar.editor.MapEditor;
import de.dakror.liturfaliar.map.Map;
import de.dakror.liturfaliar.settings.CFG;

public class OpenMapDialog
{
  public OpenMapDialog(final MapEditor me)
  {
    if (me.mappackdata == null) return;
    final JDialog dialog = new JDialog(me.w, true);
    dialog.setTitle("Karte öffnen");
    dialog.setSize(400, 170);
    dialog.setResizable(false);
    dialog.setLocationRelativeTo(me.w);
    dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    final DefaultListModel<String> maps = new DefaultListModel<String>();
    try
    {
      for (String s : Map.getMaps(me.mappackdata.getString("name"), CFG.MAPEDITORDIR))
      {
        maps.addElement(s);
      }
    }
    catch (JSONException e1)
    {
      e1.printStackTrace();
    }
    JList<String> list = new JList<String>(maps);
    list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    list.addListSelectionListener(new ListSelectionListener()
    {
      @Override
      public void valueChanged(final ListSelectionEvent e)
      {
        if (e.getValueIsAdjusting()) return;
        
        JLayeredPane pane = new JLayeredPane();
        
        pane.setPreferredSize(new Dimension(400, 22));
        me.progress = new JProgressBar(0, 100);
        me.progress.setBounds(0, 0, 399, 22);
        pane.add(me.progress, JLayeredPane.DEFAULT_LAYER);
        me.progressLabel = new JLabel();
        me.progressLabel.setHorizontalAlignment(SwingConstants.CENTER);
        me.progressLabel.setVerticalTextPosition(SwingConstants.CENTER);
        me.progressLabel.setBounds(0, 0, 400, 22);
        pane.add(me.progressLabel, JLayeredPane.MODAL_LAYER);
        dialog.setContentPane(pane);
        dialog.pack();
        dialog.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        
        new Thread()
        {
          public void run()
          {
            if (me.openMap((String) ((JList<?>) e.getSource()).getSelectedValue())) dialog.dispose();
          }
        }.start();
      }
    });
    dialog.setContentPane(new JScrollPane(list));
    dialog.setVisible(true);
  }
}
