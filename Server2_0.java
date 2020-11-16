import java.net.*;
import java.io.*;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Usage: java Server <port number>");
            System.exit(1);
        }
        ConcurrentHashMap<Socket, String> inputMap = new ConcurrentHashMap<>();
        ConcurrentHashMap<Socket, String> outputMap = new ConcurrentHashMap<>();
        int workerNumber = 2;
        int portNumber = Integer.parseInt(args[0]);
        Socket clientSocket;
        ServerSocket serverSocket = new ServerSocket(portNumber);
        while (workerNumber > 0) {
            Thread t = new Thread(new WorkerThread(inputMap, outputMap));
            t.start();
            workerNumber--;
        }
        Thread t = new Thread(new ResultThread(outputMap));
        t.start();
        t = new Thread(new ListenerThread(inputMap));
        t.start();
        while (true) {
            clientSocket = serverSocket.accept();
            inputMap.put(clientSocket, "");
        }
    }


    static class ListenerThread implements Runnable {
        private ConcurrentHashMap<Socket, String> inputMap;

        ListenerThread(ConcurrentHashMap<Socket, String> inputMap) {
            this.inputMap = inputMap;
        }

        @Override
        public void run() {
            BufferedReader in;
            while (true) {
                Iterator<ConcurrentHashMap.Entry<Socket, String>> itr = inputMap.entrySet().iterator();
                while (itr.hasNext()) {
                    try {
                        ConcurrentHashMap.Entry<Socket, String> entry = itr.next();
                        in = new BufferedReader(new InputStreamReader(entry.getKey().getInputStream()));
                        if (in.ready()) inputMap.put(entry.getKey(), in.readLine());
                    } catch (IOException e) {
                        System.out.println("IOException caught on ListenerThread");
                    }
                }
            }
        }
    }


    static class WorkerThread implements Runnable {
        private ConcurrentHashMap<Socket, String> inputMap;
        private ConcurrentHashMap<Socket, String> outputMap;

        WorkerThread(ConcurrentHashMap<Socket, String> inputMap, ConcurrentHashMap<Socket, String> outputMap) {
            this.inputMap = inputMap;
            this.outputMap = outputMap;
        }

        @Override
        public void run() {
            String inputLine;
            String[] mathLine;
            String outputLine;
            while(true) {
                Iterator<ConcurrentHashMap.Entry<Socket, String>> itr = inputMap.entrySet().iterator();
                while (itr.hasNext()) {
                    ConcurrentHashMap.Entry<Socket, String> entry = itr.next();
                    if (!(inputLine = inputMap.get(entry.getKey())).equals("")) {
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
                        outputMap.put(entry.getKey(),outputLine);
                        inputMap.put(entry.getKey(),"");
                    }
                }
            }
        }
    }


    static class ResultThread implements Runnable {
        private ConcurrentHashMap<Socket, String> outputMap;
        ResultThread(ConcurrentHashMap<Socket, String> outputMap) {
            this.outputMap = outputMap;
        }

        @Override
        public void run() {
            PrintWriter out;
            while(true) {
                Iterator<ConcurrentHashMap.Entry<Socket, String>> itr = outputMap.entrySet().iterator();
                while (itr.hasNext()) {
                    try {
                        ConcurrentHashMap.Entry<Socket, String> entry = itr.next();
                        out = new PrintWriter(entry.getKey().getOutputStream(), true);
                        if (!outputMap.get(entry.getKey()).equals("")) {
                            out.println("Answer: " + outputMap.get(entry.getKey()));
                            outputMap.put(entry.getKey(), "");
                        }
                    }
                    catch (IOException e) {
                        System.out.println("IOException caught on ResultThread");
                    }

                }
            }
        }
    }
}

