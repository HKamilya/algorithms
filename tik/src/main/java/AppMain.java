import arithmetic.ArithmeticCoding;
import arithmetic.ArithmeticDecoding;
import hamming.HammingCoding;
import hamming.HammingDecoding;
import huffman.HuffmanCoding;
import huffman.HuffmanDecoding;
import javafx.application.Application;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.FlowPane;
import javafx.geometry.Orientation;
import javafx.geometry.Insets;
import lzw.LZWCoding;
import lzw.LZWDecoding;

import java.io.IOException;

public class AppMain extends Application {

    public static void main(String[] args) {
        Application.launch();
    }

    @Override
    public void start(Stage stage) {
        Label selectedLbl = new Label();
        TextField textField = new TextField();
        textField.setPrefColumnCount(11);
        Button encodeBtn = new Button("Кодировать");
        Button decodeBtn = new Button("Декодировать");
        RadioButton huffman = new RadioButton("Huffman");
        RadioButton arithmetic = new RadioButton("Arithmetic");
        RadioButton lzw = new RadioButton("LZW");
        RadioButton hamming = new RadioButton("Hamming");
        ToggleGroup group = new ToggleGroup();
        huffman.setToggleGroup(group);
        arithmetic.setToggleGroup(group);
        lzw.setToggleGroup(group);
        hamming.setToggleGroup(group);
        encodeBtn.setOnAction(event -> {
            RadioButton selection = (RadioButton) group.getSelectedToggle();
            String method = selection.getText();
            String file = "";
            if (method.equals("Arithmetic")) {
                ArithmeticCoding coding = new ArithmeticCoding();
                try {
                    file = coding.main(textField.getText());
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            if (method.equals("LZW")) {
                LZWCoding lzwCoding = new LZWCoding();
                String filename = textField.getText();
                try {
                    file = lzwCoding.main(filename);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (method.equals("Hamming")) {
                HammingCoding hammingCoding = new HammingCoding();
                String filename = textField.getText();
                try {
                    file = hammingCoding.main(filename);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (method.equals("Huffman")) {
                HuffmanCoding coding = new HuffmanCoding();
                try {
                    file = coding.main(textField.getText());
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            selectedLbl.setText("filepath: " + file);
        });
        decodeBtn.setOnAction(event -> {
            RadioButton selection = (RadioButton) group.getSelectedToggle();
            String method = selection.getText();
            String file = "";
            if (method.equals("Arithmetic")) {
                ArithmeticDecoding decoding = new ArithmeticDecoding();
                try {
                    file = decoding.main(textField.getText());
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            if (method.equals("Huffman")) {
                HuffmanDecoding decoding = new HuffmanDecoding();
                try {
                    file = decoding.main(textField.getText());
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            if (method.equals("Hamming")) {
                HammingDecoding hammingDecoding = new HammingDecoding();
                String filename = textField.getText();
                try {
                    file = hammingDecoding.main(filename);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (method.equals("LZW")) {
                LZWDecoding decoding = new LZWDecoding();
                try {
                    file = decoding.main(textField.getText());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            selectedLbl.setText("filepath: " + file);
        });
        FlowPane root = new FlowPane(Orientation.VERTICAL, 10, 10);
        root.getChildren().addAll(huffman, arithmetic, lzw, hamming, textField, encodeBtn, decodeBtn, selectedLbl);
        root.setPadding(new Insets(10));
        Scene scene = new Scene(root, 500, 400);

        stage.setScene(scene);
        stage.setTitle("Coding");
        stage.show();
    }
}