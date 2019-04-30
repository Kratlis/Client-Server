package work_with_collection;

import com.google.gson.JsonSyntaxException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * @author katya
 */
public class CommandReaderAndExecutor {
    private CollectionManager collectionManager;
    private boolean needExit;
    private static String[] names = {"add", "exit", "help", "import", "info", "insert", "load", "remove", "show", "save"};

    /**
     * Конструктор с входными параметрами.
     * @param collectionManager: CollectionManager
     */
    public CommandReaderAndExecutor(CollectionManager collectionManager){
        this.collectionManager = collectionManager;
        if (collectionManager != null) {
            needExit = false;
        }
    }

    /**
     * Метод начинает выполнение программы.
     */
    public void govern(){

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                collectionManager.save();
            } catch (FileNotFoundException e) {
                System.out.println("Файл не найден. Коллекция не сохранена.");
            } catch (NullPointerException e) {
                System.out.println("Это конец.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));

        while(!needExit){
            String fullCommand = readAndParseCommand();   //fullCommand[0] is command. fullCommand[1] is argument
            if (checkCommand(fullCommand)){
               try{
                   doCommand(fullCommand, collectionManager);
               }catch (Exception e){
                   System.out.println("Упс, ошибочка вышла.");
               }

            } else {
                System.out.println("Неверно написана команда.");
            }

        }
    }

    /**
     * Метод выводит информацию о командах.
     */
    public static void getInfoForHelp(){
        System.out.println("You can use these commands:");
        for (String command: names){
            System.out.println(command+" \t"+getDescription(command));
        }
        System.out.println();
    }

    public static boolean checkCommand(String input){
        for (String command: names){
            if(((input.split(" ", 2)[0].equals(command))&&(input.split(" ").length >= numberInput(command)))){
                return true;
            }
        } return false;
    }

    private static int numberInput(String command){
        if (command.equals("info")||command.equals("show")||command.equals("load")||command.equals("help")||command.equals("exit")||command.equals("save")) {
            return 1;
        } else{
            if (command.equals("add")||command.equals("import")||command.equals("load")||command.equals("remove")) {
                return 2;
            } else {
                return 3;
            }
        }
    }

    private static String getDescription(String command){
        if (command.equals("add")) {
            return " Add element";
        }
        if (command.equals("help")) {
            return " Enable command";
        }
        if (command.equals("info")) {
            return " Information about collection";
        }
        if (command.equals("import")){
            return " Add elements from file";
        }
        if (command.equals("insert")) {
            return " Add element to this place";
        }
        if (command.equals("load")) {
            return " Rewrite collection";
        }
        if (command.equals("remove")) {
            return " Delete element on this place";
        }
        if (command.equals("show")){
            return " Show collection";
        } else return "Description hasn't be written yet.";
    }

    public static String readAndParseCommand(){
        Scanner in = new Scanner(System.in);
        String fullCommand;
        try {
            fullCommand = "";
            do {
                fullCommand = fullCommand + in.nextLine();
            }
            while ((fullCommand.length() - fullCommand.replaceAll("\\{", "").length()) > (fullCommand.length() - fullCommand.replaceAll("}", "").length()));
        }catch(NoSuchElementException ex){
            fullCommand = "";
        }
        fullCommand = fullCommand.trim();
        if(fullCommand.split(" ",2).length > 1) {
            while (fullCommand.split(" ",2)[1].contains("  ")) {
                fullCommand.split(" ",2)[1] = fullCommand.
                        split(" ",2)[1].replaceAll("  ", " ");
                fullCommand = fullCommand.split(" ",2)[0] +
                        fullCommand.split(" ",2)[1];
            }
        }
        return fullCommand;
    }

    private void doCommand(String com, CollectionManager colManager){
        switch (com.split(" ", 2)[0]){
            case ("info"):
                collectionManager.info();
                break;
            case ("add"):
                try{
                    collectionManager.add(com.split(" ", 2)[1]);
                }catch (JsonSyntaxException e){
                    System.out.println("Элемент задан неверно.");
                }
                break;
            case ("show"):
                colManager.show();
                break;
            case ("remove"):
                try{
                collectionManager.remove(com.split(" ", 2)[1]);
                }catch (JsonSyntaxException e){
                    System.out.println("Элемент задан неверно.");
                }
                break;
            case ("help"):
                getInfoForHelp();
                break;
            case ("import"):
                try {
                    collectionManager.doImport(com.split(" ", 2)[1]);

                } catch (FileNotFoundException e) {
                    System.out.println("Неверный файл.");
                }
                break;
            case ("load"):
                try{
                    collectionManager.load(com.split(" ", 2)[1]);
                } catch (IOException e) {
                    System.out.println("Неверный файл: ");
                    e.printStackTrace();
                }
                break;
            case ("exit"):
                needExit = true;
                collectionManager.finishWork();
                break;
            case ("insert"):
                try{
                    collectionManager.insert(com.split(" ", 3)[1], com.split(" ", 3)[2]);
                }catch (JsonSyntaxException e){
                    System.out.println("Элемент задан неверно. Он должен быть задан в формате json.");
                }catch (NumberFormatException e){
                    System.out.println("Ошибка, индекс");
                }
                break;
            case ("save"):
                try {
                    colManager.save();
                } catch (IOException e) {
                    System.out.println("Не удалось сохранить коллекцию.");
                }
            default:
                System.out.println("Ошибка, Неизвестная команда");
        }
    }
}
