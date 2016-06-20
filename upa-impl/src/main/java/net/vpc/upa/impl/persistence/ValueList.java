package net.vpc.upa.impl.persistence;

import net.vpc.upa.exceptions.UPAException;
import net.vpc.upa.persistence.QueryResult;

import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA. User: vpc Date: 8/16/12 Time: 6:41 AM To change
 * this template use File | Settings | File Templates.
 */
public class ValueList<T> extends QueryResultLazyList<T> {
    int index;

    public ValueList(NativeSQL nativeSQL, int index) throws SQLException, UPAException {
        super(nativeSQL);
        this.index = index;
    }

    public T parse(QueryResult result) throws UPAException {
        return result.read(index);
    }
}
