package net.vpc.upa.impl;

import net.vpc.upa.*;
import net.vpc.upa.exceptions.UPAException;
import net.vpc.upa.filters.FieldFilter;
import net.vpc.upa.impl.util.PlatformUtils;
import net.vpc.upa.types.DataType;
import net.vpc.upa.types.DataTypeTransform;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class AbstractField extends AbstractUPAObject implements Field, Comparable<Object> {

    protected Entity entity;
    protected EntityPart parent;
    protected DataType dataType;
    protected Formula persistFormula;
    protected int persistFormulaOrder;
    protected Formula updateFormula;
    protected int updateFormulaOrder;
    protected Formula queryFormula;
    protected Object defaultObject;
    protected SearchOperator searchOperator = SearchOperator.DEFAULT;
    protected DataTypeTransform typeTransform;
    protected HashMap<String, Object> properties;
    protected FlagSet<UserFieldModifier> userModifiers = FlagSets.noneOf(UserFieldModifier.class);
    protected FlagSet<UserFieldModifier> userExcludeModifiers = FlagSets.noneOf(UserFieldModifier.class);
    protected FlagSet<FieldModifier> effectiveModifiers = FlagSets.noneOf(FieldModifier.class);
    protected boolean closed;
    protected Object unspecifiedValue = UnspecifiedValue.DEFAULT;
    //    protected PasswordStrategy passwordStrategy;
    private Relationship[] rightsRelations = new Relationship[0];
    private AccessLevel persistAccessLevel = AccessLevel.PUBLIC;
    private AccessLevel updateAccessLevel = AccessLevel.PUBLIC;
    private AccessLevel readAccessLevel = AccessLevel.PUBLIC;
    private FieldPersister fieldPersister;
    private PropertyAccessType accessType;

    protected AbstractField() {
    }

    @Override
    public String getAbsoluteName() {
        return (getEntity() == null ? "?" : getEntity().getName()) + "." + getName();
    }

    public EntityPart getParent() {
        return parent;
    }

    public void setParent(EntityPart item) {
        EntityPart old = this.parent;
        this.parent = item;
        propertyChangeSupport.firePropertyChange("parent", old, parent);
    }

    //    public boolean is(long modifier) {
//        return modifier == (modifiers & modifier);
//    }
//    public void setModifiers(long v, boolean enable) {
//        if (enable) {
//            addModifiers(v);
//        } else {
//            removeModifiers(v);
//        }
//    }
//
    public boolean is(FieldFilter ff) throws UPAException {
        return ff.accept(this);
    }

    public boolean isId() throws UPAException {
        return getModifiers().contains(FieldModifier.ID);
    }

    public boolean isMain() throws UPAException {
        return getModifiers().contains(FieldModifier.MAIN);
    }

    public boolean isSummary() throws UPAException {
        return getModifiers().contains(FieldModifier.SUMMARY);
    }

    public List<Relationship> getRelationships() {
        List<Relationship> relations = new ArrayList<Relationship>();
        for (Relationship r : getPersistenceUnit().getRelationshipsForSource(getEntity())) {
            Field entityField = r.getSourceRole().getEntityField();
            if (entityField != null && entityField.equals(this)) {
                relations.add(r);
            } else {
                List<Field> fields = r.getSourceRole().getFields();
                for (Field field : fields) {
                    if (field.equals(this)) {
                        relations.add(r);
                    }
                }
            }
        }
        return relations;
    }

    public void setFormula(Formula formula) {
        setPersistFormula(formula);
        setUpdateFormula(formula);
    }

    @Override
    public void setFormula(String formula) {
        setFormula(formula == null ? null : new ExpressionFormula(formula));
    }

    public void setPersistFormula(Formula formula) {
        this.persistFormula = formula;
    }

    @Override
    public void setPersistFormula(String formula) {
        setPersistFormula(formula == null ? null : new ExpressionFormula(formula));
    }

    public void setUpdateFormula(Formula formula) {
        this.updateFormula = formula;
    }

    @Override
    public void setUpdateFormula(String formula) {
        setUpdateFormula(formula == null ? null : new ExpressionFormula(formula));
    }

    @Override
    public void setFormulaOrder(int order) {
        setPersistFormulaOrder(order);
        setUpdateFormulaOrder(order);
    }

    @Override
    public void setPersistFormulaOrder(int order) {
        this.persistFormulaOrder = order;
    }

    @Override
    public void setUpdateFormulaOrder(int order) {
        this.updateFormulaOrder = order;
    }

    public int getUpdateFormulaOrder() {
        return updateFormulaOrder;
    }

    public int getPersistFormulaOrder() {
        return persistFormulaOrder;
    }

    public Formula getUpdateFormula() {
        return updateFormula;
    }

    public Formula getSelectFormula() {
        return queryFormula;
    }

    @Override
    public void setSelectFormula(String formula) {
        setSelectFormula(formula == null ? null : new ExpressionFormula(formula));
    }

    public void setSelectFormula(Formula queryFormula) {
        this.queryFormula = queryFormula;
    }

    //    public boolean isRequired() throws UPAException {
//        return (!isReadOnlyOnPersist() || !isReadOnlyOnUpdate()) && !getDataType().isNullable();
//    }
    public String getPath() {
        EntityPart parent = getParent();
        return parent == null ? ("/" + getName()) : (parent.getPath() + "/" + getName());
    }

    @Override
    public PersistenceUnit getPersistenceUnit() {
        return entity.getPersistenceUnit();
    }

    public Formula getPersistFormula() {
        return persistFormula;
    }

    public Entity getEntity() {
        return entity;
    }

    /**
     * called by PersistenceUnitFilter / Table You should not use it
     *
     * @param datatype datatype
     */
    @Override
    public void setDataType(DataType datatype) {
        this.dataType = datatype;
    }

    public DataType getDataType() {
        return dataType;
    }

    /**
     * called by PersistenceUnitFilter / Table You should not use it
     *
     * @param o default value witch may be san ObjectHandler
     */
    public void setDefaultObject(Object o) {
        defaultObject = o;
    }

    public Object getDefaultValue() {
        Object _defaultValue = getDefaultObject();
        return _defaultValue != null ? (_defaultValue instanceof CustomDefaultObject) ? ((CustomDefaultObject) _defaultValue).getObject() : _defaultValue : null;
    }

    public Object getDefaultObject() {
        return defaultObject;
    }

    //    public void resetModifiers() {
//        modifiers = 0;
//    }
    public void setUserModifiers(FlagSet<UserFieldModifier> modifiers) {
        this.userModifiers = modifiers == null ? FlagSets.noneOf(UserFieldModifier.class) : modifiers;
    }

    public void setUserExcludeModifiers(FlagSet<UserFieldModifier> modifiers) {
        this.userExcludeModifiers = modifiers == null ? FlagSets.noneOf(UserFieldModifier.class) : modifiers;
    }

    public FlagSet<FieldModifier> getModifiers() {
        return effectiveModifiers;
    }

    public void setEffectiveModifiers(FlagSet<FieldModifier> effectiveModifiers) {
        this.effectiveModifiers = effectiveModifiers;
    }

    //    public void addModifiers(long modifiers) {
//        setModifiers(getModifiers() | modifiers);
//    }
//
//    public void removeModifiers(long modifiers) {
//        setModifiers(getModifiers() & ~modifiers);
//    }
//    public Expression getExpression() {
//        return formula == null ? null : formula.getExpression();
//    }
    @Override
    public boolean equals(Object other) {
        return !(other == null || !(other instanceof Field)) && compareTo(other) == 0;
    }

    public int compareTo(Object other) {
        if (other == this) {
            return 0;
        }
        if (other == null) {
            return 1;
        }
        Field f = (Field) other;
        NamingStrategy comp = getEntity().getPersistenceUnit().getNamingStrategy();

        String s1 = entity != null ? comp.getUniformValue(entity.getName()) : "";
        String s2 = f.getName() != null ? comp.getUniformValue(f.getEntity().getName()) : "";
        int i = s1.compareTo(s2);
        if (i != 0) {
            return i;
        } else {
            String s3 = getName() != null ? comp.getUniformValue(getName()) : "";
            String s4 = f.getName() != null ? comp.getUniformValue(f.getName()) : "";
            i = s3.compareTo(s4);
            return i;
        }
    }

    /**
     * called by PersistenceUnitFilter You should not use it
     *
     * @param r relation
     */
    public void addTargetRelationship(Relationship r) {
        int max = rightsRelations.length;
        for (int i = 0; i < rightsRelations.length; i++) {
            Relationship relation = rightsRelations[i];
            if (relation.equals(r)) {
                return;
            }
        }
        Relationship[] rr = new Relationship[max + 1];
        System.arraycopy(rightsRelations, 0, rr, 0, max);
        rr[max] = r;
        rightsRelations = rr;
    }

    public Relationship[] getTargetRelationships() {
        return rightsRelations;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    @Override
    public FlagSet<UserFieldModifier> getUserModifiers() {
        return userModifiers;
    }

    @Override
    public FlagSet<UserFieldModifier> getUserExcludeModifiers() {
        return userExcludeModifiers;
    }

    @Override
    public String toString() {
        return getAbsoluteName();
    }

    @Override
    public void close() throws UPAException {
        this.closed = true;
    }

    public boolean isClosed() {
        return closed;
    }

    @Override
    public void setUnspecifiedValue(Object o) {
        this.unspecifiedValue = o;
    }

    @Override
    public Object getUnspecifiedValue() {
        return unspecifiedValue;
    }

    public Object getUnspecifiedValueDecoded() {
        final Object fuv = getUnspecifiedValue();
        if (UnspecifiedValue.DEFAULT.equals(fuv)) {
            return getDataType().getDefaultUnspecifiedValue();
        } else {
            return fuv;
        }
    }

    public boolean isUnspecifiedValue(Object value) {
        Object v = getUnspecifiedValueDecoded();
        return (v == value || (v != null && v.equals(value)));
    }

    public AccessLevel getPersistAccessLevel() {
        return persistAccessLevel;
    }

    public void setPersistAccessLevel(AccessLevel persistAccessLevel) {
        if (PlatformUtils.isUndefinedValue(AccessLevel.class, persistAccessLevel)) {
            persistAccessLevel = AccessLevel.PUBLIC;
        }
        this.persistAccessLevel = persistAccessLevel;
    }

    public AccessLevel getUpdateAccessLevel() {
        return updateAccessLevel;
    }

    public void setUpdateAccessLevel(AccessLevel updateAccessLevel) {
        if (PlatformUtils.isUndefinedValue(AccessLevel.class, updateAccessLevel)) {
            updateAccessLevel = AccessLevel.PUBLIC;
        }
        this.updateAccessLevel = updateAccessLevel;
    }

    public AccessLevel getReadAccessLevel() {
        return readAccessLevel;
    }

    public void setReadAccessLevel(AccessLevel readAccessLevel) {
        if (PlatformUtils.isUndefinedValue(AccessLevel.class, readAccessLevel)) {
            readAccessLevel = AccessLevel.PUBLIC;
        }
        this.readAccessLevel = readAccessLevel;
    }

    public void setAccessLevel(AccessLevel accessLevel) {
        setPersistAccessLevel(accessLevel);
        setUpdateAccessLevel(accessLevel);
        setReadAccessLevel(accessLevel);
    }

    public SearchOperator getSearchOperator() {
        return searchOperator;
    }

    public void setSearchOperator(SearchOperator searchOperator) {
        this.searchOperator = searchOperator;
    }

    //    public void setPasswordStrategy(CipherStrategyType cipherStrategyType, String cipherStrategy, String cipherValue) {
//        if (cipherStrategyType == null) {
//            setPasswordStrategy(null);
//        } else {
//            switch (cipherStrategyType) {
//                case DEFAULT:
//                case MD5: {
//                    setPasswordStrategy(DefaultCipherStrategy.MD5, cipherValue);
//                    break;
//                }
//                case SHA1: {
//                    setPasswordStrategy(DefaultCipherStrategy.SHA1, cipherValue);
//                    break;
//                }
//                case SHA256: {
//                    setPasswordStrategy(DefaultCipherStrategy.SHA256, cipherValue);
//                    break;
//                }
//                default: {
//                    if (Strings.isNullOrEmpty(cipherStrategy)) {
//                        throw new UPAException("MissingCipherStrategy", cipherStrategy, this);
//                    }
//                    CipherStrategy o;
//                    try {
//                        o = (CipherStrategy) Class.forName(cipherStrategy).newInstance();
//                        setPasswordStrategy(o, cipherValue);
//                    } catch (Exception ex) {
//                        throw new UPAException(ex,new I18NString("InvalidCipherStrategy"), cipherStrategy, this);
//                    }
//                }
//            }
//        }
//    }
//    public void setPasswordStrategy(CipherStrategy cipherStrategy, String cypherValue) {
//        setPasswordStrategy(cipherStrategy == null ? null : new DefaultPasswordStrategy(cipherStrategy, cypherValue));
//    }
//
//    public void setPasswordStrategy(PasswordStrategy passwordStrategy) {
//        PasswordStrategy old = this.passwordStrategy;
//        this.passwordStrategy = passwordStrategy;
//        propertyChangeSupport.firePropertyChange("passwordStrategy", old, passwordStrategy);
//    }
//
//    public PasswordStrategy getPasswordStrategy() {
//        return passwordStrategy;
//    }
    public FieldPersister getFieldPersister() {
        return fieldPersister;
    }

    public void setFieldPersister(FieldPersister fieldPersister) {
        this.fieldPersister = fieldPersister;
    }

    public DataTypeTransform getTypeTransform() {
        return typeTransform;
    }

    public void setTypeTransform(DataTypeTransform transform) {
        this.typeTransform = transform;
    }

    public PropertyAccessType getPropertyAccessType() {
        return accessType;
    }

    public void setPropertyAccessType(PropertyAccessType accessType) {
        this.accessType = accessType;
    }

    @Override
    public Object getValue(Object instance) {
        return getEntity().getBuilder().getProperty(instance, getName());
    }

    @Override
    public void setValue(Object instance, Object value) {
        getEntity().getBuilder().setProperty(instance, getName(), value);
    }

}
