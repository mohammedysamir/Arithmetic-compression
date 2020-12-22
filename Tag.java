public class Tag {
    public char Symbol;
    public float Lower_Range;
    public float Higher_Range;
    //public float Range;
    public int no_occ;
    public float probability;
    Tag(){
        Lower_Range=0.0f;
        Higher_Range=1.0f;
        probability=0.0f;
        no_occ=0;
        Symbol='\0';
    }
    Tag(float l,float h,float p){
        Lower_Range=l;
        Higher_Range=h;
        probability=p;
        //Range=Higher_Range-Lower_Range;
    }
    public void Updateprop(int length){
        no_occ++;
        float len=length;
        probability=no_occ/len;
    }
}
