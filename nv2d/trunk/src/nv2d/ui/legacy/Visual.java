/* Visual.java - Manages visualization drawing and mouse events.
   Copyright (C) 2003,04 Bo Shi.

   NV2D is free software; you can redistribute it and/or modify it under the
   terms of the GNU General Public License as published by the Free
   Software Foundation; either version 2.1 of the License, or (at your option)
   any later version.

   NV2D is distributed in the hope that it will be useful, but WITHOUT ANY
   WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
   FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
   more details.

   You should have received a copy of the GNU General Public License
   along with NV2D; if not, write to the Free Software Foundation, Inc., 59
   Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/

package nv2d.ui.legacy;

// saving an image
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

import nv2d.graph.Graph;

/** The Visual class handles user interaction within the Visualization window.
 */
public class Visual extends JPanel {
	public static final int SCR_WIDTH = 700;
	public static final int SCR_HEIGHT = 500;

	private Draw _draw;
	// private MouseHandler _mouse;
	private Graph _graph;

	/** Constructor.
	 * @param e program environment data (user settings, etc)
	 * @param n	network data
	 */
    public Visual(Graph g/*, MouseHandler m*/) {
		_draw = new Draw(this.getGraphics(), this.getGraphics().getFontMetrics());
		_graph = g;
		//_mouse = m;

        setPreferredSize(new Dimension(SCR_WIDTH, SCR_HEIGHT));
        // addMouseListener(_mouse);
        // addMouseMotionListener(_mouse);
    }

	/** Draw the network to a graphics context.
	 *
	 * @param g graphics context to draw the network data to.
	 */
    public void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;

		// draw stuff
    }

	/** Saves the current visualization to a PNG or JPEG file.
	 *
	 * @param filename	the name of the file to save to.
	 */
	public void saveVisualFile(String filename) {
		BufferedImage bi = new BufferedImage(
				SCR_WIDTH,
				SCR_HEIGHT,
				BufferedImage.TYPE_INT_RGB);
		Graphics2D g = bi.createGraphics();

		// draw what we have
		this.paintComponent(g);

		// saves according to extension.  if extension is invalid
		// (i.e. not .jpg or .png) will default to jpg file.
		try {
			File f = new File(filename);
			if(filename.substring(filename.length() - 4).equals(".png")) {
				ImageIO.write((RenderedImage) bi, "png", f);
			} else {
				ImageIO.write((RenderedImage) bi, "jpg", f);
			}
		} catch (IOException e) {
			System.out.println("Error: " + e);
		}
		g.dispose();
	}
}
