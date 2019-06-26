package story;

import java.io.Serializable;

public class Thing extends Creature implements Serializable {

    Thing(){
        super();
    }
    Thing(String n,  int x, int y){
        super(n, x, y);
    }
}
