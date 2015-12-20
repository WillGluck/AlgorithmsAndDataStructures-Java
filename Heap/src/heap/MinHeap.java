package heap;

/**
 * MinHeap for general use
 * 
 * @author bigwi_000
 *
 * @param <T>
 */
public class MinHeap<T> implements IHeap<T> {
	
	//Attributes
	
	private HeapElement<T>[] minHeap;	
	private int heapSize;	
	private int currentIndex;
	
	//Constructor
	
	@SuppressWarnings("unchecked")
	public MinHeap(int heapSize) { 
		this.currentIndex = -1;
		this.minHeap = new HeapElement[heapSize];
		this.heapSize = heapSize;
	}
	
	public MinHeap() {
		
	}

	//Methods
	
	@Override
	public T pop() throws Exception {
		if (this.currentIndex == -1) {
			throw new Exception("Heap is empty");
		} else {
			HeapElement<T> min = this.minHeap[0];
			this.minHeap[0] = this.minHeap[currentIndex];	
			this.minHeap[currentIndex] = null;
			currentIndex--;		
			this.heapifyDown(0);		
			return min.getInformation();
		}
	}
	
	@Override
	public void insert(int key, T information) throws Exception {		
		if (this.currentIndex == (this.heapSize-1)) {
			throw new Exception("Heap Overflow");
		} else {
			this.currentIndex++;
			this.minHeap[this.currentIndex] = new HeapElement<T>(key, information);
			this.heapifyUp(this.currentIndex);
		}
	}
	
	@Override
	public boolean isEmpty() {
		return this.currentIndex == 0;
	}
	
	//Pop the minimum value of the heap
	public T getMin() throws Exception {
		return this.pop();
	}	
	
	//Take the element up the Heap to his right position
	private void heapifyUp(int current) {
		int parent = (current - 1) / 2;
		if (parent < 0) {
			return;
		} else if (this.minHeap[current].getKey() < this.minHeap[parent].getKey()) {
			this.swapElements(current, parent);
			current = parent;
			this.heapifyUp(current);
		}
	}
	
	//Take the element down the Heap to his right position
	private void heapifyDown(int current) {
		
		int left = (2 * current) + 1;
		int right = left + 1;
		int smallerChild = 0;
		
		if (current >= this.currentIndex / 2) {
			return;
		} else if (current < this.currentIndex / 2) {
			smallerChild = this.minHeap[left].getKey() < this.minHeap[right].getKey() ? left : right;			
		} 
		if (this.minHeap[smallerChild].getKey() < this.minHeap[current].getKey()) {
			this.swapElements(current, smallerChild);
			current = smallerChild;
			this.heapifyDown(current);
		}
	}
	
	//Swap the position of the two positions
	private void swapElements(int elementOne, int elementTwo) {
		HeapElement<T> tempElementOne = this.minHeap[elementOne];
		this.minHeap[elementOne] = this.minHeap[elementTwo];
		this.minHeap[elementTwo] = tempElementOne;
	}

	//Getters and Setters	

	public HeapElement<T>[] getMinHeap() {
		return minHeap;
	}

	public void setMinHeap(HeapElement<T>[] minHeap) {
		this.minHeap = minHeap;
	}

	public int getHeapSize() {
		return heapSize;
	}

	public void setHeapSize(int heapSize) {
		this.heapSize = heapSize;
	}

	public int getCurrentIndex() {
		return currentIndex;
	}

	public void setCurrentIndex(int currentIndex) {
		this.currentIndex = currentIndex;
	}	
}
