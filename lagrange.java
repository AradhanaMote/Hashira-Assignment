import org.json.JSONObject;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class PolynomialSecret {
    
    // Function to decode y from base
    public static int decode(String value, int base) {
        return Integer.parseInt(value, base);
    }

    // Solve system of linear equations using Gaussian elimination
    public static double[] gaussianElimination(double[][] A, double[] B) {
        int n = B.length;
        for (int i = 0; i < n; i++) {
            // Partial pivot
            int max = i;
            for (int j = i + 1; j < n; j++) {
                if (Math.abs(A[j][i]) > Math.abs(A[max][i])) max = j;
            }
            double[] temp = A[i]; A[i] = A[max]; A[max] = temp;
            double t = B[i]; B[i] = B[max]; B[max] = t;

            // Eliminate below
            for (int j = i + 1; j < n; j++) {
                double factor = A[j][i] / A[i][i];
                B[j] -= factor * B[i];
                for (int k = i; k < n; k++) {
                    A[j][k] -= factor * A[i][k];
                }
            }
        }

        // Back substitution
        double[] X = new double[n];
        for (int i = n - 1; i >= 0; i--) {
            double sum = B[i];
            for (int j = i + 1; j < n; j++) {
                sum -= A[i][j] * X[j];
            }
            X[i] = sum / A[i][i];
        }
        return X;
    }

    public static void main(String[] args) {
        try {
            // Load JSON input
            String content = new String(Files.readAllBytes(Paths.get("input.json")));
            JSONObject obj = new JSONObject(content);

            // Read n and k
            JSONObject keys = obj.getJSONObject("keys");
            int n = keys.getInt("n");
            int k = keys.getInt("k"); // polynomial degree = k-1

            List<Integer> xs = new ArrayList<>();
            List<Integer> ys = new ArrayList<>();

            // Collect at least k points
            for (String key : obj.keySet()) {
                if (key.equals("keys")) continue;
                JSONObject point = obj.getJSONObject(key);
                int base = Integer.parseInt(point.getString("base"));
                String value = point.getString("value");
                int x = Integer.parseInt(key);
                int y = decode(value, base);
                xs.add(x);
                ys.add(y);
            }

            // Take first k points
            double[][] A = new double[k][k];
            double[] B = new double[k];
            for (int i = 0; i < k; i++) {
                int x = xs.get(i);
                int y = ys.get(i);
                for (int j = 0; j < k; j++) {
                    A[i][j] = Math.pow(x, k - j - 1); // descending powers
                }
                B[i] = y;
            }

            // Solve system
            double[] coeff = gaussianElimination(A, B);

            // Secret = last coefficient (c)
            double c = coeff[coeff.length - 1];
            System.out.println("Secret (c): " + c);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}