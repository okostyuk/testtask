package com.oleg.testttask;

import java.io.*;
import java.net.Socket;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Client {

    private static final String CLOSE_REQUEST_STRING = "OK CLOSE\n";
    private final String host;
    private final int port;

    public Client(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() throws IOException {
        new Thread(() -> {
            try {
                Request request = getRequestFromConsole();
                String response = sendRequest(request);
                System.out.println("RESULT IS " + response);
            }catch (IOException ex){
                System.err.println(ex.getMessage());
            }
        }).start();
    }

    public String sendRequest(Request request) throws IOException {
        Socket socket = new Socket(host, port);
        BufferedWriter writer = null;
        BufferedReader reader = null;
        String response = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            response = sendRequest(request, writer, reader);
            sendCloseRequest(writer, reader);
            return response;
        }finally {
            if (writer != null) try{writer.close();}catch (IOException ex){}
            if (reader != null) try{reader.close();}catch (IOException ex){}
            try{socket.close();}catch (IOException ex){}
        }

    }

    private Request getRequestFromConsole() {
        System.out.println("Select operation:");
        for (Operation.Type type : Operation.Type.values()){
            System.out.println(type.getShortName());
        }
        BufferedReader inputReader = new BufferedReader(new InputStreamReader(System.in));
        try{
            Operation operation = null;
            while(operation == null){
                try {
                    String selectedOperationName = inputReader.readLine();
                    validateOperaton(selectedOperationName);
                    Operation.Type type = Operation.Type.findByShortName(selectedOperationName);
                    operation = new Operation(type);
                }catch (InvalidParameterException|IOException ex){
                    System.err.println(ex.getMessage());
                    continue;
                }
            }

            System.out.println("Selected operation is " + operation.getType().name());
            System.out.println("Enter arguments (or leave blank for send request): ");

            final List<Integer> arguments = new ArrayList<>();
            int enteredArgument;
            String enteredArgumentString = null;
            while(enteredArgumentString == null || !enteredArgumentString.isEmpty()){
                try {
                    enteredArgumentString = inputReader.readLine();
                    if (enteredArgumentString.isEmpty()){
                        if (arguments.isEmpty())
                            continue;
                        break;
                    }

                    enteredArgument = Integer.valueOf(enteredArgumentString);
                    arguments.add(enteredArgument);
                }catch (NumberFormatException|InvalidParameterException|IOException ex){
                    System.err.println(ex.getMessage());
                    continue;
                }
                System.out.println("Entered arguments: " + Arrays.toString(arguments.toArray()));
            }
            return new Request(operation, arguments);
        }finally {
            try {
                inputReader.close();
            } catch (IOException e) {}
        }
    }

    private String sendCloseRequest(BufferedWriter writer, BufferedReader reader) throws IOException {
        System.out.println("SEND: " + CLOSE_REQUEST_STRING);
        writer.write(CLOSE_REQUEST_STRING);
        writer.flush();
        String response = reader.readLine();
        System.out.println("RECEIVE: " + response);
        return response;
    }

    private String sendRequest(Request request, BufferedWriter writer, BufferedReader reader) throws IOException {
        String requestString = request.toString() + "\n";
        System.out.println("SEND: " + requestString);
        writer.write(requestString);
        writer.flush();
        String result = reader.readLine();
        System.out.println("RECEIVE: " + result);
        return result;
    }


    private static void validateOperaton(String selectedOperation) throws InvalidParameterException{
        if (Operation.Type.findByShortName(selectedOperation) == null)
            throw new InvalidParameterException("Invalid operation " + selectedOperation);
    }

}
