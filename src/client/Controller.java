package client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;

import java.io.IOException;

class ProcessServer extends Thread {
    private Canvas canvasField;
    private ServerConnection serverConnection;

    private GraphicsContext gc;
    private String field = null;

    private double dy, dx, w, h;
    private final char CRISS = 'O';
    private final char CROSS = 'X';

    private String sign = null;

    private final String WIN_CROSS = "WinCross";
    private final String WIN_CRISS = "WinCriss";
    private final String DRAW = "Draw";
    private final String CONTINUE_GAME = "Continue";

    private Button btnConnect;

    private void ShowDialog(String message) {
        Platform.runLater(new Runnable() {
            @Override public void run() {
                new Alert(Alert.AlertType.CONFIRMATION, message).showAndWait();


            }
        });
    }

    public ProcessServer(ServerConnection serverConnection, Canvas canvasField, String sign, Button btnConnect) {
        this.canvasField = canvasField;
        this.serverConnection = serverConnection;

        gc = canvasField.getGraphicsContext2D();

        dy = canvasField.getHeight() / 3.0;
        dx = canvasField.getWidth() / 3.0;
        w = canvasField.getWidth();
        h = canvasField.getHeight();

        this.sign = sign;

        this.btnConnect = btnConnect;
    }

    private void DrawGrid() {
        gc.setLineWidth(2.0);

        //line x 1
        gc.moveTo(0, dy);
        gc.lineTo(w, dy);
        gc.stroke();

        //line x 2
        gc.moveTo(0, dy * 2);
        gc.lineTo(w, dy * 2);
        gc.stroke();

        //line y 1
        gc.moveTo(dx, 0);
        gc.lineTo(dx, h);
        gc.stroke();

        //line y 2
        gc.moveTo(dx * 2, 0);
        gc.lineTo(dx * 2, h);
        gc.stroke();
    }

    private void DrawField() {
        String[] lines = field.split("\n");

        int fieldSize = 3;
        char[][] field = new char[fieldSize][fieldSize];

        for (int l = 0; l < fieldSize; l++) {
            for (int e = 0; e < fieldSize; e++) {
                field[l][e] = lines[l].charAt(e);
            }
        }

        gc.setFont(new Font("Arial", dy / 2));

        for (int i = 0; i < fieldSize; i++) {
            for (int j = 0; j < fieldSize; j++) {
                if (field[i][j] == CROSS) {
                    gc.fillText(Character.toString(CROSS), j * dx + dx / 3, i * dy + 2 * dy / 3);
                }
                if (field[i][j] == CRISS) {
                    gc.fillText(Character.toString(CRISS), j * dx + dx / 3, i * dy + 2 * dy / 3);
                }
            }
        }
    }

    public void run() {
        boolean doing = true;
        while (doing == true) {
            try {
                serverConnection.SendRequestToServer("getfield|9|9");
                field = serverConnection.ReceiveResponseFromServer();

                DrawGrid();
                DrawField();

                serverConnection.SendRequestToServer("currentstep|9|9");
                int currentStep = Integer.parseInt(serverConnection.ReceiveResponseFromServer());

                if(sign.equals("X")==true && currentStep%2==1){
                    canvasField.setDisable(false);
                }

                if(sign.equals("O")==true && currentStep%2==0){
                    canvasField.setDisable(false);
                }

                serverConnection.SendRequestToServer("gameresult|9|9");
                String gameResult = serverConnection.ReceiveResponseFromServer();

                if(gameResult.equals(CONTINUE_GAME)==false){
                    canvasField.setDisable(true);

                    switch (gameResult)
                    {
                        case WIN_CRISS:
                            ShowDialog("Победил O");
                            break;
                        case WIN_CROSS:
                            ShowDialog("Победил X");
                            break;
                        case DRAW:
                            ShowDialog("Ничья");
                            break;
                    }

                    doing=false;
                    btnConnect.setDisable(false);
                    canvasField.setDisable(true);

                }

                Thread.sleep(500);

            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
                doing=false;
            }
        }
    }
}

public class Controller {

    @FXML
    Canvas canvasField;

    @FXML
    Button btnConnect;

    @FXML
    Label labelSign;

    private ServerConnection serverConnection = null;
    private String sign = null;



    @FXML
    public void initialize() {
        canvasField.setDisable(true);
    }

    private void ShowDialog(String message) {
        new Alert(Alert.AlertType.CONFIRMATION, message).showAndWait();
    }


    public void btnConnectClick(ActionEvent actionEvent) {
        try {
            serverConnection = new ServerConnection();
            sign = serverConnection.ReceiveResponseFromServer();

            if (sign.equals("X") == true) {
                ShowDialog("Вы играете за X - ожидаем подключение другого игрока");
                canvasField.setDisable(false);
            }

            if (sign.equals("O") == true) {
                ShowDialog("Вы играете за O");
            }

            GraphicsContext gc = canvasField.getGraphicsContext2D();
            gc.clearRect(0,0,canvasField.getWidth(),canvasField.getHeight());

            labelSign.setText("Вы играете за: "+sign);

            btnConnect.setDisable(true);

            ProcessServer processServer = new ProcessServer(serverConnection, canvasField, sign, btnConnect);
            processServer.start();

        } catch (Exception e) {
            ShowDialog(e.getMessage());
        }
    }

    public void canvasFieldClicked(MouseEvent mouseEvent) {

        try {
            int j = (int) ((mouseEvent.getSceneX() - canvasField.getLayoutX()) / (canvasField.getWidth() / 3.0));
            int i = (int) ((mouseEvent.getSceneY() - canvasField.getLayoutY()) / (canvasField.getHeight() / 3.0));

            serverConnection.SendRequestToServer("setsign|" + i + "|" + j);

            String setSignResult = serverConnection.ReceiveResponseFromServer();

            if (setSignResult.equals("error") == true) {
                ShowDialog("Неверный ход походите ещё");
            }else{
                canvasField.setDisable(true);
            }
        } catch (Exception e) {
            ShowDialog(e.getMessage());
        }
    }
}
