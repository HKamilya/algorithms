package hamming;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class HammingDecoding {

    public String main(String filename) throws Exception {
        File file = new File(filename);
        Scanner sc = new Scanner(file);
        StringBuilder text = new StringBuilder();
        Path path = Paths.get(filename);
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            text.append(line);
        }
        sc.close();
        HammingDecoding decoding = new HammingDecoding();
        filename = decoding.decode(text.toString(), path.getParent().toString());
        return filename;
    }

    public String decode(String symbols, String path) throws Exception {
        int pow = 1;
        while (pow * 2 <= symbols.length())
            pow *= 2;

        char[] codes = symbols.toCharArray();
        int mistake = searchProblems(codes, pow);
        FileWriter writer = new FileWriter(path + File.separator + "output.txt");
        if (mistake >= 0) {
            codes[mistake] = codes[mistake] == '1' ? '0' : '1';
            writer.write("ошибка по индексу: " + (mistake + 1) + "\n");
        } else {
            writer.write("ошибок нет\n");
        }
        writer.write("исходный код: ");
        writer.write(binaryToText(out(codes)));
        writer.write("\n");
        writer.flush();
        writer.close();
        return path + File.separator + "output.txt";
    }

    private static int searchProblems(char[] codes, int pow) {
        int j;
        int res = 0;
        for (int i = 1; i <= pow; i *= 2) {
            int read = 0;
            j = i - 1;
            int sum = 0;
            while (j < codes.length) {
                if (read != i) {
                    read++;
                    if (codes[j] == '1')
                        sum++;
                    j++;
                } else {
                    j += i;
                    read = 0;
                }
            }
            if (sum % 2 != 0)
                res += i;
        }
        return res - 1;
    }

    private String out(char[] codes) {
        int pow = 1;
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < codes.length; i++) {
            if (i == (pow - 1)) {
                pow *= 2;
                continue;
            }
            b.append(codes[i]);
        }
        return b.toString();
    }

    public static String binaryToText(String binaryText) {
        StringBuilder sb = new StringBuilder();

        Arrays.stream(
                binaryText.split("(?<=\\G.{8})")
        ).forEach(s ->
                sb.append((char) Integer.parseInt(s, 2))
        );

        return sb.toString();
    }


}
