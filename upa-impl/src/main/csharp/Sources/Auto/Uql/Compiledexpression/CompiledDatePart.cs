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



namespace Net.Vpc.Upa.Impl.Uql.Compiledexpression
{


    /**
     * Created by IntelliJ IDEA.
     * User: root
     * Date: 22 mai 2003
     * Time: 12:07:34
     * To change this template use Options | File Templates.
     */
    public class CompiledDatePart : Net.Vpc.Upa.Impl.Uql.Compiledexpression.CompiledFunction {



        public CompiledDatePart(Net.Vpc.Upa.Expressions.DatePartType type, Net.Vpc.Upa.Types.Temporal date)  : this(type, new Net.Vpc.Upa.Impl.Uql.Compiledexpression.CompiledLiteral(date)){

        }

        public CompiledDatePart(Net.Vpc.Upa.Expressions.DatePartType type, string varDate)  : this(type, new Net.Vpc.Upa.Impl.Uql.Compiledexpression.UserCompiledExpression(varDate, Net.Vpc.Upa.Impl.Transform.IdentityDataTypeTransform.DATETIME)){

        }

        public CompiledDatePart(Net.Vpc.Upa.Expressions.DatePartType type, Net.Vpc.Upa.Impl.Uql.Compiledexpression.DefaultCompiledExpression @value)  : base("Datepart"){

            ProtectedAddArgument(new Net.Vpc.Upa.Impl.Uql.Compiledexpression.CompiledCst(type));
            ProtectedAddArgument(@value);
        }

        public virtual Net.Vpc.Upa.Impl.Uql.Compiledexpression.CompiledCst GetDatePartTypeExpression() {
            return (Net.Vpc.Upa.Impl.Uql.Compiledexpression.CompiledCst) GetArgument(0);
        }

        public virtual Net.Vpc.Upa.Expressions.DatePartType GetDatePartType() {
            return (Net.Vpc.Upa.Expressions.DatePartType) GetDatePartTypeExpression().GetValue();
        }

        public virtual Net.Vpc.Upa.Impl.Uql.Compiledexpression.DefaultCompiledExpression GetValue() {
            return GetArgument(1);
        }


        public override Net.Vpc.Upa.Types.DataTypeTransform GetTypeTransform() {
            return Net.Vpc.Upa.Impl.Transform.IdentityDataTypeTransform.STRING;
        }


        public override Net.Vpc.Upa.Impl.Uql.Compiledexpression.DefaultCompiledExpression Copy() {
            Net.Vpc.Upa.Impl.Uql.Compiledexpression.CompiledDatePart o = new Net.Vpc.Upa.Impl.Uql.Compiledexpression.CompiledDatePart(GetDatePartType(), GetValue().Copy());
            o.SetDescription(GetDescription());
            o.GetClientParameters().SetAll(GetClientParameters());
            return o;
        }
    }
}
