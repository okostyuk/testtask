package com.oleg.testttask;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        int port = 7999;
        Server server = new Server(port);
        Client client = new Client("localhost", port);
        server.start();
        client.start();
    }
}
