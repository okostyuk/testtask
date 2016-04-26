package com.oleg.testttask;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Optional;

public class Server {
    private volatile boolean runned = false;
    private final int port;
    public Server(int port){
        this.port = port;
    }

    public void start() {
        runned = true;
        new Thread(() -> {
            try {
                final ServerSocket serverSocket = new ServerSocket(port);
                while(runned){
                    final Socket socket = serverSocket.accept();
                    new Thread(new RequestHandler(socket)).start();

                }
            }catch (IOException ex){
                System.err.println(ex.getMessage());
            }
        }).start();
    }

    public void stop() {
        runned = false;
    }

    private static class RequestHandler implements Runnable{
        private static final String ERROR_STRING = "ERROR\n";
        private static final String CLOSE_RESPONSE_STRING = "CLOSED\n";
        private static final String CLOSE_REQUEST_STRING = "OK CLOSE";
        private final Socket socket;

        private RequestHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            BufferedReader reader = null;
            BufferedWriter writer = null;
            try {
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                String requestString = reader.readLine();
                while(requestString != null){
                    if (requestString.startsWith(CLOSE_REQUEST_STRING)){
                        writer.write(CLOSE_RESPONSE_STRING);
                        break;
                    }else{
                        String responseString = processRequest(requestString);
                        writer.write(responseString);
                        writer.flush();
                        requestString = reader.readLine();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try { if (writer != null) writer.close();} catch (IOException e) {}
                try { if (reader != null) reader.close(); } catch (IOException e) {}
                try { socket.close(); } catch (IOException e) {}
            }
        }

        private String processRequest(String requestString) {
            try{
                Request request = Request.fromString(requestString);
                Optional<Integer> resultValue = request.getArguments().stream()
                        .reduce(request.getOperation());
                if (resultValue.isPresent()){
                    int result = resultValue.get();
                    if (isResultValid(result))
                        return "RESULT " + result + "\n";
                    else
                        return ERROR_STRING;
                }else {
                    return ERROR_STRING;
                }
            }catch (Exception ex){
                ex.printStackTrace();
                return ERROR_STRING;
            }
        }

        private boolean isResultValid(int result) {
            return result >= Character.MIN_VALUE && result <= Character.MAX_VALUE;
        }
    }
}
