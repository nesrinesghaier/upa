package net.vpc.upa.impl.uql.compiledexpression;

import net.vpc.upa.*;
import net.vpc.upa.exceptions.UPAException;
import net.vpc.upa.expressions.CompiledExpression;
import net.vpc.upa.extensions.HierarchyExtension;
import net.vpc.upa.impl.extension.HierarchicalRelationshipSupport;
import net.vpc.upa.impl.transform.IdentityDataTypeTransform;
import net.vpc.upa.impl.uql.ExpressionCompiler;
import net.vpc.upa.impl.uql.ReplaceResult;
import net.vpc.upa.impl.util.UPAUtils;
import net.vpc.upa.types.*;

import java.util.List;

public final class IsHierarchyDescendantCompiled extends CompiledQLFunctionExpression
        implements Cloneable,net.vpc.upa.Function {

    private static final long serialVersionUID = 1L;
    private DefaultCompiledExpression ancestorExpression;
    private DefaultCompiledExpression childExpression;
    private CompiledEntityName entityName;

    public IsHierarchyDescendantCompiled(DefaultCompiledExpression ancestorExpression, DefaultCompiledExpression childExpression, CompiledEntityName entityName) {
        super("treeAncestor",new DefaultCompiledExpression[]{ancestorExpression,childExpression,entityName},new IdentityDataTypeTransform(TypesFactory.BOOLEAN),null);
        this.handler = this;
        this.ancestorExpression = ancestorExpression;
        this.childExpression = childExpression;
        this.entityName = entityName;
//        protectedAddArgument(ancestorExpression);
//        protectedAddArgument(childExpression);
//        protectedAddArgument(entityName);
    }

    public DefaultCompiledExpression getAncestorExpression() {
        return ancestorExpression;
    }

    public DefaultCompiledExpression getChildExpression() {
        return childExpression;
    }

    public CompiledEntityName getEntityName() {
        return entityName;
    }
    
    public DefaultCompiledExpression copy() {
        return new IsHierarchyDescendantCompiled(ancestorExpression.copy(), childExpression.copy(),(CompiledEntityName)entityName.copy());
    }


    public Object eval(EvalContext evalContext) {
        IsHierarchyDescendantCompiled o=this;
        ExpressionCompiler expressionCompiler=(ExpressionCompiler) evalContext.getCompilerContext();
        PersistenceUnit persistenceUnit=evalContext.getPersistenceUnit();
        DefaultCompiledExpression c = o.getChildExpression();
        DefaultCompiledExpression p = o.getAncestorExpression();
        CompiledEntityName n = o.getEntityName();
        Entity treeEntity = null;
        if (c instanceof CompiledVar) {

            Object childReferrer = resolveReferrer((CompiledVar) ((CompiledVar) c).getFinest(),expressionCompiler);
            if (childReferrer != null) {
                if (childReferrer instanceof Entity) {
                    if (treeEntity == null) {
                        treeEntity = (Entity) childReferrer;
                    } else {
                        if (!treeEntity.getName().equals(((Entity) childReferrer).getName())) {
                            throw new IllegalArgumentException("Ambiguous or Invalid Type " + treeEntity.getName() + " in TreeEntity near " + o);
                        }
                    }
                }
            }
        } else if (c instanceof CompiledParam) {
            Object co = ((CompiledParam) c).getValue();
            if (co != null && persistenceUnit.containsEntity(co.getClass())) {
                Entity rr = persistenceUnit.getEntity(co.getClass());
                if (treeEntity == null) {
                    treeEntity = rr;
                }
                ((CompiledParam) c).setValue(rr.getBuilder().objectToId(co));
            }
//            Object co = ((CompiledParam) c).getEffectiveDataType();
        }
        if (p instanceof CompiledVar) {
            Object parentReferrer = resolveReferrer((CompiledVar) ((CompiledVar) p).getFinest(),expressionCompiler);
            if (parentReferrer != null) {
                if (parentReferrer instanceof Entity) {
                    if (treeEntity == null) {
                        treeEntity = (Entity) parentReferrer;
                    } else {
                        if (!treeEntity.getName().equals(((Entity) parentReferrer).getName())) {
                            throw new IllegalArgumentException("Ambiguous or Invalid Type " + treeEntity.getName() + " in TreeEntity near " + o);
                        }
                    }
                }
            }
        } else if (p instanceof CompiledParam) {
            Object co = ((CompiledParam) p).getValue();
            if (co != null && persistenceUnit.containsEntity(co.getClass())) {
                Entity rr = persistenceUnit.findEntity(co.getClass());
                if (treeEntity == null) {
                    treeEntity = rr;
                }
                ((CompiledParam) p).setValue(rr.getBuilder().objectToId(co));
                if (rr.getIdFields().size() > 1) {
                    throw new IllegalArgumentException("Not supported");
                }
                ((CompiledParam) p).setTypeTransform(UPAUtils.getTypeTransformOrIdentity(rr.getIdFields().get(0)));
            }
//            Object co = ((CompiledParam) c).getEffectiveDataType();
        }
        Entity expectedEntity = n.getName() == null ? null : persistenceUnit.getEntity(n.getName());
        if (treeEntity == null) {
            if (expectedEntity != null) {
                treeEntity = expectedEntity;
            } else {
                throw new IllegalArgumentException("Unable to resolve Hierarchy Entity in " + o);
            }
        } else if (expectedEntity != null && !expectedEntity.getName().equals(treeEntity.getName())) {
            throw new IllegalArgumentException("Expected " + expectedEntity.getName() + " but found " + treeEntity.getName() + " in " + o);
        }

        Relationship t = HierarchicalRelationshipSupport.getTreeRelation(treeEntity);
        if (t == null) {
            throw new IllegalArgumentException("Hierarchy Relationship not found");
        }
        HierarchyExtension s = t.getHierarchyExtension();
        if (s == null) {
            throw new IllegalArgumentException("Not a valid TreeEntity");
        }
        Field pathField = treeEntity.getField(s.getHierarchyPathField());
        String pathSep = s.getHierarchyPathSeparator();
        return createConditionForDeepSearch(c, p, true, pathField, pathSep,expressionCompiler);
    }

    public DefaultCompiledExpression createConditionForDeepSearch(DefaultCompiledExpression alias, DefaultCompiledExpression id, boolean includeId, Field field, String pathSep,ExpressionCompiler expressionCompiler) throws UPAException {
        alias = alias.copy();
        if (alias instanceof CompiledVar) {
//            CompiledVar cv = (CompiledVar) alias;
            CompiledVarOrMethod finest = ((CompiledVar) alias).getFinest();
            Object referrer = resolveReferrer((CompiledVar) finest,expressionCompiler);
            if (referrer instanceof Entity) {

                CompiledVar v = new CompiledVar(field.getName());
                ((CompiledVar) alias).getFinest().setChild(v);
            } else if (referrer instanceof Field && ((Field) referrer).getDataType() instanceof ManyToOneType &&
                    ((ManyToOneType) ((Field) referrer).getDataType()).getTargetEntity().getName().equals(field.getEntity().getName())
                    ) {
                CompiledVar v = new CompiledVar(field.getName());
                finest.setChild(v);
            } else {
                throw new IllegalArgumentException("Expected " + field.getEntity().getName() + " var name");
            }
        } else {
            throw new IllegalArgumentException("Expected " + field.getEntity().getName() + " var name");
        }
        id = id.copy();
        List<Field> primaryFields = field.getEntity().getIdFields();
        if (primaryFields.size() > 1) {
            throw new IllegalArgumentException("Composite ID unsupported for function isHierarchyDescendant");
        }
        DataType pkType = primaryFields.get(0).getDataType();
        DefaultCompiledExpression strId = null;
        if (pkType instanceof IntType) {
            strId = new CompiledI2V(id);
        } else if (pkType instanceof LongType) {
            strId = new CompiledI2V(id);
        } else if (pkType instanceof ShortType) {
            strId = new CompiledI2V(id);
        } else if (pkType instanceof ByteType) {
            strId = new CompiledI2V(id);
        } else if (pkType instanceof FloatType) {
            strId = new CompiledD2V(id);
        } else if (pkType instanceof DoubleType) {
            strId = new CompiledD2V(id);
        } else if (pkType instanceof StringType) {
            strId = id;
        } else {
            strId = new CompiledCast(id, IdentityDataTypeTransform.STRING);
        }
        if (includeId) {
            return new CompiledOr(
                    new CompiledEquals(alias.copy(), strId.copy()),
                    new CompiledOr(
                            new CompiledLike(
                                    alias.copy(),
                                    new CompiledConcat(new CompiledLiteral("%" + pathSep), strId.copy())),
                            new CompiledOr(
                                    new CompiledLike(
                                            alias.copy(),
                                            new CompiledConcat(new CompiledLiteral("%" + pathSep), strId.copy(), new CompiledLiteral(pathSep + "%")))
                                    ,
                                    new CompiledLike(
                                            alias.copy(),
                                            new CompiledConcat(strId.copy(), new CompiledLiteral(pathSep + "%")))
                            )
                    )
            );
        } else {
            return new CompiledLike(
                    alias.copy(),
                    new CompiledConcat(new CompiledLiteral("%" + pathSep), strId.copy(), new CompiledLiteral(pathSep + "%")));
        }
    }
    public Object resolveReferrer(CompiledVar var, ExpressionCompiler expressionCompiler) {
        return expressionCompiler.resolveReferrer(var);
    }

}
