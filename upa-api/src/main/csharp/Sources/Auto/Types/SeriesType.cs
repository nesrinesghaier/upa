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



namespace Net.Vpc.Upa.Types
{


    public abstract class SeriesType : Net.Vpc.Upa.Types.DataType {

        protected internal SeriesType(string name, System.Type platformType)  : base(name, platformType){

        }

        protected internal SeriesType(string name, System.Type platformType, bool nullable)  : base(name, platformType, nullable){

        }

        protected internal SeriesType(string name, System.Type platformType, int scale, int precision, bool nullable)  : base(name, platformType, scale, precision, nullable){

        }

        public abstract System.Collections.Generic.IList<object> GetValues();
    }
}
