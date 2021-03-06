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



namespace Net.Vpc.Upa.Config
{


    /**
     * @author Taha BEN SALAH <taha.bensalah@gmail.com>
     * @creationdate 8/29/12 12:27 AM
     */
    [System.AttributeUsage(System.AttributeTargets.Class)]

    public class UnionEntity : System.Attribute {

        private Net.Vpc.Upa.Config.UnionEntityEntry[] _Entities;
        public  Net.Vpc.Upa.Config.UnionEntityEntry[] Entities{
            get {return _Entities;}
            set {_Entities=value;}
        }


        private string[] _Fields;
        public  string[] Fields{
            get {return _Fields;}
            set {_Fields=value;}
        }


        private string _Discriminator;
        public  string Discriminator{
            get {return _Discriminator;}
            set {_Discriminator=value;}
        }


        private System.Type _Spec = typeof(Net.Vpc.Upa.Extensions.UnionEntityExtensionDefinition);
        public  System.Type Spec{
            get {return _Spec;}
            set {_Spec=value;}
        }


        /**
             * annotation config defines how this annotation must be handled
             *
             * @return annotation configuration
             */
        private Net.Vpc.Upa.Config.Config _Config = new Net.Vpc.Upa.Config.Config();
        public  Net.Vpc.Upa.Config.Config Config{
            get {return _Config;}
            set {_Config=value;}
        }

    }
}
