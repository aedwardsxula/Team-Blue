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

}
