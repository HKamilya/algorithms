package lzw;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.zip.GZIPInputStream;

public class LZWDecoding {
    public String main(String filename) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(filename));
        String line = decompress(bytes);
        String[] codesTemp = line.split(";");
        List<Integer> codes = new ArrayList<>();
        for (String s : codesTemp) {
            codes.add(Integer.valueOf(s));
        }
        Path path = Paths.get(filename);
        File file = new File(path.getParent() + File.separator + "map.txt");
       Scanner sc = new Scanner(file);
        LinkedHashMap<String, Integer> map = new LinkedHashMap<>();
        while (sc.hasNextLine()) {
            line = sc.nextLine();
            String[] parts = line.split("';'");
            if (parts[0].equals("/n")) {
                map.put("\n", Integer.parseInt(parts[1]));
            } else {
                map.put(parts[0], Integer.parseInt(parts[1]));
            }
        }
        LZWDecoding decoding = new LZWDecoding();
        String output = decoding.decode(codes, map);
        filename = path.getParent() + File.separator + "output.txt";
        BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
        writer.write(output);
        writer.close();
        return filename;
    }

    public String decode(List<Integer> codes, Map<String, Integer> entryMap) {
        Map<Integer, String> dict = new HashMap<>();
        for (Map.Entry<String, Integer> entry : entryMap.entrySet()) {
            dict.put(entry.getValue(), entry.getKey());
        }
        StringBuilder encode = new StringBuilder();
        String chars = dict.get(codes.remove(0));
        encode.append(chars);
        for (Integer code : codes) {
            String entry;
            if (dict.containsKey(code)) {
                entry = dict.get(code);
            } else {
                entry = chars + chars.charAt(0);
            }
            encode.append(entry);
            dict.put(dict.size(), chars + entry.charAt(0));
            chars = entry;
        }
        return encode.toString();
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
