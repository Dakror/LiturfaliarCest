package de.dakror.liturfaliar.editor.dialog;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import layout.SpringUtilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.dakror.liturfaliar.editor.MapEditor;
import de.dakror.liturfaliar.editor.NPCButton;
import de.dakror.liturfaliar.ui.swing.JColorSlider;

public class TalkDialog
{
	public TalkDialog(final MapEditor me, final NPCButton npc)
	{
		final JDialog talkFrame = new JDialog(me.w);
		talkFrame.setTitle("Talk-Bearbeitung - NPC #" + npc.ID);
		talkFrame.setResizable(false);
		talkFrame.setAlwaysOnTop(true);
		talkFrame.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		
		JPanel p = new JPanel(new FlowLayout());
		p.setPreferredSize(new Dimension(600, 500));
		me.talkPanel = new JPanel();
		me.talkPanel.setPreferredSize(new Dimension(600, 0));
		me.talkPanel.setLayout(null);
		me.talkScrollPane = new JScrollPane(me.talkPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		me.talkScrollPane.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.gray));
		me.talkScrollPane.setPreferredSize(new Dimension(600, 310));
		p.add(me.talkScrollPane);
		
		me.talkColorSlider = new JColorSlider();
		me.talkColorSlider.setPreferredSize(new Dimension(600, 150));
		p.add(me.talkColorSlider);
		
		me.talkAdd = new JButton("Talk hinzufügen");
		me.talkAdd.setPreferredSize(new Dimension(295, 24));
		me.talkAdd.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				addTalkComponent(me, null);
			}
		});
		p.add(me.talkAdd);
		me.talkOk = new JButton("Speichern");
		me.talkOk.setPreferredSize(new Dimension(295, 24));
		me.talkOk.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				JSONArray talk = new JSONArray();
				for (int i = 0; i < me.talkPanel.getComponentCount(); i++)
				{
					JTextField talkCond = (JTextField) ((JPanel) me.talkPanel.getComponent(i)).getComponent(1);
					JTextArea talkText = (JTextArea) ((JScrollPane) ((JPanel) me.talkPanel.getComponent(i)).getComponent(3)).getViewport().getView();
					
					if (talkCond.getText().length() == 0 && talkText.getText().length() == 0) continue;
					
					JSONArray cond = null;
					try
					{
						cond = new JSONArray("[" + talkCond.getText() + "]");
					}
					catch (JSONException e1)
					{
						e1.printStackTrace();
						JOptionPane.showMessageDialog(talkFrame, "Talk #" + (i + 1) + " konnte nicht gespeichert werden!\nDer Text im Konditionsfeld ist ungültig!\nDer Speichervorgang wird abgebrochen.", "Fehler!", JOptionPane.ERROR_MESSAGE);
						return;
					}
					try
					{
						JSONObject t = new JSONObject();
						t.put("cond", cond);
						t.put("text", talkText.getText());
						
						talk.put(t);
					}
					catch (JSONException e1)
					{
						e1.printStackTrace();
					}
				}
				npc.talk = talk;
			}
		});
		p.add(me.talkOk);
		
		talkFrame.setContentPane(p);
		talkFrame.pack();
		talkFrame.setLocationRelativeTo(me.w);
		
		if (npc != null)
		{
			for (int i = 0; i < npc.talk.length(); i++)
			{
				try
				{
					addTalkComponent(me, npc.talk.getJSONObject(i));
				}
				catch (JSONException e1)
				{
					e1.printStackTrace();
				}
			}
		}
		
		addTalkComponent(me, null);
		
		talkFrame.setVisible(true);
	}
	
	private void addTalkComponent(MapEditor me, JSONObject data)
	{
		
		JPanel p = new JPanel(new SpringLayout());
		
		if (me.talkPanel.getComponentCount() > 0) p.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, Color.gray));
		
		JLabel label = new JLabel("Bedingungen: ", JLabel.TRAILING);
		p.add(label);
		JTextField talkCond = new JTextField();
		if (data != null)
		{
			try
			{
				talkCond.setText(data.getJSONArray("cond").toString().replaceAll("(\\[)|(\\])|(\\\")", "").replace(",", ", "));
			}
			catch (JSONException e)
			{
				e.printStackTrace();
			}
		}
		p.add(talkCond);
		
		label = new JLabel("Text: ", JLabel.TRAILING);
		p.add(label);
		
		JTextArea talkText = new JTextArea(4, 0);
		talkText.setLineWrap(true);
		talkText.setFont(talkCond.getFont());
		if (data != null)
		{
			try
			{
				talkText.setText(data.getString("text"));
			}
			catch (JSONException e)
			{
				e.printStackTrace();
			}
		}
		
		JScrollPane pane = new JScrollPane(talkText, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		p.add(pane);
		
		p.setBounds(0, me.talkPanel.getComponentCount() * me.talkComponentHeight, me.talkComponentWidth, me.talkComponentHeight);
		SpringUtilities.makeCompactGrid(p, 2, 2, 6, 6, 6, 6);
		
		me.talkPanel.setPreferredSize(new Dimension(600, me.talkPanel.getPreferredSize().height + me.talkComponentHeight));
		me.talkPanel.add(p);
		
		me.talkScrollPane.setViewportView(me.talkPanel);
	}
	
}
