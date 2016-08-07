/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.upa.impl.transform;

import net.vpc.upa.types.ByteArrayEncoder;

/**
 *
 * @author taha.bensalah@gmail.com
 */
public class ChainByteArrayEncoder implements ByteArrayEncoder {

    private ByteArrayEncoder a;
    private ByteArrayEncoder b;

    public ChainByteArrayEncoder(ByteArrayEncoder a, ByteArrayEncoder b) {
        this.a = a;
        this.b = b;
    }

    public byte[] encode(Object bytes) {
        return b.encode(a.encode(bytes));
    }

    public Object decode(byte[] value) {
        return a.decode((byte[]) b.decode(value));
    }

}
