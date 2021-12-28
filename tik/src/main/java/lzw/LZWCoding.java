package lzw;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.zip.GZIPOutputStream;

public class LZWCoding {

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
                symbols.add("/n");
            }
        }

        Path path = Paths.get(filename);
        sc.close();
        LZWCoding lzwCoding = new LZWCoding();
        Map<String, Integer> dict = new HashMap<>();
        for (String c : symbols) {
            if (!dict.containsKey(String.valueOf(c))) {
                dict.put(String.valueOf(c), dict.size());
            }
        }
        List<Integer> codes = lzwCoding.encode(symbols);
        filename = path.getParent() + File.separator + "decode.txt";


        OutputStream os = new FileOutputStream(filename);
        StringBuilder text = new StringBuilder();
        for (Integer code : codes) {
            text.append(code).append(";");
        }
        os.write(compress(text.toString()));
        os.close();
        System.out.println(codes);
        BufferedWriter writer = new BufferedWriter(new FileWriter(path.getParent() + File.separator +"map.txt"));
        for (Map.Entry<String, Integer> entry : dict.entrySet()) {
            writer.write(entry.getKey() + "';'" + entry.getValue() + "\n");
        }
        writer.close();
        return filename;
    }

    public List<Integer> encode(List<String> text) {
        Map<String, Integer> dict = new LinkedHashMap<>();
        for (String c : text) {
            if (!dict.containsKey(String.valueOf(c))) {
                dict.put(String.valueOf(c), dict.size());
            }
        }
        String chars = "";
        List<Integer> codes = new ArrayList<>();
        for (String character : text) {
            String newCombOfChars = chars + character;
            if (dict.containsKey(newCombOfChars)) {
                chars = newCombOfChars;
            } else {
                codes.add(dict.get(chars));
                dict.put(newCombOfChars, dict.size());
                chars = String.valueOf(character);
            }
        }
        if (!chars.isEmpty()) {
            codes.add(dict.get(chars));
        }
        return codes;
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
