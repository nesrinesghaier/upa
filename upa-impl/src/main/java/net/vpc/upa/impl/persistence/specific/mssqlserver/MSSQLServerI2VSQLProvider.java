package net.vpc.upa.impl.persistence.specific.mssqlserver;

import net.vpc.upa.impl.uql.compiledexpression.CompiledI2V;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: root
 * Date: 22 mai 2003
 * Time: 17:26:10
 * To change this template use Options | File Templates.
 */
class MSSQLServerI2VSQLProvider extends MSSQLServerFunctionSQLProvider {
    public MSSQLServerI2VSQLProvider() {
        super(CompiledI2V.class);
    }

    @Override
    public String simplify(String functionName, String[] params, Map<String, Object> context) {
        checkFunctionSignature(1, params);
        return params[0];
    }
}
