package linearalgebra;

/**
 *
 * @author Jack
 */
public class Fraction extends Number {

    public final int NUM;
    public final int DENOM;

    public static final Fraction ZERO = new Fraction(0, 1);
    public static final Fraction ONE = new Fraction(1, 1);

    private static int gcd(int a, int b) {

        int[] r;
        if (a > b) {
            r = new int[]{b, a};
        } else {
            r = new int[]{a, b};
        }

        while (r[0] != 0) {
            int q = r[1] / r[0];
            r = new int[]{r[1] - q * r[0], r[0]};
        }

        return r[1];
    }

    public Fraction(int num, int denom) {
        if (denom == 0) {
            throw new IllegalArgumentException("Divide by zero");
        } else if (num == 0) {
            denom = 1;
        }
        int gcd = gcd(num, denom);
        num /= gcd;
        denom /= gcd;
        if(denom < 0){
            num *= -1;
            denom *= -1;
        }
        this.NUM = num;
        this.DENOM = denom;
    }

    public Fraction(Fraction f) {
        this.NUM = f.NUM;
        this.DENOM = f.DENOM;
    }

    public Fraction(double dub) {
        String s = String.valueOf(dub);
        int digitsDec = s.length() - 1 - s.indexOf('.');

        int denom = 1;
        for (int i = 0; i < digitsDec; i++) {
            dub *= 10;
            denom *= 10;
        }
        int num = (int) Math.round(dub);

        if (num == 0 || denom == 0) {
            this.NUM = 0;
            this.DENOM = 1;
        } else {
            int gcd = gcd(num, denom);
            this.NUM = num / gcd;
            this.DENOM = denom / gcd;
        }
    }

    // Possible overflow
    public static Fraction add(Fraction a, Fraction b) {
        int gcd = gcd(a.DENOM, b.DENOM);
        int num1 = a.DENOM / gcd;
        int num2 = b.DENOM / gcd;
        return new Fraction((a.NUM * num2) + (b.NUM * num1), num1 * b.DENOM);
    }

    // Possible overflow
    public static Fraction subtract(Fraction a, Fraction b) {
        int gcd = gcd(a.DENOM, b.DENOM);
        int num1 = a.DENOM / gcd;
        int num2 = b.DENOM / gcd;
        return new Fraction((a.NUM * num2) - (b.NUM * num1), num1 * b.DENOM);
    }

    public static Fraction multiply(Fraction a, Fraction b) {
        return new Fraction(a.NUM * b.NUM, a.DENOM * b.DENOM);
    }

    public static Fraction divide(Fraction a, Fraction b) {
        return new Fraction(a.NUM * b.DENOM, a.DENOM * b.NUM);
    }

    public static Fraction invertSign(Fraction a) {
        return new Fraction(a.NUM, -a.DENOM);
    }

    public static Fraction flip(Fraction a) {
        return new Fraction(a.DENOM, a.NUM);
    }

    public boolean isZero() {
        return this.NUM == 0;
    }

    public boolean isOne() {
        return this.NUM == 1 && this.DENOM == 1;
    }

    public boolean isUnit() {
        return (this.NUM == 1 || this.NUM == -1) && (this.DENOM == 1 || this.DENOM == -1);
    }

    public boolean isNegative() {
        return this.DENOM < 0 ^ this.NUM < 0;
    }

    public String toString() {
        if (DENOM == 1) {
            return "" + NUM;
        } else if (DENOM == -1) {
            return "-" + NUM;
        }
        return NUM + "/" + DENOM;
    }

    public String toStringLatex() {
        if (DENOM == 1) {
            return "" + NUM;
        } else if (DENOM == -1) {
            return "-" + NUM;
        }
        return String.format("\\frac{%d}{%d}", NUM, DENOM);
    }

    @Override
    public boolean equals(Object thatO) {
        if (thatO instanceof Fraction) {
            @SuppressWarnings("unchecked")
            Fraction that = (Fraction) thatO;
            return this.NUM == that.NUM && this.DENOM == that.DENOM;
        }
        return false;
    }

    @Override
    public int intValue() {
        return this.NUM / this.DENOM;
    }

    @Override
    public long longValue() {
        return ((long) this.NUM) / this.DENOM;
    }

    @Override
    public float floatValue() {
        return ((float) this.NUM) / this.DENOM;
    }

    @Override
    public double doubleValue() {
        return ((double) this.NUM) / this.DENOM;
    }
}
