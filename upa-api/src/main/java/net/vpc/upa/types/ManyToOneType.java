/**
 * ====================================================================
 * UPA (Unstructured Persistence API)
 * Yet another ORM Framework
 * ++++++++++++++++++++++++++++++++++
 * Unstructured Persistence API, referred to as UPA, is a genuine effort
 * to raise programming language frameworks managing relational data in
 * applications using Java Platform, Standard Edition and Java Platform,
 * Enterprise Edition and Dot Net Framework equally to the next level of
 * handling ORM for mutable data structures. UPA is intended to provide
 * a solid reflection mechanisms to the mapped data structures while
 * affording to make changes at runtime of those data structures.
 * Besides, UPA has learned considerably of the leading ORM
 * (JPA, Hibernate/NHibernate, MyBatis and Entity Framework to name a few)
 * failures to satisfy very common even known to be trivial requirement in
 * enterprise applications.
 * <p>
 * Copyright (C) 2014-2015 Taha BEN SALAH
 * <p>
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * <p>
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * <p>
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 * ====================================================================
 */
package net.vpc.upa.types;

import net.vpc.upa.DataTypeInfo;
import net.vpc.upa.Entity;
import net.vpc.upa.Relationship;

public class ManyToOneType extends DefaultDataType implements Cloneable {

    private String targetEntityName;
    private Relationship relationship;
    private boolean updatable;

    //    public ManyToOneType(String name, Class platformType, boolean updatable) {
//        super(name, platformType);
//        this.updatable = updatable;
//    }
    public ManyToOneType(Class entityType, String entityName, boolean nullable) {
        this(null, entityType, entityName, true, nullable);
    }

    public ManyToOneType(Class entityType, boolean nullable) {
        this(null, entityType, entityType.getSimpleName(), true, nullable);
    }

    public ManyToOneType(String typeName, Class entityType, String entityName, boolean updatable, boolean nullable) {
        super(typeName, entityType, nullable);
        this.updatable = updatable;
        this.targetEntityName = entityName;
    }

    public String getTargetEntityName() {
        return targetEntityName;
    }

    public void setTargetEntityName(String targetEntityName) {
        this.targetEntityName = targetEntityName;
    }

    public Relationship getRelationship() {
        return relationship;
    }

    public void setRelationship(Relationship relationship) {
        this.relationship = relationship;
    }

    public boolean isUpdatable() {
        return updatable;
    }

    @Override
    public String toString() {
        String n = getPlatformType() == null ? null : getPlatformType().getName();
        if (n == null) {
            n = getTargetEntityName();
        } else if (getTargetEntityName() != null) {
            n = n + ":" + getTargetEntityName();
        }
        return "ManyToOneType{" + n + ", updatable=" + updatable + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        ManyToOneType that = (ManyToOneType) o;

        if (updatable != that.updatable) return false;
        if (targetEntityName != null ? !targetEntityName.equals(that.targetEntityName) : that.targetEntityName != null)
            return false;
        return relationship != null ? relationship.equals(that.relationship) : that.relationship == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (targetEntityName != null ? targetEntityName.hashCode() : 0);
        result = 31 * result + (relationship != null ? relationship.hashCode() : 0);
        result = 31 * result + (updatable ? 1 : 0);
        return result;
    }

    @Override
    public DataTypeInfo getInfo() {
        DataTypeInfo d = super.getInfo();
        d.getProperties().put("updatable", String.valueOf(updatable));
        if(targetEntityName!=null) {
            d.getProperties().put("targetEntityName", String.valueOf(targetEntityName));
        }
        if(relationship!=null) {
            d.getProperties().put("relationship", String.valueOf(relationship.getName()));
        }
        if(relationship!=null) {
        }
        return d;
    }
}
