/* DrawJ2.java - All drawing routines.
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

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Ellipse2D;
import java.util.Iterator;
import java.util.Vector;

import nv2d.graph.DataStore;
import nv2d.graph.Datum;
import nv2d.graph.Edge;
import nv2d.graph.Graph;
import nv2d.graph.Vertex;

public class Draw {
	FontMetrics _fm;
	Graphics2D _g;

	public static int PADDING = 10;	// amount of padding text boxes get

	public Draw(Graphics g, FontMetrics fm) {
		_fm = fm;
		_g = (Graphics2D) g;
	}

	public void setAntialiasing(boolean tf) {
		if(_g == null) {
			return;
		}
		if(tf) {
			_g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
		} else {
			_g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_OFF);
		}
	}
    
    // public void key(String attr) {

	/* Prereq: v is not null */
	/** Low level draw function */
    public void paintVertexInfo(Vertex v, int X, int Y) {
		Iterator i = v.getDatumSet().iterator();
		Vector txt = new Vector();
		int maxWidth = -1;

		txt.add("Vertex [" + v.id() + "]");
		txt.add("  ");	// skip a space
		while(i.hasNext()) {
			Datum d = (Datum) i.next();
			String line = d.name() + " [" + d.get().toString() + "]";
			if(_fm.stringWidth(line) > maxWidth) {
				maxWidth = _fm.stringWidth(line);
			}
			txt.add(line);
		}
		paintText(txt, X, Y, maxWidth);
    }

	public void paintGraphInfo(Graph g, int X, int Y) {
		Iterator i = g.getDatumSet().iterator();
		Vector txt = new Vector();
		int maxWidth = -1;

		while(i.hasNext()) {
			Datum d = (Datum) i.next();
			String line = d.name() + " [" + d.get().toString() + "]";
			if(_fm.stringWidth(line) > maxWidth) {
				maxWidth = _fm.stringWidth(line);
			}
			txt.add(line);
		}
		paintText(txt, X, Y, maxWidth);
	}


	private void paintText(Vector txt, int X, int Y, int maxWidth) {
        _g.setPaint(new Color(100, 100, 100, 153));
        _g.fill(new Rectangle2D.Double(X, Y, X + maxWidth + 2 * PADDING, txt.size()));
		Iterator i = txt.iterator();
		int x = X + PADDING / 2;
		int h = _fm.getHeight();

		while(i.hasNext()) {
			String s = (String) i.next();
			_g.drawString(s, x, Y + PADDING / 2);
			x += h;
		}
	}

    public void paintEdge(Edge e, int x1, int y1, int x2, int y2) {
        _g.draw(new Line2D.Double(x1, y1, x2, y2));

		// figure out angle that arrow should be rotated
		// (arrow is always to (x2, y2))
		double theta = Math.atan((double) (y2 - y1) / (double) (x2 - x1));
		if (x1 > x2) {
			theta = theta + Math.PI;
		}
		Polygon arrow = poly((int) (x1 + (x2 - x1) * (4.0 / 5.0)),
			(int) (y1 + (y2 - y1) * (4.0 / 5.0)), 3 /* sides */, 4.0, theta);
		_g.draw(arrow);
    }

	public void paintVertex(Vertex v, int X, int Y) {
        int w = _fm.stringWidth(v.id());
        int h = _fm.getHeight();
		int r = 10;

		_g.draw(new Ellipse2D.Double(X - r, Y - r, r * 2, r * 2));
		_g.drawString(v.id(), X + r, Y + r);
    }
/*
    public void emptyKey() {
        String s = "(!) No Key for Selected Attribute";
        int X = (int) Util.VISUAL_SIZE.getWidth();
        int Y = (int) Util.VISUAL_SIZE.getHeight();
        int x = X - 20 - _fm.stringWidth(s);
        int y = Y - 20 - _fm.getAscent() * 3;

        _g.setColor(Color.black);
        _g.drawString("Key", x, y);
        _g.drawString(s, x, y + 2 * _fm.getAscent());
    }
	*/

    public Polygon poly(int x, int y, int sides, double radius, double theta) {
        Polygon p = new Polygon();

        for (int i = 0; i < sides; i++) {
            double angle = (double) i * 2.0 * Math.PI / (double) sides + theta;

            p.addPoint(x + (int) (radius * Math.cos(angle)),
                    y + (int) (radius * Math.sin(angle)));
        }

        return p;
    }
}
