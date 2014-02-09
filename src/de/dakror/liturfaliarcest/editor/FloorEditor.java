package de.dakror.liturfaliarcest.editor;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileNameExtensionFilter;

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
	
	int width, height;
	
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
		cp.setDividerLocation(200);
		JPanel tilesets = new JPanel(new WrapLayout(WrapLayout.LEFT));
		
		for (String tileset : CFG.AUTOTILES)
			tilesets.add(new Autotile(0, 0, tileset, false));
		
		tilesets.setSize(200, 600);
		
		JScrollPane jsp = new JScrollPane(tilesets, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		cp.add(jsp);
		
		map = new JPanel(null);
		KeyStroke keyStroke = KeyStroke.getKeyStroke("A");
		map.getActionMap().put("a-add", new AbstractAction("a-add")
		{
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent e)
			{
				for (Component c : map.getComponents())
					c.setBounds(c.getX() + 32, c.getY(), 32, 32);
				
				for (int i = 0; i < height; i++)
					map.add(new Autotile(0, i, "", true));
				
				width++;
				
				map.setPreferredSize(new Dimension(width * 32, height * 32));
				map.setSize(new Dimension(width * 32, height * 32));
			}
		});
		map.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyStroke, "a-add");
		keyStroke = KeyStroke.getKeyStroke("W");
		map.getActionMap().put("w-add", new AbstractAction("w-add")
		{
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent e)
			{
				for (Component c : map.getComponents())
					c.setBounds(c.getX(), c.getY() + 32, 32, 32);
				
				for (int i = 0; i < width; i++)
					map.add(new Autotile(i, 0, "", true));
				
				height++;
				
				map.setPreferredSize(new Dimension(width * 32, height * 32));
				map.setSize(new Dimension(width * 32, height * 32));
			}
		});
		map.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyStroke, "w-add");
		keyStroke = KeyStroke.getKeyStroke("S");
		map.getActionMap().put("s-add", new AbstractAction("s-add")
		{
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent e)
			{
				for (int i = 0; i < width; i++)
					map.add(new Autotile(i, height, "", true));
				
				height++;
				
				map.setPreferredSize(new Dimension(width * 32, height * 32));
				map.setSize(new Dimension(width * 32, height * 32));
			}
		});
		map.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyStroke, "s-add");
		keyStroke = KeyStroke.getKeyStroke("D");
		map.getActionMap().put("d-add", new AbstractAction("d-add")
		{
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent e)
			{
				for (int i = 0; i < height; i++)
					map.add(new Autotile(width, i, "", true));
				
				width++;
				
				map.setPreferredSize(new Dimension(width * 32, height * 32));
				map.setSize(new Dimension(width * 32, height * 32));
			}
		});
		map.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyStroke, "d-add");
		keyStroke = KeyStroke.getKeyStroke("control S");
		map.getActionMap().put("save", new AbstractAction("save")
		{
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent e)
			{
				JFileChooser jfc = new JFileChooser(new File(System.getProperty("user.dir")));
				jfc.setMultiSelectionEnabled(false);
				jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				jfc.setFileFilter(new FileNameExtensionFilter("PNG-Bild (*.png)", "png"));
				if (jfc.showSaveDialog(FloorEditor.this) == JFileChooser.APPROVE_OPTION)
				{
					File f = jfc.getSelectedFile();
					if (!f.getName().endsWith(".png")) f = new File(f.getPath() + ".png");
					BufferedImage bi = new BufferedImage(width * 32, height * 32, BufferedImage.TYPE_INT_ARGB);
					Graphics2D g = (Graphics2D) bi.getGraphics();
					for (Component c : map.getComponents())
						if (c instanceof Autotile) g.drawImage(((ImageIcon) ((Autotile) c).getIcon()).getImage(), c.getX(), c.getY(), null);
					
					try
					{
						ImageIO.write(bi, "PNG", f);
					}
					catch (IOException e1)
					{
						e1.printStackTrace();
					}
				}
			}
		});
		map.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyStroke, "save");
		
		width = 10;
		height = 10;
		for (int i = 0; i < width; i++)
		{
			for (int j = 0; j < height; j++)
			{
				map.add(new Autotile(i, j, "", true));
			}
		}
		JScrollPane jsp2 = new JScrollPane(map, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		jsp2.setSize(new Dimension(600, 600));
		cp.add(jsp2);
		
		setContentPane(cp);
	}
}
