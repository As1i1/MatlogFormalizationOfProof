import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProofLinePair {

    public ProofLinePair(List<Expression> context, Expression proof) {
        this.context = context;
        this.proof = proof;
        Pair<Map<String, Integer>, Map<String, Integer>> tmp = listToMap(context);
        map_context = tmp.first;
        pos_context = tmp.second;
    }

    private static Pair<Map<String, Integer>, Map<String, Integer>> listToMap(List<Expression> l1) {
        Map<String, Integer> result = new HashMap<>();
        Map<String, Integer> cnt = new HashMap<>();
        int i = 1;
        for (Expression expr : l1) {
            String tmp = expr.prefix_form();
            result.put(tmp, result.getOrDefault(tmp, 0) + 1);
            cnt.put(tmp, i);
            i++;
        }
        return new Pair<>(result, cnt);
    }

    List<Expression> context;
    Expression proof;

    Map<String, Integer> map_context;
    Map<String, Integer> pos_context;

    ProofLinePair minimize;
}