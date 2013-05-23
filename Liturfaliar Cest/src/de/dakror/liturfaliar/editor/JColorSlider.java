package de.dakror.liturfaliar.editor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class JColorSlider extends JPanel
{
  private static final long serialVersionUID = 1L;
  
  private JLabel            preview;
  private JTextField        hex;
  private JSlider           red, green, blue;
  
  public JColorSlider()
  {
    FlowLayout layout = new FlowLayout();
    layout.setAlignment(FlowLayout.LEFT);
    layout.setAlignOnBaseline(true);
    setLayout(layout);
    
    JLabel label = new JLabel("Rot: ");
    add(label);
    red = new JSlider(0, 255, 255);
    red.setPreferredSize(new Dimension(49, 140));
    red.setOrientation(JSlider.VERTICAL);
    red.setMajorTickSpacing(255);
    red.setPaintLabels(true);
    red.setPaintTicks(true);
    red.addChangeListener(new ChangeListener()
    {
      @Override
      public void stateChanged(ChangeEvent e)
      {
        update();
      }
    });
    label.setLabelFor(red);
    add(red);
    label = new JLabel("Grün: ");
    add(label);
    green = new JSlider(0, 255, 255);
    green.setOrientation(JSlider.VERTICAL);
    green.setPreferredSize(new Dimension(49, 140));
    green.setMajorTickSpacing(255);
    green.setPaintLabels(true);
    green.setPaintTicks(true);
    green.addChangeListener(new ChangeListener()
    {
      @Override
      public void stateChanged(ChangeEvent e)
      {
        update();
      }
    });
    label.setLabelFor(green);
    add(green);
    
    label = new JLabel("Blau: ");
    add(label);
    blue = new JSlider(0, 255, 255);
    blue.setOrientation(JSlider.VERTICAL);
    blue.setPreferredSize(new Dimension(49, 140));
    blue.setMajorTickSpacing(255);
    blue.setPaintLabels(true);
    blue.setPaintTicks(true);
    blue.addChangeListener(new ChangeListener()
    {
      @Override
      public void stateChanged(ChangeEvent e)
      {
        update();
      }
    });
    label.setLabelFor(blue);
    add(blue);
    
    label = new JLabel("Hex: ");
    add(label);
    hex = new JTextField();
    hex.setPreferredSize(new Dimension(60, 20));
    hex.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent e)
      {
        Integer r = Integer.parseInt(hex.getText().substring(1, 3), 16);
        Integer g = Integer.parseInt(hex.getText().substring(3, 5), 16);
        Integer b = Integer.parseInt(hex.getText().substring(5, 7), 16);
        red.setValue(r);
        green.setValue(g);
        blue.setValue(b);
        
        update();
      }
    });
    label.setLabelFor(hex);
    add(hex);
    
    label = new JLabel("Vorschau: ");
    add(label);
    preview = new JLabel(" ");
    preview.setPreferredSize(new Dimension(100, 20));
    preview.setBorder(BorderFactory.createLineBorder(Color.black));
    preview.setOpaque(true);
    label.setLabelFor(preview);
    add(preview);
    
    update();
  }
  
  public void update()
  {
    Color color = new Color(red.getValue(), green.getValue(), blue.getValue());
    hex.setText("#" + Integer.toHexString(color.getRGB()).substring(2, 8));
    preview.setBackground(Color.decode(hex.getText()));
  }
  
  public String getHex()
  {
    return hex.getText();
  }
}
