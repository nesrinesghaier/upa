package net.vpc.upa.impl.persistence.specific.derby;

import net.vpc.upa.PortabilityHint;
import net.vpc.upa.types.DataType;
import net.vpc.upa.types.NumberType;
import net.vpc.upa.impl.persistence.SQLManager;
import net.vpc.upa.impl.persistence.shared.AbstractSQLProvider;
import net.vpc.upa.impl.uql.ExpressionDeclarationList;
import net.vpc.upa.impl.uql.compiledexpression.CompiledTypeName;
import net.vpc.upa.persistence.EntityExecutionContext;

import net.vpc.upa.impl.util.PlatformUtils;
import net.vpc.upa.types.EnumType;

/**
 * Created with IntelliJ IDEA. User: vpc Date: 8/15/12 Time: 11:46 PM To change
 * this template use File | Settings | File Templates.
 */
@PortabilityHint(target = "C#", name = "suppress")
public class DerbyTypeNameSQLProvider extends AbstractSQLProvider {

    public DerbyTypeNameSQLProvider() {
        super(CompiledTypeName.class);
    }

    @Override
    public String getSQL(Object oo, EntityExecutionContext qlContext, SQLManager sqlManager, ExpressionDeclarationList declarations) {
        CompiledTypeName o = (CompiledTypeName) oo;
        return getSqlTypeName(o.getTypeTransform().getTargetType());
    }

    public String getSqlTypeName(DataType datatype) {
        Class platformType = datatype.getPlatformType();
        int length = datatype.getScale();
        int precision = datatype.getPrecision();
        if (platformType.equals(String.class)) {
            if (length <= 0) {
                length = 255;
            }
            if (length <= 32672) {
                return "VARCHAR(" + length + ")";
            } else {
                return "BLOB";//return "NTEXT";
            }
        }
        if (PlatformUtils.isInt32(platformType)) {
            return "INT";
        }
        if (PlatformUtils.isInt8(platformType)) {
            return "SMALLINT";
        }
        if (PlatformUtils.isInt16(platformType)) {
            return "SMALLINT";
        }
        if (PlatformUtils.isInt64(platformType)) {
            return "BIGINT";
        }
        if (PlatformUtils.isFloat32(platformType)) {
            return "FLOAT";
        }
        if (PlatformUtils.isFloat64(platformType)) {
            if (datatype instanceof NumberType) {
                NumberType n = ((NumberType) datatype);
                return "DOUBLE";
                //return n.isFixedDigits() ? "DECIMAL(" + (n.getMaximumIntegerDigits() + n.getMaximumFractionDigits()) + "," + n.getMaximumFractionDigits() + ")" : "DOUBLE";
            } else {
                return "DOUBLE";
            }
        }
        if (PlatformUtils.isNumber(platformType)) {
            return "NUMBER";
        }
        if (PlatformUtils.isBool(platformType)) {
            return "INT";
        }
        if (PlatformUtils.isAnyDate(platformType)) {
            if (PlatformUtils.isTime(platformType)) {
                return "TIME";
            } else if (PlatformUtils.isTimestamp(platformType)) {
                return "TIMESTAMP";
            } else if (PlatformUtils.isDateTime(platformType)) {
                return "TIMESTAMP";
            } else if (PlatformUtils.isDateOnly(platformType)) {
                return "DATE";
            }
        }
//        if (platformType.equals(java.util.Date.class) || platformType.equals(Date.class)) {
//            return "DATE";
//        }

        if (datatype instanceof EnumType) {
            //TODO should support marshalling types
            return "INT";
        }

        if (Object.class.equals(platformType) || PlatformUtils.isSerializable(platformType)) {
            return "BLOB"; // serialized form
        }
        throw new IllegalArgumentException("UNKNOWN_TYPE<" + platformType.getName() + "," + length + "," + precision + ">");
    }

}
