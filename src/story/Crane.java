package story;

import java.io.Serializable;

public class Crane extends Thing implements Serializable {

    private CraneCondition cond;
    private WithWater water;

    Crane(){
        super();
        cond = CraneCondition.ThereNot;
        water = WithWater.Yes;
    }
    Crane(int x, int y, String s){
        super(s, x, y);
        cond = CraneCondition.ThereIs;
        water = WithWater.Yes;
    }


    public void pour(){
        System.out.println("Вода льется из крана "+this.getName());
    }
    public CraneCondition getCraneCondition(){
        return cond;
    }
    public WithWater getWater(){
        return water;
    }
}
