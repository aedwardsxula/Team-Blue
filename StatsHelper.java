import java.util.List;

public class StatsHelper {
    double mean(List<? extends Number> list) {
                double sum = 0;
                for (Number n : list) sum += n.doubleValue();
                return sum / list.size();
            }

    

}
