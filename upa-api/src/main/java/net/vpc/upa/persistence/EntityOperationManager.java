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
package net.vpc.upa.persistence;

/**
 * @author Taha BEN SALAH <taha.bensalah@gmail.com>
 * @creationdate 8/30/12 1:14 AM
 */
public interface EntityOperationManager {

//    public void init(Entity entity, PersistenceStore persistenceStore);

    public EntityRemoveOperation getRemoveOperation();

    public void setRemoveOperation(EntityRemoveOperation operation);

    public EntityUpdateOperation getUpdateOperation();

    public void setUpdateOperation(EntityUpdateOperation operation);

    public EntityPersistOperation getPersistOperation();

    public void setPersistOperation(EntityPersistOperation operation);

    public EntityFindOperation getFindOperation();

    public void setFindOperation(EntityFindOperation operation);

    public EntityResetOperation getResetOperation();

    public void setResetOperation(EntityResetOperation operation);

    public EntityClearOperation getClearOperation();

    public void setClearOperation(EntityClearOperation operation);

    public EntityInitializeOperation getInitializeOperation();

    public void setInitOperation(EntityInitializeOperation operation);

    public PersistenceStore getPersistenceStore();
}
