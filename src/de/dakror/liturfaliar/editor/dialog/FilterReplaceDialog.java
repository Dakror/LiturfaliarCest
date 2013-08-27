package de.dakror.liturfaliar.editor.dialog;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import layout.SpringUtilities;
import de.dakror.liturfaliar.editor.MapEditor;
import de.dakror.liturfaliar.editor.TileButton;
import de.dakror.liturfaliar.settings.CFG;
import de.dakror.liturfaliar.util.Assistant;

public class FilterReplaceDialog
{
	public FilterReplaceDialog(final MapEditor me)
	{
		final JDialog FRframe = new JDialog(me.w, "Felder per Filter ersetzen");
		FRframe.setResizable(false);
		FRframe.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		
		me.dragmode = true;
		me.selectedtile = null;
		
		FRframe.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosed(WindowEvent e)
			{
				for (Component c : me.map.getComponents())
				{
					if (c instanceof TileButton)
					{
						((TileButton) c).fitsFilter = false;
						((TileButton) c).update = true;
						((TileButton) c).repaint();
					}
				}
				me.dragmode = me.mDrag.isSelected();
				
				me.map.mouseDown = null;
				me.map.mousePos = null;
			}
		});
		
		JPanel p = new JPanel(new SpringLayout());
		
		JPanel oldPanel = new JPanel(new SpringLayout());
		oldPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.gray), "Finden"));
		
		JLabel label = new JLabel("Tileset:");
		oldPanel.add(label);
		me.FRoldTileset = new JComboBox<String>(Assistant.concat(new String[] { "Ignorieren" }, me.tilesets));
		me.FRoldTileset.setSelectedIndex(0);
		oldPanel.add(me.FRoldTileset);
		
		label = new JLabel("Layer:");
		oldPanel.add(label);
		me.FRoldLayer = new JTextField();
		oldPanel.add(me.FRoldLayer);
		
		label = new JLabel("Tileset-X:");
		oldPanel.add(label);
		me.FRoldTX = new JTextField();
		oldPanel.add(me.FRoldTX);
		
		label = new JLabel("Tileset-Y:");
		oldPanel.add(label);
		me.FRoldTY = new JTextField();
		oldPanel.add(me.FRoldTY);
		
		SpringUtilities.makeCompactGrid(oldPanel, 4, 2, 6, 6, 6, 6);
		
		p.add(oldPanel);
		
		JPanel newPanel = new JPanel(new SpringLayout());
		newPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.gray), "Ersetzen"));
		
		label = new JLabel("Tileset:");
		newPanel.add(label);
		me.FRnewTileset = new JComboBox<String>(Assistant.concat(new String[] { "Ignorieren" }, me.tilesets));
		me.FRnewTileset.setSelectedIndex(0);
		newPanel.add(me.FRnewTileset);
		
		label = new JLabel("Layer:");
		newPanel.add(label);
		me.FRnewLayer = new JTextField();
		newPanel.add(me.FRnewLayer);
		
		label = new JLabel("Tileset-X:");
		newPanel.add(label);
		me.FRnewTX = new JTextField();
		newPanel.add(me.FRnewTX);
		
		label = new JLabel("Tileset-Y:");
		newPanel.add(label);
		me.FRnewTY = new JTextField();
		newPanel.add(me.FRnewTY);
		
		SpringUtilities.makeCompactGrid(newPanel, 4, 2, 6, 6, 6, 6);
		
		p.add(newPanel);
		
		JButton find = new JButton("Finden");
		find.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				new Thread()
				{
					public void run()
					{
						Double layer = Assistant.parseDouble(me.FRoldLayer.getText());
						Integer tx = Assistant.parseInt(me.FRoldTX.getText());
						Integer ty = Assistant.parseInt(me.FRoldTY.getText());
						
						for (int i = 0; i < me.map.getComponentCount(); i++)
						{
							Component c = me.map.getComponent(i);
							if (c instanceof TileButton)
							{
								((TileButton) c).fitsFilter = false;
								((TileButton) c).update = true;
							}
						}
						me.map.repaint();
						
						if (me.map.mouseDown == null)
						{
							for (Component c : me.map.getComponents())
							{
								if (c instanceof TileButton)
								{
									((TileButton) c).checkReplaceFilterFits(me.FRoldTileset.getSelectedItem().toString(), layer, tx, ty);
									me.map.repaint();
								}
							}
						}
						else
						{
							for (int i = 0; i < me.map.selW / CFG.FIELDSIZE; i++)
							{
								for (int j = 0; j < me.map.selH / CFG.FIELDSIZE; j++)
								{
									for (Component c : me.map.getComponents())
									{
										if (c instanceof TileButton && c.getX() >= i * CFG.FIELDSIZE + me.map.selX && c.getX() < (i + 1) * CFG.FIELDSIZE + me.map.selX && c.getY() >= j * CFG.FIELDSIZE + me.map.selY && c.getY() < (j + 1) * CFG.FIELDSIZE + me.map.selY)
										{
											((TileButton) c).checkReplaceFilterFits(me.FRoldTileset.getSelectedItem().toString(), layer, tx, ty);
											me.map.repaint();
										}
									}
								}
							}
						}
						me.map.repaint();
					}
				}.start();
			}
		});
		
		p.add(find);
		
		JButton replace = new JButton("Ersetzen");
		replace.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				Double layer = Assistant.parseDouble(me.FRnewLayer.getText());
				Integer tx = Assistant.parseInt(me.FRnewTX.getText());
				Integer ty = Assistant.parseInt(me.FRnewTY.getText());
				
				for (Component c : me.map.getComponents())
				{
					if (c instanceof TileButton)
					{
						((TileButton) c).execFilterReplace(me.FRnewTileset.getSelectedItem().toString(), layer, tx, ty);
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
				for (Component c : me.map.getComponents())
				{
					if (c instanceof TileButton && ((TileButton) c).fitsFilter)
					{
						me.map.remove(c);
						me.map.repaint();
					}
				}
			}
		}));
		
		SpringUtilities.makeCompactGrid(p, 3, 2, 6, 6, 6, 6);
		
		FRframe.setContentPane(p);
		FRframe.pack();
		FRframe.setLocationRelativeTo(me.w);
		FRframe.setVisible(true);
	}
}
