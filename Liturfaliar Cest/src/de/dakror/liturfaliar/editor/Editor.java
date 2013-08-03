package de.dakror.liturfaliar.editor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import de.dakror.liturfaliar.Viewport;
import de.dakror.universion.UniVersion;

public class Editor extends JFrame
{
  private static final long      serialVersionUID = 1L;
  
  public static final FileFilter FILE_FILTER_PACK = new FileNameExtensionFilter("Liturfaliar Cest Kartenpaket (*.pack)", "pack");
  public static final FileFilter FILE_FILTER_MAP  = new FileNameExtensionFilter("Liturfaliar Cest Karte (*.map)", "map");
  
  Viewport                       v;
  
  JTree                          tree;
  JScrollPane                    treePanel;
  
  public Editor(final Viewport v)
  {
    super("Liturfaliar Cest MapEditor (" + UniVersion.prettyVersion() + ")");
    this.v = v;
    ToolTipManager.sharedInstance().setInitialDelay(0);
    setSize(Toolkit.getDefaultToolkit().getScreenSize());
    setExtendedState(JFrame.MAXIMIZED_BOTH);
    setIconImage(Viewport.loadImage("system/logo.png"));
    setLocationRelativeTo(null);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    addWindowListener(new WindowAdapter()
    {
      @Override
      public void windowClosing(WindowEvent e)
      {
        v.w.setVisible(true);
        v.editor = null;
      }
    });
    
    v.w.setVisible(false);
    toFront();
    
    initCompontents();
    
    setVisible(true);
  }
  
  // -- publics -- //
  
  public Icon getIcon(String name)
  {
    return new ImageIcon(Viewport.loadScaledImage("icon/eclipse/" + name + ".png", 20, 20));
  }
  
  public JButton createToolBarButton(String tooltip, String icon, Action action)
  {
    JButton button = new JButton();
    button.setPreferredSize(new Dimension(24, 24));
    button.setIcon(getIcon(icon));
    action.putValue(Action.SMALL_ICON, getIcon(icon));
    action.putValue(Action.SHORT_DESCRIPTION, tooltip);
    button.setAction(action);
    button.setFocusPainted(false);
    
    return button;
  }
  
  // -- privates -- //
  
  void initCompontents()
  {
    JPanel contentPanel = new JPanel(new BorderLayout());
    // -- toolbar -- //
    JToolBar toolBar = new JToolBar();
    toolBar.setFloatable(false);
    toolBar.setRollover(true);
    toolBar.add(createToolBarButton("Neues Kartenpaket", "newprj_wiz", new AbstractAction()
    {
      private static final long serialVersionUID = 1L;
      
      @Override
      public void actionPerformed(ActionEvent e)
      {
        newMapPack();
      }
    }));
    toolBar.add(createToolBarButton("Kartenpaket laden", "prj_obj", new AbstractAction()
    {
      private static final long serialVersionUID = 1L;
      
      @Override
      public void actionPerformed(ActionEvent e)
      {
        openMapPack();
      }
    }));
    toolBar.add(createToolBarButton("Kartenpaket-Info bearbeiten", "editor", new AbstractAction()
    {
      private static final long serialVersionUID = 1L;
      
      @Override
      public void actionPerformed(ActionEvent e)
      {
        editMapPackInfo();
      }
    }));
    toolBar.addSeparator();
    toolBar.add(createToolBarButton("Neue Karte", "new_con", new AbstractAction()
    {
      private static final long serialVersionUID = 1L;
      
      @Override
      public void actionPerformed(ActionEvent e)
      {
        newMap();
      }
    }));
    toolBar.add(createToolBarButton("Karte laden", "fldr_obj", new AbstractAction()
    {
      private static final long serialVersionUID = 1L;
      
      @Override
      public void actionPerformed(ActionEvent e)
      {
        openMap();
      }
    }));
    toolBar.add(createToolBarButton("Karte speichern", "save_edit", new AbstractAction()
    {
      private static final long serialVersionUID = 1L;
      
      @Override
      public void actionPerformed(ActionEvent e)
      {
        saveMap();
      }
    }));
    
    contentPanel.add(toolBar, BorderLayout.PAGE_START);
    
    // -- selection field -- //
    
    // -- map view -- //
    
    
    setContentPane(contentPanel);
  }
  
  // -- dialogs -- //
  
  void newMapPack()
  {}
  
  void openMapPack()
  {}
  
  void editMapPackInfo()
  {}
  
  void newMap()
  {}
  
  void openMap()
  {}
  
  void saveMap()
  {}
  
  // -- methods -- //
  
  void refresh()
  { 
    
  }
}
