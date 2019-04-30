package story;

import java.io.Serializable;

public abstract class Creature implements Serializable {

    private String name;
    private int ordinate;
    private int abscissa;
//    private transient int code;

    Creature(){
        name = "без имени";
        abscissa = (int)(Math.random()*100);
        ordinate = (int)(Math.random()*100);
    }
    Creature(String n){
        this();
        name = n;
    }
    Creature(int x, int y){
        name = "без имени";
        abscissa = x;
        ordinate = y;
    }
    Creature(String n, int x, int y){
        this();
        name = n;
        abscissa = x;
        ordinate = y;
    }

    int getAbscissa(){return this.abscissa;}
    int getOrdinate(){return this.ordinate;}
    void setAbscissa(int a){
        this.abscissa += a;
    }
    void setOrdinate(int a){
        this.ordinate += a;
    }
    public String getName(){
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int hashCode(){
        int code = 7;
        code = 7*code + abscissa;
        code = 7*code + ordinate;
        code = (7 * code) + name.length();
        return code;
    }
    @Override
    public boolean equals(Object obj){
        return (this == obj)||(obj.hashCode() == this.hashCode())&&(obj instanceof Creature)&&(((Creature) obj).name.equals(this.name));
    }
    @Override
    public String toString() {
        return "Объект класса story.Creature:" +
                "name='" + name + '\'' +
                ", ordinate=" + ordinate +
                ", abscissa=" + abscissa +
                '}';
    }
}