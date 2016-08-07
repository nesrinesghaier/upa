package net.vpc.upa.impl.uql.compiledexpression;

/**
 * @author Taha BEN SALAH <taha.bensalah@gmail.com>
 * @creationdate 12/29/12 5:52 PM
 */
public class CompiledVarVal extends DefaultCompiledExpressionImpl {

    private CompiledVar var;
    private DefaultCompiledExpression val;

    public CompiledVarVal(CompiledVar var, DefaultCompiledExpression val) {
        this.var = var;
        this.val = val;
        prepareChildren(var, val);
    }

    @Override
    public DefaultCompiledExpression[] getSubExpressions() {
        return new DefaultCompiledExpression[]{var, val};
    }

    @Override
    public void setSubExpression(int index, DefaultCompiledExpression expression) {
        switch (index) {
            case 0: {
                var = (CompiledVar) expression;
                prepareChildren(var);
                return;
            }
            case 1: {
                val = expression;
                prepareChildren(val);
                return;
            }
        }
        throw new UnsupportedOperationException("Invalid index");
    }

    public DefaultCompiledExpression copy() {
        return new CompiledVarVal(var == null ? null : ((CompiledVar) var.copy()), val == null ? null : ((DefaultCompiledExpression) val.copy()));
    }

    public CompiledVar getVar() {
        return var;
    }

    public DefaultCompiledExpression getVal() {
        return val;
    }
}
