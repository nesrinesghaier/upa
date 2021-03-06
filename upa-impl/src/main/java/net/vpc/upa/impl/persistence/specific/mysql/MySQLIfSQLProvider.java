package net.vpc.upa.impl.persistence.specific.mysql;

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
class MySQLIfSQLProvider extends MySQLFunctionSQLProvider {
    MySQLIfSQLProvider() {
        super(CompiledIf.class);
    }

    public String simplify(String functionName, String[] params, Map<String, Object> context) {
        if (params.length < 3 || params.length % 2 == 0) {
            return error("bad number of params", params);
        }
        String s = params[params.length - 1];
        for (int i = params.length - 3; i >= 0; i -= 2) {
            s = "if(" + params[i] + "," + params[i + 1] + "," + s + ")";
        }
        return s;
    }
}
