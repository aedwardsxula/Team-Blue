import java.io.*;
import java.util.*;


class InsuranceRecord {
    String sex;
    String smoker;
    String region;
    int age;
    double bmi;
    int children;
    double charges;

    public InsuranceRecord(int age, String sex, double bmi, int children, String smoker, String region, double charges) {
        this.age = age;
        this.sex = sex;
        this.bmi = bmi;
        this.children = children;
        this.smoker = smoker;
        this.region = region;
        this.charges = charges;
    }

    @Override
    public String toString() {
        return "InsuranceRecord{" +
                "age=" + age +
                ", sex='" + sex + '\'' +
                ", bmi=" + bmi +
                ", children=" + children +
                ", smoker='" + smoker + '\'' +
                ", region='" + region + '\'' +
                ", charges=" + charges +
                '}';
    }
}

public class Main {
    public static void main(String[] args) {
        String filePath = "insurance.csv"; // path to dataset
        int N = 10; // how many records to store
        List<InsuranceRecord> records = new ArrayList<>();

        if (args.length > 0) {
            try {
                N = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.out.println("Invalid N value, using default (10).");
            }
        }

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line = br.readLine(); // skip header
            int count = 0;

            while ((line = br.readLine()) != null && count < N) {
                String[] values = line.split(",");

                int age = Integer.parseInt(values[0].trim());
                String sex = values[1].trim();
                double bmi = Double.parseDouble(values[2].trim());
                int children = Integer.parseInt(values[3].trim());
                String smoker = values[4].trim();
                String region = values[5].trim();
                double charges = Double.parseDouble(values[6].trim());

                InsuranceRecord record = new InsuranceRecord(age, sex, bmi, children, smoker, region, charges);
                records.add(record);

                count++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Print stored records
        for (InsuranceRecord r : records) {
            System.out.println(r);
        }
        //Put data into lists for stats
        List<Integer> ages = new ArrayList<>();
        List<Double> bmis = new ArrayList<>();
        List<Integer> childrenList = new ArrayList<>();
        List<Double> charges = new ArrayList<>();

        for (InsuranceRecord r : records) {
            ages.add(r.age);
            bmis.add(r.bmi);
            childrenList.add(r.children);
            charges.add(r.charges);
        }
        StatsHelper stats = new StatsHelper();
        Runnable displayStats = () -> {
            System.out.println("\n===== Descriptive Statistics =====");

            System.out.printf("%-10s %5s %10s %10s %10s %10s %10s %10s %10s%n",
                "Variable", "Count", "Mean", "Std", "Min", "25%", "50%", "75%", "Max");

            List<Map.Entry<String, List<? extends Number>>> data = new ArrayList<>();
                data.add(new AbstractMap.SimpleEntry<>("Age", ages));
                data.add(new AbstractMap.SimpleEntry<>("BMI", bmis));
                data.add(new AbstractMap.SimpleEntry<>("Children", childrenList));
                data.add(new AbstractMap.SimpleEntry<>("Charges", charges));

            for (Map.Entry<String, List<? extends Number>> entry : data) {
            String name = entry.getKey();
            List<? extends Number> list = entry.getValue();
            System.out.printf("%-10s %5d %10.2f %10.2f %10.2f %10.2f %10.2f %10.2f %10.2f%n",
                name,
                list.size(),
                stats.mean(list),
                stats.std(list),
                stats.min(list),
                stats.percentile(list, 0.25),
                stats.percentile(list, 0.50),
                stats.percentile(list, 0.75),
                stats.max(list));
        }
        };
        displayStats.run();
        System.out.println("\nHistogram of Ages:");

        Map<Integer, Integer> ageFreq = new TreeMap<>();
        for (InsuranceRecord r : records) {
            ageFreq.put(r.age, ageFreq.getOrDefault(r.age, 0) + 1);
        }

         for (Map.Entry<Integer, Integer> entry : ageFreq.entrySet()){
            int age = entry.getKey();
            int freq = entry.getValue();
            System.out.printf("%3d | %s (%d)%n", age, "*".repeat(freq), freq);
         }
        System.out.println("\nHistogram of BMI (Vertical):");
        Map<Double, Integer> bmiFreq = new TreeMap<>();
        for (InsuranceRecord r : records) {
            bmiFreq.put(r.bmi, bmiFreq.getOrDefault(r.bmi, 0) + 1);
        }
        for (Map.Entry<Double, Integer> entry : bmiFreq.entrySet()){
            double bmi = entry.getKey();
            int bmifreq = entry.getValue();
            for (int i = 0; i < bmifreq; i++) {
                System.out.println("  *"); // each star on its own line
            }
        System.out.printf("%6.2f | (%d)%n", bmi, bmifreq);
        }
        System.out.println("\nHistogram of Smokers vs Non-Smokers (Vertical):");
        int smokerCount = 0;
        int nonSmokerCount = 0;
        List<String> smokerList = new ArrayList<>();
        for (InsuranceRecord r : records) {
            smokerList.add(r.smoker);
        }


        for (String status : smokerList) { // smokerList = ["yes", "no", "yes", ...]
            if (status.equalsIgnoreCase("yes")) {
            smokerCount++;
            } else if (status.equalsIgnoreCase("no")) {
            nonSmokerCount++;
        }
        }
        for (int i = 0; i < smokerCount; i++) {
            System.out.println("  *");
        }
        System.out.printf("Smokers (%d)%n", smokerCount);

        for (int i = 0; i < nonSmokerCount; i++) {
            System.out.println("  *");
        }    
        System.out.printf("Non-Smokers (%d)%n", nonSmokerCount);

        List<Double> chargeForOver50 = new ArrayList<>();
        for (InsuranceRecord r : records) {
            if (r.age >= 50) {
                chargeForOver50.add(r.charges);
            }
        }
        List<Double> chargeForUnder20 = new ArrayList<>();
        for (InsuranceRecord r : records) {
            if (r.age <= 20) {
                chargeForUnder20.add(r.charges);
            }
        }
        double avgChargeOver50 = stats.mean(chargeForOver50);
        double avgChargeUnder20 = stats.mean(chargeForUnder20);

        if (avgChargeOver50 <= 2 * avgChargeUnder20) {
            System.out.printf("\nPeople 50 or older do NOT average twice the charges as the average of people 20 and younger");
        } else {
        System.out.printf("\nPeople 50 or older average twice the charges as the average of people 20 and younger");
        }

        List<Double> childrenCharges1 = new ArrayList<>();
        List<Double> childrenChargesOver1 = new ArrayList<>();

        for (InsuranceRecord r : records) {
            if (r.children == 1) {
                childrenCharges1.add(r.charges);
            }
        }
        for (InsuranceRecord r : records) {
            if (r.children > 1) {
                childrenChargesOver1.add(r.charges);
            }
        }
        double avgChildrenCharges1 = stats.mean(childrenCharges1);
        double avgChildrenChargesOver1 = stats.mean(childrenChargesOver1);

        if (avgChildrenChargesOver1 < avgChildrenCharges1) {
            System.out.println();
            System.out.printf("\nPeople with more than 1 child do average a lower charges per child than those with exactly 1 child");
        } else {
        System.out.println();
        System.out.printf("\nPeople with more than 1 child do NOT average a lower charges per child than those with exactly 1 child");
        }

        List<Double> southernSmokers = new ArrayList<>();
        List<Double> northernSmokers = new ArrayList<>();

        for (InsuranceRecord r : records) {
            if (r.smoker.equalsIgnoreCase("yes") && (r.region.equalsIgnoreCase("southwest") || r.region.equalsIgnoreCase("southeast"))) {
                southernSmokers.add(r.charges);
            } else if (r.smoker.equalsIgnoreCase("yes") && (r.region.equalsIgnoreCase("northwest") || r.region.equalsIgnoreCase("northeast"))) {
                northernSmokers.add(r.charges);
            }
        }

        double avgSouthernSmokers = stats.mean(southernSmokers);
        double avgNorthernSmokers = stats.mean(northernSmokers);

        if (avgSouthernSmokers >= .25 * avgNorthernSmokers) {
            System.out.println();
            System.out.println("\nSouthern smokers are charged are least 25% more than other smokers.");
        } else {
            System.out.println();
            System.out.println("\nSouthern smokers are NOT charged at least 25% more than other smokers.");
        }
        System.out.println();
        System.out.println("\nHistogram of Age Distribution for Smokers:");
        Map<Integer, Integer> smokerAgeFreq = new TreeMap<>();
        for (InsuranceRecord r : records){
            if (r.smoker.equalsIgnoreCase("yes")){
                smokerAgeFreq.put(r.age, smokerAgeFreq.getOrDefault(r.age, 0) + 1);
            }
        }
          for (Map.Entry<Integer, Integer> entry : smokerAgeFreq.entrySet()){
            int age = entry.getKey();
            int smokerAgefreq = entry.getValue();
            System.out.printf("%3d | %s (%d)%n", age, "*".repeat(smokerAgefreq), smokerAgefreq);
          }
        
        List<Integer> smokers20andYounger = new ArrayList<>();
        List<Integer> smokers50andOlder = new ArrayList<>();

        for (InsuranceRecord r : records) {
            if (r.smoker.equalsIgnoreCase("yes") && r.age <= 20) {
                smokers20andYounger.add(r.age);
            } else if (r.smoker.equalsIgnoreCase("yes") && r.age >= 50) {
                smokers50andOlder.add(r.age);
            }
        }
        double avgSmokers20andYounger = stats.mean(smokers20andYounger);
        double avgSmokers50andOlder = stats.mean(smokers50andOlder);

        if (avgSmokers20andYounger > avgSmokers50andOlder) {
            System.out.println();
            System.out.printf("\n The average age that young people smoker more than old people is: ", avgSmokers20andYounger);
        } else {
            System.out.println();
            System.out.println("\n There is no average age where young people smoke more than old people.");
        }
        List<Double> southernBMI = new ArrayList<>();
        List<Double> northernBMI = new ArrayList<>();

        for (InsuranceRecord r : records) {
            if (r.region.equalsIgnoreCase("southwest") || r.region.equalsIgnoreCase("southeast")) {
                southernBMI.add(r.bmi);
            } else if (r.region.equalsIgnoreCase("northwest") || r.region.equalsIgnoreCase("northeast")) {
                northernBMI.add(r.bmi); 
            }
        }
        double avgSouthernBMI = stats.mean(southernBMI);
        double avgNorthernBMI = stats.mean(northernBMI);

        if (avgSouthernBMI > avgNorthernBMI) {
            System.out.println();
            System.out.printf("\nSouthern residents have a higher average BMI than Northern residents at an average BMI of: %6.2f%n", avgSouthernBMI);
        } else {
            System.out.println();
            System.out.println("\nSouthern residents do NOT have a higher average BMI than Northern residents.");
        }

        List<Double> bmiArray = new ArrayList<>();
        List<Double> chargesArray = new ArrayList<>();
        for (InsuranceRecord r : records) {
            bmiArray.add(r.bmi);
            chargesArray.add(r.charges);
        }
        // convert Lists to primitive double[] for existing LinearRegression constructor
        double[] bmiArr = bmiArray.stream().mapToDouble(Double::doubleValue).toArray();
        double[] chargesArr = chargesArray.stream().mapToDouble(Double::doubleValue).toArray();
        LinearRegression lr = new LinearRegression(chargesArr, bmiArr);
        Double lrCorrelation = lr.getCorrelation();

        System.out.println();
        System.out.printf("Correlation between BMI and Charges: %6.4f%n", lrCorrelation);

        double[] fakevalues1 = {17.4, 20.8, 23.1, 25.6, 27.9, 31.5, 34.2, 36.8, 39.7, 42.3, 45.9};
        double[] fakevalues2 = {17.8, 19.6, 22.4, 24.1, 27.7, 30.3, 33.9, 35.8, 38.2, 41.5, 45.7};


        LinearRegression lr2 = new LinearRegression(fakevalues1, fakevalues2);
        System.out.println();
        lr2.printData();





        System.out.println();//spacing 

          System.out.println("\nNumber of Records by Children:");
          Map<Integer, Integer> childrenFreq = new TreeMap<>();
          for (InsuranceRecord r : records){
            childrenFreq.put(r.children, childrenFreq.getOrDefault(r.children, 0) + 1);
          }
            for (Map.Entry<Integer, Integer> entry : childrenFreq.entrySet()){
                int children = entry.getKey();
                int freq = entry.getValue();
                System.out.printf("%3d | %s (%d)%n", children, "*".repeat(freq), freq);
            }


         System.out.println("\nRegion Distribution & Fairness (≤ 5 percentage points spread):");
        int total = records.size();
        if (total == 0) {
            System.out.println("No records loaded; cannot evaluate fairness.");
            return;
        }

        // Count by region (case-insensitive sort for neat output)
        Map<String, Integer> regionCounts = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        for (InsuranceRecord r : records) {
            regionCounts.put(r.region, regionCounts.getOrDefault(r.region, 0) + 1);
        }

        double minPct = Double.POSITIVE_INFINITY;
        double maxPct = Double.NEGATIVE_INFINITY;

        for (Map.Entry<String, Integer> e : regionCounts.entrySet()) {
            String region = e.getKey();
            int cnt = e.getValue();
            double pct = (100.0 * cnt) / total;
            minPct = Math.min(minPct, pct);
            maxPct = Math.max(maxPct, pct);
            System.out.printf("  %-12s : %4d (%.2f%%)%n", region, cnt, pct);
        }

        double spread = maxPct - minPct;
        boolean isFair = spread <= 5.0;

        System.out.printf("%nSpread between largest and smallest region shares: %.2f percentage points%n", spread);
        System.out.println("Is the data fair? " + (isFair ? "YES" : "NO"));


        System.out.println("\nCharges Range Comparison by BMI Group:");
        double minLow = Double.POSITIVE_INFINITY, maxLow = Double.NEGATIVE_INFINITY;
        double minMid = Double.POSITIVE_INFINITY, maxMid = Double.NEGATIVE_INFINITY;
        double minHigh = Double.POSITIVE_INFINITY, maxHigh = Double.NEGATIVE_INFINITY;

        for (InsuranceRecord r : records) {
            if (r.bmi < 30) {
                minLow = Math.min(minLow, r.charges);
                maxLow = Math.max(maxLow, r.charges);
            } else if (r.bmi <= 45) {
                minMid = Math.min(minMid, r.charges);
                maxMid = Math.max(maxMid, r.charges);
            } else {
                minHigh = Math.min(minHigh, r.charges);
                maxHigh = Math.max(maxHigh, r.charges);
            }
        }

        double rangeLow = (minLow == Double.POSITIVE_INFINITY) ? 0 : (maxLow - minLow);
        double rangeMid = (minMid == Double.POSITIVE_INFINITY) ? 0 : (maxMid - minMid);
        double rangeHigh = (minHigh == Double.POSITIVE_INFINITY) ? 0 : (maxHigh - minHigh);

        System.out.printf("BMI < 30   : Range of charges = %.2f%n", rangeLow);
        System.out.printf("BMI 30-45  : Range of charges = %.2f%n", rangeMid);
        System.out.printf("BMI > 45   : Range of charges = %.2f%n", rangeHigh);

        if (rangeMid > rangeLow && rangeMid > rangeHigh)
            System.out.println("\n YES, BMI 30–45 has the greatest range of charges.");
        else
            System.out.println("\n NO, BMI 30–45 does NOT have the greatest range of charges.");


        System.out.println("\nSmoker vs Non-Smoker Charges Comparison:");

        double minSmoker = Double.POSITIVE_INFINITY, maxSmoker = Double.NEGATIVE_INFINITY, sumSmoker = 0;
        double minNon = Double.POSITIVE_INFINITY, maxNon = Double.NEGATIVE_INFINITY, sumNon = 0;
        int countSmoker = 0, countNon = 0;

        for (InsuranceRecord r : records) {
            if (r.smoker.equals("yes")) {
                minSmoker = Math.min(minSmoker, r.charges);
                maxSmoker = Math.max(maxSmoker, r.charges);
                sumSmoker += r.charges;
                countSmoker++;
            } else {
                minNon = Math.min(minNon, r.charges);
                maxNon = Math.max(maxNon, r.charges);
                sumNon += r.charges;
                countNon++;
            }
        }

        double meanSmoker = (countSmoker == 0) ? 0 : sumSmoker / countSmoker;
        double meanNon = (countNon == 0) ? 0 : sumNon / countNon;
        double rangeSmoker = (minSmoker == Double.POSITIVE_INFINITY) ? 0 : (maxSmoker - minSmoker);
        double rangeNon = (minNon == Double.POSITIVE_INFINITY) ? 0 : (maxNon - minNon);
        
        System.out.printf("Smokers     -> Count: %d | Avg charges: %.2f | Range: %.2f%n",
                countSmoker, meanSmoker, rangeSmoker);
        System.out.printf("Non-Smokers -> Count: %d | Avg charges: %.2f | Range: %.2f%n",
                countNon, meanNon, rangeNon);

        boolean higherAvg = meanSmoker > meanNon;
        boolean widerRange = rangeSmoker > rangeNon;

        if (higherAvg && widerRange)
            System.out.println("\n Hypothesis confirmed: Smokers have higher and wider charge ranges.");
        else
            System.out.println("\n Hypothesis not fully confirmed.");

        System.out.println("\nSmoker vs Non-Smoker BMI Comparison:");

        double sumBmiSmoker = 0, sumBmiNon = 0;
        int countBmiSmoker = 0, countBmiNon = 0;

        for (InsuranceRecord r : records) {
            if (r.smoker.equals("yes")) {
                sumBmiSmoker += r.bmi;
                countBmiSmoker++;
            } else {
                sumBmiNon += r.bmi;
                countBmiNon++;
            }
        }

        double avgBmiSmoker = (countBmiSmoker == 0) ? 0 : sumBmiSmoker / countBmiSmoker;
        double avgBmiNon = (countBmiNon == 0) ? 0 : sumBmiNon / countBmiNon;

        System.out.printf("Smokers     -> Count: %d | Avg BMI: %.2f%n", countBmiSmoker, avgBmiSmoker);
        System.out.printf("Non-Smokers -> Count: %d | Avg BMI: %.2f%n", countBmiNon, avgBmiNon);

        if (avgBmiSmoker < avgBmiNon)
            System.out.println("\n Yes, smokers have a lower average BMI.");
        else
            System.out.println("\n No, smokers do not have a lower average BMI.");

        System.out.println("\nRegions Sorted by Average Charges (Descending):");

        Map<String, Double[]> regionStats = new HashMap<>(); // [sum, count]
        for (InsuranceRecord r : records) {
            Double[] stat = regionStats.getOrDefault(r.region, new Double[]{0.0, 0.0});
            stat[0] += r.charges; // sum
            stat[1] += 1.0;       // count
            regionStats.put(r.region, stat);
        }

        List<Map.Entry<String, Double>> avgList = new ArrayList<>();
        for (Map.Entry<String, Double[]> entry : regionStats.entrySet()) {
            double avg = entry.getValue()[0] / entry.getValue()[1];
            avgList.add(Map.entry(entry.getKey(), avg));
        }

        avgList.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

        for (Map.Entry<String, Double> e : avgList)
            System.out.printf("%-12s -> Avg charges: %.2f%n", e.getKey(), e.getValue());


        System.out.println("\nSouthern vs Northern Smoking Analysis:");

        int southSmokers = 0, southTotal = 0;
        int northSmokers = 0, northTotal = 0;
        double southSmokerAgeSum = 0, northSmokerAgeSum = 0;

        for (InsuranceRecord r : records) {
            String regionLower = r.region.toLowerCase();
            if (regionLower.contains("south")) {
                southTotal++;
                if (r.smoker.equalsIgnoreCase("yes")) {
                    southSmokers++;
                    southSmokerAgeSum += r.age;
                }
            } else if (regionLower.contains("north")) {
                northTotal++;
                if (r.smoker.equalsIgnoreCase("yes")) {
                    northSmokers++;
                    northSmokerAgeSum += r.age;
                }
            }
        }

        double southSmokingRate = (southTotal == 0) ? 0 : (100.0 * southSmokers / southTotal);
        double northSmokingRate = (northTotal == 0) ? 0 : (100.0 * northSmokers / northTotal);

        double southAvgSmokerAge = (southSmokers == 0) ? 0 : southSmokerAgeSum / southSmokers;
        double northAvgSmokerAge = (northSmokers == 0) ? 0 : northSmokerAgeSum / northSmokers;

        System.out.printf("Southerners -> Total: %d | Smokers: %d (%.2f%%) | Avg smoker age: %.2f%n",
                southTotal, southSmokers, southSmokingRate, southAvgSmokerAge);
        System.out.printf("Northerners -> Total: %d | Smokers: %d (%.2f%%) | Avg smoker age: %.2f%n",
                northTotal, northSmokers, northSmokingRate, northAvgSmokerAge);

        if (southSmokingRate > northSmokingRate) {
            System.out.printf("%nYES, Southerners smoke more than Northerners.%n");
            System.out.printf("Average age of Southern smokers: %.2f years%n", southAvgSmokerAge);
        } else {
            System.out.printf("%nNO, Southerners do not smoke more than Northerners.%n");
            System.out.printf("Average age of Northern smokers: %.2f years%n", northAvgSmokerAge);
        }

    
        System.out.println("\nSouthern vs Northern Children Analysis:");

        int southChildTotal = 0, northChildTotal = 0;
        int southChildrenSum = 0, northChildrenSum = 0;
        double southAgeSumForChildren = 0, northAgeSumForChildren = 0;

        for (InsuranceRecord r : records) {
            String regionLower = r.region.toLowerCase();
            if (regionLower.contains("south")) {
                southChildTotal++;
                southChildrenSum += r.children;
                southAgeSumForChildren += r.age;
            } else if (regionLower.contains("north")) {
                northChildTotal++;
                northChildrenSum += r.children;
                northAgeSumForChildren += r.age;
            }
        }

        double southAvgChildren = (southChildTotal == 0) ? 0 : (1.0 * southChildrenSum / southChildTotal);
        double northAvgChildren = (northChildTotal == 0) ? 0 : (1.0 * northChildrenSum / northChildTotal);

        double southAvgAgeForChildren = (southChildTotal == 0) ? 0 : (southAgeSumForChildren / southChildTotal);
        double northAvgAgeForChildren = (northChildTotal == 0) ? 0 : (northAgeSumForChildren / northChildTotal);

        System.out.printf("Southerners -> Total: %d | Avg children: %.2f | Avg age: %.2f%n",
                southChildTotal, southAvgChildren, southAvgAgeForChildren);
        System.out.printf("Northerners -> Total: %d | Avg children: %.2f | Avg age: %.2f%n",
                northChildTotal, northAvgChildren, northAvgAgeForChildren);

        if (southAvgChildren > northAvgChildren) {
            System.out.printf("%nYES, Southerners have more children on average than Northerners.%n");
            System.out.printf("Average age of Southerners when this is true: %.2f years%n", southAvgAgeForChildren);
        } else {
            System.out.printf("%nNO, Southerners do not have more children on average.%n");
            System.out.printf("Average age of Northerners when this is true: %.2f years%n", northAvgAgeForChildren);
        }


        System.out.println("\nSimple Linear Regression: Charges vs. Number of Children");

        int n = records.size();
        if (n == 0) {
            System.out.println("No data loaded; cannot perform regression.");
            return;
        }

        double sumX = 0, sumY = 0, sumXY = 0, sumX2 = 0, sumY2 = 0;
        for (InsuranceRecord r : records) {
            double x = r.children;
            double y = r.charges;
            sumX += x;
            sumY += y;
            sumXY += x * y;
            sumX2 += x * x;
            sumY2 += y * y;
        }

         double meanX = sumX / n;
        double meanY = sumY / n;

       
        double numerator = sumXY - (sumX * sumY / n);
        double denominator = sumX2 - (sumX * sumX / n);
        double b1 = (denominator == 0) ? 0 : numerator / denominator;
        double b0 = meanY - b1 * meanX;

       
        double rNumerator = n * sumXY - (sumX * sumY);
        double rDenominator = Math.sqrt((n * sumX2 - sumX * sumX) * (n * sumY2 - sumY * sumY));
        double r = (rDenominator == 0) ? 0 : rNumerator / rDenominator;

        
        System.out.printf("Regression line: charges = %.4f + %.4f * children%n", b0, b1);
        System.out.printf("Pearson correlation coefficient (r): %.4f%n", r);

       
        System.out.println("\nPredicted charges for 22 new x values (children count):");
        Random rand = new Random();
        for (int i = 1; i <= 22; i++) {
            int xNew = rand.nextInt(6); // random number of children between 0 and 5
            double yPred = b0 + b1 * xNew;
            System.out.printf("Case %2d: children = %d -> predicted charges = %.2f%n", i, xNew, yPred);
        }

    }
}
