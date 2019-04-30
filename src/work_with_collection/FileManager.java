package work_with_collection;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 * @author katya
 */
public class FileManager {

    private File workFile;

    /**
     * Конструктор - создание нового объекта с аргументами.
     * @param file - файл, с которым программа будет работать, например: считывать и записывать коллекцию.
     * @see File
     * @see Scanner
     * @see FileManager#checkFileUse(File)
     */
    public FileManager(File file) {
        while (!checkFileUse(file)){
            System.out.println("Файл не может быть использован. Введите имя файла.");
            file = new File(new Scanner(System.in).next());
        } workFile = file;
    }

    /**
     * Метод считывает данные из файла.
     * @return Возвращает строку, содержащую коллекцию в формате json.
     * @throws FileNotFoundException Ошибка пояаляется, когда файл с коллекцией не был найден.
     * @see StringBuilder
     * @see Scanner
     */
    public String readFile() throws FileNotFoundException {
        StringBuilder s = new StringBuilder();
        Scanner scanner = new Scanner(workFile);
        while (scanner.hasNextLine()) {
            s.append(scanner.nextLine());
        }
        scanner.close();
        if (s.toString().equals("")) {
            System.out.println("Файл пуст.");
        }
        return s.toString();
    }

    /**
     * Метод записывает данные в файл.
     * @param str - строка с данными о коллекции в формате json.
     * @throws FileNotFoundException Ошибка пояаляется, когда файл с коллекцией не был найден.
     * @see PrintWriter
     */
    public void writeToFile(String str) throws FileNotFoundException {
        PrintWriter writer = new PrintWriter(workFile);
        writer.write(str);
        writer.close();
    }

    /**
     * Метод проверяет, существует ли файл, является ли он файлом, можно ли его прочитать, изменить, и определяет, можно ли этот файл использовать.
     * @param file - файл, с которым программа будет работать.
     * @return Возвращает true, если файл можно использовать, и false, если по каким-то причинам файл не удовлетворяет нашим требованиям.
     * @see File
     */
    public static boolean checkFileUse(File file){
        if (file.exists()) {
            if (file.isFile()) {
                if (file.canRead()) {
                    if (file.canWrite()) {
                        System.out.println("Файл найден.");
                        return true;
                    } else {
                        System.out.println("Файл не может быть изменен.");
                        return false;
                    }
                } else {
                    System.out.println("Файл не может быть прочитан.");
                    return false;
                }
            } else {
                if (file.isDirectory()) {
                    System.out.println("Это директория.");
                    return false;
                } else {
                    System.out.println("Неизвестный вид файла.");
                    return false;
                }
            }
        }else {
            System.out.println("Файл не найден.");
            return false;
        }
    }

    @Override
    public String toString() {
        return "FileManager{" +
                "workFile=" + workFile +
                '}';
    }
}
