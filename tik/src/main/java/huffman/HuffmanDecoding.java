package huffman;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.zip.GZIPInputStream;

public class HuffmanDecoding {
    public String main(String filename) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(filename));
        String line = decompress(bytes);
        Path path = Paths.get(filename);
        HuffmanDecoding decoding = new HuffmanDecoding();
        ObjectMapper mapper = new ObjectMapper();
        Node root = mapper.readValue(new File(path.getParent() + File.separator + "tree.json"), Node.class);
        String decode = decoding.decode(line, root);
        filename = path.getParent() + File.separator + "output.txt";
        BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
        writer.write(decode);
        writer.close();
        return filename;
    }


    public String decode(String code, Node root) {
        String text = "";
        Node temp = root;
        StringBuilder textBuilder = new StringBuilder(text);
        for (int i = 0; i < code.length(); i++) {
            if (temp.getRightNode() == null && temp.getLeftNode() == null) {
                textBuilder.append(temp.getSymbol());
                temp = root;
            }
            if (code.charAt(i) == '0') {
                temp = temp.getLeftNode();
            }
            if (code.charAt(i) == '1') {
                temp = temp.getRightNode();
            }

        }
        textBuilder.append(temp.getSymbol());
        return textBuilder.toString();
    }

    public static String decompress(byte[] compressed) throws IOException {
        final StringBuilder outStr = new StringBuilder();
        if ((compressed == null) || (compressed.length == 0)) {
            return "";
        }
        if (isCompressed(compressed)) {
            final GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(compressed));
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(gis, StandardCharsets.UTF_8));

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                outStr.append(line);
            }
        } else {
            outStr.append(Arrays.toString(compressed));
        }
        return outStr.toString();
    }

    public static boolean isCompressed(final byte[] compressed) {
        return (compressed[0] == (byte) (GZIPInputStream.GZIP_MAGIC)) && (compressed[1] == (byte) (GZIPInputStream.GZIP_MAGIC >> 8));
    }
}
