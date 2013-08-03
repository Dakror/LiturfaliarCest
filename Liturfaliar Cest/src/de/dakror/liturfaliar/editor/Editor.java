package de.dakror.liturfaliar.editor;

import java.awt.Dimension;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;

import de.dakror.liturfaliar.Viewport;

public class Editor
{
  Viewport v;
  
  public Editor(Viewport v)
  {
    this.v = v;
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
    
  }
}
