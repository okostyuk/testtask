package com.oleg.testtask;

import com.oleg.testttask.Client;
import com.oleg.testttask.Operation;
import com.oleg.testttask.Request;
import com.oleg.testttask.Server;
import org.junit.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;



public class Tests extends Assert {

    private static final int PORT = 7998;
    private static final String HOST = "localhost";
    private static Server server;
    private static Client client;

    @BeforeClass
    public static void init() {
        client = new Client(HOST, PORT);
        server = new Server(PORT);
        server.start();
    }

    @Test
    public void testMultiply() throws IOException {
        Operation multiplyOperation = new Operation(Operation.Type.TIMES);
        List<Integer> arguments = Arrays.asList(25, 3);
        Request request = new Request(multiplyOperation, arguments);
        String response = client.sendRequest(request);
        assertEquals("RESULT 75", response);
    }

    @Test
    public void testErrorResult() throws IOException {
        Operation minusOperation = new Operation(Operation.Type.MINUS);
        List<Integer> arguments = Arrays.asList(1, 100);
        Request request = new Request(minusOperation, arguments);
        String response = client.sendRequest(request);
        assertEquals("ERROR", response);
    }
}
