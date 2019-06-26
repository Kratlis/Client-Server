import com.google.gson.JsonSyntaxException;
import story.Jail;
import work_with_collection.CollectionManager;
import work_with_collection.CommandReaderAndExecutor;
import work_with_collection.FileManager;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.*;


class ClientOperator {
    private SocketChannel socketChannel;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private int port;
    private String login;
    private String password;

    ClientOperator(SocketChannel socketChannel) {

        this.socketChannel = socketChannel;
        Socket socket = socketChannel.socket();
        System.out.println("Успешное подключение");
        port = socket.getPort();

        try{
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    downService();
                } catch (NullPointerException e) {
                    System.out.println("Это конец.");
                }
            }));
        } catch (IllegalStateException ignored){}

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
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        while (true) {
            try {
                String message = getMessage();
                System.out.println(message);
                Scanner scanner =  new Scanner(System.in);
                if (message.equals("Введите \"enter\", чтобы подключиться к базе данных. \n" +
                        "Если вы еще не зарегистрированы, введите \"reg\"") ||  message.equals("Введите адрес электронной почты")
                || message.equals("Пароль был отправлен Вам на почту. Введите его.") || message.equals("Пароли не совпадают. Попробуйте ещё раз ввести пароль.")){
                    sendObject(scanner.next());
                    continue;
                }
                if (message.equals("Введите логин")){
                    login = scanner.next();
                    sendObject(login);
                    continue;
                }
                if (message.equals("Введите пароль")){
                    password = scanner.next();
                    sendObject(password);
                    continue;
                }
                if (message.equals("Соединение разорвано.") || message.equals("Сокет закрылся.")) {
                    downService();
                    return false;
                }
                if (message.equals("\nВведите команду")) {
                    message = CommandReaderAndExecutor.readAndParseCommand();
                    if (CommandReaderAndExecutor.checkCommand(message)) {
                        try{
                            sendObject(login);
                            sendObject(password);
                            sendObject(message.split(" ", 2)[0]);
                            makeMessage(message);
                        } catch (IOException e) {
                            System.out.println("Соединение было прервано. " +
                                    "Пробуем подключиться заново и отправить комаду.");
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
                break;
            }
        }
        return false;
    }

    private String getMessage() throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(100000);
        long t1 = System.currentTimeMillis();
        int com = socketChannel.read(byteBuffer);
        if (com == -1){
            throw new IOException();
        }
        while (com == 0){
            if (System.currentTimeMillis() - t1 > 2000){
                throw new IOException();
            }
            com = socketChannel.read(byteBuffer);
        }
//        System.out.println(con);
//        while (con != -1){
//            con = socketChannel.read(byteBuffer);
//            System.out.println(con);
//        }
//        boolean ch = byteBuffer.hasRemaining();
//        while (System.currentTimeMillis() - t1 < 5000) {
//            if (con != -1) {
//                t1 = System.currentTimeMillis();
//                System.out.println(byteBuffer.toString());
//                byteBuffer.flip();
////                second.put(byteBuffer);
//            } else {
//                System.out.println("Сокет закрылся.");
//                break;
//            }
//        }
//        if (con == -1) {
//            return "Сокет закрылся.";
//        }
        byteBuffer.flip();
        byte[] bs = new byte[byteBuffer.remaining()];
        byteBuffer.get(bs);
        String str = new String(bs, StandardCharsets.UTF_8).trim();
        //        ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bs));
//        String str = null;
//        try {
//            str = (String)objectInputStream.readObject();
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//        str = str.substring(4);
//        int h = str.split("\n", 2).length;
        if (str.split("!", 2).length > 1) {
            str = str.split("!", 2)[1];
        }
        return str;
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

    private void resend(String message) throws IOException {
        socketChannel = SocketChannel.open(new InetSocketAddress("localhost", port));
        getMessage();
        sendObject("enter");
        getMessage();
        sendObject(login);
        getMessage();
        sendObject(password);
        getMessage();
        sendObject(message.split(" ", 2)[0]);
        makeMessage(message);
    }
    
    private void sendObject(Object object) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        out = new ObjectOutputStream(bos);
        out.writeObject(object);
        out.flush();
        socketChannel.write(ByteBuffer.wrap(bos.toByteArray()));
    }
    
    private Stack<Jail> makeStack(String inputString){
        String fileName = inputString.split(" ", 2)[1];
        try {
            FileManager fileManager = new FileManager(new File(fileName));
            if (CollectionManager.checkSource(fileManager.readFile())){
                Stack<Jail> st = CollectionManager.createStack(fileManager.readFile());
                st.stream().filter(t -> t.getName()==null).forEach(t -> t.setName("NoName"));
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
                out.writeUTF("exit");
                socketChannel.close();
                System.exit(0);
            }
        } catch (IOException ignored) {}
    }
}
