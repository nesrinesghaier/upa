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



namespace Net.Vpc.Upa.Impl.Persistence.Specific.Mssqlserver
{


    /**
    * @author Taha BEN SALAH <taha.bensalah@gmail.com>
    * @creationdate 11/22/12 10:02 PM*/
    internal class MSSQLServerDateTimeMarshaller : Net.Vpc.Upa.Impl.Persistence.SimpleTypeMarshaller {

        public override object Read(int index, System.Data.IDataReader resultSet) /* throws System.Exception */  {
            //            Timestamp ts=resultSet.getDate(index);
            Net.Vpc.Upa.Types.Timestamp ts = resultSet.GetTimestamp(index);
            return ts == null ? null : new Net.Vpc.Upa.Types.DateTime(ts);
        }


        public override string ToSQLLiteral(object @object) {
            if (@object == null) {
                return base.ToSQLLiteral(@object);
            }
            //return "'" + Utils.UNIVERSAL_DATE_TIME_FORMAT.format((java.util.Date)object) + "'";
            return "'" + Net.Vpc.Upa.Impl.Util.DateUtils.FormatUniversalDateTime(Net.Vpc.Upa.Impl.Persistence.Specific.Mssqlserver.MSSQLServerPersistenceStore.ToTimestamp(@object)) + "'";
        }

        public override void Write(object @object, int i, System.Data.IDbCommand preparedStatement) /* throws System.Exception */  {
            if (@object == null) {
                preparedStatement.SetNull(i, Java.Sql.Types.DATE);
            } else {
                ( System.Data.IDbDataParameter)(preparedStatement).Parameters[i].Value=Net.Vpc.Upa.Impl.Persistence.Specific.Mssqlserver.MSSQLServerPersistenceStore.ToTimestamp(@object);
            }
        }

        public override void Write(object @object, int i, System.Data.IDataReader updatableResultSet) /* throws System.Exception */  {
        }

        public MSSQLServerDateTimeMarshaller() {
        }
    }
}
