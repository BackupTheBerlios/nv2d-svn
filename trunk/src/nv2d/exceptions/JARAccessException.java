package nv2d.exceptions;

public class JARAccessException extends Exception {
	public JARAccessException(String msg) {
		super("JARAccessException: " + msg);
	}
}
