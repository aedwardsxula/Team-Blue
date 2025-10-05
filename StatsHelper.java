import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StatsHelper {
    double mean(List<? extends Number> list) {
                double sum = 0;
                for (Number n : list) sum += n.doubleValue();
                return sum / list.size();
            }

    double std(List<? extends Number> list) {
                double mean = mean(list);
                double sumSq = 0;
                for (Number n : list) sumSq += Math.pow(n.doubleValue() - mean, 2);
                return Math.sqrt(sumSq / list.size());
            }
    double min(List<? extends Number> list) {
                double min = Double.MAX_VALUE;
                for (Number n : list) if (n.doubleValue() < min) min = n.doubleValue();
                return min;
            }
    double max(List<? extends Number> list) {
                double max = -Double.MAX_VALUE;
                for (Number n : list) if (n.doubleValue() > max) max = n.doubleValue();
                return max;
         
            }
    double percentile(List<? extends Number> list, double p) {
                List<Double> sorted = new ArrayList<>();
                for (Number n : list) sorted.add(n.doubleValue());
                Collections.sort(sorted);
                double idx = p * (sorted.size() - 1);
                int lower = (int) Math.floor(idx);
                int upper = (int) Math.ceil(idx);
                if (upper == lower) return sorted.get(lower);
                return sorted.get(lower) + (sorted.get(upper) - sorted.get(lower)) * (idx - lower);

            }
}
