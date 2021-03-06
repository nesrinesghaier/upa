package net.vpc.upa.impl.uql.compiledexpression;

import net.vpc.upa.expressions.BinaryOperator;

import java.math.BigInteger;

import net.vpc.upa.impl.ext.expressions.CompiledExpressionExt;
import net.vpc.upa.impl.transform.IdentityDataTypeTransform;


public final class CompiledLShift extends CompiledBinaryOperatorExpression
        implements Cloneable {

    private static final long serialVersionUID = 1L;

    public CompiledLShift(CompiledExpressionExt left, Object right) {
        super(BinaryOperator.LSHIFT, left, right);
    }

    public CompiledLShift(CompiledExpressionExt left, CompiledExpressionExt right) {
        super(BinaryOperator.LSHIFT, left, right);
    }
}
