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
            

}
