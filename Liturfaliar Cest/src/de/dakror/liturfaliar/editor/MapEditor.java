package de.dakror.liturfaliar.editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
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
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.ToolTipManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.editor.dialog.EditFieldDataDialog;
import de.dakror.liturfaliar.editor.dialog.EquipmentDialog;
import de.dakror.liturfaliar.editor.dialog.FilterReplaceDialog;
import de.dakror.liturfaliar.editor.dialog.ImportObjectDialog;
import de.dakror.liturfaliar.editor.dialog.NPCDialog;
import de.dakror.liturfaliar.editor.dialog.NewMapDialog;
import de.dakror.liturfaliar.editor.dialog.NewMapPackDialog;
import de.dakror.liturfaliar.editor.dialog.OpenMapDialog;
import de.dakror.liturfaliar.editor.dialog.OpenMapPackDialog;
import de.dakror.liturfaliar.editor.dialog.TalkDialog;
import de.dakror.liturfaliar.item.Equipment;
import de.dakror.liturfaliar.item.IconSelecter;
import de.dakror.liturfaliar.item.Item;
import de.dakror.liturfaliar.map.Map;
import de.dakror.liturfaliar.map.data.FieldData;
import de.dakror.liturfaliar.settings.Attributes;
import de.dakror.liturfaliar.settings.CFG;
import de.dakror.liturfaliar.settings.DamageType;
import de.dakror.liturfaliar.ui.swing.JColorSlider;
import de.dakror.liturfaliar.util.Assistant;
import de.dakror.liturfaliar.util.Compressor;
import de.dakror.liturfaliar.util.FileManager;
import de.dakror.liturfaliar.util.Vector;
import de.dakror.universion.UniVersion;

public class MapEditor
{
  
  public class SelectionListener implements MouseListener, MouseMotionListener
  {
    JComponent component;
    
    public SelectionListener(JComponent c)
    {
      component = c;
    }
    
    @Override
    public void mouseClicked(MouseEvent e)
    {}
    
    @Override
    public void mouseDragged(MouseEvent e)
    {
      if (!dragmode) return;
      
      if (e.getModifiers() != 16 && e.getModifiers() != 4) return;
      
      map.delSelection = e.getModifiers() == 4;
      
      if (map.mouseDown == null)
      {
        if (gridmode) map.mouseDown = new Point(Assistant.round(e.getPoint().x + ((component != null) ? component.getX() : 0), CFG.FIELDSIZE), Assistant.round(e.getPoint().y + ((component != null) ? component.getY() : 0), CFG.FIELDSIZE));
        else map.mouseDown = new Point(e.getPoint().x + ((component != null) ? component.getX() : 0), e.getPoint().y + ((component != null) ? component.getY() : 0));
      }
      
      if (gridmode) map.mousePos = new Point(Assistant.round(e.getPoint().x + ((component != null) ? component.getX() : 0), CFG.FIELDSIZE), Assistant.round(e.getPoint().y + ((component != null) ? component.getY() : 0), CFG.FIELDSIZE));
      else map.mousePos = new Point(Assistant.round(e.getPoint().x + ((component != null) ? component.getX() : 0) - map.mouseDown.x, CFG.FIELDSIZE) + map.mouseDown.x, Assistant.round(e.getPoint().y + ((component != null) ? component.getY() : 0) - map.mouseDown.y, CFG.FIELDSIZE) + map.mouseDown.y);
      
      map.repaint();
    }
    
    @Override
    public void mouseEntered(MouseEvent e)
    {}
    
    @Override
    public void mouseExited(MouseEvent e)
    {}
    
    @Override
    public void mouseMoved(MouseEvent e)
    {
      if (component == null) map.mouse = new Point(e.getX(), e.getY());
      else map.mouse = new Point(e.getX() + component.getX(), e.getY() + component.getY());
      
      map.repaint();
    }
    
    @Override
    public void mousePressed(MouseEvent e)
    {
      if (!dragmode) return;
      
      
      map.mouseDown = null;
      map.mousePos = null;
      map.repaint();
    }
    
    @Override
    public void mouseReleased(MouseEvent e)
    {
      if (!dragmode) return;
      
      if (map.mouseDown != null) handleSelectionBox();
      map.repaint();
    }
    
  }
  
  // -- editFieldData dialog -- //
  public NPCButton             spawnerNPC;
  
  // -- filterReplace dialog -- //
  public JComboBox<String>     FRoldTileset, FRnewTileset;
  public JTextField            FRoldLayer, FRnewLayer, FRoldTX, FRoldTY, FRnewTX, FRnewTY;
  
  // -- NPC dialog -- //
  public JComboBox<String>     NPCsprite, NPCdir, NPCai;
  public JDialog               NPCframe;
  public JTextField            NPCx, NPCy, NPCname;
  public JCheckBox             NPCmove, NPClook, NPChostile;
  public JLabel                NPCpreview;
  public JSpinner              NPCspeed, NPCmoveT, NPClookT;
  public JButton               NPCok;
  public Attributes            NPCattr;
  
  public int                   NPClastID           = 0;
  
  // -- equip dialog -- //
  public JSpinner              EQhair, EQskin, EQeyes;
  public JLabel                EQpreview;
  public Equipment             EQ;
  
  // -- talk dialog -- //
  public JColorSlider          talkColorSlider;
  public JScrollPane           talkScrollPane;
  public JPanel                talkPanel;
  public JButton               talkAdd, talkOk;
  public String[]              tilesets;
  public final int             talkComponentWidth  = 585;
  public final int             talkComponentHeight = 100;
  
  // -- item dialog -- //
  public Item                  tmpItem;
  public Attributes            tmpAttributes;
  public Attributes            tmpRequires;
  
  // -- item dialog actions -- //
  public JPanel                actionSettings;
  
  public JTextField            potionTarget;
  public Attributes            potionAttributes;
  public JComboBox<DamageType> potionDamageType;
  
  public Attributes            weaponAttributes;
  public JComboBox<DamageType> weaponDamageType;
  
  // -- attr dialog -- //
  public Attributes            tmpAttr;
  
  // -- global stuff -- //
  public JFrame                w;
  
  public JMenuBar              menu;
  public JMenu                 mpmenu, mmenu;
  public JMenu                 fmenu;
  public JMenu                 omenu;
  public JMenuItem             mUndo;
  public JCheckBoxMenuItem     mDrag;
  public JSONObject            mappackdata;
  public JSONObject            mapdata;
  public JPanel                tiles, tilepanel;
  public MapPanel              map;
  public JScrollPane           msp;
  public String                tileset;
  public JButton               selectedtile;
  public BufferedImage[][]     autotiles;
  public JDialog               bumpPreview;
  
  public JProgressBar          progress;
  public JLabel                progressLabel;
  
  public BufferedImage         cursor;
  
  // -- modes -- //
  public boolean               gridmode;
  public boolean               rasterview;
  public boolean               deletemode;
  public boolean               autotilemode;
  public boolean               dragmode;
  public boolean               importmode;
  
  public double                cachelayer;
  
  // -- UNDO -- //
  TileButton[]                 lastChangedTiles;
  public boolean               tilesWereDeleted;
  
  // -- Import -- //
  public TileButton[]          importTiles;
  
  public MapEditor(Viewport viewport)
  {
    ToolTipManager.sharedInstance().setInitialDelay(0);
    this.gridmode = true;
    this.deletemode = false;
    this.rasterview = false;
    this.importmode = false;
    new File(FileManager.dir, CFG.MAPEDITORDIR).mkdir();
    new File(FileManager.dir, CFG.MAPEDITOROBJECTSDIR).mkdir();
    w = new JFrame("Liturfaliar Cest MapEditor (" + UniVersion.prettyVersion() + ")");
    w.setSize(Toolkit.getDefaultToolkit().getScreenSize());
    w.setExtendedState(JFrame.MAXIMIZED_BOTH);
    w.setIconImage(Viewport.loadImage("system/editor.png"));
    w.setLocationRelativeTo(null);
    w.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    w.addComponentListener(new ComponentAdapter()
    {
      @Override
      public void componentResized(ComponentEvent e)
      {
        tilepanel.setBounds(0, 0, 240, w.getHeight());
        msp.setBounds(240, 0, w.getWidth() - 255, w.getHeight() - (w.getInsets().top + w.getInsets().bottom) - menu.getHeight());
      }
    });
    w.addWindowStateListener(new WindowStateListener()
    {
      
      @Override
      public void windowStateChanged(WindowEvent e)
      {
        tilepanel.setBounds(0, 0, 240, w.getHeight());
        msp.setBounds(240, 0, w.getWidth() - 255, w.getHeight() - (w.getInsets().top + w.getInsets().bottom) - menu.getHeight());
      }
    });
    w.addWindowListener(new WindowAdapter()
    {
      @Override
      public void windowClosing(WindowEvent e)
      {
        Viewport.w.setVisible(true);
      }
    });
    init();
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
        new NewMapPackDialog(MapEditor.this);
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
        new OpenMapPackDialog(MapEditor.this);
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
        new NewMapDialog(MapEditor.this);
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
        new OpenMapDialog(MapEditor.this);
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
    
    JMenuItem mfree = new JMenuItem(new AbstractAction("Maus freigeben")
    {
      private static final long serialVersionUID = 1L;
      
      @Override
      public void actionPerformed(ActionEvent e)
      {
        selectedtile = null;
        importmode = false;
        importTiles = null;
        cursor = null;
      }
    });
    fmenu.add(mfree);
    
    fmenu.addSeparator();
    
    JMenuItem mexp = new JMenuItem(new AbstractAction("Export")
    {
      private static final long serialVersionUID = 1L;
      
      @Override
      public void actionPerformed(ActionEvent e)
      {
        exportObject();
      }
    });
    mexp.setAccelerator(KeyStroke.getKeyStroke("ctrl E"));
    fmenu.add(mexp);
    
    JMenuItem mimp = new JMenuItem(new AbstractAction("Import...")
    {
      private static final long serialVersionUID = 1L;
      
      @Override
      public void actionPerformed(ActionEvent e)
      {
        new ImportObjectDialog(MapEditor.this);
      }
    });
    mimp.setAccelerator(KeyStroke.getKeyStroke("ctrl R"));
    fmenu.add(mimp);
    
    fmenu.addSeparator();
    
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
        final JDialog dialog = new JDialog(w, true);
        dialog.setTitle("Kartengröße ändern");
        dialog.setSize(200, 100);
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(w);
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
            dialog.dispose();
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
        new NPCDialog(MapEditor.this, null, false);
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
        new FilterReplaceDialog(MapEditor.this);
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
    
    tilepanel = new JPanel();
    tilepanel.setLayout(null);
    tilepanel.setBounds(0, 0, 240, w.getHeight());
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
      public void valueChanged(final ListSelectionEvent e)
      {
        new Thread()
        {
          public void run()
          {
            tileset = (String) ((JList<?>) e.getSource()).getSelectedValue();
            tiles.removeAll();
            Image image = Viewport.loadImage("Tiles/" + tileset + ".png");
            int w = image.getWidth(null) / CFG.FIELDSIZE;
            int h = image.getHeight(null) / CFG.FIELDSIZE;
            tiles.setPreferredSize(new Dimension(w * CFG.FIELDSIZE, h * CFG.FIELDSIZE));
            
            if (Arrays.asList(FileManager.getMediaFiles("Tiles")).contains(tileset)) autotiles = new BufferedImage[w][h];
            
            
            for (int j = 0; j < h; j++)
            {
              for (int i = 0; i < w; i++)
              {
                BufferedImage bi = new BufferedImage(CFG.FIELDSIZE, CFG.FIELDSIZE, BufferedImage.TYPE_INT_ARGB);
                bi.getGraphics().drawImage(image, 0, 0, CFG.FIELDSIZE, CFG.FIELDSIZE, i * CFG.FIELDSIZE, j * CFG.FIELDSIZE, i * CFG.FIELDSIZE + CFG.FIELDSIZE, j * CFG.FIELDSIZE + CFG.FIELDSIZE, null);
                
                if (Arrays.asList(FileManager.getMediaFiles("Tiles")).contains(tileset)) autotiles[i][j] = bi;
                
                final JButton button = new JButton();
                button.setBounds(i * CFG.FIELDSIZE, j * CFG.FIELDSIZE, CFG.FIELDSIZE, CFG.FIELDSIZE);
                button.setBorder(BorderFactory.createEmptyBorder());
                button.setContentAreaFilled(false);
                button.setIcon(new ImageIcon(bi));
                button.addActionListener(new ActionListener()
                {
                  @Override
                  public void actionPerformed(ActionEvent e)
                  {
                    JButton src = (JButton) e.getSource();
                    for (Component c : tiles.getComponents())
                    {
                      if (c.getClass() == JButton.class) ((JButton) c).setBorder(BorderFactory.createEmptyBorder());
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
                    button.setBorder(BorderFactory.createLineBorder(Color.black));
                  }
                  
                  @Override
                  public void mouseExited(MouseEvent e)
                  {
                    button.setBorder(BorderFactory.createEmptyBorder());
                  }
                });
                tiles.add(button);
                tiles.repaint();
                
                tiles.revalidate();
              }
            }
          }
        }.start();
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
    tilesScrollPane.getHorizontalScrollBar().setUnitIncrement(CFG.FIELDSIZE / 2);
    tilesScrollPane.getVerticalScrollBar().setUnitIncrement(CFG.FIELDSIZE / 2);
    tilepanel.add(tilesScrollPane);
    
    map = new MapPanel(this);
    map.setLayout(null);
    map.setOpaque(true);
    map.setBackground(Color.black);
    // map.setPreferredSize(new Dimension(w.getWidth() / 8 * 7, w.getHeight() / 5 * 4 + 132));
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
          if (importmode)
          {
            placeImportObject(e.getPoint());
          }
          else if (selectedtile != null && map.mouseDown == null && map.mousePos == null)
          {
            int round = (gridmode) ? CFG.FIELDSIZE : 1;
            addTile(((ImageIcon) selectedtile.getIcon()).getImage(), Assistant.round(e.getX(), round), Assistant.round(e.getY(), round), tileset, selectedtile.getX() / CFG.FIELDSIZE, selectedtile.getY() / CFG.FIELDSIZE, -1, new JSONObject(), true);
          }
          else if (NPCframe != null)
          {
            updateNPCCoords(e.getX(), e.getY());
          }
        }
      }
    });
    msp = new JScrollPane(map, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    msp.setBounds(240, 0, w.getWidth() - 255, w.getHeight() - (w.getInsets().top + w.getInsets().bottom) - menu.getHeight());
    msp.getHorizontalScrollBar().setUnitIncrement(CFG.FIELDSIZE / 2);
    msp.getVerticalScrollBar().setUnitIncrement(CFG.FIELDSIZE / 2);
    w.add(msp);
    
    w.add(tilepanel);
    
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
          new NPCDialog(MapEditor.this, fNPC, false);
        }
      });
      jpm.add(edit);
      
      JMenuItem eedit = new JMenuItem(new AbstractAction("Ausrüstung bearbeiten")
      {
        private static final long serialVersionUID = 1L;
        
        @Override
        public void actionPerformed(ActionEvent e)
        {
          new EquipmentDialog(MapEditor.this, fNPC);
        }
      });
      jpm.add(eedit);
      
      JMenuItem tedit = new JMenuItem(new AbstractAction("Talk bearbeiten")
      {
        private static final long serialVersionUID = 1L;
        
        @Override
        public void actionPerformed(ActionEvent e)
        {
          new TalkDialog(MapEditor.this, fNPC);
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
          if (e.getButton() == 3) jpm.show(e.getComponent(), e.getX(), e.getY());
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
          if (!(map.getComponent(i) instanceof TileButton)) continue;
          TileButton b = (TileButton) map.getComponent(i);
          if (b.getBounds().intersects(x, y, CFG.FIELDSIZE, CFG.FIELDSIZE)) l = b.getLayer();
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
          
          if (result == null) return;
          
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
            new EditFieldDataDialog(MapEditor.this, tile, s);
          }
        });
        mdata.add(tmp);
      }
      jpm.add(mdata);
      tile.addMouseListener(new MouseAdapter()
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
        {}
        
        @Override
        public void mouseReleased(MouseEvent e)
        {
          if (e.getButton() == 3 && !deletemode && !dragmode) jpm.show(tile, e.getX(), e.getY());
          
          if (e.getButton() == 1)
          {
            JButton src = (JButton) e.getSource();
            if (importmode)
            {
              placeImportObject(new Point(e.getX() + src.getX(), e.getY() + src.getY()));
            }
            else if (selectedtile != null && map.mouseDown == null && map.mousePos == null)
            {
              int round = (gridmode) ? CFG.FIELDSIZE : 1;
              addTile(((ImageIcon) selectedtile.getIcon()).getImage(), Assistant.round(e.getX() + src.getX(), round), Assistant.round(e.getY() + src.getY(), round), tileset, selectedtile.getX() / CFG.FIELDSIZE, selectedtile.getY() / CFG.FIELDSIZE, -1, new JSONObject(), true);
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
      });
      
      if (mapdata == null) return null;
      
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
  
  public void exportObject()
  {
    if (map.mouseDown == null)
    {
      dragmode = true;
      selectedtile = null;
      return;
    }
    
    String name = JOptionPane.showInputDialog("Name:");
    if (name == null || name.length() == 0) return;
    File f = new File(FileManager.dir, CFG.MAPEDITOROBJECTSDIR + "/" + name + ".object");
    if (f.exists())
    {
      int r = JOptionPane.showConfirmDialog(w, "Ein Objekt mit diesem Namen existiert bereits!\nÜberschreiben?", "Warnung!", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
      if (r == JOptionPane.CANCEL_OPTION) return;
    }
    
    ArrayList<TileButton> tiles = new ArrayList<>();
    for (int i = 0; i < map.selW / CFG.FIELDSIZE; i++)
    {
      for (int j = 0; j < map.selH / CFG.FIELDSIZE; j++)
      {
        for (Component c : map.getComponents())
        {
          if (c instanceof TileButton && c.getX() >= i * CFG.FIELDSIZE + map.selX && c.getX() < (i + 1) * CFG.FIELDSIZE + map.selX && c.getY() >= j * CFG.FIELDSIZE + map.selY && c.getY() < (j + 1) * CFG.FIELDSIZE + map.selY)
          {
            tiles.add(((TileButton) c).clone(this));
          }
        }
      }
    }
    
    Collections.sort(tiles, new Comparator<TileButton>()
    {
      
      @Override
      public int compare(TileButton o1, TileButton o2)
      {
        if (o1.getX() == o2.getX())
        {
          return (o1.getY() < o1.getY()) ? -1 : ((o1.getY() == o2.getY()) ? 0 : 1);
        }
        else
        {
          return (o1.getX() < o2.getX()) ? -1 : 1;
        }
      }
    });
    
    JSONArray data = new JSONArray();
    for (TileButton b : tiles)
    {
      b.setX(b.getX() - map.selX);
      b.setY(b.getY() - map.selY);
      data.put(b.getSave());
    }
    
    Compressor.compressFile(f, tiles.toString());
    JOptionPane.showMessageDialog(w, "\"" + name + "\" wurde erfolgreich exportiert!", "Erfolg!", JOptionPane.INFORMATION_MESSAGE);
  }
  
  public BufferedImage getTileImage()
  {
    BufferedImage bi = new BufferedImage(CFG.FIELDSIZE, CFG.FIELDSIZE, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = (Graphics2D) bi.getGraphics();
    g.drawImage(((ImageIcon) selectedtile.getIcon()).getImage(), 0, 0, CFG.FIELDSIZE, CFG.FIELDSIZE, null);
    Assistant.Rect(0, 0, CFG.FIELDSIZE - 1, CFG.FIELDSIZE - 1, Color.white, null, g);
    return bi;
  }
  
  public void handleSelectionBox()
  {
    new Thread()
    {
      public void run()
      {
        int w = map.selW / CFG.FIELDSIZE;
        int h = map.selH / CFG.FIELDSIZE;
        
        if (w + h < 2) return;
        
        ArrayList<TileButton> tiles = new ArrayList<TileButton>();
        
        if (!map.delSelection && selectedtile == null) return;
        
        double layer = 0;
        
        if (!map.delSelection)
        {
          String inputString = JOptionPane.showInputDialog(MapEditor.this.w, "Layer der einzufügenden Felder angeben");
          if (inputString == null) return;
          
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
              
              if (Arrays.asList(FileManager.getMediaFiles("Tiles")).contains(tileset))
              {
                if (i == 0) tx = 0;
                if (j == 0) ty = 1;
                
                if (i > 0 && i < w - 1) tx = 1;
                if (j > 0 && j < w - 1) ty = 2;
                
                if (i == w - 1) tx = 2;
                if (j == h - 1) ty = 3;
                
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
  
  public void importObject(String obj)
  {
    try
    {
      JSONArray array = new JSONArray(Compressor.decompressFile(new File(FileManager.dir, CFG.MAPEDITOROBJECTSDIR + "/" + obj + ".object")));
      int w = array.getJSONObject(array.length() - 1).getInt("x") + CFG.FIELDSIZE;
      int h = array.getJSONObject(array.length() - 1).getInt("y") + CFG.FIELDSIZE;
      selectedtile = null;
      cursor = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
      importTiles = new TileButton[array.length()];
      
      for (int i = 0; i < array.length(); i++)
      {
        JSONObject o = array.getJSONObject(i);
        BufferedImage fieldImage = new BufferedImage(CFG.FIELDSIZE, CFG.FIELDSIZE, BufferedImage.TYPE_INT_ARGB);
        fieldImage.getGraphics().drawImage(Viewport.loadImage("Tiles/" + o.getString("tileset") + ".png"), 0, 0, CFG.FIELDSIZE, CFG.FIELDSIZE, o.getInt("tx") * CFG.FIELDSIZE, o.getInt("ty") * CFG.FIELDSIZE, o.getInt("tx") * CFG.FIELDSIZE + CFG.FIELDSIZE, o.getInt("ty") * CFG.FIELDSIZE + CFG.FIELDSIZE, null);
        cursor.getGraphics().drawImage(fieldImage, o.getInt("x"), o.getInt("y"), null);
        
        importTiles[i] = new TileButton(o.getInt("x"), o.getInt("y"), o.getInt("tx"), o.getInt("ty"), o.getDouble("l"), o.getString("tileset"), fieldImage, this);
      }
      
      dragmode = false;
      importmode = true;
    }
    catch (JSONException e)
    {
      e.printStackTrace();
    }
  }
  
  public void placeImportObject(Point mouse)
  {
    for (TileButton tb : importTiles)
    {
      int round = (gridmode) ? CFG.FIELDSIZE : 1;
      addTile(tb.getImage(), Assistant.round(tb.getX() + mouse.x, round), tb.getY() + mouse.y, tb.getTileset(), tb.getTx(), tb.getTy(), tb.getLayer(), tb.getData(), true);
    }
    
    cursor = null;
    importmode = false;
    importTiles = null;
  }
  
  public boolean openMap(String m)
  {
    try
    {
      map.mouse = null;
      map.mouseDown = null;
      map.mousePos = null;
      map.repaint();
      
      NPClastID = 0;
      w.setTitle("Liturfaliar Cest MapEditor (" + UniVersion.prettyVersion() + ") - " + mappackdata.getString("name") + "/" + m);
      map.removeAll();
      msp.setViewportView(map);
      selectedtile = null;
      mapdata = Compressor.openMap(new File(FileManager.dir, CFG.MAPEDITORDIR + "/" + mappackdata.getString("name") + "/" + m + ".map"));
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
            if (dif < 0) return -1;
            else if (dif > 0) return 1;
            else return 0;
          }
          catch (JSONException e)
          {
            e.printStackTrace();
            return 0;
          }
        }
      });
      int h = 0, w = 0;
      for (int i = 0; i < tiles.size(); i++)
      {
        progress.setValue((int) ((i + 1) / (double) tiles.size() * 100));
        progressLabel.setText((i + 1) + " / " + tiles.size());
        JSONObject o = tiles.get(i);
        BufferedImage bi = new BufferedImage(CFG.FIELDSIZE, CFG.FIELDSIZE, BufferedImage.TYPE_INT_ARGB);
        bi.getGraphics().drawImage(Viewport.loadImage("Tiles/" + o.getString("tileset") + ".png"), 0, 0, CFG.FIELDSIZE, CFG.FIELDSIZE, o.getInt("tx") * CFG.FIELDSIZE, o.getInt("ty") * CFG.FIELDSIZE, o.getInt("tx") * CFG.FIELDSIZE + CFG.FIELDSIZE, o.getInt("ty") * CFG.FIELDSIZE + CFG.FIELDSIZE, null);
        
        if (o.getInt("x") > w) w = o.getInt("x");
        if (o.getInt("y") > h) h = o.getInt("y");
        
        addTile(bi, o.getInt("x"), o.getInt("y"), o.getString("tileset"), o.getInt("tx"), o.getInt("ty"), o.getDouble("l"), o.getJSONObject("data"), false);
      }
      
      JSONArray npcs = mapdata.getJSONArray("npc");
      for (int i = 0; i < npcs.length(); i++)
      {
        addNPC(npcs.getJSONObject(i));
      }
      map.setPreferredSize(new Dimension(w + CFG.FIELDSIZE, h + CFG.FIELDSIZE));
      
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
  
  public void openMapPack(String pack)
  {
    try
    {
      mappackdata = new JSONObject(Assistant.getFileContent(new File(FileManager.dir, CFG.MAPEDITORDIR + "/" + pack + "/.pack")));
      w.setTitle("Liturfaliar Cest MapEditor (" + UniVersion.prettyVersion() + ") - " + mappackdata.getString("name"));
      mmenu.setEnabled(true);
    }
    catch (Exception e1)
    {
      JOptionPane.showMessageDialog(w, "Kartenpaket konnte nicht geöffnet werden. Beachte, dass du den Ordner auswählen musst,\n" + "der sowohl die Datei \"pack.json\" als auch den Ordner \"maps\" enthält.", "Fehler!", JOptionPane.ERROR_MESSAGE);
    }
  }
  
  public void saveMap()
  {
    try
    {
      File f = new File(FileManager.dir, CFG.MAPEDITORDIR + "/" + mappackdata.getString("name") + "/" + mapdata.getString("name") + ".map");
      
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
  
  public void saveMapPack()
  {
    try
    {
      File dir = new File(FileManager.dir, CFG.MAPEDITORDIR + "/" + mappackdata.getString("name"));
      dir.mkdir();
      // new File(dir, "maps").mkdir();
      File pack = new File(dir, ".pack");
      if (!pack.exists()) pack.createNewFile();
      Assistant.setFileContent(pack, mappackdata.toString());
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  
  public void showCustomCursor(boolean show)
  {
    if (!show && (selectedtile != null || NPCframe != null))
    {
      cursor = null;
      map.repaint();
      return;
    }
    
    if (selectedtile != null) cursor = getTileImage();
    
    if (NPCframe != null) cursor = (BufferedImage) ((ImageIcon) NPCpreview.getIcon()).getImage();
    
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
  
  private void updateNPCCoords(int x, int y)
  {
    NPCx.setText(x + "");
    NPCy.setText(y + "");
  }
}
