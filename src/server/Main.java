package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

class ProcessPlayer extends Thread {
    static void Log(String msg) {
        System.out.println(msg);
    }

    private DataInputStream inPlayer;
    private DataOutputStream outPlayer;
    private Controller controller;
    private String playerName;
    private char sign;


    public ProcessPlayer(DataInputStream inPlayer, DataOutputStream outPlayer, Controller controller, String playerName, char sign) {
        this.inPlayer = inPlayer;
        this.outPlayer = outPlayer;
        this.controller = controller;
        this.playerName = playerName;
        this.sign = sign;
    }

    public void run() {
        boolean doing = true;
        while (doing == true) {
            try {
                String request = inPlayer.readUTF();
                String response;


                String[] params = request.split("\\|");

                String command = params[0];
                int i = Integer.parseInt(params[1]);
                int j = Integer.parseInt(params[2]);

                switch (command) {
                    case "getfield":
                        Log("from " + playerName + ": " + request);

                        response = controller.GetFieldInString();

                        outPlayer.writeUTF(response);

                        Log("to " + playerName + ":\n" + response);
                        break;

                    case "setsign":
                        Log("from " + playerName + ": " + request);

                        boolean setSignResult = controller.SetSign(i, j, sign);

                        response = setSignResult == true ? "ok" : "error";

                        outPlayer.writeUTF(response);

                        Log("to " + playerName + ":" + response);
                        break;

                    case "currentstep":
                        Log("from " + playerName + ": " + request);

                        response = Integer.toString(controller.GetCurrentStep());

                        outPlayer.writeUTF(response);

                        Log("to " + playerName + ":" + response);
                        break;

                    case "gameresult":
                        Log("from " + playerName + ": " + request);

                        response = controller.GetGameResult();

                        outPlayer.writeUTF(response);

                        Log("to " + playerName + ":" + response);

                        if (response.equals("Continue") == false) {
                            doing = false;
                        }

                        break;
                }


            } catch (IOException e) {
                Log("some error: " + e.getMessage());
                doing = false;
            }
        }
    }
}

public class Main {
    static void Log(String msg) {
        System.out.println(msg);
    }

    public static void main(String[] args) throws IOException, InterruptedException {

        //System.in.read();

        while (true) {

            Controller controller = new Controller();
            controller.ClearField();

            ServerSocket listener = null;

            try {
                listener = new ServerSocket(37152, 1, InetAddress.getByName("127.0.0.1"));
                Log("server is started");
            } catch (Exception e) {
                Log("failed to start server: " + e.getMessage());
                return;
            }

            Log("server is listening");

            Socket talkingPlayer1 = null;
            Socket talkingPlayer2 = null;

            DataInputStream inPlayer1 = null;
            DataOutputStream outPlayer1 = null;

            DataInputStream inPlayer2 = null;
            DataOutputStream outPlayer2 = null;

            try {
                talkingPlayer1 = listener.accept();
                Log("player1 is connected");

                inPlayer1 = new DataInputStream(talkingPlayer1.getInputStream());
                outPlayer1 = new DataOutputStream(talkingPlayer1.getOutputStream());

                outPlayer1.writeUTF(Character.toString(controller.CROSS));

            } catch (Exception e) {
                Log("player1 error: " + e.getMessage());
                return;
            }
            ProcessPlayer processPlayer1 = new ProcessPlayer(inPlayer1, outPlayer1, controller, "player1", controller.CROSS);
            processPlayer1.start();


            try {
                talkingPlayer2 = listener.accept();
                Log("player2 is connected");

                inPlayer2 = new DataInputStream(talkingPlayer2.getInputStream());
                outPlayer2 = new DataOutputStream(talkingPlayer2.getOutputStream());

                outPlayer2.writeUTF(Character.toString(controller.CRISS));

            } catch (Exception e) {
                Log("player2 error: " + e.getMessage());
                return;
            }

            ProcessPlayer processPlayer2 = new ProcessPlayer(inPlayer2, outPlayer2, controller, "player2", controller.CRISS);
            processPlayer2.start();


            processPlayer1.join();
            processPlayer2.join();

            listener.close();
        }
    }
}
