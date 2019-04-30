package story;

import java.io.Serializable;

public class Shorty extends Creature implements Alive, Serializable {

    private Condition cond;
    private double energy;

    Shorty(String s, int x, int y){
        super(s, x, y);
    }
    Shorty(String s, Condition c, int x, int y){
        super(s, x, y);
        cond = c;
        switch (cond){
        case LYING: energy = Math.random()+ 5;
                break;
        case MOVING: energy = Math.random()+ 8;
                break;
        case STANDING: energy = Math.random()+ 10;
                break;
        }
    }

    class Angry {
        int power;
        public void takeBaton(Policeman p){
            p.setArmament(false);
        }
    }


    public void speak(String s) {
        System.out.println(this.getName()+" говорит: "+s);
    }
    public void walk() {
        System.out.println("Коротышка "+this.getName()+" убегает.");
    }
    Condition getCond(){
        return cond;
    }
    double getEnergy(){ return energy;}
    void setEnergy(double a){
        if (a!=0) {
            energy -= a;
        }
        System.out.println("Энергия коротышки "+this.getName()+" равна " + energy);
        whatToDo();
        System.out.println("Энергия коротышки "+this.getName()+" равна " + energy);
    }
    void push(Shorty shorty) throws TooFarException {
        if (Math.abs(this.getAbscissa() - shorty.getAbscissa()) > 1 || Math.abs(this.getOrdinate() - shorty.getOrdinate()) > 1){
            throw new TooFarException("Коротышка "+shorty.getName()+" слишком далеко.");
        }
    }
    private void whatToDo(){
        if (energy > 8){
            standUp();
            relax();
        } else if (energy > 6) {
            move();
            relax();
        } else if (energy > 2) {
            lie();
            relax();
        } else exhausted();
    }
    private void lie(){
        cond = Condition.LYING;
        System.out.println("Коротышка "+this.getName()+" лежит.");
    }
    private void move(){
        cond = Condition.MOVING;
        System.out.println("Коротышка "+this.getName()+" шевелится.");
    }
    private void standUp(){
        cond = Condition.STANDING;
        System.out.println("Коротышка "+this.getName()+" встает.");
    }
    private void exhausted() {
        cond = Condition.EXHAUSTED;
        System.out.println("Коротышка " + this.getName() + " без сил.");
    }
    private void relax(){
        energy += Math.random()*2;
        System.out.println("Коротышка "+this.getName()+" отдохнул.");
    }
    private void hide(){
        cond = Condition.HIDDEN;
        System.out.println("Коротышка "+this.getName()+" спрятался под полкой.");
    }


}
