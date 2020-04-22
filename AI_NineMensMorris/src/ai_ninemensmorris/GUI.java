/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ai_ninemensmorris;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.VPos;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;


/**
 *
 * @author prada
 */
public class GUI {
    
    // Cenas de todas as telas do jogo 
    Scene introScene;
    Scene playScene;
    Scene rulesScene;
        
    /*Parâmetros para as posições do tabuleiro e características gerais da apresentação da janela do jogo*/
    double cemitery [] = {700, 50}; //{X,Y}
    double posX = 100;
    double posY = 100;
    double espessura = 10; // Espessura das linhas das figuras da imagem
    double radius = 20; // Raio dos círculos que representam as peças
    double tam_botao = 15; /* parâmetro utilizado para colocar botões no lugar correto. Tem relação com metade do valor de pixels do diâmetro do botão no StyleSheet*/
    Color board = Color.BLACK; // Cor dos elementos do tabuleiro
    double duracao = 700;
    double matriz_posicoes[][] = new double[2][24]; // Posições na tela das posições do tabuleiro! Linha 0 - posição X, Linha 1 - posição Y. Colunas de 0 a 23: as 24 casas do tabuleiro
    boolean neigborsSelected = false; // Caso um nó já tem suas vizinhas mostradas na tela
    int posicoesBrancas[] = new int [9]; // posições das peças brancas no tabuleiro
    int posicoesPretas[] = new int [9]; //posições da peças brancas no tabuleiro
    int posicoesVizinhancas[] = new int[4]; // posições das vizinhanças no tabuleiro
    int posicoesSuperVizinhancas[] = new int[24]; // posições das super vizinhanças no tabuleiro
    final double origin [] = {50,720}; //Localização dos círculos de vizinhança
    final double originSuper [] = {140,720}; // Localização dos círculos da vizinhança no modo super
    final double originLastMove [] = {655,720}; // Localização do círculo representando o último movimento
    int pecaSelected = -1; // Peca a qual está sendo mostrada sua vizinhança. Caso se -1, é pq não está sendo usado
    
    Button botoes[] = new Button[24]; // Declaração dos 24 botões do tabuleiro
    Circle pecasBrancas[] = new Circle[9]; // Declaração das 9 peças brancas
    Circle pecasMarrom[] = new Circle[9]; // Declaração das 9 peças pretas
    Circle vizinhos []= new Circle[4]; // No máximo, uma casa possui 4 vizinhos. Círculos vermelhos
    Circle superVizinhos[] = new Circle[24]; // A situação com mais casas vazias seria se os dois jogadores estiverem com 3 pecas e portanto 18 casas vazias, entretanto usou-se 24 pois existe um for que dava VectorOutOfBounds
    Circle ultimoMovimento[] = new Circle[2]; // Círculo rosa para indicar último movimento feito
    //TranslateTransition animBranco [] = new TranslateTransition[9];
    //TranslateTransition animPreto [] = new TranslateTransition[9];
    
    Tabuleiro plateau = new Tabuleiro();
    IA ser = new IA(plateau);
    
    public GUI(){
    }
    
    // Inicializando a tela de início do jogo
    public void setUpIntro(Stage primaryStage){
        
        // Criando a imagem de fundo da cena
        Image image = new Image("/image/game.jpg", 1500, 1000, true, true);
        ImageView fundo = new ImageView(image);
        
        // Criando o texto que será o título da tela
        Text texto = new Text(20,50,"Trabalho de IA\nNine Men's Morris");
        texto.setWrappingWidth(1000);
        texto.setFont(Font.font("Ubuntu Light", FontWeight.EXTRA_BOLD, FontPosture.REGULAR, 50));
        texto.setTextOrigin(VPos.TOP);
        texto.setTextAlignment(TextAlignment.JUSTIFY);
        texto.getStyleClass().add("emphasized-text");
        
        // Criando o botão que ao ser clicado resultará na tela de jogo e motivo pelo qual se passou o stage como argumento para este método
        Button play = new Button();
        play.setText("Play");
        play.setLayoutX(640);
        play.setLayoutY(340);
        play.setOnAction(new EventHandler<ActionEvent>() {
            
            @Override
            public void handle(ActionEvent event) {
                primaryStage.setScene(playScene);
            }
        });
        
        Group content = new Group(texto, play);
        Group cena = new Group(fundo, content);
        
        
        introScene = new Scene(cena, 1200, 800);
        introScene.getStylesheets().add("/estilo10.css");
   }
    
   private void moverPecasPrimeiraFase(boolean turno, int posicaoDestino){
        if(turno){
            if(plateau.adicionarUmaPeca(posicaoDestino, Tabuleiro.BRANCO)){ // Checa se a adição de uma peça deu errado
                System.out.println("Todas as peças já foram colocadas");
            }else{
                posicoesBrancas[plateau.pecaLivre[Tabuleiro.BRANCO]+1] = posicaoDestino; // Como a variável peçaLivre começa em 8 e é descrecida no método adicionaPeca(). Então, ela serve para nos dizer qual peça começará a ser movimentada
                pecasBrancas[plateau.pecaLivre[Tabuleiro.BRANCO]+1].setLayoutX(matriz_posicoes[0][posicaoDestino]); //Deslocando a peça para a posição de destino
                pecasBrancas[plateau.pecaLivre[Tabuleiro.BRANCO]+1].setLayoutY(matriz_posicoes[1][posicaoDestino]); //Deslocando a peça para a posição de destino
                ultimoMovimento[1].setLayoutX(matriz_posicoes[0][posicaoDestino]); // Marcando com círculo verde o ultimo movimento feito
                ultimoMovimento[1].setLayoutY(matriz_posicoes[1][posicaoDestino]); // Marcando com círculo verde o ultimo movimento feito
                //System.out.println("A peça branca "+ (plateau.pecaLivre[Tabuleiro.BRANCO]+1) + "está na posição " + posicaoDestino);
            }
        }else{
            Triple movimentoIA = IA.melhorMovimento(plateau, Tabuleiro.PRETO);
            posicaoDestino = movimentoIA.x;
            System.out.printf ("movimento dado pela IA = %d %d %d %c", movimentoIA.x, movimentoIA.y, movimentoIA.z, 10);
            
            // Adicionar uma peça
            if (movimentoIA.x == movimentoIA.y){
                plateau.adicionarUmaPeca(movimentoIA.x, Tabuleiro.PRETO);
                posicoesPretas[plateau.pecaLivre[Tabuleiro.PRETO]+1] = posicaoDestino; // Como a variável peçaLivre começa em 8 e é descrecida no método adicionaPeca(). Então, ela serve para nos dizer qual peça começará a ser movimentada
                pecasMarrom[plateau.pecaLivre[Tabuleiro.PRETO]+1].setLayoutX(matriz_posicoes[0][posicaoDestino]); //Deslocando a peça para a posição de destino
                pecasMarrom[plateau.pecaLivre[Tabuleiro.PRETO]+1].setLayoutY(matriz_posicoes[1][posicaoDestino]); //Deslocando a peça para a posição de destino
                ultimoMovimento[1].setLayoutX(matriz_posicoes[0][posicaoDestino]); // Marcando com círculo verde o ultimo movimento feito
                ultimoMovimento[1].setLayoutY(matriz_posicoes[1][posicaoDestino]); // Marcando com círculo verde o ultimo movimento feito
                
                if (plateau.ocorreuMoinho()){
                    int removerPosicao = movimentoIA.z;
                    plateau.removerPeca(removerPosicao, false, Tabuleiro.PRETO);
                    
                    int idPecaRemovida = encontra_ID_com_POSICAO(Tabuleiro.BRANCO, removerPosicao);
                    pecasBrancas[idPecaRemovida].setLayoutX(cemitery[0]);
                    pecasBrancas[idPecaRemovida].setLayoutY(cemitery[1]);
                    posicoesBrancas[idPecaRemovida] = -1;
                    cemitery[1] += 50;
                    // Desativando o modo remoção de peças
                    plateau.modoRemocao = false;
                    plateau.alguemGanhou(checaCasasLivres(plateau.turno));// Checando se alguem ganhou ou ativando um super modo
                    plateau.remocaoProperty.set("A IA é muito forte! Cuidado...");// Atualizando a mensagem na tela do jogo
                    
                    System.out.printf ("idPecaRemovida = %d %c", idPecaRemovida, 10);
                    
                }
                plateau.trocaTurno();// Método que troca de turno 
                
            }else {
                System.out.println("Todas as peças pretas já foram colocadas");
            }
            /*
            if(plateau.adicionarUmaPeca(posicaoDestino, Tabuleiro.PRETO)){ // Checa se a adição de uma peça deu errado
                System.out.println("Todas as peças pretas já foram colocadas");
            }else{
                posicoesPretas[plateau.pecaLivre[Tabuleiro.PRETO]+1] = posicaoDestino; // Como a variável peçaLivre começa em 8 e é descrecida no método adicionaPeca(). Então, ela serve para nos dizer qual peça começará a ser movimentada
                pecasMarrom[plateau.pecaLivre[Tabuleiro.PRETO]+1].setLayoutX(matriz_posicoes[0][posicaoDestino]); //Deslocando a peça para a posição de destino
                pecasMarrom[plateau.pecaLivre[Tabuleiro.PRETO]+1].setLayoutY(matriz_posicoes[1][posicaoDestino]); //Deslocando a peça para a posição de destino
            } */  
        }

        if(plateau.pecaLivre[Tabuleiro.BRANCO] <= -1 && plateau.pecaLivre[Tabuleiro.PRETO] <= -1){ // Checando se todas as peças já foram colocadas
            plateau.fase1 = false;
            plateau.fase2 = true;
            plateau.FASE_DO_JOGO = 2; //***Alterei aqui
            plateau.alguemGanhou(checaCasasLivres(plateau.turno));//checa se ao se começar a segunda fase, se o jogador já está travado
        }   
    }
    
   private void voarPecas(boolean turno, int idPeca){
        if(!neigborsSelected){// Caso nenhuma vizinhança esteja selecionada
            if(turno){ // Vez das brancas
                for(int i = 0; i< 24; i++){//Percorre todas as posicoes vazias do tabuleiro. E coloca os círculos das supervizinhanças nos que estão vazios
                    // Aqui que se poderia colocar um contador para ter que instanciar somente 18 super vizinhanças
                    if( plateau.getPosicao(i) == Tabuleiro.VAZIO){// Se o tabuleiro estiver vazio em uma casa, uma superVizinhança será colocada ali
                        posicoesSuperVizinhancas[i] = i;
                        superVizinhos[i].setLayoutX(matriz_posicoes[0][i]);
                        superVizinhos[i].setLayoutY(matriz_posicoes[1][i]);
                    }else{ // Caso a casa esteja ocupada se deixa a super vizinhança na sua origem
                        posicoesSuperVizinhancas[i] = -1;
                        superVizinhos[i].setLayoutX(originSuper[0]);
                        superVizinhos[i].setLayoutY(originSuper[1]);
                    }   
                }
            }else{ // Vez das pretas
                    for(int i = 0; i< 24; i++){//Percorre todas as posicoes vazias do tabuleiro
                        // Aqui que se poderia colocar um contador para ter que instanciar somente 18 super vizinhanças
                        if(plateau.getPosicao(i) == Tabuleiro.VAZIO){// Se o tabuleiro estiver vazio em uma casa, uma superVizinhança será colocada ali
                            posicoesSuperVizinhancas[i] = i;
                            superVizinhos[i].setLayoutX(matriz_posicoes[0][i]);
                            superVizinhos[i].setLayoutY(matriz_posicoes[1][i]);
                        }else{ // Caso a casa esteja ocupada se deixa a super vizinhança na sua origem
                            posicoesSuperVizinhancas[i] = -1;
                            superVizinhos[i].setLayoutX(originSuper[0]);
                            superVizinhos[i].setLayoutY(originSuper[1]);
                        }   
                    }    
            }
            pecaSelected = idPeca;
            neigborsSelected = true;
            //System.out.println("Peca selecionada: " + idPeca + " Neighbors selecionados: " + neigborsSelected);
        }else{// Caso alguma vizinhança esteja selecionada. Teremos que clicar na peça para desaparecer com as suas vizinhanças 
            if(idPeca == pecaSelected){
                for(int j = 0; j<24; j++){
                    superVizinhos[j].setLayoutX(originSuper[0]);
                    superVizinhos[j].setLayoutY(originSuper[1]);
                }
                neigborsSelected = false;
                pecaSelected = -1;
            }

        }
    }
   
   // Movendo as peças durante a segunda fase de jogo
    private void moverPecasSegundaFase(boolean turno, int idPeca){
        // Para as BRANCAS jogarem com FASE 3 basta tirar o comentário abaixo
        // if(plateau.superModeB && turno){ // Caso as brancas estejam em super Modo
        //    voarPecas(turno, idPeca);
        //    return;
        //}
        // ***Alterei. Aqui havia um if para pretas entrando em supermodo
        if(!neigborsSelected){
            int counter = 0;
            if(turno){
                //System.out.println("Vez brancas vizinhanca");
                for(int i = 0; i < Tabuleiro.CASAS_VIZINHAS[posicoesBrancas[idPeca]].length; i++){ // Percorre-se pelas 4 vizinhanças da peça e se estiver vazia se mostra a vizinhança, se não, a vizinhança fica na origem 
                    if(Tabuleiro.CASAS_VIZINHAS[posicoesBrancas[idPeca]][i] > -1 && Tabuleiro.CASAS_VIZINHAS[posicoesBrancas[idPeca]][i] < 24 && plateau.getPosicao(Tabuleiro.CASAS_VIZINHAS[posicoesBrancas[idPeca]][i]) == Tabuleiro.VAZIO){
                        posicoesVizinhancas[i] = Tabuleiro.CASAS_VIZINHAS[posicoesBrancas[idPeca]][i];
                        vizinhos[i].setLayoutX(matriz_posicoes[0][Tabuleiro.CASAS_VIZINHAS[posicoesBrancas[idPeca]][i]]);
                        vizinhos[i].setLayoutY(matriz_posicoes[1][Tabuleiro.CASAS_VIZINHAS[posicoesBrancas[idPeca]][i]]);
                    }else{
                        counter++;
                        posicoesVizinhancas[i] = -1;
                        vizinhos[i].setLayoutX(origin[0]);
                        vizinhos[i].setLayoutY(origin[1]);
                    }   
                }
                if(counter != Tabuleiro.CASAS_VIZINHAS[posicoesBrancas[idPeca]].length){ // Se a peça tiver pelo menos 1 vizinhança vazia. A peça é selecionada. Se não, não ocorre nada
                    pecaSelected = idPeca;
                    neigborsSelected = true;
                }
            }else{
                Triple movimentoIA = IA.melhorMovimento(plateau, Tabuleiro.PRETO);
                int posicao = movimentoIA.x;
                int posicaoDestino = movimentoIA.y;
                int removePosicao = movimentoIA.z;
                
                
                // Modifica-se o tabuleiro
                plateau.movePeca(posicao, posicaoDestino);
                // Move-se a peça na tela
                int idDaPeca = encontra_ID_com_POSICAO(Tabuleiro.PRETO, posicao);
                pecasMarrom[idDaPeca].setLayoutX(matriz_posicoes[0][posicaoDestino]);
                pecasMarrom[idDaPeca].setLayoutY(matriz_posicoes[1][posicaoDestino]);
                posicoesPretas[idDaPeca] = posicaoDestino; // Atualiza a posição daquela peça movida
                ultimoMovimento[1].setLayoutX(matriz_posicoes[0][posicaoDestino]);
                ultimoMovimento[1].setLayoutY(matriz_posicoes[1][posicaoDestino]);
                
                if(!plateau.ocorreuMoinho()){ // Caso não ocorra moinho
                    checaCasasLivres(plateau.turno);
                    plateau.trocaTurno();// Troca de turno
                    
                }else { // Houve moinho
                    int idDaPecaAserRemovida = encontra_ID_com_POSICAO(Tabuleiro.BRANCO, removePosicao);
                                        
                    if(plateau.removerPeca(posicoesBrancas[idDaPecaAserRemovida], false, Tabuleiro.PRETO)){// Removendo a peça e "zerando" a sua posição do vetor de posições
                        posicoesBrancas[idDaPecaAserRemovida] = -1;
                    }else{
                        plateau.remocaoProperty.set("Não foi possível remover a peca " + idDaPecaAserRemovida + " [ERRO DA IA]"); 
                        return; // não foi possível remover a peça escolhida
                    }
                    // Colocando a peça na área cemitério de peças e setando a posição da próxima peça no cemitério
                    pecasBrancas[idDaPecaAserRemovida].setLayoutX(cemitery[0]);
                    pecasBrancas[idDaPecaAserRemovida].setLayoutY(cemitery[1]);
                    cemitery[1] += 50;
                    // Desativando o modo remoção de peças
                    plateau.modoRemocao = false;
                    plateau.alguemGanhou(checaCasasLivres(plateau.turno));// Checando se alguem ganhou ou ativando um super modo
                    plateau.remocaoProperty.set("A IA é muito forte! Cuidado...");// Atualizando a mensagem na tela do jogo
                    plateau.trocaTurno();// Método que troca de turno 
                    
                }
            }
            
            //System.out.println("Peca selecionada: " + idPeca + " Neighbors selecionados: " + neigborsSelected);
        }else{//des-selecionar as vizinhanças da peca selecionada
            if(idPeca == pecaSelected){
                for(int j = 0; j<4; j++){
                    vizinhos[j].setLayoutX(origin[0]);
                    vizinhos[j].setLayoutY(origin[1]);
                }   
                neigborsSelected = false;
                pecaSelected = -1;
            }

        }
    }
    
    public boolean checaCasasLivres(boolean turno){//checar depois da remoção de uma peça e depois de uma jogada sem remoção
        if(turno){//checar se as peças pretas tem posições livres
            for(int j = 0; j < 9 ; j++){
                if( posicoesPretas[j] == -1 )
                    continue;
                for(int i = 0; i< Tabuleiro.CASAS_VIZINHAS[posicoesPretas[j]].length;i++){
                    if(plateau.getPosicao(Tabuleiro.CASAS_VIZINHAS[posicoesPretas[j]][i]) == Tabuleiro.VAZIO)
                        return false;
                }
            }
        }else{//checar se as brancas tem posições livres 
            for(int j = 0; j < 9 ; j++){
                if(posicoesBrancas[j] == -1)
                    continue;
                for(int i = 0; i< Tabuleiro.CASAS_VIZINHAS[posicoesBrancas[j]].length ;i++){
                    if(plateau.getPosicao(Tabuleiro.CASAS_VIZINHAS[posicoesBrancas[j]][i]) == Tabuleiro.VAZIO)
                        return false;
                }
            }
        }
        return true;
    }
    
       // Setando a tela de jogo
   void setUpPlay(){
       
       Button playAgain = new Button("Jogar Novamente");
       playAgain.getStylesheets().add("/estilo10.css");
       playAgain.setVisible(false);
       
       playAgain.setOnMouseClicked(new EventHandler<MouseEvent>(){
            @Override
                public void handle(MouseEvent event) {
                    startAgain();
                }
        });
       
       //Retângulos do Tabuleiro
        Rectangle exterior = new Rectangle(500,500, Color.TRANSPARENT);
        exterior.setX(posX);
        exterior.setY(posY);
        exterior.setStroke(board);
        exterior.setStrokeWidth(espessura);
        
        Rectangle meio = new Rectangle(300, 300, Color.TRANSPARENT);
        meio.setStroke(board);
        meio.setStrokeWidth(espessura);
        meio.setX(posX + exterior.getWidth()/2 - meio.getWidth()/2);
        meio.setY(posY + exterior.getHeight()/2 - meio.getHeight()/2);
        
        Rectangle interior = new Rectangle(100, 100, Color.TRANSPARENT);
        interior.setStroke(board);
        interior.setStrokeWidth(espessura);
        interior.setX(posX + exterior.getWidth()/2 - interior.getWidth()/2);
        interior.setY(posY + exterior.getHeight()/2 - interior.getHeight()/2);
        
        //Linhas médias do tabuleiro
        Line cmed[] = new Line[4];
        cmed[0] = new Line(posX+exterior.getWidth()/2,posY, posX+exterior.getWidth()/2, posY+ (exterior.getWidth()-interior.getWidth())/2);
        cmed[0].setStrokeWidth(espessura);
        cmed[0].setFill(board);
        
        cmed[1] = new Line(posX, posY + exterior.getHeight()/2, posX + (exterior.getWidth()-interior.getWidth())/2, posY + exterior.getHeight()/2);
        cmed[1].setStrokeWidth(espessura);
        cmed[1].setFill(board);
        
        cmed[2] = new Line(posX+exterior.getWidth()/2,posY+exterior.getHeight(), posX+exterior.getWidth()/2, posY + exterior.getHeight()- (exterior.getWidth()-interior.getWidth())/2);
        cmed[2].setStrokeWidth(espessura);
        cmed[2].setFill(board);
        
        cmed[3] = new Line(posX + exterior.getWidth(), posY + exterior.getHeight()/2, posX + exterior.getWidth() - (exterior.getWidth()-interior.getWidth())/2, posY + exterior.getHeight()/2);
        cmed[3].setStrokeWidth(espessura);
        cmed[3].setFill(board);
        
        //Instanciando botoes e dando-lhes o devido estilo
        for(int i = 0; i < 24; i++){
            botoes[i] = new Button();
            botoes[i].setId( Integer.toString(i) );
            botoes[i].setStyle("-fx-background-radius: 5em; " +
                "-fx-min-width: 30px; " +
                "-fx-min-height: 30px; " +
                "-fx-max-width: 30px; " +
                "-fx-max-height: 30px;"+
                "-fx-background-color: rgb(30,30,30)");
        }
        
        // Definindo a posicao de todos os botoes na tela e guardando esses valores em uma matriz
        for(int i = 0; i < 3; i++){
            botoes[i].setLayoutX(posX + i*exterior.getWidth()/2 - tam_botao);
            botoes[i].setLayoutY(posY-tam_botao);
            matriz_posicoes[0][i] = posX + i*exterior.getWidth()/2;
            matriz_posicoes[1][i] = posY;
        }
        
        
        for(int i = 3; i < 6; i++){
            botoes[i].setLayoutX(posX + (exterior.getWidth()-meio.getWidth())/2 + (i-3)*meio.getWidth()/2 - tam_botao);
            botoes[i].setLayoutY(posY + (exterior.getHeight()-meio.getHeight())/2 -tam_botao);
            matriz_posicoes[0][i] = posX + (exterior.getWidth()-meio.getWidth())/2 + (i-3)*meio.getWidth()/2;
            matriz_posicoes[1][i] = posY + (exterior.getHeight()-meio.getHeight())/2;
        }
        
        for(int i = 6; i < 9; i++){
            botoes[i].setLayoutX(posX + (exterior.getWidth()-interior.getWidth())/2 + (i-6)*interior.getWidth()/2 - tam_botao);
            botoes[i].setLayoutY( posY + (exterior.getHeight()-interior.getHeight())/2 -tam_botao);
            matriz_posicoes[0][i] = posX + (exterior.getWidth()-interior.getWidth())/2 + (i-6)*interior.getWidth()/2;
            matriz_posicoes[1][i] = posY + (exterior.getHeight()-interior.getHeight())/2;
        }
        
        for(int i = 9; i < 12; i++){
            botoes[i].setLayoutX( posX + (i-9)*interior.getWidth() - tam_botao);
            botoes[i].setLayoutY( posY + (exterior.getHeight())/2 -tam_botao);
            matriz_posicoes[0][i] = posX + (i-9)*interior.getWidth();
            matriz_posicoes[1][i] = posY + (exterior.getHeight())/2;
        }
        
        for(int i = 12; i < 15; i++){
            botoes[i].setLayoutX( posX + 3* interior.getWidth() + (i-12)* interior.getWidth() - tam_botao);
            botoes[i].setLayoutY( posY + (exterior.getHeight())/2 -tam_botao);
            matriz_posicoes[0][i] = posX + 3* interior.getWidth() + (i-12)* interior.getWidth();
            matriz_posicoes[1][i] = posY + (exterior.getHeight())/2;
        }
        
        
        for(int i = 15; i<18; i++){
            botoes[i].setLayoutX( posX + (exterior.getWidth()-interior.getWidth())/2 + (i-15)*interior.getWidth()/2 - tam_botao);
            botoes[i].setLayoutY( posY + exterior.getHeight() -(exterior.getHeight()-interior.getHeight())/2 -tam_botao);
            matriz_posicoes[0][i] = posX + (exterior.getWidth()-interior.getWidth())/2 + (i-15)*interior.getWidth()/2;
            matriz_posicoes[1][i] = posY + exterior.getHeight() -(exterior.getHeight()-interior.getHeight())/2;
        }
        
        for(int i = 18; i < 21; i++){
            botoes[i].setLayoutX( posX + (exterior.getWidth()-meio.getWidth())/2 + (i-18)*meio.getWidth()/2 - tam_botao);
            botoes[i].setLayoutY( posY + exterior.getHeight() - (exterior.getHeight()-meio.getHeight())/2 - tam_botao);
            matriz_posicoes[0][i] = posX + (exterior.getWidth()-meio.getWidth())/2 + (i-18)*meio.getWidth()/2;
            matriz_posicoes[1][i] = posY + exterior.getHeight() - (exterior.getHeight()-meio.getHeight())/2;
        }
        
        for(int i = 21; i<24; i++){
            botoes[i].setLayoutX( posX + (i-21)*exterior.getWidth()/2 - tam_botao);
            botoes[i].setLayoutY( posY + exterior.getHeight() - tam_botao);
            matriz_posicoes[0][i] = posX + (i-21)*exterior.getWidth()/2;
            matriz_posicoes[1][i] = posY + exterior.getHeight();
        }
        
        // Definindo as ações dos botões quando eles forem clicados
        for(int i = 0; i < 24; i++){
            botoes[i].setOnMouseClicked(new EventHandler<MouseEvent>() {
            
            // Estilo de saída do event.getSource.toString(): ''Button[id=22, styleClass=button]''
            @Override
            public void handle(MouseEvent event) {
                if(plateau.fase2)// Na segunda fase do jogo eles não participam
                    return;
                // Obtendo qual o botão que foi pressionado
                String partes [] =  event.getSource().toString().split("[=,]");
                int idBotao = Integer.parseInt(partes[1]);
                //No modo de remoção, eles não fazem nada
                if(plateau.modoRemocao || plateau.getPosicao(idBotao)!= Tabuleiro.VAZIO){
                    return;
                }
                if(plateau.fase1 == true){ // Na 1ª fase eles chamam o método de mover peças na fase 1
                    moverPecasPrimeiraFase(plateau.turno, idBotao);
                    if(plateau.ocorreuMoinho())
                        System.out.println("Ocorreu uma triade");
                    else 
                        plateau.trocaTurno(); // Troca de turno
                }           
            }
            });
        }
        
        //Peças brancas
        //Circle pecasBrancas[] = new Circle[9]; 
        for(int i = 0; i<9; i++){//Definindo suas características e onde elas começarão na tela 
            pecasBrancas[i] = new Circle(radius, Color.WHITE);
            pecasBrancas[i].setStrokeWidth(espessura);
            pecasBrancas[i].setLayoutX(posX + 65*i);
            pecasBrancas[i].setLayoutY(posY - 50);
            pecasBrancas[i].setId( Integer.toString(i) );
        }
        
        //Peças marrons
        //Circle pecasMarrom[] = new Circle[9]; 
        for(int i = 0; i<9; i++){//Definindo suas características e onde elas começarão na tela 
            pecasMarrom[i] = new Circle(radius, Color.SADDLEBROWN);
            pecasMarrom[i].setStrokeWidth(espessura);
            pecasMarrom[i].setLayoutX(posX + 65*i);
            pecasMarrom[i].setLayoutY(posY +exterior.getHeight() + 50);
            pecasMarrom[i].setId( Integer.toString(i) );
            
        }
        
        
        for(int i = 0; i<9; i++){
            pecasBrancas[i].setOnMouseClicked(new EventHandler<MouseEvent>() {
            
            // Estilo de saída do event.getSource.toString(): ''Button[id=22, styleClass=button]''
            @Override
            public void handle(MouseEvent event) {
                // Obtendo qual é a peça branca que foi pressionada
                String partes [] =  event.getSource().toString().split("[=,]");
                int idPecaB = Integer.parseInt(partes[1]);
                // Caso a peça possuir uma posição -1, quer dizer que já foi removida ou ainda nem foi colocada no tabuleiro
                if(posicoesBrancas[idPecaB] == -1)
                    return;
                //if(plateau.superModeP && idPecaB != pecaSelected && pecaSelected != -1) // Outras peças que não estão selecionadas para mostrar a vizinhança não terão nenhuma ação
                    //return;
                // Fase na qual a segunda fase irá funcionar
                if(plateau.turno && plateau.fase2 && !plateau.modoRemocao)
                    moverPecasSegundaFase(plateau.turno, idPecaB); //Somente habilita as vizinhanças
                // Modo de remoção de peças brancas
                if(plateau.modoRemocao && !plateau.turno){
                    if(plateau.removerPeca(posicoesBrancas[idPecaB], false, Tabuleiro.PRETO)){// Removendo a peça e "zerando" a sua posição do vetor de posições
                        posicoesBrancas[idPecaB] = -1;
                    }else{
                        return; // não foi possível remover a peça escolhida
                    }
                    // Colocando a peça na área cemitério de peças e setando a posição da próxima peça no cemitério
                    pecasBrancas[idPecaB].setLayoutX(cemitery[0]);
                    pecasBrancas[idPecaB].setLayoutY(cemitery[1]);
                    cemitery[1] += 50;
                    // Desativando o modo remoção de peças
                    plateau.modoRemocao = false;
                    plateau.alguemGanhou(checaCasasLivres(plateau.turno));// Checando se alguem ganhou ou ativando um super modo
                    plateau.remocaoProperty.set("A IA é muito forte! Cuidado...");// Atualizando a mensagem na tela do jogo
                    plateau.trocaTurno();// Método que troca de turno 
                }
                    
                }             
            });
        }
        
        for(int i = 0; i<9; i++){
            pecasMarrom[i].setOnMouseClicked(new EventHandler<MouseEvent>() {
            
            // Estilo de saída do event.getSource.toString(): ''Button[id=22, styleClass=button]''
            @Override
            public void handle(MouseEvent event) {
                // Obtendo qual é a peça preta que foi pressionada
                String partes [] =  event.getSource().toString().split("[=,]");
                int idPecaM = Integer.parseInt(partes[1]);
                //if(plateau.superModeP && idPecaM != pecaSelected && pecaSelected != -1) // Outras peças que não estão selecionadas para mostrar a vizinhança não terão nenhuma ação
                    //return;
                if(posicoesPretas[idPecaM] == -1) // Caso a peça possuir uma posição -1, quer dizer que já foi removida ou ainda nem foi colocada no tabuleiro
                        return;    
                if(!plateau.turno && plateau.fase2 && !plateau.modoRemocao) // Movimentos gerais na 2ª fase do jogo
                        moverPecasSegundaFase(plateau.turno, idPecaM);
                // Modo de remoção de peças marrons na vez das peças brancas
                if(plateau.modoRemocao && plateau.turno){
                    if(plateau.removerPeca(posicoesPretas[idPecaM], true, Tabuleiro.BRANCO)){ // Removendo a peça e "zerando" a sua posição do vetor de posições
                        posicoesPretas[idPecaM] = -1;
                    }else{
                            return;// não foi possível remover a peça escolhida
                    }
                    // Colocando a peça na área cemitério de peças e setando a posição da próxima peça no cemitério
                    pecasMarrom[idPecaM].setLayoutX(cemitery[0]);
                    pecasMarrom[idPecaM].setLayoutY(cemitery[1]);
                    cemitery[1] += 50;
                    // Desativando o modo remoção de peças
                    plateau.modoRemocao = false;
                    plateau.alguemGanhou(checaCasasLivres(plateau.turno)); // Checando se alguem ganhou ou ativando um super modo
                    plateau.remocaoProperty.set("A IA é muito forte! Cuidado..."); // Atualizando a mensagem na tela do jogo
                    plateau.trocaTurno(); // Método que troca de turno
                }
            }
            
            });
        }
        
        // Definição dos círculos de vizinhança normal. OBS: O círculo por cima do botão o inibe, mesmo tendo seu conteúdo transparente
        for(int i = 0; i < 4;i++){
            vizinhos[i] = new Circle(1.5*radius, Color.TRANSPARENT);
            vizinhos[i].setStrokeWidth(espessura);
            vizinhos[i].setStroke(Color.RED);
            vizinhos[i].setLayoutX(origin[0]);
            vizinhos[i].setLayoutY(origin[1]);
            vizinhos[i].setId(Integer.toString(i));
        }
        
        // Definindo as ações dos botões de vizinhança normal
        for(int i = 0; i<4; i++){
            vizinhos[i].setOnMouseClicked(new EventHandler<MouseEvent>(){
                @Override
                public void handle(MouseEvent event) {
                    if(neigborsSelected){
                        // Obtendo qual é a vizinhança que foi pressionada
                        String partes [] =  event.getSource().toString().split("[=,]");
                        int idViz = Integer.parseInt(partes[1]);
                        int posicaoDestino = posicoesVizinhancas[idViz];
                        //Caso a posição seja válida e seja o turno das brancas
                        if(plateau.turno && posicaoDestino != -1){
                            //Colocando todos os círculos de vizinhanca para a origem
                            for(int i = 0; i<4; i++){
                                vizinhos[i].setLayoutX(origin[0]);
                                vizinhos[i].setLayoutY(origin[1]);
                                posicoesVizinhancas[i] = -1;
                            }
                            // Modifica-se o tabuleiro
                            plateau.movePeca(posicoesBrancas[pecaSelected],posicaoDestino );
                            // Move-se a peça na tela
                            pecasBrancas[pecaSelected].setLayoutX(matriz_posicoes[0][posicaoDestino]);
                            pecasBrancas[pecaSelected].setLayoutY(matriz_posicoes[1][posicaoDestino]);
                            posicoesBrancas[pecaSelected] = posicaoDestino;// Atualiza a posição daquela peça movida
                            ultimoMovimento[1].setLayoutX(matriz_posicoes[0][posicaoDestino]);
                            ultimoMovimento[1].setLayoutY(matriz_posicoes[1][posicaoDestino]);
                            
                            if(!plateau.ocorreuMoinho()){ // Caso não ocorra moinho
                                    plateau.alguemGanhou(checaCasasLivres(plateau.turno));
                                    plateau.trocaTurno();// Troca de turno
                                }
                            neigborsSelected = !neigborsSelected; // As vizinhanças voltam a estar desativadas
                            pecaSelected = -1; // Nenhuma peça está selecionada. O -1 representa isso
                            return;
                        }
                        // Caso a posição seja válida e seja o turno das pretas
                        if(!plateau.turno && posicaoDestino!=-1){
                                //Colocando todos os círculos de vizinhanca para a origem
                            for(int i = 0; i<4; i++){
                                vizinhos[i].setLayoutX(origin[0]);
                                vizinhos[i].setLayoutY(origin[1]);
                                posicoesVizinhancas[i] = -1;
                            }
                            // Modifica-se o tabuleiro
                            plateau.movePeca(posicoesPretas[pecaSelected],posicaoDestino );
                            // Move-se a peça na tela
                            pecasMarrom[pecaSelected].setLayoutX(matriz_posicoes[0][posicaoDestino]);
                            pecasMarrom[pecaSelected].setLayoutY(matriz_posicoes[1][posicaoDestino]);
                            posicoesPretas[pecaSelected] = posicaoDestino; // Atualiza a posição daquela peça movida
                            ultimoMovimento[1].setLayoutX(matriz_posicoes[0][posicaoDestino]);
                            ultimoMovimento[1].setLayoutY(matriz_posicoes[1][posicaoDestino]);
                            
                            if(!plateau.ocorreuMoinho()){ // Caso não ocorra moinho
                                checaCasasLivres(plateau.turno);
                                plateau.trocaTurno();// Troca de turno
                            }
                            neigborsSelected = !neigborsSelected; // As vizinhanças voltam a estar desativadas
                            pecaSelected = -1;  // Nenhuma peça está selecionada. O -1 representa isso
                        }
                    }
                }
            });
        }
        
        // Definição dos super vizinhos
        for(int i = 0; i < 24;i++){
            superVizinhos[i] = new Circle(1.5*radius, Color.TRANSPARENT);
            superVizinhos[i].setStrokeWidth(espessura);
            superVizinhos[i].setStroke(Color.GOLD);
            superVizinhos[i].setLayoutX(originSuper[0]);
            superVizinhos[i].setLayoutY(originSuper[1]);
            superVizinhos[i].setId(Integer.toString(i));
        }
        // Definição das ações dos super vizinhos. OBS: O círculo por cima do botão o inibe, mesmo tendo seu conteúdo transparente
        for(int i = 0; i<24; i++){
            superVizinhos[i].setOnMouseClicked(new EventHandler<MouseEvent>(){
                @Override
                public void handle(MouseEvent event) {
                    if(neigborsSelected){
                        // Obtendo qual é a super vizinhança que foi pressionada
                        String partes [] =  event.getSource().toString().split("[=,]");
                        int idViz = Integer.parseInt(partes[1]);
                        int posicaoDestino = posicoesSuperVizinhancas[idViz];
                        // Se for a vez das brancas e a posição de destino for válida
                        if(plateau.turno && posicaoDestino!=-1){
                            //Colocando todos os círculos de vizinhanca para a origem
                            for(int i = 0; i<24; i++){
                                superVizinhos[i].setLayoutX(originSuper[0]);
                                superVizinhos[i].setLayoutY(originSuper[1]);
                                posicoesSuperVizinhancas[i] = -1;
                            }
                            // Altera o tabuleiro
                            plateau.movePeca(posicoesBrancas[pecaSelected],posicaoDestino );
                            // Alterando a posição da peça na tela
                            pecasBrancas[pecaSelected].setLayoutX(matriz_posicoes[0][posicaoDestino]);
                            pecasBrancas[pecaSelected].setLayoutY(matriz_posicoes[1][posicaoDestino]);
                            posicoesBrancas[pecaSelected] = posicaoDestino; // Atualizando a posição da peça branca
                            ultimoMovimento[1].setLayoutX(matriz_posicoes[0][posicaoDestino]);
                            ultimoMovimento[1].setLayoutY(matriz_posicoes[1][posicaoDestino]);
                            
                            if(!plateau.ocorreuMoinho()){ // Caso não ocorra moinho
                                plateau.alguemGanhou(checaCasasLivres(plateau.turno));
                                plateau.trocaTurno();// Troca turno
                            }
                            neigborsSelected = !neigborsSelected; // As vizinhanças voltam a estar desativadas
                            pecaSelected = -1; // Nenhuma peça está selecionada. O -1 representa isso
                            return;
                        }
                        if(!plateau.turno && posicaoDestino!=-1){
                            //Colocando todos os círculos de vizinhanca para a origem
                            for(int i = 0; i<24; i++){
                                superVizinhos[i].setLayoutX(originSuper[0]);
                                superVizinhos[i].setLayoutY(originSuper[1]);
                                posicoesSuperVizinhancas[i] = -1;
                            }
                            // Altera o tabuleiro
                            plateau.movePeca(posicoesPretas[pecaSelected],posicaoDestino );
                            // Alterando a posição da peça na tela
                            pecasMarrom[pecaSelected].setLayoutX(matriz_posicoes[0][posicaoDestino]);
                            pecasMarrom[pecaSelected].setLayoutY(matriz_posicoes[1][posicaoDestino]);
                            posicoesPretas[pecaSelected] = posicaoDestino; // Atualizando a posição da peça branca
                            ultimoMovimento[1].setLayoutX(matriz_posicoes[0][posicaoDestino]);
                            ultimoMovimento[1].setLayoutY(matriz_posicoes[1][posicaoDestino]);
                            
                            if(!plateau.ocorreuMoinho()){ // Caso não ocorra moinho
                                if(plateau.alguemGanhou(checaCasasLivres(plateau.turno)))
                                    playAgain.setVisible(true); //***Alterei
                                plateau.trocaTurno(); // Troca de turno
                            }
                            neigborsSelected = !neigborsSelected; // As vizinhanças voltam a estar desativadas
                            pecaSelected = -1; // Nenhuma peça está selecionada. O -1 representa isso
                        }
                    }
                }
            });
        }
        
        // Definição do círculo referente ao último movimento
        for (int i = 0; i < 2; i++){
            ultimoMovimento[i] = new Circle(1.5*radius, Color.TRANSPARENT);
            ultimoMovimento[i].setStrokeWidth(espessura);
            ultimoMovimento[i].setStroke(Color.CADETBLUE);
            ultimoMovimento[i].setLayoutX(originLastMove[0]);
            ultimoMovimento[i].setLayoutY(originLastMove[1]);
        }
        
        
        // ***Alterei começou aqui
        // Criando um botão que ativa o movimento da IA
        Button playIA = new Button("Jogar IA");
        playIA.getStylesheets().add("/estilo10.css");
        playIA.setOnMouseClicked(new EventHandler<MouseEvent>(){
            @Override
                public void handle(MouseEvent event) {
                    if(!plateau.turno){
                        System.out.println("ALALALLA");
                        if(plateau.fase1)
                            moverPecasPrimeiraFase(plateau.turno, -1);
                        else if (!plateau.alguemGanhou(checaCasasLivres(!plateau.turno)))
                            moverPecasSegundaFase(plateau.turno, -1);
                    }
                }
        });
        
        // caixa com: profundidade de 1 a 8
        Label prof = new Label("Nível da IA: ");
        ObservableList num = FXCollections.observableArrayList(
            "1","2","3","4","5","6","7","8"
        );
        ChoiceBox profundidade = new ChoiceBox(num);
        profundidade.getSelectionModel().selectFirst();
        
        IA.profundidadeStr.bind(profundidade.getSelectionModel().selectedItemProperty());
        HBox infoIA = new HBox(prof, profundidade);
        // ***Alterei até aqui
        
        //Construção dos elementos na tela do jogo
        Group frame = new Group(exterior, meio, interior,cmed[0],cmed[1],cmed[2], cmed[3]);
        Group sensores = new Group(botoes[0],botoes[1], botoes[2], botoes[3], botoes[4], botoes[5], botoes[6],botoes[7], botoes[8], botoes[9], botoes[10], botoes[11], botoes[12], botoes[13],botoes[14], botoes[15], botoes[16], botoes[17] ,botoes[18],botoes[19], botoes[20], botoes[21], botoes[22], botoes[23]);
        Group pecasB = new Group(pecasBrancas[0], pecasBrancas[1], pecasBrancas[2], pecasBrancas[3], pecasBrancas[4], pecasBrancas[5], pecasBrancas[6], pecasBrancas[7], pecasBrancas[8]);
        Group pecasM = new Group(pecasMarrom[0], pecasMarrom[1], pecasMarrom[2], pecasMarrom[3], pecasMarrom[4], pecasMarrom[5], pecasMarrom[6], pecasMarrom[7], pecasMarrom[8]);
        Group viz = new Group(vizinhos[0], vizinhos[1], vizinhos[2], vizinhos[3]);
        Group superViz = new Group (superVizinhos[0], superVizinhos[1], superVizinhos[2], superVizinhos[3], superVizinhos[4], superVizinhos[5], superVizinhos[6], superVizinhos[7], superVizinhos[8], superVizinhos[9], superVizinhos[10], superVizinhos[11], superVizinhos[12], superVizinhos[13], superVizinhos[14], superVizinhos[15], superVizinhos[16], superVizinhos[17], superVizinhos[18], superVizinhos[19], superVizinhos[20], superVizinhos[21], superVizinhos[22], superVizinhos[23] );
        Group ultimoMov = new Group(ultimoMovimento[0], ultimoMovimento[1]);
        Text titulo = new Text("Info do jogo");
        titulo.setWrappingWidth(350);
        titulo.setFont(Font.font("Ubuntu Light", FontWeight.EXTRA_BOLD, FontPosture.REGULAR, 10));
        titulo.setTextOrigin(VPos.TOP);
        titulo.setTextAlignment(TextAlignment.JUSTIFY);
        titulo.getStyleClass().add("emphasized-text");
        Text turno = new Text();
        Text remocao = new Text();
        remocao.setWrappingWidth(350);
        Text win = new Text();
        Text rounds = new Text();
        //turno.textProperty().bindBidirectional((Property<String>) new SimpleStringProperty("Turno: ").concat(plateau.turnoProperty.getValueSafe()));
        //turno.textProperty().bindBidirectional(new SimpleStringProperty("Turno: ").concat(plateau.turnoProperty.getValueSafe()));
        //plateau.turnoProperty.bind(turno.textProperty());
        turno.textProperty().bindBidirectional(plateau.turnoProperty);
        remocao.textProperty().bindBidirectional(plateau.remocaoProperty);
        win.textProperty().bindBidirectional(plateau.ganharProperty);
        rounds.textProperty().bindBidirectional(plateau.rodadasProperty);
        win.setWrappingWidth(350);
        VBox msgs = new VBox(titulo,turno,remocao,win,rounds,playIA, infoIA,playAgain); // ***Alterei aqui. Acrescentando informações da IA

//        VBox msgs = new VBox(titulo,turno,remocao,win,rounds,playIA, infoIA); // ***Alterei aqui. Acrescentando informações da IA

        msgs.setLayoutX(800);
        msgs.setLayoutY(30);
        msgs.setSpacing(10);
        Group tabuleiro = new Group(frame, sensores, pecasB, pecasM, viz, msgs, superViz, ultimoMov);
        playScene = new Scene(tabuleiro, 1200, 800);
        playScene.getStylesheets().add("/estilo10.css");
        playScene.setFill(Color.BISQUE);
   }
    
    public int encontraCasaRemocao(int remocao){
        for (int i = 0; i < 9; i++){
            if (posicoesBrancas[i] == remocao)
                return i;
        }
        return -1;
    }
    
    public int encontra_ID_com_POSICAO(int jogador, int posicao){
        int id = -1;
        
        for (int i = 0; i < 9; i++){
            if (jogador == Tabuleiro.BRANCO){
                if (posicoesBrancas[i] == posicao)
                    id = i;
            }else {
                if (posicoesPretas[i] == posicao)
                    id = i;
            }
        }
        
        return id;

    }
    
    void startAgain(){
        //Peças brancas
        //Circle pecasBrancas[] = new Circle[9]; 
        for(int i = 0; i<9; i++){//Definindo suas características e onde elas começarão na tela 
            pecasBrancas[i] = new Circle(radius, Color.WHITE);
            pecasBrancas[i].setStrokeWidth(espessura);
            pecasBrancas[i].setLayoutX(posX + 65*i);
            pecasBrancas[i].setLayoutY(posY - 50);
            pecasBrancas[i].setId( Integer.toString(i) );
        }
        
        //Peças marrons
        //Circle pecasMarrom[] = new Circle[9]; 
        for(int i = 0; i<9; i++){//Definindo suas características e onde elas começarão na tela 
            pecasMarrom[i] = new Circle(radius, Color.SADDLEBROWN);
            pecasMarrom[i].setStrokeWidth(espessura);
            pecasMarrom[i].setLayoutX(posX + 65*i);
            pecasMarrom[i].setLayoutY(posY + 500 + 50);
            pecasMarrom[i].setId( Integer.toString(i) );
            
        }
        plateau.reinicia();    

    }
}