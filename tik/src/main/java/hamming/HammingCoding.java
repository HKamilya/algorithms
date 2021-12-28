package hamming;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class HammingCoding {
    public String main(String filename) throws Exception {
        File file = new File(filename);
        Scanner sc = new Scanner(file);
        StringBuilder text = new StringBuilder();
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            text.append(line);
        }
        sc.close();
        HammingCoding coding = new HammingCoding();

        char[] codes = coding.encode(convertStringToBinary(text.toString()));
        Path path = Paths.get(filename);
        FileWriter writer = new FileWriter(path.getParent() + File.separator + "decode.txt");
        writer.write(codes);
        System.out.println(codes);
        writer.flush();
        writer.close();
        return path.getParent() + File.separator + "decode.txt";

    }

    public char[] encode(String text) throws Exception {
        String code = generateCodeWithContBits(text);
        int pow = 1;
        while (pow * 2 <= text.length())
            pow *= 2;
        return updateRedBits(code.toCharArray(), pow);
    }

    public String generateCodeWithContBits(String a) {
        StringBuilder textWithControlBits = new StringBuilder();
        int i = 1;
        int j = 0;
        while (j < a.length()) {
            while (i - textWithControlBits.length() != 1 && j < a.length()) {
                textWithControlBits.append(a.charAt(j));
                j++;
            }
            if (i - textWithControlBits.length() == 1)
                textWithControlBits.append("0");
            i *= 2;
        }
        return textWithControlBits.toString();
    }

    private char[] updateRedBits(char[] codes, int pow) {
        int j;
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
                codes[i - 1] = '1';
        }
        return codes;
    }

    public static String convertStringToBinary(String input) {

        StringBuilder result = new StringBuilder();
        char[] chars = input.toCharArray();

        for (char aChar : chars) {
            result.append(String.format("%8s", Integer.toBinaryString(aChar)).replaceAll(" ", "0"));
        }
        return result.toString();
    }

}
