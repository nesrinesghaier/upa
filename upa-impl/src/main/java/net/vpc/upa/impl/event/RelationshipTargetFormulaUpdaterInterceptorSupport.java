/*
 * Created by IntelliJ IDEA.
 * User: taha
 * Date: 8 nov. 02
 * Time: 21:57:02
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package net.vpc.upa.impl.event;


import net.vpc.upa.Entity;
import net.vpc.upa.Relationship;
import net.vpc.upa.callbacks.EntityTriggerContext;
import net.vpc.upa.callbacks.UpdateRelationshipTargetFormulaInterceptor;
import net.vpc.upa.callbacks.UpdateEvent;
import net.vpc.upa.exceptions.UPAException;
import net.vpc.upa.expressions.Expression;
import net.vpc.upa.filters.FieldFilter;
import net.vpc.upa.filters.FieldFilters;

public class RelationshipTargetFormulaUpdaterInterceptorSupport extends FormulaUpdaterInterceptorSupport {
    private UpdateRelationshipTargetFormulaInterceptor entityTargetFormulaUpdaterInterceptor;
    private Relationship relation;
    private FieldFilter relationFilter;
//    private String[] conditions;

    public RelationshipTargetFormulaUpdaterInterceptorSupport(final UpdateRelationshipTargetFormulaInterceptor entityTargetFormulaUpdaterInterceptor) {
        super(new RelationshipTargetFormulaUpdaterInterceptorSupportBaseInterceptor(entityTargetFormulaUpdaterInterceptor));
        this.entityTargetFormulaUpdaterInterceptor = entityTargetFormulaUpdaterInterceptor;
    }

    public Relationship getRelationship(EntityTriggerContext context)throws UPAException{
        if(relation==null){
            context.getEntity().getPersistenceUnit().getRelationship(entityTargetFormulaUpdaterInterceptor.getRelationshipName());
        }
      return relation;
    }

    @Override
    public boolean acceptUpdateTableHelper(UpdateEvent event) throws UPAException {
        FieldFilter conditionFields = entityTargetFormulaUpdaterInterceptor.getConditionFields();
        if (conditionFields == null) {
            return true;
        }
        if(relationFilter==null){
            relationFilter= FieldFilters.regular().and(FieldFilters.byList(relation.getSourceRole().getFields()));
        }
        FieldFilter actualFilter= FieldFilters.as(conditionFields).or(relationFilter);
        Entity entityManager = event.getEntity();
        for (String updatedField : event.getUpdatesDocument().keySet()) {
            if (actualFilter.accept(entityManager.getField(updatedField))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean acceptUpdateTableOlderValuesHelper(UpdateEvent event) throws UPAException {
        if(relationFilter==null){
            relationFilter= FieldFilters.regular().and(FieldFilters.byList(relation.getSourceRole().getFields()));
        }
        Entity entityManager = event.getEntity();
        for (String updatedField : event.getUpdatesDocument().keySet()) {
            if (relationFilter.accept(entityManager.getField(updatedField))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Expression translateExpression(Expression e) throws UPAException{
        return relation.getTargetCondition(e, relation.getSourceRole().getEntity().getName(), relation.getTargetRole().getEntity().getName());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + entityTargetFormulaUpdaterInterceptor + ")";
    }

    
}
