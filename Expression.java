interface Expression {
    String prefix_form();

    String normal_form();

    boolean check_type(String operator);

    Expression getLeft();

    Expression getRight();
}