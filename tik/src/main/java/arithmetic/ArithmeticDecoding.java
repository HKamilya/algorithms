package arithmetic;

import java.io.*;
import java.math.BigDecimal;
import java.math.MathContext;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.zip.GZIPInputStream;

public class ArithmeticDecoding {
    public String main(String filename) throws IOException {
        LinkedHashMap<String, Double> sortedMap = new LinkedHashMap<>();
        byte[] bytes = Files.readAllBytes(Paths.get(filename));
        String code = decompress(bytes);
        Path path = Paths.get(filename);
        File file = new File(path.getParent() + File.separator + "map.txt");
        Scanner sc = new Scanner(file);
        int length = Integer.parseInt(sc.nextLine());
        String line ;
        while (sc.hasNextLine()) {
            line = sc.nextLine();
            String[] parts = line.split("';'");
            sortedMap.put(parts[0], Double.valueOf(parts[1]));
        }
        sc.close();

        ArithmeticDecoding decoding = new ArithmeticDecoding();
        filename = path.getParent() + File.separator + "output.txt";
        String word = decoding.decode(code, sortedMap, length);
        BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
        writer.write(word);
        writer.close();
        return filename;
    }

    public BigDecimal pow(int value, int powValue) {
        BigDecimal result = BigDecimal.valueOf(1);
        for (int i = 1; i <= powValue; i++) {
            result = result.multiply(BigDecimal.valueOf(value));
        }
        return result;
    }

    public String decode(String code, LinkedHashMap<String, Double> symbols, int length) {
        StringBuilder word = new StringBuilder();
        BigDecimal codeSum = BigDecimal.valueOf(0);
        int j = 1;
        for (int i = 0; i < code.length(); i++) {
            if (code.charAt(i) == '1') {
                codeSum = codeSum.add(BigDecimal.valueOf(1).divide(pow(2, j), MathContext.DECIMAL128));
            }
            j++;
        }
        BigDecimal upperBound;
        BigDecimal lowerBound = BigDecimal.valueOf(0);
        HashMap<String, Node> nodes = new HashMap<>();
        for (Map.Entry<String, Double> entry : symbols.entrySet()) {
            nodes.put(entry.getKey(), Node.builder()
                    .lowerBound(lowerBound)
                    .upperBound(lowerBound.add(BigDecimal.valueOf(entry.getValue())))
                    .prob(entry.getValue())
                    .build());
            lowerBound = lowerBound.add(BigDecimal.valueOf(entry.getValue()));
        }
        BigDecimal bound;
        for (int i = 0; i < length; i++) {
            for (Map.Entry<String, Node> node : nodes.entrySet()) {
                if (codeSum.compareTo(node.getValue().getLowerBound()) >= 0 & codeSum.compareTo(node.getValue().getUpperBound()) < 0) {
                    if (node.getKey().equals("/n")) {
                        word.append("\n");
                    } else {
                        word.append(node.getKey());
                    }
                    upperBound = node.getValue().getUpperBound();
                    bound = lowerBound = node.getValue().getLowerBound();
                    BigDecimal r = upperBound.subtract(lowerBound);
                    for (Map.Entry<String, Node> symbol : nodes.entrySet()) {
                        lowerBound = bound;
                        Node temp = symbol.getValue();
                        temp.setLowerBound(lowerBound);
                        temp.setUpperBound(lowerBound.add(BigDecimal.valueOf(temp.getProb()).multiply(r)));
                        bound = temp.getUpperBound();
                        symbol.setValue(temp);
                    }
                    break;
                }
            }
        }
        System.out.println(word);
        return word.toString();
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
