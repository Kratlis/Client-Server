import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Client {

    /*
    Выполнить команду от пользования при установке соединения
     */
    private static SocketChannel findConnect(){
        System.out.println("Введите \"connect\" и номер порта, чтобы подключиться к серверу");
        String s = new Scanner(System.in).nextLine();
        while (!(s.split(" ")[0].equals("exit") || s.split(" ")[0].equals("connect"))) {
            System.out.println("Вы ввели неизвестную команду. Введите \"connect\" и номер порта, чтобы подключиться к серверу.");
            s = new Scanner(System.in).next();
        }
        if (s.split(" ")[0].equals("exit")) {
            System.exit(0);
        }
        if (s.split(" ")[0].equals("connect")) {
            try{
                return connect(Integer.parseInt(s.split(" ")[1]));
            }
            catch (NumberFormatException|ArrayIndexOutOfBoundsException e){
                System.out.println("Порт задан неверно");
                return null;
            }
        }
        return null;
    }

    /*
    Соединиться с сервером
     */
    private static SocketChannel connect(int i) {
        try {
            SocketAddress sa = new InetSocketAddress("localhost", i);
            return SocketChannel.open(sa);
        } catch (Exception e) {
            System.out.println("Не удалось установить соединение");
        }
        return null;
    }

    public static void main(String[] args) {

        Runtime.getRuntime().addShutdownHook(new Thread(() -> System.out.println("Работа закончена.")));

        while (true) {
            try {
                SocketChannel channel = findConnect();
                if (channel == null) {
                    continue;
                }
                try {
                    new ClientOperator(channel);
                } catch (IOException e) {
                    System.out.println("Соединение не установлено");
                }
            } catch (NoSuchElementException e) {
                System.out.println("Работа закончена.");
                break;
            }
        }
    }
}
