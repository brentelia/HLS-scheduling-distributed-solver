package util;
public class Pair<T, E> implements Comparable{
	T first;
	E second;

	public T getFirst() {
		return first;
	}

	public void setFirst(T first) {
		this.first = first;
	}

	public E getSecond() {
		return second;
	}

	public void setSecond(E second) {
		this.second = second;
	}

	public Pair(T t, E n) {
		first = t;
		second = n;
	}

	@Override
	public int compareTo(Object o) {
		if (o instanceof Pair<?, ?> && ((Pair) o).first instanceof Comparable && first instanceof Comparable) {
			Comparable f = (Comparable)((Pair) o).first;
			Comparable t = (Comparable)first;
			return t.compareTo(f);
		}
		return 0;
	}
}