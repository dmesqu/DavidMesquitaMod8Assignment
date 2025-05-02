package org.example.davidmesquitamod8assignment;


import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;

import static java.lang.Thread.sleep;

public class MainController implements Initializable {
    @FXML
    private ComboBox dropdownPort;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dropdownPort.getItems().addAll("7",     // ping
                "13",     // daytime
                "21",     // ftp
                "23",     // telnet
                "71",     // finger
                "80",     // http
                "119",     // nntp (news)
                "161"      // snmp);
        );
        new Thread(this::startChatServer).start();
    }

    @FXML
    private Button clearBtn;

    @FXML
    private TextArea resultArea;

    @FXML
    private Label server_lbl;

    @FXML
    private Button testBtn;

    @FXML
    private Label test_lbl;

    @FXML
    private TextField urlName;

    Socket socket1;

    Label lb122, lb12;
    TextField msgText;

    @FXML
    void checkConnection(ActionEvent event) {

        String host = urlName.getText();
        int port = Integer.parseInt(dropdownPort.getValue().toString());

        try {
            Socket sock = new Socket(host, port);
            resultArea.appendText(host + " listening on port " + port + "\n");
            sock.close();
        } catch (UnknownHostException e) {
            resultArea.setText(String.valueOf(e) + "\n");
            return;
        } catch (Exception e) {
            resultArea.appendText(host + " not listening on port "
                    + port + "\n");
        }


    }


    @FXML
    void clearBtn(ActionEvent event) {
        resultArea.setText("");
        urlName.setText("");

    }


    @FXML
    void startServer(ActionEvent event) {
        Stage stage = new Stage();
        Group root = new Group();
        Label lb11 = new Label("Server");
        lb11.setLayoutX(100);
        lb11.setLayoutY(100);

        lb12 = new Label("info");
        lb12.setLayoutX(100);
        lb12.setLayoutY(200);
        root.getChildren().addAll(lb11, lb12);
        Scene scene = new Scene(root, 600, 350);
        stage.setScene(scene);
        lb12.setText("Server is running and waiting for a client...");

        stage.setTitle("Server");
        stage.show();


        new Thread(this::runServer).start();

    }

    String message;

    private void runServer() {
        try {

            ServerSocket serverSocket = new ServerSocket(6666);
            updateServer("Server is running and waiting for a client...");
            while (true) { // Infinite loop
                try {
                    Socket clientSocket = serverSocket.accept();
                    updateServer("Client connected!");

                    new Thread(() -> {
                        try {
                            sleep(3000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    });
                    DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
                    DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream());

                    message = dis.readUTF();
                    updateServer("Message from client: " + message);

                    // Sending a response back to the client
                    dos.writeUTF("Received: " + message);

                    dis.close();
                    dos.close();

                } catch (IOException e) {
                    updateServer("Error: " + e.getMessage());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                if (message.equalsIgnoreCase("exit")) break;

            }
        } catch (IOException e) {
            updateServer("Error: " + e.getMessage());
        }
    }

    private void updateServer(String message) {
        // Run on the UI thread
        Platform.runLater(() -> lb12.setText(message + "\n"));
    }


    @FXML
    void startClient(ActionEvent event) {
        Stage stage = new Stage();
        Group root = new Group();
        Button connectButton = new Button("Connect to server");
        connectButton.setLayoutX(100);
        connectButton.setLayoutY(300);
        connectButton.setOnAction(this::connectToServer);
        // new Thread(this::connectToServer).start();

        Label lb11 = new Label("Client");
        lb11.setLayoutX(100);
        lb11.setLayoutY(100);
        msgText = new TextField("msg");
        msgText.setLayoutX(100);
        msgText.setLayoutY(150);

        lb122 = new Label("info");
        lb122.setLayoutX(100);
        lb122.setLayoutY(200);
        root.getChildren().addAll(lb11, lb122, connectButton, msgText);


        Scene scene = new Scene(root, 600, 350);
        stage.setScene(scene);
        stage.setTitle("Client");
        stage.show();


    }


    private void connectToServer(ActionEvent event) {


        try {
            socket1 = new Socket("localhost", 6666);

            DataOutputStream dos = new DataOutputStream(socket1.getOutputStream());
            DataInputStream dis = new DataInputStream(socket1.getInputStream());

            dos.writeUTF(msgText.getText());
            String response = dis.readUTF();
            updateTextClient("Server response: " + response + "\n");

            dis.close();
            dos.close();
            socket1.close();
        } catch (Exception e) {
            updateTextClient("Error: " + e.getMessage() + "\n");
        }


    }

    private void updateTextClient(String message) {
        // Run on the UI thread
        Platform.runLater(() -> lb122.setText(message + "\n"));
    }

    //----------------------------------------------------------------------

    final Set<PrintWriter> clientWriters = new HashSet<>();

    @FXML
    void startUser1(ActionEvent event) {
        startClientWindow("User1");
    }

    @FXML
    void startUser2(ActionEvent event) {
        startClientWindow("User2");
    }

    private void startClientWindow(String user) {
        Platform.runLater(() -> {
            Stage stage = new Stage();
            TextArea chat = new TextArea();
            chat.setEditable(false);
            TextField tfInput = new TextField();
            Button btnSend = new Button("Send");
            VBox vbox = new VBox(10, chat, tfInput, btnSend);
            Scene scene = new Scene(vbox, 400, 300);
            stage.setScene(scene);
            stage.setTitle(user);
            stage.show();
            new Thread(() -> {
                try (Socket socket = new Socket("localhost", 6667);
                     BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                     PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
                    btnSend.setOnAction(e -> {
                        String message = user + ": " + tfInput.getText();
                        out.println(message);
                        tfInput.clear();
                    });
                    String message;
                    while ((message = in.readLine()) != null) {
                        String finalMessage = message;
                        Platform.runLater(() -> chat.appendText(finalMessage + "\n"));
                    }
                } catch (IOException e) {
                    Platform.runLater(() -> chat.appendText("Connection error: " + e.getMessage() + "\n"));
                }
            }).start();
        });
    }

    private void startChatServer() {
        try (ServerSocket serverSocket = new ServerSocket(6667)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(() -> handleUser(clientSocket)).start();
            }
        } catch (IOException ignored) {

        }
    }

    private void handleUser(Socket clientSocket) {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            synchronized (clientWriters) {
                clientWriters.add(out);
            }
            String message;
            while ((message = in.readLine()) != null) {
                synchronized (clientWriters) {
                    for (PrintWriter writer : clientWriters) {
                        writer.println(message);
                    }
                }
            }
            clientSocket.close();
            synchronized (clientWriters) {
                clientWriters.remove(out);
            }
        } catch (IOException ignored) {

        }
    }
}
