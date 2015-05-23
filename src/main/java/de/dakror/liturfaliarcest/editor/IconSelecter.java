/*******************************************************************************
 * Copyright 2015 Maximilian Stark | Dakror <mail@dakror.de>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
 

package de.dakror.liturfaliarcest.editor;

import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;

import de.dakror.gamesetup.util.swing.WrapLayout;

/**
 * @author Dakror
 */
public class IconSelecter {
	public static void create() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			ToolTipManager.sharedInstance().setInitialDelay(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		final JFrame frame = new JFrame("Liturfaliar Cest Editor: Icons");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setResizable(false);
		
		final JPanel panel = new JPanel(new WrapLayout(FlowLayout.LEFT, 0, 0));
		JScrollPane jsp = new JScrollPane(panel);
		frame.setContentPane(jsp);
		frame.setVisible(true);
		Insets i = frame.getInsets();
		frame.setSize(384 + i.left + i.right + 20, 1000);
		frame.setLocationRelativeTo(null);
		
		new Thread() {
			@Override
			public void run() {
				setPriority(MAX_PRIORITY);
				try {
					BufferedImage bi = ImageIO.read(IconSelecter.class.getResource("/img/system/icons.png"));
					int amount = bi.getHeight() / 24 * bi.getWidth() / 24;
					int c = 0;
					for (int i = 0; i < bi.getHeight() / 24; i++) {
						for (int j = 0; j < bi.getWidth() / 24; j++) {
							JLabel l = new JLabel(new ImageIcon(bi.getSubimage(j * 24, i * 24, 24, 24)));
							l.setToolTipText(j + " x " + i);
							panel.add(l);
							
							frame.setTitle("Icon Selecter (" + Math.round(c / (float) amount * 100) + "%)");
							c++;
						}
					}
					
					frame.setTitle("Icon Selecter (" + c + " icons)");
					frame.revalidate();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}
}
