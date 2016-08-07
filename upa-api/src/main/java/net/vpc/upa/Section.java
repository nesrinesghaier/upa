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
package net.vpc.upa;

import net.vpc.upa.exceptions.UPAException;
import net.vpc.upa.types.DataType;

import java.util.List;

public interface Section extends EntityPart {

    public Field addField(String name, FlagSet<UserFieldModifier> modifiers, Object defaultValue, DataType type) throws UPAException;

    public Field addField(String name, FlagSet<UserFieldModifier> modifiers, Object defaultValue, DataType type, int index) throws UPAException;

    void addPart(EntityPart child) throws UPAException;

    void addPart(EntityPart child, int index) throws UPAException;

    EntityPart removePartAt(int index) throws UPAException;

    EntityPart removePart(String name) throws UPAException;

    public void movePart(int index, int newIndex) throws UPAException;

    public void movePart(String itemName, int newIndex) throws UPAException;

    public List<Field> getFields() throws UPAException;

    public List<Section> getSections() throws UPAException;

    public List<EntityPart> getParts() throws UPAException;

    public EntityPart getPart(String name) throws UPAException;

    public EntityPart getPartAt(int index) throws UPAException;

    public Section getSection(String name) throws UPAException;

    public int indexOfPart(EntityPart part) throws UPAException;

    public int indexOfPart(String partName) throws UPAException;

    public int getPartsCount() throws UPAException;


    public Section findSection(String name) throws UPAException;

    public Section getSection(String path, MissingStrategy missingStrategy) throws UPAException;

    public Section addSection(String name, String parentPath) throws UPAException;

    public Section addSection(String name, String parentPath, int index) throws UPAException;

    public Section addSection(String name, int index) throws UPAException;

    public Section addSection(String name) throws UPAException;
}
