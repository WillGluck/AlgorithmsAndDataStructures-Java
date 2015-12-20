package heap;

public class HeapTest {

	public static void main(String[] args) throws Exception {
		
		MinHeap<String> heap = new MinHeap<String>(10);
		heap.insert(2, "William");
		heap.insert(1, "Alini");
		heap.insert(3, "Andressa");
		heap.insert(0, "first");
		heap.insert(4, "teste");
		
		String first = heap.getMin();
		System.out.println(first);
		first = heap.getMin();
		System.out.println(first);
		first = heap.getMin();
		System.out.println(first);
		first = heap.getMin();
		System.out.println(first);
		first = heap.getMin();
		System.out.println(first);
		//first = heap.getMin();
		//System.out.println(first);

	}

}
