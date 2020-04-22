/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ai_ninemensmorris;

 import java.util.*;

 //Será que vale a pena fazer uma classe que herda de Circle mas que possui sua id e em qual posição do tableiro ela está?

//Erro ao fazer moinho e tentar mover outra peca minha novamente

/*
    Considerações e erros 
        Criar um botão de jogar novamente que é ativado na no método alguemGanhou()
        Super vizinhança com 18 circulos e colocar um contador ao se determinar onde se deve colocar a super vizinhança para não dar VectorOutOfBounds
        Acrescentar um ícone para o jogo se der tempo
        Ambos jogadores tendo 3 peças, se ocorrer 10 jogadas sem ninguém ganhar, então empate 
        Erro mostrando que já se ganhou quando ainda se está jogando. A mensagem de que as brancas ganharam e de que ela faz parte de um grupo seleto de pessoas aparece. Também ocorreu com a mensagem de que a IA ganhou
        Checar remoção de moinho quando o adversário só tem moinhos 
        Será preciso fazer um método que identifique a id da peça a ser movida pela IA de acordo com a sua posição
        Falta fazer uma função para a IA fazer a remoção de uma peça
        Falta setar o valor dessa remoção no campo correspondente do movimento
        Checar se nos movimentos futuros se houve empate
*/

/**
 *
 * @author caio e prada
 */
import java.util.SortedSet;
import java.util.TreeSet;
import javafx.beans.property.SimpleStringProperty;

public class Tabuleiro {
    public SortedSet<Integer> pecasBranco, pecasPreto; // guardam a informação das casas do tabuleiro ocupadas pelas peças das duas cores
    public int[] matriz_jogo; // vetor que identifica se a posição está vazia ou possui uma peça branca ou uma peça preta
    public int FASE_DO_JOGO;
    public int numeroDeJogadas;
    public int jogadas_sem_pecas_removidas;
    public boolean brancoEstaNaFase3;
    public boolean pretoEstaNaFase3;
    public boolean turno; // true = turno das brancas, false =  turno das pretas
    public boolean fase1; // Identifica se a fase de colocar peças no tabuleiro já acabou
    public boolean fase2; // Identifica quando os jogadores só poderão mover suas peças para casas vizinhas
    public boolean superModeB = false; // Modo no qual as peças brancas podem "voar" pelo tabuleiro
    public boolean superModeP = false; // Modo no qual as peças pretas podem "voar" pelo tabuleiro 
    // Variáveis que identificam quem é o que no vetor de posições do jogo
    public final static int VAZIO = 3;
    public final static int PRETO = 0;
    public final static int BRANCO = 1;
    
    private int jogadasSemPecasRemovidas; // // Conta o número de jogadas sem haver a remoção de uma peça
    int pecaLivre [] = {8,8}; // Identifica quantas peças faltam a ser colocadas na fase1.. Índice 0: brancas, Índice 1: pretas
    //Tríades possíveis do jogo
    static final int[][] TRIADES = {
        {0,1,2}, {3,4,5}, {6,7,8}, {9,10,11}, {12,13,14}, {15,16,17},{18,19,20}, {21,22,23},
        {0,9,21},{3,10,18},{6,11,15}, {1,4,7}, {16,19,22}, {8,12,17}, {5,13,20}, {2,14,23}
    } ; // 16 formas de se fazer uma tríade
    // Posições de casas vizinhas às posições do tabuleiro. Os -1 representam nada, foram acrecentados para a matriz ter o mesmo número de colunas
    static final int[][] CASAS_VIZINHAS =
              {    { 1, 9}, { 0, 2, 4}, { 1,14},
                   { 4,10}, { 1, 3, 5, 7}, { 4,13},
                   { 7,11}, { 4, 6, 8}, { 7,12},
    { 0,10,21}, { 3, 9,11,18}, { 6,10,15}, { 8,13,17}, { 5,12,14,20}, { 2,13,23},
                   {11,16}, {15,17,19}, {12,16},
                   {10,19}, {16,18,20,22}, {13,19},
                   { 9,22}, {19,21,23}, {14,22}      };
    boolean modoRemocao; // Habilita que as peças (círculos) possam ser retirados
    
// Variável que guarda o número de triades para saber quando se fez uma triade na rodada
    int numTriades[]= {0,0}; // índice 0: Brancas, índice 1: Pretas   
    int posicoesTriadesB[] = new int[12]; // Posições das peças brancas que formam moinhos=> Se pode ter no máximo 4 moinhos com 9 peças
    int posicoesTriadesP[] = new int[12]; // Posições das peças pretas que formam moinhos=> Se pode ter no máximo 4 moinhos com 9 peças
    //Propriedades criadas a fim de mostrar um texto na tela do jogo. Usa-se bindBidirectional
    SimpleStringProperty turnoProperty;
    SimpleStringProperty remocaoProperty = new SimpleStringProperty("A IA é muito forte! Cuidado...");
    SimpleStringProperty ganharProperty = new SimpleStringProperty("Quem será que irá ganhar? ");
    SimpleStringProperty rodadasProperty = new SimpleStringProperty();
    boolean vencer = false; // Guarda a informação de que alguém venceu
    boolean empate = false; // Guarda a informação de que houve empate
    
    //Construtor da classe
    public Tabuleiro(){
        //Inicializando as posições da matriz do jogo sendo todas as casas vazias
        matriz_jogo = new int[24];
        for (int i = 0; i < 24; i++) matriz_jogo[i] = Tabuleiro.VAZIO;
        //Inicializando o contador de jogadas
        jogadasSemPecasRemovidas = 0;
        //Declarando as árvores de posições
        pecasBranco = new TreeSet<>(); // O tipo ser Integer é redundante, já que é um SortedSet<Integer>
        pecasPreto = new TreeSet<>();
        turno = true; // As brancas sempre começam
        turnoProperty = new SimpleStringProperty("Turno: Brancas jogam"); //Inicializando o texto de quem joga nesta vez 
        FASE_DO_JOGO = 1;
        fase1 = true; // Fase de se colocar as peças em jogo
        fase2 = false; // Fase de se mover as peças em jogo
        modoRemocao = false; // Modo de remoção começa destivado
        rodadasProperty.set("Rodadas sem remoção de peças: " + jogadasSemPecasRemovidas); // Inicializando o texto sobre o contador de rodadas
        numeroDeJogadas = 0;
        jogadas_sem_pecas_removidas = 0;
    }
    
    public Tabuleiro(Tabuleiro t){
        numeroDeJogadas = t.numeroDeJogadas;
        jogadas_sem_pecas_removidas = t.jogadas_sem_pecas_removidas;
        FASE_DO_JOGO = t.FASE_DO_JOGO;
        
        matriz_jogo = new int[24];
        for (int i = 0; i < 24; i++) matriz_jogo[i] = t.matriz_jogo[i];
        jogadas_sem_pecas_removidas = t.jogadas_sem_pecas_removidas;
        
        pecasBranco = new TreeSet<Integer>();
        Iterator<Integer>it = t.pecasBranco.iterator();
        while (it.hasNext()){
            int aux = it.next();
            pecasBranco.add(aux);
        }
        
        pecasPreto = new TreeSet<Integer>();
        it = t.pecasPreto.iterator();
        while (it.hasNext()){
            int aux = it.next();
            pecasPreto.add(aux);
        }
    }
    
    // Devolve o valor da posição do vetor de posições, se está ocupadae por qual peça ou se está vazia
    public int getPosicao(int posicao){
        return matriz_jogo[posicao];
    }
    
    // Acrescenta uma peça a matriz do jogo e as árvores de peças brancas ou pretas dependendo de quem é o jogador a jogar
    public boolean adicionarUmaPeca(int posicao, int jogador){
        if (matriz_jogo[posicao] != Tabuleiro.VAZIO && pecaLivre[jogador]<=-1 ){ // Testa se a posição do jogo já está ocupada e se o jogador ainda tem peças a serem colocadas
            return true; // Ocorreu erro
        }else {
            matriz_jogo[posicao] = jogador; // Transfere valor do jogador a matriz do jogo
            // Acrescenta posição da peça a respectiva árvore 
            if (jogador == BRANCO)
                pecasBranco.add(posicao);
                //Mover uma das peças brancas para a respectiva posição da escolhida
            else
                pecasPreto.add(posicao);
            pecaLivre[jogador]--;// Diminui o número de peças ainda não em jogo daquele jogador
        }
        return false; // Não ocorreu erro
    }
    
    
    
    
    
    public void adicionarPeca(int posicao, int jogador){
        if (matriz_jogo[posicao] != VAZIO){
            System.out.printf("A posicao %d do tabuleiro ja possui uma peca, nao pode adicionar outra!!!\n", posicao);
            System.exit(0);
        }else {
            matriz_jogo[posicao] = jogador;
            if (jogador == BRANCO) pecasBranco.add(posicao);
            if (jogador == PRETO) pecasPreto.add(posicao);
        }
        
        incrementarNumeroDeJogadas();
    }
    
    public void incrementarNumeroDeJogadas(){
        numeroDeJogadas++;
        if (numeroDeJogadas == 18){
            FASE_DO_JOGO = 2;
        }
    }
    
    public void removerPeca(int posicao){
        if (matriz_jogo[posicao] == VAZIO){
            System.out.printf("A posicao %d nao possui nenhuma peca para ser removida!!!\n", posicao);
            System.exit(0);
        }else {
            matriz_jogo[posicao] = VAZIO;
            pecasBranco.remove(posicao);
            pecasPreto.remove(posicao);
        }
    }
    
    public int numeroDeJogadas(){
        return numeroDeJogadas;
    }
    
    public void mudarTurno(){
        if (turno == true) turno = false;    // 1 = BRANCO
        else turno = true;                   // 0 = PRETO
    }
    
    public void moverPeca(int posicaoInicial, int posicaoFinal){
       adicionarPeca(posicaoFinal, matriz_jogo[posicaoInicial]);
       removerPeca(posicaoInicial);
       
       mudarTurno();
    }
    
    public void incrementarNumeroDeJogadasSemCaptura(){
        jogadas_sem_pecas_removidas++;
    }   
    
    public void setFaseDoJogo(int novaFase){
        FASE_DO_JOGO = novaFase;
    }
    
    public int getFaseDoJogo(){
        return FASE_DO_JOGO;
    }
    
    public void setBrancoParaFase3(){
        brancoEstaNaFase3 = true;
    }
    
    public void setPretoParaFase3(){
        pretoEstaNaFase3 = true;
    }
    
    
    
    
    // Fazer uma função que desfaz um movimento e outro ou este mesmo que também desfaz uma remoção 
    public void desfazMovimento(int posicaoNow ,int posicaoAntiga, int jogador, int posRemove){
        if(fase1){ //desfazendo um movimento na fase 1 de jogo
            if(jogador == BRANCO){ // Quem jogou foram as brancas
                pecasBranco.remove(posicaoNow); // remove a peça da árvore de posições das brancas
                pecaLivre[0]++; // se adiciona 1 às peças livres, ou seja, as que não foram colocadas em jogo ainda. Isto se deve a como a função adicionarPeca funciona
                if(posRemove > -1 && posRemove < 24){ // se alguém foi removido, então se adiciona ele novamente
                    adicionarPeca(posicaoAntiga, PRETO);
                    pecaLivre[1]++;
                }
            }else{ // Quem jogou foram as pretas
                pecasPreto.remove(posicaoNow); // remove a peça da árvore de posições das pretas
                pecaLivre[1]++; // se adiciona 1 às peças livres, ou seja, as que não foram colocadas em jogo ainda. Isto se deve a como a função adicionarPeca funciona
                if(posRemove > -1 && posRemove < 24){// se alguém foi removido, então se adiciona ele novamente
                    adicionarPeca(posicaoAntiga, BRANCO);
                    pecaLivre[0]++;
                }
            }
            matriz_jogo[posicaoNow] = VAZIO;       
        }else{ // Desfazendo o movimento na Fase 2 e Fase 3
            if(jogador == BRANCO){ // Quem jogou foram as brancas
                movePeca(posicaoNow, posicaoAntiga); // Depende do turno estar atualizado para funcionar corretamente. Peça é movida para seu lugar original
                if(posRemove > -1 && posRemove < 24){ // se alguém foi removido, então se adiciona ele novamente
                    adicionarPeca(posicaoAntiga, PRETO);
                    pecaLivre[1]++;
                }
            }else{ // Quem jogou foram as pretas
                movePeca(posicaoNow, posicaoAntiga); // Depende do turno estar atualizado para funcionar corretamente
                if(posRemove > -1 && posRemove < 24){// se alguém foi removido, então se adiciona ele novamente
                    adicionarPeca(posicaoAntiga, BRANCO);
                    pecaLivre[0]++;
                }
            }
            matriz_jogo[posicaoNow] = VAZIO;
        }
    }
    
    //Remove peças do tabuleiro. Peças que fazem moinhos são protegidas
    public boolean removerPeca(int posicao, boolean branco, int jogador ){ // A variável jogador mostra de quem é a jogada, a cor inversa é quem vai ser removida
        // Protegendo as peças pretas que formam moinhos, se só houver peças que formam moinhos, então uma delas terá de ser removida e a proteção não vale mais
        if(branco && pecasPreto.size()!= 3*numTriades[1]){
            if(numTriades[1]!=0) // Caso o número de moinhos das pretas seja maior que 0
                for(int i = 0; i<3*numTriades[1];i++) // Para todas as posições das peças dos moinhos
                    if(posicao == posicoesTriadesP[i]){ // Verifica-se se a posição é a mesma a ser removida, se for a peça é protegida
                        remocaoProperty.set("A peça escolhida faz parte de um moinho ou é sua, não pode ser retirada! Escolha outra");
                        return false;// Mostrar um texto em um label na tela do jogo
                    }
        }
        if(!branco && pecasBranco.size()!= 3*numTriades[0]){
            // Protegendo as peças brancasas que formam moinhos
            if(numTriades[0]!=0)// Caso o número de moinhos das brancas seja maior que 0
                for(int i = 0; i<3*numTriades[0];i++) // Para todas as posições das peças dos moinhos
                    if(posicao == posicoesTriadesB[i]){ // // Verifica-se se a posição é a mesma a ser removida, se for a peça é protegida
                        remocaoProperty.set("A peça escolhida faz parte de um moinho ou é sua, não pode ser retirada! Escolha outra");
                        return false;// Mostrar um texto em um label na tela do jogo
                    }
        }
        //System.out.println("A posição a ser removida é " + posicao);
        if (matriz_jogo[posicao] == Tabuleiro.VAZIO || matriz_jogo[posicao] == jogador){ // A casa não tem nenhuma peça ou a peça da casa é sua 
            remocaoProperty.set("A peça escolhida faz parte de um moinho ou é sua, não pode ser retirada! Escolha outra");
            return false;// Mostrar um texto em um label na tela do jogo
        }else {
            matriz_jogo[posicao] = Tabuleiro.VAZIO; // O vetor do jogo na posição a ser removida é esvaziado
            jogadasSemPecasRemovidas = -1; // Jogadas sem remoção vai a -1 pois o método trocaTurno() irá somar 1 a esse valor
            // Removendo a posição da peça da respectiva árvore de sua cor
            if(jogador == BRANCO)
                pecasPreto.remove(posicao);
            else
                pecasBranco.remove(posicao);
            return true;
        }
    }
    
    /**
     *
     * @param turno
     * @return
     */
    // Método que identifica se ocorreu um moinho com as peças que se movimentaram naquele turno
    public boolean ocorreuMoinho(){
        // Variáveis auxiliares
        int moinhos = 0; 
        int aux[] = new int[12];
        if(turno){ // Vez das Brancas
            //Copia o vetor da vez anterior de posições dos moinhos para o auxiliar
            for(int i = 0; i< 12;i++){
                aux[i] = posicoesTriadesB[i];
            }
            //Navega-se pelas 16 opções de triades do jogo e guarda-se as posições de moinhos das peças brancas  
            for(int i = 0,j=0; i< 16; i++){
                if(pecasBranco.contains(TRIADES[i][0]) && pecasBranco.contains(TRIADES[i][1]) && pecasBranco.contains(TRIADES[i][2])){
                    moinhos++;
                    posicoesTriadesB[j] = TRIADES[i][0];
                    posicoesTriadesB[j+1] = TRIADES[i][1];
                    posicoesTriadesB[j+2] = TRIADES[i][2];
                    j+=3;
                }
            }
            // Caso o número de moinhos seja maior que o de antes
            if(moinhos > numTriades[0]){
                modoRemocao = true; // Modo de remoção é ativado
                remocaoProperty.set("Brancas podem remover uma peça preta já que fizeram um moinho"); // Atualizando texto da tela
                numTriades[0] = moinhos; // Igualando o número de moinhos contados com o da vez anterior. Antes se aumentava em um o número de moinhos. Assumindo que só houve 1 moinho quando o número contado de moinhos era maior que o do turno anterior
                return true;
            }else{
                System.out.println("Triades Brancas: "+numTriades[0]);
                if(moinhos< numTriades[0]){ // Se o número de moinhos diminuiu se igual com o número de moinhos contados
                    numTriades[0] = moinhos;
                    return false;
                }
                if(moinhos==numTriades[0]){ // Caso o número de moinhos seja o mesmo mas sejam diferentes moinhos. Caso em que se sai de um moinho e já se faz outro
                    for(int i = 0; i< 3*numTriades[0];i++){
                        if(aux[i] != posicoesTriadesB[i]){ // Para todas as peças que formam moinhos, vê-se a igualdade das posições dos moinhos. Caso uma posição de um moinho seja diferente, se sabe que um moinho novo foi criado
                            modoRemocao = true;
                            remocaoProperty.set("Brancas podem remover uma peça preta já que fizeram um moinho");
                            return true;
                        }
                    }
                }
                    
            }
        }else{
            // Vez das Pretas
            //Copia o vetor da vez anterior de posições dos moinhos para o auxiliar
            for(int i = 0; i< 12;i++)
                aux[i] = posicoesTriadesP[i];
            //Navega-se pelas 16 opções de triades do jogo e guarda-se as posições de moinhos das peças pretas
            for(int i = 0, j = 0; i< 16; i++){
                if(pecasPreto.contains(TRIADES[i][0]) && pecasPreto.contains(TRIADES[i][1]) && pecasPreto.contains(TRIADES[i][2])){
                    moinhos++;
                    posicoesTriadesP[j] = TRIADES[i][0];
                    posicoesTriadesP[j+1] = TRIADES[i][1];
                    posicoesTriadesP[j+2] = TRIADES[i][2];
                    j+=3;
                }
            }
            // Caso o número de moinhos seja maior que o de antes
            if(moinhos > numTriades[1]){
                modoRemocao = true; // Modo de remoção é ativado
                remocaoProperty.set("Pretas podem remover uma peça branca já que fizeram um moinho"); // Atualizando texto da tela
                numTriades[1] = moinhos; // Igualando o número de moinhos contados com o da vez anterior. Antes se aumentava em um o número de moinhos, Assumindo que só houve 1 moinho quando o número contado de moinhos era maior que o do turno anterior
                return true;
            }else{
                //System.out.println("Triades Pretas: "+numTriades[1]);
                if(moinhos<numTriades[1]){ // Se o número de moinhos diminuiu se igual com o número de moinhos contados
                    numTriades[1] = moinhos;
                    return false;
                }
                if(moinhos==numTriades[1]){ // Caso o número de moinhos seja o mesmo mas sejam diferentes moinhos. Caso em que se sai de um moinho e já se faz outro
                    for(int i = 0; i< 3*numTriades[1];i++){
                        if(aux[i] != posicoesTriadesP[i]){ // Para todas as peças que formam moinhos, vê-se a igualdade das posições dos moinhos. Caso uma posição de um moinho seja diferente, se sabe que um moinho novo foi criado
                            modoRemocao = true;
                            remocaoProperty.set("Pretas podem remover uma peça preta já que fizeram um moinho");
                            return true;
                        }
                    }
                }
                    
            }
        }
        return false;
    }   
    
  
    /**
     *
     * @param posicaoInicial
     * @param posicaoFinal
     */
    // Este método move uma peça de um dos times dependendo do turno. Considera-se que as posições dadas são válidas, senão ocorre erro
    public void movePeca(int posicaoInicial , int posicaoFinal ){
        if(turno){ // Movendo peças brancas
            matriz_jogo[posicaoInicial] = Tabuleiro.VAZIO;
            pecasBranco.remove(posicaoInicial);
            matriz_jogo[posicaoFinal] = Tabuleiro.BRANCO;
            pecasBranco.add(posicaoFinal);
        }else{ // Movendo peças pretas
            matriz_jogo[posicaoInicial] = Tabuleiro.VAZIO;
            pecasPreto.remove(posicaoInicial);
            matriz_jogo[posicaoFinal] = Tabuleiro.PRETO;
            pecasPreto.add(posicaoFinal);
        }
    }
    
    // Método que troca o turno, atualiza textos e atualiza o contador de rodadas 
    public void trocaTurno(){
        turno = !turno;
        if(turno)
            turnoProperty.set("Turno: Brancas jogam");
        if(!turno)
            turnoProperty.set("Turno: Pretas jogam. Aperte o botão Joga IA, por favor"); // ***Alterei: o texto de mensagem
        //System.out.println(turnoProperty.toString());
        jogadasSemPecasRemovidas++;
        rodadasProperty.set("Rodadas sem remoção de peças: " + jogadasSemPecasRemovidas);
    }
    
    // Método que verifica se alguém ganhou ou perdeu e que ativa o Super Modo 
    public boolean alguemGanhou(boolean travado){ // adicionar parâmetro do checaCasasLivres ***Alterei
        
        System.out.println("Alguém foi travado: "+travado);
        if(!superModeP && turno && travado){ // Turno das brancas e as pretas estão encurraladas
            vencer = true; // As brancas venceram
            ganharProperty.set("Parabéns!! Você faz parte de um hall seleto de pessoas. Você travou a nossa IA, ela não tem mais jogadas a fazer.");//depois disso habilitar um botão para jogar de novo. Texto do botao => Revanche/2 Round
            return vencer;
        }
        
        if(!superModeB && !turno && travado){ // Turno das pretas e as brancas estão encurraladas
            vencer = true; // As brancas venceram
            ganharProperty.set("Nossa IA ganhou!!! Wohooooooo. Você foi travado. Não possui mais jogadas");//depois disso habilitar um botão para jogar de novo. Texto do botao => Revanche/2 Round
            return vencer;
        }
        if(pecasBranco.size() <= 2 && fase2){
            vencer = true; // As peças pretas venceram
            ganharProperty.set("Nossa IA ganhou!!! Wohooooooo. Você só possui 2 peças.");//depois disso habilitar um botão para jogar de novo. Texto do botao => Revanche/2 Round
            return vencer;
        }
        if(pecasPreto.size()<=2 && fase2){
            vencer = true; // As brancas venceram
            ganharProperty.set("Parabéns!! Você faz parte de um hall seleto de pessoas. Você fez com que nossa IA ficasse com 2 peças.");//depois disso habilitar um botão para jogar de novo. Texto do botao => Revanche/2 Round
            return vencer;
        }
        if(jogadasSemPecasRemovidas >= 35){ // Um dos casos de empate
            empate = true;
            ganharProperty.set("Ocorreu um empate entre o jogador e a IA. Ocorreram 35 jogadas sem a remoção de uma peça.");
            return empate;
        }
        if(pecasBranco.size()==3 && fase2){ //  Caso em que se ativa o super modo para as brancas 
            superModeB = true;
            FASE_DO_JOGO  = 3; ///***Alterei
            //System.out.println("As brancas tem "+pecasBranco.size() + " peças");
            return false;
        }
        if(pecasPreto.size()==3 && fase2){ // Caso em que se ativa o supermodo para as pretas
            superModeP = true;
            FASE_DO_JOGO = 3; //***Alterei
            //System.out.println("As pretas tem "+ pecasPreto.size() + " peças");
            return false;
        } 
        
        return false;
    }
    
   // Imprimindo a matriz de posições
    public void imprimirTabuleiro(){
        System.out.println( toString() );
    }
    
    public void reinicia(){ //***Alterei
        //Inicializando as posições da matriz do jogo sendo todas as casas vazias
        matriz_jogo = new int[24];
        for (int i = 0; i < 24; i++) matriz_jogo[i] = Tabuleiro.VAZIO;
        //Inicializando o contador de jogadas
        jogadasSemPecasRemovidas = 0;
        //Declarando as árvores de posições
        pecasBranco.clear();// O tipo ser Integer é redundante, já que é um SortedSet<Integer>
        pecasPreto.clear();
        turno = true; // As brancas sempre começam
        turnoProperty = new SimpleStringProperty("Turno: Brancas jogam"); //Inicializando o texto de quem joga nesta vez 
        FASE_DO_JOGO = 1;
        fase1 = true; // Fase de se colocar as peças em jogo
        fase2 = false; // Fase de se mover as peças em jogo
        modoRemocao = false; // Modo de remoção começa destivado
        rodadasProperty.set("Rodadas sem remoção de peças: " + jogadasSemPecasRemovidas); // Inicializando o texto sobre o contador de rodadas
        numeroDeJogadas = 0;
        jogadas_sem_pecas_removidas = 0;
        pecaLivre[0] = pecaLivre[1] = 8;
        pretoEstaNaFase3 = brancoEstaNaFase3 = false;
        superModeB = false;
        superModeP = false;  
        vencer = false;
        empate = false;
        numTriades[0] = numTriades[1] = 0;
        for(int i = 0; i<12;i++)posicoesTriadesB[i] = -1;
        for(int i = 0; i<12;i++)posicoesTriadesP[i] = -1;
        remocaoProperty.set("A IA é muito forte! Cuidado...");
        ganharProperty.set("Quem será que irá ganhar? ");
    }

    
    
    @Override
    public String toString(){
        char[] s = new char[24];
        for (int i = 0; i < 24; i++){
            s[i] = (char)('0' + matriz_jogo[i]);
        }
        return "" +
"  "+s[ 0]+" --------- "+s[ 1]+" --------- "+s[ 2]                         + "                      0 --------- 1 --------- 2"+ "\n" +
"  |           |           |"                                              + "                      |           |           |"+ "\n" +
"  |   "+s[ 3]+" ----- "    +s[ 4]+" ----- "    +s[ 5]+"   |"              + "                      |   3 ----- 4 ----- 5   |"+ "\n" +
"  |   |       |       |   |"                                              + "                      |   |       |       |   |"+ "\n" +
"  |   |   "+s[ 6]+" - "+s[ 7]+" - "+s[ 8]+"   |   |"                      + "                      |   |   6 - 7 - 8   |   |"+ "\n" +
"  |   |   |       |   |   |"                                              + "                      |   |   |       |   |   |"+ "\n" +
"  "+s[ 9]+" - "+s[10]+" - "+s[11]+"       "+s[12]+" - "+s[13]+" - "+s[14] + "                      9 - 10- 11      12- 13- 14"+ "\n" +
"  |   |   |       |   |   |"                                              + "                      |   |   |       |   |   |"+ "\n" +
"  |   |   "+s[15]+" - "+s[16]+" - "+s[17]+"   |   |"                      + "                      |   |   15- 16- 17  |   |"+ "\n" +
"  |   |       |       |   |"                                              + "                      |   |       |       |   |"+ "\n" +
"  |   "+s[18]+" ----- "+s[19]+" ----- "+s[20]+"   |"                      + "                      |   18----- 19----- 20  |"+ "\n" +
"  |           |           |"                                              + "                      |           |           |"+ "\n" +
"  "+s[21]+" --------- "+s[22]+" --------- "+s[23]                         + "                      21--------- 22--------- 23"       ;
    }
}