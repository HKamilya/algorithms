package huffman;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Node {
    private String symbol;
    private double probability;
    private Node leftNode;
    private Node rightNode;

    public Double compare(Node n1, Node n2) {
        return n1.getProbability() - n2.getProbability();
    }

}
