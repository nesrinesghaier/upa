package net.vpc.upa.impl.persistence.specific.derby;

import net.vpc.upa.PortabilityHint;
import net.vpc.upa.impl.uql.compiledexpression.CompiledIf;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: root
 * Date: 22 mai 2003
 * Time: 17:26:10
 * To change this template use Options | File Templates.
 */
@PortabilityHint(target = "C#", name = "suppress")
class DerbyIfSQLProvider extends DerbyFunctionSQLProvider {
    DerbyIfSQLProvider() {
        super(CompiledIf.class);
    }

    @Override
    public String simplify(String functionName, String[] params, Map<String, Object> context) {
        if (params.length < 3 || params.length % 2 == 0) {
            return error("bad number of params", params);
        }
        StringBuilder sb = new StringBuilder("case");
        for (int i = 0; i < params.length; i += 2) {
            if (i == params.length - 1) {
                sb.append(" else ").append("(").append(params[i]).append(")").append(" end");
            } else {
                sb.append(" when ").append("(").append(params[i]).append(")").append(" then ").append("(").append(params[i + 1]).append(")");
            }
        }
        return sb.toString();
    }
}