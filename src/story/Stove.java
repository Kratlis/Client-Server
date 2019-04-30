package story;

import java.io.Serializable;

public class Stove extends Thing implements Serializable {

    private WithFire fire;

    public Stove(String n, int x, int y){
        super(n, x, y);
    }
    public Stove(String n, int x, int y, WithFire f){
        super(n, x, y);
        fire = f;
    }

    //потушить огонь
    public  void goOut(){
        fire = WithFire.No;
        System.out.println("Огонь потух в  печке "+this.getName());
    }
    public WithFire getFire(){
        return fire;
    }
}
