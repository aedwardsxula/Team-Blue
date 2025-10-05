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

    
    }
}