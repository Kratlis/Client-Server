package story;

public class Main {

    public static void main(String[] args) {

        Crane crane = new Crane(2, 3, "Ржавый");
        Stove stove = new Stove("Черная", 1, 1, story.WithFire.Yes);
        Jail jail = new Jail(10, 10, crane, stove);
        System.out.println("В самый разгар драки:");

        story.Shorty first = new story.Shorty("Незнайка", story.Condition.LYING, 4, 4);
        story.Shorty second = new story.Shorty("Tanya", story.Condition.MOVING, 6, 1);
        story.Shorty third = new story.Shorty("Алёша", story.Condition.STANDING, 2, 4);
        story.Shorty fourth = new story.Shorty("Masha", story.Condition.MOVING, 9, 8);
        story.Policeman P1 = new story.Policeman("Сигль", 5, 2){
            public void enter(){
                System.out.println("Первый полицейский "+this.getName()+" влетел в комнату.");
            }
        };
        story.Policeman P2 = new story.Policeman("Дригль", 5, 9);
        story.Policeman P3 = new story.Policeman("Жмигль", 3, 7);
        story.Policeman P4 = new story.Policeman("Пхигль", 1, 5);

        jail.addShorty(first);
        jail.addShorty(second);
        jail.addShorty(third);
        jail.addShorty(fourth);
        jail.addPoliceman(P1);
        jail.addPoliceman(P2);
        jail.addPoliceman(P3);
        jail.addPoliceman(P4);
        System.out.println("\nНе прошло и пяти минут в каталажке");
        jail.work();
    }
    
}
