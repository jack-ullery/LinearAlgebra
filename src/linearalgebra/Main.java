package linearalgebra;

/**
 *
 * @author Jack
 */
public class Main {

    public static void main(String[] args) {
        Number[][] mat = {
            {1,0},
            {0,-1}
        };

        Number[] aug = {
            1,1,1/4
        };
        Matrix m = new Matrix(mat, true, System.out);
        m.getInverse();

//        Vector v = new Vector(new Number[]{1,4,0}, false, System.out);
//        Vector a = new Vector(new Number[]{2,-1,1}, false, System.out);
//        Vector b = new Vector(new Number[]{3,-1,0}, false, System.out);
//        v.orthogonallyDecompose(new Vector[]{a, b});
        //v.orthogonallyDecompose(m);
    }
}
