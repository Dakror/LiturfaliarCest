package de.dakror.liturfaliar.editor.dialog;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Arrays;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import layout.SpringUtilities;

import org.json.JSONException;
import org.json.JSONObject;

import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.editor.MapEditor;
import de.dakror.liturfaliar.editor.TileButton;
import de.dakror.liturfaliar.item.Equipment;
import de.dakror.liturfaliar.map.Map;
import de.dakror.liturfaliar.map.data.Door;
import de.dakror.liturfaliar.settings.CFG;
import de.dakror.liturfaliar.util.Assistant;
import de.dakror.liturfaliar.util.FileManager;

public class EditFieldDataDialog
{
  public EditFieldDataDialog(final MapEditor me, final TileButton field, final String dataType)
  {
    try
    {
      // -- general setup -- //
      final JDialog dialog = new JDialog(me.w, true);
      dialog.setTitle("Feld-Data bearbeiten");
      dialog.setIconImage(me.w.getIconImage());
      dialog.setSize(400, 320);
      dialog.setResizable(false);
      dialog.setLocationRelativeTo(me.w);
      dialog.setLayout(new FlowLayout());
      dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      JPanel inputs = new JPanel(new SpringLayout());
      inputs.setPreferredSize(new Dimension(400, 257));
      JButton delete = new JButton("Löschen");
      delete.setEnabled(false);
      delete.setPreferredSize(new Dimension(190, 23));
      JButton save = new JButton("Speichern");
      save.setPreferredSize(new Dimension(190, 23));
      // -- type specific setup -- //
      JSONObject exist = field.getDataByType(dataType);
      if (exist != null)
      {
        delete.setEnabled(true);
        delete.addActionListener(new ActionListener()
        {
          @Override
          public void actionPerformed(ActionEvent e)
          {
            field.removeDataByType(dataType);
            dialog.dispose();
          }
        });
      }
      switch (dataType)
      {
        case "Door":
        {
          JLabel name = new JLabel("Ziel X-Koordinate:");
          name.setPreferredSize(new Dimension(190, 23));
          inputs.add(name);
          final JTextField dx = new JTextField("0");
          dx.setName("int_dx");
          if (exist != null) dx.setText("" + exist.getInt("dx"));
          inputs.add(dx);
          
          inputs.add(new JLabel("Ziel Y-Koordinate:"));
          final JTextField dy = new JTextField("0");
          dy.setName("int_dy");
          if (exist != null) dy.setText("" + exist.getInt("dy"));
          inputs.add(dy);
          
          inputs.add(new JLabel("Zielrichtung:"));
          final String[] dirs = new String[] { "Gleiche", "Unten", "Links", "Rechts", "Oben" };
          final JComboBox<String> dir = new JComboBox<String>(dirs);
          dir.setName("int_dir");
          if (exist != null) dir.setSelectedIndex(exist.getInt("dir") + 1);
          inputs.add(dir);
          
          inputs.add(new JLabel("Leucht-Pfeil:"));
          final String[] arrows = new String[] { "< Leer >", "Unten", "Links", "Rechts", "Oben" };
          final JComboBox<String> arr = new JComboBox<String>(arrows);
          arr.setName("int_arr");
          if (exist != null && exist.has("arr")) arr.setSelectedIndex(exist.getInt("arr") + 1);
          else arr.setSelectedIndex(0);
          inputs.add(arr);
          
          inputs.add(new JLabel("Zielkarte:"));
          final JDialog mapCoordSelect = new JDialog(dialog, "", false);
          mapCoordSelect.setLayout(null);
          mapCoordSelect.setResizable(false);
          mapCoordSelect.setLocation(dialog.getX() + dialog.getWidth() + 10, dialog.getY());
          mapCoordSelect.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
          mapCoordSelect.setVisible(true);
          final JComboBox<String> map = new JComboBox<String>(Map.getMaps(me.mappackdata.getString("name"), CFG.MAPEDITORDIR));
          map.setName("string_map");
          map.addActionListener(new ActionListener()
          {
            @Override
            public void actionPerformed(ActionEvent e)
            {
              new Thread()
              {
                public void run()
                {
                  try
                  {
                    final String s = (String) map.getSelectedItem();
                    mapCoordSelect.setTitle(s);
                    BufferedImage bi = new Map(me.mappackdata.getString("name"), s, CFG.MAPEDITORDIR).getRendered(1);
                    final JLabel l = new JLabel();
                    l.addMouseListener(new MouseAdapter()
                    {
                      @Override
                      public void mousePressed(MouseEvent e)
                      {
                        dx.setText("" + (e.getX() - CFG.HUMANBOUNDS[0] / 2));
                        dy.setText("" + (e.getY() - CFG.HUMANBOUNDS[1] * 2 / 3));
                        try
                        {
                          BufferedImage bi = new Map(me.mappackdata.getString("name"), s, CFG.MAPEDITORDIR).getRendered(1);
                          int d = Arrays.asList(dirs).indexOf(((String) dir.getSelectedItem())) - 1;
                          d = (d < 0) ? 0 : d;
                          Assistant.drawChar(e.getX() - CFG.HUMANBOUNDS[0] / 2, e.getY() - CFG.HUMANBOUNDS[1] * 2 / 3, CFG.HUMANBOUNDS[0], CFG.HUMANBOUNDS[1], d, 0, Equipment.getDefault(true), (Graphics2D) bi.getGraphics(), true);// Assistant.Rect(e.getX() - CFG.HUMANBOUNDS[0] / 2, e.getY() - CFG.HUMANBOUNDS[0], CFG.HUMANBOUNDS[0], CFG.HUMANBOUNDS[1], Color.cyan, null, (Graphics2D) bi.getGraphics());
                          l.setIcon(new ImageIcon(bi));
                        }
                        catch (JSONException e1)
                        {
                          e1.printStackTrace();
                        }
                      }
                    });
                    l.setSize(bi.getWidth(), bi.getHeight());
                    l.setIcon(new ImageIcon(bi));
                    JScrollPane jsp = new JScrollPane(l, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                    jsp.getVerticalScrollBar().setUnitIncrement(CFG.FIELDSIZE / 3);
                    jsp.getHorizontalScrollBar().setUnitIncrement(CFG.FIELDSIZE / 3);
                    jsp.setPreferredSize(new Dimension(500, 500));
                    mapCoordSelect.setContentPane(jsp);
                    mapCoordSelect.pack();
                  }
                  catch (JSONException e1)
                  {
                    e1.printStackTrace();
                  }
                }
              }.start();
            }
          });
          map.setSelectedIndex(0);
          if (exist != null) map.setSelectedItem(exist.getString("map"));
          inputs.add(map);
          
          inputs.add(new JLabel("Sound:"));
          final JComboBox<String> sound = new JComboBox<String>();
          sound.setName("string_sound");
          sound.addItem("< Leer >");
          for (String s : FileManager.getMediaFiles("Sound"))
          {
            sound.addItem(s);
          }
          if (exist != null) sound.setSelectedItem(exist.getString("sound"));
          sound.addActionListener(new ActionListener()
          {
            @Override
            public void actionPerformed(ActionEvent e)
            {
              if (((String) sound.getSelectedItem()).equals("< Leer >")) return;
              Viewport.playSound((String) sound.getSelectedItem());
            }
          });
          inputs.add(sound);
          
          inputs.add(new JLabel("Animation:"));
          final JComboBox<String> img = new JComboBox<String>();
          img.setName("string_img");
          final JLabel preview = new JLabel();
          preview.setPreferredSize(new Dimension(CFG.FIELDSIZE, CFG.FIELDSIZE));
          img.addItem("< Leer >");
          for (int i = 0; i < Door.CHARS.length * 4; i++)
          {
            img.addItem(Door.CHARS[(int) Math.floor(i / 4.0)] + ": " + ((i % 4) + 1));
          }
          img.addActionListener(new ActionListener()
          {
            @Override
            public void actionPerformed(ActionEvent e)
            {
              String s = (String) img.getSelectedItem();
              if (s.equals("< Leer >"))
              {
                preview.setIcon(null);
                return;
              }
              int part = Integer.parseInt(s.substring(s.indexOf(": ") + ": ".length())) - 1;
              BufferedImage i = (BufferedImage) Viewport.loadImage("char/objects/" + s.substring(0, s.indexOf(": ")) + ".png");
              preview.setPreferredSize(new Dimension(i.getWidth() / 4, i.getHeight() / 4));
              preview.setIcon(new ImageIcon(i.getSubimage(i.getWidth() / 4 * part, 0, i.getWidth() / 4, i.getHeight() / 4)));
            }
          });
          if (exist != null && exist.getString("img").length() > 0)
          {
            int index = Arrays.asList(Door.CHARS).indexOf(exist.getString("img")) * 4;
            img.setSelectedIndex(index + exist.getInt("t") + 1);
          }
          inputs.add(img);
          
          inputs.add(new JLabel());
          inputs.add(preview);
          save.addActionListener(new ActionListener()
          {
            @Override
            public void actionPerformed(ActionEvent e)
            {
              JSONObject o = new JSONObject();
              try
              {
                try
                {
                  o.put("dx", Integer.parseInt(dx.getText()));
                  o.put("dy", Integer.parseInt(dy.getText()));
                }
                catch (NumberFormatException e1)
                {
                  JOptionPane.showMessageDialog(me.w, "Koordinaten dürfen nur aus Zahlen bestehen!", "", JOptionPane.ERROR_MESSAGE);
                  return;
                }
                o.put("dir", dir.getSelectedIndex() - 1);
                o.put("arr", arr.getSelectedIndex() - 1);
                o.put("map", (String) map.getSelectedItem());
                o.put("sound", ((String) sound.getSelectedItem()).replace("< Leer >", ""));
                String s = (String) img.getSelectedItem();
                if (!s.equals("< Leer >"))
                {
                  o.put("img", s.substring(0, s.indexOf(": ")));
                  o.put("t", Integer.parseInt(s.substring(s.indexOf(": ") + ": ".length())) - 1);
                  Image i = Viewport.loadImage("char/objects/" + s.substring(0, s.indexOf(": ")) + ".png");
                  int w = i.getWidth(null) / 4;
                  int h = i.getHeight(null) / 4;
                  o.put("x", (CFG.FIELDSIZE / 2 - w / 2));
                  o.put("y", (CFG.FIELDSIZE - h / 2));
                }
                else
                {
                  o.put("img", "");
                  o.put("t", 0);
                  o.put("x", 0);
                  o.put("y", 0);
                }
                field.addData(dataType, o);
                dialog.dispose();
              }
              catch (JSONException e2)
              {
                e2.printStackTrace();
                return;
              }
            }
          });
          SpringUtilities.makeCompactGrid(inputs, 8, 2, 6, 6, 6, 6);
          break;
        }
      }
      dialog.add(inputs);
      dialog.add(delete);
      dialog.add(save);
      dialog.setVisible(true);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
}
