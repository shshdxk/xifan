package io.github.shshdxk.data.specification;



import java.util.Objects;

public enum Operator {
    gt(">"),
    ge(">="),
    lt("<"),
    le("<="),
    eq("=");

    private final String op;

    private Operator(String op) {
        this.op = op;
    }

    /**
     * @param op &gt;, &ge;, &lt;, &le;, =;
     * @return
     */
    public static Operator fromOp(String op) {
        Operator[] var1 = values();

        for (Operator each : var1) {
            if (Objects.equals(op, each.op)) {
                return each;
            }
        }

        throw new IllegalArgumentException("Unknown op " + op);
    }

    public String getOp() {
        return op;
    }
}