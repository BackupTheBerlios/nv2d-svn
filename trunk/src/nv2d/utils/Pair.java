package nv2d.utils;

public class Pair {
	protected Object _car;
	protected Object _cdr;

	public Pair(Object a, Object b) {
		_car = a;
		_cdr = b;
	}

	public Object car() {
		return _car;
	}

	public Object cdr() {
		return _cdr;
	}

	public void setCar(Object o) {
		_car = o;
	}

	public void setCdr(Object o) {
		_cdr = o;
	}

	public boolean contains(Object o) {
		if(o.equals(_car) || o.equals(_cdr)) {
			return true;
		}
		return false;
	}

	public boolean equals(Object o) {
		if(o.getClass() != Pair.class) {
			return false;
		}

		return (_car.equals(((Pair) o)._car) && _cdr.equals(((Pair) o)._cdr));
	}
}
