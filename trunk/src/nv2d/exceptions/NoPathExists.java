package nv2d.exceptions;

import java.lang.Exception;

import nv2d.graph.Vertex;

public class NoPathExists extends Exception {
	public NoPathExists(Vertex source, Vertex dest) {
		super("There is no path between " + source.id() + " and " + dest.id() + ".");
	}
}
