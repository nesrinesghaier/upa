package net.vpc.upa.impl.uql.compiledexpression;

import net.vpc.upa.expressions.BinaryOperator;

import java.math.BigInteger;

import net.vpc.upa.impl.ext.expressions.CompiledExpressionExt;
import net.vpc.upa.impl.transform.IdentityDataTypeTransform;

public final class CompiledXOr extends CompiledBinaryOperatorExpression
        implements Cloneable {

    private static final long serialVersionUID = 1L;

    public CompiledXOr(CompiledExpressionExt left, Object right) {
        super(BinaryOperator.XOR, left, right);
        Class t = left.getTypeTransform().getTargetType().getPlatformType();
        Class r = left.getTypeTransform().getTargetType().getPlatformType();
        if (BigInteger.class.equals(t) || BigInteger.class.equals(r)) {
            setTypeTransform(IdentityDataTypeTransform.BIGINT);
        } else if (Long.class.equals(t) || Long.class.equals(r)) {
            setTypeTransform(IdentityDataTypeTransform.LONG);
        } else if (Integer.class.equals(t) || Integer.class.equals(r)) {
            setTypeTransform(IdentityDataTypeTransform.INT);
        }
    }

    public CompiledXOr(CompiledExpressionExt left, CompiledExpressionExt right) {
        super(BinaryOperator.XOR, left, right);
        Class t = left.getTypeTransform().getTargetType().getPlatformType();
        Class r = left.getTypeTransform().getTargetType().getPlatformType();
        if (BigInteger.class.equals(t) || BigInteger.class.equals(r)) {
            setTypeTransform(IdentityDataTypeTransform.BIGINT);
        } else if (Long.class.equals(t) || Long.class.equals(r)) {
            setTypeTransform(IdentityDataTypeTransform.LONG);
        } else if (Integer.class.equals(t) || Integer.class.equals(r)) {
            setTypeTransform(IdentityDataTypeTransform.INT);
        }
    }
}
