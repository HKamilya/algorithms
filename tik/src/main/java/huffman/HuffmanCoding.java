package huffman;

import org.json.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.zip.GZIPOutputStream;

public class HuffmanCoding {
    private Node root;

    public String main(String filename) throws IOException {
        File file = new File(filename);
        List<String> symbols = new ArrayList<>();
        Scanner sc = new Scanner(file);
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            for (int i = 0; i < line.length(); i++) {
                symbols.add(String.valueOf(line.charAt(i)));
            }
            if (sc.hasNextLine()) {
                symbols.add("\n");
            }
        }
        sc.close();
        Map<String, Double> chars = new HashMap<>();
        for (String c : symbols) {
            Double count = chars.get(c);
            if (count == null) {
                chars.put(c, (double) 1);
            } else {
                chars.put(c, count + 1);
            }
        }
        PriorityQueue<Node> nodes = new PriorityQueue<>(chars.size(), nodeComparator);
        calculateProbability(chars, symbols.size(), nodes);
        HuffmanCoding huffman = new HuffmanCoding();
        Map<String, String> coded = huffman.encode(nodes);
        StringBuilder code = new StringBuilder();
        for (String symbol : symbols) {
            if (coded.containsKey(symbol)) {
                code.append(coded.get(symbol));
            }
        }
        System.out.println(code);
        JSONObject json = new JSONObject(huffman.getRoot());
        Path path = Paths.get(filename);
        FileWriter fileWriter = new FileWriter(path.getParent() + File.separator + "tree.json");
        fileWriter.write(json.toString());
        fileWriter.close();
        filename = path.getParent() + File.separator + "decode.txt";
        OutputStream os = new FileOutputStream(filename);
        os.write(compress(code.toString()));
        os.close();
        return filename;
    }

    public Map<String, String> encode(PriorityQueue<Node> nodes) {
        while (nodes.size() > 1) {
            Node n1 = nodes.peek();
            nodes.poll();
            Node n2 = nodes.peek();
            nodes.poll();
            Node n3 = new Node();
            n3.setProbability(n1.getProbability() + n2.getProbability());
            n3.setSymbol("null");
            n3.setLeftNode(n1);
            n3.setRightNode(n2);
            root = n3;
            nodes.add(n3);
        }
        HashMap<String, String> mapOfCodes = new HashMap<>();
        if (root != null) {
            addCode(root, "", mapOfCodes);
        }
        return mapOfCodes;
    }

    public void addCode(Node node, String code, HashMap<String, String> nodes) {
        if (node.getLeftNode() == null && node.getRightNode() == null && !node.getSymbol().equals("null")) {
            nodes.put(node.getSymbol(), code);
            return;
        }
        addCode(node.getLeftNode(), code + "0", nodes);
        addCode(node.getRightNode(), code + "1", nodes);

    }

    public Node getRoot() {
        return root;
    }

    public static void calculateProbability(Map<String, Double> chars, int length, PriorityQueue<Node> nodes) {

        for (Map.Entry<String, Double> pair : chars.entrySet()) {
            nodes.add(Node.builder()
                    .probability(pair.getValue() / length)
                    .symbol(pair.getKey())
                    .build());
        }
    }

    public static byte[] compress(String str) throws IOException {
        if ((str == null) || (str.length() == 0)) {
            return null;
        }
        ByteArrayOutputStream obj = new ByteArrayOutputStream();
        GZIPOutputStream gzip = new GZIPOutputStream(obj);
        gzip.write(str.getBytes(StandardCharsets.UTF_8));
        gzip.flush();
        gzip.close();
        return obj.toByteArray();
    }

    public static Comparator<Node> nodeComparator = Comparator.comparingDouble(Node::getProbability);


}
