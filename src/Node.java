
public class Node implements Comparable<Node> {

	protected int freq;
	private Node left;
	private Node right;

	public Node() {
	}

	public Node(Node left , Node right) {
		this.left = left;
		this.right = right;
		this.freq = left.freq + right.freq;
	}

	@Override
	public int compareTo(Node o) {
		return Integer.compare(freq, o.freq);
	}

	public int getFreq() {
		return freq;
	}

	public void setFreq(int freq) {
		this.freq = freq;
	}

	public Node getLeft() {
		return left;
	}

	public void setLeft(Node left) {
		this.left = left;
	}

	public Node getRight() {
		return right;
	}

	public void setRight(Node right) {
		this.right = right;
	}

	@Override
	public String toString() {
		return freq + "";
	}

}
