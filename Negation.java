public class Negation implements Expression {

    public Negation(Expression expr) {
        this.expr = expr;
    }

    @Override
    public String prefix_form() {
        return "(!" + expr.prefix_form() + ")";
    }

    @Override
    public String normal_form() {
        return prefix_form();
    }

    @Override
    public boolean check_type(String operator) {
        return operator.equals("!");
    }

    @Override
    public Expression getLeft() {
        return expr;
    }

    @Override
    public Expression getRight() {
        return expr;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Negation) {
            return prefix_form().equals(((Negation) obj).prefix_form());
        }
        return false;
    }


    private final Expression expr;
}