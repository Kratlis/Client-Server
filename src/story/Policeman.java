package story;

import java.io.Serializable;

public class Policeman extends Shorty implements Alive, Serializable {
    
    private boolean armament;
//    private Baton policemanBaton;
    
    public Policeman(String s, int x, int y) {
        super(s, x, y);
        armament = false;
    }
    public Policeman(String name, int x, int y, boolean arm){
        this(name, x, y);
        armament = arm;
    }
    
    void punish(Shorty shorty) {
        moveToObject(shorty);
        if (this.armament) {
            this.speak("Лежать!");
            System.out.println("Полицейский " + this.getName() + " жалит электрической дубинкой коротышку " + shorty.getName());
            shorty.speak("ОЙ-ой-ой!");
            shorty.setEnergy(Math.random() * 20);
        } else if (shorty.getCond() == Condition.STANDING) {
            shorty.walk();
        }
    }
    
    void enter() {
        System.out.println("Врывается полицейский " + this.getName());
    }
    
    void isWinner(Crane crane, Stove stove) {
        if (this.getName().equals("Дригль")) {
            System.out.println("\nПолицейский " + this.getName() + " окинул взглядом победителя поле боя и убедился, что все коротышки лежат неподвижно.");
            pourWater(crane);
            extinguishFire(stove);
        }
    }
    
    boolean checkExhausted(Shorty shorty) {
        return (shorty.getCond() == Condition.EXHAUSTED);
    }
    
    boolean check(Shorty shorty) {
        return ((shorty.getCond() == Condition.MOVING) || (shorty.getCond() == Condition.STANDING));
    }
    
    private void moveToObject(Creature obj) {
        System.out.println("Полицейский " + this.getName() + " бежит к коротышке " + obj.getName());
        while ((Math.abs(this.getAbscissa() - obj.getAbscissa()) != 1) && (Math.abs(this.getOrdinate() - obj.getOrdinate()) != 1)) {
            if (this.getAbscissa() > obj.getAbscissa()) {
                this.setAbscissa(-1);
            } else this.setAbscissa(1);
            if (this.getOrdinate() > obj.getOrdinate()) {
                this.setOrdinate(-1);
            } else this.setOrdinate(1);
        }
    }
    
    double determineDistance(Creature obj) {
        return (Math.sqrt(Math.pow(this.getAbscissa() - obj.getAbscissa(), 2) + Math.pow(this.getOrdinate() - obj.getOrdinate(), 2)));
    }
    
    @Override
    public void speak(String s) {
        System.out.println(this.getName() + " приказывает: " + s);
    }
    
    @Override
    public void walk(){
        System.out.println("Полицейский ходит.");
    }
    
    void setArmament(boolean batonCond) {
        this.armament = batonCond;
    }
    
    public boolean isArmament() {
        return armament;
    }
    
    @Override
    public String toString() {
        if (armament) {
            return "Полицейский " + getName() + " с координатами (" + getAbscissa() + "; " + getOrdinate() + ") имеет дубинку ";
        } else {
            return "Полицейский " + getName() + " с координатами (" + getAbscissa() + "; " + getOrdinate() + ") не имеет дубинки.";
        }
    }
    private boolean checkCrane(Crane cr) {
        return cr.getCraneCondition() == CraneCondition.ThereIs;
    }
    
    private boolean checkWater(Crane cr) {
        return cr.getWater() == WithWater.Yes;
    }
    
    private void pourWater(Crane cr) {
        if (checkCrane(cr)) {
            System.out.println("Кран " + cr.getName() + " в комнате.");
            if (checkWater(cr)) {
                System.out.println("Полицейский " + this.getName() + " набирает из-под крана " + cr.getName() + " воды");
                cr.pour();
            } else System.out.println("В кране " + this.getName() + " нет воды.");
        } else System.out.println("Крана " + cr.getName() + " нет.");
    }
    
    private boolean checkFire(Stove st) {
        return st.getFire() == WithFire.Yes;
    }
    
    private void extinguishFire(Stove st) {
        if (checkFire(st)) {
            System.out.println("Полицейский " + this.getName() + " залил все еще пылающий огонь в опрокинутой печке " + st.getName());
            st.goOut();
        } else System.out.println("В каталажке темно.");
    }
}