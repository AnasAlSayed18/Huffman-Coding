
import java.io.*;
import java.text.DecimalFormat;
import java.util.*;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Main extends Application {
	BorderPane root;
	Scene scene;
	File file, compressedFile;
	FileChooser fc;
	Button btCompress, btDecompress, btShowStatistics;
	Label lblFile, freqTable, fileHeader;
	Stage seconderyStage;
	Heap heap;
	FileInputStream in;
	byte bytes[];
	int[] charFreq;
	String[] codes;
	Node huffmanRoot;
	LeafNode[] list;
	int listSize = 0; // To keep track of dynamic additions
	int[] nodeHeaders;// array of assci of the leaf nodes chart
	int nodeHeadersSize = 0;
	VBox vbox, v1, v2;
	HBox h1, h2;

	ListView<LeafNode> freqTableList = new ListView<>();
	ListView<String> headerList = new ListView<>();
	DecimalFormat decimalFormat = new DecimalFormat("#.###");
	FileOutputStream fos;
	BufferedInputStream bis;

	int hufTreeCount, headerSize, index, offset, pointer;
	String preOrder, hs;
	double total;

	@Override
	public void start(Stage primaryStage) {

		root = new BorderPane();
		prepareFile();

		scene = new Scene(root, 800, 565);
		scene.getStylesheets().add(getClass().getResource("listview-style.css").toExternalForm()); // Apply CSS
		scene.getStylesheets().add(getClass().getResource("hacker-style.css").toExternalForm()); // Apply hacker theme

		root.setStyle("-fx-background: #21211f;"); // Dark background for the pane
		primaryStage.setTitle("𝗛𝗨𝗙𝗙𝗠𝗔𝗡 𝗣𝗥𝗢𝗝𝗘𝗖𝗧");
		primaryStage.getIcons().add(new Image("img/logo.png", 300, 300, false, false));
		primaryStage.setScene(scene);
		primaryStage.setResizable(true);
		primaryStage.show();

	}

	private void prepareFile() {
		btCompress = new Button("Compress");
		btDecompress = new Button("Decompress");
		btShowStatistics = new Button("Show statistics");
		btShowStatistics.setDisable(true);
		btCompress.setOnAction(e -> chooseFileCompress());
		btDecompress.setOnAction(e -> chooseFileDecompress());
		btShowStatistics.setOnAction(e -> statisticsBt());

		lblFile = new Label("Choose File To :");
		freqTable = new Label("Frequency Table");
		fileHeader = new Label("File Header");


		lblFile.setContentDisplay(ContentDisplay.RIGHT);
		lblFile.setLayoutX(350);
		lblFile.setLayoutY(50);

		vbox = new VBox();
		vbox.setAlignment(Pos.CENTER);
		vbox.setSpacing(30);
		vbox.setOpaqueInsets(new Insets(10));
		v1 = new VBox();
		v1.setAlignment(Pos.CENTER);
		v1.setSpacing(5);

		v2 = new VBox();
		v2.setAlignment(Pos.CENTER);
		v2.setSpacing(5);
		h1 = new HBox();
		h1.setAlignment(Pos.CENTER);
		h1.setSpacing(5);
		h2 = new HBox();
		h2.setAlignment(Pos.CENTER);
		h2.setSpacing(5);

		h1.getChildren().addAll(btCompress, btDecompress);
		v1.getChildren().addAll(freqTable, freqTableList);
		v2.getChildren().addAll(fileHeader, headerList);
		h2.getChildren().addAll(v1, v2);
		v1.setMaxHeight(200);
		v1.setMinWidth(440);
		v2.setMaxHeight(200);
		v2.setMinWidth(200);

		vbox.getChildren().addAll(lblFile, h1, h2, btShowStatistics);

		root.setCenter(vbox);

		btCompress.setLayoutX(300);
		btCompress.setLayoutY(100);
		btDecompress.setLayoutX(400);
		btDecompress.setLayoutY(100);
		btDecompress.setMinWidth(80);
		btCompress.setMinWidth(80);

	}

	public void statisticsBt() {
		Stage stage = new Stage();

		// Root Pane with Styling
		BorderPane root = new BorderPane();
		root.setStyle("-fx-background-color: #21211f; -fx-padding: 20px;");

		// VBox for Vertical Layout
		VBox vbox = new VBox(20); // 20px spacing
		vbox.setAlignment(Pos.CENTER);

		// Title Label
		Label lblTitle = new Label("File Statistics");
		lblTitle.setStyle("""
        -fx-font-size: 28px;
        -fx-text-fill: #00ff00;
        -fx-font-weight: bold;
        -fx-border-color: #00ff00;
        -fx-border-width: 2px;
        -fx-padding: 10px;
        -fx-background-color: #21211f;
    """);

		// Size Before and After
		double strSizeBefore = Double.parseDouble(calculateBeforeTotalSize());
		double strSizeAfter = Double.parseDouble(calculateAfterTotalSize());
		double ratioValue = ((strSizeBefore - strSizeAfter) / strSizeBefore) * 100;

		// Labels for Sizes and Ratio
		Label sizeBefore = new Label("File Size Before: " + strSizeBefore + " KB");
		sizeBefore.setStyle("""
        -fx-font-size: 18px;
        -fx-text-fill: #00cc00;
        -fx-border-color: #00cc00;
        -fx-border-width: 1px;
        -fx-padding: 5px;
        -fx-background-color:#21211f;
    """);

		Label sizeAfter = new Label("File Size After: " + strSizeAfter + " KB");
		sizeAfter.setStyle("""
        -fx-font-size: 18px;
        -fx-text-fill: #00cc00;
        -fx-border-color: #00cc00;
        -fx-border-width: 1px;
        -fx-padding: 5px;
        -fx-background-color:#21211f;
    """);

		Label ratio = new Label("Compression Ratio: " + decimalFormat.format(ratioValue) + "%");
		ratio.setStyle("""
        -fx-font-size: 20px;
        -fx-text-fill: #00ff00;
        -fx-border-color: #00ff00;
        -fx-border-width: 1px;
        -fx-padding: 5px;
        -fx-background-color:#21211f;
    """);

		// PieChart for Visual Representation
		PieChart chart = new PieChart();
		//chart.setStyle("-fx-background-color: #00FF00; -fx-text-fill: #7c7c7c;");
		chart.setTitle("File Size Distribution");


		PieChart.Data beforeData = new PieChart.Data("Size Before", strSizeBefore);
		PieChart.Data afterData = new PieChart.Data("Size After", strSizeAfter);

		chart.getData().addAll(beforeData, afterData);

		// Adding Labels and Chart to VBox
		vbox.getChildren().addAll(lblTitle, sizeBefore, sizeAfter, ratio, chart);

		// Set VBox to Root
		root.setCenter(vbox);

		// Scene and Stage Setup
		Scene statisticsScene = new Scene(root, 600, 600);
		statisticsScene.getStylesheets().add(getClass().getResource("listview-style.css").toExternalForm()); // Apply custom CSS
		statisticsScene.getStylesheets().add(getClass().getResource("hacker-style.css").toExternalForm()); // Apply hacker theme

		stage.setTitle("𝗛𝗨𝗙𝗙𝗠𝗔𝗡 𝗣𝗥𝗢𝗝𝗘𝗖𝗧");
		stage.getIcons().add(new Image("img/logo.png", 300, 300, false, false));
		stage.setScene(statisticsScene);
		stage.setResizable(false);
		stage.show();
	}




	private void chooseFileCompress() {
		btDecompress.setText("Decompress");

		headerList.getItems().clear();
		freqTableList.getItems().clear();

		fc = new FileChooser();
		fc.getExtensionFilters().removeAll();
		fc.getExtensionFilters()
				.addAll(new ExtensionFilter("Include", "*.data", "*.txt", "*.docx", "*.pdf", "*.java", "*.cpp", "*.py",
						"*.log", "*.html", "*.css", "*.xml", "*.png", "*.jpg", "*.json", "*.gif", "*.mp3", "*.wav",
						"*.xlsx", "*.csv", "*.mp4", "*.mkv", "*.mov", "*.zip", "*.rar", "*.prx", "*.exe"));
		file = fc.showOpenDialog(seconderyStage);

		if (file == null) {
			errorM("No File has been selected");
		} else {
			btCompress.setText(file.getName());
			readFile();
			writeHeader();
			writeDataOnFile(); // Compresses to temporary file
			btShowStatistics.setDisable(false);

		}
	}

	private void writeHeader() {
		try {
			total = 0;//number of bit for header

			// Open FileChooser to select the save location
			FileChooser saveFileChooser = new FileChooser();
			saveFileChooser.setInitialFileName(file.getName().split("\\.")[0] + ".huf");
			saveFileChooser.getExtensionFilters().add(new ExtensionFilter("Huffman Compressed Files", "*.huf"));

			File saveLocation = saveFileChooser.showSaveDialog(seconderyStage);

			if (saveLocation == null) {
				errorM("Save operation cancelled. No file selected.");
				return;
			}

			compressedFile = saveLocation;
			compressedFile.createNewFile();
			PrintWriter pw = new PrintWriter(compressedFile);


			preOrder = toBits();// Pre-order Huffman tree as bits
			headerSize = (preOrder.length() % 8) == 0 ? preOrder.length() / 8 : (preOrder.length() / 8) + 1;
			headerSize += (file.getName().split("\\.")[1].length());//extension length + preOrder length

			int lengthOfHeaderSize= String.valueOf(headerSize).length();//calculated number of digits in headerSize.
			hs = String.valueOf(headerSize + lengthOfHeaderSize + 1);//extension length + preOrder length + headerSize length + 1

			pw.print(file.getName().split("\\.")[1]); // Write the extension
			pw.print(lengthOfHeaderSize); // Write the length of header size
			pw.print(hs); // Write the header size

			pw.flush();
			pw.close();
			fos = new FileOutputStream(compressedFile, true);

			//Write PreOrder on header
			int currByte = 0;
			int count = 0;
			for (int i = 0; i < preOrder.length(); i++) {
				char bit = preOrder.charAt(i);
				if (count == 8) {
					fos.write(currByte);
					count = 0;
					currByte = 0;
				}
				if (bit == '1')
					currByte = (currByte <<= 1) | 1;// Set the bit to 1
				else
					currByte <<= 1;// Shift left if the bit is 0
				count++;
			}
			if (count > 0) {
				currByte <<= (8 - count);// Adjust for remaining bits
				fos.write(currByte);
				fos.flush();
			}
			fos.flush();

			headerList.getItems().addAll(
					"File Extension : " + file.getName().split("\\.")[1],
					"Header Size : " + hs,
					"Huffman Tree : " + preOrder
			);
			headerList.setMaxHeight(100);
			headerList.setMinWidth(300);

		} catch (FileNotFoundException e) {
			errorM(e.getMessage());
		} catch (IOException e) {
			errorM(e.getMessage());
		}
	}


	//The toBits method is responsible for converting the preOrder tree
	// into a binary string representation.
	private String toBits() {

		String bits = "";
		for (int i = 0; i < preOrder.length(); i++) {
			char c = preOrder.charAt(i);

			if (c == '0')
				bits += "0";


			else if (c == '1') {
				bits += "1";
				String inBinary = Integer.toBinaryString(nodeHeaders[(pointer)]);//appends the assci binary representation of the node in the tree

				for (int j = 0; j < 8 - inBinary.length(); j++)//It ensures that each byte is 8 bits long
					bits += "0";//(turning 101 ----> 00000101)

				bits += inBinary;
				i += String.valueOf(nodeHeaders[(pointer++)]).length();
			}

		}
		return bits;
	}

	private void writeDataOnFile() {
		try {

			FileInputStream in = new FileInputStream(file);

			int currByte = 0;
			int count = 0;
			while (in.available() > 0) {
				bytes = in.readNBytes(8);
				for (int i = 0; i < bytes.length; i++) {

					char[] bits = codes[Byte.toUnsignedInt(bytes[i])].toCharArray();
					for (char bit : bits) {
						if (bit == '1')
							currByte = (currByte << 1) | 1;
						else
							currByte <<= 1;
						count++;
						if (count == 8) {
							fos.write(currByte);
							currByte = 0;
							count = 0;
						}

					}

				}

			}
			if (count > 0) {
				currByte <<= (8 - count);
				fos.write(currByte);
				fos.flush();
				total += 8;
			}
			fos.write(8 - count);//ofset value
			total += 8;
			fos.flush();

			fos.close();
			infoM(file.getName() + " compressed successfully.");

		} catch (IOException e) {
		}
		catch (NullPointerException e) {
return ;
		}

	}

	private void readFile() {
		preOrder = "";//preOrder: Will store the preorder traversal of the Huffman tree.
		hufTreeCount = 0;//Tracks the size of the Huffman tree (number of bits).
		charFreq = new int[256];//Array to count the frequency of each character (ASCII range: 0–255).
		codes = new String[256];//Array to store the Huffman binary code for each character
		listSize=0;
		headerSize=0;
		offset=0;
		pointer=0;
		nodeHeadersSize=0;
		list = new LeafNode[256]; //Array of LeafNode objects representing characters and their frequencies.
		nodeHeaders = new int[256]; // Similarly, assuming max size
		heap = new Heap(256);//Min-heap data structure used to construct the Huffman tree.

		try {
			//Count Character Frequencies
			in = new FileInputStream(file);
			while (in.available() > 0) {
				bytes = in.readNBytes(8);
				for (int i = 0; i < bytes.length; i++) {
					charFreq[Byte.toUnsignedInt(bytes[i])]++;
				}
			}
			// Insert all the nodes with non-zero frequencies into the heap
			for (int i = 0; i < charFreq.length; i++) {
				if (charFreq[i] != 0) {
					heap.insert(new LeafNode((byte) i, charFreq[i]));
				}
			}

			createHufTree();//creates the Huffman tree

			//Generate Huffman Codes
			//If the Huffman tree has more than one node.
			if (!(huffmanRoot instanceof LeafNode)) {
				generateHufCode(huffmanRoot, "");
			}

			//If the tree has only one node.
			else {
				generateHufCode(huffmanRoot, "");
				((LeafNode) huffmanRoot).setCode("0");
				codes[((LeafNode) huffmanRoot).getCharacter()] = "0";
			}

			// Sort the charFreq  based on frequencies
			sortListByFrequency();

			// Adds the sorted LeafNode objects to the ListView component (freqTableList),
			freqTableList.getItems().addAll(Arrays.copyOfRange(list, 0, listSize));
			freqTableList.setMaxHeight(150);
			freqTableList.setMinWidth(440);



		} catch (IOException e) {
			errorM(file.getName() + " not found.");
		}
	}



	// Add sorting logic for `list` array
	private void sortListByFrequency() {
		for (int i = 0; i < listSize - 1; i++) {
			for (int j = 0; j < listSize - i - 1; j++) {
				if (list[j].getFreq() < list[j + 1].getFreq()) {
					LeafNode temp = list[j];
					list[j] = list[j + 1];
					list[j + 1] = temp;
				}
			}
		}
	}
	//method converts a binary string into its corresponding ASCII
	private char toChar(String binaryValue) {
		int value = 0;
		for (int i = binaryValue.length() - 1; i >= 0; i--) {
			char c = binaryValue.charAt(binaryValue.length() - i - 1);
			if (c == '1')
				value += Math.pow(2, i);
		}
		return (char) value;
	}

	private String calculateAfterTotalSize() {
		for (int i = 0; i < listSize; i++)
			total += (list[i].freq * list[i].getCode().length());

		total = total / 8.0;
		String result = decimalFormat.format(((total) / 1024.0) + (Integer.valueOf(hs) / 1024.0));//total / 1024.0: Converts bytes to KB.
		return result;
	}

	private String calculateBeforeTotalSize() {
		double total = 0;
		for (int i = 0; i < listSize; i++) {
			total += list[i].freq * 8;
		}
		total = total / 8.0;
		String result = decimalFormat.format(total / 1024.0);//total / 1024.0: Converts bytes to KB.
		return result;
	}

	private void generateHufCode(Node node, String code) {
		if (node == null)
			return;

		if (node instanceof LeafNode) {
			((LeafNode) node).setCode(code);// Assign the Huffman code to the leaf node.

			// Add to list
			list[listSize++] = (LeafNode) node;// Add the leaf node to the list array.

			codes[Byte.toUnsignedInt(((LeafNode) node).getCharacter())] = code; // Save the code for this character.
			preOrder += "1" + Byte.toUnsignedInt(((LeafNode) node).getCharacter());// Add "1" and the character to the preorder string.
			hufTreeCount++;
			nodeHeaders[nodeHeadersSize++] = Byte.toUnsignedInt(((LeafNode) node).getCharacter());
			return;
		}

		//Process Internal Nodes
		preOrder += "0";
		hufTreeCount++;
		generateHufCode(node.getLeft(), code + "0");
		generateHufCode(node.getRight(), code + "1");
	}


	private void createHufTree() {

		huffmanRoot = null;
		if (heap.size() > 1)
			while (!heap.isEmpty()) {
				Node leftNode = heap.deleteMin();
				if (heap.isEmpty()) {
					huffmanRoot = leftNode;
					break;
				}
				Node rightNode = heap.deleteMin();
				Node parent = new Node(leftNode, rightNode);
				heap.insert(parent);
			}

		else if (heap.size() == 1) {
			LeafNode n = (LeafNode) heap.deleteMin();
			huffmanRoot = new LeafNode(n.getCharacter(), n.freq);
		}
	}

	private void chooseFileDecompress() {
		btCompress.setText("Compress");

		fc = new FileChooser();
		fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Huffman File", "*.huf"));
		file = fc.showOpenDialog(seconderyStage);

		if (file == null)
			errorM("No File has been selected");
		else {
			btDecompress.setText(file.getName());
			index=0;
			infoM("Please wait until decompressing of "+file.getName() + " is finished");
			readCompressedFile();
			infoM(  file.getName()+" has been decompressed successfully");
		}

	}

	private void readCompressedFile() {
		try {
			headerList.getItems().clear();


			FileInputStream fis = new FileInputStream(file);
			 bis = new BufferedInputStream(fis);
			int c = 0;
			String extension = ".";
			int counter = 0;
			bis.mark(10);

			//This loop reads the extension character by character until a digit is encountered.
			while (!Character.isDigit(c = bis.read())) {
				extension += ((char) c);
				counter++;
			}
			//After finding the extension, the stream is reset and skips the number of bytes read to reach the next part.
			headerList.getItems().add(new String("Original File Extension : " + extension));
			bis.reset();
			bis.skip(counter);

			String hederSizeLength = new String(bis.readNBytes(1));
			int sizeLength = Integer.valueOf(hederSizeLength);
			int hederSize = Integer.valueOf(new String(bis.readNBytes(sizeLength)));
			headerList.getItems().add(new String("Original File Header Size : " + hederSize));

			// Read the Huffman tree's pre-order traversal
			StringBuilder hufTreePreOrder = new StringBuilder();
			int preOrderLength = hederSize - extension.length() - sizeLength; // Total bytes to read for the Huffman tree
			byte[] buffer = new byte[8]; // Adjust the buffer size as needed
			int totalBytesRead = 0;

			while (totalBytesRead < preOrderLength) {
				int bytesRead = bis.read(buffer, 0, Math.min(buffer.length, preOrderLength - totalBytesRead));
				if (bytesRead == -1) break; // End of stream

				for (int b = 0; b < bytesRead; b++) {
					int byte1 = buffer[b] & 0xFF; // Get unsigned byte value
					for (int bit = 7; bit >= 0; bit--) {
						int currBit = 1 << bit;
						int result = (byte1 & currBit) == 0 ? 0 : 1;
						hufTreePreOrder.append(result);
					}
				}
				totalBytesRead += bytesRead;
			}

			//Trim Extra Bits from Last Byte.
			if (totalBytesRead == preOrderLength) {
				int remainingBits = 8 - offset; // Calculates remaining bits in the last byte.
				hufTreePreOrder.setLength(hufTreePreOrder.length() - offset);
			}

			headerList.getItems().add(new String("Original File PreOrder : " + hufTreePreOrder));
			headerList.setMaxHeight(100);
			headerList.setMinWidth(200);

			//Read the encoded data.
			StringBuilder allData = new StringBuilder();
			byte[] buffer2 = new byte[8]; // Buffer size can be adjusted
			int bytesRead;

              // Read chunks of data into the buffer
			while ((bytesRead = bis.read(buffer2)) != -1) {
				for (int b = 0; b < bytesRead; b++) {
					int byte1 = buffer2[b] & 0xFF; // Get unsigned byte value
					for (int bit = 7; bit >= 0; bit--) {
						int currBit = 1 << bit;
						int result = (byte1 & currBit) == 0 ? 0 : 1;
						allData.append(result);// Appends bits to the encoded data string.
					}
				}

				// Break if only one byte is left to process the padding
				if (bis.available() == 1) {
					break;
				}
			}

			// Read the offset (padding)
			offset = bis.read(); // This should be the last byte, containing the padding info

			// Trim any extra bits from the last byte using the offset
			if (offset > 0) {
				allData.setLength(allData.length() - offset);
			}


			//Decode the data
			decode(hufTreePreOrder.toString(), allData.toString(), extension);
			fis.close();

		} catch (FileNotFoundException e) {
			errorM(e.getMessage());

		} catch (IOException e) {
			errorM(e.getMessage());
		}
	}

	private void decode(String hufTreePreOrder, String data, String extension) throws IOException {
		// Create a New File for the Uncompressed Output:
		File unCompressedFile = new File(file.getParent(), file.getName().split("\\.")[0] + extension);
		unCompressedFile.createNewFile();

		// Use FileOutputStream for writing
		try (FileOutputStream pw = new FileOutputStream(unCompressedFile)) {

			// Reconstruct the Huffman Tree:
			Node root = constructTree(hufTreePreOrder.toCharArray());

			if (root.getLeft() == null && root.getRight() == null) {
				byte[] buffer = new byte[data.length() - offset];
				for (int i = 0; i < buffer.length; i++) {
					buffer[i] = (byte) ((LeafNode) root).getCharacter();
				}
				pw.write(buffer);
				return;
			}

			// Decode the data using the Huffman tree
			Node curr = root;
			byte[] buffer = new byte[8];  // Adjust buffer size as needed
			int bufferIndex = 0;

			// Ensure that the loop does not exceed the bounds of the data string
			for (int i = 0; i < data.length() - offset && i < data.length(); i++) {
				char c = data.charAt(i);
				if (curr instanceof LeafNode) {
					buffer[bufferIndex++] = (byte) ((LeafNode) curr).getCharacter();
					curr = root;

					// When the buffer reaches its limit, write it to the file and reset the index
					if (bufferIndex >= buffer.length) {
						pw.write(buffer, 0, bufferIndex);
						bufferIndex = 0;
					}
				}

				// Traverse left or right based on the encoded bit
				if (c == '0') {
					curr = curr.getLeft();
				} else if (c == '1') {
					curr = curr.getRight();
				}
			}

			// Write any remaining bytes in the buffer
			if (bufferIndex > 0) {
				pw.write(buffer, 0, bufferIndex);
			}

		} catch (IOException e) {
			errorM("Error during decompression: " + e.getMessage());
		}
	}

	private Node constructTree(char[] preOrder) {
		if (preOrder == null || index >= preOrder.length) {
			return null;
		}

		char value = preOrder[index++];
		Node node = null;
		if (value == '1') {
			String binary = "";
			for (int i = index; i < index + 8; i++) {
				binary += preOrder[i];
			}
			index += 8;
			LeafNode l = new LeafNode(convertBitStringToByte(binary), 0);
			return l;
		} else if (value == '0') {

			node = new Node();
			node.setLeft(constructTree(preOrder));
			node.setRight(constructTree(preOrder));
		}

		return node;
	}

	public byte convertBitStringToByte(String bitString) {

		byte result = 0;
		for (int i = 0; i < 8; i++) {
			char bit = bitString.charAt(i);
			result |= (bit - '0') << (7 - i);
		}
		return result;
	}

	public void errorM(String errorMessage) {

		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle("⚠ ERROR MESSAGE ⚠");

		// Header with hacker-style theme
		Text header = new Text("𝗛𝗨𝗙𝗙𝗠𝗔𝗡 𝗣𝗥𝗢𝗝𝗘𝗖𝗧");
		header.setFont(Font.font("Monospaced", FontWeight.EXTRA_BOLD, 20));
		header.setStyle("-fx-fill: #00FF00; -fx-effect: dropshadow(one-pass-box, #00FF00, 3, 0.8, 0, 1);");
		alert.setHeaderText(null);

		// Add custom header to dialog pane
		DialogPane dialogPane = alert.getDialogPane();
		dialogPane.setHeader(header);

		// Customize content text
		Text content = new Text("❗ " + errorMessage + " ❗");
		content.setFont(Font.font("Monospaced", FontWeight.BOLD, 16));
		content.setStyle("-fx-fill: #00FF00; -fx-effect: innershadow(one-pass-box, #00FF00, 2, 0.8, 0, 1);");
		dialogPane.setContent(content);

		// Set dialog pane styling for hacker green theme
		dialogPane.setStyle("-fx-background-color: #21211f; " +
				"-fx-border-color: #00FF00; -fx-border-radius: 10; -fx-padding: 20px;");

		// Style OK button
		dialogPane.lookupButton(ButtonType.OK).setStyle(
				"-fx-background-color: #00FF00; -fx-text-fill: #21211f; -fx-font-weight: bold; -fx-background-radius: 5;");

		alert.showAndWait();
	}

	public void infoM(String informationMessage) {

		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("⚠ INFORMATION MESSAGE ⚠");

		// Header with hacker-style theme
		Text header = new Text("𝗛𝗨𝗙𝗙𝗠𝗔𝗡 𝗣𝗥𝗢𝗝𝗘𝗖𝗧");
		header.setFont(Font.font("Monospaced", FontWeight.EXTRA_BOLD, 20));
		header.setStyle("-fx-fill: #00FF00; -fx-effect: dropshadow(one-pass-box, #00FF00, 3, 0.8, 0, 1);");
		alert.setHeaderText(null);

		// Add custom header to dialog pane
		DialogPane dialogPane = alert.getDialogPane();
		dialogPane.setHeader(header);

		// Customize content text
		Text content = new Text("❗ " + informationMessage + " ❗");
		content.setFont(Font.font("Monospaced", FontWeight.BOLD, 16));
		content.setStyle("-fx-fill: #00FF00; -fx-effect: innershadow(one-pass-box, #00FF00, 2, 0.8, 0, 1);");
		dialogPane.setContent(content);

		// Set dialog pane styling for hacker green theme
		dialogPane.setStyle("-fx-background-color: #21211f; " +
				"-fx-border-color: #00FF00; -fx-border-radius: 10; -fx-padding: 20px;");

		// Style OK button
		dialogPane.lookupButton(ButtonType.OK).setStyle(
				"-fx-background-color: #00FF00; -fx-text-fill: #21211f; -fx-font-weight: bold; -fx-background-radius: 5;");

		alert.showAndWait();
	}


	public static void main(String[] args) {
		launch(args);
	}
}
