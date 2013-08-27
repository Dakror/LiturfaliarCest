package de.dakror.liturfaliar.editor.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.SpinnerListModel;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import layout.SpringUtilities;
import de.dakror.liturfaliar.editor.MapEditor;
import de.dakror.liturfaliar.editor.NPCButton;
import de.dakror.liturfaliar.item.Categories;
import de.dakror.liturfaliar.item.Equipment;
import de.dakror.liturfaliar.item.Item;
import de.dakror.liturfaliar.item.Types;
import de.dakror.liturfaliar.util.Assistant;
import de.dakror.liturfaliar.util.FileManager;

public class EquipmentDialog
{
	public EquipmentDialog(final MapEditor me, final NPCButton npc)
	{
		if (npc != null) me.EQ = npc.getEquipment();
		
		final JDialog dialog = new JDialog(me.w, true);
		
		
		dialog.setTitle("Ausrüstungs-Bearbeitung");
		dialog.setResizable(false);
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		
		JPanel cp = new JPanel();
		cp.setLayout(new BoxLayout(cp, BoxLayout.X_AXIS));
		
		JPanel p = new JPanel(new BorderLayout());
		
		me.EQpreview = new JLabel();
		me.EQpreview.setPreferredSize(new Dimension(320, 480));
		p.add(me.EQpreview, BorderLayout.NORTH);
		
		JPanel buttons = new JPanel(new GridLayout(1, 2));
		JButton ok = new JButton("Speichern");
		ok.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				npc.setEquipment(me.EQ);
				dialog.dispose();
			}
		});
		buttons.add(ok);
		
		JButton noEquip = new JButton("Ausrüstung entfernen");
		noEquip.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				me.EQ = new Equipment();
				updateEquipDialogPreview(me);
			}
		});
		buttons.add(noEquip);
		
		p.add(buttons, BorderLayout.SOUTH);
		
		cp.add(p);
		
		cp.add(new JSeparator(SwingConstants.VERTICAL));
		
		JPanel panel = new JPanel(new SpringLayout());
		
		JLabel l = new JLabel(Categories.HAIR.name());
		panel.add(l);
		String[] chars = FileManager.getCharParts(Categories.HAIR.name().toLowerCase());
		me.EQhair = new JSpinner();
		
		ArrayList<String> list = new ArrayList<String>();
		for (String part : chars)
		{
			if (part.indexOf("_b.png") > -1 || part.indexOf("_m.png") > -1) continue;
			list.add(part.replace("_f.png", "").replace(".png", ""));
		}
		me.EQhair.setModel(new SpinnerListModel(list));
		
		me.EQhair.setPreferredSize(new Dimension(150, 22));
		if (Arrays.asList(chars).indexOf("none.png") > -1) me.EQhair.setValue("none");
		
		if (npc.getEquipment().hasEquipmentItem(Categories.HAIR)) me.EQhair.setValue(npc.getEquipment().getEquipmentItem(Categories.HAIR).getCharPath());
		
		me.EQhair.addChangeListener(new ChangeListener()
		{
			@Override
			public void stateChanged(ChangeEvent e)
			{
				me.EQ.setEquipmentItem(Categories.HAIR, new Item(Types.HAIR, me.EQhair.getValue().toString().replace("_f.png", "").replace(".png", ""), 1));
				updateEquipDialogPreview(me);
			}
		});
		
		me.EQhair.addMouseWheelListener(new MouseWheelListener()
		{
			
			@Override
			public void mouseWheelMoved(MouseWheelEvent e)
			{
				if (e.getWheelRotation() < 0)
				{
					if (me.EQhair.getModel().getPreviousValue() != null) me.EQhair.getModel().setValue(me.EQhair.getModel().getPreviousValue());
				}
				else if (me.EQhair.getModel().getNextValue() != null) me.EQhair.getModel().setValue(me.EQhair.getModel().getNextValue());
			}
		});
		panel.add(me.EQhair);
		
		l = new JLabel(Categories.SKIN.name());
		panel.add(l);
		chars = FileManager.getCharParts(Categories.SKIN.name().toLowerCase());
		me.EQskin = new JSpinner();
		
		list = new ArrayList<String>();
		for (String part : chars)
		{
			if (part.indexOf("_b.png") > -1 || part.indexOf("_m.png") > -1) continue;
			list.add(part.replace("_f.png", "").replace(".png", ""));
		}
		me.EQskin.setModel(new SpinnerListModel(list));
		
		me.EQskin.setPreferredSize(new Dimension(150, 22));
		if (Arrays.asList(chars).indexOf("none.png") > -1) me.EQskin.setValue("none");
		
		if (npc.getEquipment().hasEquipmentItem(Categories.SKIN)) me.EQskin.setValue(npc.getEquipment().getEquipmentItem(Categories.SKIN).getCharPath());
		
		me.EQskin.addChangeListener(new ChangeListener()
		{
			@Override
			public void stateChanged(ChangeEvent e)
			{
				me.EQ.setEquipmentItem(Categories.SKIN, new Item(Types.SKIN, me.EQskin.getValue().toString().replace("_f.png", "").replace(".png", ""), 1));
				updateEquipDialogPreview(me);
			}
		});
		
		me.EQskin.addMouseWheelListener(new MouseWheelListener()
		{
			
			@Override
			public void mouseWheelMoved(MouseWheelEvent e)
			{
				if (e.getWheelRotation() < 0)
				{
					if (me.EQskin.getModel().getPreviousValue() != null) me.EQskin.getModel().setValue(me.EQskin.getModel().getPreviousValue());
				}
				else if (me.EQskin.getModel().getNextValue() != null) me.EQskin.getModel().setValue(me.EQskin.getModel().getNextValue());
			}
		});
		panel.add(me.EQskin);
		
		l = new JLabel(Categories.EYES.name());
		panel.add(l);
		chars = FileManager.getCharParts(Categories.EYES.name().toLowerCase());
		me.EQeyes = new JSpinner();
		
		list = new ArrayList<String>();
		for (String part : chars)
		{
			if (part.indexOf("_b.png") > -1 || part.indexOf("_m.png") > -1) continue;
			list.add(part.replace("_f.png", "").replace(".png", ""));
		}
		me.EQeyes.setModel(new SpinnerListModel(list));
		
		me.EQeyes.setPreferredSize(new Dimension(150, 22));
		if (Arrays.asList(chars).indexOf("none.png") > -1) me.EQeyes.setValue("none");
		
		if (npc.getEquipment().hasEquipmentItem(Categories.EYES)) me.EQeyes.setValue(npc.getEquipment().getEquipmentItem(Categories.EYES).getCharPath());
		
		me.EQeyes.addChangeListener(new ChangeListener()
		{
			@Override
			public void stateChanged(ChangeEvent e)
			{
				me.EQ.setEquipmentItem(Categories.EYES, new Item(Types.EYES, me.EQeyes.getValue().toString().replace("_f.png", "").replace(".png", ""), 1));
				updateEquipDialogPreview(me);
			}
		});
		
		me.EQeyes.addMouseWheelListener(new MouseWheelListener()
		{
			
			@Override
			public void mouseWheelMoved(MouseWheelEvent e)
			{
				if (e.getWheelRotation() < 0)
				{
					if (me.EQeyes.getModel().getPreviousValue() != null) me.EQeyes.getModel().setValue(me.EQeyes.getModel().getPreviousValue());
				}
				else if (me.EQeyes.getModel().getNextValue() != null) me.EQeyes.getModel().setValue(me.EQeyes.getModel().getNextValue());
			}
		});
		panel.add(me.EQeyes);
		
		for (final Categories c : Categories.EQUIPS)
		{
			if (Arrays.asList(Categories.NATIVES).contains(c)) continue;
			
			l = new JLabel(c.name());
			panel.add(l);
			JPanel pnl = new JPanel();
			JButton btn = new JButton("X");
			btn.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					me.EQ.setEquipmentItem(c, null);
					updateEquipDialogPreview(me);
				}
			});
			pnl.add(btn);
			
			btn = new JButton("Bearbeiten");
			btn.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					new ItemDialog(me, me.EQ.getEquipmentItem(c));
					
					if (me.tmpItem != null) me.EQ.setEquipmentItem(c, me.tmpItem);
					updateEquipDialogPreview(me);
				}
			});
			pnl.add(btn);
			panel.add(pnl);
		}
		
		// -- weapon1 -- //
		l = new JLabel("WEAPON 1");
		panel.add(l);
		JPanel pnl = new JPanel();
		JButton btn = new JButton("X");
		btn.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				me.EQ.setFirstWeapon(null);
				updateEquipDialogPreview(me);
			}
		});
		pnl.add(btn);
		
		btn = new JButton("Bearbeiten");
		btn.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				new ItemDialog(me, me.EQ.getFirstWeapon());
				if (me.tmpItem != null) me.EQ.setFirstWeapon(me.tmpItem);
				updateEquipDialogPreview(me);
			}
		});
		pnl.add(btn);
		panel.add(pnl);
		
		// -- weapon2 -- //
		l = new JLabel("WEAPON 2");
		panel.add(l);
		pnl = new JPanel();
		btn = new JButton("X");
		btn.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				me.EQ.setSecondWeapon(null);
				updateEquipDialogPreview(me);
			}
		});
		pnl.add(btn);
		
		btn = new JButton("Bearbeiten");
		btn.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				new ItemDialog(me, me.EQ.getSecondWeapon());
				if (me.tmpItem != null) me.EQ.setSecondWeapon(me.tmpItem);
				updateEquipDialogPreview(me);
			}
		});
		pnl.add(btn);
		panel.add(pnl);
		
		
		SpringUtilities.makeCompactGrid(panel, Categories.EQUIPS.length + 2, 2, 6, 6, 6, 6);
		
		cp.add(panel);
		
		dialog.setContentPane(cp);
		dialog.pack();
		dialog.setLocationRelativeTo(me.w);
		updateEquipDialogPreview(me);
		dialog.setVisible(true);
		
	}
	
	private void updateEquipDialogPreview(MapEditor me)
	{
		int w = me.EQpreview.getPreferredSize().width;
		int h = me.EQpreview.getPreferredSize().height;
		BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D) bi.getGraphics();
		
		for (int i = 0; i < 4; i++)
		{
			for (int j = 0; j < 4; j++)
			{
				Assistant.drawChar(i * w / 4, j * h / 4, w / 4, h / 4, j, i, me.EQ, g, true);
			}
		}
		me.EQpreview.setIcon(new ImageIcon(bi));
	}
}
