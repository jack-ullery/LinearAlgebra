package linearalgebra;

import java.io.PrintStream;

/**
 *
 * @author Jack
 */
public class MapSorter<E> {

    /**
     * Sort a generic array using an integer array as a value map. Uses
     * selection sort which has O(n^2) complexity
     *
     */
    public static <E> void sort(int[] key, Swapper swapper) {
        if (key == null || swapper == null) {
            throw new NullPointerException();
        } 
        
        for(int i = 0; i < key.length; i++){
            int min = i;
            for(int j = i + 1; j < key.length; j++){
                if(key[min] > key[j]){
                    min = j;
                }
            }
            
            if(min != i){
                swapper.swap(i, min);
            }
        }
    }

    public static void main(String[] args) {
//        java.util.Random rand = new java.util.Random();
//        int[] a1 = {rand.nextInt(10), rand.nextInt(10), rand.nextInt(10), rand.nextInt(10)};
//        Integer[] a2 = {1, 2, 3, 4};
//
//        System.out.println(java.util.Arrays.toString(a1));
//        //sort(a1, a2, System.out);
//        System.out.println(java.util.Arrays.toString(a1));
//        System.out.println(java.util.Arrays.toString(a2));
    }
}
