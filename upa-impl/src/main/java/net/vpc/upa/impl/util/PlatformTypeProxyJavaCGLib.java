package net.vpc.upa.impl.util;

import net.vpc.upa.PortabilityHint;

import java.lang.reflect.Method;

/**
 * Created by vpc on 8/6/15.
 */
@PortabilityHint(target = "C#", name = "suppress")
public class PlatformTypeProxyJavaCGLib implements PlatformTypeProxy {
    @Override
    public <T> T create(Class<T> type, final PlatformMethodProxy pmethodProxy) {
        return (T) net.sf.cglib.proxy.Enhancer.create(
                type, null,
                new ProxyMethodInterceptor(pmethodProxy));
    }

    private static class ProxyMethodInterceptor implements net.sf.cglib.proxy.MethodInterceptor {
        private final PlatformMethodProxy pmethodProxy;

        public ProxyMethodInterceptor(PlatformMethodProxy pmethodProxy) {
            this.pmethodProxy = pmethodProxy;
        }

        @Override
        public Object intercept(Object object, Method method, Object[] args, net.sf.cglib.proxy.MethodProxy methodProxy) throws Throwable {
            return pmethodProxy.intercept(new PlatformMethodProxyEventCGLibJava(object, args, method, methodProxy));
        }
    }
}
