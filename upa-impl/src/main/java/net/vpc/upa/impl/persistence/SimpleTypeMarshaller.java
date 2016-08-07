package net.vpc.upa.impl.persistence;

import net.vpc.upa.types.DataType;

/**
 * Created with IntelliJ IDEA.
 * User: vpc
 * Date: 8/17/12
 * Time: 5:40 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class SimpleTypeMarshaller extends DefaultTypeMarshaller {

    @Override
    public String toSQLLiteral(Object object) {
        if(object==null){
            return getMarshallManager().getNullMarshaller().toSQLLiteral(object);
        }
        return null;
    }

    @Override
    public boolean isLiteralSupported() {
        return true;
    }

    @Override
    public int getSize() {
        return 1;
    }

    @Override
    public String getName(String name, int i) {
        return name;
    }

    public DataType getPersistentDataType(DataType datatype) {
        return null;
    }
    
}
