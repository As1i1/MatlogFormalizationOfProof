import java.util.*;
import java.util.function.Function;

public class Main {

    private static final ExpressionParser parser = new ExpressionParser();
    private static final List<ProofLinePair> proofLines = new ArrayList<>();

    private static final Map<Pair<String, Map<String, Integer>>, List<Integer>> mapMPLong = new HashMap<>();

    private static final Map<Pair<String, Map<String, Integer>>, Integer> mapMPShort = new HashMap<>();

    private static final List<Function<Expression, Boolean>> ax = List.of(
            Main::axSch1, Main::axSch2, Main::axSch3, Main::axSch4,
            Main::axSch5, Main::axSch6, Main::axSch7, Main::axSch8,
            Main::axSch9, Main::axSch10
    );

    private static void parseLine(String line) {
        String[] expressions = line.split("\\|-");
        Expression proof = parser.parse(expressions[1]);
        List<Expression> context = new ArrayList<>();
        String[] tmp = expressions[0].split(",");
        for (String s : tmp) {
            if (!s.isBlank()) {
                context.add(parser.parse(s));
            }
        }
        ProofLinePair current = new ProofLinePair(context, proof);
        current.minimize = minimize(current);
        proofLines.add(current);
    }

    private static boolean axSch1(Expression expr) {
        if (!expr.check_type("->") || !expr.getRight().check_type("->")) {
            return false;
        }
        return expr.getLeft().prefix_form().equals(expr.getRight().getRight().prefix_form());
    }

    private static boolean axSch2(Expression expr) {
        if (!expr.check_type("->") || !expr.getRight().check_type("->")) {
            return false;
        }
        Expression impl1 = expr.getLeft();
        Expression impl2 = expr.getRight().getLeft();
        Expression impl3 = expr.getRight().getRight();
        if (!impl1.check_type("->") || !impl3.check_type("->") ||
                !impl2.check_type("->") || !impl2.getRight().check_type("->")) {
            return false;
        }
        boolean sameAlpha = impl1.getLeft().prefix_form().equals(impl2.getLeft().prefix_form()) &&
                impl1.getLeft().prefix_form().equals(impl3.getLeft().prefix_form());
        boolean sameBeta = impl1.getRight().prefix_form().equals(impl2.getRight().getLeft().prefix_form());
        boolean sameGamma = impl2.getRight().getRight().prefix_form().equals(impl3.getRight().prefix_form());
        return sameAlpha && sameBeta && sameGamma;
    }

    private static boolean axSch3(Expression expr) {
        if (!expr.check_type("->") || !expr.getRight().check_type("->") ||
                !expr.getRight().getRight().check_type("&")) {
            return false;
        }
        boolean sameAlpha = expr.getLeft().prefix_form().equals(expr.getRight().getRight().getLeft().prefix_form());
        boolean sameBeta = expr.getRight().getLeft().prefix_form().equals(expr.getRight().getRight().getRight().prefix_form());
        return sameAlpha && sameBeta;
    }

    private static boolean axSch4(Expression expr) {
        if (!expr.check_type("->") || !expr.getLeft().check_type("&")) {
            return false;
        }
        return expr.getLeft().getLeft().prefix_form().equals(expr.getRight().prefix_form());
    }

    private static boolean axSch5(Expression expr) {
        if (!expr.check_type("->") || !expr.getLeft().check_type("&")) {
            return false;
        }
        return expr.getLeft().getRight().prefix_form().equals(expr.getRight().prefix_form());
    }

    private static boolean axSch6(Expression expr) {
        if (!expr.check_type("->") || !expr.getRight().check_type("|")) {
            return false;
        }
        return expr.getLeft().prefix_form().equals(expr.getRight().getLeft().prefix_form());
    }

    private static boolean axSch7(Expression expr) {
        if (!expr.check_type("->") || !expr.getRight().check_type("|")) {
            return false;
        }
        return expr.getLeft().prefix_form().equals(expr.getRight().getRight().prefix_form());
    }

    private static boolean axSch8(Expression expr) {
        if (!expr.check_type("->") || !expr.getRight().check_type("->")) {
            return false;
        }
        Expression impl1 = expr.getLeft();
        Expression impl2 = expr.getRight().getLeft();
        Expression impl3 = expr.getRight().getRight();
        if (!impl1.check_type("->") || !impl2.check_type("->") ||
                !impl3.check_type("->") || !impl3.getLeft().check_type("|")) {
            return false;
        }
        boolean sameAlpha = impl1.getLeft().prefix_form().equals(impl3.getLeft().getLeft().prefix_form());
        boolean sameBeta = impl2.getLeft().prefix_form().equals(impl3.getLeft().getRight().prefix_form());
        boolean sameGamma = impl1.getRight().prefix_form().equals(impl3.getRight().prefix_form()) &&
                impl1.getRight().prefix_form().equals(impl2.getRight().prefix_form());
        return sameAlpha && sameBeta && sameGamma;
    }

    private static boolean axSch9(Expression expr) {
        if (!expr.check_type("->") || !expr.getRight().check_type("->")) {
            return false;
        }
        Expression impl1 = expr.getLeft();
        Expression impl2 = expr.getRight().getLeft();
        Expression neg = expr.getRight().getRight();
        if (!impl1.check_type("->") || !impl2.check_type("->") ||
                !impl2.getRight().check_type("!") || !neg.check_type("!")) {
            return false;
        }
        boolean sameAlpha = impl1.getLeft().prefix_form().equals(impl2.getLeft().prefix_form()) &&
                impl1.getLeft().prefix_form().equals(neg.getLeft().prefix_form());
        boolean sameBeta = impl1.getRight().prefix_form().equals(impl2.getRight().getLeft().prefix_form());
        return sameAlpha && sameBeta;
    }

    private static boolean axSch10(Expression expr) {
        if (!expr.check_type("->") || !expr.getLeft().check_type("!") ||
                !expr.getLeft().getLeft().check_type("!")) {
            return false;
        }
        return expr.getRight().prefix_form().equals(expr.getLeft().getLeft().getLeft().prefix_form());
    }

    private static int getHyp(ProofLinePair p) {
        return p.pos_context.getOrDefault(p.proof.prefix_form(), -1);
    }

    private static boolean isEqualsLists(ProofLinePair l1, ProofLinePair l2) {
        return l1.map_context.equals(l2.map_context);
    }

    private static Pair<Integer, Integer> getModusPonens(ProofLinePair p) {
        List<Integer> indexs = mapMPLong.getOrDefault(new Pair<>(p.proof.prefix_form(), p.map_context),
                new ArrayList<>());
        for (Integer i : indexs) {
            Expression shortExpr = proofLines.get(i - 1).proof.getLeft();
            Integer j = mapMPShort.getOrDefault(new Pair<>(shortExpr.prefix_form(), p.map_context), -1);
            if (j == -1) continue;
            return new Pair<>(j, i);
        }

        return new Pair<>(-1, -1);
    }

    private static ProofLinePair minimize(ProofLinePair p) {
        List<Expression> context = new ArrayList<>(p.context);
        Expression proof = p.proof;
        while (proof.check_type("->")) {
            context.add(proof.getLeft());
            proof = proof.getRight();
        }
        return new ProofLinePair(context, proof);
    }

    private static int getDeduction(ProofLinePair p, int index) {
        if (p.context.isEmpty() && !p.proof.check_type("->")) {
            return -1;
        }
        for (int i = index - 1; i >= 0; i--) {
            ProofLinePair cur = proofLines.get(i);
            if (isEqualsLists(cur.minimize, p.minimize) &&
                    cur.minimize.proof.prefix_form().equals(p.minimize.proof.prefix_form())) {
                return i + 1;
            }
        }
        return -1;
    }

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        List<String> lines = new ArrayList<>();
        while (in.hasNext()) {
            lines.add(in.nextLine());
            parseLine(lines.get(lines.size() - 1));
        }
        StringBuilder result = new StringBuilder();
        ProofLinePair p = null;
        no:
        for (int i = 0; i < proofLines.size(); ++i) {
            if (i != 0) {
                if (p.proof.check_type("->")) {
                    String s = p.proof.getRight().prefix_form();
                    if (mapMPLong.containsKey(new Pair<>(s, p.map_context))) {
                        mapMPLong.get(new Pair<>(p.proof.getRight().prefix_form(),
                                p.map_context)).add(i);
                    } else {
                        List<Integer> l = new ArrayList<>();
                        l.add(i);
                        mapMPLong.put(new Pair<>(p.proof.getRight().prefix_form(),
                                p.map_context), l);
                    }
                }
                mapMPShort.put(new Pair<>(p.proof.prefix_form(), p.map_context), i);
            }
            p = proofLines.get(i);
            String lineToWrite = "[" + (i + 1) + "] " + lines.get(i);
            for (int j = 0; j < 10; j++) {
                if (ax.get(j).apply(p.proof)) {
                    result.append(lineToWrite).append(" [Ax. sch. ").append(j + 1).append("]\n");
                    continue no;
                }
            }
            int indexHyp = getHyp(p);
            if (indexHyp != -1) {
                result.append(lineToWrite).append("  [Hyp. ").append(indexHyp).append("]\n");
                continue;
            }
            Pair<Integer, Integer> indexMP = getModusPonens(p);
            if (indexMP.first != -1 && indexMP.second != -1) {
                result.append(lineToWrite).append("  [M.P. ").append(indexMP.first).append(", ").append(indexMP.second).append("]\n");
                continue;
            }
            int indexDed = getDeduction(p, i);
            if (indexDed != -1) {
                result.append(lineToWrite).append(" [Ded. ").append(indexDed).append("]\n");
            } else {
                result.append(lineToWrite).append(" [Incorrect]\n");
            }

        }
        System.out.println(result);
    }
}








