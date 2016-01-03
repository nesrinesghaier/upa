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
     * @creationdate 12/20/12 2:47 AM
     */
    public class IntegerMarshaller : Net.Vpc.Upa.Impl.Persistence.SimpleTypeMarshaller {

        public override object Read(int index, System.Data.IDataReader resultSet) /* throws System.Exception */  {
            int n = System.Convert.ToInt32(resultSet[index]);
            if (n == 0 && resultSet.WasNull()) {
                return null;
            }
            return new int?(n);
        }


        public override void Write(object @object, int i, System.Data.IDataReader updatableResultSet) /* throws System.Exception */  {
        }

        public override string ToSQLLiteral(object @object) {
            if (@object == null) {
                return base.ToSQLLiteral(@object);
            }
            return System.Convert.ToString(@object);
        }

        public override void Write(object @object, int i, System.Data.IDbCommand preparedStatement) /* throws System.Exception */  {
            if (@object == null) {
                preparedStatement.SetNull(i, Java.Sql.Types.INTEGER);
            } else {
                try {
                    ( System.Data.IDbDataParameter)(preparedStatement).Parameters[i].Value=System.Convert.ToInt32((((object) @object)));
                } catch (System.InvalidCastException e) {
                    throw e;
                }
            }
        }

        public IntegerMarshaller() {
        }
    }
}
