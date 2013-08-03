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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
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
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerListModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SpringLayout;
import javax.swing.ToolTipManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import layout.SpringUtilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.item.Categories;
import de.dakror.liturfaliar.item.Equipment;
import de.dakror.liturfaliar.item.IconSelecter;
import de.dakror.liturfaliar.item.Item;
import de.dakror.liturfaliar.item.Types;
import de.dakror.liturfaliar.item.action.EmptyAction;
import de.dakror.liturfaliar.item.action.ItemAction;
import de.dakror.liturfaliar.item.action.PotionAction;
import de.dakror.liturfaliar.item.action.WeaponAction;
import de.dakror.liturfaliar.map.Map;
import de.dakror.liturfaliar.map.creature.NPC;
import de.dakror.liturfaliar.map.data.Door;
import de.dakror.liturfaliar.map.data.FieldData;
import de.dakror.liturfaliar.settings.Attributes;
import de.dakror.liturfaliar.settings.Attributes.Attr;
import de.dakror.liturfaliar.settings.CFG;
import de.dakror.liturfaliar.settings.DamageType;
import de.dakror.liturfaliar.util.Assistant;
import de.dakror.liturfaliar.util.Compressor;
import de.dakror.liturfaliar.util.FileManager;
import de.dakror.liturfaliar.util.Vector;
import de.dakror.universion.UniVersion;

public class MapEditor
{
  
  // -- filterReplace dialog -- //
  JComboBox<String>     FRoldTileset, FRnewTileset;
  JTextField            FRoldLayer, FRnewLayer, FRoldTX, FRoldTY, FRnewTX, FRnewTY;
  
  // -- NPC dialog -- //
  JComboBox<String>     NPCsprite, NPCdir, NPCai;
  JDialog               NPCframe;
  JTextField            NPCx, NPCy, NPCname;
  JCheckBox             NPCmove, NPClook, NPChostile;
  JLabel                NPCpreview;
  JSpinner              NPCspeed, NPCmoveT, NPClookT;
  JButton               NPCok;
  Attributes            NPCattr;
  
  int                   NPClastID           = 0;
  
  // -- equip dialog -- //
  JSpinner              EQhair, EQskin, EQeyes;
  JLabel                EQpreview;
  Equipment             EQ;
  
  // -- talk dialog -- //
  JColorSlider          talkColorSlider;
  JScrollPane           talkScrollPane;
  JPanel                talkPanel;
  JButton               talkAdd, talkOk;
  String[]              tilesets;
  final int             talkComponentWidth  = 585;
  final int             talkComponentHeight = 100;
  
  // -- item dialog -- //
  Item                  tmpItem;
  Attributes            tmpAttributes;
  Attributes            tmpRequires;
  
  // -- item dialog actions -- //
  JPanel                actionSettings;
  
  JTextField            potionTarget;
  Attributes            potionAttributes;
  JComboBox<DamageType> potionDamageType;
  
  Attributes            weaponAttributes;
  JComboBox<DamageType> weaponDamageType;
  
  // -- attr dialog -- //
  Attributes            tmpAttr;
  
  // -- global stuff -- //
  public JFrame         w;
  
  JMenuBar              menu;
  JMenu                 mpmenu, mmenu, fmenu, omenu;
  JMenuItem             mUndo;
  JCheckBoxMenuItem     mDrag;
  Viewport              v;
  JSONObject            mappackdata, mapdata;
  JPanel                tiles;
  MapPanel              map;
  JScrollPane           msp;
  String                tileset;
  JButton               selectedtile;
  BufferedImage[][]     autotiles;
  JDialog               bumpPreview;
  
  // -- modes -- //
  boolean               gridmode;
  boolean               rasterview;
  boolean               deletemode;
  boolean               autotilemode;
  boolean               dragmode;
  
  double                cachelayer;
  
  // -- UNDO -- //
  TileButton[]          lastChangedTiles;
  boolean               tilesWereDeleted;
  
  public MapEditor(Viewport viewport)
  {
    ToolTipManager.sharedInstance().setInitialDelay(0);
    this.gridmode = true;
    this.deletemode = false;
    this.rasterview = false;
    this.v = viewport;
    new File(FileManager.dir, CFG.MAPEDITORDIR).mkdir();
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
        v.w.setVisible(true);
        v.mapeditor = null;
      }
    });
    init();
    viewport.w.setVisible(false);
    
    w.setVisible(true);
    
    w.toFront();
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
    
    mUndo = new JMenuItem(new AbstractAction("Rückgängig machen")
    {
      private static final long serialVersionUID = 1L;
      
      @Override
      public void actionPerformed(ActionEvent e)
      {
        undo();
      }
    });
    mUndo.setAccelerator(KeyStroke.getKeyStroke("ctrl Z"));
    mUndo.setEnabled(false);
    fmenu.add(mUndo);
    
    fmenu.addSeparator();
    
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
        dialog.setSize(200, 100);
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
    
    JMenuItem fnpc = new JMenuItem(new AbstractAction("NPC-Bearbeitung")
    {
      private static final long serialVersionUID = 1L;
      
      @Override
      public void actionPerformed(ActionEvent e)
      {
        showNPCDialog(null);
      }
    });
    fmenu.add(fnpc);
    
    JMenuItem fis = new JMenuItem(new AbstractAction("Icon Selecter GUI")
    {
      private static final long serialVersionUID = 1L;
      
      @Override
      public void actionPerformed(ActionEvent e)
      {
        IconSelecter is = new IconSelecter();
        is.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      }
    });
    fmenu.add(fis);
    
    JMenuItem ffr = new JMenuItem(new AbstractAction("Felder per Filter ersetzen")
    {
      private static final long serialVersionUID = 1L;
      
      @Override
      public void actionPerformed(ActionEvent e)
      {
        showFilterReplaceDialog();
      }
    });
    fmenu.add(ffr);
    
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
    ogrid.setAccelerator(KeyStroke.getKeyStroke("F5"));
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
    
    JCheckBoxMenuItem obump = new JCheckBoxMenuItem(new AbstractAction("Bump-Modus")
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
        
        bumpPreview = new JDialog(w);
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
        bumpPreview.setResizable(false);
        bumpPreview.setVisible(true);
      }
    });
    obump.setAccelerator(KeyStroke.getKeyStroke("F6"));
    obump.setState(false);
    omenu.add(obump);
    
    JCheckBoxMenuItem oauto = new JCheckBoxMenuItem(new AbstractAction("Autotile-Modus")
    {
      private static final long serialVersionUID = 1L;
      
      @Override
      public void actionPerformed(ActionEvent e)
      {
        autotilemode = ((JCheckBoxMenuItem) e.getSource()).getState();
      }
    });
    oauto.setAccelerator(KeyStroke.getKeyStroke("F7"));
    oauto.setState(false);
    omenu.add(oauto);
    
    mDrag = new JCheckBoxMenuItem(new AbstractAction("Drag-Modus")
    {
      private static final long serialVersionUID = 1L;
      
      @Override
      public void actionPerformed(ActionEvent e)
      {
        dragmode = ((JCheckBoxMenuItem) e.getSource()).getState();
      }
    });
    mDrag.setAccelerator(KeyStroke.getKeyStroke("F8"));
    mDrag.setState(false);
    omenu.add(mDrag);
    
    JCheckBoxMenuItem odel = new JCheckBoxMenuItem(new AbstractAction("Entfernmodus")
    {
      private static final long serialVersionUID = 1L;
      
      @Override
      public void actionPerformed(ActionEvent e)
      {
        deletemode = ((JCheckBoxMenuItem) e.getSource()).getState();
      }
    });
    odel.setAccelerator(KeyStroke.getKeyStroke("F9"));
    odel.setState(false);
    omenu.add(odel);
    
    omenu.setEnabled(false);
    menu.add(omenu);
    
    w.setJMenuBar(menu);
    
    JPanel tilepanel = new JPanel();
    tilepanel.setLayout(null);
    tilepanel.setBounds(0, 0, w.getWidth() / 8, w.getHeight());
    final DefaultListModel<String> dlm = new DefaultListModel<String>();
    
    
    ArrayList<String> t = new ArrayList<String>();
    
    for (File f : new File(FileManager.dir, "Tiles").listFiles())
    {
      if (f.isFile() && f.getName().endsWith(".png"))
      {
        dlm.addElement(f.getName().replace(".png", ""));
        t.add(f.getName().replace(".png", ""));
      }
    }
    
    tilesets = t.toArray(new String[] {});
    
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
        
        if (Arrays.asList(CFG.AUTOTILES).contains(tileset))
          autotiles = new BufferedImage[w][h];
        
        
        for (int i = 0; i < w; i++)
        {
          for (int j = 0; j < h; j++)
          {
            BufferedImage bi = new BufferedImage(CFG.FIELDSIZE, CFG.FIELDSIZE, BufferedImage.TYPE_INT_ARGB);
            bi.getGraphics().drawImage(image, 0, 0, CFG.FIELDSIZE, CFG.FIELDSIZE, i * CFG.FIELDSIZE, j * CFG.FIELDSIZE, i * CFG.FIELDSIZE + CFG.FIELDSIZE, j * CFG.FIELDSIZE + CFG.FIELDSIZE, null);
            
            if (Arrays.asList(CFG.AUTOTILES).contains(tileset))
              autotiles[i][j] = bi;
            
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
            button.addMouseListener(new MouseAdapter()
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
    map.setOpaque(true);
    map.setBackground(Color.black);
    map.setPreferredSize(new Dimension(w.getWidth() / 8 * 7, w.getHeight() / 5 * 4 + 132));
    map.addMouseListener(new MouseAdapter()
    {
      @Override
      public void mouseEntered(MouseEvent e)
      {
        showCustomCursor(true);
      }
      
      @Override
      public void mouseExited(MouseEvent e)
      {
        showCustomCursor(false);
      }
      
      @Override
      public void mousePressed(MouseEvent e)
      {
        if (e.getButton() == 1 && !dragmode)
        {
          if (selectedtile != null && map.mouseDown == null && map.mousePos == null)
          {
            int round = (gridmode) ? CFG.FIELDSIZE : 1;
            addTile(((ImageIcon) selectedtile.getIcon()).getImage(), Assistant.round(e.getX() - CFG.FIELDSIZE / 2, round), Assistant.round(e.getY() - CFG.FIELDSIZE / 2, round), tileset, selectedtile.getX() / CFG.FIELDSIZE, selectedtile.getY() / CFG.FIELDSIZE, -1, new JSONObject(), true);
          }
          else if (NPCframe != null)
          {
            updateNPCCoords(e.getX(), e.getY());
          }
        }
      }
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
    for (File f : new File(FileManager.dir, CFG.MAPEDITORDIR).listFiles())
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
    dialog.addWindowListener(new WindowAdapter()
    {
      @Override
      public void windowClosed(WindowEvent e)
      {
        v.stopMusic();
      }
    });
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
    inputs.add(new JLabel("Friedlich: "));
    final JCheckBox peaceful = new JCheckBox();
    inputs.add(peaceful);
    
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
          mapdata.put("peaceful", peaceful.isSelected());
          fmenu.setEnabled(true);
          omenu.setEnabled(true);
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
      for (String s : Map.getMaps(mappackdata.getString("name"), CFG.MAPEDITORDIR))
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
      public void valueChanged(final ListSelectionEvent e)
      {
        if (e.getValueIsAdjusting())
          return;
        new Thread()
        {
          public void run()
          {
            if (openMap((String) ((JList<?>) e.getSource()).getSelectedValue()))
              d2.dispose();
          }
        }.start();
      }
    });
    dialog.setContentPane(new JScrollPane(list));
    dialog.setVisible(true);
  }
  
  public void openMapPack(String pack)
  {
    try
    {
      mappackdata = new JSONObject(Assistant.getFileContent(new File(FileManager.dir, CFG.MAPEDITORDIR + "/" + pack + "/pack.json")));
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
      File dir = new File(FileManager.dir, CFG.MAPEDITORDIR + "/" + mappackdata.getString("name"));
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
      map.mouseDown = null;
      map.mousePos = null;
      map.repaint();
      
      NPClastID = 0;
      
      w.setTitle("Liturfaliar Cest MapEditor (" + UniVersion.prettyVersion() + ") - " + mappackdata.getString("name") + "/" + m);
      map.removeAll();
      msp.setViewportView(map);
      selectedtile = null;
      mapdata = Compressor.openMap(new File(FileManager.dir, CFG.MAPEDITORDIR + "/" + mappackdata.getString("name") + "/maps/" + m + ".map"));
      ArrayList<JSONObject> tiles = Assistant.JSONArrayToArray(mapdata.getJSONArray("tile"));
      Collections.sort(tiles, new Comparator<JSONObject>()
      {
        @Override
        public int compare(JSONObject o1, JSONObject o2)
        {
          try
          {
            return (int) (new Vector(o1.getInt("x"), o1.getInt("y")).length - new Vector(o2.getInt("x"), o2.getInt("y")).length);
          }
          catch (JSONException e)
          {
            e.printStackTrace();
            return 0;
          }
        }
      });
      
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
        addTile(bi, o.getInt("x"), o.getInt("y"), o.getString("tileset"), o.getInt("tx"), o.getInt("ty"), o.getDouble("l"), o.getJSONObject("data"), false);
      }
      JSONArray npcs = mapdata.getJSONArray("npc");
      for (int i = 0; i < npcs.length(); i++)
      {
        addNPC(npcs.getJSONObject(i));
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
      File f = new File(FileManager.dir, CFG.MAPEDITORDIR + "/" + mappackdata.getString("name") + "/maps/" + mapdata.getString("name") + ".map");
      
      JSONArray tiles = new JSONArray();
      JSONArray npcs = new JSONArray();
      
      for (Component c : map.getComponents())
      {
        if (c instanceof TileButton)
        {
          tiles.put(((TileButton) c).getSave());
        }
        else if (c instanceof NPCButton)
        {
          npcs.put(((NPCButton) c).getSave());
        }
      }
      
      mapdata.put("npc", npcs);
      mapdata.put("tile", tiles);
      Compressor.saveMap(f, mapdata);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  
  public void showNPCDialog(final NPCButton exist)
  {
    if (NPCframe == null)
    {
      NPCframe = new JDialog(w);
      NPCframe.setTitle("NPC-Bearbeitung" + ((exist != null) ? " - NPC #" + exist.ID : ""));
      NPCframe.addWindowListener(new WindowAdapter()
      {
        @Override
        public void windowClosed(WindowEvent e)
        {
          NPCframe = null;
          w.setCursor(Cursor.getDefaultCursor());
        }
      });
      NPCframe.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      NPCframe.setAlwaysOnTop(true);
      NPCframe.setResizable(false);
    }
    
    if (exist != null)
      NPCattr = exist.attributes;
    else NPCattr = new Attributes();
    
    JPanel p = new JPanel(new SpringLayout());
    
    JLabel label = new JLabel("X-Position: ", JLabel.TRAILING);
    p.add(label);
    NPCx = new JTextField(15);
    if (exist != null)
      NPCx.setText(exist.x + "");
    
    p.add(NPCx);
    
    label = new JLabel("Y-Position: ", JLabel.TRAILING);
    p.add(label);
    NPCy = new JTextField(15);
    if (exist != null)
      NPCy.setText(exist.y + "");
    
    p.add(NPCy);
    
    label = new JLabel("Blickrichtung: ", JLabel.TRAILING);
    p.add(label);
    NPCdir = new JComboBox<String>(new String[] { "Unten", "Links", "Rechts", "Oben" });
    if (exist != null)
      NPCdir.setSelectedIndex(exist.dir);
    
    NPCdir.addItemListener(new ItemListener()
    {
      
      @Override
      public void itemStateChanged(ItemEvent e)
      {
        if (e.getStateChange() == ItemEvent.SELECTED)
          updateNPCDialogPreview();
      }
    });
    p.add(NPCdir);
    
    label = new JLabel("Name: ", JLabel.TRAILING);
    p.add(label);
    NPCname = new JTextField(15);
    if (exist != null)
      NPCname.setText(exist.name);
    
    p.add(NPCname);
    
    label = new JLabel("Sprite: ", JLabel.TRAILING);
    p.add(label);
    NPCsprite = new JComboBox<String>(NPC.CHARS);
    if (exist != null)
      NPCsprite.setSelectedItem(exist.sprite);
    
    else NPCsprite.setSelectedIndex(0);
    
    NPCsprite.addItemListener(new ItemListener()
    {
      @Override
      public void itemStateChanged(ItemEvent e)
      {
        if (e.getStateChange() == ItemEvent.SELECTED)
          updateNPCDialogPreview();
      }
    });
    
    p.add(NPCsprite);
    
    label = new JLabel("Vorschau: ", JLabel.TRAILING);
    p.add(label);
    NPCpreview = new JLabel();
    NPCpreview.setPreferredSize(new Dimension(32, 48));
    updateNPCDialogPreview();
    p.add(NPCpreview);
    
    label = new JLabel("Bewegungsgeschwindigkeit: ", JLabel.TRAILING);
    p.add(label);
    NPCspeed = new JSpinner(new SpinnerNumberModel(1.0, 0, 20, 0.1));
    if (exist != null)
      NPCspeed.setValue(exist.speed);
    
    p.add(NPCspeed);
    
    label = new JLabel("zufällige Bewegung:", JLabel.TRAILING);
    p.add(label);
    NPCmove = new JCheckBox();
    if (exist != null)
      NPCmove.setSelected(exist.move);
    
    NPCmove.addChangeListener(new ChangeListener()
    {
      @Override
      public void stateChanged(ChangeEvent e)
      {
        NPCmoveT.setEnabled(((JCheckBox) e.getSource()).isSelected());
      }
    });
    p.add(NPCmove);
    
    label = new JLabel("Zufallsbewegung-Interval. (ms):", JLabel.TRAILING);
    p.add(label);
    NPCmoveT = new JSpinner(new SpinnerNumberModel(3000, 0, 1000000000, 100));
    if (exist != null)
      NPCmoveT.setValue(exist.moveT);
    
    NPCmoveT.setEnabled(NPCmove.isSelected());
    p.add(NPCmoveT);
    
    label = new JLabel("zufälliges Blicken:", JLabel.TRAILING);
    p.add(label);
    NPClook = new JCheckBox();
    if (exist != null)
      NPClook.setSelected(exist.look);
    
    NPClook.addChangeListener(new ChangeListener()
    {
      
      @Override
      public void stateChanged(ChangeEvent e)
      {
        NPClookT.setEnabled(((JCheckBox) e.getSource()).isSelected());
      }
    });
    p.add(NPClook);
    
    label = new JLabel("Zufallsblicken-Interval. (ms):", JLabel.TRAILING);
    p.add(label);
    NPClookT = new JSpinner(new SpinnerNumberModel(3000, 0, 1000000000, 100));
    if (exist != null)
      NPClookT.setValue(exist.lookT);
    
    NPClookT.setEnabled(NPClook.isSelected());
    p.add(NPClookT);
    
    label = new JLabel("Künstliche Intelligenz:", JLabel.TRAILING);
    p.add(label);
    NPCai = new JComboBox<String>(new String[] { "MeleeAI" }); // TODO: Keep in sync
    if (exist != null)
      NPCai.setSelectedItem(exist.ai);
    p.add(NPCai);
    
    label = new JLabel("immer feindlich:", JLabel.TRAILING);
    p.add(label);
    NPChostile = new JCheckBox();
    if (exist != null)
      NPChostile.setSelected(exist.hostile);
    p.add(NPChostile);
    
    label = new JLabel("Attribute:", JLabel.TRAILING);
    p.add(label);
    JButton attr = new JButton("Bearbeiten");
    attr.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent e)
      {
        showAttributesDialog(NPCattr, false);
        NPCattr = tmpAttr;
      }
    });
    p.add(attr);
    
    p.add(new JLabel());
    NPCok = new JButton("Platzieren");
    NPCok.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent e)
      {
        JSONArray talk = null;
        Equipment equipment = null;
        if (exist != null)
        {
          talk = exist.talk;
          equipment = exist.getEquipment();
          
          if (NPClastID == exist.ID + 1)
            NPClastID--;
          
          map.remove(exist);
        }
        NPCButton b = addNPC(null);
        if (talk != null)
          b.talk = talk;
        
        if (equipment != null)
          b.setEquipment(equipment);
        
        showNPCDialog(b);
      }
    });
    p.add(NPCok);
    
    SpringUtilities.makeCompactGrid(p, 15, 2, 6, 6, 6, 6);
    
    NPCframe.setContentPane(p);
    NPCframe.pack();
    NPCframe.setVisible(true);
    NPCframe.setLocationRelativeTo(null);
  }
  
  public void showAttributesDialog(Attributes exist, final boolean range)
  {
    final JDialog attrFrame = new JDialog(w);
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
        tmpAttr = new Attributes();
        for (int i = 0; i < Attr.values().length; i++)
        {
          tmpAttr.getAttribute(Attr.values()[i]).setValue(Double.valueOf(spinners[i * ((range) ? 2 : 1)].getValue().toString()));
          if (!range)
            tmpAttr.getAttribute(Attr.values()[i]).setMaximum(Double.valueOf(spinners[i * ((range) ? 2 : 1)].getValue().toString()));
          else tmpAttr.getAttribute(Attr.values()[i]).setMaximum(Double.valueOf(spinners[i * ((range) ? 2 : 1) + 1].getValue().toString()));
        }
        attrFrame.dispose();
      }
    });
    panel.add(attrOk);
    
    SpringUtilities.makeCompactGrid(panel, Attr.values().length + 1, (range) ? 3 : 2, 6, 6, 6, 6);
    
    attrFrame.setContentPane(panel);
    
    attrFrame.pack();
    attrFrame.setLocationRelativeTo(null);
    attrFrame.setVisible(true);
  }
  
  public void showEquipmentDialog(final NPCButton npc)
  {
    if (npc != null)
      EQ = npc.getEquipment();
    
    final JDialog adjFrame = new JDialog(w);
    
    final JDialog viewFrame = new JDialog(w);
    
    adjFrame.setTitle("Ausrüstungs-Bearbeitung");
    adjFrame.setResizable(false);
    adjFrame.addWindowListener(new WindowAdapter()
    {
      @Override
      public void windowClosed(WindowEvent e)
      {
        viewFrame.dispose();
      }
      
    });
    adjFrame.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    
    viewFrame.setTitle("Ausrüstungs-Bearbeitung");
    viewFrame.setResizable(false);
    viewFrame.addWindowListener(new WindowAdapter()
    {
      @Override
      public void windowClosed(WindowEvent e)
      {
        adjFrame.dispose();
      }
      
    });
    viewFrame.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    
    JPanel p = new JPanel(new BorderLayout());
    
    EQpreview = new JLabel();
    EQpreview.setPreferredSize(new Dimension(320, 480));
    p.add(EQpreview, BorderLayout.NORTH);
    
    JPanel buttons = new JPanel(new GridLayout(1, 2));
    JButton ok = new JButton("Speichern");
    ok.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent e)
      {
        npc.setEquipment(EQ);
      }
    });
    buttons.add(ok);
    
    JButton noEquip = new JButton("Ausrüstung entfernen");
    noEquip.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent e)
      {
        EQ = new Equipment();
        updateEquipDialogPreview();
      }
    });
    buttons.add(noEquip);
    
    p.add(buttons, BorderLayout.SOUTH);
    
    viewFrame.setContentPane(p);
    viewFrame.pack();
    viewFrame.setLocationRelativeTo(null);
    viewFrame.setVisible(true);
    
    JPanel panel = new JPanel(new SpringLayout());
    
    JLabel l = new JLabel(Categories.HAIR.name());
    panel.add(l);
    String[] chars = FileManager.getCharParts(Categories.HAIR.name().toLowerCase());
    EQhair = new JSpinner();
    
    ArrayList<String> list = new ArrayList<String>();
    for (String part : chars)
    {
      if (part.indexOf("_b.png") > -1 || part.indexOf("_m.png") > -1)
        continue;
      list.add(part.replace("_f.png", "").replace(".png", ""));
    }
    EQhair.setModel(new SpinnerListModel(list));
    
    EQhair.setPreferredSize(new Dimension(150, 22));
    if (Arrays.asList(chars).indexOf("none.png") > -1)
      EQhair.setValue("none");
    
    if (npc.getEquipment().hasEquipmentItem(Categories.HAIR))
      EQhair.setValue(npc.getEquipment().getEquipmentItem(Categories.HAIR).getCharPath());
    
    EQhair.addChangeListener(new ChangeListener()
    {
      @Override
      public void stateChanged(ChangeEvent e)
      {
        EQ.setEquipmentItem(Categories.HAIR, new Item(Types.HAIR, EQhair.getValue().toString().replace("_f.png", "").replace(".png", ""), 1));
        updateEquipDialogPreview();
      }
    });
    
    EQhair.addMouseWheelListener(new MouseWheelListener()
    {
      
      @Override
      public void mouseWheelMoved(MouseWheelEvent e)
      {
        if (e.getWheelRotation() < 0)
        {
          if (EQhair.getModel().getPreviousValue() != null)
            EQhair.getModel().setValue(EQhair.getModel().getPreviousValue());
        }
        else if (EQhair.getModel().getNextValue() != null)
          EQhair.getModel().setValue(EQhair.getModel().getNextValue());
      }
    });
    panel.add(EQhair);
    
    l = new JLabel(Categories.SKIN.name());
    panel.add(l);
    chars = FileManager.getCharParts(Categories.SKIN.name().toLowerCase());
    EQskin = new JSpinner();
    
    list = new ArrayList<String>();
    for (String part : chars)
    {
      if (part.indexOf("_b.png") > -1 || part.indexOf("_m.png") > -1)
        continue;
      list.add(part.replace("_f.png", "").replace(".png", ""));
    }
    EQskin.setModel(new SpinnerListModel(list));
    
    EQskin.setPreferredSize(new Dimension(150, 22));
    if (Arrays.asList(chars).indexOf("none.png") > -1)
      EQskin.setValue("none");
    
    if (npc.getEquipment().hasEquipmentItem(Categories.SKIN))
      EQskin.setValue(npc.getEquipment().getEquipmentItem(Categories.SKIN).getCharPath());
    
    EQskin.addChangeListener(new ChangeListener()
    {
      @Override
      public void stateChanged(ChangeEvent e)
      {
        EQ.setEquipmentItem(Categories.SKIN, new Item(Types.SKIN, EQskin.getValue().toString().replace("_f.png", "").replace(".png", ""), 1));
        updateEquipDialogPreview();
      }
    });
    
    EQskin.addMouseWheelListener(new MouseWheelListener()
    {
      
      @Override
      public void mouseWheelMoved(MouseWheelEvent e)
      {
        if (e.getWheelRotation() < 0)
        {
          if (EQskin.getModel().getPreviousValue() != null)
            EQskin.getModel().setValue(EQskin.getModel().getPreviousValue());
        }
        else if (EQskin.getModel().getNextValue() != null)
          EQskin.getModel().setValue(EQskin.getModel().getNextValue());
      }
    });
    panel.add(EQskin);
    
    l = new JLabel(Categories.EYES.name());
    panel.add(l);
    chars = FileManager.getCharParts(Categories.EYES.name().toLowerCase());
    EQeyes = new JSpinner();
    
    list = new ArrayList<String>();
    for (String part : chars)
    {
      if (part.indexOf("_b.png") > -1 || part.indexOf("_m.png") > -1)
        continue;
      list.add(part.replace("_f.png", "").replace(".png", ""));
    }
    EQeyes.setModel(new SpinnerListModel(list));
    
    EQeyes.setPreferredSize(new Dimension(150, 22));
    if (Arrays.asList(chars).indexOf("none.png") > -1)
      EQeyes.setValue("none");
    
    if (npc.getEquipment().hasEquipmentItem(Categories.EYES))
      EQeyes.setValue(npc.getEquipment().getEquipmentItem(Categories.EYES).getCharPath());
    
    EQeyes.addChangeListener(new ChangeListener()
    {
      @Override
      public void stateChanged(ChangeEvent e)
      {
        EQ.setEquipmentItem(Categories.EYES, new Item(Types.EYES, EQeyes.getValue().toString().replace("_f.png", "").replace(".png", ""), 1));
        updateEquipDialogPreview();
      }
    });
    
    EQeyes.addMouseWheelListener(new MouseWheelListener()
    {
      
      @Override
      public void mouseWheelMoved(MouseWheelEvent e)
      {
        if (e.getWheelRotation() < 0)
        {
          if (EQeyes.getModel().getPreviousValue() != null)
            EQeyes.getModel().setValue(EQeyes.getModel().getPreviousValue());
        }
        else if (EQeyes.getModel().getNextValue() != null)
          EQeyes.getModel().setValue(EQeyes.getModel().getNextValue());
      }
    });
    panel.add(EQeyes);
    
    for (final Categories c : Categories.EQUIPS)
    {
      if (Arrays.asList(Categories.NATIVES).contains(c))
        continue;
      
      l = new JLabel(c.name());
      panel.add(l);
      JPanel pnl = new JPanel();
      JButton btn = new JButton("X");
      btn.addActionListener(new ActionListener()
      {
        @Override
        public void actionPerformed(ActionEvent e)
        {
          EQ.setEquipmentItem(c, null);
          updateEquipDialogPreview();
        }
      });
      pnl.add(btn);
      
      btn = new JButton("Bearbeiten");
      btn.addActionListener(new ActionListener()
      {
        @Override
        public void actionPerformed(ActionEvent e)
        {
          showItemDialog(EQ.getEquipmentItem(c));
          if (tmpItem != null)
            EQ.setEquipmentItem(c, tmpItem);
          updateEquipDialogPreview();
        }
      });
      pnl.add(btn);
      panel.add(pnl);
    }
    
    // -- weapon1 -- //
    l = new JLabel("WEAPON 1");
    panel.add(l);
    JPanel pnl = new JPanel();
    JButton btn = new JButton("X");
    btn.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent e)
      {
        EQ.setFirstWeapon(null);
        updateEquipDialogPreview();
      }
    });
    pnl.add(btn);
    
    btn = new JButton("Bearbeiten");
    btn.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent e)
      {
        showItemDialog(EQ.getFirstWeapon());
        if (tmpItem != null)
          EQ.setFirstWeapon(tmpItem);
        updateEquipDialogPreview();
      }
    });
    pnl.add(btn);
    panel.add(pnl);
    
    // -- weapon2 -- //
    l = new JLabel("WEAPON 2");
    panel.add(l);
    pnl = new JPanel();
    btn = new JButton("X");
    btn.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent e)
      {
        EQ.setSecondWeapon(null);
        updateEquipDialogPreview();
      }
    });
    pnl.add(btn);
    
    btn = new JButton("Bearbeiten");
    btn.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent e)
      {
        showItemDialog(EQ.getSecondWeapon());
        if (tmpItem != null)
          EQ.setSecondWeapon(tmpItem);
        updateEquipDialogPreview();
      }
    });
    pnl.add(btn);
    panel.add(pnl);
    
    updateEquipDialogPreview();
    
    SpringUtilities.makeCompactGrid(panel, Categories.EQUIPS.length + 2, 2, 6, 6, 6, 6);
    
    adjFrame.setContentPane(panel);
    adjFrame.pack();
    adjFrame.setLocation(viewFrame.getX() + viewFrame.getWidth() + 10, viewFrame.getY());
    adjFrame.setVisible(true);
  }
  
  public void updateEquipDialogPreview()
  {
    int w = EQpreview.getPreferredSize().width;
    int h = EQpreview.getPreferredSize().height;
    BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = (Graphics2D) bi.getGraphics();
    
    for (int i = 0; i < 4; i++)
    {
      for (int j = 0; j < 4; j++)
      {
        Assistant.drawChar(i * w / 4, j * h / 4, w / 4, h / 4, j, i, EQ, g, null, true);
      }
    }
    EQpreview.setIcon(new ImageIcon(bi));
  }
  
  public void showTalkDialog(final NPCButton npc)
  {
    final JDialog talkFrame = new JDialog(w);
    talkFrame.setTitle("Talk-Bearbeitung - NPC #" + npc.ID);
    talkFrame.setResizable(false);
    talkFrame.setAlwaysOnTop(true);
    talkFrame.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    
    JPanel p = new JPanel(new FlowLayout());
    p.setPreferredSize(new Dimension(600, 500));
    talkPanel = new JPanel();
    talkPanel.setPreferredSize(new Dimension(600, 0));
    talkPanel.setLayout(null);
    talkScrollPane = new JScrollPane(talkPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    talkScrollPane.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.gray));
    talkScrollPane.setPreferredSize(new Dimension(600, 310));
    p.add(talkScrollPane);
    
    talkColorSlider = new JColorSlider();
    talkColorSlider.setPreferredSize(new Dimension(600, 150));
    p.add(talkColorSlider);
    
    talkAdd = new JButton("Talk hinzufügen");
    talkAdd.setPreferredSize(new Dimension(295, 24));
    talkAdd.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent e)
      {
        addTalkComponent(null);
      }
    });
    p.add(talkAdd);
    talkOk = new JButton("Speichern");
    talkOk.setPreferredSize(new Dimension(295, 24));
    talkOk.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent e)
      {
        JSONArray talk = new JSONArray();
        for (int i = 0; i < talkPanel.getComponentCount(); i++)
        {
          JTextField talkCond = (JTextField) ((JPanel) talkPanel.getComponent(i)).getComponent(1);
          JTextArea talkText = (JTextArea) ((JScrollPane) ((JPanel) talkPanel.getComponent(i)).getComponent(3)).getViewport().getView();
          
          if (talkCond.getText().length() == 0 && talkText.getText().length() == 0)
            continue;
          
          JSONArray cond = null;
          try
          {
            cond = new JSONArray("[" + talkCond.getText() + "]");
          }
          catch (JSONException e1)
          {
            e1.printStackTrace();
            JOptionPane.showMessageDialog(talkFrame, "Talk #" + (i + 1) + " konnte nicht gespeichert werden!\nDer Text im Konditionsfeld ist ungültig!\nDer Speichervorgang wird abgebrochen.", "Fehler!", JOptionPane.ERROR_MESSAGE);
            return;
          }
          try
          {
            JSONObject t = new JSONObject();
            t.put("cond", cond);
            t.put("text", talkText.getText());
            
            talk.put(t);
          }
          catch (JSONException e1)
          {
            e1.printStackTrace();
          }
        }
        npc.talk = talk;
      }
    });
    p.add(talkOk);
    
    talkFrame.setContentPane(p);
    talkFrame.pack();
    talkFrame.setLocationRelativeTo(null);
    
    if (npc != null)
    {
      for (int i = 0; i < npc.talk.length(); i++)
      {
        try
        {
          addTalkComponent(npc.talk.getJSONObject(i));
        }
        catch (JSONException e1)
        {
          e1.printStackTrace();
        }
      }
    }
    
    addTalkComponent(null);
    
    talkFrame.setVisible(true);
  }
  
  private void addTalkComponent(JSONObject data)
  {
    
    JPanel p = new JPanel(new SpringLayout());
    
    if (talkPanel.getComponentCount() > 0)
      p.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, Color.gray));
    
    JLabel label = new JLabel("Bedingungen: ", JLabel.TRAILING);
    p.add(label);
    JTextField talkCond = new JTextField();
    if (data != null)
    {
      try
      {
        talkCond.setText(data.getJSONArray("cond").toString().replaceAll("(\\[)|(\\])|(\\\")", "").replace(",", ", "));
      }
      catch (JSONException e)
      {
        e.printStackTrace();
      }
    }
    p.add(talkCond);
    
    label = new JLabel("Text: ", JLabel.TRAILING);
    p.add(label);
    
    JTextArea talkText = new JTextArea(4, 0);
    talkText.setLineWrap(true);
    talkText.setFont(talkCond.getFont());
    if (data != null)
    {
      try
      {
        talkText.setText(data.getString("text"));
      }
      catch (JSONException e)
      {
        e.printStackTrace();
      }
    }
    
    JScrollPane pane = new JScrollPane(talkText, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    
    p.add(pane);
    
    p.setBounds(0, talkPanel.getComponentCount() * talkComponentHeight, talkComponentWidth, talkComponentHeight);
    SpringUtilities.makeCompactGrid(p, 2, 2, 6, 6, 6, 6);
    
    talkPanel.setPreferredSize(new Dimension(600, talkPanel.getPreferredSize().height + talkComponentHeight));
    talkPanel.add(p);
    
    talkScrollPane.setViewportView(talkPanel);
  }
  
  public void showItemDialog(final Item exist)
  {
    if (exist != null)
    {
      tmpAttributes = exist.getAttributes();
      tmpRequires = exist.getRequirements();
    }
    
    final JDialog itemFrame = new JDialog(w, true);
    itemFrame.setTitle("Item-Bearbeitung");
    itemFrame.setResizable(false);
    itemFrame.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    
    final JPanel p = new JPanel(new SpringLayout());
    
    JLabel l = new JLabel("Icon-X:");
    p.add(l);
    final JSpinner ix = new JSpinner(new SpinnerNumberModel(0, 0, 16, 1));
    if (exist != null)
      ix.setValue(exist.getIconPoint().x);
    p.add(ix);
    
    l = new JLabel("Icon-Y:");
    p.add(l);
    final JSpinner iy = new JSpinner(new SpinnerNumberModel(0, 0, 629, 1));
    if (exist != null)
      iy.setValue(exist.getIconPoint().y);
    p.add(iy);
    
    l = new JLabel("Korrektur-X:");
    p.add(l);
    final JSpinner cx = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
    if (exist != null)
      cx.setValue(exist.getCorrectionX());
    p.add(cx);
    
    l = new JLabel("Korrektur-Y:");
    p.add(l);
    final JSpinner cy = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
    if (exist != null)
      cy.setValue(exist.getCorrectionY());
    p.add(cy);
    
    l = new JLabel("Name:");
    p.add(l);
    final JTextField name = new JTextField(15);
    if (exist != null)
      name.setText(exist.getName());
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
    final JComboBox<Types> type = new JComboBox<Types>(Types.values());
    
    path.addItemListener(new ItemListener()
    {
      @Override
      public void itemStateChanged(ItemEvent e)
      {
        if (e.getStateChange() != ItemEvent.SELECTED)
          return;
        
        Image body1 = Viewport.loadImage("char/skin/man_f.png");
        Image body2 = Viewport.loadImage("char/skin/man_b.png");
        Image part = Viewport.loadImage("char/" + type.getSelectedItem().toString().toLowerCase() + "/" + path.getSelectedItem().toString() + ".png");
        if (part == null)
          part = Viewport.loadImage("char/" + type.getSelectedItem().toString().toLowerCase() + "/" + path.getSelectedItem().toString() + "_f.png");
        
        BufferedImage bi = new BufferedImage(body1.getWidth(null), body1.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics g = bi.getGraphics();
        if (!((Types) type.getSelectedItem()).equals(Types.SKIN))
          g.drawImage(body2, 0, 0, null);
        if (!((Types) type.getSelectedItem()).equals(Types.SKIN))
          g.drawImage(body1, 0, 0, null);
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
          String[] parts = FileManager.getCharParts(i.name().toLowerCase());
          for (String part : parts)
          {
            if (part.indexOf("_b.png") > -1 || part.indexOf("_m.png") > -1)
              continue;
            path.addItem(part.replace("_f.png", "").replace(".png", ""));
          }
          if (exist != null && exist.getCharPath() != null && exist.getType().equals(i))
            path.setSelectedItem(exist.getCharPath());
        }
      }
    });
    if (exist != null)
      type.setSelectedItem(exist.getType());
    p.add(type);
    
    l = new JLabel("Attribute:");
    p.add(l);
    JButton btn = new JButton("Bearbeiten");
    btn.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent e)
      {
        showAttributesDialog((tmpAttributes != null) ? tmpAttributes : new Attributes(), false);
        tmpAttributes = tmpAttr;
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
        showAttributesDialog((tmpRequires != null) ? tmpRequires : new Attributes(), false);
        tmpRequires = tmpAttr;
      }
    });
    p.add(btn);
    
    actionSettings = new JPanel(new SpringLayout());
    
    final String[] actions = new String[] { "EmptyAction", "PotionAction", "WeaponAction" }; // TODO: Keep in sync with available item actions. SkillAction is excluded, it's only for native purpose
    l = new JLabel("Action:");
    p.add(l);
    final JComboBox<String> action = new JComboBox<String>(actions);
    action.addItemListener(new ItemListener()
    {
      @Override
      public void itemStateChanged(ItemEvent e)
      {
        if (e.getStateChange() != ItemEvent.SELECTED)
          return;
        
        JPanel labels = new JPanel(new SpringLayout());
        JPanel panel = new JPanel(new SpringLayout());
        
        switch (e.getItem().toString())
        {
          case "PotionAction":
          {
            labels.add(new JLabel("Target:"));
            potionTarget = new JTextField("CASTER");
            potionTarget.setColumns(15);
            if (exist != null && exist.getAction() instanceof PotionAction)
              potionTarget.setText(((PotionAction) exist.getAction()).getTarget());
            panel.add(potionTarget);
            
            labels.add(new JLabel("Attribute:"));
            JButton btn = new JButton("Bearbeiten");
            if (potionAttributes == null && exist != null && exist.getAction() instanceof PotionAction)
              potionAttributes = ((PotionAction) exist.getAction()).getChanges();
            btn.addActionListener(new ActionListener()
            {
              @Override
              public void actionPerformed(ActionEvent e)
              {
                showAttributesDialog((potionAttributes != null) ? potionAttributes : new Attributes(), false);
                potionAttributes = tmpAttr;
              }
            });
            panel.add(btn);
            
            labels.add(new JLabel("Schadens-Typ:"));
            potionDamageType = new JComboBox<DamageType>(DamageType.values());
            panel.add(potionDamageType);
            
            SpringUtilities.makeCompactGrid(labels, 3, 1, 6, 12, 6, 12);
            SpringUtilities.makeCompactGrid(panel, 3, 1, 6, 6, 6, 6);
            break;
          }
          case "WeaponAction":
          {
            labels.add(new JLabel("Attribute:"));
            JButton btn = new JButton("Bearbeiten");
            if (weaponAttributes == null && exist != null && exist.getAction() instanceof WeaponAction)
              weaponAttributes = ((WeaponAction) exist.getAction()).getEffect();
            btn.addActionListener(new ActionListener()
            {
              @Override
              public void actionPerformed(ActionEvent e)
              {
                showAttributesDialog((weaponAttributes != null) ? weaponAttributes : new Attributes(), true);
                weaponAttributes = tmpAttr;
              }
            });
            panel.add(btn);
            
            labels.add(new JLabel("Schadens-Typ:"));
            weaponDamageType = new JComboBox<DamageType>(DamageType.values());
            if (exist != null && exist.getAction() instanceof WeaponAction)
              weaponDamageType.setSelectedItem(((WeaponAction) exist.getAction()).getDamageType());
            panel.add(weaponDamageType);
            
            SpringUtilities.makeCompactGrid(labels, 2, 1, 6, 6, 6, 6);
            SpringUtilities.makeCompactGrid(panel, 2, 1, 6, 6, 6, 6);
            break;
          }
        }
        p.remove(22);
        p.remove(actionSettings);
        SpringUtilities.makeCompactGrid(p, 11, 2, 6, 6, 6, 6);
        p.add(labels, 22);
        p.add(panel, 23);
        actionSettings = panel;
        SpringUtilities.makeCompactGrid(p, 13, 2, 6, 6, 6, 6);
        itemFrame.pack();
      }
    });
    
    p.add(action);
    
    p.add(new JLabel());
    p.add(actionSettings);
    
    p.add(new JLabel());
    JButton ok = new JButton("OK");
    ok.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent e)
      {
        ItemAction ia = new EmptyAction();
        
        if (action.getSelectedItem().equals("PotionAction"))
          ia = new PotionAction(potionTarget.getText(), potionAttributes, (DamageType) potionDamageType.getSelectedItem());
        
        else if (action.getSelectedItem().equals("WeaponAction"))
          ia = new WeaponAction(weaponAttributes, (DamageType) weaponDamageType.getSelectedItem());
        
        String charPath = "";
        if (path.getSelectedItem() != null)
          charPath = path.getSelectedItem().toString().replace("_f.png", "").replace("_b.png", "").replace("_m.png", "").replace(".png", "");
        
        tmpItem = new Item((Types) type.getSelectedItem(), name.getText(), (int) ix.getValue(), (int) iy.getValue(), (int) cx.getValue(), (int) cy.getValue(), charPath, tmpAttributes, tmpRequires, ia, 1);
        
        itemFrame.dispose();
      }
    });
    p.add(ok);
    
    if (exist != null)
      action.setSelectedItem(exist.getAction().getClass().getSimpleName());
    
    SpringUtilities.makeCompactGrid(p, 13, 2, 6, 6, 6, 6);
    
    itemFrame.setContentPane(p);
    
    itemFrame.pack();
    itemFrame.setLocationRelativeTo(null);
    itemFrame.setVisible(true);
  }
  
  private void updateNPCDialogPreview()
  {
    String sprite = NPCsprite.getSelectedItem().toString();
    BufferedImage image = (BufferedImage) Viewport.loadImage("char/chars/" + sprite + ".png");
    NPCpreview.setPreferredSize(new Dimension(image.getWidth() / 4, image.getHeight() / 4));
    NPCpreview.setIcon(new ImageIcon(image.getSubimage(0, image.getHeight() / 4 * NPCdir.getSelectedIndex(), image.getWidth() / 4, image.getHeight() / 4)));
    NPCframe.invalidate();
    NPCframe.pack();
  }
  
  private void updateNPCCoords(int x, int y)
  {
    NPCx.setText(x + "");
    NPCy.setText((y - 16) + "");
  }
  
  public NPCButton addNPC(JSONObject data)
  {
    try
    {
      final JPopupMenu jpm = new JPopupMenu();
      NPCButton npc;
      if (data == null)
      {
        npc = new NPCButton(Integer.parseInt(NPCx.getText()), Integer.parseInt(NPCy.getText()), NPCpreview.getPreferredSize().width - CFG.BOUNDMALUS, NPCpreview.getPreferredSize().height - CFG.BOUNDMALUS, NPCdir.getSelectedIndex(), NPCname.getText(), NPCsprite.getSelectedItem().toString(), (double) NPCspeed.getValue(), NPCmove.isSelected(), NPClook.isSelected(), (int) NPCmoveT.getValue(), (int) NPClookT.getValue(), ((ImageIcon) NPCpreview.getIcon()).getImage(), NPChostile.isSelected(), NPClastID, NPCai.getSelectedItem().toString(), this);
        npc.attributes = NPCattr;
      }
      else
      {
        BufferedImage image = (BufferedImage) Viewport.loadImage("char/chars/" + data.getString("char") + ".png");
        npc = new NPCButton(data.getInt("x"), data.getInt("y"), data.getInt("w"), data.getInt("h"), data.getInt("dir"), data.getString("name"), data.getString("char"), data.getDouble("speed"), data.getJSONObject("random").getBoolean("move"), data.getJSONObject("random").getBoolean("look"), data.getJSONObject("random").getInt("moveT"), data.getJSONObject("random").getInt("lookT"), image.getSubimage(0, data.getInt("dir") * image.getHeight() / 4, image.getWidth() / 4, image.getHeight() / 4), data.getBoolean("hostile"), NPClastID, data.getString("ai"), this);
        npc.talk = data.getJSONArray("talk");
        npc.setEquipment(new Equipment(data.getJSONObject("equip")));
        npc.attributes = new Attributes(data.getJSONObject("attr"));
      }
      
      NPClastID++;
      
      final NPCButton fNPC = npc;
      
      JMenuItem edit = new JMenuItem(new AbstractAction("Bearbeiten")
      {
        private static final long serialVersionUID = 1L;
        
        @Override
        public void actionPerformed(ActionEvent e)
        {
          showNPCDialog(fNPC);
        }
      });
      jpm.add(edit);
      
      JMenuItem eedit = new JMenuItem(new AbstractAction("Ausrüstung bearbeiten")
      {
        private static final long serialVersionUID = 1L;
        
        @Override
        public void actionPerformed(ActionEvent e)
        {
          showEquipmentDialog(fNPC);
        }
      });
      jpm.add(eedit);
      
      JMenuItem tedit = new JMenuItem(new AbstractAction("Talk bearbeiten")
      {
        private static final long serialVersionUID = 1L;
        
        @Override
        public void actionPerformed(ActionEvent e)
        {
          showTalkDialog(fNPC);
        }
      });
      jpm.add(tedit);
      
      JMenuItem del = new JMenuItem(new AbstractAction("Löschen")
      {
        private static final long serialVersionUID = 1L;
        
        @Override
        public void actionPerformed(ActionEvent e)
        {
          map.remove(fNPC);
          
          map.repaint();
        }
      });
      jpm.add(del);
      npc.addMouseListener(new MouseAdapter()
      {
        @Override
        public void mousePressed(MouseEvent e)
        {
          if (e.getButton() == 3)
            jpm.show(e.getComponent(), e.getX(), e.getY());
        }
      });
      map.add(npc, JLayeredPane.PALETTE_LAYER);
      
      msp.setViewportView(map);
      
      return npc;
    }
    catch (Exception e)
    {
      e.printStackTrace();
      JOptionPane.showMessageDialog(NPCframe, "Bitte alle Felder korrekt ausfüllen!", "NPC-Ertellung", JOptionPane.ERROR_MESSAGE);
      return null;
    }
  }
  
  public TileButton addTile(Image icon, final int x, final int y, String t, int tx, int ty, double l, JSONObject data, boolean undo)
  {
    try
    {
      if (l == -1)
      {
        for (int i = 0; i < map.getComponentCount(); i++)
        {
          if (!(map.getComponent(i) instanceof TileButton))
            continue;
          TileButton b = (TileButton) map.getComponent(i);
          if (b.getBounds().intersects(x, y, CFG.FIELDSIZE, CFG.FIELDSIZE))
          {
            l = b.getLayer();
          }
        }
        l++;
      }
      final TileButton tile = new TileButton(x, y, tx, ty, l, t, icon, this);
      tile.data = data;
      tile.setEnabled(false);
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
          String result = JOptionPane.showInputDialog(w, "Layer anpassen", tile.getLayer());
          
          if (result == null)
            return;
          
          try
          {
            double ly = Double.parseDouble(result);
            cachelayer = ly;
            tile.setLayer(ly);
            saveMap();
            try
            {
              openMap(mapdata.getString("name"));
            }
            catch (JSONException e1)
            {
              e1.printStackTrace();
            }
          }
          catch (Exception e1)
          {
            JOptionPane.showMessageDialog(w, "Layerangabe darf nur Zahlen enthalten.", "Fehler!", JOptionPane.ERROR_MESSAGE);
            return;
          }
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
      tile.addMouseListener(new MouseAdapter()
      {
        @Override
        public void mousePressed(MouseEvent e)
        {}
        
        @Override
        public void mouseReleased(MouseEvent e)
        {
          if (e.getButton() == 3 && !deletemode && !dragmode)
            jpm.show(tile, e.getX(), e.getY());
          
          if (e.getButton() == 1)
          {
            JButton src = (JButton) e.getSource();
            if (selectedtile != null && map.mouseDown == null && map.mousePos == null)
            {
              int round = (gridmode) ? CFG.FIELDSIZE : 1;
              addTile(((ImageIcon) selectedtile.getIcon()).getImage(), Assistant.round(e.getX() - CFG.FIELDSIZE / 2 + src.getX(), round), Assistant.round(e.getY() - CFG.FIELDSIZE / 2 + src.getY(), round), tileset, selectedtile.getX() / CFG.FIELDSIZE, selectedtile.getY() / CFG.FIELDSIZE, -1, new JSONObject(), true);
            }
            else if (NPCframe != null)
            {
              updateNPCCoords(e.getX() + src.getX(), e.getY() + src.getY());
            }
          }
          if (e.getButton() == 3 && deletemode)
          {
            map.remove(tile);
            msp.setViewportView(map);
          }
        }
        
        @Override
        public void mouseEntered(MouseEvent e)
        {
          showCustomCursor(true);
        }
        
        @Override
        public void mouseExited(MouseEvent e)
        {
          showCustomCursor(false);
        }
      });
      if (mapdata == null)
        return null;
      map.add(tile, JLayeredPane.DEFAULT_LAYER);
      
      map.setComponentZOrder(tile, 0);
      
      if (undo)
      {
        lastChangedTiles = new TileButton[] { tile };
        mUndo.setEnabled(true);
      }
      
      cachelayer = 0;
      return tile;
    }
    catch (Exception e1)
    {
      e1.printStackTrace();
      return null;
    }
    
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
          for (String s : Map.getMaps(mappackdata.getString("name"), CFG.MAPEDITORDIR))
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
                BufferedImage bi = new Map(mappackdata.getString("name"), s, CFG.MAPEDITORDIR).getRendered(1, v);
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
                      BufferedImage bi = new Map(mappackdata.getString("name"), s, CFG.MAPEDITORDIR).getRendered(1, v);
                      int d = Arrays.asList(dirs).indexOf(((String) dir.getSelectedItem())) - 1;
                      d = (d < 0) ? 0 : d;
                      Assistant.drawChar(e.getX() - CFG.HUMANBOUNDS[0] / 2, e.getY() - CFG.HUMANBOUNDS[1] * 2 / 3, CFG.HUMANBOUNDS[0], CFG.HUMANBOUNDS[1], d, 0, Equipment.getDefault(true), (Graphics2D) bi.getGraphics(), null, true);// Assistant.Rect(e.getX() - CFG.HUMANBOUNDS[0] / 2, e.getY() - CFG.HUMANBOUNDS[0], CFG.HUMANBOUNDS[0], CFG.HUMANBOUNDS[1], Color.cyan, null, (Graphics2D) bi.getGraphics());
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
  
  public void handleSelectionBox()
  {
    new Thread()
    {
      public void run()
      {
        int w = map.selW / CFG.FIELDSIZE;
        int h = map.selH / CFG.FIELDSIZE;
        
        if (w + h < 2)
          return;
        
        ArrayList<TileButton> tiles = new ArrayList<TileButton>();
        
        if (!map.delSelection && selectedtile == null)
          return;
        
        double layer = 0;
        
        if (!map.delSelection)
        {
          String inputString = JOptionPane.showInputDialog(MapEditor.this.w, "Layer der einzufügenden Felder angeben");
          if (inputString == null)
            return;
          
          layer = Double.parseDouble(inputString);
        }
        
        for (int i = 0; i < w; i++)
        {
          for (int j = 0; j < h; j++)
          {
            if (!map.delSelection)
            {
              Image image = ((ImageIcon) selectedtile.getIcon()).getImage();
              int tx = selectedtile.getX() / CFG.FIELDSIZE;
              int ty = selectedtile.getY() / CFG.FIELDSIZE;
              
              if (Arrays.asList(CFG.AUTOTILES).contains(tileset))
              {
                if (i == 0)
                  tx = 0;
                if (j == 0)
                  ty = 1;
                
                if (i > 0 && i < w - 1)
                  tx = 1;
                if (j > 0 && j < w - 1)
                  ty = 2;
                
                if (i == w - 1)
                  tx = 2;
                if (j == h - 1)
                  ty = 3;
                
                image = autotiles[tx][ty];
              }
              tiles.add(addTile(image, i * CFG.FIELDSIZE + map.selX, j * CFG.FIELDSIZE + map.selY, tileset, tx, ty, layer, new JSONObject(), false));
              map.repaint();
            }
            else
            {
              Component temp;
              while ((temp = map.getComponentAt(i * CFG.FIELDSIZE + map.selX, j * CFG.FIELDSIZE + map.selY)) != null && temp instanceof TileButton)
              {
                map.remove(temp);
                
                tiles.add((TileButton) temp);
                break;
              }
              map.repaint();
            }
          }
        }
        if (tiles.size() > 0)
        {
          lastChangedTiles = tiles.toArray(new TileButton[] {});
          mUndo.setEnabled(true);
          tilesWereDeleted = map.delSelection;
        }
        map.mouseDown = null;
        map.mousePos = null;
        
        map.repaint();
      }
      
    }.start();
  }
  
  public void showCustomCursor(boolean show)
  {
    if (!show && (selectedtile != null || NPCframe != null))
    {
      w.setCursor(Cursor.getDefaultCursor());
      return;
    }
    
    if (selectedtile != null)
      w.setCursor(Toolkit.getDefaultToolkit().createCustomCursor(getTileImage(), new Point(CFG.FIELDSIZE / 2, CFG.FIELDSIZE / 2), "tile"));
    
    if (NPCframe != null)
    {
      BufferedImage image = (BufferedImage) ((ImageIcon) NPCpreview.getIcon()).getImage();
      int x = 0;
      int y = 0;
      int width = image.getWidth();
      int height = image.getHeight();
      x = (width > 32) ? width - 32 : x;
      y = (height > 32) ? height - 32 : y;
      width = (width > 32) ? 32 : width;
      height = (height > 32) ? 32 : height;
      w.setCursor(Toolkit.getDefaultToolkit().createCustomCursor(image.getSubimage(x, y, width, height), new Point(0, 0), "npc"));
    }
  }
  
  public void undo()
  {
    new Thread()
    {
      public void run()
      {
        for (TileButton tile : lastChangedTiles)
        {
          if (!tilesWereDeleted)
          {
            map.remove(tile);
          }
          else
          {
            map.add(tile);
          }
        }
        map.repaint();
        lastChangedTiles = null;
        mUndo.setEnabled(false);
      }
    }.start();
  }
  
  public void showFilterReplaceDialog()
  {
    JDialog FRframe = new JDialog(w, "Felder per Filter ersetzen");
    FRframe.setResizable(false);
    FRframe.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    
    dragmode = true;
    selectedtile = null;
    
    FRframe.addWindowListener(new WindowAdapter()
    {
      @Override
      public void windowClosed(WindowEvent e)
      {
        for (Component c : map.getComponents())
        {
          if (c instanceof TileButton)
          {
            ((TileButton) c).fitsFilter = false;
            ((TileButton) c).update = true;
            ((TileButton) c).repaint();
          }
        }
        dragmode = mDrag.isSelected();
        
        map.mouseDown = null;
        map.mousePos = null;
        
        map.repaint();
      }
    });
    
    JPanel p = new JPanel(new SpringLayout());
    
    JPanel oldPanel = new JPanel(new SpringLayout());
    oldPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.gray), "Finden"));
    
    JLabel label = new JLabel("Tileset:");
    oldPanel.add(label);
    FRoldTileset = new JComboBox<String>(Assistant.concat(new String[] { "Ignorieren" }, tilesets));
    FRoldTileset.setSelectedIndex(0);
    oldPanel.add(FRoldTileset);
    
    label = new JLabel("Layer:");
    oldPanel.add(label);
    FRoldLayer = new JTextField();
    oldPanel.add(FRoldLayer);
    
    label = new JLabel("Tileset-X:");
    oldPanel.add(label);
    FRoldTX = new JTextField();
    oldPanel.add(FRoldTX);
    
    label = new JLabel("Tileset-Y:");
    oldPanel.add(label);
    FRoldTY = new JTextField();
    oldPanel.add(FRoldTY);
    
    SpringUtilities.makeCompactGrid(oldPanel, 4, 2, 6, 6, 6, 6);
    
    p.add(oldPanel);
    
    JPanel newPanel = new JPanel(new SpringLayout());
    newPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.gray), "Ersetzen"));
    
    label = new JLabel("Tileset:");
    newPanel.add(label);
    FRnewTileset = new JComboBox<String>(Assistant.concat(new String[] { "Ignorieren" }, tilesets));
    FRnewTileset.setSelectedIndex(0);
    newPanel.add(FRnewTileset);
    
    label = new JLabel("Layer:");
    newPanel.add(label);
    FRnewLayer = new JTextField();
    newPanel.add(FRnewLayer);
    
    label = new JLabel("Tileset-X:");
    newPanel.add(label);
    FRnewTX = new JTextField();
    newPanel.add(FRnewTX);
    
    label = new JLabel("Tileset-Y:");
    newPanel.add(label);
    FRnewTY = new JTextField();
    newPanel.add(FRnewTY);
    
    SpringUtilities.makeCompactGrid(newPanel, 4, 2, 6, 6, 6, 6);
    
    p.add(newPanel);
    
    JButton find = new JButton("Finden");
    find.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent e)
      {
        Double layer = Assistant.parseDouble(FRoldLayer.getText());
        Integer tx = Assistant.parseInt(FRoldTX.getText());
        Integer ty = Assistant.parseInt(FRoldTY.getText());
        
        for (Component c : map.getComponents())
        {
          if (c instanceof TileButton)
          {
            ((TileButton) c).fitsFilter = false;
            ((TileButton) c).update = true;
            ((TileButton) c).repaint();
          }
        }
        
        if (map.mouseDown == null)
        {
          for (Component c : map.getComponents())
          {
            if (c instanceof TileButton)
            {
              ((TileButton) c).checkReplaceFilterFits(FRoldTileset.getSelectedItem().toString(), layer, tx, ty);
            }
          }
        }
        else
        {
          for (int i = 0; i < map.selW / CFG.FIELDSIZE; i++)
          {
            for (int j = 0; j < map.selH / CFG.FIELDSIZE; j++)
            {
              for (Component c : map.getComponents())
              {
                if (c instanceof TileButton && c.getX() >= i * CFG.FIELDSIZE + map.selX && c.getX() < (i + 1) * CFG.FIELDSIZE + map.selX && c.getY() >= j * CFG.FIELDSIZE + map.selY && c.getY() < (j + 1) * CFG.FIELDSIZE + map.selY)
                {
                  ((TileButton) c).checkReplaceFilterFits(FRoldTileset.getSelectedItem().toString(), layer, tx, ty);
                }
              }
            }
          }
        }
      }
    });
    
    p.add(find);
    
    JButton replace = new JButton("Ersetzen");
    replace.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent e)
      {
        Double layer = Assistant.parseDouble(FRnewLayer.getText());
        Integer tx = Assistant.parseInt(FRnewTX.getText());
        Integer ty = Assistant.parseInt(FRnewTY.getText());
        
        for (Component c : map.getComponents())
        {
          if (c instanceof TileButton)
          {
            ((TileButton) c).execFilterReplace(FRnewTileset.getSelectedItem().toString(), layer, tx, ty);
          }
        }
      }
    });
    p.add(replace);
    
    p.add(new JLabel("Gefundene Felder: "));
    p.add(new JButton(new AbstractAction("Entfernen")
    {
      private static final long serialVersionUID = 1L;
      
      @Override
      public void actionPerformed(ActionEvent e)
      {
        for (Component c : map.getComponents())
        {
          if (c instanceof TileButton && ((TileButton) c).fitsFilter)
          {
            map.remove(c);
            map.repaint();
          }
        }
      }
    }));
    
    SpringUtilities.makeCompactGrid(p, 3, 2, 6, 6, 6, 6);
    
    FRframe.setContentPane(p);
    FRframe.pack();
    FRframe.setLocationRelativeTo(null);
    FRframe.setVisible(true);
  }
  
  public class SelectionListener implements MouseListener, MouseMotionListener
  {
    JComponent component;
    
    public SelectionListener(JComponent c)
    {
      component = c;
    }
    
    @Override
    public void mouseDragged(MouseEvent e)
    {
      if (!dragmode)
        return;
      
      if (e.getModifiers() != 16 && e.getModifiers() != 4)
        return;
      
      map.delSelection = e.getModifiers() == 4;
      
      if (map.mouseDown == null)
      {
        if (gridmode)
          map.mouseDown = new Point(Assistant.round(e.getPoint().x + ((component != null) ? component.getX() : 0), CFG.FIELDSIZE), Assistant.round(e.getPoint().y + ((component != null) ? component.getY() : 0), CFG.FIELDSIZE));
        else map.mouseDown = new Point(e.getPoint().x + ((component != null) ? component.getX() : 0), e.getPoint().y + ((component != null) ? component.getY() : 0));
      }
      
      if (gridmode)
        map.mousePos = new Point(Assistant.round(e.getPoint().x + ((component != null) ? component.getX() : 0), CFG.FIELDSIZE), Assistant.round(e.getPoint().y + ((component != null) ? component.getY() : 0), CFG.FIELDSIZE));
      else map.mousePos = new Point(Assistant.round(e.getPoint().x + ((component != null) ? component.getX() : 0) - map.mouseDown.x, CFG.FIELDSIZE) + map.mouseDown.x, Assistant.round(e.getPoint().y + ((component != null) ? component.getY() : 0) - map.mouseDown.y, CFG.FIELDSIZE) + map.mouseDown.y);
      
      map.repaint();
    }
    
    @Override
    public void mouseMoved(MouseEvent e)
    {}
    
    @Override
    public void mouseClicked(MouseEvent e)
    {}
    
    @Override
    public void mousePressed(MouseEvent e)
    {
      if (!dragmode)
        return;
      
      
      map.mouseDown = null;
      map.mousePos = null;
      map.repaint();
    }
    
    @Override
    public void mouseReleased(MouseEvent e)
    {
      if (!dragmode)
        return;
      
      if (map.mouseDown != null)
        handleSelectionBox();
      map.repaint();
    }
    
    @Override
    public void mouseEntered(MouseEvent e)
    {}
    
    @Override
    public void mouseExited(MouseEvent e)
    {}
    
  }
}
