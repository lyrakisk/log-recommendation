import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.GenericListVisitorAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MethodCallVisitor extends GenericListVisitorAdapter<Boolean, Void> {
    public boolean isLogger = false;

    private String[] logLevels = {"debug", "error", "fatal", "info", "warn", "trace"};
    private List<String> logLevelsList = Arrays.asList(logLevels);

    public List<Boolean> visit(MethodCallExpr n, Void arg)  {
        ArrayList<Boolean> result = new ArrayList<>();
        if (logLevelsList.contains(n.getNameAsString())) {
            isLogger = true;
            result.add(isLogger);
        }
        // Don't forget to call super, it may find more method calls inside the arguments of this method call, for example.
        super.visit(n, arg);
        return result;
    }
}
