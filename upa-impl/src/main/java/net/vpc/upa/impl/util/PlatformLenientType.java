package net.vpc.upa.impl.util;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by vpc on 5/17/16.
 */
public class PlatformLenientType implements LenientType{
    private String typeName;
    private Class type;
    private static final Logger log=Logger.getLogger(PlatformLenientType.class.getName());

    public PlatformLenientType(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeName() {
        return typeName;
    }

    public boolean isValid(){
        return getValidType()!=null;
    }

    public Class getValidTypeOrError(){
        Class c=getValidType();
        if(c==null){
            throw new RuntimeException("Invalid Type");
        }
        return c;
    }

    public Class getValidType(){
        if(type==null){
            try {
                type=Class.forName(typeName);
                log.log(Level.INFO, "Lenient Type " + typeName + " loaded successfully");
            } catch (ClassNotFoundException e) {
//                e.printStackTrace();
                type=Void.TYPE;
                log.log(Level.SEVERE, "Lenient Type " + typeName + " was unable to load : " + e);
            }
        }
        if(Void.TYPE.equals(type)){
            return null;
        }
        return type;
    }

    public Object newInstance(){
        try {
            return getValidTypeOrError().newInstance();
        } catch (Exception e) {
            throw PlatformUtils.createRuntimeException(e);
        }
    }

    public void setPropertyAsString(Object instance,String propertyName,String value){
        getValidTypeOrError();
        PlatformBeanProperty a = PlatformUtils.findPlatformBeanProperty(propertyName, instance.getClass());
        if(a!=null) {
            if (value == null) {
                a.setValue(instance, null);
            }else {
                Class s = a.getPlatformType();
                Object ovalue = UPAUtils.createValue(value, s, null);
                a.setValue(instance,ovalue);
            }
        }
    }
    public void setProperty(Object instance,String propertyName,Object value){
        getValidTypeOrError();
        PlatformBeanProperty a = PlatformUtils.findPlatformBeanProperty(propertyName, instance.getClass());
        if(a!=null) {
            if (value == null) {
                a.setValue(instance, null);
            }else {
                a.setValue(instance,value);
            }
        }
    }

    public Object invokeInstance(Object instance,String method,Class[] types,Object[] args){
        try {
            return getValidTypeOrError().getMethod(method, types).invoke(instance, args);
        } catch (Exception e) {
            throw PlatformUtils.createRuntimeException(e);
        }
    }

    public Object invokeStatic(String method,Class[] types,Object[] args){
        try {
            return getValidTypeOrError().getMethod(method,types).invoke(null,args);
        } catch (Exception e) {
            throw PlatformUtils.createRuntimeException(e);
        }
    }
}
