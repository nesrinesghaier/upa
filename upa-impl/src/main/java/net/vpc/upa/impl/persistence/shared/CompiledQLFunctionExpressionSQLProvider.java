package net.vpc.upa.impl.persistence.shared;

import net.vpc.upa.EvalContext;
import net.vpc.upa.exceptions.UPAException;
import net.vpc.upa.impl.persistence.SQLManager;
import net.vpc.upa.impl.persistence.SQLProvider;
import net.vpc.upa.impl.uql.ExpressionDeclarationList;
import net.vpc.upa.impl.uql.compiledexpression.DefaultCompiledExpression;
import net.vpc.upa.impl.uql.compiledexpression.CompiledLiteral;
import net.vpc.upa.impl.uql.compiledexpression.CompiledParam;
import net.vpc.upa.impl.uql.compiledexpression.CompiledQLFunctionExpression;
import net.vpc.upa.persistence.EntityExecutionContext;

/**
 * @author Taha BEN SALAH <taha.bensalah@gmail.com>
 * @creationdate 12/14/12 12:28 AM
 */
public class CompiledQLFunctionExpressionSQLProvider implements SQLProvider {

    @Override
    public String getSQL(Object oo, EntityExecutionContext qlContext, SQLManager sqlManager, ExpressionDeclarationList declarations) throws UPAException {
        CompiledQLFunctionExpression o = (CompiledQLFunctionExpression) oo;
        int argumentsCount = o.getArgumentsCount();
        Object[] args = new Object[argumentsCount];
        for (int i = 0; i < args.length; i++) {
            args[i] = eval(o.getArgument(i), qlContext);
        }
        return sqlManager.getMarshallManager().formatSqlValue(o.getHandler().eval(new EvalContext(o.getName(), args, qlContext.getPersistenceUnit())));
    }

    protected Object eval(DefaultCompiledExpression o, EntityExecutionContext qlContext) {
        if (o == null) {
            return null;
        }
        if (o instanceof CompiledQLFunctionExpression) {
            CompiledQLFunctionExpression s = (CompiledQLFunctionExpression) o;
            int argumentsCount = s.getArgumentsCount();
            Object[] args = new Object[argumentsCount];
            for (int i = 0; i < args.length; i++) {
                args[i] = eval(s.getArgument(i), qlContext);
            }
            return (s.getHandler().eval(new EvalContext(s.getName(), args, qlContext.getPersistenceUnit())));
        }
        if (o instanceof CompiledLiteral) {
            return ((CompiledLiteral) o).getValue();
        }
        if (o instanceof CompiledParam) {
            return ((CompiledParam) o).getValue();
        }
        throw new IllegalArgumentException("Unable to evaluate type " + o.getClass() + " :: " + o);
    }

    @Override
    public Class<CompiledQLFunctionExpression> getExpressionType() {
        return CompiledQLFunctionExpression.class;
    }
}
