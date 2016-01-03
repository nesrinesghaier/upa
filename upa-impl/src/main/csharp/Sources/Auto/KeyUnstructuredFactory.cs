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
    public class KeyUnstructuredFactory : Net.Vpc.Upa.Impl.KeyFactory {

        private Net.Vpc.Upa.Entity entity;

        public KeyUnstructuredFactory(Net.Vpc.Upa.Entity entity) {
            this.entity = entity;
        }

        public virtual System.Type GetIdType() {
            return typeof(Net.Vpc.Upa.Key);
        }


        public virtual Net.Vpc.Upa.Key CreateKey(params object [] keyValues) {
            return new Net.Vpc.Upa.Impl.DefaultKey(keyValues);
        }


        public virtual object CreateId(params object [] keyValues) {
            if ((entity.GetPrimaryFields()).Count != keyValues.Length) {
                throw new System.ArgumentException ("Invalid Key Size. Expected " + (entity.GetPrimaryFields()).Count + " but found " + keyValues.Length);
            }
            return new Net.Vpc.Upa.Impl.DefaultKey(keyValues);
        }


        public virtual object GetId(Net.Vpc.Upa.Key unstructuredKey) {
            return unstructuredKey;
        }


        public virtual Net.Vpc.Upa.Key GetKey(object key) {
            return (Net.Vpc.Upa.Key) key;
        }
    }
}
