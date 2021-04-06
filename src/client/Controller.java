package client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.TextField;

public class Controller {

    @FXML
    Canvas canvasField;

    GraphicsContext gc;

    @FXML
    public void initialize() {
        gc = canvasField.getGraphicsContext2D();
        DrawGrid();
    }

    private void DrawGrid() {
        gc.setLineWidth(2.0);

        double dy = canvasField.getHeight() / 3.0;
        double dx = canvasField.getWidth() / 3.0;
        double w = canvasField.getWidth();
        double h = canvasField.getHeight();

        gc.moveTo(0, dy);
        gc.lineTo(w, dy);
        gc.stroke();
    }

    public void btnConnectClick(ActionEvent actionEvent) {

    }
}
