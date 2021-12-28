package arithmetic;

import javafx.scene.chart.ValueAxisBuilder;
import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Node {
    private BigDecimal upperBound;
    private BigDecimal lowerBound;
    private double prob;


}
