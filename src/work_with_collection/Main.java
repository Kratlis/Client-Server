package work_with_collection;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * @author katya
 */
public class Main{

    /**
     * @param args - При запуске программы в качестве входных файлов ожидается имя файла,
     *             если он находится в той же директории, откуда запускается программа,
     *             или полный путь к файлу.
     * @throws FileNotFoundException - Ошибка может появиться, если указанный файл не будет найден.
     */
    public static void main(String[] args) throws FileNotFoundException {


        CollectionManager stackManager = new CollectionManager();
        try {
            String inputFile = "";
            if (args.length == 0) {
                System.out.println("Введите имя файла, в котором записана коллекция объектов класса story.Jail в формате JSON.");
                inputFile = new Scanner(System.in).next();
            } else {
                inputFile = args[0];
            }
            stackManager = new CollectionManager(new File(inputFile));
            CommandReaderAndExecutor readerAndExecutor = new CommandReaderAndExecutor(stackManager);
            readerAndExecutor.getInfoForHelp();
            readerAndExecutor.govern();
        } catch (NoSuchElementException e){
            try {
                stackManager.save();
            } catch (IOException e1) {
                System.out.println("Коллекция не сохранена.");
            }
        }
    }
    
}
