package net.vpc.upa.impl;


import net.vpc.upa.PortabilityHint;
import net.vpc.upa.Record;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

/**
 * AbstractRecord is an abstract implementation of the Record interface
 * @author Taha BEN SALAH <taha.bensalah@gmail.com>
 * @creationdate 8/25/12 1:31 AM
 */
public abstract class AbstractRecord implements Record {
    protected LinkedHashSet<String> updated;

    protected AbstractRecord() {
    }

    /**
     * reset all updated fields
     */
    public void resetUpdatedFields() {
        updated = null;
    }

    /**
     * return all updated fields
     * @return
     */
    public String[] getUpdatedFields() {
        return updated == null ? new String[0] : updated.toArray(new String[updated.size()]);
    }

    protected void setUpdated(String field) {
        if (updated == null) {
            updated = new LinkedHashSet<String>();
        }
        updated.add(field);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAll(Map<String, Object> other, String... keys) {
        if (keys.length == 0) {
            for (Map.Entry<String, Object> entry : other.entrySet()) {
                setObject(entry.getKey(), entry.getValue());
            }
        } else {
            HashSet<String> accepted = new HashSet<String>(Arrays.asList(keys));
            for (String s : accepted) {
                if (other.containsKey(s)) {
                    setObject(s, other.get(s));
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T getObject(String key, T value) {
        if (isSet(key)) {
            return (T) getObject(key);
        } else {
            return value;
        }
    }

    //////////////////////////////////////

    /**
     * {@inheritDoc}
     */
    @Override
    public int getInt(String key) {
        return ((Number) getObject(key)).intValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getInt(String key, int defaultValue) {
        Number object = getObject(key, defaultValue);
        return object.intValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setInt(String key, int value) {
        setObject(key, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getInt() {
        return getNumber().intValue();
    }

    //////////////////////////////////////

    /**
     * {@inheritDoc}
     */
    @Override
    public long getLong(String key) {
        return ((Number) getObject(key)).longValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getLong(String key, long defaultValue) {
        Number object = getObject(key, defaultValue);
        return object.longValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLong(String key, long value) {
        setObject(key, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getLong() {
        return getNumber().longValue();
    }

    //////////////////////////////////////

    /**
     * {@inheritDoc}
     */
    @Override
    public double getDouble(String key) {
        return ((Number) getObject(key)).doubleValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getDouble(String key, double defaultValue) {
        Number object = getObject(key, defaultValue);
        return object.doubleValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDouble(String key, double value) {
        setObject(key, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getDouble() {
        return getNumber().doubleValue();
    }


    //////////////////////////////////////

    /**
     * {@inheritDoc}
     */
    @Override
    public float getFloat(String key) {
        return ((Number) getObject(key)).floatValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float getFloat(String key, float defaultValue) {
        Number object = getObject(key, defaultValue);
        return object.floatValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFloat(String key, float value) {
        setObject(key, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float getFloat() {
        return getNumber().floatValue();
    }

    //////////////////////////////////////

    /**
     * {@inheritDoc}
     */
    @PortabilityHint(target = "C#", name = "suppress")
    @Override
    public BigDecimal getBigDecimal(String key) {
        return ((BigDecimal) getObject(key));
    }

    /**
     * {@inheritDoc}
     */
    @PortabilityHint(target = "C#", name = "suppress")
    @Override
    public BigDecimal getBigDecimal(String key, BigDecimal defaultValue) {
        return getObject(key, defaultValue);
    }

    /**
     * {@inheritDoc}
     */
    @PortabilityHint(target = "C#", name = "suppress")
    @Override
    public void setBigDecimal(String key, BigDecimal value) {
        setObject(key, value);
    }

    /**
     * {@inheritDoc}
     */
    @PortabilityHint(target = "C#", name = "suppress")
    @Override
    public BigDecimal getBigDecimal() {
        Number n = getNumber();
        return (n instanceof BigDecimal) ? (BigDecimal) n :
                new BigDecimal(n.toString());
    }
    //////////////////////////////////////

    /**
     * {@inheritDoc}
     */
    @Override
    public BigInteger getBigInteger(String key) {
        return getObject(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigInteger getBigInteger(String key, BigInteger defaultValue) {
        return getObject(key, defaultValue);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setBigInteger(String key, BigInteger value) {
        setObject(key, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigInteger getBigInteger() {
        Number n = getNumber();
        return (n instanceof BigInteger) ? (BigInteger) n :
                new BigInteger(n.toString());
    }

    //////////////////////////////////////

    /**
     * {@inheritDoc}
     */
    @Override
    public Number getNumber(String key) {
        return ((Number) getObject(key));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Number getNumber(String key, Number defaultValue) {
        Number object = getObject(key, defaultValue);
        return object.intValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setNumber(String key, Number value) {
        setObject(key, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Number getNumber() {
        return getSingleResult();
    }

    //////////////////////////////////////

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getBoolean(String key) {
        return getObject(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getBoolean(String key, boolean defaultValue) {
        return getObject(key, defaultValue);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setBoolean(String key, boolean value) {
        setObject(key, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getBoolean() {
        return getSingleResult();
    }

    //////////////////////////////////////

    /**
     * {@inheritDoc}
     */
    @Override
    public String getString(String key) {
        return ((String) getObject(key));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getString(String key, String defaultValue) {
        return getObject(key, defaultValue);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setString(String key, String value) {
        setObject(key, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getString() {
        return getSingleResult();
    }

    //////////////////////////////////////

    /**
     * {@inheritDoc}
     */
    @Override
    public Date getDate(String key) {
        return getObject(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Date getDate(String key, Date defaultValue) {
        return getObject(key, defaultValue);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDate(String key, Date value) {
        setObject(key, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Date getDate() {
        return getSingleResult();
    }

    //////////////////////////////////////


    /**
     * {@inheritDoc}
     */
    @Override
    public void setAll(Record other, String... keys) {
        if(other!=null) {
            setAll(other.toMap(), keys);
        }
    }

    //////////////////////////////////////

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean retainAll(Set<String> keys) {
        boolean modified = false;
        HashSet<String> k = new HashSet<String>(keySet());
        k.removeAll(keys);
        for (String s : k) {
            modified = true;
            remove(s);
        }
        return modified;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof Record)) {
            return false;
        }
        return toMap().equals(((Record) o).toMap());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return toMap().hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return toMap().toString();
    }
}
