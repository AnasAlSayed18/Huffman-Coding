# Huffman Code 🗜️

## 📜 Overview
This project implements the **Huffman coding** algorithm to **compress** and **decompress** files of any type. While Huffman coding is highly efficient in many cases, please note that it may sometimes **increase the size** of certain files due to the nature of encoding.

The tool is built using **Java** with a user-friendly **JavaFX** graphical interface.

To optimize performance, this project minimizes the use of **strings**, which are resource-intensive in both memory and processing time.

---

## 🏷️ Header of Compressed File
The compressed file contains the following information:

1. **Original File Extension** 🗂️
2. **Number of Padding Bits** 📏
3. **Header Size** (in bits) 🧮
4. **Huffman Tree Structure** 🌳

The Huffman tree is stored in **Standard Tree Format (STF)** using **post-order traversal**.  
- Internal nodes are encoded as **0**,  
- Leaf nodes are encoded as **1** followed by the byte value.

### 🌲 Tree Decoding Algorithm
The **stack** is used to decode the tree format:

1. If you encounter **1**, read the byte value, create a node, and push it onto the stack.
2. If you encounter **0**, pop two nodes (pop right and pop left), create a parent node, and push it back onto the stack.
3. The final node on the stack is the **root** of the Huffman tree.

---

## 🚀 Features
- **🖥️ User-Friendly Interface**: Simple, easy-to-use graphical interface for seamless interaction.
- **📊 Compression Statistics**: Get insights into how efficient the compression is.
- **🔄 Accurate Decompression**: Files are restored to their original state without loss.
- **⚡ Efficient I/O**: Buffered reading and writing for fast and optimized performance.

---

## 📸 Screenshots
### Start Screen
![Start Screen](https://github.com/AnasAlSayed18/img/blob/3fc468d37769cf6dcc9031e92998ce4354006f62/Screenshot%202025-04-02%20052454.png)

### Compressing a File
![Compressing](https://github.com/AnasAlSayed18/img/blob/3fc468d37769cf6dcc9031e92998ce4354006f62/Screenshot%202025-04-02%20052610.png)

### Frequency Table and Header of Compressed File
![Frequency Table and Header](https://github.com/AnasAlSayed18/img/blob/3fc468d37769cf6dcc9031e92998ce4354006f62/Screenshot%202025-04-02%20052632.png)

### Compression Statistics
![Stats](https://github.com/AnasAlSayed18/img/blob/3fc468d37769cf6dcc9031e92998ce4354006f62/Screenshot%202025-04-02%20052729.png)

---

## 🎬 Demo Video
[Click here to see the demo (⌐■_■)](soon)
