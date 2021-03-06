/*********************************************************
 *********************************************************
 **   DO NOT EDIT                                       **
 **                                                     **
 **   THIS FILE AS BEEN GENERATED AUTOMATICALLY         **
 **   BY UPA PORTABLE GENERATOR                         **
 **   (c) vpc                                           **
 **                                                     **
 *********************************************************
 ********************************************************/



namespace Net.Vpc.Upa.Callbacks
{


    /**
     *
     * @author taha.bensalah@gmail.com
     */
    public class UpdateFormulaEvent : Net.Vpc.Upa.Callbacks.EntityEvent {

        private Net.Vpc.Upa.Record updates;

        private Net.Vpc.Upa.Expressions.Expression filterExpression;

        public UpdateFormulaEvent(Net.Vpc.Upa.Record updates, Net.Vpc.Upa.Expressions.Expression filterExpression, Net.Vpc.Upa.Persistence.EntityExecutionContext entityExecutionContext, Net.Vpc.Upa.EventPhase phase)  : base(entityExecutionContext, phase){

            this.updates = updates;
            this.filterExpression = filterExpression;
        }

        public virtual Net.Vpc.Upa.Record GetUpdates() {
            return updates;
        }

        public virtual Net.Vpc.Upa.Expressions.Expression GetFilterExpression() {
            return filterExpression;
        }
    }
}
