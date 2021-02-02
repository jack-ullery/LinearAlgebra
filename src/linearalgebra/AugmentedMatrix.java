package linearalgebra;

import java.io.PrintStream;
import java.util.Scanner;

/**
 *
 * @author Jack
 */
public class AugmentedMatrix extends Matrix {

    private Matrix aug;

    public AugmentedMatrix(Number[][] arr, Number[][] augmented) {
        this(arr, augmented, false, System.out);
    }

    public AugmentedMatrix(Number[][] arr, Number[][] augmented, boolean toLatex, PrintStream outputStream) {
        super(arr, toLatex, outputStream);
        if (augmented == null || augmented.length == 0) {
            throw new NullPointerException();
        } else if (augmented.length != arr.length) {
            throw new IllegalArgumentException("Expected length " + arr.length + " but got length " + augmented[0].length);
        }
        aug = new Matrix(augmented, toLatex, outputStream);
    }

    public AugmentedMatrix(Number[][] arr, Number[] augmented) {
        this(arr, addDimension(augmented));
    }

    public AugmentedMatrix(Number[][] arr, Number[] augmented, boolean toLatex, PrintStream outputStream) {
        this(arr, addDimension(augmented), toLatex, outputStream);
    }

    private static Number[][] addDimension(Number[] arr) {
        Number[][] newArr = new Number[arr.length][1];
        for (int i = 0; i < arr.length; i++) {
            newArr[i][0] = arr[i];
        }
        return newArr;
    }

    @Override
    public void multiplyRow(int row, Fraction c) {
        super.multiplyRow(row, c);
        aug.multiplyRow(row, c);
    }

    @Override
    public void divideRow(int row, Fraction c) {
        super.divideRow(row, c);
        aug.divideRow(row, c);
    }

    @Override
    public void subRow(int rowDest, int rowSource, Fraction c) {
        super.subRow(rowDest, rowSource, c);
        aug.subRow(rowDest, rowSource, c);
    }

    @Override
    public void swapRows(int x, int y) {
        super.swapRows(x, y);
        aug.swapRows(x, y);
    }
    
    @Override
    public Matrix getInverse(){
        throw new UnsupportedOperationException("Can't invert augmented matrix!");        
    }

    public Fraction[][][] toArrays() {
        return new Fraction[][][]{this.toArray(), aug.toArray()};
    }

    @Override
    public String toString() {
        if (this.getLatex()) {
            return toStringLatex();
        }
        return toStringDefault();
    }

    @Override
    public String toStringLatex() {
        StringBuilder sb = new StringBuilder();
        sb.append("\\[ \\left(\\begin{matrix}");
        sb.append(this.toStringCustom("&", "\\\\"));
        sb.append("\\end{matrix}\\left|\\,\\begin{matrix}");
        sb.append(aug.toStringCustom("&", "\\\\")); // NEW
        sb.append("    \\end{matrix}\\right. \\right) \\]");
        return sb.toString();
    }

    private String toStringDefault() {
        StringBuilder sb = new StringBuilder();
        Scanner line = new Scanner(this.toStringCustom("", "\n"));
        int max1 = 0;
        while (line.hasNextLine()) {
            String next = line.nextLine();
            if (next.length() > max1) {
                max1 = next.length();
            }
        }

        line = new Scanner(aug.toStringCustom("", "\n"));
        int max2 = 0;
        while (line.hasNextLine()) {
            String next = line.nextLine();
            if (next.length() > max2) {
                max2 = next.length();
            }
        }

        line = new Scanner(this.toStringCustom("", "\n"));
        Scanner line2 = new Scanner(aug.toStringCustom("", "\n"));
        while (line.hasNextLine()) {
            String row = line.nextLine();
            String el = line2.nextLine();
            sb.append(String.format("|%" + max1 + "s| %" + max2 + "s|\n", row, el));
        }
        return sb.toString();
    }

}
