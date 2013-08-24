package de.dakror.liturfaliar.editor.dialog;

import java.io.File;
import java.util.Arrays;

import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import de.dakror.liturfaliar.editor.MapEditor;
import de.dakror.liturfaliar.settings.CFG;
import de.dakror.liturfaliar.util.FileManager;

public class OpenMapPackDialog
{
  public OpenMapPackDialog(final MapEditor me)
  {
    final JDialog dialog = new JDialog(me.w, true);
    dialog.setTitle("Kartenpaket öffnen");
    dialog.setSize(400, 170);
    dialog.setResizable(false);
    dialog.setLocationRelativeTo(me.w);
    dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    final DefaultListModel<String> mappacks = new DefaultListModel<String>();
    for (File f : new File(FileManager.dir, CFG.MAPEDITORDIR).listFiles())
    {
      if (f.isDirectory() && Arrays.asList(f.list()).contains(".pack"))
      {
        mappacks.addElement(f.getName());
      }
    }
    JList<String> list = new JList<String>(mappacks);
    list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    list.addListSelectionListener(new ListSelectionListener()
    {
      @Override
      public void valueChanged(ListSelectionEvent e)
      {
        if (e.getValueIsAdjusting())
        {
          return;
        }
        me.openMapPack((String) ((JList<?>) e.getSource()).getSelectedValue());
        dialog.dispose();
      }
    });
    dialog.setContentPane(new JScrollPane(list));
    dialog.setVisible(true);
  }
}
