package net.vpc.upa.impl;

import net.vpc.upa.Entity;
import net.vpc.upa.Field;
import net.vpc.upa.Key;
import net.vpc.upa.Record;
import net.vpc.upa.exceptions.UPAException;
import net.vpc.upa.expressions.*;
import net.vpc.upa.impl.util.CastConverter;
import net.vpc.upa.impl.util.ConvertedList;
import net.vpc.upa.impl.util.StringUtils;
import net.vpc.upa.impl.util.UPAUtils;
import net.vpc.upa.types.ManyToOneType;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Taha BEN SALAH <taha.bensalah@gmail.com>
 * @creationdate 8/28/12 12:45 AM
 */
public abstract class AbstractEntityFactory implements EntityFactory {

    @Override
    public Record objectToRecord(Object object, Set<String> fields, boolean ignoreUnspecified, boolean ensureIncludeIds) {
        Record r = createRecord();
        Record allFieldsRecord = objectToRecord(object,ignoreUnspecified);
        if(fields==null || fields.isEmpty()){
            r.setAll(r);
            return r;
        }else {
            for (String k : fields) {
                r.setObject(k, allFieldsRecord.getObject(k));
            }
            if(ensureIncludeIds) {
                for (Field o : getEntity().getPrimaryFields()) {
                    String idname = o.getName();
                    if (!r.isSet(idname)) {
                        r.setObject(idname, allFieldsRecord.getObject(idname));
                    }
                }
            }
            return r;
        }
    }
    protected abstract Entity getEntity();

    @Override
    public <R> R idToObject(Object id) throws UPAException {
        if (id == null) {
            return null;
        }
        Entity entity = getEntity();
        R r = createObject();
        Record ur = objectToRecord(r, true);
        List<Field> primaryFields = entity.getPrimaryFields();
        if (id == null) {
            for (Field aF : primaryFields) {
                ur.setObject(aF.getName(), null);
            }
        } else {
            Object[] uk = entity.getBuilder().getKey(id).getValue();
            for (int i = 0; i < primaryFields.size(); i++) {
                ur.setObject(primaryFields.get(i).getName(), uk[i]);
            }
        }
        return r;
    }

    @Override
    public Record idToRecord(Object id) throws UPAException {
        if (id == null) {
            return null;
        }
        Entity entity = getEntity();
        Record ur = createRecord();
        List<Field> primaryFields = entity.getPrimaryFields();
//        if (k == null) {
//            for (Field aF : primaryFields) {
//                ur.setObject(aF.getName(), null);
//            }
//        } else {
        Object[] uk = entity.getBuilder().getKey(id).getValue();
        for (int i = 0; i < primaryFields.size(); i++) {
            ur.setObject(primaryFields.get(i).getName(), uk[i]);
        }
//        }
        return ur;
    }

    @Override
    public Object objectToId(Object object) throws UPAException {
        if (object == null) {
            return null;
        }
        if (object instanceof Record) {
            return recordToId((Record) object);
        }
        Entity entity = getEntity();
        List<Field> f = entity.getPrimaryFields();
        Object[] rawKey = new Object[f.size()];
        for (int i = 0; i < rawKey.length; i++) {
            final Field field = f.get(i);
            final String name = field.getName();
            if(!entity.getPlatformBeanType().isDefaultValue(object,name)){
                rawKey[i] = getProperty(object, name);
            }else {
                return null;
            }
        }
        return entity.getBuilder().createId(rawKey);
    }

    @Override
    public Object recordToId(Record record) throws UPAException {
        if (record == null) {
            return null;
        }
        Entity entity = getEntity();
        List<Field> f = entity.getPrimaryFields();
        Object[] rawKey = new Object[f.size()];
        for (int i = 0; i < rawKey.length; i++) {
            final Field field = f.get(i);
            final String name = field.getName();
            if (record.isSet(name)) {
                rawKey[i] = record.getObject(name);
            } else {
                return null;
            }
        }
        return entity.getBuilder().createId(rawKey);
    }

    @Override
    public Key objectToKey(Object object) throws UPAException {
        if (object == null) {
            return null;
        }
        if (object instanceof Record) {
            return recordToKey((Record) object);
        }
        Entity entity = getEntity();
        List<Field> f = entity.getPrimaryFields();
        Object[] rawKey = new Object[f.size()];
        for (int i = 0; i < rawKey.length; i++) {
            final Field field = f.get(i);
            final String name = field.getName();
            if(!entity.getPlatformBeanType().isDefaultValue(object,name)){
                rawKey[i] = getProperty(object, name);
            }else {
                return null;
            }
        }
        return entity.getBuilder().createKey(rawKey);
    }

    @Override
    public Key recordToKey(Record record) throws UPAException {
        if (record == null) {
            return null;
        }
        Entity entity = getEntity();
        List<Field> f = entity.getPrimaryFields();
        Object[] rawKey = new Object[f.size()];
        for (int i = 0; i < rawKey.length; i++) {
            final Field field = f.get(i);
            final String name = field.getName();
            if (record.isSet(name)) {
                rawKey[i] = record.getObject(name);
            } else {
                return entity.getBuilder().createKey((Object[]) null);
            }
        }
        return entity.getBuilder().createKey(rawKey);
    }

    @Override
    public Object keyToObject(Key key) throws UPAException {
        if (key == null) {
            return null;
        }
        return objectToRecord(keyToRecord(key));
    }

    @Override
    public Record keyToRecord(Key key) throws UPAException {
        if (key == null) {
            return null;
        }
        Record ur = createRecord();
        List<Field> primaryFields = getEntity().getPrimaryFields();
        if (key == null) {
            for (Field aF : primaryFields) {
                ur.setObject(aF.getName(), null);
            }
        } else {
            Object[] uk = key.getValue();
            for (int i = 0; i < primaryFields.size(); i++) {
                ur.setObject(primaryFields.get(i).getName(), uk[i]);
            }
        }
        return ur;
    }

    @Override
    public Key idToKey(Object id) throws UPAException {
        if (id == null) {
            return null;
        }
        return getEntity().getBuilder().getKey(id);
    }

    @Override
    public Object keyToId(Key uk) throws UPAException {
        if (uk == null) {
            return null;
        }
        return getEntity().getBuilder().getId(uk);
    }

    public Record objectToRecord(Object object) throws UPAException {
        return objectToRecord(object, false);
    }

    public Object getMainProperty(Object object) throws UPAException {
        Field mf = getEntity().getMainField();
        Object v = getProperty(object, mf.getName());
        if(v!=null && mf.getDataType() instanceof ManyToOneType && !UPAUtils.isSimpleFieldType(v.getClass())){
            Entity t = ((ManyToOneType)mf.getDataType()).getRelationship().getTargetEntity();
            return t.getMainFieldValue(v);
        }
        return v;
    }

    //    public Object entityToProperty(Object entityValue, String propertyName) throws UPAException {
//        return entityToProperty(entityValue, propertyName, false);
//    }
//
//    public Object entityToProperty(Object entityValue, String propertyName, boolean ignoreUnspecified) throws UPAException {
//        Record rec = objectToRecord(entityValue, ignoreUnspecified);
//        return rec == null ? null : rec.getObject(propertyName);
//    }

    public void setRecordId(Record record, Object id) throws UPAException {
        List<Field> f = getEntity().getPrimaryFields();
        if (id == null) {
            for (Field aF : f) {
                record.remove(aF.getName());
            }
            return;
        }
        Object[] uk = idToKey(id).getValue();
        if (f.size() > 0) {
            if (uk.length != f.size()) {
                throw new RuntimeException("key " + id + " could not denote for entity " + getEntity().getName() + " ; got " + uk.length + " elements instread of " + f.size());
            }
            for (int i = 0; i < f.size(); i++) {
                record.setObject(f.get(i).getName(), uk[i]);
            }
        }
    }

    public void setObjectId(Object object, Object id) throws UPAException {
        setRecordId(objectToRecord(object, true), id);
    }

    public Expression objectToExpression(Object object, boolean ignoreUnspecified, String alias) throws UPAException {
        return recordToExpression(objectToRecord(object, ignoreUnspecified), alias);
    }

    public Expression recordToExpression(Record record, String alias) throws UPAException {
        if (record == null) {
            return null;
        }
        Expression a = null;
        for (Map.Entry<String, Object> entry : record.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            Field field = getEntity().getField(key);
            if (!field.isUnspecifiedValue(value)) {
                Expression e = null;
                Var p = new Var(StringUtils.isNullOrEmpty(alias) ? getEntity().getName() : alias);
                switch (field.getSearchOperator()) {
                    case DEFAULT:
                    case EQ: {
                        if (field.getDataType() instanceof ManyToOneType) {
                            ManyToOneType et = (ManyToOneType) field.getDataType();
                            Key foreignKey = et.getRelationship().getTargetRole().getEntity().getBuilder().objectToKey(value);
                            Expression b = null;
                            int i = 0;
                            for (Field df : et.getRelationship().getSourceRole().getFields()) {
                                e = new Equals(new Var((Var) p.copy(), df.getName()), ExpressionFactory.toLiteral(foreignKey.getObjectAt(i)));
                                b = b == null ? b : new And(b, e);
                                i++;
                            }
                        } else {
                            e = new Equals(new Var(p, key), ExpressionFactory.toLiteral(value));
                        }
                        break;
                    }
                    case NE: {
                        e = new Different(new Var(p, key), ExpressionFactory.toLiteral(value));
                        break;
                    }
                    case GT: {
                        e = new GreaterThan(new Var(p, key), ExpressionFactory.toLiteral(value));
                        break;
                    }
                    case GTE: {
                        e = new GreaterEqualThan(new Var(p, key), ExpressionFactory.toLiteral(value));
                        break;
                    }
                    case LT: {
                        e = new LessThan(new Var(p, key), ExpressionFactory.toLiteral(value));
                        break;
                    }
                    case LTE: {
                        e = new LessEqualThan(new Var(p, key), ExpressionFactory.toLiteral(value));
                        break;
                    }
                    case CONTAINS: {
                        e = new Like(new Var(p, key), ExpressionFactory.toLiteral("%" + value + "%"));
                        break;
                    }
                    case STARTS_WITH: {
                        e = new Like(new Var(p, key), ExpressionFactory.toLiteral(value + "%"));
                        break;
                    }
                    case ENDS_WITH: {
                        e = new Like(new Var(p, key), ExpressionFactory.toLiteral("%" + value));
                        break;
                    }
                }
                if (e != null) {
                    a = a == null ? e : new And(a, e);
                }
            }
        }
        return a;
    }

    //    public Expression idToExpression(Object key) throws UPAException {
//        return idToExpression(key, null);
//    }
    public Expression idToExpression(Object id, String entityAlias) {
//        if (id == null) {//TODO TAHA
//            return null;
//        }
//        keyExpression.setClientProperty(EXPRESSION_SURELY_EXISTS, true);
        return new IdExpression(getEntity(), id, entityAlias);
    }

    @Override
    public Expression objectToIdExpression(Object entityOrRecord, String alias) throws UPAException {
        if(entityOrRecord==null){
            return null;
        }
        Record r = null;
        if(entityOrRecord instanceof Record){
            r=(Record)entityOrRecord;
        }
        r = objectToRecord(entityOrRecord);
        Key k = recordToKey(r);
        return keyToExpression(k, alias);
    }



    public Expression keyToExpression(Key key, String entityAlias) {
        Object id = keyToId(key);
        return idToExpression(id, entityAlias);
    }

    public <K> Expression idListToExpression(List<K> idList, String entityAlias) {
        List<Object> convertedList = new ConvertedList<K, Object>(idList, new CastConverter<K, Object>());

        //        keyEnumerationExpression.setClientProperty(EXPRESSION_SURELY_EXISTS,keys.size() > 0);
        return new IdEnumerationExpression(
                convertedList, entityAlias == null ? null : new Var(entityAlias));
    }

    @Override
    public Expression keyListToExpression(List<Key> keyList, String alias) throws UPAException {
        List<Object> list = new ConvertedList<Key, Object>(keyList, new KeyToIdConverter<Object>(getEntity()));
        return idListToExpression(list, alias
        );
    }
}
