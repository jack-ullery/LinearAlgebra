package linearalgebra;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author Jack
 */
public class Matrix {

    private class LocalSwapper implements Swapper {

        @Override
        public void swap(int x, int y) {
            swapRows(x, y);
        }
    }

    private static class DummyStream extends OutputStream {

        @Override
        public void write(int b) throws IOException {
        }
    }

    private final Swapper swapper = new LocalSwapper();
    protected PrintStream output = new PrintStream(new DummyStream());
    protected final boolean toLatex;
    protected int[] leadingCol;
    protected Fraction[][] matrix;

    public Matrix(Number[][] arr) {
        fillMatrix(arr);
        this.toLatex = false;
    }

    public Matrix(Number[][] arr, boolean toLatex, PrintStream outputStream) {
        fillMatrix(arr);
        this.output = outputStream;
        this.toLatex = toLatex;
    }

    public Matrix(Matrix toCopy) {
        copyMatrix(toCopy.matrix, toCopy.leadingCol);
        this.output = toCopy.output;
        this.toLatex = toCopy.toLatex;
    }

    private void copyMatrix(Fraction[][] arr, int[] arr2) {
        matrix = new Fraction[arr.length][arr[0].length];
        leadingCol = new int[matrix.length];
        for (int i = 0; i < arr.length; i++) {
            leadingCol[i] = arr2[i];
            System.arraycopy(arr[i], 0, matrix[i], 0, arr[0].length);
        }
    }

    private void fillMatrix(Number[][] arr) {
        leadingCol = new int[arr.length];
        matrix = new Fraction[arr.length][arr[0].length];
        for (int i = 0; i < arr.length; i++) {
            leadingCol[i] = Integer.MAX_VALUE;
            boolean hasNoLead = true;
            for (int j = 0; j < arr[0].length; j++) {
                if (arr[i][j] instanceof Fraction) {
                    matrix[i][j] = new Fraction((Fraction) arr[i][j]);
                } else {
                    matrix[i][j] = new Fraction(arr[i][j].doubleValue());
                }

                if (hasNoLead && arr[i][j].doubleValue() != 0) {
                    hasNoLead = false;
                    leadingCol[i] = j;
                }
            }
        }
    }

    public Matrix toRowEchelon() {
        output.println("Converting to Row Echelon: ");
        output.println(this);
        for (int i = 0; i < matrix.length && i < matrix[0].length; i++) {
            boolean wasZero = true;
            if (leadingCol[i] < matrix.length) {
                for (int j = i + 1; j < matrix.length; j++) {
                    Fraction a = matrix[i][leadingCol[i]];
                    Fraction b = matrix[j][leadingCol[i]];
                    if (!a.isZero() && !b.isZero()) {
                        wasZero = false;
                        cancelRows(j, i, leadingCol[i]);
                    }
                }
            }
            if (wasZero == false) {
                output.println(this);
            }
        }
        normalizeLeading();
        MapSorter.sort(leadingCol, swapper);
        return this;
    }

    public Matrix toReducedRowEchelon() {
        this.toRowEchelon();
        output.println("Converting to Reduced Row Echelon: ");
        output.println(this);
        for (int i = matrix.length - 1; i > 0; i--) {
            boolean wasZero = true;
            if (leadingCol[i] < matrix[i].length) {
                for (int j = 0; j < i; j++) {
                    if (!matrix[j][leadingCol[i]].isZero()) {
                        wasZero = false;
                        cancelRows(j, i, leadingCol[i]);
                    }
                }
            }

            if (wasZero == false) {
                output.println(this);
            }
        }

        normalizeLeading();
        MapSorter.sort(leadingCol, swapper);
        return this;
    }

    public Matrix[] getRowSpace() {
        output.println("To get row space, we need to convert to rref");
        Matrix copy = new Matrix(this);
        copy.toRowEchelon();

        ArrayList<Matrix> list = new ArrayList<>(copy.leadingCol.length);
        for (int i = 0; i < copy.leadingCol.length; i++) {
            if (copy.leadingCol[i] != Integer.MAX_VALUE) {
                list.add(new Vector(copy.matrix[i], toLatex, output).transpose());
            }
        }

        Vector[] vectors = list.toArray(new Vector[list.size()]);
        output.println("Therefore the row space is:");
        printMatrices(vectors, output);
        return vectors;
    }

    public Vector[] getColSpace() {
        output.println("To get column space, we need to convert to rref");
        Matrix copy = new Matrix(this);
        copy.toRowEchelon();

        ArrayList<Vector> list = new ArrayList<>(copy.leadingCol.length);
        for (int i = 0; i < copy.leadingCol.length; i++) {
            int leading = copy.leadingCol[i];
            if (leading != Integer.MAX_VALUE) {
                Fraction[] arr = new Fraction[this.matrix.length];
                for (int j = 0; j < arr.length; j++) {
                    arr[j] = matrix[j][leading];
                }
                list.add(new Vector(arr, toLatex, output));
            }
        }

        Vector[] vectors = list.toArray(new Vector[list.size()]);
        output.println("Therefore the column space is:");
        printMatrices(vectors, output);
        return vectors;

    }

    public Vector[] getNullSpace() {
        output.println("To get nullspace, we need to convert to rref");
        Matrix copy = new Matrix(this);
        copy.toReducedRowEchelon();

        Fraction[][] arr = new Fraction[copy.matrix[0].length][copy.matrix[0].length];
        for (int i = 0; i < copy.leadingCol.length; i++) {
            int leading = copy.leadingCol[i];
            if (leading != Integer.MAX_VALUE) {
                for (int j = copy.leadingCol[i] + 1; j < copy.matrix[i].length; j++) {
                    if (!copy.matrix[i][j].isZero()) {
                        //System.out.println(copy.leadingCol[i] + ": Free variable found at " + i + " " + j);
                        arr[j][leading] = Fraction.invertSign(copy.matrix[i][j]);
                        arr[j][j] = Fraction.ONE;
                    }
                }
            }
        }

        ArrayList<Vector> list = new ArrayList<>(copy.leadingCol.length);
        for (Fraction[] vec : arr) {
            boolean isZeroVector = true;
            for (int i = 0; i < vec.length; i++) {
                if (vec[i] == null) {
                    vec[i] = Fraction.ZERO;
                } else if (!vec[i].isZero()) {
                    isZeroVector = false;
                }
            }

            if (!isZeroVector) {
                list.add(new Vector(vec, toLatex, output));
            }
        }

        if (list.isEmpty()) {
            list.add(new Vector(arr[0], toLatex, output));
        }

        Vector[] vectors = list.toArray(new Vector[list.size()]);
        output.println("Therefore the nullspace is:");
        printMatrices(vectors, output);
        return vectors;
    }

    public Vector[] getOrthogonalBasis() {
        output.println("To get basis, we first need to get the nullspace of the matrix");
        Vector[] basis = getNullSpace();
        output.println("Now we can orthogonalize basis using Graham-Shmidt");
        basis = Vector.orthogonalize(basis);
        return basis;
    }

    public void normalizeLeading() {
        for (int i = 0; i < matrix.length; i++) {
            if (leadingCol[i] < matrix[i].length) {
                Fraction div = matrix[i][leadingCol[i]];
                printNormal(i, Fraction.flip(div));
                divideRow(i, div);
            }
        }
    }

    public static Matrix multiply(Matrix mul, Fraction c) {
        mul = new Matrix(mul);
        for (int i = 0; i < mul.leadingCol.length; i++) {
            mul.multiplyRow(i, c);
        }
        return mul;
    }

    public void multiplyRow(int row, Fraction c) {
        for (int j = leadingCol[row]; j < matrix[row].length; j++) {
            if (!matrix[row][j].isZero()) {
                matrix[row][j] = Fraction.multiply(matrix[row][j], c);
            }
        }
    }

    public void divideRow(int row, Fraction c) {
        for (int j = leadingCol[row]; j < matrix[row].length; j++) {
            if (!matrix[row][j].isZero()) {
                matrix[row][j] = Fraction.divide(matrix[row][j], c);
            }
        }
    }

    public void subRow(int rowDest, int rowSource, Fraction c) {
        boolean hasNoLeading = true;
        for (int i = 0; i < matrix[rowDest].length; i++) {
            matrix[rowDest][i] = Fraction.subtract(matrix[rowDest][i], Fraction.multiply(c, matrix[rowSource][i]));
            if (hasNoLeading && !matrix[rowDest][i].isZero()) {
                hasNoLeading = false;
                leadingCol[rowDest] = i;
            }
        }

        if (hasNoLeading) {
            leadingCol[rowDest] = Integer.MAX_VALUE;
        }
    }
    
    public void swapRows(int x, int y) {
        if (toLatex) {
            output.printf("\\[R_%d \\leftrightarrow R_%d\\]\n", x + 1, y + 1);
        } else {
            output.printf("R_%d <-> R_%d\n", x + 1, y + 1);
        }

        int tempLead = leadingCol[x];
        leadingCol[x] = leadingCol[y];
        leadingCol[y] = tempLead;

        for (int i = 0; i < matrix[x].length; i++) {
            Fraction temp = matrix[x][i];
            matrix[x][i] = matrix[y][i];
            matrix[y][i] = temp;
        }
    }

    public Matrix transpose() {
        Fraction[][] newMatrix = new Fraction[matrix[0].length][matrix.length];
        leadingCol = new int[newMatrix.length];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                newMatrix[j][i] = matrix[i][j];
            }
        }

        return new Matrix(newMatrix, toLatex, output);
    }

    public Matrix getInverse() {
        output.println("Converting to augmented matrix and getting RREF");
        Number[][] identity = new Number[matrix.length][matrix[0].length];
        for (int i = 0; i < identity.length; i++) {
            for (int j = 0; j < identity[i].length; j++) {
                if (i == j) {
                    identity[i][j] = 1;
                } else {
                    identity[i][j] = 0;
                }
            }
        }
        AugmentedMatrix inMatrix = new AugmentedMatrix(matrix, identity, toLatex, output);
        output.println(inMatrix);
        inMatrix.toReducedRowEchelon();
        Matrix inverse = new Matrix(inMatrix.toArrays()[1]);
        output.println(inverse);
        return inverse;
    }

    private void cancelRows(int rowDest, int rowSource, int y) {
        Fraction num = Fraction.divide(matrix[rowDest][y], matrix[rowSource][y]);
        printCancel(rowDest, rowSource, num);
        subRow(rowDest, rowSource, num);
    }

    private void printNormal(int row, Fraction num) {
        if (!num.isOne()) {
            output.printf("%s R_%d%s\n", toLatex ? "\\[" + num.toStringLatex() : num.toString(), row + 1, toLatex ? "\\]" : "");
        }
    }

    private void printCancel(int x1, int x2, Fraction num) {
        boolean inverted = false;
        if (num.isNegative()) {
            inverted = true;
            num = Fraction.invertSign(num);
        }

        output.printf("%sR_%d %c %s R_%d%s\n", (toLatex) ? "\\[" : "", x1 + 1, (inverted) ? '+' : '-', num.isOne() ? "" : ((toLatex) ? num.toStringLatex() : num.toString()), x2 + 1, (toLatex) ? "\\]" : "");
    }

    @Override
    public String toString() {
        if (toLatex) {
            return toStringLatex();
        }
        return toStringDefault();
    }

    private int[] getMax() {
        int[] max = new int[matrix[0].length];
        for (Fraction[] row : matrix) {
            for (int j = 0; j < row.length; j++) {
                int len = row[j].toString().length();
                if (max[j] < len) {
                    max[j] = len;
                }
            }
        }

        for (int i = 0; i < max.length; i++) {
            max[i]++;
        }

        return max;
    }

    public boolean getLatex() {
        return toLatex;
    }

    public Fraction[][] toArray() {
        Fraction[][] newArr = new Fraction[matrix.length][matrix[0].length];
        for (int i = 0; i < matrix.length; i++) {
            System.arraycopy(matrix[i], 0, newArr[i], 0, matrix[i].length);
        }

        return newArr;
    }

    public int[] getDimensions() {
        return new int[]{matrix.length, matrix[0].length};
    }

    private String toStringDefault() {
        int[] max = getMax();
        StringBuilder sb = new StringBuilder();
        for (Fraction[] row : matrix) {
            sb.append("|");
            for (int i = 0; i < row.length; i++) {
                sb.append(String.format("%" + max[i] + "s", row[i]));
            }
            sb.append("|\n");
        }
        return sb.toString();
    }

    public String toStringLatex() {
        StringBuilder sb = new StringBuilder();
        sb.append("\\[\\begin{bmatrix}");
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                sb.append(String.format("%s", matrix[i][j].toStringLatex()));
                if (j < matrix[i].length - 1) {
                    sb.append(" & ");
                }
            }
            if (i < matrix.length - 1) {
                sb.append("\\\\");
            }
        }
        sb.append("\\end{bmatrix}\\]");
        return sb.toString();
    }

    public String toStringCustom(String delim, String eol) {
        int[] max = new int[matrix.length];
        for (Fraction[] row : matrix) {
            for (int i = 0; i < row.length; i++) {
                String str = row[i].toString();
                if (str.length() > max[i]) {
                    max[i] = str.length();
                }
            }
        }

        StringBuilder sb = new StringBuilder();
        for (Fraction[] row : matrix) {
            for (int i = 0; i < row.length; i++) {
                sb.append(String.format("%" + max[i] + "s %s ", row[i], delim));
            }
            sb.append(eol);
        }
        return sb.toString();
    }

    public static void printMatrices(Matrix[] arr, PrintStream output) {
        if (arr == null || arr.length == 0) {
            return;
        }

        Scanner[] scannerArr = new Scanner[arr.length];
        for (int i = 0; i < arr.length; i++) {
            scannerArr[i] = new Scanner(arr[i].toStringDefault());
        }

        // Print commas on every first row
        boolean first = true;
        // Assumes every matrix has same number of rows!
        while (scannerArr[0].hasNextLine()) {
            for (int i = 0; i < scannerArr.length; i++) {
                output.print(scannerArr[i].nextLine());
                if (first && i != scannerArr.length - 1) {
                    output.print(',');
                } else {
                    output.print(' ');
                }
            }
            first = false;
            output.println();
        }
    }
}
