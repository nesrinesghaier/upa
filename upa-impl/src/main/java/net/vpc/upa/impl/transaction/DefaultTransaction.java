package net.vpc.upa.impl.transaction;


import net.vpc.upa.persistence.UConnection;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Taha BEN SALAH <taha.bensalah@gmail.com>
 * @creationdate 9/16/12 4:59 PM
 */
public class DefaultTransaction extends AbstractTransaction {
    protected Logger log = Logger.getLogger(DefaultTransaction.class.getName());
    private UConnection connection;
    private String transactionId;
    public DefaultTransaction() {
        transactionId = Integer.toHexString(System.identityHashCode(this)).toUpperCase();
    }

    public void init(UConnection connection) {
        this.connection = connection;
    }

    @Override
    public void commitImpl() throws SQLException {
        if (log.isLoggable(Level.FINE)) {
            log.log(Level.FINE, "Commit Transaction {0}", transactionId);
        }
        /**
         *  @PortabilityHint(target = "C#", name = "todo")
         */
        connection.getPlatformConnection().commit();
    }

    @Override
    protected void beginImpl() throws Exception {
        if (log.isLoggable(Level.FINE)) {
            log.log(Level.FINE, "Begin  Transaction {0}", transactionId);
        }
    }

    @Override
    public void rollbackImpl() throws Exception {
        if (log.isLoggable(Level.FINE)) {
            log.log(Level.FINE, "Rollback Transaction {0}", transactionId);
        }
        /**
         *  @PortabilityHint(target = "C#", name = "todo")
         */
        connection.getPlatformConnection().rollback();
    }
}
