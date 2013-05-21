package de.dakror.liturfaliar.editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.ToolTipManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import layout.SpringUtilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.dakror.liturfaliar.CFG;
import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.map.Map;
import de.dakror.liturfaliar.map.data.Door;
import de.dakror.liturfaliar.map.data.FieldData;
import de.dakror.liturfaliar.util.Assistant;
import de.dakror.liturfaliar.util.Compressor;
import de.dakror.liturfaliar.util.FileManager;
import de.dakror.universion.UniVersion;

public class MapEditor
{
  public JFrame     w;
  JMenuBar          menu;
  JMenu             mpmenu, mmenu, fmenu, omenu;
  Viewport          v;
  JSONObject        mappackdata, mapdata;
  JPanel            tiles, map;
  JScrollPane       msp;
  String            tileset;
  JButton           selectedtile;
  JDialog           bumpPreview;
  
  // -- NPC creation -- //
  JDialog           NPCframe;
  JTextField        NPCx, NPCy, NPCname;
  JCheckBox         NPCrandom;
  JLabel            NPCpreview;
  JSpinner          NPCspeed, NPCrandspeed;
  JComboBox<String> NPCsprite;
  
  // -- modes -- //
  boolean           gridmode;
  boolean           rasterview;
  boolean           deletemode;
  double            cachelayer;
  
  public MapEditor(Viewport viewport)
  {
    ToolTipManager.sharedInstance().setInitialDelay(0);
    this.gridmode = true;
    this.deletemode = false;
    this.rasterview = false;
    this.v = viewport;
    new File(FileManager.dir, "myMaps").mkdir();
    w = new JFrame("Liturfaliar Cest MapEditor (" + UniVersion.prettyVersion() + ")");
    w.setSize(Toolkit.getDefaultToolkit().getScreenSize());
    w.setExtendedState(JFrame.MAXIMIZED_BOTH);
    w.setIconImage(Viewport.loadImage("system/logo.png"));
    w.setLocationRelativeTo(null);
    w.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    w.addWindowListener(new WindowAdapter()
    {
      @Override
      public void windowClosing(WindowEvent e)
      {
        v.unfreeze();
      }
    });
    init();
    w.setVisible(true);
  }
  
  public void init()
  {
    // -- menu -- //
    menu = new JMenuBar();
    
    mpmenu = new JMenu("Kartenpaket");
    JMenuItem mpnew = new JMenuItem(new AbstractAction("Neu...")
    {
      private static final long serialVersionUID = 1L;
      
      @Override
      public void actionPerformed(ActionEvent e)
      {
        showNewMapPackDialog();
      }
    });
    mpnew.setAccelerator(KeyStroke.getKeyStroke("ctrl N"));
    mpmenu.add(mpnew);
    JMenuItem mpopen = new JMenuItem(new AbstractAction("Öffnen...")
    {
      private static final long serialVersionUID = 1L;
      
      @Override
      public void actionPerformed(ActionEvent e)
      {
        showOpenMapPackDialog();
      }
    });
    mpopen.setAccelerator(KeyStroke.getKeyStroke("ctrl O"));
    mpmenu.add(mpopen);
    menu.add(mpmenu);
    
    mmenu = new JMenu("Karte");
    JMenuItem mnew = new JMenuItem(new AbstractAction("Neu...")
    {
      private static final long serialVersionUID = 1L;
      
      @Override
      public void actionPerformed(ActionEvent e)
      {
        showNewMapDialog();
      }
    });
    mnew.setAccelerator(KeyStroke.getKeyStroke("ctrl shift N"));
    mmenu.add(mnew);
    JMenuItem mopen = new JMenuItem(new AbstractAction("Öffnen...")
    {
      private static final long serialVersionUID = 1L;
      
      @Override
      public void actionPerformed(ActionEvent e)
      {
        showOpenMapDialog();
      }
    });
    mopen.setAccelerator(KeyStroke.getKeyStroke("ctrl shift O"));
    mmenu.add(mopen);
    mmenu.setEnabled(false);
    menu.add(mmenu);
    
    fmenu = new JMenu("Datei");
    JMenuItem msave = new JMenuItem(new AbstractAction("Speichern")
    {
      private static final long serialVersionUID = 1L;
      
      @Override
      public void actionPerformed(ActionEvent e)
      {
        saveMap();
      }
    });
    msave.setAccelerator(KeyStroke.getKeyStroke("ctrl S"));
    fmenu.add(msave);
    JMenuItem madj = new JMenuItem(new AbstractAction("Größe ändern...")
    {
      private static final long serialVersionUID = 1L;
      
      @Override
      public void actionPerformed(ActionEvent e)
      {
        if (mapdata == null)
        {
          JOptionPane.showMessageDialog(w, "Bevor Karten bearbeitet werden können, muss ein Kartenpaket ausgewählt werden!", "", JOptionPane.ERROR_MESSAGE);
          return;
        }
        JDialog dialog = new JDialog(w, true);
        dialog.setTitle("Kartengröße ändern");
        dialog.setIconImage(w.getIconImage());
        dialog.setSize(400, 170);
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(null);
        dialog.setLayout(new BorderLayout());
        dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        JPanel inputs = new JPanel();
        inputs.setLayout(new GridLayout(0, 2));
        inputs.add(new JLabel("Breite: "));
        final JTextField width = new JTextField(map.getWidth() + "");
        inputs.add(width);
        inputs.add(new JLabel("Höhe: "));
        final JTextField height = new JTextField(map.getHeight() + "");
        inputs.add(height);
        dialog.add(inputs, BorderLayout.PAGE_START);
        JButton create = new JButton("Speichern");
        final JDialog d = dialog;
        create.addActionListener(new ActionListener()
        {
          @Override
          public void actionPerformed(ActionEvent e)
          {
            try
            {
              map.setPreferredSize(new Dimension(Integer.parseInt(width.getText()), Integer.parseInt(height.getText())));
              msp.setViewportView(map);
            }
            catch (NumberFormatException e1)
            {
              JOptionPane.showMessageDialog(w, "Es dürfen nur Zahlen für Breite und Höhe eingegeben werden!", "", JOptionPane.ERROR_MESSAGE);
              return;
            }
            d.dispose();
          }
        });
        dialog.add(create, BorderLayout.PAGE_END);
        dialog.setVisible(true);
      }
    });
    fmenu.add(madj);
    fmenu.addSeparator();
    JCheckBoxMenuItem fnpc = new JCheckBoxMenuItem(new AbstractAction("NPC-Erstellung")
    {
      private static final long serialVersionUID = 1L;
      
      @Override
      public void actionPerformed(ActionEvent e)
      {
        showNPCCreationDialog(((JCheckBoxMenuItem) e.getSource()).getState());
      }
    });
    fnpc.setState(false);
    fmenu.add(fnpc);
    fmenu.setEnabled(false);
    menu.add(fmenu);
    
    omenu = new JMenu("Optionen");
    JCheckBoxMenuItem ogrid = new JCheckBoxMenuItem(new AbstractAction("Rastermodus")
    {
      private static final long serialVersionUID = 1L;
      
      @Override
      public void actionPerformed(ActionEvent e)
      {
        gridmode = ((JCheckBoxMenuItem) e.getSource()).getState();
      }
    });
    ogrid.setState(true);
    omenu.add(ogrid);
    JCheckBoxMenuItem ogview = new JCheckBoxMenuItem(new AbstractAction("Rasteranzeige")
    {
      private static final long serialVersionUID = 1L;
      
      @Override
      public void actionPerformed(ActionEvent e)
      {
        rasterview = ((JCheckBoxMenuItem) e.getSource()).getState();
        map.repaint();
      }
    });
    ogview.setState(false);
    omenu.add(ogview);
    JCheckBoxMenuItem obump = new JCheckBoxMenuItem(new AbstractAction("Bumpmodus")
    {
      private static final long serialVersionUID = 1L;
      
      @Override
      public void actionPerformed(ActionEvent e)
      {
        if (mapdata == null)
        {
          return;
        }
        
        if (!((JCheckBoxMenuItem) e.getSource()).getState() && bumpPreview != null)
        {
          bumpPreview.dispose();
          return;
        }
        
        bumpPreview = new JDialog(w, "Bumpmap Vorschau");
        bumpPreview.setUndecorated(true);
        bumpPreview.setOpacity(0.4f);
        
        final Area bump = new Map(mapdata).getBumpMap();
        JLabel label = new JLabel()
        {
          private static final long serialVersionUID = 1L;
          
          public void paint(Graphics g)
          {
            Assistant.Rect(0, 0, getWidth(), getHeight(), Color.black, Color.black, (Graphics2D) g);
            g.setColor(Color.white);
            ((Graphics2D) g).fill(bump);
          }
        };
        label.setPreferredSize(new Dimension(bump.getBounds().width, bump.getBounds().height));
        bumpPreview.getContentPane().add(label);
        bumpPreview.pack();
        bumpPreview.setLocationRelativeTo(w);
        bumpPreview.setLocation(w.getWidth() / 8 - 1, w.getJMenuBar().getHeight() + w.getInsets().top - 7);
        bumpPreview.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        bumpPreview.setResizable(false);
        bumpPreview.setVisible(true);
      }
    });
    obump.setState(false);
    omenu.add(obump);
    JCheckBoxMenuItem odel = new JCheckBoxMenuItem(new AbstractAction("Entfernmodus")
    {
      private static final long serialVersionUID = 1L;
      
      @Override
      public void actionPerformed(ActionEvent e)
      {
        deletemode = ((JCheckBoxMenuItem) e.getSource()).getState();
      }
    });
    odel.setState(false);
    omenu.add(odel);
    omenu.setEnabled(false);
    menu.add(omenu);
    
    w.setJMenuBar(menu);
    
    JPanel tilepanel = new JPanel();
    tilepanel.setLayout(null);
    tilepanel.setBounds(10, 10, w.getWidth() / 8, w.getHeight());
    final DefaultListModel<String> dlm = new DefaultListModel<String>();
    for (File f : new File(FileManager.dir, "Tiles").listFiles())
    {
      if (f.isFile() && f.getName().endsWith(".png"))
      {
        dlm.addElement(f.getName().replace(".png", ""));
      }
    }
    JList<String> tilefiles = new JList<String>(dlm);
    tilefiles.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    tilefiles.setIgnoreRepaint(true);
    tilefiles.addListSelectionListener(new ListSelectionListener()
    {
      @Override
      public void valueChanged(ListSelectionEvent e)
      {
        tileset = (String) ((JList<?>) e.getSource()).getSelectedValue();
        tiles.removeAll();
        Image image = Viewport.loadImage("Tiles/" + tileset + ".png");
        int w = image.getWidth(null) / CFG.FIELDSIZE;
        int h = image.getHeight(null) / CFG.FIELDSIZE;
        tiles.setPreferredSize(new Dimension(w * CFG.FIELDSIZE, h * CFG.FIELDSIZE));
        for (int i = 0; i < w; i++)
        {
          for (int j = 0; j < h; j++)
          {
            BufferedImage bi = new BufferedImage(CFG.FIELDSIZE, CFG.FIELDSIZE, BufferedImage.TYPE_INT_ARGB);
            bi.getGraphics().drawImage(image, 0, 0, CFG.FIELDSIZE, CFG.FIELDSIZE, i * CFG.FIELDSIZE, j * CFG.FIELDSIZE, i * CFG.FIELDSIZE + CFG.FIELDSIZE, j * CFG.FIELDSIZE + CFG.FIELDSIZE, null);
            JButton button = new JButton();
            button.setBounds(i * CFG.FIELDSIZE, j * CFG.FIELDSIZE, CFG.FIELDSIZE, CFG.FIELDSIZE);
            button.setBorder(BorderFactory.createEmptyBorder());
            button.setContentAreaFilled(false);
            button.setIcon(new ImageIcon(bi));
            final JButton copy = button;
            button.addActionListener(new ActionListener()
            {
              @Override
              public void actionPerformed(ActionEvent e)
              {
                JButton src = (JButton) e.getSource();
                for (Component c : tiles.getComponents())
                {
                  if (c.getClass() == JButton.class)
                    ((JButton) c).setBorder(BorderFactory.createEmptyBorder());
                }
                src.setBorder(BorderFactory.createLineBorder(Color.blue));
                src.setBorderPainted(true);
                selectedtile = src;
              }
            });
            button.addMouseListener(new MouseListener()
            {
              @Override
              public void mouseEntered(MouseEvent e)
              {
                copy.setBorder(BorderFactory.createLineBorder(Color.black));
              }
              
              @Override
              public void mouseExited(MouseEvent e)
              {
                copy.setBorder(BorderFactory.createEmptyBorder());
              }
              
              @Override
              public void mouseClicked(MouseEvent e)
              {}
              
              @Override
              public void mousePressed(MouseEvent e)
              {}
              
              @Override
              public void mouseReleased(MouseEvent e)
              {}
            });
            tiles.add(button);
          }
        }
        tiles.repaint();
      }
    });
    JScrollPane jsp = new JScrollPane(tilefiles);
    jsp.setBounds(0, 0, w.getWidth() / 8, w.getHeight() / 5);
    tilepanel.add(jsp);
    tiles = new JPanel();
    tiles.setLayout(null);
    tiles.setBounds(0, w.getHeight() / 5, 0, 0);
    JScrollPane tilesScrollPane = new JScrollPane(tiles, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
    tilesScrollPane.setBounds(0, w.getHeight() / 5, w.getWidth() / 8, w.getHeight() / 5 * 3 + 132);
    tilepanel.add(tilesScrollPane);
    
    map = new MapPanel(this);
    map.setLayout(null);
    map.setBackground(Color.black);
    map.setPreferredSize(new Dimension(w.getWidth() / 8 * 7, w.getHeight() / 5 * 4 + 132));
    map.addMouseListener(new MouseListener()
    {
      @Override
      public void mouseEntered(MouseEvent e)
      {
        if (selectedtile != null)
          w.setCursor(Toolkit.getDefaultToolkit().createCustomCursor(getTileImage(), new Point(CFG.FIELDSIZE / 2, CFG.FIELDSIZE / 2), "tile"));
      }
      
      @Override
      public void mouseExited(MouseEvent e)
      {
        if (selectedtile != null)
          w.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
      }
      
      @Override
      public void mousePressed(MouseEvent e)
      {
        if (selectedtile != null && e.getButton() == 1)
        {
          int round = (gridmode) ? CFG.FIELDSIZE : 1;
          addTile(((ImageIcon) selectedtile.getIcon()).getImage(), Assistant.round(e.getX() - CFG.FIELDSIZE / 2, round), Assistant.round(e.getY() - CFG.FIELDSIZE / 2, round), tileset, selectedtile.getX() / CFG.FIELDSIZE, selectedtile.getY() / CFG.FIELDSIZE, -1, new JSONObject());
        }
      }
      
      @Override
      public void mouseClicked(MouseEvent e)
      {}
      
      @Override
      public void mouseReleased(MouseEvent e)
      {}
    });
    msp = new JScrollPane(map, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
    msp.setBounds(w.getWidth() / 8, 0, w.getWidth() / 8 * 7, w.getHeight() / 5 * 4 + 132);
    w.add(msp);
    
    w.add(tilepanel);
    
  }
  
  public void showNewMapPackDialog()
  {
    if (mappackdata != null)
      return;
    JDialog dialog = new JDialog(w, true);
    dialog.setTitle("Kartenpaket erstellen");
    dialog.setSize(400, 170);
    dialog.setResizable(false);
    dialog.setLocationRelativeTo(null);
    dialog.setLayout(new BorderLayout());
    dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    JPanel inputs = new JPanel();
    inputs.setLayout(new GridLayout(0, 2));
    inputs.add(new JLabel("Paketname: "));
    final JTextField name = new JTextField();
    inputs.add(name);
    dialog.add(inputs, BorderLayout.PAGE_START);
    JButton create = new JButton("Erstellen");
    final JDialog d = dialog;
    create.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent e)
      {
        try
        {
          w.setTitle("Liturfaliar Cest MapEditor (" + UniVersion.prettyVersion() + ") - " + name.getText());
          mappackdata = new JSONObject();
          mappackdata.put("name", name.getText());
          mappackdata.put("init", new JSONObject());
          mappackdata.put("version", System.currentTimeMillis());
          mmenu.setEnabled(true);
          saveMapPack();
        }
        catch (JSONException e1)
        {
          e1.printStackTrace();
        }
        d.dispose();
        openMapPack(name.getText());
      }
    });
    dialog.add(create, BorderLayout.PAGE_END);
    dialog.setVisible(true);
  }
  
  public void showOpenMapPackDialog()
  {
    if (mappackdata != null)
    {
      return;
    }
    JDialog dialog = new JDialog(w, true);
    dialog.setTitle("Kartenpaket öffnen");
    dialog.setSize(400, 170);
    dialog.setResizable(false);
    dialog.setLocationRelativeTo(null);
    dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    final DefaultListModel<String> mappacks = new DefaultListModel<String>();
    for (File f : new File(FileManager.dir, "myMaps").listFiles())
    {
      if (f.isDirectory() && Arrays.asList(f.list()).contains("pack.json") && Arrays.asList(f.list()).contains("maps"))
      {
        mappacks.addElement(f.getName());
      }
    }
    JList<String> list = new JList<String>(mappacks);
    list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    final JDialog d2 = dialog;
    list.addListSelectionListener(new ListSelectionListener()
    {
      @Override
      public void valueChanged(ListSelectionEvent e)
      {
        if (e.getValueIsAdjusting())
        {
          return;
        }
        openMapPack((String) ((JList<?>) e.getSource()).getSelectedValue());
        d2.dispose();
      }
    });
    dialog.setContentPane(new JScrollPane(list));
    dialog.setVisible(true);
  }
  
  public void showNewMapDialog()
  {
    if (mappackdata == null)
      return;
    JDialog dialog = new JDialog(w, true);
    dialog.setTitle("Karte erstellen");
    dialog.setSize(400, 170);
    dialog.setResizable(false);
    dialog.setLocationRelativeTo(null);
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
      if (f.isFile() && f.getName().endsWith(".wav"))
        music.addItem(f.getName().replace(".wav", ""));
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
            v.stopMusic();
            break;
          }
          case ItemEvent.SELECTED:
          {
            if (!item.equals("Keine Musik"))
              v.playMusic(item, true, 0.2f);
            break;
          }
        }
      }
    });
    inputs.add(music);
    dialog.add(inputs, BorderLayout.PAGE_START);
    JButton create = new JButton("Erstellen");
    final JDialog d = dialog;
    create.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent e)
      {
        if (name.getText().length() == 0)
          return;
        v.stopMusic();
        try
        {
          w.setTitle("Liturfaliar Cest MapEditor (" + UniVersion.prettyVersion() + ") - " + mappackdata.getString("name") + "/" + name.getText());
          mapdata = new JSONObject();
          map.removeAll();
          selectedtile = null;
          msp.setViewportView(map);
          selectedtile = null;
          mapdata.put("music", (!music.getSelectedItem().equals("Keine Musik")) ? music.getSelectedItem() : "");
          mapdata.put("name", name.getText());
          mapdata.put("tile", new JSONArray());
          fmenu.setEnabled(true);
          saveMap();
        }
        catch (JSONException e1)
        {
          e1.printStackTrace();
        }
        d.dispose();
      }
    });
    dialog.add(create, BorderLayout.PAGE_END);
    dialog.setVisible(true);
  }
  
  public void showOpenMapDialog()
  {
    if (mappackdata == null)
      return;
    JDialog dialog = new JDialog(w, true);
    dialog.setTitle("Karte öffnen");
    dialog.setSize(400, 170);
    dialog.setResizable(false);
    dialog.setLocationRelativeTo(null);
    dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    final DefaultListModel<String> maps = new DefaultListModel<String>();
    try
    {
      for (String s : Map.getMaps(mappackdata.getString("name"), "myMaps"))
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
    final JDialog d2 = dialog;
    list.addListSelectionListener(new ListSelectionListener()
    {
      @Override
      public void valueChanged(ListSelectionEvent e)
      {
        if (e.getValueIsAdjusting())
          return;
        if (openMap((String) ((JList<?>) e.getSource()).getSelectedValue()))
          d2.dispose();
      }
    });
    dialog.setContentPane(new JScrollPane(list));
    dialog.setVisible(true);
  }
  
  public void openMapPack(String pack)
  {
    try
    {
      mappackdata = new JSONObject(Assistant.getFileContent(new File(FileManager.dir, "myMaps/" + pack + "/pack.json")));
      w.setTitle("Liturfaliar Cest MapEditor (" + UniVersion.prettyVersion() + ") - " + mappackdata.getString("name"));
      mmenu.setEnabled(true);
    }
    catch (Exception e1)
    {
      JOptionPane.showMessageDialog(w, "Kartenpaket konnte nicht geöffnet werden. Beachte, dass du den Ordner auswählen musst,\n" + "der sowohl die Datei \"pack.json\" als auch den Ordner \"maps\" enthält.", "Fehler!", JOptionPane.ERROR_MESSAGE);
    }
  }
  
  public void saveMapPack()
  {
    try
    {
      File dir = new File(FileManager.dir, "myMaps/" + mappackdata.getString("name"));
      dir.mkdir();
      new File(dir, "maps").mkdir();
      File pack = new File(dir, "pack.json");
      if (!pack.exists())
        pack.createNewFile();
      Assistant.setFileContent(pack, mappackdata.toString(4));
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  
  public boolean openMap(String m)
  {
    try
    {
      w.setTitle("Liturfaliar Cest MapEditor (" + UniVersion.prettyVersion() + ") - " + mappackdata.getString("name") + "/" + m);
      map.removeAll();
      msp.setViewportView(map);
      selectedtile = null;
      mapdata = new JSONObject(Compressor.decompressFile(new File(FileManager.dir, "myMaps/" + mappackdata.getString("name") + "/maps/" + m + ".map")));
      ArrayList<JSONObject> tiles = Assistant.JSONArrayToArray(mapdata.getJSONArray("tile"));
      Collections.sort(tiles, new Comparator<JSONObject>()
      {
        @Override
        public int compare(JSONObject o1, JSONObject o2)
        {
          try
          {
            double dif = o1.getDouble("l") - o2.getDouble("l");
            if (dif < 0)
              return -1;
            else if (dif > 0)
              return 1;
            else return 0;
          }
          catch (JSONException e)
          {
            e.printStackTrace();
            return 0;
          }
        }
      });
      for (int i = 0; i < tiles.size(); i++)
      {
        JSONObject o = tiles.get(i);
        BufferedImage bi = new BufferedImage(CFG.FIELDSIZE, CFG.FIELDSIZE, BufferedImage.TYPE_INT_ARGB);
        bi.getGraphics().drawImage(Viewport.loadImage("Tiles/" + o.getString("tileset") + ".png"), 0, 0, CFG.FIELDSIZE, CFG.FIELDSIZE, o.getInt("tx") * CFG.FIELDSIZE, o.getInt("ty") * CFG.FIELDSIZE, o.getInt("tx") * CFG.FIELDSIZE + CFG.FIELDSIZE, o.getInt("ty") * CFG.FIELDSIZE + CFG.FIELDSIZE, null);
        addTile(bi, o.getInt("x"), o.getInt("y"), o.getString("tileset"), o.getInt("tx"), o.getInt("ty"), o.getDouble("l"), o.getJSONObject("data"));
      }
      msp.setViewportView(map);
      fmenu.setEnabled(true);
      omenu.setEnabled(true);
      return true;
    }
    catch (Exception e1)
    {
      JOptionPane.showMessageDialog(w, "Karte konnte nicht geöffnet werden!", "Fehler!", JOptionPane.ERROR_MESSAGE);
      return false;
    }
  }
  
  public BufferedImage getTileImage()
  {
    BufferedImage bi = new BufferedImage(CFG.FIELDSIZE, CFG.FIELDSIZE, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = (Graphics2D) bi.getGraphics();
    g.drawImage(((ImageIcon) selectedtile.getIcon()).getImage(), 0, 0, CFG.FIELDSIZE, CFG.FIELDSIZE, null);
    Assistant.Rect(0, 0, CFG.FIELDSIZE - 1, CFG.FIELDSIZE - 1, Color.white, null, g);
    return bi;
  }
  
  public void saveMap()
  {
    try
    {
      File f = new File(FileManager.dir, "myMaps/" + mappackdata.getString("name") + "/maps/" + mapdata.getString("name") + ".map");
      if (!f.exists())
        f.createNewFile();
      JSONArray tiles = new JSONArray();
      for (Component c : map.getComponents())
      {
        tiles.put(((TileButton) c).getSave());
      }
      mapdata.put("npc", new JSONArray());
      mapdata.put("tile", tiles);
      Compressor.compressFile(f, mapdata.toString());
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  
  public void showNPCCreationDialog(boolean show)
  {
    if (!show && NPCframe != null && !NPCframe.isVisible())
    {
      NPCframe.dispose();
      return;
    }
    NPCframe = new JDialog(w);
    NPCframe.setLocationRelativeTo(null);
    NPCframe.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    NPCframe.setAlwaysOnTop(true);
    NPCframe.setResizable(false);
    
    JPanel p = new JPanel(new SpringLayout());
    
    JLabel label = new JLabel("X-Position: ", JLabel.TRAILING);
    p.add(label);
    NPCx = new JTextField(15);
    label.setLabelFor(NPCx);
    p.add(NPCx);
    
    label = new JLabel("Y-Position: ", JLabel.TRAILING);
    p.add(label);
    NPCy = new JTextField(15);
    label.setLabelFor(NPCy);
    p.add(NPCy);
    
    label = new JLabel("Name: ", JLabel.TRAILING);
    p.add(label);
    NPCname = new JTextField(15);
    label.setLabelFor(NPCname);
    p.add(NPCname);
    
    label = new JLabel("Sprite: ", JLabel.TRAILING);
    p.add(label);
    NPCname = new JTextField(15);
    label.setLabelFor(NPCname);
    p.add(NPCname);
     
    SpringUtilities.makeCompactGrid(p, 3, 2, 6, 6, 6, 6);
    
    NPCframe.setContentPane(p);
    
    NPCframe.pack();
    NPCframe.setVisible(true);
    
    
  }
  
  public void addTile(Image icon, final int x, final int y, String t, int tx, int ty, double l, JSONObject data)
  {
    try
    {
      if (l == -1)
      {
        for (int i = 0; i < map.getComponentCount(); i++)
        {
          TileButton b = (TileButton) map.getComponent(i);
          if (b.getBounds().intersects(x, y, CFG.FIELDSIZE, CFG.FIELDSIZE))
          {
            l = b.getLayer();
          }
        }
        l++;
      }
      final TileButton tile = new TileButton(x, y, tx, ty, l, t, icon);
      tile.data = data;
      final JPopupMenu jpm = new JPopupMenu();
      JMenuItem init = new JMenuItem("Als Startfeld des Kartenpakets festlegen");
      init.addActionListener(new ActionListener()
      {
        @Override
        public void actionPerformed(ActionEvent e)
        {
          JSONObject d = new JSONObject();
          try
          {
            d.put("map", mapdata.getString("name"));
            d.put("x", tile.getX());
            d.put("y", tile.getY() - CFG.FIELDSIZE * 3 / 4);
            mappackdata.put("init", d);
            saveMapPack();
          }
          catch (JSONException e1)
          {
            e1.printStackTrace();
          }
        }
      });
      cachelayer = l + 1;
      jpm.add(init);
      JMenuItem lchange = new JMenuItem(new AbstractAction("Layer anpassen")
      {
        private static final long serialVersionUID = 1L;
        
        @Override
        public void actionPerformed(ActionEvent e)
        {
          final JDialog dialog = new JDialog(w, true);
          dialog.setTitle("Layer anpassen");
          dialog.setIconImage(w.getIconImage());
          dialog.setSize(200, 80);
          dialog.setResizable(false);
          dialog.setLocationRelativeTo(null);
          dialog.setLayout(new GridLayout(2, 1, 0, 0));
          dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
          final JTextField layer = new JTextField("" + tile.getLayer());
          layer.setSelectionStart(0);
          layer.setSelectionEnd(layer.getText().length());
          final JButton btn = new JButton("Speichern");
          btn.addActionListener(new ActionListener()
          {
            @Override
            public void actionPerformed(ActionEvent e)
            {
              double ly = 0;
              try
              {
                ly = Double.parseDouble(layer.getText());
              }
              catch (Exception e1)
              {
                JOptionPane.showMessageDialog(w, "Layerangabe darf nur Zahlen enthalten.", "Fehler!", JOptionPane.ERROR_MESSAGE);
                return;
              }
              cachelayer = ly;
              tile.setLayer(ly);
              saveMap();
              dialog.dispose();
              try
              {
                openMap(mapdata.getString("name"));
              }
              catch (JSONException e1)
              {
                e1.printStackTrace();
              }
            }
          });
          layer.addKeyListener(new KeyListener()
          {
            @Override
            public void keyPressed(KeyEvent e)
            {
              if (e.getExtendedKeyCode() == KeyEvent.VK_ENTER)
                btn.doClick();
            }
            
            @Override
            public void keyReleased(KeyEvent arg0)
            {}
            
            @Override
            public void keyTyped(KeyEvent arg0)
            {}
          });
          dialog.add(layer);
          dialog.add(btn);
          dialog.setVisible(true);
        }
      });
      jpm.add(lchange);
      JMenu mdata = new JMenu("Feld-Data bearbeiten");
      for (final String s : FieldData.DATATYPES)
      {
        JMenuItem tmp = new JMenuItem(new AbstractAction(s)
        {
          private static final long serialVersionUID = 1L;
          
          @Override
          public void actionPerformed(ActionEvent e)
          {
            editFieldData(tile, s);
          }
        });
        mdata.add(tmp);
      }
      jpm.add(mdata);
      tile.addMouseListener(new MouseListener()
      {
        @Override
        public void mouseClicked(MouseEvent e)
        {}
        
        @Override
        public void mousePressed(MouseEvent e)
        {
          if (e.getButton() == 1 && selectedtile != null)
          {
            JButton src = (JButton) e.getSource();
            int round = (gridmode) ? CFG.FIELDSIZE : 1;
            addTile(((ImageIcon) selectedtile.getIcon()).getImage(), Assistant.round(e.getX() - CFG.FIELDSIZE / 2 + src.getX(), round), Assistant.round(e.getY() - CFG.FIELDSIZE / 2 + src.getY(), round), tileset, selectedtile.getX() / CFG.FIELDSIZE, selectedtile.getY() / CFG.FIELDSIZE, -1, new JSONObject());
          }
          if (e.getButton() == 3 && deletemode)
          {
            map.remove(tile);
            msp.setViewportView(map);
          }
        }
        
        @Override
        public void mouseReleased(MouseEvent e)
        {
          if (e.getButton() == 3 && !deletemode)
            jpm.show(tile, e.getX(), e.getY());
        }
        
        @Override
        public void mouseEntered(MouseEvent e)
        {
          if (selectedtile != null)
          {
            w.setCursor(Toolkit.getDefaultToolkit().createCustomCursor(getTileImage(), new Point(CFG.FIELDSIZE / 2, CFG.FIELDSIZE / 2), "tile"));
          }
        }
        
        @Override
        public void mouseExited(MouseEvent e)
        {
          if (selectedtile != null)
            w.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }
      });
      if (mapdata == null)
        return;
      map.add(tile);
      msp.setViewportView(map);
      map.setComponentZOrder(tile, 0);
    }
    catch (Exception e1)
    {
      e1.printStackTrace();
      return;
    }
    cachelayer = 0;
  }
  
  public void editFieldData(final TileButton field, final String dataType)
  {
    try
    {
      // -- general setup -- //
      final JDialog dialog = new JDialog(w, true);
      dialog.setTitle("Feld-Data bearbeiten");
      dialog.setIconImage(w.getIconImage());
      dialog.setSize(400, 300);
      dialog.setResizable(false);
      dialog.setLocationRelativeTo(null);
      dialog.setLayout(new FlowLayout());
      dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      JPanel inputs = new JPanel();
      inputs.setLayout(new FlowLayout());
      inputs.setPreferredSize(new Dimension(400, 235));
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
          final JTextField dx = new JTextField("0");
          dx.setName("int_dx");
          if (exist != null)
            dx.setText("" + exist.getInt("dx"));
          dx.setPreferredSize(new Dimension(190, 23));
          inputs.add(name);
          inputs.add(dx);
          name = new JLabel("Ziel Y-Koordinate:");
          name.setPreferredSize(new Dimension(190, 23));
          final JTextField dy = new JTextField("0");
          dy.setName("int_dy");
          if (exist != null)
            dy.setText("" + exist.getInt("dy"));
          dy.setPreferredSize(new Dimension(190, 23));
          inputs.add(name);
          inputs.add(dy);
          final String[] dirs = new String[] { "Gleiche", "Unten", "Links", "Rechts", "Oben" };
          name = new JLabel("Zielrichtung:");
          name.setPreferredSize(new Dimension(190, 23));
          final JComboBox<String> dir = new JComboBox<String>();
          dir.setName("int_dir");
          dir.setPreferredSize(new Dimension(190, 23));
          for (String s : dirs)
          {
            dir.addItem(s);
          }
          if (exist != null)
            dir.setSelectedIndex(exist.getInt("dir") + 1);
          inputs.add(name);
          inputs.add(dir);
          name = new JLabel("Zielkarte:");
          name.setPreferredSize(new Dimension(190, 23));
          final JDialog mapCoordSelect = new JDialog(dialog, "", false);
          mapCoordSelect.setLayout(null);
          mapCoordSelect.setResizable(false);
          mapCoordSelect.setLocation(dialog.getX() + dialog.getWidth() + 10, dialog.getY());
          mapCoordSelect.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
          mapCoordSelect.setVisible(true);
          final JComboBox<String> map = new JComboBox<String>();
          map.setName("string_map");
          map.setPreferredSize(new Dimension(190, 23));
          for (String s : Map.getMaps(mappackdata.getString("name"), "myMaps"))
          {
            map.addItem(s);
          }
          map.addActionListener(new ActionListener()
          {
            @Override
            public void actionPerformed(ActionEvent e)
            {
              try
              {
                final String s = (String) map.getSelectedItem();
                mapCoordSelect.setTitle(s);
                BufferedImage bi = new Map(mappackdata.getString("name"), s, "myMaps").getRendered(1, v);
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
                      BufferedImage bi = new Map(mappackdata.getString("name"), s, "myMaps").getRendered(1, v);
                      int d = Arrays.asList(dirs).indexOf(((String) dir.getSelectedItem())) - 1;
                      d = (d < 0) ? 0 : d;
                      Assistant.drawChar(e.getX() - CFG.HUMANBOUNDS[0] / 2, e.getY() - CFG.HUMANBOUNDS[1] * 2 / 3, CFG.HUMANBOUNDS[0], CFG.HUMANBOUNDS[1], d, 0, new JSONObject(Assistant.getURLContent(getClass().getResource("/json/char.json"))).getJSONObject("default").getJSONObject("Mann"), (Graphics2D) bi.getGraphics(), null, true);// Assistant.Rect(e.getX() - CFG.HUMANBOUNDS[0] / 2, e.getY() - CFG.HUMANBOUNDS[0], CFG.HUMANBOUNDS[0], CFG.HUMANBOUNDS[1], Color.cyan, null, (Graphics2D) bi.getGraphics());
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
                mapCoordSelect.setContentPane(l);
                mapCoordSelect.pack();
              }
              catch (JSONException e1)
              {
                e1.printStackTrace();
              }
            }
          });
          map.setSelectedIndex(0);
          if (exist != null)
            map.setSelectedItem(exist.getString("map"));
          inputs.add(name);
          inputs.add(map);
          name = new JLabel("Sound:");
          name.setPreferredSize(new Dimension(190, 23));
          final JComboBox<String> sound = new JComboBox<String>();
          sound.setName("string_sound");
          sound.setPreferredSize(new Dimension(190, 23));
          sound.addItem("< Leer >");
          for (String s : CFG.SOUND)
          {
            sound.addItem(s);
          }
          if (exist != null)
            sound.setSelectedItem(exist.getString("sound"));
          sound.addActionListener(new ActionListener()
          {
            @Override
            public void actionPerformed(ActionEvent e)
            {
              if (((String) sound.getSelectedItem()).equals("< Leer >"))
                return;
              v.playSound((String) sound.getSelectedItem());
            }
          });
          inputs.add(name);
          inputs.add(sound);
          name = new JLabel("Animation:");
          name.setPreferredSize(new Dimension(190, 23));
          final JComboBox<String> img = new JComboBox<String>();
          img.setName("string_img");
          img.setPreferredSize(new Dimension(190, 23));
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
          inputs.add(name);
          inputs.add(img);
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
                  JOptionPane.showMessageDialog(w, "Koordinaten dürfen nur aus Zahlen bestehen!", "", JOptionPane.ERROR_MESSAGE);
                  return;
                }
                o.put("dir", dir.getSelectedIndex() - 1);
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
