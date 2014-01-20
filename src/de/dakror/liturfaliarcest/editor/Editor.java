package de.dakror.liturfaliarcest.editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.SpringLayout;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.dakror.gamesetup.util.Helper;
import de.dakror.gamesetup.util.swing.SpringUtilities;
import de.dakror.liturfaliarcest.game.Game;
import de.dakror.liturfaliarcest.settings.CFG;

/**
 * @author Dakror
 */
public class Editor extends JFrame
{
	private static final long serialVersionUID = 1L;
	
	MapPanel mapPanel;
	JSONArray entities;
	Point lt = new Point(-1, -1), rb = new Point(-1, -1);
	
	boolean devMode;
	
	File entlist;
	
	public Editor()
	{
		super("Liturfaliar Cest Editor");
		setSize(1280, 720);
		setLocationRelativeTo(null);
		setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		devMode = new File(System.getProperty("user.dir"), "src").exists();
		
		try
		{
			if (devMode)
			{
				entlist = new File(System.getProperty("user.dir"), "src/entities.entlist");
				entities = new JSONArray(Helper.getFileContent(entlist));
			}
			else entities = new JSONArray(Helper.getURLContent(getClass().getResource("/entities.entlist")));
		}
		catch (JSONException e1)
		{
			e1.printStackTrace();
		}
		
		initComponents();
		
		setVisible(true);
	}
	
	public void initComponents()
	{
		JTabbedPane cp = new JTabbedPane();
		
		JSplitPane p1 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		p1.setEnabled(false);
		
		JPanel left = new JPanel(new BorderLayout());
		final JPanel tiles = new JPanel();
		tiles.setLayout(null);
		final JList<String> tilesets = new JList<>(CFG.TILES);
		tilesets.addListSelectionListener(new ListSelectionListener()
		{
			@Override
			public void valueChanged(ListSelectionEvent e)
			{
				new Thread()
				{
					@Override
					public void run()
					{
						tiles.removeAll();
						
						BufferedImage bi = Game.getImage("tiles/" + tilesets.getSelectedValue());
						tiles.setPreferredSize(new Dimension(bi.getWidth(), bi.getHeight()));
						for (int i = 0; i < bi.getHeight() / 32; i++)
						{
							for (int j = 0; j < bi.getWidth() / 32; j++)
							{
								final JLabel l = new JLabel(new ImageIcon(bi.getSubimage(j * 32, i * 32, 32, 32)));
								l.setBounds(j * 32, i * 32, 32, 32);
								l.addMouseListener(new MouseAdapter()
								{
									@Override
									public void mouseEntered(MouseEvent e)
									{
										if (l.getBorder() == null || !((LineBorder) l.getBorder()).getLineColor().equals(Color.red)) l.setBorder(BorderFactory.createLineBorder(Color.black));
									}
									
									@Override
									public void mouseExited(MouseEvent e)
									{
										if (l.getBorder() == null || !((LineBorder) l.getBorder()).getLineColor().equals(Color.red)) l.setBorder(null);
									}
									
									@Override
									public void mousePressed(MouseEvent e)
									{
										if (!((LineBorder) l.getBorder()).getLineColor().equals(Color.red)) l.setBorder(BorderFactory.createLineBorder(Color.red));
										else l.setBorder(BorderFactory.createLineBorder(Color.black));
									}
								});
								tiles.add(l);
							}
						}
						tiles.revalidate();
						tiles.repaint();
						((JScrollPane) tiles.getParent().getParent()).getVerticalScrollBar().setValue(0);
						((JScrollPane) tiles.getParent().getParent()).getHorizontalScrollBar().setValue(0);
					}
				}.start();
			}
		});
		JScrollPane wrap = new JScrollPane(tilesets);
		wrap.setPreferredSize(new Dimension(400, 150));
		left.add(wrap, BorderLayout.PAGE_START);
		wrap = new JScrollPane(tiles);
		wrap.getVerticalScrollBar().setUnitIncrement(32);
		wrap.setPreferredSize(new Dimension(200, 512));
		left.add(wrap, BorderLayout.PAGE_END);
		p1.add(left);
		
		JPanel right = new JPanel(null);
		final JLabel preview = new JLabel();
		right.add(preview);
		
		final JSpinner bumpX = new JSpinner();
		bumpX.setPreferredSize(new Dimension(100, bumpX.getPreferredSize().height));
		final JSpinner bumpY = new JSpinner();
		final JSpinner bumpWidth = new JSpinner();
		final JSpinner bumpHeight = new JSpinner();
		final JPanel settings = new JPanel(new SpringLayout());
		final AbstractAction update = new AbstractAction()
		{
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent e)
			{
				lt = new Point(-1, -1);
				rb = new Point(-1, -1);
				ArrayList<JLabel> sel = new ArrayList<>();
				for (Component c : tiles.getComponents())
				{
					if (!(c instanceof JLabel) || ((JLabel) c).getBorder() == null || !((LineBorder) ((JLabel) c).getBorder()).getLineColor().equals(Color.red)) continue;
					if (lt.x == -1 || c.getX() < lt.x) lt.x = c.getX();
					if (lt.y == -1 || c.getY() < lt.y) lt.y = c.getY();
					
					if (rb.x == -1 || c.getX() > rb.x) rb.x = c.getX();
					if (rb.y == -1 || c.getY() > rb.y) rb.y = c.getY();
					sel.add((JLabel) c);
				}
				
				BufferedImage bi = new BufferedImage(rb.x - lt.x + 32, rb.y - lt.y + 32, BufferedImage.TYPE_INT_ARGB);
				Graphics2D g = (Graphics2D) bi.getGraphics();
				for (JLabel l : sel)
					g.drawImage(((ImageIcon) l.getIcon()).getImage(), l.getX() - lt.x, l.getY() - lt.y, null);
				
				g.setColor(Color.red);
				g.drawRect((int) bumpX.getValue(), (int) bumpY.getValue(), (int) bumpWidth.getValue(), (int) bumpHeight.getValue());
				
				preview.setIcon(new ImageIcon(bi));
				preview.setBounds((850 - bi.getWidth()) / 2, 50, bi.getWidth(), bi.getHeight());
				
				bumpX.setModel(new SpinnerNumberModel((int) bumpX.getValue() > bi.getWidth() - 1 ? bi.getWidth() - 1 : (int) bumpX.getValue(), 0, bi.getWidth() - 1, 1));
				bumpY.setModel(new SpinnerNumberModel((int) bumpY.getValue() > bi.getHeight() - 1 ? bi.getHeight() - 1 : (int) bumpY.getValue(), 0, bi.getHeight() - 1, 1));
				bumpWidth.setModel(new SpinnerNumberModel((int) bumpWidth.getValue() > bi.getWidth() || (int) bumpWidth.getValue() == 0 ? bi.getWidth() : (int) bumpWidth.getValue(), 1, bi.getWidth(), 1));
				bumpHeight.setModel(new SpinnerNumberModel((int) bumpHeight.getValue() > bi.getHeight() || (int) bumpHeight.getValue() == 0 ? bi.getHeight() : (int) bumpHeight.getValue(), 1, bi.getHeight(), 1));
				
				int w = settings.getPreferredSize().width;
				int h = settings.getPreferredSize().height;
				settings.setBounds((850 - w) / 2, 60 + bi.getHeight(), w, h);
			}
		};
		
		settings.add(new JButton(new AbstractAction("Aktualisieren")
		{
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent e)
			{
				update.actionPerformed(null);
			}
		}));
		settings.add(new JLabel());
		
		settings.add(new JLabel("BumpX:"));
		bumpX.addChangeListener(new ChangeListener()
		{
			@Override
			public void stateChanged(ChangeEvent e)
			{
				update.actionPerformed(null);
			}
		});
		settings.add(bumpX);
		settings.add(new JLabel("BumpY:"));
		bumpY.addChangeListener(new ChangeListener()
		{
			@Override
			public void stateChanged(ChangeEvent e)
			{
				update.actionPerformed(null);
			}
		});
		settings.add(bumpY);
		settings.add(new JLabel("BumpWidth:"));
		bumpWidth.addChangeListener(new ChangeListener()
		{
			@Override
			public void stateChanged(ChangeEvent e)
			{
				update.actionPerformed(null);
			}
		});
		settings.add(bumpWidth);
		settings.add(new JLabel("BumpHeight:"));
		bumpHeight.addChangeListener(new ChangeListener()
		{
			@Override
			public void stateChanged(ChangeEvent e)
			{
				update.actionPerformed(null);
			}
		});
		settings.add(bumpHeight);
		settings.add(new JButton(new AbstractAction("Speichern")
		{
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					JSONObject o = new JSONObject();
					o.put("t", tilesets.getSelectedValue());
					o.put("x", lt.x);
					o.put("y", lt.y);
					o.put("w", rb.x - lt.x);
					o.put("h", rb.y - lt.y);
					o.put("bx", bumpX.getValue());
					o.put("by", bumpY.getValue());
					o.put("bw", bumpWidth.getValue());
					o.put("bh", bumpHeight.getValue());
					entities.put(o);
					
					if (devMode) Helper.setFileContent(entlist, entities.toString());
				}
				catch (JSONException e1)
				{
					e1.printStackTrace();
				}
			}
		}));
		settings.add(new JLabel());
		
		SpringUtilities.makeCompactGrid(settings, 6, 2, 0, 0, 0, 0);
		int w = settings.getPreferredSize().width;
		int h = settings.getPreferredSize().height;
		settings.setBounds((850 - w) / 2, 60, w, h);
		right.add(settings);
		
		p1.add(right);
		
		if (devMode) cp.addTab("Entity Editor", p1);
		
		setContentPane(cp);
	}
	
	public static void main(String[] args)
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		new Game();
		
		new Editor();
	}
}
