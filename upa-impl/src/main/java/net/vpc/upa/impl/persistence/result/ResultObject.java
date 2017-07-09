package net.vpc.upa.impl.persistence.result;

import net.vpc.upa.Document;
import net.vpc.upa.Key;
import net.vpc.upa.ObjectFactory;

/**
 * Created by vpc on 7/1/17.
 */
public class ResultObject {
    Key resultKey;
    String entityName;
    Object entityObject;
    Object entityResult;
    Object entityUpdatable;
    Document entityDocument;

    public ResultObject() {
    }
}
