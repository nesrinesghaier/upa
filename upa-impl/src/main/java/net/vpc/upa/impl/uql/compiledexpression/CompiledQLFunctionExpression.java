package net.vpc.upa.impl.uql.compiledexpression;

import net.vpc.upa.EvalContext;
import net.vpc.upa.PersistenceUnit;
import net.vpc.upa.Function;
import net.vpc.upa.exceptions.UPAIllegalArgumentException;
import net.vpc.upa.impl.ext.expressions.CompiledExpressionExt;
import net.vpc.upa.impl.uql.ExpressionCompiler;
import net.vpc.upa.types.DataTypeTransform;

/**
 * Created by IntelliJ IDEA.
 * User: root
 * Date: 22 mai 2003
 * Time: 12:07:34
 * To change this template use Options | File Templates.
 */
public class CompiledQLFunctionExpression extends CompiledFunction {
    private static final long serialVersionUID = 1L;
    protected Function handler;

    public CompiledQLFunctionExpression(String name, CompiledExpressionExt[] arguments, DataTypeTransform type, Function handler) {
        super(name);
        for (CompiledExpressionExt a : arguments) {
            protectedAddArgument(a);
        }
        this.handler = handler;
        setTypeTransform(type);
//        bindChildren(arguments);
    }

    @Override
    public CompiledExpressionExt copy() {
        CompiledExpressionExt[] arguments = getArguments();
        for (int i = 0; i < arguments.length; i++) {
            arguments[i]=arguments[i].copy();
        }
        CompiledQLFunctionExpression o = new CompiledQLFunctionExpression(getName(), arguments, getTypeTransform(),handler);
        o.setDescription(getDescription());
        o.getClientParameters().setAll(getClientParameters());
        return o;
    }

    public CompiledExpressionExt expand(PersistenceUnit persistenceUnit, ExpressionCompiler expressionCompiler) {
        int argumentsCount = getArgumentsCount();
        Object[] args=new Object[argumentsCount];
        for (int i = 0; i < args.length; i++) {
            args[i]=eval(getArgument(i), persistenceUnit,expressionCompiler);
        }
        Object v = getHandler().eval(new EvalContext(getName(),args,persistenceUnit,expressionCompiler));
        return new CompiledParam(v, null, getTypeTransform(), false);
    }

    protected Object eval(CompiledExpressionExt o, PersistenceUnit persistenceUnit, ExpressionCompiler expressionCompiler){
        if(o==null){
            return null;
        }
        if(o instanceof CompiledQLFunctionExpression){
            CompiledQLFunctionExpression s=(CompiledQLFunctionExpression) o;
            int argumentsCount = s.getArgumentsCount();
            Object[] args=new Object[argumentsCount];
            for (int i = 0; i < args.length; i++) {
                args[i]=eval(s.getArgument(i), persistenceUnit,expressionCompiler);
            }
            return (s.getHandler().eval(new EvalContext(s.getName(),args,persistenceUnit,expressionCompiler)));
        }
        if(o instanceof CompiledLiteral){
            return ((CompiledLiteral)o).getValue();
        }
        if(o instanceof CompiledParam){
            return ((CompiledParam)o).getValue();
        }
        throw new UPAIllegalArgumentException("Unable to evaluate type "+o.getClass()+" :: "+o);
    }

    

    public Function getHandler() {
        return handler;
    }

}
