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



namespace Net.Vpc.Upa.Impl.Uql.Compiler
{


    /**
     *
     * @author Taha BEN SALAH <taha.bensalah@gmail.com>
     */
    public class DatePartExpressionTranslator : Net.Vpc.Upa.Impl.Uql.ExpressionTranslator {

        private readonly Net.Vpc.Upa.Impl.Uql.ExpressionTranslationManager outer;

        public DatePartExpressionTranslator(Net.Vpc.Upa.Impl.Uql.ExpressionTranslationManager outer) {
            this.outer = outer;
        }

        public virtual Net.Vpc.Upa.Impl.Uql.Compiledexpression.DefaultCompiledExpression TranslateExpression(object o, Net.Vpc.Upa.Impl.Uql.ExpressionTranslationManager expressionTranslationManager, Net.Vpc.Upa.Impl.Uql.ExpressionDeclarationList declarations) {
            return CompileDatePart((Net.Vpc.Upa.Expressions.DatePart) o, declarations);
        }

        protected internal virtual Net.Vpc.Upa.Impl.Uql.Compiledexpression.CompiledDatePart CompileDatePart(Net.Vpc.Upa.Expressions.DatePart v, Net.Vpc.Upa.Impl.Uql.ExpressionDeclarationList declarations) {
            if (v == null) {
                return null;
            }
            Net.Vpc.Upa.Impl.Uql.Compiledexpression.CompiledDatePart s = new Net.Vpc.Upa.Impl.Uql.Compiledexpression.CompiledDatePart(v.GetDatePartType(), outer.CompileAny(v.GetValue(), declarations));
            //        s.setDeclarationList(declarations);
            return s;
        }
    }
}
