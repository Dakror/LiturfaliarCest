package de.dakror.liturfaliar.editor.dialog;

import java.io.File;

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

public class ImportObjectDialog
{ 
  public ImportObjectDialog(final MapEditor me) {
    final JDialog dialog = new JDialog(me.w, true);
    dialog.setTitle("Objekt importieren");
    dialog.setSize(400, 170);
    dialog.setResizable(false);
    dialog.setLocationRelativeTo(me.w);
    dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    final DefaultListModel<String> maps = new DefaultListModel<String>();
    for (String s : new File(FileManager.dir, CFG.MAPEDITOROBJECTSDIR).list())
    {
      maps.addElement(s.replace(".object", ""));
    }
    JList<String> list = new JList<String>(maps);
    list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    list.addListSelectionListener(new ListSelectionListener()
    {
      @Override
      public void valueChanged(final ListSelectionEvent e)
      {
        if (e.getValueIsAdjusting()) return;
        dialog.dispose();
        me.importObject((String) ((JList<?>) e.getSource()).getSelectedValue());
      }
    });
    dialog.setContentPane(new JScrollPane(list));
    dialog.setVisible(true);
  }
}
