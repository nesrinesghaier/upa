//package net.vpc.upa.impl.persistence;
//
//
//import net.vpc.upa.impl.persistence.NativeSQL;
//import net.vpc.upa.persistence.PersistenceUnitManager;
//
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.sql.Statement;
//
//// Referenced classes of package net.vpc.lib.pheromone.ariana.database.jdbc:
////            ResultSetHandler, NativeExecutionContext, NativeSQL, StatementDelegate,
////            DatabaseAdapter
//
//public class ResultSetDelegate extends ResultSetHandler {
//
//    public ResultSetDelegate(ResultSet rs, StatementDelegate statement, NativeSQL execContext) {
//        super(rs);
//        this.statement = statement;
//        this.execContext = execContext;
//    }
//
//    public NativeSQL getContext() {
//        return execContext;
//    }
//
////    public void close()
////            throws SQLException {
////        super.close();
////        if (execContext != null) {
////            NativeSQL c = execContext;
////            if (c != null) {
////                c.dispose();
////            }
////        }
////    }
//
////    public PersistenceUnitManager getPersistenceManager() {
////        return statement.getPersistenceManager();
////    }
//
//    @Override
//    public Statement getStatement()
//            throws SQLException {
//        return statement;
//    }
//
//    private Statement statement;
////    private NativeSQL execContext;
//}
