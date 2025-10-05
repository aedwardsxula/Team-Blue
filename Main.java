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


    
    }
}
