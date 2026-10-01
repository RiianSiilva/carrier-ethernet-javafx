package org.example.javafx;

import javafx.animation.AnimationTimer;
// AnimationTimer = classe do JavaFX que executa um bloco de código a cada frame
// É como um loop infinito sincronizado com a taxa de atualização da tela (60fps)

import javafx.fxml.FXML;
// Habilita a anotação @FXML, que liga variáveis e métodos Java aos elementos do FXML

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
// Canvas = a "tela em branco" onde vamos desenhar a animação
// É o mesmo elemento que colocamos no MenuView.fxml com fx:id="animCanvas"

import javafx.scene.canvas.GraphicsContext;
// GraphicsContext = o "pincel" do Canvas
// É através dele que desenhamos linhas, formas, cores no Canvas

import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
// Color = classe que representa cores no JavaFX
// Usamos Color.web("#00e5ff") para criar cores a partir de código hexadecimal

import java.io.IOException;
import java.util.Random;
// Random = gerador de números aleatórios do Java
// Usamos para sortear posição, ângulo, velocidade e cor de cada feixo de luz

public class MenuController {
    // classe publica - o FXMLLoader precisa que seja publica para instanciar

    @FXML
    private Canvas animCanvas;
    // @FXML diz ao javaFx: "injete aqui o Canvas cujo px:id ='animCanvas' no FXML"
    // sem o @FXML, esta variavel ficaria null e a animacao nao funcionaria

    @FXML
    private Button btnELan;
    @FXML
    private Button btnETree;
    @FXML
    private Button btnELine;

    private final Random rng = new Random();
    // instancia do gerador de numeros aleatorios
    // "final" = esta variavel nuncavai apontar para outro obj depois de criada

    private final int BEAM_COUNT = 30;
    // quantidade de feixo de luz na animacao
    // aumente para mais feixes, diminua parar menos - afeta o desempenho

    // arrays paralelos: cada indice i representa um feixo de luz
    // usar arrays separados é mais eficiente que criar 30 objs Beam
    private final double[] bx = new double[BEAM_COUNT];
    // bx[i] = posição X atual do feixo i (horizontal, da esquerda para direita)

    private final double[] by = new double[BEAM_COUNT];
    // by[i] posicao y atualfo feixo i (vertical, de cima para baixo)

    private final double[] angle = new double[BEAM_COUNT];
    // angle[i] = angulo de movimento do feixo i em radianos
    // 0 = direita, PI/2 = baixo, PI = esquerda, 3PI/2 = cima

    private final double[] speed = new double[BEAM_COUNT];
    // speed[i] = quantos pixels o feixo i avnca por frame

    private final double[] length = new double[BEAM_COUNT];
    // lengh[i] = comprimento do feixo i em pixels

    private final double[] width = new double[BEAM_COUNT];
    // width[i] = espessura da linha do feixo i em pixels

    private final double[] alpha = new double[BEAM_COUNT];
    // alpha[i] = tranparencia do feixo i
    // 0.0 = invisivel. 1.0 = totalmente opaco

    private final Color[] colors = {
            Color.web("#00e5ff"), // azul ciano - cor do botao E-Line
            Color.web("#64b4ff"), // azul claro - cor do botao E-LAN
            Color.web("#00ffb4") // verde ciano - cor do botao E-Tree
    };
    // Paleta de cores possíveis para os feixes
    // Color.web() aceita qualquer cor em formato hexadecimal (#RRGGBB)

    private final Color[] beamColor = new Color[BEAM_COUNT];
    // beamColor[i] = qual cor da paleta o feixo i vai usar
    // cada feixo recebe uma cor aleatoria do array colors[] acima

    @FXML
    private void initialize(){
        // initialize() é chamado AUTOMATICAMENTE pelo FXMLLoader depois de injetar todos os @FXML
        // É aqui que configuramos a animação — equivalente ao "setup" do Processing ou Unity

        double w = animCanvas.getWidth();
        // Pega a largura do Canvas em pixels (definida no FXML como width="800")

        double h = animCanvas.getHeight();
        // Pega a altura do Canvas em pixels (definida no FXML como height="520")

        // LOOP DE INICIALIZAÇÃO: configura cada um dos 30 feixes com valores aleatórios
        for (int i = 0; i < BEAM_COUNT; i++){

            bx[i] = rng.nextDouble() * w;
            // nextDouble() gera número entre 0.0 e 1.0
            // Multiplicando por w, gera posição X aleatória dentro da largura da tela

            by[i] = rng.nextDouble() * h;
            // Mesmo conceito para posição Y dentro da altura da tela

            angle[i] = rng.nextDouble() * Math.PI * 2;
            // Math.PI * 2 = 6.28... = 360 graus em radianos
            // Gera um ângulo aleatório em qualquer direção (0 a 360 graus)

            speed[i] = 0.5 + rng.nextDouble() * 1.5;
            // Velocidade entre 0.5 e 2.0 pixels por frame
            // O 0.5 garante que nenhum feixo fique parado (velocidade mínima)

            length[i] = 60 + rng.nextDouble() * 140;
            // Comprimento entre 60 e 200 pixels
            // Feixes com comprimentos variados ficam mais naturais

            width[i] = 0.5 + rng.nextDouble() * 1.5;
            // espessura entre 0.5 e 2.0 pixels
            // linhas finas parecem feixes de luz distantes

            alpha[i] = 0.2 + rng.nextDouble() * 0.5;
            // Transparência entre 0.2 e 0.7
            // Nenhum feixo é totalmente opaco — mantém o visual sutil

            beamColor[i] = colors[rng.nextInt(colors.length)];
            // nextInt(3) gera 0, 1 ou 2 — índices válidos da array colors[]
            // Cada feixo recebe uma das 3 cores aleatoriamente

        }

        GraphicsContext gc = animCanvas.getGraphicsContext2D();
        // pega o pincel do canvas
        // todos os elementos de desenho sao feitos atraves deste obj

        // ANIMATION TIMER: é o coracao da animacao
        // o metodo handle() é chamado automaticamente a cada frame (-60x por segundo)
        new AnimationTimer() {
            @Override
            public void handle(long now){
              //"now" = tempo atual em nanosegundos - podemos usar para calcular delta time
              // por simpliciade, nao usamos "now" aqui

              gc.clearRect(0,0,w,h);
                //LIMPA o cnavas inteiro antes de desenhar o proximo frame
                //sem isso, os feixes deixariam rastros e a tela ficara toda pintada
                // clearReact(x,y,largura,altura) - limpamos da posicao (0,0) ate (w,h)

                //Loop de desenho: atualiza e desenha cada feixo
                for(int i = 0; i < BEAM_COUNT; i++){

                    double ex = bx[i] + Math.cos(angle[i]) * length[i];
                    // Calcula o ponto final X do feixo usando trigonometria
                    // Math.cos(ângulo) = componente horizontal da direção
                    // Multiplicando pelo comprimento, chega ao ponto final

                    double ey = by[i] + Math.sin(angle[i]) * length[i];
                    // Calcula o ponto final Y do feixo
                    // Math.sin(ângulo) = componente vertical da direção

                    Color c = beamColor[i];
                    //pega a cor deste feixo

                    gc.setStroke(Color.color(c.getRed(), c.getGreen(), c.getBlue(), alpha[i]));
                    // Define a cor da linha a ser desenhada
                    // Color.color(r, g, b, alpha) — todos os valores entre 0.0 e 1.0
                    // c.getRed/Green/Blue() retorna os componentes da cor
                    // alpha[i] = transparência individual de cada feixo

                    gc.setLineWidth(width[i]);
                    // define a espessura da linha deste eixo

                    gc.strokeLine(bx[i],by[i],ex, ey );
                    // desenha a linha do ponto inicial (bx, by) ate o ponto final (ex, ey)

                    bx[i] += Math.cos(angle[i] * speed[i]);
                    // move o feixo: avanca a posicao x na direcao do angulo
                    // += significa: bx[i] = bx[i] + incremento

                    by[i] += Math.sin(angle[i]) * speed[i];
                    // move o feixo: avanca a posicao y na direcao do angulo

                    //VERIFICACAO DE SAIDA DA TELA
                    // se o feixo saiu completamente da tela (com margem de 200px)....
                    if(bx[i] < -200 || bx[i] > w + 200 || by[i] < -200 || by[i] > h + 200){

                        bx[i] = rng.nextDouble() * w;
                        by[i] = rng.nextDouble() * h;
                        angle[i] = rng.nextDouble() * Math.PI * 2;
                        // ...reposiciona em lugar e direção aleatórios dentro da tela
                        // Isso cria o efeito "infinito" — sempre há feixes aparecendo
                    }

                }
            }
        }.start();
        // .start() inicia o AnimationTimer — a partir daqui handle() é chamado tdo frame

    }

    // Metodo chamado quando o botão E-Line é clicado (onAction="#openELine" no FXML)
    // (Stage) = metodo cast
    //  id = btnElan
    // getScene() = retorna uma janela especifica
    // getWindow() = pegar uma window = uma classe pai que representa qualquer tipo de janela
    // getClass() → pega a classe atual (MenuController)
    // getResource() → busca o arquivo dentro do pacote dessa classe
    // ("ELanView.fxml") → o arquivo que queremos carregar

    @FXML
    private void openELine() throws IOException {
       Stage stage = (Stage) btnELine.getScene().getWindow();
       FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ELineView.fxml"));
       Scene scene = new Scene(fxmlLoader.load());
       stage.setScene(scene);
       stage.show();
    }

    @FXML
    private void openELan() throws IOException {

        Stage stage = (Stage) btnELan.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ELanView.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        stage.show();

    }

    @FXML
    private void openETree() throws IOException{
        Stage stage = (Stage) btnETree.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ETreeView.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        stage.show();
    }





}
