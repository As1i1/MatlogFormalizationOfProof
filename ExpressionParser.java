import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BinaryOperator;

class ExpressionParser {

    public Expression parse(String expression) {
        return new Parser(expression).parseStart(BaseParser.END);
    }

    private static class Parser extends BaseParser {

        private static final Map<String, BinaryOperator<Expression>> OPERATORS_CONSTRUCTORS = Map.ofEntries(Map.entry("&", Conjunction::new), Map.entry("|", Disjunction::new), Map.entry("->", Implication::new));

        Parser(String source) {
            super(source);
        }

        private Expression parseStart(char endSymbol) {
            return parseImplication(endSymbol);
        }

        private Expression foldRight(List<Expression> expressions, String operator) {
            Expression current = expressions.get(expressions.size() - 1);
            for (int i = expressions.size() - 2; i >= 0; i--) {
                current = union(expressions.get(i), current, operator);
            }
            return current;
        }

        private Expression parseImplication(char endSymbol) {
            List<Expression> list = new ArrayList<>();
            list.add(parseDisjunction(endSymbol));
            skipWhitespace();
            while (!test(endSymbol)) {
                String operator = getOperator();
                if (!operator.equals("->")) {
                    return foldRight(list, "->");
                }
                takeOperator(operator);
                list.add(parseDisjunction(endSymbol));
                skipWhitespace();
            }
            return foldRight(list, "->");
        }

        private Expression parseDisjunction(char endSymbol) {
            Expression left = parseConjunction(endSymbol);
            skipWhitespace();
            while (!test(endSymbol)) {
                String operator = getOperator();
                if (!operator.equals("|")) {
                    return left;
                }
                takeOperator(operator);
                Expression right = parseConjunction(endSymbol);
                left = union(left, right, operator);
                skipWhitespace();
            }
            return left;
        }

        private Expression parseConjunction(char endSymbol) {
            Expression left = parseExpression();
            skipWhitespace();
            while (!test(endSymbol)) {
                String operator = getOperator();
                if (!operator.equals("&")) {
                    return left;
                }
                takeOperator(operator);
                Expression right = parseExpression();
                left = union(left, right, operator);
                skipWhitespace();
            }
            return left;
        }

        private Expression union(Expression left, Expression right, String operator) {
            return OPERATORS_CONSTRUCTORS.get(operator).apply(left, right);
        }

        private Expression parseExpression() {
            skipWhitespace();
            if (take('(')) {
                skipWhitespace();
                Expression expression = parseStart(')');
                take(')');
                return expression;
            } else {
                if (take('!')) {
                    return new Negation(parseExpression());
                } else {
                    return new Variable(getNextToken());
                }
            }
        }

        private String getOperator() {
            int position = getPos();
            StringBuilder sb = new StringBuilder();
            while (test(c -> c == '&' || c == '|' || c == '-' || c == '>')) {
                sb.append(take());
            }
            setPos(position);
            return sb.toString();
        }

        private void takeOperator(String operation) {
            expect(operation);
            skipWhitespace();
        }

        private String getNextToken() {
            StringBuilder sb = new StringBuilder();
            while (test(Character::isLetter) || test(Character::isDigit) || test('\'')) {
                sb.append(take());
            }
            return sb.toString();
        }

    }
}
