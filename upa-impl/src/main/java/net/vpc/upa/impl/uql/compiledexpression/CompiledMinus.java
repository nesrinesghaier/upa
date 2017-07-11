package net.vpc.upa.impl.uql.compiledexpression;

import net.vpc.upa.exceptions.UPAIllegalArgumentException;
import net.vpc.upa.expressions.BinaryOperator;

import java.math.BigDecimal;
import java.math.BigInteger;

import net.vpc.upa.impl.ext.expressions.CompiledExpressionExt;
import net.vpc.upa.impl.transform.IdentityDataTypeTransform;

public final class CompiledMinus extends CompiledBinaryOperatorExpression
        implements Cloneable {

    private static final long serialVersionUID = 1L;

    public CompiledMinus(CompiledExpressionExt left, Object right) {
        super(BinaryOperator.MINUS, left, right);
        Class t = left.getTypeTransform().getTargetType().getPlatformType();
        Class r = left.getTypeTransform().getTargetType().getPlatformType();
        /**@PortabilityHint(target="C#",name="suppress")*/
        if (BigDecimal.class.equals(t) || BigDecimal.class.equals(r)) {
            setTypeTransform(IdentityDataTypeTransform.BIGDECIMAL);
        }
        if (BigInteger.class.equals(t) || BigInteger.class.equals(r)) {
            if (Double.class.equals(t) || Double.class.equals(r)) {
                /**@PortabilityHint(target="C#",name="replace")
                 * SetTypeTransform(Net.Vpc.Upa.Impl.Transform.IdentityDataTypeTransform.DOUBLE);
                 **/
                setTypeTransform(IdentityDataTypeTransform.BIGDECIMAL);
            } else if (Float.class.equals(t) || Float.class.equals(r)) {
                /**@PortabilityHint(target="C#",name="replace")
                 * SetTypeTransform(Net.Vpc.Upa.Impl.Transform.IdentityDataTypeTransform.DOUBLE);
                 **/
                setTypeTransform(IdentityDataTypeTransform.BIGDECIMAL);
            } else {
                setTypeTransform(IdentityDataTypeTransform.BIGINT);
            }
        } else if (Double.class.equals(t) || Double.class.equals(r)) {
            setTypeTransform(IdentityDataTypeTransform.DOUBLE);
        } else if (Float.class.equals(t) || Float.class.equals(r)) {
            setTypeTransform(IdentityDataTypeTransform.FLOAT);
        } else if (Long.class.equals(t) || Long.class.equals(r)) {
            setTypeTransform(IdentityDataTypeTransform.LONG);
        } else if (Integer.class.equals(t) || Integer.class.equals(r)) {
            setTypeTransform(IdentityDataTypeTransform.INT);
        } else {
            throw new UPAIllegalArgumentException("Unsupported types");
        }
    }

    public CompiledMinus(CompiledExpressionExt left, CompiledExpressionExt right) {
        super(BinaryOperator.MINUS, left, right);
        Class t = left.getTypeTransform().getTargetType().getPlatformType();
        Class r = left.getTypeTransform().getTargetType().getPlatformType();
        /**@PortabilityHint(target="C#",name="suppress")*/
        if (BigDecimal.class.equals(t) || BigDecimal.class.equals(r)) {
            setTypeTransform(IdentityDataTypeTransform.BIGDECIMAL);
        }
        if (BigInteger.class.equals(t) || BigInteger.class.equals(r)) {
            if (Double.class.equals(t) || Double.class.equals(r)) {
                /**@PortabilityHint(target="C#",name="replace")
                 * SetTypeTransform(Net.Vpc.Upa.Impl.Transform.IdentityDataTypeTransform.DOUBLE);
                 **/
                setTypeTransform(IdentityDataTypeTransform.BIGDECIMAL);
            } else if (Float.class.equals(t) || Float.class.equals(r)) {
                /**@PortabilityHint(target="C#",name="replace")
                 * SetTypeTransform(Net.Vpc.Upa.Impl.Transform.IdentityDataTypeTransform.DOUBLE);
                 **/
                setTypeTransform(IdentityDataTypeTransform.BIGDECIMAL);
            } else {
                setTypeTransform(IdentityDataTypeTransform.BIGINT);
            }
        } else if (Double.class.equals(t) || Double.class.equals(r)) {
            setTypeTransform(IdentityDataTypeTransform.DOUBLE);
        } else if (Float.class.equals(t) || Float.class.equals(r)) {
            setTypeTransform(IdentityDataTypeTransform.FLOAT);
        } else if (Long.class.equals(t) || Long.class.equals(r)) {
            setTypeTransform(IdentityDataTypeTransform.LONG);
        } else if (Integer.class.equals(t) || Integer.class.equals(r)) {
            setTypeTransform(IdentityDataTypeTransform.INT);
        } else {
            throw new UPAIllegalArgumentException("Unsupported types");
        }
    }
}
