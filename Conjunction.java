public class Conjunction implements Expression {

    public Conjunction(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public String prefix_form() {
        return "(&," + left.prefix_form() + "," + right.prefix_form() + ")";
    }

    @Override
    public String normal_form() {
        return "(" + left.normal_form() + "&" + right.normal_form() + ")";
    }

    @Override
    public boolean check_type(String operator) {
        return operator.equals("&");
    }

    @Override
    public Expression getLeft() {
        return left;
    }

    @Override
    public Expression getRight() {
        return right;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Conjunction) {
            return prefix_form().equals(((Conjunction) obj).prefix_form());
        }
        return false;
    }

    public final Expression left;
    public final Expression right;
}