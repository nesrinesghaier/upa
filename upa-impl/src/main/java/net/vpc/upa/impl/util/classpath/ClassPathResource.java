/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.upa.impl.util.classpath;

import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author vpc
 */
public interface ClassPathResource {
    public String getPath();
    InputStream open() throws IOException;
}
