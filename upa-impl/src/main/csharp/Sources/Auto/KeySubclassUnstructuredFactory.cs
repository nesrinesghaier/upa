/*********************************************************
 *********************************************************
 **   DO NOT EDIT                                       **
 **                                                     **
 **   THIS FILE AS BEEN GENERATED AUTOMATICALLY         **
 **   BY UPA PORTABLE GENERATOR                         **
 **   (c) vpc                                           **
 **                                                     **
 *********************************************************
 ********************************************************/



namespace Net.Vpc.Upa.Impl
{


    /**
     * @author Taha BEN SALAH <taha.bensalah@gmail.com>
     * @creationdate 8/27/12 1:51 AM
     */
    public class KeySubclassUnstructuredFactory : Net.Vpc.Upa.Impl.KeyFactory {

        private System.Type keyType;

        private Net.Vpc.Upa.Impl.Util.EntityBeanAdapter nfo;

        public KeySubclassUnstructuredFactory(System.Type keyType, Net.Vpc.Upa.Impl.Util.EntityBeanAdapter nfo) {
            this.keyType = keyType;
            this.nfo = nfo;
        }

        public virtual System.Type GetIdType() {
            return keyType;
        }


        public virtual Net.Vpc.Upa.Key CreateKey(params object [] keyValues) {
            Net.Vpc.Upa.Key o = (Net.Vpc.Upa.Key) nfo.NewInstance();
            o.SetValue(keyValues);
            return o;
        }


        public virtual object CreateId(params object [] keyValues) {
            Net.Vpc.Upa.Key o = (Net.Vpc.Upa.Key) nfo.NewInstance();
            o.SetValue(keyValues);
            return o;
        }


        public virtual object GetId(Net.Vpc.Upa.Key unstructuredKey) {
            if (keyType.IsInstanceOfType(unstructuredKey)) {
                return unstructuredKey;
            }
            Net.Vpc.Upa.Key o = (Net.Vpc.Upa.Key) nfo.NewInstance();
            o.SetValue(unstructuredKey.GetValue());
            return o;
        }


        public virtual Net.Vpc.Upa.Key GetKey(object key) {
            return (Net.Vpc.Upa.Key) key;
        }
    }
}
