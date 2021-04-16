package client;

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

    public ProcessServer(ServerConnection serverConnection, Canvas canvasField) {
        this.canvasField = canvasField;
        this.serverConnection = serverConnection;

        gc = canvasField.getGraphicsContext2D();

        dy = canvasField.getHeight() / 3.0;
        dx = canvasField.getWidth() / 3.0;
        w = canvasField.getWidth();
        h = canvasField.getHeight();
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
        while (true) {
            try {
                serverConnection.SendRequestToServer("getfield|9|9");
                field = serverConnection.ReceiveResponseFromServer();

                DrawGrid();
                DrawField();

                Thread.sleep(200);

            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
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

    private final String WIN_CROSS = "WinCross";
    private final String WIN_CRISS = "WinCriss";
    private final String DRAW = "Draw";
    private final String CONTINUE_GAME = "Continue";

    @FXML
    public void initialize() {

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
            }

            if (sign.equals("O") == true) {
                ShowDialog("Вы играете за O");
            }

            labelSign.setText(labelSign.getText()+sign);

            btnConnect.setDisable(true);

            ProcessServer processServer = new ProcessServer(serverConnection, canvasField);
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
            }
        } catch (Exception e) {
            ShowDialog(e.getMessage());
        }
    }
}
