/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.upa.impl;

import net.vpc.upa.*;
import net.vpc.upa.types.I18NString;

/**
 * @author Taha BEN SALAH <taha.bensalah@gmail.com>
 */
public class DefaultI18NStringStrategy implements I18NStringStrategy {

    private I18NString key(String s) {
        return s == null ? new I18NString("*") : new I18NString(s, "*");
    }

    public I18NString getPackageString(net.vpc.upa.Package module) {
        return new I18NString("Package").append(key(module==null?null:module.getPath()));
    }

    @Override
    public I18NString getIndexString(Index index) {
        return new I18NString("Index").append(key(index==null?null:index.getName()));
    }

    public I18NString getSectionString(Entity entity, String section) {
        return getEntityString(entity).append("Section").append(key(section));
    }

    public I18NString getRelationshipRoleString(RelationshipRole role) {
        switch (role.getRelationshipRoleType()) {
            case TARGET: {
                return getRelationshipString(role.getRelationship()).append("Target");
            }
            case SOURCE: {
                return getRelationshipString(role.getRelationship()).append("Source");
            }
        }
        return getRelationshipString(role.getRelationship()).append(key(null));
    }

    public I18NString getRelationshipString(Relationship relation) {
        I18NString r = new I18NString("Relationship");
        r = r.append(key(relation.getName()));
        r = r.append(key(relation.getSourceEntity().getName()));
        r = r.append(key(relation.getTargetEntity().getName()));
        return r;
    }

    public I18NString getEntityString(Entity entity) {
        return new I18NString("Entity").append(key(entity==null?null:entity.getName()));
    }

    public I18NString getFieldListString(Entity entity) {
        return getEntityString(entity).append("Field");
    }

    public I18NString getFieldString(Field field) {
        Entity entityName = (field != null) ? (field.getEntity() != null) ? field.getEntity() : field.getEntity() : null;
        String fieldName = (field != null) ? field.getName() : null;
        return getFieldListString(entityName).append(key(fieldName));
    }

}
