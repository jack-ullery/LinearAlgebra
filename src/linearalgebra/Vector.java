package linearalgebra;

import java.io.PrintStream;

/**
 *
 * @author Jack
 */
public class Vector extends Matrix {

    public Vector(Number[] arr) {
        super(addDimension(arr));
    }

    public Vector(Number[] arr, boolean toLatex, PrintStream outputStream) {
        super(addDimension(arr), toLatex, outputStream);
    }

    public Vector(Vector toCopy) {
        super(toCopy);
    }

    private static Number[][] addDimension(Number[] arr) {
        Number[][] newArr = new Number[arr.length][1];
        for (int i = 0; i < arr.length; i++) {
            newArr[i][0] = arr[i];
        }
        return newArr;
    }

    public static Vector[] orthogonalize(Vector[] arr) {

        if (arr == null || arr.length == 0) {
            return arr;
        }

        Vector[] resultArray = new Vector[arr.length];
        for (int i = 0; i < arr.length; i++) {
            resultArray[i] = arr[i];
            arr[0].output.printf("v_%d = x_%d", i + 1, i + 1);
            for (int j = 0; j < i; j++) {
                arr[0].output.printf(" - proj_{v_%d} (x_%d)", j + 1, i + 1);
                resultArray[i] = Vector.subtract(resultArray[i], Vector.project(resultArray[j], arr[i]));
            }
            arr[0].output.println("=\n" + resultArray[i]);
        }

        arr[0].output.println("Orthogonal vectors are: ");
        Matrix.printMatrices(resultArray, arr[0].output);
        return resultArray;
    }

    public static Vector getZeroVector(final int n, final boolean toLatex, PrintStream output) {
        Number[] zero = new Number[n];
        for (int i = 0; i < n; i++) {
            zero[i] = 0;
        }
        return new Vector(zero, toLatex, output);
    }

    public Vector[] orthogonallyDecompose(Matrix subspace) {
        if (subspace == null || subspace.getDimensions()[0] != length()) {
            throw new IllegalArgumentException("Incorrect size!");
        }

        output.println("We need to get the orthogonal basis of the subspace:");
        Vector[] basis = subspace.getOrthogonalBasis();

        Vector result = getZeroVector(length(), toLatex, output);
        output.printf("\nw = ");
        for (int i = 0; i < basis.length; i++) {
            output.printf("proj_{w_%d}(v)", i + 1);
            if (i != basis.length - 1) {
                output.print(" + ");
            }
            result = Vector.add(result, Vector.project(basis[i], this));
        }
        output.println("\n" + result);
        output.println("w^⊥ = v - w =");
        Vector complement = Vector.subtract(this, result);
        output.println(complement);
        return new Vector[]{result, complement};
    }

    public Vector[] orthogonallyDecompose(Vector[] basis) {
        Vector result = getZeroVector(length(), toLatex, output);
        output.printf("\nw = ");
        for (int i = 0; i < basis.length; i++) {
            output.printf("proj_{w_%d}(v)", i + 1);
            if (i != basis.length - 1) {
                output.print(" + ");
            }
            result = Vector.add(result, Vector.project(basis[i], this));
        }
        output.println("\n" + result);
        output.println("w^⊥ = v - w =");
        Vector complement = Vector.subtract(this, result);
        output.println(complement);
        return new Vector[]{result, complement};
    }

    public static Vector multiply(Vector mul, Fraction c) {
        mul = new Vector(mul);
        for (int i = 0; i < mul.length(); i++) {
            mul.multiplyRow(i, c);
        }
        return mul;
    }

    public static Vector subtract(Vector a, Vector b) {
        a = new Vector(a);
        for (int i = 0; i < a.length(); i++) {
            a.leadingCol[i] = 0; // HACKY
            a.matrix[i][0] = Fraction.subtract(a.matrix[i][0], b.matrix[i][0]);
        }
        return a;
    }

    public static Vector add(Vector a, Vector b) {
        a = new Vector(a);
        for (int i = 0; i < a.length(); i++) {
            a.leadingCol[i] = 0; // HACKY
            a.matrix[i][0] = Fraction.add(a.matrix[i][0], b.matrix[i][0]);
        }
        return a;
    }

    public static Vector project(Vector b, Vector a) {
        Fraction result = dotProduct(a, b);
        result = Fraction.divide(result, dotProduct(b, b));
        Vector re = Vector.multiply(b, result);
        return re;
    }

    public static Fraction dotProduct(Vector a, Vector b) {
        if (a.length() != b.length()) {
            throw new IllegalArgumentException("Input size mismatch!");
        }

        Fraction result = Fraction.ZERO;
        for (int i = 0; i < a.length(); i++) {
            result = Fraction.add(result, Fraction.multiply(a.matrix[i][0], b.matrix[i][0]));
        }
        return result;
    }

    public int length() {
        return getDimensions()[0];
    }
}
