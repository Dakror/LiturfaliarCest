package de.dakror.liturfaliar.editor.dialog;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Arrays;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SpringLayout;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import layout.SpringUtilities;

import org.json.JSONException;
import org.json.JSONObject;

import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.editor.MapEditor;
import de.dakror.liturfaliar.editor.NPCButton;
import de.dakror.liturfaliar.editor.TileButton;
import de.dakror.liturfaliar.item.Equipment;
import de.dakror.liturfaliar.map.Map;
import de.dakror.liturfaliar.map.data.Door;
import de.dakror.liturfaliar.settings.Attributes;
import de.dakror.liturfaliar.settings.CFG;
import de.dakror.liturfaliar.util.Assistant;
import de.dakror.liturfaliar.util.FileManager;

public class EditFieldDataDialog
{
	public EditFieldDataDialog(final MapEditor me, final TileButton field, final String dataType)
	{
		try
		{
			// -- general setup -- //
			final JDialog dialog = new JDialog(me.w, true);
			dialog.setTitle("Feld-Data bearbeiten");
			dialog.setSize(400, 320);
			dialog.setResizable(false);
			dialog.setLocationRelativeTo(me.w);
			
			JPanel cp = new JPanel();
			cp.setLayout(new BoxLayout(cp, BoxLayout.Y_AXIS));
			
			dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			JPanel inputs = new JPanel(new SpringLayout());
			JButton delete = new JButton("Entfernen");
			delete.setEnabled(false);
			delete.setPreferredSize(new Dimension(190, 23));
			final JButton save = new JButton("Speichern");
			save.setPreferredSize(new Dimension(190, 23));
			// -- type specific setup -- //
			JSONObject exist = field.getDataByType(dataType);
			if (exist != null)
			{
				delete.setEnabled(true);
				delete.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent e)
					{
						field.removeDataByType(dataType);
						dialog.dispose();
					}
				});
			}
			switch (dataType)
			{
				case "Door":
				{
					JLabel name = new JLabel("Ziel X-Koordinate:");
					name.setPreferredSize(new Dimension(190, 23));
					inputs.add(name);
					final JTextField dx = new JTextField("0");
					if (exist != null) dx.setText("" + exist.getInt("dx"));
					inputs.add(dx);
					
					inputs.add(new JLabel("Ziel Y-Koordinate:"));
					final JTextField dy = new JTextField("0");
					if (exist != null) dy.setText("" + exist.getInt("dy"));
					inputs.add(dy);
					
					inputs.add(new JLabel("Zielrichtung:"));
					final String[] dirs = new String[] { "Gleiche", "Unten", "Links", "Rechts", "Oben" };
					final JComboBox<String> dir = new JComboBox<String>(dirs);
					if (exist != null) dir.setSelectedIndex(exist.getInt("dir") + 1);
					inputs.add(dir);
					
					inputs.add(new JLabel("Leucht-Pfeil:"));
					final String[] arrows = new String[] { "< Leer >", "Unten", "Links", "Rechts", "Oben" };
					final JComboBox<String> arr = new JComboBox<String>(arrows);
					if (exist != null && exist.has("arr")) arr.setSelectedIndex(exist.getInt("arr") + 1);
					else arr.setSelectedIndex(0);
					inputs.add(arr);
					
					inputs.add(new JLabel("Zielkarte:"));
					final JDialog mapCoordSelect = new JDialog(dialog, "", false);
					mapCoordSelect.setLayout(null);
					mapCoordSelect.setResizable(false);
					mapCoordSelect.setLocation(dialog.getX() + dialog.getWidth() + 10, dialog.getY());
					mapCoordSelect.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
					mapCoordSelect.setVisible(true);
					final JComboBox<String> map = new JComboBox<String>(Map.getMaps(me.mappackdata.getString("name"), CFG.MAPEDITORDIR));
					map.addActionListener(new ActionListener()
					{
						@Override
						public void actionPerformed(ActionEvent e)
						{
							new Thread()
							{
								public void run()
								{
									try
									{
										final String s = (String) map.getSelectedItem();
										mapCoordSelect.setTitle(s);
										BufferedImage bi = new Map(me.mappackdata.getString("name"), s, CFG.MAPEDITORDIR).getRendered(1);
										final JLabel l = new JLabel();
										l.addMouseListener(new MouseAdapter()
										{
											@Override
											public void mousePressed(MouseEvent e)
											{
												dx.setText("" + (e.getX() - CFG.HUMANBOUNDS[0] / 2));
												dy.setText("" + (e.getY() - CFG.HUMANBOUNDS[1] * 2 / 3));
												try
												{
													BufferedImage bi = new Map(me.mappackdata.getString("name"), s, CFG.MAPEDITORDIR).getRendered(1);
													int d = Arrays.asList(dirs).indexOf(((String) dir.getSelectedItem())) - 1;
													d = (d < 0) ? 0 : d;
													Assistant.drawChar(e.getX() - CFG.HUMANBOUNDS[0] / 2, e.getY() - CFG.HUMANBOUNDS[1] * 2 / 3, CFG.HUMANBOUNDS[0], CFG.HUMANBOUNDS[1], d, 0, Equipment.getDefault(true), (Graphics2D) bi.getGraphics(), true);// Assistant.Rect(e.getX() - CFG.HUMANBOUNDS[0] / 2, e.getY() - CFG.HUMANBOUNDS[0], CFG.HUMANBOUNDS[0], CFG.HUMANBOUNDS[1], Color.cyan, null, (Graphics2D) bi.getGraphics());
													l.setIcon(new ImageIcon(bi));
												}
												catch (JSONException e1)
												{
													e1.printStackTrace();
												}
											}
										});
										l.setSize(bi.getWidth(), bi.getHeight());
										l.setIcon(new ImageIcon(bi));
										JScrollPane jsp = new JScrollPane(l, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
										jsp.getVerticalScrollBar().setUnitIncrement(CFG.FIELDSIZE / 3);
										jsp.getHorizontalScrollBar().setUnitIncrement(CFG.FIELDSIZE / 3);
										jsp.setPreferredSize(new Dimension(500, 500));
										mapCoordSelect.setContentPane(jsp);
										mapCoordSelect.pack();
									}
									catch (JSONException e1)
									{
										e1.printStackTrace();
									}
								}
							}.start();
						}
					});
					map.setSelectedIndex(0);
					if (exist != null) map.setSelectedItem(exist.getString("map"));
					inputs.add(map);
					
					inputs.add(new JLabel("Sound:"));
					final JComboBox<String> sound = new JComboBox<String>();
					sound.addItem("< Leer >");
					for (String s : FileManager.getMediaFiles("Sound"))
					{
						sound.addItem(s);
					}
					if (exist != null) sound.setSelectedItem(exist.getString("sound"));
					sound.addActionListener(new ActionListener()
					{
						@Override
						public void actionPerformed(ActionEvent e)
						{
							if (((String) sound.getSelectedItem()).equals("< Leer >")) return;
							Viewport.playSound((String) sound.getSelectedItem());
						}
					});
					inputs.add(sound);
					
					inputs.add(new JLabel("Animation:"));
					final JComboBox<String> img = new JComboBox<String>();
					final JLabel preview = new JLabel();
					preview.setPreferredSize(new Dimension(CFG.FIELDSIZE, CFG.FIELDSIZE));
					img.addItem("< Leer >");
					for (int i = 0; i < Door.CHARS.length * 4; i++)
					{
						img.addItem(Door.CHARS[(int) Math.floor(i / 4.0)] + ": " + ((i % 4) + 1));
					}
					img.addActionListener(new ActionListener()
					{
						@Override
						public void actionPerformed(ActionEvent e)
						{
							String s = (String) img.getSelectedItem();
							if (s.equals("< Leer >"))
							{
								preview.setIcon(null);
								return;
							}
							int part = Integer.parseInt(s.substring(s.indexOf(": ") + ": ".length())) - 1;
							BufferedImage i = (BufferedImage) Viewport.loadImage("char/objects/" + s.substring(0, s.indexOf(": ")) + ".png");
							preview.setPreferredSize(new Dimension(i.getWidth() / 4, i.getHeight() / 4));
							preview.setIcon(new ImageIcon(i.getSubimage(i.getWidth() / 4 * part, 0, i.getWidth() / 4, i.getHeight() / 4)));
						}
					});
					if (exist != null && exist.getString("img").length() > 0)
					{
						int index = Arrays.asList(Door.CHARS).indexOf(exist.getString("img")) * 4;
						img.setSelectedIndex(index + exist.getInt("t") + 1);
					}
					inputs.add(img);
					
					inputs.add(new JLabel());
					inputs.add(preview);
					save.addActionListener(new ActionListener()
					{
						@Override
						public void actionPerformed(ActionEvent e)
						{
							JSONObject o = new JSONObject();
							try
							{
								try
								{
									o.put("dx", Integer.parseInt(dx.getText()));
									o.put("dy", Integer.parseInt(dy.getText()));
								}
								catch (NumberFormatException e1)
								{
									JOptionPane.showMessageDialog(me.w, "Koordinaten dürfen nur aus Zahlen bestehen!", "", JOptionPane.ERROR_MESSAGE);
									return;
								}
								o.put("dir", dir.getSelectedIndex() - 1);
								o.put("arr", arr.getSelectedIndex() - 1);
								o.put("map", (String) map.getSelectedItem());
								o.put("sound", ((String) sound.getSelectedItem()).replace("< Leer >", ""));
								String s = (String) img.getSelectedItem();
								if (!s.equals("< Leer >"))
								{
									o.put("img", s.substring(0, s.indexOf(": ")));
									o.put("t", Integer.parseInt(s.substring(s.indexOf(": ") + ": ".length())) - 1);
									Image i = Viewport.loadImage("char/objects/" + s.substring(0, s.indexOf(": ")) + ".png");
									int w = i.getWidth(null) / 4;
									int h = i.getHeight(null) / 4;
									o.put("x", (CFG.FIELDSIZE / 2 - w / 2));
									o.put("y", (CFG.FIELDSIZE - h / 2));
								}
								else
								{
									o.put("img", "");
									o.put("t", 0);
									o.put("x", 0);
									o.put("y", 0);
								}
								field.addData(dataType, o);
								dialog.dispose();
							}
							catch (JSONException e2)
							{
								e2.printStackTrace();
								return;
							}
						}
					});
					SpringUtilities.makeCompactGrid(inputs, 8, 2, 6, 6, 6, 6);
					break;
				}
				case "Spawner":
				{
					if (exist != null)
					{
						JSONObject data = exist.getJSONObject("npc");
						BufferedImage image = (BufferedImage) Viewport.loadImage("char/chars/" + data.getString("char") + ".png");
						me.spawnerNPC = new NPCButton(data.getInt("x"), data.getInt("y"), data.getInt("w"), data.getInt("h"), data.getInt("dir"), data.getString("name"), data.getString("char"), data.getDouble("speed"), data.getJSONObject("random").getBoolean("move"), data.getJSONObject("random").getBoolean("look"), data.getJSONObject("random").getInt("moveT"), data.getJSONObject("random").getInt("lookT"), image.getSubimage(0, data.getInt("dir") * image.getHeight() / 4, image.getWidth() / 4, image.getHeight() / 4), data.getBoolean("hostile"), -1, data.getString("ai"), me);
						me.spawnerNPC.talk = data.getJSONArray("talk");
						me.spawnerNPC.setEquipment(new Equipment(data.getJSONObject("equip")));
						me.spawnerNPC.attributes = new Attributes(data.getJSONObject("attr"));;
					}
					else
					{
						me.spawnerNPC = null;
					}
					me.map.spawnerPos = new Point(field.getX() + field.getWidth() / 2, field.getY() + field.getHeight() / 2);
					JLabel name = new JLabel("Radius (grün):");
					name.setPreferredSize(new Dimension(190, 23));
					inputs.add(name);
					final JSpinner rad = new JSpinner(new SpinnerNumberModel(me.map.spawnerRadius = (exist == null) ? 192 : exist.getInt("radius"), 0, Integer.MAX_VALUE, 32));
					
					
					rad.addChangeListener(new ChangeListener()
					{
						
						@Override
						public void stateChanged(ChangeEvent e)
						{
							me.map.spawnerRadius = (int) rad.getValue();
							me.map.repaint();
						}
					});
					inputs.add(rad);
					
					inputs.add(new JLabel("Player-Entfernung (orange):"));
					final JSpinner dst = new JSpinner(new SpinnerNumberModel(me.map.spawnerDistance = (exist == null) ? 224 : exist.getInt("distance"), 0, Integer.MAX_VALUE, 32));
					dst.addChangeListener(new ChangeListener()
					{
						
						@Override
						public void stateChanged(ChangeEvent e)
						{
							me.map.spawnerDistance = (int) dst.getValue();
							me.map.repaint();
						}
					});
					inputs.add(dst);
					
					inputs.add(new JLabel("Spawn-Geschwindigkeit (in ms):"));
					final JSpinner spd = new JSpinner(new SpinnerNumberModel((exist == null) ? 1000 : exist.getInt("speed"), 0, Integer.MAX_VALUE, 500));
					inputs.add(spd);
					
					inputs.add(new JLabel("Spawncap (-1 = aus):"));
					final JSpinner spc = new JSpinner(new SpinnerNumberModel((exist == null) ? -1 : exist.getInt("cap"), -1, Integer.MAX_VALUE, 10));
					inputs.add(spc);
					
					inputs.add(new JLabel("Respawn nach Neuladen?"));
					final JCheckBox rsp = new JCheckBox();
					inputs.add(rsp);
					
					inputs.add(new JLabel("NPC:"));
					
					final JButton equ = new JButton(new AbstractAction("Bearbeiten")
					{
						private static final long serialVersionUID = 1L;
						
						@Override
						public void actionPerformed(ActionEvent e)
						{
							new EquipmentDialog(me, me.spawnerNPC);
						}
					});
					if (me.spawnerNPC == null) equ.setEnabled(false);
					
					inputs.add(new JButton(new AbstractAction("Bearbeiten")
					{
						private static final long serialVersionUID = 1L;
						
						@Override
						public void actionPerformed(ActionEvent e)
						{
							new NPCDialog(me, me.spawnerNPC, true);
							equ.setEnabled(true);
							save.setEnabled(true);
						}
					}));
					
					inputs.add(new JLabel("NPC-Equipment:"));
					inputs.add(equ);
					
					save.addActionListener(new ActionListener()
					{
						@Override
						public void actionPerformed(ActionEvent e)
						{
							try
							{
								JSONObject o = new JSONObject();
								o.put("npc", me.spawnerNPC.getSave());
								o.put("radius", (Integer) rad.getValue());
								o.put("distance", (Integer) dst.getValue());
								o.put("speed", (Integer) spd.getValue());
								o.put("cap", (Integer) spc.getValue());
								o.put("respawn", rsp.isSelected());
								field.addData(dataType, o);
								dialog.dispose();
							}
							catch (JSONException e1)
							{
								e1.printStackTrace();
							}
						}
					});
					if (me.spawnerNPC == null) save.setEnabled(false);
					me.map.repaint();
					SpringUtilities.makeGrid(inputs, 7, 2, 6, 6, 6, 6);
					break;
				}
			}
			cp.add(inputs);
			JPanel buttons = new JPanel(new FlowLayout());
			buttons.add(delete);
			buttons.add(save);
			cp.add(buttons);
			dialog.setContentPane(cp);
			dialog.pack();
			dialog.setVisible(true);
			
			me.map.spawnerPos = null;
			me.map.repaint();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
