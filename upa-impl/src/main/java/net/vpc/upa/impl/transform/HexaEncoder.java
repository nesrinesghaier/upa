/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.upa.impl.transform;

import net.vpc.upa.impl.util.StringUtils;
import net.vpc.upa.types.StringEncoder;

/**
 *
 * @author taha.bensalah@gmail.com
 */
public class HexaEncoder implements StringEncoder {

    public static final HexaEncoder INSTANCE = new HexaEncoder();

    public String encode(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        return StringUtils.toHexString(bytes);
    }

    public byte[] decode(String value) {
        if (value == null) {
            return null;
        }
        return StringUtils.parseHexString(value);
    }

    @Override
    public String toString() {
        return "HexadecimalStringEncoder";
    }
    
}
