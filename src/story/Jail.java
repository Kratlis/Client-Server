package story;
import com.google.gson.Gson;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;

public class Jail extends Creature implements Serializable, Comparable<Jail> {
    private ArrayList<Policeman> squad = new ArrayList<>();
    private ArrayList<Shorty> prisoners = new ArrayList<>();
    private ArrayList<Shorty> theDead = new ArrayList<>();
    private Crane crane;
    private Stove stove;
    private LocalDateTime initDate;
    private transient int num;
    
    public Jail(int x, int y, String name, Crane crane, Stove stove) {
        super(name, x, y);
        initDate = LocalDateTime.now();
        this.crane = crane;
        this.stove = stove;
    }
    public Jail(int x, int y, Crane crane, Stove stove) {
        super(x, y);
        initDate = LocalDateTime.now();
        this.crane = crane;
        this.stove = stove;
    }
    public Jail(int abscissa, int ordinate, String name, LocalDateTime date) {
        super(name, abscissa, ordinate);
        initDate = date;
    }
    public Jail(int x, int y, String name, Crane crane, Stove stove, LocalDateTime dateTime) {
        this(x, y, name, crane, stove);
        initDate = LocalDateTime.now();
    }
    
    
    public void addPoliceman(Policeman man) {
        squad.add(man);
        System.out.println("Полицейский "+man.getName()+" вбежал в каталажку");
    }
    public void addShorty(Shorty shorty) {
        prisoners.add(shorty);
    }
    public void work(){
        if (prisoners.size() == 0) {throw new EmptyJailException("В комнате нет коротышек!");}
        else {
            while (prisoners.size() > theDead.size()) {
                for (Shorty i : prisoners) {
                    if (i.getName().equals("Незнайка")) {
                        num = prisoners.indexOf(i);
                    }
                    Policeman nearest = squad.get(0);
                    End:
                    if (nearest.checkExhausted(i)) {
                        for (Shorty d: theDead){
                            if (i.equals(d)) break End;
                        }
                        theDead.add(i);
                    } else {
                        if (nearest.check(i)) {
                            for (Policeman j : squad) {
                                if (j.determineDistance(i) < nearest.determineDistance(i)) { nearest = j;}
                                nearest.punish(i);
                                if (i.getName().equals("Черноглазый")) {
                                    try {
                                        i.push(prisoners.get(num));
                                    } catch (TooFarException e) {
                                        System.out.println("Коротышка "+i.getName()+" слишком далеко от коротышки "+prisoners.get(num).getName());
                                    }
                                }
                            }
                        } else i.setEnergy(0);
                    }
                }
            }
        }
        for (Policeman j : squad)
            j.isWinner(this.crane, this.stove);
    }
    
    public ArrayList<Policeman> getSquad() {
        return squad;
    }
    public ArrayList<Shorty> getPrisoners() {
        return prisoners;
    }
    public ArrayList<Shorty> getTheDead(){
        return theDead;
    }
    
    public Crane getCrane() {
        return crane;
    }
    
    public Stove getStove() {
        return stove;
    }
    public LocalDateTime getInitDate() {
        return initDate;
    }
    public void setInitDate(LocalDateTime initDate) {
        this.initDate = initDate;
    }
    
    public String toString(){
        Gson gson = new Gson();
        return gson.toJson(this);
    }
    
    @Override
    public int compareTo(Jail o) {
        if (getName().equals(o.getName())){
            return -1;
        } else {
            return getName().compareTo(o.getName());
        }
    }
}