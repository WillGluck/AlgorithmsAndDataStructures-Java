package heap;
/**
 * 
 * @author bigwi_000
 *
 * @param <T>
 */
public class HeapElement<T> {
	
	private int key;
	private T information;
	
	public HeapElement(int key, T information) {
		this.key = key;
		this.information = information;
	}	

	public int getKey() {
		return key;
	}
	
	public void setKey(int key) {
		this.key = key;
	}
	
	public T getInformation() {
		return information;
	}
	
	public void setInformation(T information) {
		this.information = information;
	}	
}