package net.vpc.upa.impl.util;

/**
 * Created by vpc on 8/6/15.
 */
public interface PlatformMethodProxy<T> {
    public Object intercept(PlatformMethodProxyEvent<T> event) throws Throwable ;
}
