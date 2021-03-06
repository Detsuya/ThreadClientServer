import java.net.*;
import java.io.*;

public class Server {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Usage: java EchoServer <port number>");
            System.exit(1);
        }
        int portNumber = Integer.parseInt(args[0]);
        int portNumber2 = Integer.parseInt(args[0])+1;
        Thread one = new Thread(new MyRunnable(portNumber));
        Thread two = new Thread(new MyRunnable(portNumber2));
        one.start();
        two.start();
    }
    static class MyRunnable implements Runnable {
        private int port;
        public MyRunnable(int port) {
            this.port = port;
        }

        @Override
        public void run() {
            PrintWriter out = null;
            BufferedReader in = null;
            ServerSocket serverSocket = null;
            Socket clientSocket = null;
            try {
                serverSocket = new ServerSocket(port);
                clientSocket = serverSocket.accept();
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String inputLine;
                String[] mathLine;
                String outputLine;
                while ((inputLine = in.readLine()) != null) {
                    mathLine = inputLine.split(" ");
                    try {
                        switch (mathLine[1]) {
                            case ("+"):
                                outputLine = Integer.toString(Integer.parseInt(mathLine[0]) + Integer.parseInt(mathLine[2]));
                                break;
                            case ("-"):
                                outputLine = Integer.toString(Integer.parseInt(mathLine[0]) - Integer.parseInt(mathLine[2]));
                                break;
                            case ("*"):
                                outputLine = Integer.toString(Integer.parseInt(mathLine[0]) * Integer.parseInt(mathLine[2]));
                                break;
                            case ("/"):
                                if (Integer.parseInt(mathLine[2]) != 0)
                                    outputLine = Integer.toString(Integer.parseInt(mathLine[0]) / Integer.parseInt(mathLine[2]));
                                else outputLine = "Divide by zero!";
                                break;
                            default:
                                outputLine = "Operator error!";
                                break;
                        }
                    }
                    catch (ArrayIndexOutOfBoundsException e) {
                        outputLine = ("String error");
                    }
                    out.println(Thread.currentThread().getName() + ":" + outputLine);
                }
            } catch (IOException e) {
                System.out.println("Exception caught when trying to listen on port "
                        + port + " or listening for a connection");
                System.out.println(e.getMessage());
            }
            finally {
                try {
                    out.close();
                    in.close();
                    serverSocket.close();
                    clientSocket.close();
                }
                catch(Exception e) {
                    System.out.println(e);
                }
            }
        }
    }
}
