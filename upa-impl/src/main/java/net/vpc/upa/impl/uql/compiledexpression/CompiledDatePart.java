package net.vpc.upa.impl.uql.compiledexpression;

import net.vpc.upa.expressions.DatePartType;

import java.util.Date;

import net.vpc.upa.impl.ext.expressions.CompiledExpressionExt;
import net.vpc.upa.impl.transform.IdentityDataTypeTransform;
import net.vpc.upa.types.DataTypeTransform;

/**
 * Created by IntelliJ IDEA.
 * User: root
 * Date: 22 mai 2003
 * Time: 12:07:34
 * To change this template use Options | File Templates.
 */
public class CompiledDatePart extends CompiledFunction {
    private static final long serialVersionUID = 1L;

    public CompiledDatePart(DatePartType type, Date date) {
        this(type, new CompiledLiteral(date));
    }

    public CompiledDatePart(DatePartType type, String varDate) {
        this(type, new UserCompiledExpression(varDate,IdentityDataTypeTransform.DATETIME));
    }

    public CompiledDatePart(DatePartType type, CompiledExpressionExt value) {
        super("Datepart");
        protectedAddArgument(new CompiledCst(type));
        protectedAddArgument(value);
    }

    public CompiledCst getDatePartTypeExpression() {
        return (CompiledCst)getArgument(0);
    }
    
    public DatePartType getDatePartType() {
        return (DatePartType)getDatePartTypeExpression().getValue();
    }

    public CompiledExpressionExt getValue() {
        return getArgument(1);
    }

    @Override
    public DataTypeTransform getTypeTransform() {
        return IdentityDataTypeTransform.STRING;
    }

    @Override
    public CompiledExpressionExt copy() {
        CompiledDatePart o=new CompiledDatePart(getDatePartType(), getValue().copy());
        o.setDescription(getDescription());
        o.getClientParameters().setAll(getClientParameters());
        return o;
    }
}
