package de.dakror.liturfaliarcest.editor;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import de.dakror.gamesetup.util.swing.WrapLayout;
import de.dakror.liturfaliarcest.game.Game;
import de.dakror.liturfaliarcest.settings.CFG;

/**
 * @author Dakror
 */
public class FloorEditor extends JFrame
{
	private static final long serialVersionUID = 1L;
	
	public static FloorEditor currentFloorEditor;
	
	JPanel map;
	public String selectedTile;
	
	public FloorEditor()
	{
		super("Liturfaliar Cest Editor: Boden");
		
		currentFloorEditor = this;
		
		setSize(800, 600);
		setLocationRelativeTo(Editor.currentEditor);
		setResizable(false);
		setIconImage(Game.getImage("system/editor.png"));
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		initComponents();
		
		setVisible(true);
	}
	
	public void initComponents()
	{
		JSplitPane cp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		cp.setEnabled(false);
		JPanel tilesets = new JPanel(new WrapLayout(WrapLayout.LEFT));
		
		for (String tileset : CFG.AUTOTILES)
			tilesets.add(new Autotile(0, 0, tileset, false));
		
		tilesets.setSize(200, 600);
		
		JScrollPane jsp = new JScrollPane(tilesets, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		jsp.setPreferredSize(new Dimension(200, 600));
		cp.add(jsp);
		
		map = new JPanel(null);
		tilesets.setSize(600, 600);
		JScrollPane jsp2 = new JScrollPane(map, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		cp.add(jsp2);
		
		setContentPane(cp);
	}
}
