package net.vpc.upa.impl.persistence.specific.mssqlserver;

import net.vpc.upa.PortabilityHint;
import net.vpc.upa.impl.uql.compiledexpression.CompiledMonthStart;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: root
 * Date: 22 mai 2003
 * Time: 17:26:10
 * To change this template use Options | File Templates.
 */
@PortabilityHint(target = "C#",name = "suppress")
class MSSQLServerMonthStartSQLProvider extends MSSQLServerFunctionSQLProvider {
    public MSSQLServerMonthStartSQLProvider() {
        super(CompiledMonthStart.class);
    }

    @Override
    public String simplify(String functionName, String[] params, Map<String, Object> context) {
        String date = "getDate()";
        String count = "0";
        if (params.length == 0) {
            //
        } else if (params.length == 1) {
            count = "("+params[0]+")";
        } else if (params.length == 2) {
            date = "("+params[0]+")";
            count = "("+params[0]+")";
        } else {
            checkFunctionSignature(new String[]{"date", "diffCount"}, params);
        }
        return "dateadd(day,1-datepart(day,dateadd(month," + count + "," + date + ")),dateadd(month," + count + "," + date + "))";
    }
}
