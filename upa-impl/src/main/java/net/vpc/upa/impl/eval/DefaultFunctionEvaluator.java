/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.upa.impl.eval;

import net.vpc.upa.EvalContext;
import net.vpc.upa.QLEvaluator;
import net.vpc.upa.QLTypeEvaluator;
import net.vpc.upa.expressions.Cst;
import net.vpc.upa.expressions.Expression;
import net.vpc.upa.expressions.FunctionExpression;
import net.vpc.upa.expressions.Literal;
import net.vpc.upa.impl.uql.parser.syntax.FunctionFactory;
import net.vpc.upa.impl.util.UPAUtils;

import java.util.Arrays;

/**
 *
 * @author taha.bensalah@gmail.com
 */
public class DefaultFunctionEvaluator implements QLTypeEvaluator {
    private net.vpc.upa.Function evaluator;

    public DefaultFunctionEvaluator(net.vpc.upa.Function evaluator) {
        this.evaluator = evaluator;
    }

    @Override
    public final Expression evalObject(Expression e, QLEvaluator evaluator, Object context) {
        FunctionExpression f = (FunctionExpression) e;
        Expression[] arguments = f.getArguments();
        Object[] oarguments = new Object[arguments.length];
        boolean evaluatable=true;
        for (int i = 0; i < arguments.length; i++) {
            Expression expr = evaluator.evalObject(arguments[i], context);
            arguments[i]=expr;
            Object oo = UPAUtils.unwrapLiteral(expr);
            if(oo instanceof Expression){
                evaluatable=false;
            }else{
                oarguments[i]=oo;
            }
        }
        if(!evaluatable){
            return FunctionFactory.createFunction(f.getName(), Arrays.asList(arguments));
        }
        Object v = this.evaluator.eval(new EvalContext(f.getName(), oarguments, null,null));
        return new Literal(v,null);
    }

}
