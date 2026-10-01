package org.example.javafx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import java.util.Random;
import javafx.scene.layout.AnchorPane;

public class ELineController {

    @FXML
    private Button voltarELine;
    @FXML
    private Canvas animCanvas;
    @FXML
    private AnchorPane link1;
    @FXML
    private AnchorPane link2;
    @FXML
    private AnchorPane mainRoot;

    // posição dos pacotes (0.0 a 1.0 = progresso na linha)
    private double prog1 = 0.0;
    private double prog2 = 0.0;
    private static final double PACKET_SPEED = 0.004; // ajusta a velocidade aqui

    private final Random rng = new Random();
    // gerador de números aleatórios

    private final int BEAM_COUNT = 20;
    // menos feixes que o menu — a tela E-Line é mais limpa

    private final double[] bx = new double[BEAM_COUNT];
    private final double[] by = new double[BEAM_COUNT];
    private final double[] angle = new double[BEAM_COUNT];
    private final double[] speed = new double[BEAM_COUNT];
    private final double[] length = new double[BEAM_COUNT];
    private final double[] width = new double[BEAM_COUNT];
    private final double[] alpha = new double[BEAM_COUNT];

    private final Color[] colors = {
            Color.web("#00e5ff"),  // azul ciano
            Color.web("#64b4ff"),  // azul claro
            Color.web("#00ffb4")   // verde ciano
    };

    private final Color[] beamColor = new Color[BEAM_COUNT];

    @FXML
    private void initialize() {

        if(mainRoot != null) mainRoot.setSnapToPixel(true);
        link1.setSnapToPixel(true);
        link2.setSnapToPixel(true);

        double w = animCanvas.getWidth();
        double h = animCanvas.getHeight();

        for (int i = 0; i < BEAM_COUNT; i++) {
            bx[i] = rng.nextDouble() * w;
            by[i] = rng.nextDouble() * h;
            angle[i] = rng.nextDouble() * Math.PI * 2;
            speed[i] = 0.3 + rng.nextDouble() * 0.8;
            length[i] = 50 + rng.nextDouble() * 100;
            width[i] = 0.3 + rng.nextDouble() * 1.0;
            alpha[i] = 0.08 + rng.nextDouble() * 0.2;
            beamColor[i] = colors[rng.nextInt(colors.length)];
        }

        GraphicsContext gc = animCanvas.getGraphicsContext2D();

        new AnimationTimer() {
            @Override
            public void handle(long now) {
                gc.clearRect(0, 0, w, h);

                // desenha feixes
                for (int i = 0; i < BEAM_COUNT; i++) {
                    double ex = bx[i] + Math.cos(angle[i]) * length[i];
                    double ey = by[i] + Math.sin(angle[i]) * length[i];
                    Color c = beamColor[i];
                    gc.setStroke(Color.color(c.getRed(), c.getGreen(), c.getBlue(), alpha[i]));
                    gc.setLineWidth(width[i]);
                    gc.strokeLine(bx[i], by[i], ex, ey);
                    bx[i] += Math.cos(angle[i]) * speed[i];
                    by[i] += Math.sin(angle[i]) * speed[i];
                    if (bx[i] < -200 || bx[i] > w + 200 || by[i] < -200 || by[i] > h + 200) {
                        bx[i] = rng.nextDouble() * w;
                        by[i] = rng.nextDouble() * h;
                        angle[i] = rng.nextDouble() * Math.PI * 2;
                    }
                }

                // desenha pacotes no canvas usando bounds dos AnchorPanes
                desenharPacote(gc, link1, prog1, Color.web("#00e5ff"));
                desenharPacote(gc, link2, prog2, Color.web("#64b4ff"));

                // avança o progresso (vai e volta)
                prog1 = (prog1 + PACKET_SPEED) % 2.0;
                prog2 = (prog2 + PACKET_SPEED) % 2.0;
            }
        }.start();
    }

    private void desenharPacote(GraphicsContext gc, AnchorPane track, double prog, Color cor) {
        javafx.geometry.Bounds bounds = track.localToScene(track.getBoundsInLocal());

        double x = bounds.getMinX();
        double y = bounds.getMinY() + (bounds.getHeight() / 2.0) + 4;
        double largura = bounds.getWidth();

        // prog vai de 0 a 2: 0→1 = ida, 1→2 = volta
        double t = prog < 1.0 ? prog : 2.0 - prog;
        double px = x + t * largura;

        gc.setFill(Color.color(cor.getRed(), cor.getGreen(), cor.getBlue(), 0.85));
        gc.fillOval(px - 4, y - 4, 8, 8);
    }
    @FXML
    private void voltarMenu() throws IOException {
        Stage stage = (Stage) voltarELine.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MenuView.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        stage.show();

    }

}
