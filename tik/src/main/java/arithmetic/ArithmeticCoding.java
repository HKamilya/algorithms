package arithmetic;

import java.io.*;
import java.math.BigDecimal;
import java.math.MathContext;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.zip.GZIPOutputStream;

public class ArithmeticCoding {
    public String main(String filename) throws IOException {
        List<String> symbols = new ArrayList<>();
        File file = new File(filename);
        Scanner sc = new Scanner(file);
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            for (int i = 0; i < line.length(); i++) {
                symbols.add(String.valueOf(line.charAt(i)));
            }
            if (sc.hasNextLine()) {
                symbols.add("/n");
            }
        }

        sc.close();
        Map<String, Double> chars = new LinkedHashMap<>();
        for (String c : symbols) {
            Double count = chars.get(c);
            if (count == null) {
                chars.put(c, (double) 1);
            } else {
                chars.put(c, count + 1);
            }
        }
        for (Map.Entry<String, Double> entry : chars.entrySet()) {
            entry.setValue(entry.getValue() / symbols.size());
        }
        LinkedHashMap<String, Double> sortedMap = new LinkedHashMap<>();
        chars.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey(Comparator.naturalOrder()))
                .forEachOrdered(x -> sortedMap.put(x.getKey(), x.getValue()));
        ArithmeticCoding coding = new ArithmeticCoding();
        String code = coding.encode(sortedMap, symbols);
        Path path = Paths.get(filename);
        filename = path.getParent() + File.separator + "decode.txt";
        OutputStream os = new FileOutputStream(filename);
        os.write(compress(code));
        os.close();
        String mapFile = path.getParent() + File.separator + "map.txt";
        Writer writer = new BufferedWriter(new FileWriter(mapFile));
        writer.write(symbols.size() + "\n");
        for (Map.Entry<String, Double> entry : sortedMap.entrySet()) {
            writer.write(entry.getKey() + "';'" + entry.getValue() + "\n");
        }
        writer.close();
        return filename;
    }

    public String encode(LinkedHashMap<String, Double> symbols, List<String> characters) {
        BigDecimal bound = BigDecimal.valueOf(0);
        BigDecimal upperBound = BigDecimal.valueOf(0);
        BigDecimal lowerBound = BigDecimal.valueOf(0);
        HashMap<String, Node> nodes = new HashMap<>();
        for (Map.Entry<String, Double> entry : symbols.entrySet()) {
            nodes.put(entry.getKey(), Node.builder()
                    .lowerBound(bound)
                    .upperBound(bound.add(BigDecimal.valueOf(entry.getValue())))
                    .prob(entry.getValue())
                    .build());
            bound = bound.add(BigDecimal.valueOf(entry.getValue()));
        }

        for (int i = 0; i < characters.size(); i++) {
            if (nodes.containsKey(characters.get(i))) {
                upperBound = nodes.get(characters.get(i)).getUpperBound();
                bound = lowerBound = nodes.get(characters.get(i)).getLowerBound();
                BigDecimal r = upperBound.subtract(lowerBound);
                if (i != characters.size() - 1) {
                    for (Map.Entry<String, Node> symbol : nodes.entrySet()) {
                        lowerBound = bound;
                        Node temp = symbol.getValue();
                        temp.setLowerBound(lowerBound);
                        temp.setUpperBound(lowerBound.add(BigDecimal.valueOf(temp.getProb()).multiply(r)));
                        bound = temp.getUpperBound();
                        symbol.setValue(temp);
                    }
                }

            }
        }
        BigDecimal codeSum = BigDecimal.valueOf(0);
        StringBuilder code = new StringBuilder();
        int i = 1;
        while (codeSum.compareTo(lowerBound) < 0 & codeSum.compareTo(upperBound) < 0) {
            codeSum = codeSum.add(BigDecimal.valueOf(1).divide(pow(2, i), MathContext.DECIMAL128));
            if (codeSum.compareTo(upperBound) > 0) {
                codeSum = codeSum.subtract(BigDecimal.valueOf(1).divide(pow(2, i), MathContext.DECIMAL128));
                code.append("0");
            } else {
                code.append("1");
            }
            i++;
        }
        System.out.println(codeSum);
        System.out.println(code);
        return code.toString();
    }

    public BigDecimal pow(int value, int powValue) {
        BigDecimal result = BigDecimal.valueOf(1);
        for (int i = 1; i <= powValue; i++) {
            result = result.multiply(BigDecimal.valueOf(value));
        }
        return result;
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

}
