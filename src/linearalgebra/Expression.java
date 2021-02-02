package linearalgebra;

import java.util.*;
import java.util.Map.Entry;

/**
 *
 * @author Jack
 */
public class Expression {

    private static class Node implements Comparable<Node> {

        Fraction coefficent;
        final Set<String> set = new TreeSet<>();
        final TreeMap<String, Integer> map;

        public Node(String var) {
            map = new TreeMap<>();
            addVar(var);
            coefficent = new Fraction(1);
        }

        public Node(Fraction frac) {
            map = new TreeMap<>();
            coefficent = frac;
        }

        public Node(Node copy) {
            coefficent = copy.coefficent;
            set.addAll(copy.set);
            map = new TreeMap<>(copy.map);
        }

        public final void addVar(String var) {
            Integer value = map.get(var);
            if (value == null) {
                map.put(var, 1);
                set.add(var);
            } else {
                map.put(var, value + 1);
            }
        }

        public final void addVar(String var, Integer n) {
            Integer value = map.get(var);
            if (value == null) {
                map.put(var, n);
                set.add(var);
            } else {
                map.put(var, value + n);
            }
        }
        
        public void mult(Node that){
            Set<Entry<String, Integer>> entrySet = that.map.entrySet();
            for(Entry<String, Integer> entry : entrySet){
                addVar(entry.getKey(), entry.getValue());
            }
            this.coefficent = Fraction.multiply(this.coefficent, that.coefficent);
        }
        
        @Override
        public boolean equals(Object that) {
            if (that instanceof Node) {
                @SuppressWarnings("unchecked")
                boolean re = this.map.equals(((Node) that).map);
                return re;
//                @SuppressWarnings("unchecked")
//                Node that = (Node) thatO;
//                Iterator<Entry<String, Integer>> i1 = this.map.entrySet().iterator();
//                Iterator<Entry<String, Integer>> i2 = that.map.entrySet().iterator();
//                while(i1.hasNext() && i2.hasNext()){
//                    Entry e1 = i1.next();
//                    Entry e2 = i2.next();
//                    if(!e1.getKey().equals(e2.getKey()) || !e1.getValue().equals(e2.getValue())){
//                        return false;
//                    }
//                }
//                
//                if(i1.hasNext() == i2.hasNext()){
//                    return true;
//                }
            }
            return false;
        }

        @Override
        public int compareTo(Node that) {
            if(equals(that)){
                return 0;
            }
            return this.toString().compareTo(that.toString());
        }

        public String toString() {
            if (coefficent.isZero()) {
                return "";
            }

            StringBuilder sb = new StringBuilder();
            Set<Entry<String, Integer>> entrySet = map.entrySet();
            if (!coefficent.isUnit()) {
                sb.append(coefficent);
            } else if (coefficent.isNegative()) {
                sb.append('-');
            }

            boolean first = true;
            for (Entry<String, Integer> entry : entrySet) {
                if (!first) {
                    //sb.append(' ');
                }
                sb.append(entry.getKey());
                if (entry.getValue() != 1) {
                    sb.append('^').append(entry.getValue());
                }
                first = false;
            }

            return sb.toString();
        }

    }

    private TreeSet<Node> set = new TreeSet<>();

    //private Fraction coeff = new Fraction(1);
    //private String var;
    public Expression(String varName) {
        isValidName(varName);
        //var = varName;
        Node n = new Node(varName);
        set.add(n);
    }

    public Expression(Number num) {
        this(new Fraction(num.doubleValue()));
    }

    public Expression(Fraction value) {
        //coeff = value;
        set.add(new Node(value));
    }

    private Expression(TreeSet<Node> set) {
        this.set = set;
    }

    private static void isValidName(String name) {
        for (int i = 0; i < name.length(); i++) {
            if (!Character.isAlphabetic(name.charAt(i))) {
                throw new IllegalArgumentException("Invalid Variable Name!");
            }
        }
    }

    public static Expression add(Expression a, Expression b) {
        // Creates iterator in ascending order.
        Iterator<Node> it1 = a.set.iterator();
        TreeSet<Node> newSet = new TreeSet<>(b.set);

        // Could speed up with double pointers, but no thanks
        while (it1.hasNext()) {
            Node next = new Node(it1.next());
            Node ceil = newSet.ceiling(next);
            if (ceil != null && ceil.equals(next)) {
                ceil.coefficent = Fraction.add(next.coefficent, ceil.coefficent);
            } else {
                newSet.add(next);
            }
        }
        return new Expression(newSet);
    }

    public static Expression subtract(Expression b, Expression a) {
        Iterator<Node> it1 = a.set.iterator();
        TreeSet<Node> newSet = new TreeSet<>(b.set);
        while (it1.hasNext()) {
            Node next = new Node(it1.next());
            Node ceil = newSet.ceiling(next);
            next.coefficent = Fraction.invertSign(next.coefficent);
            if (ceil != null && ceil.equals(next)) {
                ceil.coefficent = Fraction.add(next.coefficent, ceil.coefficent);
                if (ceil.coefficent.isZero()) {
                    newSet.remove(ceil);
                }
            } else {
                newSet.add(next);
            }
        }
        return new Expression(newSet);
    }

    public static Expression multiply(Expression a, Expression b) {
        Iterator<Node> it1 = a.set.iterator();
        TreeSet<Node> newSet = new TreeSet<>();

        while (it1.hasNext()) {
            Node n1 = new Node(it1.next());
            Iterator<Node> it2 = b.set.iterator();
            while (it2.hasNext()) {
                Node n2 = it2.next();
                Node next = new Node(n1);
                next.mult(n2);
                Node ceil = newSet.ceiling(next);
                if (ceil != null && ceil.equals(next)) {
                    ceil.coefficent = Fraction.add(next.coefficent, ceil.coefficent);
                    if (ceil.coefficent.isZero()) {
                        newSet.remove(ceil);
                    }
                } else {
                    newSet.add(next);
                }
            }
        }
        return new Expression(newSet);        
    }

    public static Expression divide(Expression a, Expression b) {
        throw new UnsupportedOperationException();
    }

    public static Expression invert(Expression a) {
        throw new UnsupportedOperationException();
    }

    public static Expression flip(Expression a) {
        throw new UnsupportedOperationException();
    }

    public boolean isZero() {
        return set.isEmpty();
    }

//        if(a.isNumber() && b.isNumber()){
//        }
    @Override
    public String toString() {
        if (set.isEmpty()) {
            return "0";
        }
        StringBuilder sb = new StringBuilder();
        Iterator<Node> nodes = set.iterator();
        boolean first = true;
        while (nodes.hasNext()) {
            Node elem = nodes.next();
            if (!first && !elem.coefficent.isNegative()) {
                sb.append('+');
            }
            sb.append(elem);
            first = false;
        }
        return sb.toString();
    }
}
