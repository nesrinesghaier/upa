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
     * Created by vpc on 7/25/15.
     */
    [System.AttributeUsage(System.AttributeTargets.Method)]

    public class OnReset : System.Attribute {

        private string _Name = "";
        public  string Name{
            get {return _Name;}
            set {_Name=value;}
        }


        private bool _TrackSystemObjects = false;
        public  bool TrackSystemObjects{
            get {return _TrackSystemObjects;}
            set {_TrackSystemObjects=value;}
        }


        private bool _Before = false;
        public  bool Before{
            get {return _Before;}
            set {_Before=value;}
        }


        private bool _After = true;
        public  bool After{
            get {return _After;}
            set {_After=value;}
        }


        private bool _Veto = false;
        public  bool Veto{
            get {return _Veto;}
            set {_Veto=value;}
        }


        private Net.Vpc.Upa.Config.Config _Config = new Net.Vpc.Upa.Config.Config();
        public  Net.Vpc.Upa.Config.Config Config{
            get {return _Config;}
            set {_Config=value;}
        }

    }
}
