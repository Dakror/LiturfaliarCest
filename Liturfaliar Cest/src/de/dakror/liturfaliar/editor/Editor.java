package de.dakror.liturfaliar.editor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.ToolTipManager;

import de.dakror.liturfaliar.Viewport;
import de.dakror.universion.UniVersion;

public class Editor extends JFrame
{
  private static final long serialVersionUID = 1L;
  
  Viewport                  v;
  
  public Editor(final Viewport v)
  {
    super("Liturfaliar Cest MapEditor (" + UniVersion.prettyVersion() + ")");
    this.v = v;
    ToolTipManager.sharedInstance().setInitialDelay(0);
    setSize(Toolkit.getDefaultToolkit().getScreenSize());
    setExtendedState(JFrame.MAXIMIZED_BOTH);
    setIconImage(Viewport.loadImage("system/logo.png"));
    setLocationRelativeTo(null);
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
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
    return new ImageIcon(Viewport.loadScaledImage("icons/eclipse/" + name + ".png", 20, 20));
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
    
    contentPanel.add(toolBar, BorderLayout.PAGE_START);
    
    // -- selection field -- //
    
    // -- map view -- //
    
    
    setContentPane(contentPanel);
  }
  
  void newMapPack()
  { 
    
  }
  
  void openMapPack()
  { 
    
  }
  
  void saveMapPack()
  { 
    
  }
}
