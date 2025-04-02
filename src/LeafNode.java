
import java.text.DecimalFormat;

public class LeafNode extends Node {

	private byte character; 
	private String code;

	public LeafNode(byte character, int freq) {
		this.character = character;
		super.freq = freq;
	}

	public byte getCharacter() {
		return character;
	}

	public void setCharacter(byte character) {
		this.character = character;
	}

	@Override
	public String toString() {
		DecimalFormat decimalFormat = new DecimalFormat("#.###");
		String NumberAfter = decimalFormat.format((code.length() * freq) / 1024.0);
		String NumberBefore = decimalFormat.format((8 * freq) / 1024.0);
		return "Character : " + (char)this.character + ", Freq : " + super.freq  + " , Huffman Code : " + code
				+ ", Size Before : " + NumberBefore + ", Size After : " + NumberAfter + "KB";
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

}
