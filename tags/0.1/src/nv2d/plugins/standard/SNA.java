package nv2d.plugins.standard;

import java.awt.Container;
import java.lang.Integer;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;
import java.util.Stack;
import javax.swing.JMenu;
import javax.swing.JPanel;

import nv2d.plugins.NPluginLoader;
import nv2d.plugins.NV2DPlugin;

import nv2d.graph.Datum;
import nv2d.graph.Edge;
import nv2d.graph.Graph;
import nv2d.graph.Vertex;
import nv2d.algorithms.shortestpaths.Dijkstra;
import nv2d.ui.NController;

/** Plugin for Social Network Analysis (SNA) calculations.
 *
 * Italicized descriptions are taken from from the following text:
 * <p>Wasserman, Stanley and Katherine Faust.  <u>Social Network Analysis:
 * Methods and Applications</u>.  Cambridge University Press, 1994.
 * @see Matrix
 */
public class SNA implements NV2DPlugin  {
	String _desc;
	String _name;
	String _author;

	Graph _graph;

	/* Some datums */

	/** Datum name for current index */
	public static final String DATUM_INDEX = "__sna_index";

	public static final String DATUM_GRP_DENSITY = "Group Density";
	public static final String DATUM_GRP_TRANSITIVITY = "Group Transitivity";
	public static final String DATUM_GRP_BETWEENNESS = "Group Betweenness";
	public static final String DATUM_GRP_CLOSENESS = "Group Closeness";
	public static final String DATUM_GRP_DEGREE = "Group Degree";
	public static final String DATUM_BETWEENNESS = "Betweenness";
	public static final String DATUM_CLOSENESS = "Closeness";
	public static final String DATUM_DEGREE = "Degree";
	public static final String DATUM_INDEGREE = "In-Degree";
	public static final String DATUM_OUTDEGREE = "Out-Degree";

	public SNA() {
		_desc = new String("This plugin calculates basic social network analysis measures for a graph and it's elements.");
		_name = new String("SNA");
		_author= new String("Bo Shi");
	}

	public void initialize(Graph g, Container view, NController control) {
		System.out.print("--> initialize()\n");
		_graph = g;

		if(g != null) {
			indecize();
			calculate();
		}
	}
	public void heartbeat() {
		System.out.print("--> heartbeat()\n");
	}

	/* TODO */
	public void cleanup() {
		System.out.print("--> cleanup()\n");
	}
	public JPanel ui() {
		return null;
	}
	public JMenu menu() {
		return null;
	}

	public String require() {
		return "";
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
		NPluginLoader.reg("SNA", new SNA());
	}

	/* ===================================== *
	      SNA Functions
	 * ===================================== */

	/** Lookup-table to map vertices to indeces of the generated adjacency
	 * matrices */
	Vertex [] _vtx_index_tbl = null;

	/** Assign an integer index to every Vertex in the graph.  This does not
	 * sort the vertices in any fashion. */
	private void indecize() {
		Set v = _graph.getVertices();
		Iterator i = v.iterator();
		int index = 0;

		_vtx_index_tbl = new Vertex[v.size()];

		while(i.hasNext()) {
			Vertex vtx = (Vertex) i.next();

			// index -> vertex map
			_vtx_index_tbl[index] = vtx;

			// vertex -> index map
			vtx.setDatum(new Datum(DATUM_INDEX, new Integer(index)));

			index++;
		}
	}

	/** Wrapper funtion to get the distance between two vertices. Does not check
	 * to see if indices provided are within bounds. */
	private double edgeLen(int source, int dest) {
		return _graph.edgeLen(_vtx_index_tbl[source], _vtx_index_tbl[dest]);
	}

	/** Wrapper funtion to get the geodesic distance between two vertices. Does not check
	 * to see if indices provided are within bounds. */
	private double shortestPathLen(int source, int dest) {
		return _graph.shortestPathLen(_vtx_index_tbl[source], _vtx_index_tbl[dest]);
	}

	/* TODO */
	private void calculate() {
		if (_vtx_index_tbl == null || _vtx_index_tbl.length < 1) {
			System.out.println("[SNA] Warning: no graph loaded or graph contains no vertices.");
			return;
		}
		int v;
		double m_grpDensity = grp_density();
		double m_grpTransitivity = grp_transitivity();
		double [] m_betweenness = compute_betweenness();
		double m_grpBetweenness = grp_betweenness(m_betweenness);
		double [] m_closeness = compute_closeness();
		double m_grpCloseness = grp_closeness(m_closeness);
		int [] m_degree = compute_totaldegree();
		int [] m_indegree = compute_indegree();
		int [] m_outdegree = compute_outdegree();
		double m_grpDegree = grp_degree(m_degree);

		_graph.setDatum(new Datum(DATUM_GRP_DENSITY, new Double(m_grpDensity)));
		System.out.println("   Group Density = " + m_grpDensity);

		_graph.setDatum(new Datum(DATUM_GRP_TRANSITIVITY, new Double(m_grpTransitivity)));
		System.out.println("   Group Transitivity = " + m_grpTransitivity);

		_graph.setDatum(new Datum(DATUM_GRP_BETWEENNESS, new Double(m_grpBetweenness)));
		System.out.println("   Group Betweenness= " + m_grpBetweenness);

		_graph.setDatum(new Datum(DATUM_GRP_CLOSENESS, new Double(m_grpCloseness)));
		System.out.println("   Group Closeness= " + m_grpCloseness);

		_graph.setDatum(new Datum(DATUM_GRP_DEGREE, new Double(m_grpDegree)));
		System.out.println("   Group Degree = " + m_grpDegree);

		System.out.println("   Betweenness:");
		for(v = 0; v < _graph.numVertices(); v++) {
			_vtx_index_tbl[v].setDatum(new Datum(DATUM_BETWEENNESS, new Double(m_betweenness[v])));
			System.out.println("      [" + v + "] " + m_betweenness[v]);
		}

		System.out.println("   Closeness:");
		for(v = 0; v < _graph.numVertices(); v++) {
			_vtx_index_tbl[v].setDatum(new Datum(DATUM_CLOSENESS, new Double(m_closeness[v])));
			System.out.println("      [" + v + "] " + m_closeness[v]);
		}

		System.out.println("   Degree:");
		for(v = 0; v < _graph.numVertices(); v++) {
			_vtx_index_tbl[v].setDatum(new Datum(DATUM_DEGREE, new Double(m_degree[v])));
			System.out.println("      [" + v + "] " + m_degree[v]);
		}

		System.out.println("   In-Degree:");
		for(v = 0; v < _graph.numVertices(); v++) {
			_vtx_index_tbl[v].setDatum(new Datum(DATUM_INDEGREE, new Double(m_indegree[v])));
			System.out.println("      [" + v + "] " + m_indegree[v]);
		}

		System.out.println("   Out-Degree:");
		for(v = 0; v < _graph.numVertices(); v++) {
			_vtx_index_tbl[v].setDatum(new Datum(DATUM_OUTDEGREE, new Double(m_outdegree[v])));
			System.out.println("      [" + v + "] " + m_outdegree[v]);
		}
	}


	/*===========================================================*/
	// SNA algorithms
	/*===========================================================*/

    /** Calculate the density measure for a group.
     *
	 * <p><i>The density of a graph is perhaps the most widely used group-level
	 * index.  It is a recommended measure of group cohesion...  Bott (1957)
	 * used densities to quantify network "knittedness," while Barnes (1969b)
	 * used them to determine how "closeknit" empirical networks were...
	 * Density takes on values between 0 (empty graph) and 1 (complete graph),
	 * and is the average of the standardized actor degree indices, as well as
	 * the fraction of possible ties present in the network for the relation
	 * under study. (181-2)</i>
     *
     * @return group density
     */    
	private double grp_density() {
		double sum = 0;
		double total = _graph.numVertices() * (_graph.numVertices() - 1);

		for (int r = 0; r < _graph.numVertices(); r++) {
			for (int c = 0; c < _graph.numVertices(); c++) {
				sum += edgeLen(r, c);
			}
		}
		if (total == 0.0) {
			return 0.0;
		}
		return sum / total;
	}

    /** Calculates the transitivity measure for a group.
     *
	 * <i>Transitivity is a property that considers patterns of triples of
	 * actors in a etwork or triples of nodes in a graph. (165).</i>
     * @return transitivity
     */
	private double grp_transitivity() {
        int count = 0;
        // count triads, and transitive triads
        int total = 0;

        for (int i = 0; i < _graph.numVertices(); i++) {
            for (int j = 0; j < _graph.numVertices(); j++) {
                for (int k = 0; k < _graph.numVertices(); k++) {
                    if (i != j && j != k && k != i && edgeLen(i, j) != 0.0
                            && edgeLen(j, k) != 0.0) {
                        total++;
                        if (edgeLen(i, k) != 0.0) {
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

    /** Compute the betweenness measure for the nodes of a graph using Brandes'
     * algorithm. Assumes that edges cannot have negative lengths.
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
    private double[] compute_betweenness() {
		int numv = _graph.numVertices();
        int v, w, t;
        double[] betw = new double[numv];

        for (t = 0; t < numv; t++) {
            betw[t] = 0.0;
        }

        int s;

        for (s = 0; s < numv; s++) {
            int[] sigma = new int[numv];
            int[] d = new int[numv];
            Stack S = new Stack();
            Vector P = new Vector();

            for (t = 0; t < numv; t++) {
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
                for (w = 0; w < numv; w++) {
                    if (edgeLen(v, w) > 0.0) {
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

            double[] delta = new double[numv];

            for (t = 0; t < numv; t++) {
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

		for(int i = 0; i < numv; i++) {
			betw[i] = betw[i] / 2.0;
		}

        return betw;
    }
			
	/** Support function for Stack/Queue operations in
	 * <b>compute_betweenness()</b>
     *
     * @param s stack to push value <b>v</b> onto
     * @param v value to push onto stack <b>s</b>
     * @see Stack
     */
    private void push(Stack s, int v) {
        s.push(new Integer(v));
    }

	/** Support function for Stack/Queue operations in
	 * <b>compute_betweenness()</b> @param s stack from which to pop
     * @return the value popped from the stack
     * @see Stack
     */    
    private int pop(Stack s) {
        return ((Integer) (s.pop())).intValue();
    }

	/** Support function for Stack/Queue operations in
	 * <b>compute_betweenness()</b> @param q enqueue a value
     * @param v value to place into queue
     * @see Vector
     */    
    private void enq(Vector q, int v) {
        q.addElement(new Integer(v));
    }

	/** Support function for Stack/Queue operations in
	 * <b>compute_betweenness()</b> @param q enqueue a value
     * @return the value removed from the queue
     * @see Vector
     */    
    private int deq(Vector q) {
        return ((Integer) (q.remove(0))).intValue();
    }

	/** Support function for Stack/Queue operations in
	 * <b>compute_betweenness()</b> @param P a vector of vectors
     * @param where the index of the vector to append to
     * @param val the value to place into the vector
     * @see Vector
     */
    private void appendP(Vector P, int where, int val) {
        Vector V = (Vector) (P.elementAt(where));

        V.addElement(new Integer(val));
    }

	/** Support function for Stack/Queue operations in
	 * <b>compute_betweenness()</b> @param P a vector of vectors
     * @param where the index of one particular vector
     * @return the size of the vector at <b>where</b>
     * @see Vector
     */
    private int sizeP(Vector P, int where) {
        Vector V = (Vector) (P.elementAt(where));

        return V.size();
    }

	/** Support function for Stack/Queue operations in
	 * <b>compute_betweenness()</b> @param P a vector of vectors
     * @param row the index of one particular vector
     * @param col the index of the value on the vector at <b>P[row]</b>
     * @return the value at <b>P[row][col]</b>
     * @see Vector
     */
    private int getP(Vector P, int row, int col) {
        Vector V = (Vector) (P.elementAt(row));
        Integer i = (Integer) V.elementAt(col);

        return i.intValue();
    }

	/** A measure of the variability of betweenness measures among the nodes of
	 * a group.  Requires that you have already computed the individual
	 * betweenness scores of all the vertices.
	 *
	 * @return group betweenness
     */
    private double grp_betweenness(double [] betweenness) {
        int numv = _graph.numVertices();
        int i;
		double dd = (double) numv;	// save some typing
		double denom = (((dd - 1.0) * (dd - 1.0) * (dd - 2.0) / 2.0));

        if (denom == 0.0) {
            return 0.0;
        }
        double max = -1.0;
        double grp_betweenness = 0.0;

        for (i = 0; i < numv; i++) {
            if (betweenness[i] > max) {
                max = betweenness[i];
            }
        }

        for (i = 0; i < numv; i++) {
            grp_betweenness += max - betweenness[i];
        }

		if(denom == 0) {
			return 0;
		}

        return (grp_betweenness / denom);
    }

    /** Calculate the closeness centrality of a vertex. TODO: broken
     *
	 * <p><i>The measure focuses on how close an actor is to all the other
	 * actors in the set of actors.  The idea is that an actor is central if it
	 * can quickly interact with all others.  In the context of a communication
	 * relation, such actors need not rely on other actors for the relaying of
	 * information... (183) </i>
	 *
     * @param paths a matrix containing the shortest paths between each pair of vertices in a network.
     * @param ego the index of the vertex in the matrix
     * @return ego (or actor) centrality
     */
    private double ego_closeness(int ego) {
        int clo = 0;
		int numv = _graph.numVertices();

        for (int i = 0; i < numv; i++) {
            if (i != ego && shortestPathLen(ego, i) == 0.0) {
				// there is no connection between the two vertices
                clo += 100000;
            } else {
                clo += shortestPathLen(ego, i);
            }
        }

        if (clo == 0) {
            return 0;
        }
        return ((double) (numv - 1) / (double) clo);
    }

	private double [] compute_closeness() {
		double [] closeness = new double[_graph.numVertices()];
		for(int i = 0; i < _graph.numVertices(); i++) {
			closeness[i] = ego_closeness(i);
		}
		return closeness;
	}


	/** A measure of the variability of closeness measures among the vertices
	 * of a group.
	 *
     * @return group closeness
     */    
    private double grp_closeness(double [] clo) {
        int i;
        double numv = _graph.numVertices();
		double denom = ((numv - 2.0) * (numv - 1.0)) / (2.0 * numv - 3.0);
        double group_clo;
        double max = -1;

        if (denom == 0) {
            return 0.0;
        }

        for (i = 0; i < numv; i++) {
            if (clo[i] > max) {
                max = clo[i];
            }
        }

        group_clo = 0.0;
        for (i = 0; i < numv; i++) {
            group_clo += max - clo[i];
        }
        return (group_clo / denom);
    }

	/** A measure of the variability of degree measures among the vertices of a
	 * group.
     */    
    private double grp_degree(int [] degs) {
        int i;
        int numv = _graph.numVertices();
        int denom = (numv - 1) * 2 * (numv - 2);
        int max = -1;
        double group_deg;

        if (denom == 0) {
            return 0.0;
        }

        for (i = 0; i < numv; i++) {
            if (degs[i] > max) {
                max = degs[i];
            }
        }

        group_deg = 0.0;
        for (i = 0; i < numv; i++) {
            group_deg += max - degs[i];
        }

        return ((double) group_deg / (double) denom);
    }

    /** Calculate the indegree measure for a vertex.
     *
	 * <p><i>... the indegrees are measures of </i>receptivity<i>, or
	 * </i>popularity<i>.  If we consider the sociometric relation of
	 * friendship ... [an] actor with a large indegree is one whom many others
	 * nominate as a friend, and an actor with a small indegree is chosen by
	 * few others. (126) </i>
     */
	private int [] compute_indegree() {
		int [] indeg = new int[_graph.numVertices()];
		for(int i = 0; i < _graph.numVertices(); i++) {
			indeg[i] = _vtx_index_tbl[i].inEdges().size();
		}
		return indeg;
	}

    /** Calculate the outdegree measure for a vertex.
     *
	 * <p><i>Outdegrees are measures of </i>expansiveness<i>... If we consider
	 * the sociometric relation of friendship, and actor with a large outdegree
	 * is one who nominates many others as friends. (126)</i>
     */    
	private int [] compute_outdegree() {
		int [] outdeg = new int[_graph.numVertices()];
		for(int i = 0; i < _graph.numVertices(); i++) {
			outdeg[i] = _vtx_index_tbl[i].outEdges().size();
		}
		return outdeg;
	}

    /** Calculate the total degree of a vertex.  Higher degree indicates higher
     * interaction with others in the group.
     *
	 * <p><i>The </i>degree<i> of a node ... is the number of lines that are
	 * incident with it... Degrees are very easy to compute, and yet can be
	 * quite informative in many applications. (100)</i>
     */
    private int [] compute_totaldegree() {
		int [] deg = new int[_graph.numVertices()];
		for(int i = 0; i < _graph.numVertices(); i++) {
			Vertex v = _vtx_index_tbl[i];
			if (v.inEdges().equals(v.outEdges())) {
				// undirected graph
				deg[i] = v.inEdges().size();
			} else {
				// directed
				deg[i] = v.inEdges().size() + v.outEdges().size();
			}
		}
		return deg;
    }
}
