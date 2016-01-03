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



namespace Net.Vpc.Upa
{


    /**
     * @author Taha BEN SALAH <taha.bensalah@gmail.com>
     * @creationdate 11/9/12 9:59 AM
     */
    public interface QueryBuilder : Net.Vpc.Upa.Query {

         Net.Vpc.Upa.Entity GetEntityType();

         Net.Vpc.Upa.QueryBuilder AddAndField(string field, object @value);

         Net.Vpc.Upa.QueryBuilder AddAndExpression(string expression);

         Net.Vpc.Upa.QueryBuilder SetExpression(string expression);

         Net.Vpc.Upa.QueryBuilder AddAndExpression(Net.Vpc.Upa.Expressions.Expression expression);

         Net.Vpc.Upa.QueryBuilder SetExpression(Net.Vpc.Upa.Expressions.Expression expression);

         Net.Vpc.Upa.QueryBuilder SetOrder(Net.Vpc.Upa.Expressions.Order order);

         Net.Vpc.Upa.QueryBuilder SetFieldFilter(Net.Vpc.Upa.Filters.FieldFilter fieldFilter);

         Net.Vpc.Upa.QueryBuilder SetId(object id);

         Net.Vpc.Upa.QueryBuilder SetKey(Net.Vpc.Upa.Key key);

         Net.Vpc.Upa.QueryBuilder SetPrototype(object prototype);

         Net.Vpc.Upa.QueryBuilder SetRecordPrototype(Net.Vpc.Upa.Record prototype);

         Net.Vpc.Upa.Expressions.Expression GetExpression();

         Net.Vpc.Upa.Expressions.Order GetOrder();

         Net.Vpc.Upa.Filters.FieldFilter GetFieldFilter();

         object GetId();

         Net.Vpc.Upa.Key GetKey();

         object GetPrototype();

         Net.Vpc.Upa.Record GetRecordPrototype();

         string GetEntityAlias();

         Net.Vpc.Upa.QueryBuilder SetEntityAlias(string entityAlias);
    }
}
