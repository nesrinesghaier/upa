package net.vpc.upa.impl.persistence;

import net.vpc.upa.Entity;
import net.vpc.upa.MultiRecord;
import net.vpc.upa.Record;
import net.vpc.upa.exceptions.NoResultException;
import net.vpc.upa.exceptions.NonUniqueResultException;
import net.vpc.upa.exceptions.UPAException;
import net.vpc.upa.impl.util.UPAUtils;
import net.vpc.upa.Query;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA. User: vpc Date: 8/19/12 Time: 6:14 PM To change
 * this template use File | Settings | File Templates.
 */
public abstract class AbstractQuery implements Query {

    private boolean updatable = false;

    @Override
    public Date getDate() throws UPAException {
        return (Date) getSingleValue();
    }

    @Override
    public Boolean getBoolean() throws UPAException {
        return (Boolean) getSingleValue();
    }

    @Override
    public Integer getInteger() throws UPAException {
        Number n = (Number) getSingleValue();
        return n==null?null:n.intValue();
    }

    @Override
    public Long getLong() throws UPAException {
        Number n = (Number) getSingleValue();
        return n==null?null:n.longValue();
    }

    @Override
    public Double getDouble() throws UPAException {
        Number n = (Number) getSingleValue();
        return n==null?null:n.doubleValue();
    }

    @Override
    public String getString() throws UPAException {
        return (String) getSingleValue();
    }

    @Override
    public Number getNumber() throws UPAException {
        return (Number) getSingleValue();
    }

    @Override
    public Object getSingleValue() throws UPAException {
        return getSingleValue(null);
    }

    @Override
    public Object getSingleValue(Object defaultValue) throws UPAException {
        Record mergedRecord = getRecord();
        if (mergedRecord == null) {
            return defaultValue;
        }
        return mergedRecord.getSingleResult();
    }

    @Override
    public MultiRecord getMultiRecord() throws UPAException {
        List<MultiRecord> multiRecordList = null;
        try {
            multiRecordList = getMultiRecordList();
            if (!multiRecordList.isEmpty()) {
                return multiRecordList.get(0);
            }
            return null;
        } finally {
            UPAUtils.close(multiRecordList);
        }
    }

    @Override
    public Record getRecord() throws UPAException {
        List<Record> multiRecordList = null;
        try {
            multiRecordList = getRecordList();
            boolean empty = multiRecordList.isEmpty();
            if (!empty) {
                return multiRecordList.get(0);
            }
            return null;
        } finally {
            UPAUtils.close(multiRecordList);
        }
    }

    @Override
    public <R> R getEntity() throws UPAException {
        List<R> r = null;
        try {
            r = getEntityList();
            if (!r.isEmpty()) {
                return r.get(0);
            }
            return null;
        } finally {
            UPAUtils.close(r);
        }
    }

    public <R> R getSingleResult() throws UPAException {
        List<R> entityList = null;
        try {
            entityList = getResultList();
            if (entityList.isEmpty()) {
                throw new NoResultException();
            }
            //do not call size, as it will load all entities if fount
            //just iterate and throw exception if ambiguity
            int x = 0;
            for (Object object : entityList) {
                x++;
                if (x > 1) {
                    throw new NonUniqueResultException();
                }
            }
            return entityList.get(0);
        } finally {
            UPAUtils.close(entityList);
        }
    }

    public <R> R getSingleResultOrNull() throws UPAException {
        List<R> entityList = null;
        try {
            entityList = getResultList();
            if (entityList.isEmpty()) {
                return null;
            }
            int x = 0;
            for (Object object : entityList) {
                x++;
                if (x > 1) {
                    throw new NonUniqueResultException();
                }
            }
            return entityList.get(0);
        } finally {
            UPAUtils.close(entityList);
        }
    }

    public <R> R getFirstResultOrNull() throws UPAException {
        List<R> entityList = null;
        try {
            entityList = getResultList();
            if (entityList.isEmpty()) {
                return null;
            }
            return entityList.get(0);
        } finally {
            UPAUtils.close(entityList);
        }
    }

    public boolean isUpdatable() {
        return updatable;
    }

    public void setUpdatable(boolean forUpdate) {
        this.updatable = forUpdate;
    }

    public Query setParameters(Map<String, Object> parameters) {
        if (parameters != null) {
            for (Map.Entry<String, Object> entry : parameters.entrySet()) {
                setParameter(entry.getKey(), entry.getValue());
            }
        }
        return this;
    }

    @Override
    public Query setParameter(String name, Object value,boolean condition) {
        if(!condition){
            return this;
        }
        return setParameter(name,value);
    }

    @Override
    public Query setParameters(Map<String, Object> parameters,boolean condition) {
        if(!condition){
            return this;
        }
        return setParameters(parameters);
    }

    @Override
    public Query setParameter(int index, Object value,boolean condition) {
        if(!condition){
            return this;
        }
        return setParameter(index,value);
    }


}
