package org.example.javafx;
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


public class ELanController {

    @FXML
    private Canvas animCanvas;
    // canvas da animação de fundo

    @FXML
    private javafx.scene.layout.Pane topoPane;
    // o Pane onde estão os nós da topologia — vamos desenhar as linhas aqui

    @FXML
    private Button voltarELan; // id

    private final Random rng = new Random();
    private final int BEAM_COUNT = 20;
    private final double[] bx = new double[BEAM_COUNT];
    private final double[] by = new double[BEAM_COUNT];
    private final double[] angle = new double[BEAM_COUNT];
    private final double[] speed = new double[BEAM_COUNT];
    private final double[] length = new double[BEAM_COUNT];
    private final double[] width = new double[BEAM_COUNT];
    private final double[] alpha = new double[BEAM_COUNT];
    private final Color[] colors = {
            Color.web("#64b4ff"),
            Color.web("#00e5ff"),
            Color.web("#00ffb4")
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

        // aguarda o layout ser calculado antes de desenhar as linhas
        javafx.application.Platform.runLater(() -> desenharLinhasEAnimar());
    }

    private void desenharLinhasEAnimar() {
        double[][] nos = {
                {330 + 28, 60  + 28},  // Matriz
                {330 + 28, -35 + 28},  // Agência SP
                {330 + 28, 168 + 28},  // Agência RJ
                {240 + 28, 60  + 28},  // Agência BH
                {420 + 28, 60  + 28},  // Agência POA
        };

        Canvas linhasCanvas = new Canvas(topoPane.getWidth(), topoPane.getHeight());
        topoPane.getChildren().add(0, linhasCanvas);
        GraphicsContext gc = linhasCanvas.getGraphicsContext2D();

        // linhas um pouco mais visíveis
        for (int i = 0; i < nos.length; i++) {
            for (int j = i + 1; j < nos.length; j++) {
                gc.setStroke(Color.web("#64b4ff").deriveColor(0, 1, 1, 0.25));
                gc.setLineWidth(1);
                gc.strokeLine(nos[i][0], nos[i][1], nos[j][0], nos[j][1]);
            }
        }

        // cada rota com delay diferente — evita sincronização
        animarPacoteELan(nos[0], nos[1], "#64b4ff", 0);      // Matriz → SP
        animarPacoteELan(nos[0], nos[2], "#64b4ff", 0.5);    // Matriz → RJ
        animarPacoteELan(nos[0], nos[3], "#00e5ff", 1.0);    // Matriz → BH
        animarPacoteELan(nos[0], nos[4], "#00e5ff", 1.5);    // Matriz → POA
        animarPacoteELan(nos[1], nos[3], "#00ffb4", 2.0);    // SP → BH
        animarPacoteELan(nos[2], nos[4], "#00ffb4", 2.5);    // RJ → POA
    }

    private void animarPacoteELan(double[] origem, double[] destino, String cor, double delaySegundos) {
        // por isso — Circle é nativamente redondo
        javafx.scene.shape.Circle pacote = new javafx.scene.shape.Circle(4);
        // 4 = raio em pixels, então o círculo tem 8px de diâmetro
        pacote.setFill(Color.web(cor));
        pacote.setOpacity(0.85);
        pacote.setVisible(false);
        topoPane.getChildren().add(1, pacote);

        // delay antes de começar — cada pacote começa em momento diferente
        javafx.animation.PauseTransition pausa = new javafx.animation.PauseTransition(
                javafx.util.Duration.seconds(delaySegundos)
        );

        pausa.setOnFinished(e -> {
            // torna visível depois do delay
            pacote.setVisible(true);

            javafx.animation.Timeline timeline = new javafx.animation.Timeline();

            // começa na origem
            javafx.animation.KeyFrame inicio = new javafx.animation.KeyFrame(
                    javafx.util.Duration.ZERO,
                    new javafx.animation.KeyValue(pacote.layoutXProperty(), origem[0]),
                    new javafx.animation.KeyValue(pacote.layoutYProperty(), origem[1])
            );

            // chega no destino em 2 segundos — mais lento, mais elegante
            javafx.animation.KeyFrame chegada = new javafx.animation.KeyFrame(
                    javafx.util.Duration.seconds(2.0),
                    new javafx.animation.KeyValue(
                            pacote.layoutXProperty(), destino[0],
                            javafx.animation.Interpolator.EASE_BOTH
                    ),
                    new javafx.animation.KeyValue(
                            pacote.layoutYProperty(), destino[1],
                            javafx.animation.Interpolator.EASE_BOTH
                    )
            );

            // volta para origem em mais 2 segundos
            javafx.animation.KeyFrame volta = new javafx.animation.KeyFrame(
                    javafx.util.Duration.seconds(4.0),
                    new javafx.animation.KeyValue(
                            pacote.layoutXProperty(), origem[0],
                            javafx.animation.Interpolator.EASE_BOTH
                    ),
                    new javafx.animation.KeyValue(
                            pacote.layoutYProperty(), origem[1],
                            javafx.animation.Interpolator.EASE_BOTH
                    )
            );

            timeline.getKeyFrames().addAll(inicio, chegada, volta);
            timeline.setCycleCount(javafx.animation.Animation.INDEFINITE);
            timeline.play();
        });

        pausa.play();
    }
    @FXML
    private void voltarMenu() throws IOException {

        Stage stage = (Stage) voltarELan.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MenuView.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        stage.show();

    }


}
