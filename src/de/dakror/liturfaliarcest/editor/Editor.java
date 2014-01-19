package de.dakror.liturfaliarcest.editor;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import de.dakror.liturfaliarcest.game.Game;
import de.dakror.liturfaliarcest.settings.CFG;

/**
 * @author Dakror
 */
public class Editor extends JFrame
{
	private static final long serialVersionUID = 1L;
	
	MapPanel mapPanel;
	
	public Editor()
	{
		super("Liturfaliar Cest Editor");
		setSize(800, 600);
		setLocationRelativeTo(null);
		setResizable(false);
		setAlwaysOnTop(true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosed(WindowEvent e)
			{
				Game.editor = null;
			}
		});
		
		initComponents();
		
		setVisible(true);
	}
	
	public void initComponents()
	{
		
		
		
		DefaultListModel<String> dlm = new DefaultListModel<>();
		for (String t : CFG.TILES)
			dlm.addElement(t);
		JList<String> tiles = new JList<>(dlm);
		
		JPanel cp = new JPanel(null);
		
		JPanel left = new JPanel();
		tiles.setPreferredSize(new Dimension(230, 400));
		left.add(new JScrollPane(tiles));
		left.setBounds(-1, -5, 250, 600);
		
		cp.add(left);
		// mapPanel = new MapPanel();
		// cp.add(mapPanel);
		
		setContentPane(cp);
	}
}
