import com.google.gson.JsonSyntaxException;
import story.Jail;
import work_with_collection.CollectionManager;
import work_with_collection.CommandReaderAndExecutor;
import work_with_collection.FileManager;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.SocketChannel;
import java.util.NoSuchElementException;
import java.util.Stack;


class ClientOperator {
    private SocketChannel socketChannel;
    private ObjectInputStream inputStream;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
    private int port;
    private String message;

    ClientOperator(SocketChannel socketChannel) throws IOException {

        this.socketChannel = socketChannel;
        this.inputStream = new ObjectInputStream(Channels.newInputStream(socketChannel));
        // поток чтения из сокета / записи в сокет
        Socket socket = socketChannel.socket();
//        objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
//        objectInputStream = new ObjectInputStream(socket.getInputStream());
        System.out.println("Успешное подключение");
        port = socket.getPort();

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
                if (!work()){
                    break;
                }
            }
        }catch (NullPointerException e){
            System.out.println("Соединение прервано. Канал закрылся.");
        }
    }

    private boolean work(){
        while (true) {
            try {
                message = getMessage();
                System.out.println(message);
                if (message.equals("Соединение разорвано.") || message.equals("Сокет закрылся.")) {
                    downService();
                    return false;
                }
                if (message.equals("\nВведите команду")) {
                    message = CommandReaderAndExecutor.readAndParseCommand();
                    System.out.println(message);
                    if (CommandReaderAndExecutor.checkCommand(message)) {
                        try{
                            sendObject(message.split(" ", 2)[0]);
                            makeMessage(message);
                        } catch (IOException e) {
                            resend(message);
                        } catch (NoSuchElementException e) {
                            break;
                        }
                    } else {
                        sendObject("");
                    }
                }
            }catch (IOException e) {
                System.out.println("Сервер недоступен для отправки");
                downService();
                break;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private String getMessage() throws IOException, ClassNotFoundException {
//        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(100000);
//        long t1 = System.currentTimeMillis();

//        int con = socketChannel.read(byteBuffer);
//        boolean ch = byteBuffer.hasRemaining();
        /*while (System.currentTimeMillis() - t1 < 5000) {
            if (con != -1) {
                t1 = System.currentTimeMillis();
                System.out.println(byteBuffer.toString());
                byteBuffer.flip();
//                second.put(byteBuffer);
            } else {
                System.out.println("Сокет закрылся.");
                break;
            }
        }*/
//        if (con == -1) {
//            return "Сокет закрылся.";
//        }

//        byteBuffer.flip();
//        byte[] bs = new byte[byteBuffer.remaining()];
//        byteBuffer.get(bs);
//        String str = new String(bs).trim();
//        objectInputStream = new ObjectInputStream(new ByteArrayInputStream(bs));
        /*String str = null;
        try {
            str = (String)objectInputStream.readObject();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println(str);
        str = str.substring(2);
*/
        return (String) inputStream.readObject();
    }

    private void makeMessage(String inputString) throws IOException{
        String command = inputString.split(" ", 2)[0];
        switch (command){
            case ("load"):
                try {
                    sendObject(inputString.split(" ", 2)[1]);
                } catch (IndexOutOfBoundsException e){
                    sendObject("this");
                }
                break;
            case ("add"):
            case ("remove"):
                Jail jail = makeJail(inputString);
                sendObject(jail);
                System.out.println("sent Jail");
                break;
            case ("import"):
                sendObject(makeStack(inputString));
                break;
            case ("insert"):
                sendObject(inputString.split(" ", 3)[1]);
                sendObject(makeJail(inputString));
                break;
        }

    }

    private void resend(String message) throws IOException, ClassNotFoundException {
        socketChannel = SocketChannel.open(new InetSocketAddress("localhost", port));
        System.out.println(1 + getMessage());
        sendObject(message.split(" ", 2)[0]);
        makeMessage(message);
    }

    /*private void sendMessage(String msg) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        objectOutputStream = new ObjectOutputStream(bos);
        objectOutputStream.writeObject(msg);
        objectOutputStream.flush();
        socketChannel.write(ByteBuffer.wrap(bos.toByteArray()));
        System.out.println("writing:" + msg);
        byte[] b = msg.getBytes();
        socketChannel.write(ByteBuffer.wrap(b));
            objectOutputStream.writeUTF(msg);
            objectOutputStream.flush();
    }*/
    private void sendObject(Object object) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        objectOutputStream = new ObjectOutputStream(bos);
        objectOutputStream.writeObject(object);
        objectOutputStream.flush();
        socketChannel.write(ByteBuffer.wrap(bos.toByteArray()));
    }
    
    private Stack<Jail> makeStack(String inputString){
        String fileName = inputString.split(" ", 2)[1];
        try {
            FileManager fileManager = new FileManager(new File(fileName));
            if (CollectionManager.checkSource(fileManager.readFile())){
                Stack<Jail> st = CollectionManager.createStack(fileManager.readFile());
                return st;
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
            if (socketChannel.isOpen()) {
                objectOutputStream.writeUTF("exit");
                socketChannel.close();
                System.exit(0);
            }
        } catch (IOException ignored) {}
    }
}
