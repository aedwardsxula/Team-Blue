public class LinearRegression {
    private double[] x;
    private double[] y;
    private double slope;
    private double intercept;

    // Constructor
    public LinearRegression(double[] x, double[] y) {
        if (x.length != y.length) {
            throw new IllegalArgumentException("X and Y must have the same length.");
        }
        this.x = x;
        this.y = y;
        calculateCoefficients();

    }

    private void calculateCoefficients() {
        int n = x.length;
        double sumX = 0, sumY = 0, sumXY = 0, sumX2 = 0;

        for (int i = 0; i < n; i++) {
            sumX += x[i];
            sumY += y[i];
            sumXY += x[i] * y[i];
            sumX2 += x[i] * x[i];
        }

        // Formula for slope (m) and intercept (b)
        slope = (n * sumXY - sumX * sumY) / (n * sumX2 - sumX * sumX);
        intercept = (sumY - slope * sumX) / n;
    }
    public double predict(double newX) {
        return slope * newX + intercept;
    }

    public double getSlope() {
        return slope;
    }
    public double getIntercept() {
        return intercept;
    }

    public double getCorrelation() {
    int n = x.length;
    double sumX = 0, sumY = 0, sumXY = 0, sumX2 = 0, sumY2 = 0;

    for (int i = 0; i < n; i++) {
        sumX += x[i];
        sumY += y[i];
        sumXY += x[i] * y[i];
        sumX2 += x[i] * x[i];
        sumY2 += y[i] * y[i];
    }

    double numerator = n * sumXY - sumX * sumY;
    double denominator = Math.sqrt((n * sumX2 - sumX * sumX) * (n * sumY2 - sumY * sumY));

    return numerator / denominator;

    
}
    public void printData() {
    System.out.println("x\t\ty");
    for (int i = 0; i < x.length; i++) {
        System.out.printf("%.1f\t\t%.1f%n", x[i], y[i]);
    }
}

}
