package org.example.javafx;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import java.util.Random;

public class ETreeController {

    @FXML
    private Button voltarETree;

    @FXML
    private Canvas animCanvas;
    // canvas da animação de fundo

    @FXML
    private Pane topoPane;
    // pane onde estão os nós da topologia

    private final Random rng = new Random();
    private final int BEAM_COUNT = 20;
    private final double[] bx = new double[BEAM_COUNT];
    private final double[] by = new double[BEAM_COUNT];
    private final double[] angle = new double[BEAM_COUNT];
    private final double[] speed = new double[BEAM_COUNT];
    private final double[] length = new double[BEAM_COUNT];
    private final double[] width = new double[BEAM_COUNT];
    private final double[] alpha = new double[BEAM_COUNT];

    // cor verde do E-Tree
    private final Color[] colors = {
            Color.web("#00ffb4"),
            Color.web("#00e5ff"),
            Color.web("#64b4ff")
    };
    private final Color[] beamColor = new Color[BEAM_COUNT];

    @FXML
    private void initialize() {
        double w = animCanvas.getWidth();
        double h = animCanvas.getHeight();

        // inicializa os feixes de fundo
        for (int i = 0; i < BEAM_COUNT; i++) {
            bx[i] = rng.nextDouble() * w;
            by[i] = rng.nextDouble() * h;
            angle[i] = rng.nextDouble() * Math.PI * 2;
            speed[i] = 0.3 + rng.nextDouble() * 0.7;
            length[i] = 50 + rng.nextDouble() * 100;
            width[i] = 0.3 + rng.nextDouble() * 1.0;
            alpha[i] = 0.06 + rng.nextDouble() * 0.15;
            beamColor[i] = colors[rng.nextInt(colors.length)];
        }

        GraphicsContext gc = animCanvas.getGraphicsContext2D();

        new AnimationTimer() {
            @Override
            public void handle(long now) {
                gc.clearRect(0, 0, w, h);
                for (int i = 0; i < BEAM_COUNT; i++) {
                    double ex = bx[i] + Math.cos(angle[i]) * length[i];
                    double ey = by[i] + Math.sin(angle[i]) * length[i];
                    Color c = beamColor[i];
                    gc.setStroke(Color.color(c.getRed(), c.getGreen(), c.getBlue(), alpha[i]));
                    gc.setLineWidth(width[i]);
                    gc.strokeLine(bx[i], by[i], ex, ey);
                    bx[i] += Math.cos(angle[i]) * speed[i];
                    by[i] += Math.sin(angle[i]) * speed[i];
                    if (bx[i] < -200 || bx[i] > w+200 || by[i] < -200 || by[i] > h+200) {
                        bx[i] = rng.nextDouble() * w;
                        by[i] = rng.nextDouble() * h;
                        angle[i] = rng.nextDouble() * Math.PI * 2;
                    }
                }
            }
        }.start();

        // aguarda o layout ser calculado antes de desenhar
        javafx.application.Platform.runLater(() -> desenharArvoreEAnimar());
    }

    private void desenharArvoreEAnimar() {
        // centro de cada nó — layoutX + metade do ícone (28px)
        double[] root = {350 + 28, -30 + 28};  // Netflix (ROOT)

        double[][] leaves = {
                {150  + 28, 110 + 28},  // Cliente SP
                {250 + 28, 110 + 28},  // Cliente RJ
                {350 + 28, 110 + 28},  // Cliente BH
                {450 + 28, 110 + 28},  // Cliente POA
                {550 + 28, 110 + 28},  // Cliente BSB
        };

        // cria o Canvas das linhas atrás dos nós
        Canvas linhasCanvas = new Canvas(topoPane.getWidth(), topoPane.getHeight());
        topoPane.getChildren().add(0, linhasCanvas);

        GraphicsContext gc = linhasCanvas.getGraphicsContext2D();

        // desenha uma linha do ROOT para cada LEAF
        for (double[] leaf : leaves) {
            gc.setStroke(Color.web("#00ffb4").deriveColor(0, 1, 1, 0.2));
            gc.setLineWidth(1);
            gc.strokeLine(root[0], root[1], leaf[0], leaf[1]);
        }

        // anima pacotes do ROOT para cada LEAF com delay
        // no E-Tree o tráfego vai SOMENTE do Root para os Leaves
        for (int i = 0; i < leaves.length; i++) {
            animarPacoteETree(root, leaves[i], i * 0.4);
        }
    }

    private void animarPacoteETree(double[] origem, double[] destino, double delaySegundos) {
        // Circle = bolinha nativa do JavaFX — perfeitamente redonda
        Circle pacote = new Circle(4);
        pacote.setFill(Color.web("#00ffb4"));
        pacote.setOpacity(0.85);
        pacote.setVisible(false);

        // adiciona atrás dos nós mas na frente das linhas
        topoPane.getChildren().add(1, pacote);

        // delay para cada leaf começar em momento diferente
        javafx.animation.PauseTransition pausa = new javafx.animation.PauseTransition(
                javafx.util.Duration.seconds(delaySegundos)
        );

        pausa.setOnFinished(e -> {
            pacote.setVisible(true);

            javafx.animation.Timeline timeline = new javafx.animation.Timeline();

            // começa no ROOT
            javafx.animation.KeyFrame inicio = new javafx.animation.KeyFrame(
                    javafx.util.Duration.ZERO,
                    new javafx.animation.KeyValue(pacote.centerXProperty(), origem[0]),
                    new javafx.animation.KeyValue(pacote.centerYProperty(), origem[1])
            );

            // chega no LEAF em 1.5 segundos
            // no E-Tree o pacote NÃO volta — só vai do Root para o Leaf
            javafx.animation.KeyFrame chegada = new javafx.animation.KeyFrame(
                    javafx.util.Duration.seconds(1.5),
                    new javafx.animation.KeyValue(
                            pacote.centerXProperty(), destino[0],
                            javafx.animation.Interpolator.EASE_IN),
                    new javafx.animation.KeyValue(
                            pacote.centerYProperty(), destino[1],
                            javafx.animation.Interpolator.EASE_IN)
            );

            // volta invisível para o ROOT e repete
            // simula transmissão contínua de streaming
            javafx.animation.KeyFrame reset = new javafx.animation.KeyFrame(
                    javafx.util.Duration.seconds(2.6),
                    new javafx.animation.KeyValue(pacote.centerXProperty(), origem[0]),
                    new javafx.animation.KeyValue(pacote.centerYProperty(), origem[1])
            );

            timeline.getKeyFrames().addAll(inicio, chegada, reset);
            timeline.setCycleCount(javafx.animation.Animation.INDEFINITE);
            timeline.play();
        });

        pausa.play();
    }

    @FXML
    private void voltarMenu() throws IOException {
        Stage stage = (Stage) voltarETree.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MenuView.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        stage.show();

    }
}
