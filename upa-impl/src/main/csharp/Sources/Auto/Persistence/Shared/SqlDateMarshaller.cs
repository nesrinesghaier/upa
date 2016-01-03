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



namespace Net.Vpc.Upa.Impl.Persistence.Shared
{


    /**
    * @author Taha BEN SALAH <taha.bensalah@gmail.com>
    * @creationdate 12/20/12 2:48 AM*/
    public class SqlDateMarshaller : Net.Vpc.Upa.Impl.Persistence.SimpleTypeMarshaller {

        public override object Read(int index, System.Data.IDataReader resultSet) /* throws System.Exception */  {
            Net.Vpc.Upa.Types.Date t = System.Convert.ToDateTime(resultSet[index]);
            return t;
        }

        public override string ToSQLLiteral(object @object) {
            if (@object == null) {
                return base.ToSQLLiteral(@object);
            }
            return "{d '" + Net.Vpc.Upa.Impl.Util.DateUtils.FormatUniversalDate((Net.Vpc.Upa.Types.Temporal) @object) + "'}";
        }


        public override void Write(object @object, int i, System.Data.IDataReader updatableResultSet) /* throws System.Exception */  {
        }

        public override void Write(object @object, int i, System.Data.IDbCommand preparedStatement) /* throws System.Exception */  {
            if (@object == null) {
                preparedStatement.SetNull(i, Java.Sql.Types.DATE);
            } else {
                //                preparedStatement.setDate(i,
                //                        (object instanceof java.sql.Date) ?
                //                            ((java.sql.Date)object) :
                //                            (new java.sql.Date(((java.util.Date)object).getTime()))
                //                );
                ( System.Data.IDbDataParameter)(preparedStatement).Parameters[i].Value=(@object is Net.Vpc.Upa.Types.Date) ? ((Net.Vpc.Upa.Types.Date) @object) : (new Net.Vpc.Upa.Types.Date(((Net.Vpc.Upa.Types.Temporal) @object).GetTime()));
            }
        }

        public SqlDateMarshaller() {
        }
    }
}
