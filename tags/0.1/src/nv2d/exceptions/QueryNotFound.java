package nv2d.exceptions;

import java.lang.RuntimeException;

public class QueryNotFound extends RuntimeException {
	public QueryNotFound(String query, String loc) {
		super("[" + query + "] not found in " + loc);
	}
}
