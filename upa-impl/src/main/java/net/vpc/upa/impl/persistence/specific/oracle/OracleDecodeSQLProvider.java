package net.vpc.upa.impl.persistence.specific.oracle;

import net.vpc.upa.PortabilityHint;
import net.vpc.upa.impl.uql.compiledexpression.CompiledDecode;

import java.util.Arrays;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: root
 * Date: 22 mai 2003
 * Time: 17:17:34
 * To change this template use Options | File Templates.
 */
@PortabilityHint(target = "C#", name = "suppress")
class OracleDecodeSQLProvider extends OracleFunctionSQLProvider {
    public OracleDecodeSQLProvider() {
        super(CompiledDecode.class);
    }

    public String simplify(String functionName, String[] params, Map<String, Object> context) {
        System.out.println("OracleDecodeSQLProvider : " + functionName + Arrays.asList(params));
        if (params.length < 2) {
            return error("requieres at least 2 arguments", params);
        } else {
            StringBuilder sb = new StringBuilder("decode(");
            for (int i = 0; i < params.length; i++) {
                if (i > 0) {
                    sb.append(", ");
                }
                sb.append(params[i]);
            }
            sb.append(")");
            return sb.toString();
        }
    }
}
