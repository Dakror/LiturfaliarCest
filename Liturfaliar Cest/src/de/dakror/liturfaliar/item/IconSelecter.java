package de.dakror.liturfaliar.item;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;

import de.dakror.liturfaliar.Viewport;

public class IconSelecter
{
  /**
   * Small GUI program to choose an icon from the massive icons.png file
   * 
   * @param args
   */
  public static void main(String[] args)
  {
    ToolTipManager.sharedInstance().setInitialDelay(0);
    try
    {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    }
    catch (Exception e1)
    {
      e1.printStackTrace();
    }
    
    JFrame frame = new JFrame("Icon Selecter GUI");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setResizable(false);
    
    BufferedImage image = (BufferedImage) Viewport.loadImage("system/icons.png");
    
    int size = 48;
    JPanel panel = new JPanel(new GridLayout(image.getHeight() / 24, image.getWidth() / 24));
    
    panel.setPreferredSize(new Dimension((image.getWidth() / 24) * size, (image.getHeight() / 24) * size));
    
    
    for (int i = 0; i < image.getHeight() / 24; i++)
    {
      for (int j = 0; j < image.getWidth() / 24; j++)
      {
        final JLabel l = new JLabel();
        l.setIcon(new ImageIcon(image.getSubimage(j * 24, i * 24, 24, 24).getScaledInstance(size, size, BufferedImage.SCALE_AREA_AVERAGING)));
        l.setBorder(BorderFactory.createLineBorder(Color.black));
        l.setName(j + ", " + i);
        l.setToolTipText(l.getName());
        l.addMouseListener(new MouseAdapter()
        {
          @Override
          public void mousePressed(MouseEvent e)
          {
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(l.getName()), null);
          }
        });
        
        panel.add(l);
        
      }
    }
    
    JScrollPane pane = new JScrollPane(panel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    pane.setPreferredSize(new Dimension((image.getWidth() / 24) * size + 30, 900));
    pane.getVerticalScrollBar().setUnitIncrement(size);
    frame.setContentPane(pane);
    frame.pack();
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
  }
  
}
