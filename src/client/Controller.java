package client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;

public class Controller {

    @FXML
    Canvas canvasField;

    private GraphicsContext gc = null;
    private ServerConnection serverConnection = null;
    private String sign = null;
    private String field = null;


    @FXML
    public void initialize() {
        gc = canvasField.getGraphicsContext2D();
        DrawGrid();
    }

    private void ShowDialog(String message) {
        new Alert(Alert.AlertType.CONFIRMATION,message).showAndWait();
    }

    private void DrawGrid() {
        gc.setLineWidth(2.0);

        double dy = canvasField.getHeight() / 3.0;
        double dx = canvasField.getWidth() / 3.0;
        double w = canvasField.getWidth();
        double h = canvasField.getHeight();

        //line x 1
        gc.moveTo(0, dy);
        gc.lineTo(w, dy);
        gc.stroke();

        //line x 2
        gc.moveTo(0, dy*2);
        gc.lineTo(w, dy*2);
        gc.stroke();

        //line y 1
        gc.moveTo(dx, 0);
        gc.lineTo(dx, h);
        gc.stroke();

        //line y 2
        gc.moveTo(dx*2, 0);
        gc.lineTo(dx*2, h);
        gc.stroke();
    }

    public void btnConnectClick(ActionEvent actionEvent) {
        try{
            serverConnection = new ServerConnection();
            sign = serverConnection.ReceiveResponseFromServer();

            if(sign.equals("X")==true){
                ShowDialog("Вы играете за X - ожидаем подключение другого игрока");
            }

            if(sign.equals("O")==true){
                ShowDialog("Вы играете за O");
            }

            field = serverConnection.ReceiveResponseFromServer();

            ShowDialog(field);

        }catch (Exception e){
            ShowDialog(e.getMessage());
        }
    }
}
