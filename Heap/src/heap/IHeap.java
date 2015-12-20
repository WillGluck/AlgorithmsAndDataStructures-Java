package heap;

public interface IHeap<T> {
	
	public void insert(int key, T information) throws Exception;
	
	public boolean isEmpty();
	
	public T pop() throws Exception;

}
