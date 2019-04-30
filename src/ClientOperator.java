import com.google.gson.JsonSyntaxException;
import story.Jail;
import work_with_collection.CollectionManager;
import work_with_collection.CommandReaderAndExecutor;
import work_with_collection.FileManager;

import java.io.*;
import java.net.Socket;
import java.nio.channels.SocketChannel;
import java.util.NoSuchElementException;
import java.util.Stack;


class ClientOperator {
    private SocketChannel socketChannel;
    private volatile boolean status;

    ClientOperator(SocketChannel socketChannel) throws IOException {

        this.socketChannel = socketChannel;
        // поток чтения из сокета / записи в сокет
        Socket socket = socketChannel.socket();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
        DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
        DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
        System.out.println("Успешное подключение");

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                downService();
            } catch (NullPointerException e) {
                System.out.println("Это конец.");
            }
        }));

        System.out.println("Соединение установлено");

        try {
            while (true){
                if (!work(objectInputStream, objectOutputStream,dataInputStream, dataOutputStream)){
                    break;
                }
            }
        }catch (NullPointerException e){
            System.out.println("Соединение прервано. Канал закрылся.");
        }
    }

    private boolean work(ObjectInputStream objectInputStream, ObjectOutputStream objectOutputStream, DataInputStream dataInputStream, DataOutputStream dataOutputStream) throws IOException {
        while (true) {
            String message = dataInputStream.readUTF();
            System.out.println(message);
            if (message.equals("Соединение разорвано.")) {
                downService();
                return false;
            }
            if (message.equals("Введите команду")) {
                message = CommandReaderAndExecutor.readAndParseCommand();
                System.out.println(message);
                if (CommandReaderAndExecutor.checkCommand(message)) {
                    dataOutputStream.writeUTF(message.split(" ", 2)[0]);
                    try{
                        makeMessage(message, dataOutputStream, objectOutputStream);
                    }catch (IOException e){
                        System.out.println("Сервер недоступен для отправки");
                    }catch (NoSuchElementException e){
                        break;
                    }
                } else {
                    dataOutputStream.writeUTF("");
                    dataOutputStream.flush();
                }
            }
        }
        return false;
    }

    private void makeMessage(String inputString, DataOutputStream dataOutputStream, ObjectOutputStream objectOutputStream) throws IOException{
        String command = inputString.split(" ", 2)[0];
        switch (command){
            case ("load"):
                try {
                    dataOutputStream.writeUTF(inputString.split(" ", 2)[1]);
                    dataOutputStream.flush();
                } catch (IndexOutOfBoundsException e){
                    dataOutputStream.writeUTF("this");
                    dataOutputStream.flush();
                }
                break;
            case ("add"):
            case ("remove"):
                objectOutputStream.writeObject(makeJail(inputString));
                objectOutputStream.flush();
                break;
            case ("import"):
                objectOutputStream.writeObject(makeStack(inputString));
                objectOutputStream.flush();
                break;
            case ("insert"):
                dataOutputStream.writeUTF(inputString.split(" ", 3)[1]);
                dataOutputStream.flush();
                objectOutputStream.writeObject(makeJail(inputString));
                objectOutputStream.flush();
                break;
        }

    }

    private Stack<Jail> makeStack(String inputString){
        String fileName = inputString.split(" ", 2)[1];
        try {
            FileManager fileManager = new FileManager(new File(fileName));
            if (CollectionManager.checkSource(fileManager.readFile())){
                return CollectionManager.createStack(fileManager.readFile());
            } else {
                System.out.println("Ничего не добавлено: элементы заданы неверно");
                return null;
            }
        } catch (FileNotFoundException e) {
            System.out.println("Не удалось прочитать файл.");
            return null;
        }
    }

    private Jail makeJail(String command){
        try {
            switch (command.split(" ", 2)[0]){
                case ("add"):
                case ("remove"):
                    return CollectionManager.createJail(command.split(" ", 2)[1]);
                case ("insert"):
                    return CollectionManager.createJail(command.split(" ", 3)[2]);
                default:
                    return null;
            }
        }catch (JsonSyntaxException e){
            return null;
        }
    }

    private void downService() {
        try {
            if (!this.status) return;
            System.err.println("Сервер недоступен");
            this.status = false;
            if (socketChannel.isOpen()) {
                socketChannel.close();
                System.exit(0);
            }
        } catch (IOException ignored) {}
    }
}
