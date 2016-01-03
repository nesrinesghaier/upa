/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.upa.impl.event;

import net.vpc.upa.callbacks.UpdateFormulaInterceptor;
import net.vpc.upa.callbacks.UpdateRelationshipSourceFormulaInterceptor;
import net.vpc.upa.exceptions.UPAException;
import net.vpc.upa.expressions.Expression;
import net.vpc.upa.filters.FieldFilter;

/**
 *
 * @author vpc
 */
class RelationshipSourceFormulaUpdaterInterceptorSupportBaseInterceptor implements UpdateFormulaInterceptor {
    private final UpdateRelationshipSourceFormulaInterceptor entityDetailFormulaUpdaterInterceptor;

    public RelationshipSourceFormulaUpdaterInterceptorSupportBaseInterceptor(UpdateRelationshipSourceFormulaInterceptor entityDetailFormulaUpdaterInterceptor) {
        this.entityDetailFormulaUpdaterInterceptor = entityDetailFormulaUpdaterInterceptor;
    }

    @Override
    public FieldFilter getFormulaFields() throws UPAException {
        return entityDetailFormulaUpdaterInterceptor.getFormulaFields();
    }

    @Override
    public Expression translateExpression(Expression e) throws UPAException {
        return entityDetailFormulaUpdaterInterceptor.translateExpression(e);
    }
    
}
