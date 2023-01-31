package common;

import java.util.Objects;

/*
* Container for 2 objects
* @param <K> : type of the first object
* @param <V> : type of the second object
*/
public class PairContainer<K, V> {
	private K key;
	private V value;

	/*
	* @param key   : value of the first object
	* @param value : value of the second object
	*/
	public PairContainer(K key, V value) {
		this.key = key;
		this.value = value;
	}

	public K getKey() { return this.key; }
	public void setKey(K key) { this.key = key; }

	public V getValue() { return this.value; }
	public void setValue(V value) { this.value = value;}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		PairContainer<?, ?> that = (PairContainer<?, ?>) o;
		return Objects.equals(getKey(), that.getKey()) &&
				Objects.equals(getValue(), that.getValue());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getKey(), getValue());
	}
}
