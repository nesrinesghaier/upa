package net.vpc.upa.impl.uql;

import net.vpc.upa.*;
import net.vpc.upa.exceptions.UPAException;
import net.vpc.upa.expressions.BinaryOperator;
import net.vpc.upa.expressions.CompiledExpression;
import net.vpc.upa.expressions.Expression;
import net.vpc.upa.expressions.ExpressionHelper;
import net.vpc.upa.extensions.HierarchyExtension;
import net.vpc.upa.impl.extension.HierarchicalRelationshipSupport;
import net.vpc.upa.impl.transform.IdentityDataTypeTransform;
import net.vpc.upa.impl.uql.compiledexpression.*;
import net.vpc.upa.impl.uql.util.UQLCompiledUtils;
import net.vpc.upa.impl.uql.util.UQLUtils;
import net.vpc.upa.impl.util.PlatformUtils;
import net.vpc.upa.impl.util.StringUtils;
import net.vpc.upa.impl.util.UPAUtils;
import net.vpc.upa.impl.util.filters.FieldFilters2;
import net.vpc.upa.persistence.ExpressionCompilerConfig;
import net.vpc.upa.types.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Created by vpc on 6/28/17.
 */
public class ExpressionCompiler implements CompiledExpressionFilteredReplacer {
    private static Logger log = Logger.getLogger(ExpressionCompiler.class.getName());
    private PersistenceUnit persistenceUnit;
    private CompiledExpression rootExpression;
    private ExpressionCompilerConfig config;
    private int navigationDepth;
    private QueryFetchStrategy fetchStrategy;
    private ExpressionManager expressionManager;

    public ExpressionCompiler(CompiledExpression rootExpression, ExpressionCompilerConfig config, PersistenceUnit persistenceUnit) {
        this.persistenceUnit = persistenceUnit;
        this.expressionManager = persistenceUnit.getExpressionManager();
        this.rootExpression = rootExpression;
        this.config = config;
        navigationDepth = (Integer) config.getHint(QueryHints.NAVIGATION_DEPTH, -1);
        if (navigationDepth < 0) {
            navigationDepth = 4;
        }
        fetchStrategy = (QueryFetchStrategy) config.getHint(QueryHints.FETCH_STRATEGY, QueryFetchStrategy.JOIN);
        if (fetchStrategy == null) {
            fetchStrategy = QueryFetchStrategy.JOIN;
        }
    }

//    private int incDepth(String n) {
//        int v = getDepth(n) + 1;
//        depthTracker.put(n, v);
//        return v;
//    }
//
//    private int getDepth(String n) {
//        Integer v = depthTracker.get(n);
//        if (v == null) {
//            return 0;
//        }
//        return v;
//    }

    public CompiledExpression compile() {
        //will not create a copy in production mode
        CompiledExpression expression = UPAUtils.PRODUCTION_MODE?rootExpression:((DefaultCompiledExpression) rootExpression).copy();
        log.log(Level.FINEST, "Validate Compiled Expression {0}\n\t using config {1}", new Object[]{expression, config});

        DefaultCompiledExpression dce = (DefaultCompiledExpression) expression;
        //dce.copy()
        String entityBaseName = null;
        String entityAlias = null;
        if (config.isResolveThis()) {
            if (dce instanceof CompiledEntityStatement) {
                entityBaseName = ((CompiledEntityStatement) dce).getEntityName();
                entityAlias = ((CompiledEntityStatement) dce).getEntityAlias();
                if (entityBaseName != null) { // && config.getAliasToEntityContext().get(UQLUtils.THIS)==null
                    config.bindAliasToEntity(UQLUtils.THIS, entityBaseName);
                }
                if (entityAlias != null) {
                    config.setThisAlias(entityAlias);
                } else {
                    config.setThisAlias(UQLUtils.THIS);
                }
            }
        }

        ReplaceResult v2 = UQLCompiledUtils.replaceExpressions(dce, this);
        if (v2.isNewInstance()) {
            return v2.getExpression();
        }

//        String newName = entityAlias;//entityAlias != null ? entityAlias : entityBaseName;
//        if (!UQLUtils.THIS.equals(config.getThisAlias())) {
//            newName = config.getThisAlias();
//        }
//        if (newName != null) {
//            CompiledExpressionUtils.replaceThisVar(dce, newName);
//        } else {
//            CompiledExpressionUtils.removeThisVar(dce);
//        }
        return dce;
    }


    public boolean isTopDown() {
        return true;
    }

    public ReplaceResult update(CompiledExpression e) {
        ReplaceResult result = ReplaceResult.NO_UPDATES_CONTINUE;
        if (e instanceof CompiledSelect) {
            return updateCompiledSelect((CompiledSelect) e);
        } else if (e instanceof CompiledUpdate) {
            return updateCompiledUpdate((CompiledUpdate) e);
        } else if (e instanceof CompiledInsert) {
            return updateCompiledInsert((CompiledInsert) e);
        } else if (e instanceof CompiledQueryField) {
            throw new IllegalArgumentException("Should have been processed elsewhere");
        } else if (e instanceof CompiledVar) {
            return updateCompiledVar((CompiledVar) e, null, null, true);
        } else if (e instanceof CompiledParam) {
            return updateCompiledParam((CompiledParam) e);
//        } else if (e instanceof IsHierarchyDescendantCompiled) {
//            return updateIsHierarchyDescendantCompiled((IsHierarchyDescendantCompiled) e);
        } else if (e instanceof CompiledQLFunctionExpression) {
            return updateCompiledQLFunctionExpression((CompiledQLFunctionExpression) e);
        } else if (e instanceof CompiledBinaryOperatorExpression) {
            return updateCompiledBinaryOperatorExpression((CompiledBinaryOperatorExpression) e);
        }
        return result;
    }

    private ReplaceResult updateCompiledBinaryOperatorExpression(CompiledBinaryOperatorExpression tt) {
        UQLCompiledUtils.replaceExpressionChildren(tt, this);
        //process children first !!
        DefaultCompiledExpression left = tt.getLeft();
        DefaultCompiledExpression right = tt.getRight();
        Field clientProperty = (Field) left.getClientProperty("UPA.Flattened.BaseField");
        if (right instanceof CompiledVar && !(left instanceof CompiledVar)) {
            DefaultCompiledExpression temp = left;
            left = right;
            right = temp;
        }
        if (left instanceof CompiledVar && clientProperty != null && clientProperty.getDataType() instanceof ManyToOneType) {
            ManyToOneType dataType = (ManyToOneType) clientProperty.getDataType();
            Class entityType = dataType.getTargetEntity().getEntityType();
            if (right instanceof CompiledParam) {
                CompiledParam p = (CompiledParam) right;
                if (p.getValue() != null && ((p.getValue() instanceof Document) || entityType.isInstance(p.getValue()))) {
                    Object id = dataType.getTargetEntity().getBuilder().objectToId(p.getValue());
                    //the id is supposed simple (single value, not an array)
                    //TODO : what to do if array?
                    p.setValue(id);
                    return ReplaceResult.UPDATE_AND_STOP;
                }
            } else if (right instanceof CompiledLiteral) {
                CompiledLiteral p = (CompiledLiteral) right;
                if (p.getValue() != null && ((p.getValue() instanceof Document) || entityType.isInstance(p.getValue()))) {
                    Object id = dataType.getTargetEntity().getBuilder().objectToId(p.getValue());
                    //the id is supposed simple (single value, not an array)
                    //TODO : what to do if array?
                    p.setValue(id);
                    p.setType(dataType.getRelationship().getSourceRole().getField(0).getTypeTransform());
                    return ReplaceResult.UPDATE_AND_STOP;
                }
            }

        }
        return ReplaceResult.NO_UPDATES_STOP;
    }

    private ReplaceResult updateCompiledQueryField(CompiledQueryField tt, CompiledEntityStatement enclosingStmt, int index, int depth) {
        if (enclosingStmt == null) {
            enclosingStmt = (CompiledEntityStatement) tt.getParentExpression();
        }
        if (enclosingStmt == null) {
            throw new IllegalArgumentException("No Enclosing Select for " + tt);
        }
        boolean again = true;
        DefaultCompiledExpression ttExpr = tt.getExpression();
        DefaultCompiledExpression ttExpr0 = ttExpr;
        ReplaceResult r = null;
        boolean someUpdates = false;
        while (again) {
            again = false;
            r = (ttExpr0 instanceof CompiledVar) ? updateCompiledVar((CompiledVar) ttExpr0, null, enclosingStmt, false) : UQLCompiledUtils.replaceExpressions(ttExpr0, this);
            if (r.getType() != ReplaceResultType.NO_UPDATES) {
                someUpdates = true;
            }
            if (r.isNewInstance() && !r.isStop()) {
                again = true;
                ttExpr0 = r.getExpression(ttExpr);
                tt.setExpression(ttExpr0);
            }
        }
        DefaultCompiledExpression e2 = r.getExpression(ttExpr0);
        if (e2 instanceof CompiledVar) {
            CompiledVar cv = (CompiledVar) e2;
            if (cv.getChild() != null && cv.getChild().getChild() != null) {
                throw new IllegalArgumentException("Unexpected!!");
            }
            CompiledVar finest = (CompiledVar) cv.getFinest();
            List<Field> fieldsToExpand = new ArrayList<>();
            List<Field> fieldsToPreserve = new ArrayList<>();
            Object referrer = resolveReferrer(finest);
            if (referrer instanceof Entity) {
                fieldsToExpand.addAll(((Entity) referrer).getFields(depth == 0 ? FieldFilters2.ID : config.getExpandFieldFilter()));
            } else if (referrer instanceof Field) {
                Field field = (Field) referrer;
                if (field.getDataType() instanceof ManyToOneType) {
                    ManyToOneType manyToOneType = (ManyToOneType) field.getDataType();
                    if (depth <= 0 || fetchStrategy == QueryFetchStrategy.SELECT) {
                        fieldsToPreserve.addAll(manyToOneType.getRelationship().getSourceRole().getFields());
                    } else if (fetchStrategy == QueryFetchStrategy.JOIN) {
                        DefaultCompiledExpression pp = finest.getParentExpression();
                        String joinAlias = null;//tt.getAlias()
                        if (pp != null && pp instanceof CompiledVar) {
                            joinAlias = ((CompiledVar) pp).getName();
                        }
                        if (StringUtils.isNullOrEmpty(joinAlias)) {
                            throw new IllegalArgumentException("Unable to resolve join alias : " + cv);
                        }
                        BindingJoinInfo bindingJoinInfo = addBindingJoin(enclosingStmt, field, joinAlias, tt.getBinding());
                        finest = new CompiledVar(
                                bindingJoinInfo.alias,
                                bindingJoinInfo.entity,
                                bindingJoinInfo.binding
                        );
                        fieldsToExpand.addAll(FieldFilters2.filter(manyToOneType.getRelationship().getTargetEntity().getFields(), config.getExpandFieldFilter()));
                    } else {
                        throw new IllegalArgumentException("Unsupported Fetch Strategy " + fetchStrategy);
                    }
                } else {
                    if (someUpdates) {
                        tt.setExpression(e2);
                        return ReplaceResult.UPDATE_AND_STOP;
                    }
                    return ReplaceResult.NO_UPDATES_STOP;
                }
            } else {
                throw new IllegalArgumentException("Unexpected");
            }
            CompiledQueryFieldsTuple t = new CompiledQueryFieldsTuple();
            for (Field field : fieldsToPreserve) {
                DefaultCompiledExpression pp = finest.getParentExpression();
                if (pp == null || (pp.getParentExpression() != null && !(pp.getParentExpression() instanceof CompiledQueryField))) {
                    throw new IllegalArgumentException("Unexpected");
                }
                pp = pp.copy();
                CompiledVar f3 = new CompiledVar(field.getName(), field, BindingId.createChild(tt.getBinding(), field.getName()));
                ((CompiledVar) pp).setChild(f3);
                resolveReferrer((CompiledVar) pp);
                resolveReferrer(f3);

                CompiledQueryField item = new CompiledQueryField(
                        field.getName(),
                        pp,
                        -1,
                        false,
                        null,
                        BindingId.createChild(tt.getBinding().getParent(), field.getName()),
                        null

                );
                t.add(item);
            }

            for (Field field : fieldsToExpand) {
                CompiledVar f2 = (CompiledVar) finest.copy();
                CompiledVar f3 = new CompiledVar(field.getName(), field, BindingId.createChild(tt.getBinding(), field.getName()));
                f2.setChild(f3);

                CompiledQueryField item = new CompiledQueryField(
                        field.getName(),
                        f2,
                        -1,
                        false,
                        null,
                        BindingId.createChild(tt.getBinding(), field.getName()),
                        null

                );

                //just to help sub replacements!
                item.setParentExpression(enclosingStmt);

                if (depth > 0) {
                    ReplaceResult replaceResult = updateCompiledQueryField(item, enclosingStmt, -1, depth - 1);
                    CompiledExpression expression = replaceResult.getExpression(item);
                    if (expression instanceof CompiledQueryFieldsTuple) {
                        for (CompiledQueryField compiledQueryField : ((CompiledQueryFieldsTuple) expression).getItems()) {
                            t.add(compiledQueryField);
                        }
                    } else {
                        t.add((CompiledQueryField) expression);
                    }
                } else {
                    t.add(item);
                }

                //remove Binding
                item.setParentExpression(null);
            }
            if (t.getItems().size() == 1) {
                return ReplaceResult.stopWithNewObj(t.getItems().get(0));
            } else if (t.getItems().size() == 0) {
                throw new IllegalArgumentException("Unexpected");
            } else {
                return ReplaceResult.stopWithNewObj(t);
            }
        } else {
            return ReplaceResult.UPDATE_AND_CONTINUE_CLEAN;
        }
    }


    private ReplaceResult updateCompiledVar(CompiledVar o, CompiledEntityStatement rootStatement, CompiledEntityStatement enclosingStmt, boolean flattenId) {
        Object ref = resolveReferrer(o);
        if(o.getImplicitDeclaration()!=null){
            if(o.getImplicitDeclaration().getName()!=null) {
                ExpressionDeclaration idec = o.getImplicitDeclaration();
                if (idec.getValidName() == null) {
                    throw new IllegalArgumentException("Unexpected null name");
                }
                CompiledVar v2 = new CompiledVar(idec.getValidName());
                switch (idec.getReferrerType()) {
                    case ENTITY: {
                        v2.setReferrer(persistenceUnit.getEntity((String) idec.getReferrerName()));
                        break;
                    }
                    case FIELD: {
                        throw new IllegalArgumentException("Unexpected field declaration");
                    }
                    case SELECT: {
                        throw new IllegalArgumentException("Unexpected Select declaration");
                    }
                    default: {
                        throw new IllegalArgumentException("Unexpected unknown declaration");
                    }
                }
                DefaultCompiledExpression p = o.getParentExpression();
                o.unsetParent();
                o.setImplicitDeclaration(null);
                v2.setChild(o);
                v2.setParentExpression(p);
                ReplaceResult r2 = updateCompiledVar(v2, rootStatement, enclosingStmt, flattenId);
                v2.setParentExpression(null);
                return ReplaceResult.stopWithNewObj(v2);
            }
        }
        if (enclosingStmt == null) {
            enclosingStmt = UQLCompiledUtils.findEnclosingStatement(o, persistenceUnit);
        }
        if (enclosingStmt == null) {
            throw new IllegalArgumentException("No Enclosing Statement for " + o);
        }
        boolean isThis = !(o.getParentExpression() instanceof CompiledVar) && UQLUtils.THIS.equals(o.getName());
        if (isThis) {
            if (rootStatement == null) {
                rootStatement = UQLCompiledUtils.findRootStatement(o, persistenceUnit);
            }
            if (rootStatement == null) {
                throw new IllegalArgumentException("No Root Statement for " + o);
            }
            if (rootStatement.getEntityAlias() == null) {
                CompiledVarOrMethod child = o.getChild();
                if (child == null) {
                    if (rootStatement.getEntityName() == null) {
                        throw new IllegalArgumentException("Missing Alias for " + rootStatement);
                    }
                    o.setName(rootStatement.getEntityName());
                    return ReplaceResult.UPDATE_AND_CONTINUE_CLEAN;
                }
                ReplaceResult replaceChild = null;
                if (child instanceof CompiledVar) {
                    replaceChild = updateCompiledVar((CompiledVar) child, rootStatement, enclosingStmt, flattenId);
                } else {
                    replaceChild = UQLCompiledUtils.replaceExpressions(child, this);
                }
                DefaultCompiledExpression e2 = replaceChild.getExpression(child);
                e2.setParentExpression(null);
                return ReplaceResult.continueWithNewCleanObj(e2);
            }
        }
        String entityAlias = enclosingStmt.getEntityAlias();
//        if (entityAlias == null) {
//            throw new IllegalArgumentException("Missing entityAlias for "+enclosingStmt);
//        }
        if (ref instanceof Field) {

            Field field = (Field) ref;
            if (field.getModifiers().contains(FieldModifier.SELECT_LIVE)) {
                Formula liveFormula = field.getSelectFormula();
                if (liveFormula instanceof CustomFormula) {
                    //do nothing, should be processed as custom formula later
                    throw new IllegalArgumentException("Unsupported Live Formula for " + field + " : " + liveFormula);
                } else if (liveFormula instanceof ExpressionFormula) {
                    ExpressionFormula ef = (ExpressionFormula) liveFormula;
                    Expression expr = ef.getExpression();

                    ExpressionCompilerConfig cfg = new ExpressionCompilerConfig();
                    cfg.setTranslateOnly();
                    cfg.setThisAlias(entityAlias);
                    cfg.bindAliasToEntity(entityAlias, field.getEntity().getName());
                    DefaultCompiledExpression rr = (DefaultCompiledExpression) expressionManager.compileExpression(expr, cfg);
                    return ReplaceResult.continueWithNewDirtyObj(rr);
                } else {
                    throw new IllegalArgumentException("Unsupported Live Formula for " + field + " : " + liveFormula);
                }
            } else {
                DataType dataType = field.getDataType();
                if (dataType instanceof ManyToOneType) {
                    ManyToOneType manyToOneType = (ManyToOneType) dataType;
                    if (o.getChild() == null) {
                        DefaultCompiledExpression baseRoot = UQLCompiledUtils.findRootNonVar(o);
                        if (baseRoot instanceof CompiledQueryField) {
                            //good no further guesses!
                        } else if (baseRoot instanceof CompiledBinaryOperatorExpression) {
                            //force flatten!
                            flattenId = true;
                        } else {

                        }
                        if (flattenId) {
                            List<Field> fields = manyToOneType.getRelationship().getSourceRole().getFields();
                            if (fields.size() == 1) {
                                Field field0 = fields.get(0);
                                CompiledVar newVar = new CompiledVar(field0.getName(), field0, BindingId.createChild(o.getBinding(), field0.getName()));
                                newVar.setReferrer(field0);
                                newVar.setClientProperty("UPA.Flattened", true);
                                newVar.setClientProperty("UPA.Flattened.BaseField", field);
                                return ReplaceResult.stopWithNewObj(newVar);
                            } else {
                                throw new IllegalArgumentException("Unsupported multi Id Entity " + manyToOneType.getRelationship().getSourceRole().getEntity());
                            }
                        } else {
                            return ReplaceResult.NO_UPDATES_STOP;
                        }
                    } else {
                        BindingJoinInfo d = addBindingJoin(enclosingStmt, field, entityAlias, BindingId.createChild(o.getBinding(), field.getName()));
                        CompiledVarOrMethod child = o.getChild();
                        ReplaceResult repl = updateCompiledVar((CompiledVar) child, rootStatement, enclosingStmt, flattenId);
                        CompiledVar newVar = new CompiledVar(d.alias, d.entity, d.binding);
                        newVar.setBinding(d.binding);
                        o.setChild(null); //unbind child to old parent
                        if (repl.isNewInstance()) {
                            CompiledVarOrMethod child2 = (CompiledVarOrMethod) repl.getExpression(child);
                            if (child2.getChild() == null) {
                                newVar.setChild(child2);
                            } else {
                                newVar = (CompiledVar) child2;
                                newVar.setBinding(d.binding);
                            }
                        } else {
                            newVar.setChild(child);
                        }
                        return ReplaceResult.stopWithNewObj(newVar);
                    }
                }
            }
        } else if (ref instanceof Entity) {
            CompiledVarOrMethod child = o.getChild();
            if (child == null) {
                return ReplaceResult.NO_UPDATES_STOP;
            }
            ReplaceResult replaceChild = null;
            if (child instanceof CompiledVar) {
                replaceChild = updateCompiledVar((CompiledVar) child, rootStatement, enclosingStmt, flattenId);
            } else {
                replaceChild = UQLCompiledUtils.replaceExpressions(child, this);
            }
            switch (replaceChild.getType()) {
                case NEW_INSTANCE: {
                    o.setChild(null);
                    ReplaceResult e2 = UQLCompiledUtils.replaceThisVar(replaceChild.getExpression(), o);
                    return replaceChild.isStop() ?
                            ReplaceResult.stopWithNewObj(e2.getExpression(replaceChild.getExpression())) :
                            ReplaceResult.continueWithNewCleanObj(e2.getExpression(replaceChild.getExpression()))
                            ;
//                    child.setParentExpression(null);
//                    if(replaceChild.getExpression() instanceof CompiledVarOrMethod) {
//                        o.setChild((CompiledVarOrMethod) replaceChild.getExpression());
//                    }else{
//                        throw new IllegalArgumentException("Could not replace "+child+" by "+replaceChild.getExpression()+" for parent "+o);
//                    }
//                    return ReplaceResult.UPDATE_AND_STOP;
                }
                case UPDATE: {
                    return ReplaceResult.UPDATE_AND_STOP;
                }
                case REMOVE: {
                    child.setParentExpression(null);
                    o.setChild(null);
                    return ReplaceResult.UPDATE_AND_STOP;
                }
            }
        }
        return ReplaceResult.NO_UPDATES_STOP;
    }

    private ReplaceResult updateCompiledUpdate(CompiledUpdate s) {
        for (int i = 0; i < s.countFields(); i++) {
            CompiledVar fvar = s.getField(i);
            DefaultCompiledExpression vv = s.getFieldValue(i);

            Field f = (Field) resolveReferrer(fvar);
            if (f == null) {
                throw new IllegalArgumentException("Field not found " + fvar + " in " + s.getEntity().getName());
            }
            if (vv.getTypeTransform() == null || vv.getTypeTransform().getTargetType().getPlatformType().equals(Object.class)) {
                vv.setTypeTransform(UPAUtils.getTypeTransformOrIdentity(f));
            } else {
                //ignore
            }
            if (fvar.getChild() != null) {
                if (!(fvar.getChild() instanceof CompiledVar)) {
                    throw new IllegalArgumentException();
                }
                if (fvar.getChild().getChild() != null) {
                    throw new IllegalArgumentException();
                }
            }
        }
        return ReplaceResult.UPDATE_AND_CONTINUE_CLEAN;
    }

    private ReplaceResult updateCompiledInsert(CompiledInsert s) {
        for (int i = 0; i < s.countFields(); i++) {
            CompiledVar fvar = s.getField(i);
            DefaultCompiledExpression vv = s.getFieldValue(i);
            Field f = (Field) resolveReferrer(fvar);
            if (f == null) {
                throw new IllegalArgumentException("Field not found " + fvar + " in " + s.getEntity().getName());
            }
            if (vv.getTypeTransform() == null || vv.getTypeTransform().getTargetType().getPlatformType().equals(Object.class)) {
                vv.setTypeTransform(UPAUtils.getTypeTransformOrIdentity(f));
            } else {
                //ignore
            }
        }
        return ReplaceResult.UPDATE_AND_CONTINUE_CLEAN;
    }

    private ReplaceResult updateCompiledSelect(CompiledSelect s) {
        ReplaceResult result = ReplaceResult.NO_UPDATES_STOP;
        if (s.getParentExpression() == null) {
            //this is the root select
            if (s.getFields().size() == 0) {
                String a = s.getEntityAlias();
                if (a == null) {
                    s.setEntityAlias(UQLUtils.THIS);
                }
                CompiledVar v = new CompiledVar(s.getEntityAlias());
                v.setReferrer(persistenceUnit.getEntity(s.getEntityName()));
                s.field(v);
                //stop because all children will be processed here!
                result = ReplaceResult.UPDATE_AND_STOP;
            }
        } else {
            if (s.getFields().size() == 0) {
                throw new IllegalArgumentException("Inner Select Should have at least one selected field");
            }
        }
        expandEntityFilters(s);
        List<CompiledQueryField> fields = new ArrayList<>(s.getFields());
        List<Integer> toRemove = new ArrayList<>();
        for (int i = 0; i < fields.size(); i++) {
            CompiledQueryField qf = fields.get(i);
            qf.setBinding(BindingId.create(String.valueOf(i)));
            ReplaceResult rqf = updateCompiledQueryField(qf, s, i, navigationDepth);
            if (rqf.getType() == ReplaceResultType.NEW_INSTANCE) {
                result = ReplaceResult.UPDATE_AND_STOP;
                DefaultCompiledExpression replacement = rqf.getExpression();
                if (replacement instanceof CompiledQueryFieldsTuple) {
                    CompiledQueryField[] subExpressions = ((CompiledQueryFieldsTuple) replacement).getCompiledQueryFields();
                    toRemove.add(i);
                    for (CompiledQueryField ee : subExpressions) {
                        s.addField(ee);
                    }
                } else if (replacement instanceof CompiledQueryField) {
                    CompiledQueryField replacement1 = (CompiledQueryField) replacement;
                    s.setField(replacement1, i);
                } else {
                    qf.setExpression(replacement);
                }
            }
            if (rqf.getType() != ReplaceResultType.NO_UPDATES) {
                result = ReplaceResult.UPDATE_AND_STOP;
            }
        }
        for (int i = toRemove.size() - 1; i >= 0; i--) {
            s.removeField(toRemove.get(i));
        }
        DefaultCompiledExpression[] groupByExpressions = s.getGroupByExpressions();
        for (int i = 0; i < groupByExpressions.length; i++) {
            DefaultCompiledExpression ee = groupByExpressions[i];
            ReplaceResult rqf = UQLCompiledUtils.replaceExpressions(ee, this);
            if (rqf.getType() == ReplaceResultType.NEW_INSTANCE) {
                s.setGroupBy(rqf.getExpression(), i);
            }
            if (rqf.getType() != ReplaceResultType.NO_UPDATES) {
                result = ReplaceResult.UPDATE_AND_STOP;
            }
        }
        CompiledOrderItem[] orders = s.getOrderByExpressions();
        for (int i = 0; i < orders.length; i++) {
            DefaultCompiledExpression ee = orders[i].getExpression();
            ReplaceResult rqf = UQLCompiledUtils.replaceExpressions(ee, this);
            if (rqf.getType() == ReplaceResultType.NEW_INSTANCE) {
                orders[i].setExpression(rqf.getExpression());
            }
            if (rqf.getType() != ReplaceResultType.NO_UPDATES) {
                result = ReplaceResult.UPDATE_AND_STOP;
            }
        }
        CompiledNameOrSelect entity = s.getEntity();
        if (entity != null) {
            ReplaceResult rqf = UQLCompiledUtils.replaceExpressions(entity, this);
            if (rqf.getType() == ReplaceResultType.NEW_INSTANCE) {
                s.from((CompiledNameOrSelect) rqf.getExpression(), s.getEntityAlias());
            }
            if (rqf.getType() != ReplaceResultType.NO_UPDATES) {
                result = ReplaceResult.UPDATE_AND_STOP;
            }
        }
        DefaultCompiledExpression where = s.getWhere();
        if (where != null) {
            ReplaceResult rqf = UQLCompiledUtils.replaceExpressions(where, this);
            if (rqf.getType() == ReplaceResultType.NEW_INSTANCE) {
                s.where(rqf.getExpression(where));
            }
            if (rqf.getType() != ReplaceResultType.NO_UPDATES) {
                result = ReplaceResult.UPDATE_AND_STOP;
            }
        }
        DefaultCompiledExpression having = s.getHaving();
        if (having != null) {
            ReplaceResult rqf = UQLCompiledUtils.replaceExpressions(having, this);
            if (rqf.getType() == ReplaceResultType.NEW_INSTANCE) {
                s.having(rqf.getExpression(having));
            }
            if (rqf.getType() != ReplaceResultType.NO_UPDATES) {
                result = ReplaceResult.UPDATE_AND_STOP;
            }
        }
        CompiledJoinCriteria[] joins = s.getJoins();
        for (int i = 0; i < joins.length; i++) {
            DefaultCompiledExpression condition = joins[i].getCondition();
            if (condition != null) {
                ReplaceResult rqf = UQLCompiledUtils.replaceExpressions(condition, this);
                if (rqf.getType() == ReplaceResultType.NEW_INSTANCE) {
                    joins[i].setCondition(rqf.getExpression());

                }
                if (rqf.getType() != ReplaceResultType.NO_UPDATES) {
                    result = ReplaceResult.UPDATE_AND_STOP;
                }
            }
        }
        return result;
    }


    private ReplaceResult updateCompiledParam(CompiledParam o) {
        DataTypeTransform d = getValidDataType(o);
        if (d != null) {
            return ReplaceResult.NO_UPDATES_STOP;
        }
        if (o.getValue() != null) {
            o.setTypeTransform(IdentityDataTypeTransform.forNativeType(o.getValue().getClass()));
            d = getValidDataType(o);
            if (d != null) {
                return ReplaceResult.NO_UPDATES_STOP;
            }
        }
        DefaultCompiledExpression pe = o.getParentExpression();
        if (pe instanceof CompiledBinaryOperatorExpression) {
            CompiledBinaryOperatorExpression c = (CompiledBinaryOperatorExpression) pe;
            BinaryOperator operator = c.getOperator();
            switch (operator) {
                case EQ:
                case DIFF:
                case LE:
                case LT:
                case GE:
                case GT:
                case BIT_AND:
                case BIT_OR:
                case PLUS:
                case MINUS:
                case MUL:
                case DIV:
                case REM:
                case LSHIFT:
                case RSHIFT:
                case XOR:
                case URSHIFT: {
                    DefaultCompiledExpression a = c.getLeft();
                    DefaultCompiledExpression b = c.getRight();
                    if (o == a) {
                        DataTypeTransform d2 = getValidDataType(b);
                        if (d2 != null) {
                            o.setTypeTransform(d2);
                            return ReplaceResult.UPDATE_AND_STOP;
                        }
                    } else if (o == b) {
                        DataTypeTransform d2 = getValidDataType(a);
                        if (d2 != null) {
                            o.setTypeTransform(d2);
                            return ReplaceResult.UPDATE_AND_STOP;
                        }
                    }
                    return ReplaceResult.NO_UPDATES_STOP;
                }
                case LIKE: {
                    DefaultCompiledExpression a = c.getLeft();
                    DefaultCompiledExpression b = c.getRight();
                    o.setTypeTransform(IdentityDataTypeTransform.STRING_UNLIMITED);
                    return ReplaceResult.UPDATE_AND_STOP;
                }
                case OR:
                case AND: {
                    o.setTypeTransform(IdentityDataTypeTransform.BOOLEAN);
                    return ReplaceResult.UPDATE_AND_STOP;
                }
            }
        } else if (pe instanceof CompiledVarVal) {
            CompiledVarVal cvv = (CompiledVarVal) pe;
            if (cvv.getVal() == o) {
                //it should be the case
                o.setTypeTransform(cvv.getVar().getTypeTransform());
                return ReplaceResult.UPDATE_AND_STOP;
            }
        }
        return ReplaceResult.NO_UPDATES_STOP;
    }

    private BindingJoinInfo addBindingJoin(CompiledEntityStatement qs, Field field, String entityAlias, BindingId binding) {
        if (entityAlias == null) {
            throw new IllegalArgumentException("Null Join Alias");
        }
        ExprContext context = ExprContext.get(qs);
        BindingJoinInfo ret = new BindingJoinInfo();
        Relationship rel = ((ManyToOneType) field.getDataType()).getRelationship();
        Entity masterEntity = rel.getTargetRole().getEntity();
        String generatedAlias = context.getAliasFor(binding);
        boolean addJoin = generatedAlias == null;
        ret.entity = masterEntity;
        ret.binding = binding;
        if (!addJoin) {
            ret.alias = generatedAlias;
        } else {
            ret.newlyCreated = true;
            generatedAlias = context.createAliasFor(binding);
            DefaultCompiledExpression cond = null;
            Entity detailEntity = field.getEntity();
            for (Map.Entry<String, String> entry : rel.getTargetToSourceFieldNamesMap(false).entrySet()) {

                CompiledVar detailAlias = new CompiledVar(entityAlias, detailEntity, null);
                CompiledVar masterAlias = new CompiledVar(generatedAlias, masterEntity, null);
                detailAlias.setChild(new CompiledVar(detailEntity.getField(entry.getValue())));
                masterAlias.setChild(new CompiledVar(masterEntity.getField(entry.getKey())));
                CompiledEquals cond0 = new CompiledEquals(detailAlias, masterAlias);

                if (cond == null) {
                    cond = cond0;
                } else {
                    cond = new CompiledAnd(cond, cond0);
                }
            }
            ret.alias = generatedAlias;
            ret.cond = cond;
            if (qs instanceof CompiledSelect) {
                ((CompiledSelect) qs).leftJoin(ret.entity.getName(), ret.alias, ret.cond);
            } else if (qs instanceof CompiledUpdate) {
                ((CompiledUpdate) qs).leftJoin(ret.entity.getName(), ret.alias, ret.cond);
            } else if (qs instanceof CompiledDelete) {
                throw new IllegalArgumentException("Unsupported");
                //((CompiledDelete) qs).leftJoin(ret.entity.getName(), ret.alias, ret.cond);
            } else {
                throw new IllegalArgumentException("Unsupported");
            }
//            qs.getDeclarationList().push(masterAliasString, masterEntity);
        }
        return ret;
    }

//    private void expandFields(CompiledSelect qs) {
//        ExpansionVisitTracker visitedEntities = new ExpansionVisitTracker(navigationDepth);
//
//        if (qs.countFields() == 0) {
//            CompiledVar fvar = new CompiledVar("*");//throw unsupported
//            qs.field(fvar, null);
//        }
//        final List<CompiledQueryField> fields = new ArrayList<CompiledQueryField>(qs.getFields());
//        qs.removeAllFields();
//        for (CompiledQueryField f : fields) {
//            expandField(qs, true,f, visitedEntities);
//        }
//    }
//
//    /**
//     *
//     * @param qs
//     * @param expandAll if true expand sub fields else join add joins
//     * @param index
//     * @param field
//     * @param entityAlias
//     * @param binding
//     * @param aliasBinding
//     * @param expansionVisitTracker
//     * @return
//     */
//    private boolean expandOnNeedField(CompiledSelect qs, boolean expandAll,int index, Field field, String entityAlias, String binding, String aliasBinding, ExpansionVisitTracker expansionVisitTracker) {
//        if (binding !=null && binding.length() == 0) {
//            binding = null;
//        }
//        if (field.getModifiers().contains(FieldModifier.SELECT_LIVE)) {
//            Formula f = field.getSelectFormula();
//            if (f instanceof CustomFormula) {
//                //do nothing, should be processed as custom formula later
//            } else if (f instanceof ExpressionFormula) {
//                ExpressionFormula ef = (ExpressionFormula) f;
//                Expression expr = ef.getExpression();
//
//                ExpressionCompilerConfig cfg = new ExpressionCompilerConfig();
//                cfg.setThisAlias(entityAlias);
//                cfg.setExpandEntityFilter(false);
//                cfg.setExpandFields(false);
//                cfg.setValidate(false);
//                cfg.bindAliasToEntity(entityAlias, field.getEntity().getName());
//                DefaultCompiledExpression rr = (DefaultCompiledExpression) persistenceUnit.getExpressionManager().compileExpression(expr, cfg);
//                if (rr instanceof CompiledVarOrMethod) {
//                    ((CompiledVarOrMethod) rr).getFinest().setBaseReferrer(field);
//                }
//
//                cfg = new ExpressionCompilerConfig();
//                cfg.setThisAlias(UQLUtils.THIS);
//                cfg.setExpandEntityFilter(false);
//                cfg.setExpandFields(false);
//                cfg.setValidate(true);
//                cfg.bindAliasToEntity(UQLUtils.THIS, field.getEntity().getName());
//                if(!UQLUtils.THIS.equals(config.getThisAlias())) {
//                    cfg.bindAliasToEntity(UQLUtils.THIS, field.getEntity().getName());
//                    cfg.setThisAlias(config.getThisAlias());
//                    cfg.bindAliasToEntity(config.getThisAlias(), field.getEntity().getName());
//                }
//                persistenceUnit.getExpressionManager().compileExpression(rr, cfg);
////                expandField(qs,new CompiledQueryField(field.getName(),rr,index,false,field.getName(),binding,null),expansionVisitTracker,config);
//                expandField(qs,expandAll,new CompiledQueryField(field.getName(),rr,-1,false,field.getName(),binding,aliasBinding),expansionVisitTracker);
//                for (CompiledVar compiledVar : CompiledExpressionUtils.findChildrenLeafVars(qs)) {
////            validateCompiledVar(compiledVar, config);
//                    validateCompiledVarRelation(compiledVar);
//                }
//            } else {
//                //ignore
//            }
//
//
//            return true;
//        } else if (field.getDataType() instanceof ManyToOneType) {
//            if (QueryFetchStrategy.JOIN == (fetchStrategy)) {
//                expandManyToOneFieldJoinFetch(qs, expandAll,index, field, entityAlias, binding, aliasBinding, config.getExpandFieldFilter(), expansionVisitTracker);
//            } else if (QueryFetchStrategy.SELECT == (fetchStrategy)) {
//                expandManyToOneFieldSelectFetch(qs, expandAll,index, field, entityAlias, binding, aliasBinding, config.getExpandFieldFilter(), expansionVisitTracker);
//            }
//            return true;
//        }
//        return false;
//    }

//    private void expandEntityFields(CompiledSelect qs, boolean expandAll,int index, Entity e, String entityAlias, String binding, String aliasBinding, ExpansionVisitTracker visitedEntities) {
//        for (Field field : e.getFields(FieldFilters.as(config.getExpandFieldFilter()).and(FieldFilters2.READ))) {
//            ExpansionVisitTracker c = visitedEntities.copy();
//            String aliasBinding1 = UPAUtils.dotConcat(aliasBinding, field.getName());
//            if (!expandOnNeedField(qs,expandAll, index, field, entityAlias, binding,aliasBinding1,c)) {
//                CompiledVar vv = new CompiledVar(entityAlias, e);
//                vv.setChild(new CompiledVar(field));
//                qs.addField(vv, field.getName())
//                        .setBinding(binding)
//                        .setIndex(index)
//                        .setExpanded(true)
//                        .setAliasBinding(aliasBinding1);
//            }
//        }
//
//    }

//    private void expandManyToOneFieldJoinFetch(CompiledSelect qs, boolean expandAll, int index, Field field, String entityAlias, String binding, String aliasBinding, FieldFilter fieldFilter, ExpansionVisitTracker visitedEntities) {
//        if (binding!=null && binding.length() == 0) {
//            binding = null;
//        }
//        ManyToOneType manyToOneType = (ManyToOneType) field.getDataType();
//        Relationship rel = manyToOneType.getRelationship();
//        Entity masterEntity = rel.getTargetRole().getEntity();
//        ExpansionVisitTracker dived = visitedEntities.dive();
//        if (dived == null || !dived.nextVisit(masterEntity.getName())) {
//            //should add only ids
//            List<Field> otherFields = manyToOneType.getRelationship().getSourceRole().getFields();
//            for (Field f : otherFields) {
//                if (!(f.getDataType() instanceof ManyToOneType)) {
//                    CompiledVar vv = new CompiledVar(entityAlias, field.getEntity());
//                    vv.setChild(new CompiledVar(f));
//                    //binding = (binding == null ? "" : (binding + ".")) + field.getName();
//                    qs.addField(vv, f.getName())
//                            .setIndex(index)
//                            .setExpanded(true)
//                            .setBinding(binding);
//                }
//            }
//            return;
//        }
//        BindingJoinInfo d = addBindingJoin(qs, field, entityAlias,
//                (StringUtils.isNullOrEmpty(binding) ? ExpressionHelper.escapeIdentifier(field.getName()) : (binding + "." + ExpressionHelper.escapeIdentifier(field.getName()))),
//                (StringUtils.isNullOrEmpty(aliasBinding) ? ExpressionHelper.escapeIdentifier(field.getName()) : (aliasBinding + "." + ExpressionHelper.escapeIdentifier(field.getName())))
//        );
//        if(expandAll) {
//            expandEntityFields(qs, true,index, masterEntity, d.alias, d.binding, aliasBinding, dived);
//        }else{
////            expandField(qs,false,);
////            CompiledVar vv = new CompiledVar(d.alias, field.getEntity());
////            vv.setChild(new CompiledVar(f));
////            //binding = (binding == null ? "" : (binding + ".")) + field.getName();
////            qs.addField(vv, f.getName())
////                    .setIndex(index)
////                    .setExpanded(true)
////                    .setBinding(binding);
//
//        }
//    }
//
//    private void expandManyToOneFieldSelectFetch(CompiledSelect qs, boolean expandAll,int index, Field field, String entityAlias, String binding, String aliasBinding, FieldFilter fieldFilter, ExpansionVisitTracker visitedEntities) {
//        if (binding!=null && binding.length() == 0) {
//            binding = null;
//        }
//        ManyToOneType manyToOneType = (ManyToOneType) field.getDataType();
//        Relationship rel = manyToOneType.getRelationship();
//        Entity masterEntity = rel.getTargetRole().getEntity();
//        ExpansionVisitTracker dived = visitedEntities.dive();
////        if (dived == null || !dived.nextVisit(masterEntity.getName())) {
////            //should add only ids
////            return;
////        }
//        List<Field> otherFields = manyToOneType.getRelationship().getSourceRole().getFields();
//        for (Field f : otherFields) {
//            if (!(f.getDataType() instanceof ManyToOneType)) {
//                CompiledVar vv = new CompiledVar(entityAlias, field.getEntity());
//                vv.setChild(new CompiledVar(f));
//                //binding = (binding == null ? "" : (binding + ".")) + field.getName();
//                qs.addField(vv, f.getName())
//                        .setIndex(index)
//                        .setBinding(binding)
//                        .setExpanded(true)
//                        .setAliasBinding(aliasBinding);
//            }
//        }
//    }

    private NamedEntity2 getUsedEntity(CompiledSelect qs, String name) {
        NamedEntity2 selectedEntry = null;
        List<NamedEntity2> usedEntities = getUsedEntities(qs);
        for (NamedEntity2 entry : usedEntities) {
            if (name.equals(entry.alias)) {
                selectedEntry = entry;
                break;
            }
        }
        if (selectedEntry == null) {
            for (NamedEntity2 entry : usedEntities) {
                if (name.equals(entry.entity.getName())) {
                    selectedEntry = entry;
                    break;
                }
            }
        }
        if (selectedEntry == null) {
            throw new IllegalArgumentException("Unknown alias " + name);
        }
        return selectedEntry;
    }

    private List<NamedEntity2> getUsedEntities(CompiledSelect qs) {
        List<NamedEntity2> found = new ArrayList<NamedEntity2>();
        if (qs.getEntity() instanceof CompiledEntityName) {
            CompiledEntityName en = (CompiledEntityName) qs.getEntity();
            Entity _entity = persistenceUnit.getEntity(en.getName());
            found.add(new NamedEntity2(qs.getEntityAlias(), _entity));
        }
        for (CompiledJoinCriteria c : qs.getJoins()) {
            CompiledNameOrSelect e = c.getEntity();
            if (e instanceof CompiledEntityName) {
                CompiledEntityName en = (CompiledEntityName) e;
                Entity _entity = persistenceUnit.getEntity(en.getName());
                found.add(new NamedEntity2(c.getEntityAlias(), _entity));
            }
        }
        return found;
    }

    private ExpressionDeclaration getDeclaration(String name, DefaultCompiledExpression expression) {
        ExpressionDeclaration declaration = expression.getDeclaration(name);
        if (declaration != null) {
            return declaration;
        }
        Map<String, String> m = config.getAliasToEntityContext();
        String t = m.get(name);
        if (t != null) {
            return new ExpressionDeclaration(name, DecObjectType.ENTITY, t, null);
        }
        return null;
    }

    private List<ExpressionDeclaration> getDeclarations(String name, DefaultCompiledExpression expression) {
        List<ExpressionDeclaration> declarations = expression.getDeclarations(name);
        if (declarations != null && declarations.size() > 0) {
            return declarations;
        }
        Map<String, String> m = config.getAliasToEntityContext();
        String t = m.get(name);
        if (t != null) {
            ExpressionDeclaration d = new ExpressionDeclaration(name, DecObjectType.ENTITY, t, null);
            declarations = new ArrayList<ExpressionDeclaration>(1);
            declarations.add(d);
            return declarations;
        }
        return PlatformUtils.emptyList();
    }

    private DataTypeTransform getValidDataType(DefaultCompiledExpression a) {
        DataTypeTransform d = a.getEffectiveDataType();
        if (d == null) {
            return null;
        }
        if (d.getTargetType().getClass().equals(SerializableType.class) && d.getTargetType().getPlatformType().equals(Object.class)) {
            return null;
        }
        return d;
    }

    private String createBindingID(CompiledVar rv) {
        StringBuilder s = new StringBuilder();
        CompiledVarOrMethod t = rv;
        while (t != null) {
            if (s.length() > 0) {
                s.insert(0, ".");
            }
            s.insert(0, ExpressionHelper.escapeIdentifier(t.getName()));
            DefaultCompiledExpression pe = t.getParentExpression();
            if (pe instanceof CompiledVarOrMethod) {
                t = (CompiledVarOrMethod) pe;
            } else {
                t = null;
            }
        }
        return s.toString();
    }

    public Object resolveReferrer(CompiledVar var) {
        Object referrer = var.getReferrer();
        if (referrer == null) {
            referrer = resolveReferrer0(var);
        }
        return referrer;
    }

    private Object resolveReferrer0(CompiledVar var) {
        Object referrer = var.getReferrer();
        if (referrer == null) {
            //String entityBaseName,
            CompiledVar p = (var.getParentExpression() instanceof CompiledVar) ? ((CompiledVar) var.getParentExpression()) : null;
            if (p == null) {
                final String thisAlias = config.getThisAlias();
                if (UQLUtils.THIS.equals(var.getName())) {
                    if (thisAlias != null) {
                        var.setName(thisAlias);
                    } else {
//                    throw new IllegalArgumentException("Incountered this alias but never declared");
                    }
                    ExpressionDeclaration declaration = getDeclaration(var.getName(), var);
                    if (declaration != null) {
                        switch (declaration.getReferrerType()) {
                            case ENTITY: {
                                var.setReferrer(persistenceUnit.getEntity((String) declaration.getReferrerName()));
                                return var.getReferrer();
                            }
                        }
                    }
                    throw new IllegalArgumentException("'this' alias is not declared");
                }
                //check if field
                List<ExpressionDeclaration> values = getDeclarations(null, var);
                if (values != null) {
                    for (ExpressionDeclaration ref : values) {
                        switch (ref.getReferrerType()) {
                            case ENTITY: {
                                Entity ee = persistenceUnit.getEntity((String) ref.getReferrerName());
                                if (ee.containsField(var.getName())) {
                                    if (var.getParentExpression() instanceof CompiledVarVal) {
                                        //need no alias
                                        var.setReferrer(ee.getField(var.getName()));
                                        return var.getReferrer();
                                    } else {
                                        if (ref.getName() == null) {
                                            var.setReferrer(ee.getField(var.getName()));
                                            return var.getReferrer();
                                        } else {
                                            var.setImplicitDeclaration(ref);
                                            var.setReferrer(ee.getField(var.getName()));
                                            return var.getReferrer();
//                                            CompiledVar v2 = new CompiledVar(var.getName());
//                                            v2.setName(ref.getValidName());
//                                            v2.setReferrer(ee);
//                                            DefaultCompiledExpression parentExpression = var.getParentExpression();
//                                            UQLCompiledUtils.replaceRef(parentExpression, var, v2);
//                                            var.setParentExpression(null);
//                                            var.setReferrer(ee.getField(var.getName()));
//                                            v2.setChild(var);
//                                            return v2.getReferrer();
                                        }
                                    }
                                }
                                break;
                            }
                        }
                    }
                }
                //check if alias
                values = getDeclarations(var.getName(), var);
                if (values != null) {
                    for (ExpressionDeclaration ref : values) {
                        switch (ref.getReferrerType()) {
                            case ENTITY: {
                                var.setReferrer(persistenceUnit.getEntity((String) ref.getReferrerName()));
                                return var.getReferrer();
                            }
                            case FIELD: {
                                Entity ee = persistenceUnit.getEntity((String) ref.getReferrerParent());
                                var.setReferrer(ee.getField(var.getName()));
                                return var.getReferrer();
                            }
                        }
                        throw new IllegalArgumentException("Problem");
                    }
                }
                //check if entity
                if (persistenceUnit.containsEntity(var.getName())) {
                    var.setReferrer(persistenceUnit.getEntity(var.getName()));
                    return var.getReferrer();
                }
                if ("*".equals(var.getName())) {
                    return var.getReferrer();
                }
                if (thisAlias != null) {
                    UQLCompiledUtils.replaceRef(var.getParentExpression(), var, new CompiledVar(thisAlias));
                    //check if field
                    List<ExpressionDeclaration> values2 = getDeclarations(thisAlias, var);
                    if (values2 != null) {
                        for (ExpressionDeclaration ref : values2) {
                            switch (ref.getReferrerType()) {
                                case ENTITY: {
                                    Entity ee = persistenceUnit.getEntity((String) ref.getReferrerName());
                                    if (ee.containsField(var.getName())) {
                                        var.setReferrer(ee.getField(var.getName()));
                                        return var.getReferrer();
                                    }
                                }
                            }
                        }
                    }
                }
                //TODO remove me
//            validateCompiledVar(v, config);
                throw new net.vpc.upa.exceptions.NoSuchFieldException(null, var.toString(), var.getName(), null);
            } else {
                String before = p.toString();
            /*p =*/
                resolveReferrer(p);
                Object ref = p.getReferrer();
                if (ref instanceof Entity) {
                    Entity ee = (Entity) ref;
                    if (ee.containsField(var.getName())) {
                        var.setReferrer(ee.getField(var.getName()));
                        return var.getReferrer();
                    } else if ("*".equals(var.getName())) {
                        return null;
                    }
                    throw new net.vpc.upa.exceptions.NoSuchFieldException(null, var.toString(), var.getName(), null);
                } else if (ref instanceof Field) {
                    DataType dataType = ((Field) ref).getDataType();
                    if (dataType instanceof ManyToOneType) {
                        ManyToOneType et = (ManyToOneType) dataType;
                        Entity ee = et.getRelationship().getTargetRole().getEntity();
                        if (ee.containsField(var.getName())) {
                            var.setReferrer(ee.getField(var.getName()));
                            return var.getReferrer();
                        } else if ("*".equals(var.getName())) {
                            return null;
                        }
                    } else {
                        log.severe("Type Cast Exception " + ((Field) ref).getAbsoluteName() + " is not of type " + ManyToOneType.class.getName() + " but " + dataType);
                        throw new net.vpc.upa.exceptions.NoSuchFieldException(null, var.toString(), var.getName(), null);
                    }
                }
                throw new net.vpc.upa.exceptions.NoSuchFieldException(null, var.toString(), var.getName(), null);
            }
//        return defaultReferrer;


        }
        return var.getReferrer();
    }

    protected Object evalQLFunction(DefaultCompiledExpression o) {
        if (o == null) {
            return null;
        }
        if (o instanceof CompiledQLFunctionExpression) {
            CompiledQLFunctionExpression s = (CompiledQLFunctionExpression) o;
            int argumentsCount = s.getArgumentsCount();
            Object[] args = new Object[argumentsCount];
            for (int i = 0; i < args.length; i++) {
                args[i] = evalQLFunction(s.getArgument(i));
            }
            return (s.getHandler().eval(new EvalContext(s.getName(), args, persistenceUnit,this)));
        }
        if (o instanceof CompiledLiteral) {
            return ((CompiledLiteral) o).getValue();
        }
        if (o instanceof CompiledParam) {
            return ((CompiledParam) o).getValue();
        }
        return o;
    }

    public ReplaceResult updateCompiledQLFunctionExpression(CompiledQLFunctionExpression o) {
        int argumentsCount = o.getArgumentsCount();
        Object[] args = new Object[argumentsCount];
        for (int i = 0; i < args.length; i++) {
            args[i] = evalQLFunction(o.getArgument(i));
        }
        Object v = o.getHandler().eval(new EvalContext(o.getName(), args, persistenceUnit,this));
        if (v != null) {
            if (v instanceof CompiledExpression) {
                return ReplaceResult.continueWithNewDirtyObj((DefaultCompiledExpression) v);
            }
            if (v instanceof Expression) {
                throw new IllegalArgumentException("Function should return literals of compiled expressions (CompiledExpression type)");
            }
            return ReplaceResult.continueWithNewCleanObj(new CompiledParam(v, null, o.getTypeTransform(), false));
        } else {
            return ReplaceResult.continueWithNewCleanObj(new CompiledLiteral(null, null));
        }
    }

//    public ReplaceResult updateIsHierarchyDescendantCompiled(IsHierarchyDescendantCompiled o) {
//        DefaultCompiledExpression c = o.getChildExpression();
//        DefaultCompiledExpression p = o.getAncestorExpression();
//        CompiledEntityName n = o.getEntityName();
//        Entity treeEntity = null;
//        if (c instanceof CompiledVar) {
//
//            Object childReferrer = resolveReferrer((CompiledVar) ((CompiledVar) c).getFinest());
//            if (childReferrer != null) {
//                if (childReferrer instanceof Entity) {
//                    if (treeEntity == null) {
//                        treeEntity = (Entity) childReferrer;
//                    } else {
//                        if (!treeEntity.getName().equals(((Entity) childReferrer).getName())) {
//                            throw new IllegalArgumentException("Ambiguous or Invalid Type " + treeEntity.getName() + " in TreeEntity near " + o);
//                        }
//                    }
//                }
//            }
//        } else if (c instanceof CompiledParam) {
//            Object co = ((CompiledParam) c).getValue();
//            if (co != null && persistenceUnit.containsEntity(co.getClass())) {
//                Entity rr = persistenceUnit.getEntity(co.getClass());
//                if (treeEntity == null) {
//                    treeEntity = rr;
//                }
//                ((CompiledParam) c).setValue(rr.getBuilder().objectToId(co));
//            }
////            Object co = ((CompiledParam) c).getEffectiveDataType();
//        }
//        if (p instanceof CompiledVar) {
//            Object parentReferrer = resolveReferrer((CompiledVar) ((CompiledVar) p).getFinest());
//            if (parentReferrer != null) {
//                if (parentReferrer instanceof Entity) {
//                    if (treeEntity == null) {
//                        treeEntity = (Entity) parentReferrer;
//                    } else {
//                        if (!treeEntity.getName().equals(((Entity) parentReferrer).getName())) {
//                            throw new IllegalArgumentException("Ambiguous or Invalid Type " + treeEntity.getName() + " in TreeEntity near " + o);
//                        }
//                    }
//                }
//            }
//        } else if (p instanceof CompiledParam) {
//            Object co = ((CompiledParam) p).getValue();
//            if (co != null && persistenceUnit.containsEntity(co.getClass())) {
//                Entity rr = persistenceUnit.findEntity(co.getClass());
//                if (treeEntity == null) {
//                    treeEntity = rr;
//                }
//                ((CompiledParam) p).setValue(rr.getBuilder().objectToId(co));
//                if (rr.getIdFields().size() > 1) {
//                    throw new IllegalArgumentException("Not supported");
//                }
//                ((CompiledParam) p).setTypeTransform(UPAUtils.getTypeTransformOrIdentity(rr.getIdFields().get(0)));
//            }
////            Object co = ((CompiledParam) c).getEffectiveDataType();
//        }
//        Entity expectedEntity = n.getName() == null ? null : persistenceUnit.getEntity(n.getName());
//        if (treeEntity == null) {
//            if (expectedEntity != null) {
//                treeEntity = expectedEntity;
//            } else {
//                throw new IllegalArgumentException("Unable to resolve Hierarchy Entity in " + o);
//            }
//        } else if (expectedEntity != null && !expectedEntity.getName().equals(treeEntity.getName())) {
//            throw new IllegalArgumentException("Expected " + expectedEntity.getName() + " but found " + treeEntity.getName() + " in " + o);
//        }
//
//        Relationship t = HierarchicalRelationshipSupport.getTreeRelation(treeEntity);
//        if (t == null) {
//            throw new IllegalArgumentException("Hierarchy Relationship not found");
//        }
//        HierarchyExtension s = t.getHierarchyExtension();
//        if (s == null) {
//            throw new IllegalArgumentException("Not a valid TreeEntity");
//        }
//        Field pathField = treeEntity.getField(s.getHierarchyPathField());
//        String pathSep = s.getHierarchyPathSeparator();
//        return ReplaceResult.continueWithNewObj(
//                createConditionForDeepSearch(c, p, true, pathField, pathSep)
//        );
//    }

//    public DefaultCompiledExpression createConditionForDeepSearch(DefaultCompiledExpression alias, DefaultCompiledExpression id, boolean includeId, Field field, String pathSep) throws UPAException {
//        alias = alias.copy();
//        if (alias instanceof CompiledVar) {
////            CompiledVar cv = (CompiledVar) alias;
//            CompiledVarOrMethod finest = ((CompiledVar) alias).getFinest();
//            Object referrer = resolveReferrer((CompiledVar) finest);
//            if (referrer instanceof Entity) {
//
//                CompiledVar v = new CompiledVar(field.getName());
//                ((CompiledVar) alias).getFinest().setChild(v);
//            } else if (referrer instanceof Field && ((Field) referrer).getDataType() instanceof ManyToOneType &&
//                    ((ManyToOneType) ((Field) referrer).getDataType()).getTargetEntity().getName().equals(field.getEntity().getName())
//                    ) {
//                CompiledVar v = new CompiledVar(field.getName());
//                finest.setChild(v);
//            } else {
//                throw new IllegalArgumentException("Expected " + field.getEntity().getName() + " var name");
//            }
//        } else {
//            throw new IllegalArgumentException("Expected " + field.getEntity().getName() + " var name");
//        }
//        id = id.copy();
//        List<Field> primaryFields = field.getEntity().getIdFields();
//        if (primaryFields.size() > 1) {
//            throw new IllegalArgumentException("Composite ID unsupported for function isHierarchyDescendant");
//        }
//        DataType pkType = primaryFields.get(0).getDataType();
//        DefaultCompiledExpression strId = null;
//        if (pkType instanceof IntType) {
//            strId = new CompiledI2V(id);
//        } else if (pkType instanceof LongType) {
//            strId = new CompiledI2V(id);
//        } else if (pkType instanceof ShortType) {
//            strId = new CompiledI2V(id);
//        } else if (pkType instanceof ByteType) {
//            strId = new CompiledI2V(id);
//        } else if (pkType instanceof FloatType) {
//            strId = new CompiledD2V(id);
//        } else if (pkType instanceof DoubleType) {
//            strId = new CompiledD2V(id);
//        } else if (pkType instanceof StringType) {
//            strId = id;
//        } else {
//            strId = new CompiledCast(id, IdentityDataTypeTransform.STRING);
//        }
//        if (includeId) {
//            return new CompiledOr(
//                    new CompiledEquals(alias.copy(), strId.copy()),
//                    new CompiledOr(
//                            new CompiledLike(
//                                    alias.copy(),
//                                    new CompiledConcat(new CompiledLiteral("%" + pathSep), strId.copy())),
//                            new CompiledOr(
//                                    new CompiledLike(
//                                            alias.copy(),
//                                            new CompiledConcat(new CompiledLiteral("%" + pathSep), strId.copy(), new CompiledLiteral(pathSep + "%")))
//                                    ,
//                                    new CompiledLike(
//                                            alias.copy(),
//                                            new CompiledConcat(strId.copy(), new CompiledLiteral(pathSep + "%")))
//                            )
//                    )
//            );
//        } else {
//            return new CompiledLike(
//                    alias.copy(),
//                    new CompiledConcat(new CompiledLiteral("%" + pathSep), strId.copy(), new CompiledLiteral(pathSep + "%")));
//        }
//    }

    private void expandEntityFilters(CompiledSelect compiledSelect) {
        CompiledNameOrSelect nameOrSelect = compiledSelect.getEntity();
        if (nameOrSelect instanceof CompiledEntityName) {
            String entityName = ((CompiledEntityName) nameOrSelect).getName();
            final Entity entity = persistenceUnit.getEntity(entityName);
            for (String filterName : entity.getFilterNames()) {
                Expression filter = entity.getFilter(filterName);
                ExpressionCompilerConfig conf2 = new ExpressionCompilerConfig();
                String name = compiledSelect.getEntityAlias();
                if (name == null) {
                    name = entityName;
                }
                conf2.setTranslateOnly();
                conf2.bindAliasToEntity(name, entityName);
                conf2.setThisAlias(name);
                DefaultCompiledExpression compiledFilter = (DefaultCompiledExpression) expressionManager.compileExpression(filter, conf2);
                if (compiledFilter != null) {
                    compiledFilter.getClientParameters().setString("UPA.EntityFilter", entity.getName() + ":" + filterName);
                }
                compiledSelect.addWhere(compiledFilter);
            }
            Expression securityFilter = persistenceUnit.getSecurityManager().getEntityFilter(entity);
            if (securityFilter != null) {
                ExpressionCompilerConfig conf2 = new ExpressionCompilerConfig();
                String name = compiledSelect.getEntityAlias();
                if (name == null) {
                    name = entityName;
                }
                conf2.bindAliasToEntity(name, entityName);
                conf2.setTranslateOnly();
                conf2.setThisAlias(name);
                DefaultCompiledExpression compiledFilter = (DefaultCompiledExpression) expressionManager.compileExpression(securityFilter, conf2);
                compiledFilter = UQLCompiledUtils.replaceThisVar(compiledFilter, name).getExpression(compiledFilter);

                if (compiledFilter != null) {
                    compiledFilter.getClientParameters().setString("UPA.EntityFilter", entity.getName() + ":(SecurityManager)");
                }

                compiledSelect.addWhere(compiledFilter);
            }
        }
        for (CompiledJoinCriteria join : compiledSelect.getJoins()) {
            switch (join.getJoinType()) {
                case LEFT_JOIN:
                case RIGHT_JOIN:
                case FULL_JOIN:
                case INNER_JOIN: {
                    nameOrSelect = join.getEntity();
                    String entityName = ((CompiledEntityName) nameOrSelect).getName();
                    final Entity entity = persistenceUnit.getEntity(entityName);
                    for (String filterName : entity.getFilterNames()) {
                        Expression filter = entity.getFilter(filterName);
                        ExpressionCompilerConfig conf2 = new ExpressionCompilerConfig();
                        String name = join.getEntityAlias();
                        if (name == null) {
                            name = entityName;
                        }
                        conf2.bindAliasToEntity(name, entityName);
                        conf2.setTranslateOnly();
                        conf2.setThisAlias(name);
                        DefaultCompiledExpression compiledFilter = (DefaultCompiledExpression) expressionManager.compileExpression(filter, conf2);
                        if (compiledFilter != null) {
                            compiledFilter.getClientParameters().setString("UPA.EntityFilter", entity.getName() + ":" + filterName);
                        }
                        join.addCondition(compiledFilter);
                    }
                    break;
                }
                case CROSS_JOIN: {
                    nameOrSelect = join.getEntity();
                    String entityName = ((CompiledEntityName) nameOrSelect).getName();
                    final Entity entity = persistenceUnit.getEntity(entityName);
                    for (String filterName : entity.getFilterNames()) {
                        Expression filter = entity.getFilter(filterName);
                        ExpressionCompilerConfig conf2 = new ExpressionCompilerConfig();
                        String name = join.getEntityAlias();
                        if (name == null) {
                            name = entityName;
                        }
                        conf2.bindAliasToEntity(name, entityName);
                        conf2.setTranslateOnly();
                        conf2.setThisAlias(name);
                        DefaultCompiledExpression compiledFilter = (DefaultCompiledExpression) expressionManager.compileExpression(filter, conf2);
                        if (compiledFilter != null) {
                            compiledFilter.getClientParameters().setString("UPA.EntityFilter", entity.getName() + ":" + filterName);
                        }
                        compiledSelect.addWhere(compiledFilter);
                    }
                    break;
                }
            }
        }
    }


    private static class ExprContext {
        //        private ExpansionVisitTracker visitedEntities;
//        private ExpressionCompilerConfig config;
//        private List<CompiledJoinCriteria> joins=new ArrayList<>();
        private Map<String, String> upaBindingAliases = new HashMap<>();
        private int upaBindingAliasIndex = 0;

        public static ExprContext get(CompiledEntityStatement s) {
            ExprContext c = s.getClientParameters().getObject("ExprContext");
            if (c == null) {
                c = new ExprContext();
                s.getClientParameters().setObject("ExprContext", c);
            }
            return c;
        }


        private String getAliasFor(BindingId binding) {
            String sbinding = (binding == null ? "" : binding.toString());
            return upaBindingAliases.get(sbinding.toLowerCase());
        }

        private String createAliasFor(BindingId binding) {
            upaBindingAliasIndex++;
            String generatedAlias = "upa" + upaBindingAliasIndex;
            String sbinding = (binding == null ? "" : binding.toString());
            upaBindingAliases.put(sbinding, generatedAlias);
            return generatedAlias;
        }


    }

}
