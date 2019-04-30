package story;

import java.io.Serializable;

public class Baton extends Thing implements Serializable {

    private boolean isWhole;
    private int power;

    public Baton() {
        isWhole = true;
    }

    public boolean isWhole() {
        return isWhole;
    }
    public int getPower() {
        return power;
    }
    public void setWhole(boolean whole) {
        isWhole = whole;
    }
    public void setPower(int power) {
        this.power = power;
    }

    @Override
    public String toString() {
        String n;
        if (getName()==null){
            n="";
        } else {n=getName();}
        if (isWhole) {
            return "Дубинка "+n+" целая с мощностью " + power;
        } else {
            return "Дубинка "+n+" сломанная.";
        }
    }
}
