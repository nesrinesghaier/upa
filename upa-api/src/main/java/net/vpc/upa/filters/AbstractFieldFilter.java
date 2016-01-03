/**
 * ==================================================================== 
 * UPA (Unstructured Persistence API)
 *    Yet another ORM Framework
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
 *
 * Copyright (C) 2014-2015 Taha BEN SALAH
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 * ====================================================================
 */
package net.vpc.upa.filters;

import net.vpc.upa.CompoundField;
import net.vpc.upa.Field;
import net.vpc.upa.PrimitiveField;
import net.vpc.upa.exceptions.UPAException;

import java.util.ArrayList;
import java.util.List;


// Referenced classes of package net.vpc.lib.pheromone.ariana.database:
//            Field

public abstract class AbstractFieldFilter implements FieldFilter {
    public abstract boolean acceptDynamic() throws UPAException;

    public abstract boolean accept(Field f) throws UPAException;

    public static AbstractFieldFilter toAbstractFieldFilter(FieldFilter filter) {
        if (filter == null) {
            return new FieldAnyFilter();
        } else if (filter instanceof AbstractFieldFilter) {
            return (AbstractFieldFilter) filter;
        } else {
            return new FieldFilterAdapter(filter);
        }
    }

    public List<Field> filter(List<Field> fields) throws UPAException {
        List<Field> v = new ArrayList<Field>(fields.size());
        for (Field field : fields) {
            if (accept(field)) {
                v.add(field);
            }
        }
        return v;
    }

    public Field[] filter(Field[] fields) throws UPAException {
        List<Field> v = new ArrayList<Field>(fields.length);
        for (Field field : fields) {
            if (accept(field)) {
                v.add(field);
            }
        }
        return v.toArray(new Field[v.size()]);
    }

    public PrimitiveField[] filter(PrimitiveField[] fields) throws UPAException {
        ArrayList<PrimitiveField> v = new ArrayList<PrimitiveField>(fields.length);
        for (Field field : fields) {
            if (accept(field)) {
                v.add((PrimitiveField) field);
            }
        }
        return v.toArray(new PrimitiveField[v.size()]);
    }

    public CompoundField[] filter(CompoundField[] fields) throws UPAException {
        ArrayList<CompoundField> v = new ArrayList<CompoundField>(fields.length);
        for (Field field : fields) {
            if (accept(field)) {
                v.add((CompoundField) field);
            }
        }
        return v.toArray(new CompoundField[v.size()]);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return true;
    }

    @Override
    public int hashCode() {
        return 731;
    }
}
