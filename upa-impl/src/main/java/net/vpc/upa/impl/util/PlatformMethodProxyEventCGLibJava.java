package net.vpc.upa.impl.util;

import net.sf.cglib.proxy.MethodProxy;
import net.vpc.upa.PortabilityHint;

import java.lang.reflect.Method;

/**
 * Created by vpc on 8/6/15.
 */
@PortabilityHint(target = "C#", name = "suppress")
public class PlatformMethodProxyEventCGLibJava<T> implements PlatformMethodProxyEvent<T> {
    private T object;
    private Object[] arguments;
    private Method method;
    net.sf.cglib.proxy.MethodProxy methodProxy;

    public PlatformMethodProxyEventCGLibJava(T object, Object[] arguments, Method method, MethodProxy methodProxy) {
        this.object = object;
        this.arguments = arguments;
        this.method = method;
        this.methodProxy = methodProxy;
    }

    @Override
    public String getMethodName() {
        return method.getName();
    }

    @Override
    public Method getMethod() {
        return method;
    }

    @Override
    public T getObject() {
        return object;
    }

    @Override
    public Object[] getArguments() {
        return arguments;
    }

    @Override
    public Object invokeBase(T obj, Object[] args) throws Throwable {
        return methodProxy.invoke(obj,args);
    }
}
