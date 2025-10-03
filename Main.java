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
        int N = 5; // how many records to store
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



    
    }
}