package nv2d.plugins.standard;

import javax.swing.JMenu;
import javax.swing.JPanel;

import nv2d.plugins.NPluginLoader;
import nv2d.plugins.NV2DPlugin;

public class SNA implements NV2DPlugin  {
	String _desc;
	String _name;
	String _author;

	public SNA() {
		_desc = new String("This plugin calculates basic social network analysis measures for a graph and it's elements.");
		_name = new String("SNA");
		_author= new String("Bo Shi");
	}

	public void initialize(/* Model, View */) {
		System.out.print("\n--> initialize()\n");
	}
	public void heartbeat() {
		System.out.print("\n--> heartbeat()\n");
	}
	public void cleanup() {
		System.out.print("\n--> cleanup()\n");
	}
	public JPanel ui() {
		return null;
	}
	public JMenu menu() {
		return null;
	}
	public String name() {
		return _name;
	}
	public String description() {
		return _desc;
	}
	public String author() {
		return _author;
	}
   
	// Note that the following routine is static and has no name, which
	// means it will only be run when the class is loaded
	static {
		// put factory in the hashtable for detector factories.
		NPluginLoader.pluginRegistry.put("SNA", new SNA());
	}
}


/* Mod_SNA.java - Standard network analysis statistical routines.
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

/*
package nvg;

import java.util.Vector;
import java.util.Stack;
*/


/** Routines for Social Network Analysis (SNA) calculations.  This class makes
 * extensive use of the <b>Matrix</b> class.
 *
 * Italicized descriptions are taken from from the following text:
 * <p>Wasserman, Stanley and Katherine Faust.  <u>Social Network Analysis:
 * Methods and Applications</u>.  Cambridge University Press, 1994.
 * @see Matrix
 */

/*
public class Mod_SNA extends CalcModule {
    Network _n;		// pointer to the network

    int[][] ADJ;
    int[][] PATHS;
*/

	/** Run through calculations. This is used on startup and whenever the
	 * graph changes; i.e. the graph was made directed/undirected or a node or
	 * edge was added/removed)
     */
/*
    public void populate(Network n) {
    }

    public String output(short mode) {
        return "Bah! [String] nvg.Mod_SNA.output(short) is NOT DONE";
    }

    public void init_vars(Network N) {
        ADJ = N.getAdjacency();
		PATHS = geodist2(ADJ);
    }
*/

    /** Calculate the density measure for a group.
     *
	 * <p><i>The density of a graph is perhaps the most widely used group-level
	 * index.  It is a recommended measure of group cohesion...  Bott (1957)
	 * used densities to quantify network "knittedness," while Barnes (1969b)
	 * used them to determine how "closeknit" empirical networds were...
	 * Density takes on values between 0 (empty graph) and 1 (complete graph),
	 * and is the average of the standardized actor degree indices, as well as
	 * the fraction of possible ties present in the network for the relation
	 * under study. (181-2)</i>
     *
     * @param m adjacency matrix for a group
     * @return group density
     */    
/*
    public static double grp_density(int[][] m) {
        int sum = 0;
        int total = m.length * (m.length - 1);

        for (int r = 0; r < m.length; r++) {
            for (int c = 0; c < m.length; c++) {
                sum += m[r][c];
            }
        }
        if (total == 0) {
            return 0;
        }
        return ((double) sum / (double) total);
    }
*/

    /** Calculates the transitivity measure for a group.
     *
	 * <i>Transitivity is a property that considers patterns of triples of
	 * actors in a etwork or triples of nodes in a graph. (165).</i>
     * @param m adjacency matrix for a group
     * @return transitivity
     */
/*
    public static double grp_transitivity(int[][] m) {
        int count = 0;
        // count triads, and transitive triads
        int total = 0;

        for (int i = 0; i < m.length; i++) {
            for (int j = 0; j < m.length; j++) {
                for (int k = 0; k < m.length; k++) {
                    if (i != j && j != k && k != i && m[i][j] != 0
                            && m[j][k] != 0) {
                        total++;
                        if (m[i][k] != 0) {
                            count++;
                        }
                    }
                }
            }
        }
        if (total == 0) {
            return 0.0;
        }
        return ((double) count / (double) total);
    }

	public static int [][] geodist2 (int [][] m) {
		int r, c, s, i;
		int len = m.length;
		int [][] dists = new int[len][len];
		for(r = 0; r < len; r++) {
			for(c = 0; c < len; c++) {
				dists[r][c] = 0;
			}
		}

		for(s = 0; s < len; s++) {
			Stack stack = new Stack();
			int [] dist = new int[len];
			for(i = 0; i < len; i++) {
				if(i == s) {
					dist[i] = 0;
				} else {
					dist[i] = -1;
				}
			}
			Vector q = new Vector();
			enq(q, s);
			while(!q.isEmpty()) {
				int v = deq(q);
				push(stack, v);
				for(int w = 0; w < len; w++) {
					if(m[v][w] == 1) {
						if(dist[w] < 0) {
							enq(q, w);
							dist[w] = dist[v] + 1;
						}
					}
				}
			}
			// normalize lengths
			for(i = 0; i < len; i++) {
				dists[s][i] = dist[i];
				if(dist[i] == -1) {
					dists[s][i] = 0;
				}
			}
		}
		return dists;
	}

	public static int [][] geodist(int [][] m) {
		int len = m.length;
		Integer [][] dists = new Integer[len][len];

		for(int i = 0; i < len; i++) {
			dists[i][i] = (new Integer(0));
			Vector curr_nodes = new Vector();
			curr_nodes.addElement((new Integer(i)));
			for(int j = 1; j < len; j++) {
				Vector next_nodes = new Vector();
				for(int k = 0; k < curr_nodes.size(); k++) {
					for(int l = 0; l < len; l++) {
						if(dists[i][l] == null) {
							dists[i][l] = (new Integer(Integer.MAX_VALUE));
						}
						int cnk = ((Integer) curr_nodes.elementAt(k)).intValue();
						int dists_i_l = ((Integer) dists[i][l]).intValue();
						if(l != i && m[cnk][l] > 0 && dists_i_l == Integer.MAX_VALUE) {
							dists[i][l] = (new Integer(j));
							next_nodes.addElement((new Integer(l)));
						}
					}
				}
				curr_nodes = next_nodes;
			}
		}
		// convert a matrix of objects to a matrix of ints
		int [][] rval = new int[len][len];
		int dd;
		for(int r = 0; r < len; r++) {
			for(int c = 0; c < len; c++) {
				dd = 0;
				if(dists[r][c] != null) {
					dd = dists[r][c].intValue();
				}
				rval[r][c] = dd;
			}
		}
		return rval;

	}
*/

    /** Compute the betweenness measure for the nodes of a graph using Brandes'
     * algorithm.
     *
	 * <p><i>The important idea here is that an actor is central if it lies
	 * between other actors on their geodesics, implying that to have a large
	 * "betweenness centrality, the actor must be between many of the other
	 * actors via their geodesics. (189)</i>
     *
     * @param m adjacency matrix for a group
	 * @return array of betwenness measures for each node in the graph.  The
	 * index of each value corresponds with the index of the node for which it
	 * applies.
     */
/*
    public static double[] compute_betweenness(int[][] m) {
        int v, w, t;
        double[] betw = new double[m.length];

        for (t = 0; t < m.length; t++) {
            betw[t] = 0.0;
        }

        int s;

        for (s = 0; s < m.length; s++) {
            int[] sigma = new int[m.length];
            int[] d = new int[m.length];
            Stack S = new Stack();
            Vector P = new Vector();

            for (t = 0; t < m.length; t++) {
                P.addElement(new Vector());
                sigma[t] = ((t == s) ? 1 : 0);
                d[t] = ((t == s) ? 0 : -1);
            }

            Vector Q = new Vector();

            enq(Q, s);
            while (!Q.isEmpty()) {
                v = deq(Q);
                push(S, v);
                // for each neighbor w of v
                for (w = 0; w < m.length; w++) {
                    if (m[v][w] == 1) {
                        // it is a neighbor
                        if (d[w] < 0) {
                            enq(Q, w);
                            d[w] = d[v] + 1;
                        }
                        if (d[w] == (d[v] + 1)) {
                            sigma[w] = sigma[w] + sigma[v];
                            // append v -> P[w]
                            appendP(P, w, v);
                        }
                    }
                }
            }

            double[] delta = new double[m.length];

            for (t = 0; t < m.length; t++) {
                delta[t] = 0.0;
            }

            while (!S.isEmpty()) {
                w = pop(S);
                for (t = 0; t < sizeP(P, w); t++) {
                    v = getP(P, w, t);
                    delta[v] += ((double) sigma[v] / (double) sigma[w])
                            * (1.0 + delta[w]);
                }
                if (w != s) {
                    betw[w] += delta[w];
                }
            }
        }

		for(int i = 0; i < m.length; i++) {
			System.out.println("{" + betw[i] + "}");
		}

		for(int i = 0; i < m.length; i++) {
			betw[i] = betw[i] / 2.0;
		}

        return betw;
    }
*/
			
	/** Support function for Stack/Queue operations in
	 * <b>compute_betweenness()</b>
     *
     * @param s stack to push value <b>v</b> onto
     * @param v value to push onto stack <b>s</b>
     * @see Stack
     */
/*
    private static void push(Stack s, int v) {
        s.push(new Integer(v));
    }
*/

	/** Support function for Stack/Queue operations in
	 * <b>compute_betweenness()</b> @param s stack from which to pop
     * @return the value popped from the stack
     * @see Stack
     */    
/*
    private static int pop(Stack s) {
        return ((Integer) (s.pop())).intValue();
    }
*/

	/** Support function for Stack/Queue operations in
	 * <b>compute_betweenness()</b> @param q enqueue a value
     * @param v value to place into queue
     * @see Vector
     */    
/*
    private static void enq(Vector q, int v) {
        q.addElement(new Integer(v));
    }
*/

	/** Support function for Stack/Queue operations in
	 * <b>compute_betweenness()</b> @param q enqueue a value
     * @return the value removed from the queue
     * @see Vector
     */    
/*
    private static int deq(Vector q) {
        return ((Integer) (q.remove(0))).intValue();
    }
*/

	/** Support function for Stack/Queue operations in
	 * <b>compute_betweenness()</b> @param P a vector of vectors
     * @param where the index of the vector to append to
     * @param val the value to place into the vector
     * @see Vector
     */
/*
    private static void appendP(Vector P, int where, int val) {
        Vector V = (Vector) (P.elementAt(where));

        V.addElement(new Integer(val));
    }
*/

	/** Support function for Stack/Queue operations in
	 * <b>compute_betweenness()</b> @param P a vector of vectors
     * @param where the index of one particular vector
     * @return the size of the vector at <b>where</b>
     * @see Vector
     */
/*
    private static int sizeP(Vector P, int where) {
        Vector V = (Vector) (P.elementAt(where));

        return V.size();
    }
*/

	/** Support function for Stack/Queue operations in
	 * <b>compute_betweenness()</b> @param P a vector of vectors
     * @param row the index of one particular vector
     * @param col the index of the value on the vector at <b>P[row]</b>
     * @return the value at <b>P[row][col]</b>
     * @see Vector
     */
/*
    private static int getP(Vector P, int row, int col) {
        Vector V = (Vector) (P.elementAt(row));
        Integer i = (Integer) V.elementAt(col);

        return i.intValue();
    }
*/

	/** A measure of the variability of betweenness measures among the nodes of
	 * a group.  @param m adjacency matrix for a group
     * @return group betweenness
     */
/*
    public static double grp_betweenness(int[][] m) {
        int nn = m.length;
        int i;
		double dd = (double) nn;	// save some typing
		double denom = (((dd - 1.0) * (dd - 1.0) * (dd - 2.0) / 2.0));

        if (denom == 0.0) {
            return 0.0;
        }
        double[] betweenness = compute_betweenness(m);
        double max = -1.0;
        double grp_betweenness = 0.0;

        for (i = 0; i < nn; i++) {
            if (betweenness[i] > max) {
                max = betweenness[i];
            }
        }

        for (i = 0; i < nn; i++) {
            grp_betweenness += max - betweenness[i];
        }

		if(denom == 0) {
			return 0;
		}

        return (grp_betweenness / denom);
    }
*/

    /** Calculate the closeness centrality of a node.
     *
	 * <p><i>The measure focuses on how close an actor is to all the other
	 * actors in the set of actors.  The idea is that an actor is central if it
	 * can quickly interact with all others.  In the context of a communication
	 * relation, such actors need not rely on other actors for the relaying of
	 * information... (183) </i>
     * @param paths a matrix containing the shortest paths between each pair of nodes in a network.
     * @param ego the index of the node in the matrix
     * @return ego (or actor) centrality
     */
/*
    public static double ego_closeness(int[][] paths, int ego) {
        int clo = 0;
        int len = paths.length;

        for (int i = 0; i < paths.length; i++) {
            if (i != ego && paths[ego][i] == 0) {
                clo += 100000;
            } else {
                clo += paths[ego][i];
            }
        }

        // debug
        // System.out.println("   ego_closeness() [sum = " + clo + " ]");
        // debug

        if (clo == 0) {
            return 0;
        }
        return ((double) (paths.length - 1) / (double) clo);
    }
*/

    /* takes the geodesics matrix of a graph */
    
    /** A measure of the variability of closeness measures among the nodes of a group.
     * @param paths a matrix containing the geodesics for each pair of nodes in a group.
     * @return group closeness
     */    
/*
    public static double grp_closeness(int[][] paths) {
        int i;
        double nn = (double) paths.length;
        double[] clo = new double[(int) nn];
		double denom = ((nn - 2.0) * (nn - 1.0)) / (2.0 * nn - 3.0);
        double group_clo;
        double max = -1;

        if (denom == 0) {
            return 0.0;
        }

        for (i = 0; i < paths.length; i++) {
            clo[i] = ego_closeness(paths, i);
            if (clo[i] > max) {
                max = clo[i];
            }
        }

        group_clo = 0.0;
        for (i = 0; i < paths.length; i++) {
            group_clo += max - clo[i];
        }
        return (group_clo / denom);
    }
*/

	/** A measure of the variability of degree measures among the nodes of a
	 * group.
	 *
     * @param m adjacency matrix for a group
     * @return group degree
     */    
/*
    public static double grp_degree(int[][] m) {
        int i;
        int nn = m.length;
        int[] degs = new int[nn];
        int denom = (nn - 1) * 2 * (nn - 2);
        int max = -1;
        double group_deg;

        if (denom == 0) {
            return 0.0;
        }

        for (i = 0; i < nn; i++) {
            degs[i] = 0;
            for (int j = 0; j < nn; j++) {
                degs[i] += m[i][j] + m[j][i];
            }
            if (degs[i] > max) {
                max = degs[i];
            }
        }

        group_deg = 0.0;
        for (i = 0; i < nn; i++) {
            group_deg += max - degs[i];
        }

        return ((double) group_deg / (double) denom);
    }
*/

    /** Calculate the indegree measure for a node.
     *
	 * <p><i>... the indegrees are measures of </i>receptivity<i>, or
	 * </i>popularity<i>.  If we consider the sociometric relation of
	 * friendship ... [an] actor with a large indegree is one whom many others
	 * nominate as a friend, and an actor with a small indegree is chosen by
	 * few others. (126) </i>
     * @param m adjacency matrix for a group
     * @param ego the index of the node in the matrix
     * @return indegree
     */    
/*
    public static int ego_indegree(int[][] m, int ego) {
        int count = 0;

        for (int i = 0; i < m.length; i++) {
            if (m[i][ego] == 1) {
                count++;
            }
        }
        return count;
    }
*/

    /** Calculate the outdegree measure for a node.
     *
	 * <p><i>Outdegrees are measures of </i>expansiveness<i>... If we consider
	 * the sociometric relation of friendship, and actor with a large outdegree
	 * is one who nominates many others as friends. (126)</i>
     * @param m adjacency matrix for a group
     * @param ego the index of the node in the matrix
     * @return outdegreer 
     */    
/*
    public static int ego_outdegree(int[][] m, int ego) {
        int count = 0;

        for (int i = 0; i < m.length; i++) {
            if (m[ego][i] == 1) {
                count++;
            }
        }
        return count;
    }
*/

    /** Calculate the total degree of a node.  Higher degree indicates higher
     * interaction with others in the group.
     *
	 * <p><i>The </i>degree<i> of a node ... is the number of lines that are
	 * incident with it... Degrees are very easy to compute, and yet can be
	 * quite informative in many applications. (100)</i>
     * @param m adjacency matrix for a group
     * @param ego the index of the node in the matrix
     * @return total degree 
     */
/*
    public static int ego_totaldegree(int[][] m, int ego) {
        return ego_indegree(m, ego) + ego_outdegree(m, ego);
    }
}
*/
