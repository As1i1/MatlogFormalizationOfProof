public class Variable implements Expression {

    public Variable(String name) {
        this.name = name;
    }

    @Override
    public String prefix_form() {
        return name;
    }

    @Override
    public String normal_form() {
        return name;
    }

    @Override
    public boolean check_type(String operator) {
        return false;
    }

    @Override
    public Expression getLeft() {
        return this;
    }

    @Override
    public Expression getRight() {
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Variable) {
            return prefix_form().equals(((Variable) obj).prefix_form());
        }
        return false;
    }

    private final String name;
}