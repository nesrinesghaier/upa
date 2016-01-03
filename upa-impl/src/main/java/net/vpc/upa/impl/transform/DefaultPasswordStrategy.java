/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.upa.impl.transform;

import java.security.MessageDigest;
import net.vpc.upa.PasswordStrategy;
import net.vpc.upa.impl.util.Strings;

/**
 *
 * @author Taha BEN SALAH <taha.bensalah@gmail.com>
 */
public class DefaultPasswordStrategy implements PasswordStrategy {

    private String digest;
    private boolean fixedSize;
    private int maxSize;
    public static final DefaultPasswordStrategy MD5 = new DefaultPasswordStrategy("MD5", true, 32);
    public static final DefaultPasswordStrategy SHA1 = new DefaultPasswordStrategy("SHA1", true, 20);
    public static final DefaultPasswordStrategy SHA256 = new DefaultPasswordStrategy("SHA-256", true, 64);

    public DefaultPasswordStrategy(String digest, boolean fixedSize, int maxSize) {
        this.digest = digest;
        this.fixedSize = fixedSize;
        this.maxSize = maxSize;
    }

    public String encode(String value) {
        try {
            if (value == null) {
                return null;
            }
            // first of all convert this object to String
            byte[] bytesOfMessage = value.getBytes("UTF-8");
            byte[] hash =null;
            /**@PortabilityHint(target = "C#", name = "suppress")*/
            MessageDigest md = MessageDigest.getInstance(digest);
            /**@PortabilityHint(target = "C#", name = "suppress")*/
            hash = md.digest(bytesOfMessage);
            /**@PortabilityHint(target = "C#", name = "suppress")*/
            return Strings.toHexString(hash);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public String toString() {
        return String.valueOf(digest);
    }

    public boolean isFixedSize() {
        return fixedSize;
    }

    public int getMaxSize() {
        return maxSize;
    }
}
