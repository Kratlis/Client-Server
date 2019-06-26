package story;

import java.io.Serializable;

public class Crane extends Thing implements Serializable {

    private CraneCondition condition;
    private WithWater water;
    
    public Crane(int x, int y, String s){
        super(s, x, y);
        condition = CraneCondition.ThereIs;
        water = WithWater.Yes;
    }
    public Crane(int x, int y, String s, CraneCondition cond){
        this(x, y, s);
        condition = cond;
    }
    public Crane(int x, int y, String s, WithWater w){
        this(x, y, s);
        water = w;
    }
    public Crane(int x, int y, String s, CraneCondition cond, WithWater w){
        this(x, y, s, cond);
        water = w;
    }


    public void pour(){
        System.out.println("Вода льется из крана "+this.getName());
    }
    public CraneCondition getCraneCondition(){
        return condition;
    }
    public WithWater getWater(){
        return water;
    }
}
