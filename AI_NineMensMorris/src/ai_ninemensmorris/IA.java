/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ai_ninemensmorris;

import javafx.util.*;
import java.util.*; 
import javafx.beans.InvalidationListener;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 *
 * @author Caio e Mathias
 */
public class IA {
 
    public IA() {}
    
    public static int[][] vizinho = {
                    {1,9}, {0,2,4}, {1,14},
                    {4,10}, {1,3,5,7}, {4,13}, 
                    {7,11}, {4,6,8}, {7,12},
        {0,10, 21}, {3,9,18,11}, {6,10,15},  {8,13,17}, {5,12,20,14}, {2,13,23},
                    {11,16}, {15,19,17}, {12,16},
                    {10,19}, {16,18,22,20}, {13,19},
                    {9,22}, {21,19,23}, {14,22}
    };
    public static int[][] moinho = {
        // Horizontais
        {0,1,2}, {3,4,5}, {6,7,8}, {9,10,11}, {12,13,14}, {15,16,17}, {18,19,20}, {21,22,23},
        
        // Verticais
        {0,9,21}, {3,10,18}, {6,11,15}, {1,4,7}, {16,19,22}, {8,12,17}, {5,13,20}, {2,14,23}
    };
    
    public static Tabuleiro tabuleiro;
 
    public static int VAZIO = 3;
    public static int BRANCO = 1;
    public static int PRETO = 0;
    public static long tempo_por_jogada = 2;
    
    
    public static int pesoHeuristicaNumeroDePecas;
    public static int pesoHeuristicaNumeroDeMoinhos;
    public static int pesoHeuristicaNumeroDePecasBloqueadas;
    public static int pesoHeuristicaNumeroDePecasDuplas;
    public static int pesoHeuristicaNumeroDePecasTriplas;
    public static int pesoHeuristicaNumeroDePecasTriplasAdjacentes;
    public static int pesoHeuristicaConfiguracaoDeVitoria;
    
    public static SimpleStringProperty profundidadeStr = new SimpleStringProperty(); // ***Alterei aqui.Mudei aqui
       
    public static int TEMPO_MAXIMO_PARA_FASE1 = 300;
    public static int TEMPO_MAXIMO_PARA_FASE2 = 1000;
    
    
    // Guarda melhor movimento criado no método minimax
    public static Triple melhorMovimento = new Triple(-1,-1,-1);
    
    // Construtor
    IA(Tabuleiro tabuleiro_instancia){
        tabuleiro = tabuleiro_instancia;
    }
    
    public static void setTempoPorJogada(long t){
        tempo_por_jogada = t;
    }
    
    public static int NumeroDeMoinhosAdjacentes (Tabuleiro tabuleiro, int jogador){
        int []quantidade_moinhos_na_posicao = new int[24];
        for (int i = 0; i < 24; i++) quantidade_moinhos_na_posicao[i] = 0;
        
        for (int m = 0; m < moinho.length; m++){
            int contador = 0;
            for (int j = 0; j < moinho[m].length; j++){
                int posicao = moinho[m][j];
                
                if (tabuleiro.getPosicao(posicao) == jogador)
                    contador++;
            }
            
            if (contador == 3){
                for (int j = 0; j < moinho[m].length; j++){
                    int posicao = moinho[m][j];
                    
                    quantidade_moinhos_na_posicao[posicao]++;
                }
            }
        }
        
        int resposta = 0;
        for (int posicao = 0; posicao < 24; posicao++){
            if (quantidade_moinhos_na_posicao[posicao] > 1){
                resposta++;
            }
        }
        
        return resposta;
    }
    
    public static int diferencaEntreNumeroDeMoinhosAdjacentes (Tabuleiro tabuleiro){
        int diferenca = NumeroDeMoinhosAdjacentes(tabuleiro, BRANCO) - NumeroDeMoinhosAdjacentes(tabuleiro, PRETO);
        return diferenca;
    }
    
    public static int diferencaEntreNumeroDePecasTriplas (Tabuleiro tabuleiro){
        int quantidade_tripla_BRANCO = 0;
        int quantidade_tripla_PRETO = 0;
        
        for (int posicao = 0; posicao < 24; posicao++){
            for (int j = 0; j < vizinho[posicao].length-1; j++){
                int posicao2 = vizinho[posicao][j];
                int posicao3 = vizinho[posicao][j+1];
                
                // Tripla BRANCA
                if (tabuleiro.getPosicao(posicao) == BRANCO){
                    // 2 vizinhos BRANCO
                    if (tabuleiro.getPosicao(posicao2) == BRANCO && tabuleiro.getPosicao(posicao3) == BRANCO){
                        quantidade_tripla_BRANCO++;
                    }
                }
                
                // Tripla PRETO
                if (tabuleiro.getPosicao(posicao) == PRETO){
                    // 2 vizinhos BRANCO
                    if (tabuleiro.getPosicao(posicao2) == PRETO && tabuleiro.getPosicao(posicao3) == PRETO){
                        quantidade_tripla_PRETO++;
                    }
                }
            }
            
            // Tratando caso de 4 vizinhos
            if (vizinho[posicao].length == 4){
                // Tripla BRANCO
                if (tabuleiro.getPosicao(posicao) == BRANCO){
                    // 2 vizinhos BRANCO
                    if (vizinho[posicao][3] == BRANCO && vizinho[posicao][0] == BRANCO){
                        quantidade_tripla_BRANCO++;
                    }
                }
                
                // Tripla PRETO
                if (tabuleiro.getPosicao(posicao) == PRETO){
                    // 2 vizinhos PRETO
                    if (vizinho[posicao][3] == PRETO && vizinho[posicao][0] == PRETO){
                        quantidade_tripla_PRETO++;
                    }
                }
            }
        }
        
        int diferenca = quantidade_tripla_BRANCO - quantidade_tripla_PRETO;
        return diferenca;
    }
    
    public static int diferencaEntreNumeroDePecasDuplas (Tabuleiro tabuleiro){
        int quantidade_duplas_jogador1 = 0;
        int quantidade_duplas_jogador2 = 0;
        
        for (int posicao = 0; posicao < 24; posicao++){
            for (int j = 0; j < vizinho[posicao].length; j++){
                int posicao2 = vizinho[posicao][j];
                
                // Duplas BRANCAS
                if (tabuleiro.getPosicao(posicao) == BRANCO && tabuleiro.getPosicao(posicao2) == BRANCO){
                    quantidade_duplas_jogador1++;
                }
                
                // Duplas PRETAS
                if (tabuleiro.getPosicao(posicao) == PRETO && tabuleiro.getPosicao(posicao2) == PRETO){
                    quantidade_duplas_jogador2++;
                }
            }
        }
        
        quantidade_duplas_jogador1 = quantidade_duplas_jogador1 / 2;
        quantidade_duplas_jogador2 = quantidade_duplas_jogador2 / 2;
        
        int diferenca = quantidade_duplas_jogador1 - quantidade_duplas_jogador2;
        
        return diferenca;
    }
    
    public static int configuracaoVencedor(Tabuleiro tabuleiro){
        if (movimentosPermitidos(tabuleiro, PRETO).size() == 0) return 1;   // Se o PRETO ettá bloqueado, não possui nenhum movimento possível
        if (movimentosPermitidos(tabuleiro, BRANCO).size() == 0) return -1; // Se o BRANCO ettá bloqueado, não possui nenhum movimento possível
        
        int numero_pecas_jogador1 = 0;
        int numero_pecas_jogador2 = 0;
        
        for (int posicao = 0; posicao < 24; posicao++){
            if (tabuleiro.getPosicao(posicao) == BRANCO) numero_pecas_jogador1++;
            else if (tabuleiro.getPosicao(posicao) == PRETO) numero_pecas_jogador2++;
        }
        
        if (numero_pecas_jogador2 <= 2) return 1;
        else if (numero_pecas_jogador1 <= 2) return -1;
        else return 0;
    }
    
    public static int diferencaEntreNumeroDePecas(Tabuleiro tabuleiro){
        int diferenca = 0;
        
        for (int i = 0; i < 24; i++){
            if (tabuleiro.matriz_jogo[i] == BRANCO){
                diferenca++;
            }else if (tabuleiro.matriz_jogo[i] == PRETO){
                diferenca--;
            }
        }
        
        return diferenca;
    }
    
    public static int diferencaEntreNumeroDePecasBloqueadas(Tabuleiro tabuleiro){
        int diferenca = 0;
        
        for (int posicao = 0; posicao < 24; posicao++){
            // Pecas PRETA bloqueadas
            if (tabuleiro.getPosicao(posicao) == PRETO){
                
                boolean bloqueado = true;
                // Verificar se todo vizinho é BRANCO
                for (int j = 0; j < vizinho[posicao].length; j++){
                    int posicao_vizinho = vizinho[posicao][j];
                    // Se vizinho não for BRANCO então peça não está bloqueada
                    if (tabuleiro.getPosicao(posicao_vizinho) != BRANCO){
                        bloqueado = false;
                    }
                }
                
                if (bloqueado == true) diferenca++;
            }
            
            // Pecas BRANCA bloqueadas
            if (tabuleiro.getPosicao(posicao) == BRANCO){
                boolean bloqueado = true;
                // Verificar se todo vizinho é PRETO
                for (int j = 0; j < vizinho[posicao].length; j++){
                    int posicao_vizinho = vizinho[posicao][j];
                    // Se vizinho não for PRETO então peça não está bloqueada
                    if (tabuleiro.getPosicao(posicao_vizinho) != PRETO){
                        bloqueado = false;
                    }
                }
                
                if (bloqueado == true) diferenca--;
            }
        }
        
        return diferenca;
    }
    
    public static int diferencaEntreMoinhos(Tabuleiro tabuleiro){
        int diferenca = 0;
        
        for (int i = 0; i < moinho.length; i++){
            int contagem_peca_jogador1 = 0;
            int contagem_peca_jogador2 = 0;
            
            for (int j = 0; j < 3; j++){
                int posicao = moinho[i][j];
                
                if (tabuleiro.getPosicao(posicao) == BRANCO){
                    contagem_peca_jogador1++;
                }else if (tabuleiro.getPosicao(posicao) == PRETO){
                    contagem_peca_jogador2++;
                }
            }
            
            if (contagem_peca_jogador1 == 3){
                diferenca++;
            }else if (contagem_peca_jogador2 == 3){
                diferenca--;
            }
        }
        
        return diferenca;
    }
    
    public static void setPesoHeuristicaNumeroPecas(int x){
        pesoHeuristicaNumeroDePecas = x;
    }
    
    public static void setPesoHeuristicaNumeroMoinhos(int x){
        pesoHeuristicaNumeroDeMoinhos = x;
    }
    
    public static void setPesoHeuristicaNumeroPecasBloqueadas(int x){
        pesoHeuristicaNumeroDePecasBloqueadas = x;
    }
    
    public static void setPesoHeuristicaNumeroPecasDuplas(int x){
        pesoHeuristicaNumeroDePecasDuplas = x;
    }
    
    public static void setPesoHeuristicaNumeroPecasTriplas(int x){
        pesoHeuristicaNumeroDePecasTriplas = x;
    }
    
    public static void setPesoHeuristicaNumeroMoinhosAdjacentes(int x){
        pesoHeuristicaNumeroDePecasTriplasAdjacentes = x;
    }
    
    public static void setPesoHeuristicaConfiguracaoVitoria(int x){
        pesoHeuristicaConfiguracaoDeVitoria = x;
    }
    
    public static int getPesoHeuristicaNumeroPecas(){
        return pesoHeuristicaNumeroDePecas;
    }
    
    public static int getPesoHeuristicaNumeroMoinhos(){
        return pesoHeuristicaNumeroDeMoinhos;
    }
    
    public static int getPesoHeuristicaNumeroPecasBloqueadas(){
        return pesoHeuristicaNumeroDePecasBloqueadas;
    }
    
    public static int getPesoHeuristicaNumeroPecasDuplas(){
        return pesoHeuristicaNumeroDePecasDuplas;
    }
    
    public static int getPesoHeuristicaNumeroPecasTriplas(){
        return pesoHeuristicaNumeroDePecasTriplas;
    }
    
    public static int getPesoHeuristicaNumeroMoinhosAdjacentes(){
        return pesoHeuristicaNumeroDePecasTriplasAdjacentes;
    }
    
    public static int getPesoHeuristicaConfiguracaoVitoria(){
        return pesoHeuristicaConfiguracaoDeVitoria;
    }
    
    public static void setPesosHeuristicaFase1(){
        setPesoHeuristicaNumeroMoinhos(40);
        setPesoHeuristicaNumeroPecasBloqueadas(1);
        setPesoHeuristicaNumeroPecas(20);
        setPesoHeuristicaNumeroPecasDuplas(10);
        setPesoHeuristicaNumeroPecasTriplas(5);
        setPesoHeuristicaNumeroMoinhosAdjacentes(0);
        setPesoHeuristicaConfiguracaoVitoria(0);
    }
    
    public static void setPesosHeuristicaFase2(){
        setPesoHeuristicaNumeroMoinhos(45);
        setPesoHeuristicaNumeroPecasBloqueadas(10);
        setPesoHeuristicaNumeroPecas(20);
        setPesoHeuristicaNumeroPecasDuplas(1);
        setPesoHeuristicaNumeroPecasTriplas(3);
        setPesoHeuristicaNumeroMoinhosAdjacentes(3);
        setPesoHeuristicaConfiguracaoVitoria(9999);
    }
    
    public static void setPesosHeuristicaFase3(){
        setPesoHeuristicaNumeroMoinhos(20);
        setPesoHeuristicaNumeroPecasBloqueadas(0);
        setPesoHeuristicaNumeroPecas(20);
        setPesoHeuristicaNumeroPecasDuplas(5);
        setPesoHeuristicaNumeroPecasTriplas(5);
        setPesoHeuristicaNumeroMoinhosAdjacentes(0);
        setPesoHeuristicaConfiguracaoVitoria(9999);
    }
    
    public static int funcaoAvaliacao(Tabuleiro tabuleiro){
        if (tabuleiro.FASE_DO_JOGO == 1){
            setPesosHeuristicaFase1();
        }else if (tabuleiro.FASE_DO_JOGO == 2){
            setPesosHeuristicaFase2();
        }else if (tabuleiro.FASE_DO_JOGO == 3){
            setPesosHeuristicaFase3();
        }
        
        int f1 = getPesoHeuristicaNumeroPecas() * diferencaEntreNumeroDePecas(tabuleiro);
        int f2 = getPesoHeuristicaNumeroMoinhos() * diferencaEntreMoinhos(tabuleiro);
        int f3 = getPesoHeuristicaNumeroPecasBloqueadas() * diferencaEntreNumeroDePecasBloqueadas(tabuleiro);
        int f4 = getPesoHeuristicaNumeroPecasDuplas() * diferencaEntreNumeroDePecasDuplas(tabuleiro);
        int f5 = getPesoHeuristicaNumeroPecasTriplas() * diferencaEntreNumeroDePecasTriplas(tabuleiro);
        int f6 = getPesoHeuristicaNumeroMoinhosAdjacentes() * diferencaEntreNumeroDeMoinhosAdjacentes(tabuleiro);
        int f7 = getPesoHeuristicaConfiguracaoVitoria() * configuracaoVencedor(tabuleiro);
        
        return f1 + f2 + f3 + f4 + f5 + f6 + f7;
    }
    
    public static boolean eh_moinho (Tabuleiro tabuleiro, int posicao){       
        for (int i = 0; i < moinho.length; i++){           
            boolean posicaoNoMoinho = false;
            boolean moinhoMesmaCor = true;
            
            for (int j = 0; j < moinho[i].length; j++){
                int posicaoMoinho = moinho[i][j];
                
                if (posicaoMoinho == posicao){
                    posicaoNoMoinho = true;
                }
                
                // Verificar se peças possuem mesma cor
                if (tabuleiro.getPosicao(posicaoMoinho) != tabuleiro.getPosicao(posicao)){
                    moinhoMesmaCor = false;
                    break;
                }
            }
            
            if (moinhoMesmaCor == true && posicaoNoMoinho == true) 
                return true;
        }
        
        return false;
    }
    
    public static ArrayList< Triple > movimentosPermitidos (Tabuleiro tabuleiro, int jogador){
        tabuleiro.toString();
        
        if (tabuleiro.fase1) return movimentosPermitidosFase1(tabuleiro,jogador);
        else {
            if (jogador == BRANCO && tabuleiro.superModeB == true)
                return movimentosPermitidosFase3(tabuleiro,jogador);
            if (jogador == PRETO && tabuleiro.superModeP == true)
                return movimentosPermitidosFase3(tabuleiro,jogador);
            return movimentosPermitidosFase2(tabuleiro,jogador);
        }
        /*
        if (tabuleiro.FASE_DO_JOGO == 1) return movimentosPermitidosFase1(tabuleiro, jogador);
        else if (tabuleiro.FASE_DO_JOGO == 2) return movimentosPermitidosFase2(tabuleiro, jogador);
        else if (tabuleiro.FASE_DO_JOGO == 3) return movimentosPermitidosFase3(tabuleiro, jogador);
        else return null; // Aqui deveria retornar uma EXCEÇÃO!!
        */
    }
    
    public static ArrayList< Triple > movimentosPermitidosFase1 (Tabuleiro tabuleiro, int jogador){
        ArrayList< Triple > arr = new ArrayList< Triple >();
        
        for (int posicao = 0; posicao < 24; posicao++){
            if (tabuleiro.getPosicao(posicao) == VAZIO){
                
                // vis[u] = true;
                int FASE_DO_JOGO = tabuleiro.FASE_DO_JOGO;
                int numeroDeJogadas = tabuleiro.numeroDeJogadas;
                int jogadas_sem_pecas_removidas = tabuleiro.jogadas_sem_pecas_removidas;
                boolean turno = tabuleiro.turno;
                tabuleiro.adicionarPeca(posicao, jogador);
                
                if (eh_moinho(tabuleiro, posicao)){
                    
                    if (jogador == BRANCO){
                        Iterator<Integer> it2 = tabuleiro.pecasPreto.iterator();
                        boolean todos_pretos_estao_em_moinho = true;
                        
                        while (it2.hasNext()){
                            int removerPosicao = it2.next();
                            // Se peca PRETA pertencer a um Moinho NÃO podemos retirá-la
                            if (eh_moinho(tabuleiro, removerPosicao)){
                                arr.add( new Triple(posicao,posicao,24) );
                            }else {
                                todos_pretos_estao_em_moinho = false;
                                arr.add( new Triple(posicao,posicao,removerPosicao) );
                            }
                        }
                        
                        if (todos_pretos_estao_em_moinho == true){
                            // pode remover qualquer preto
                            it2 = tabuleiro.pecasPreto.iterator();
                            while (it2.hasNext()){
                                int removerPosicao = it2.next();
                                arr.add( new Triple(posicao,posicao, removerPosicao) );
                            }
                        }
                    // Jogador == PRETO
                    }else {
                        Iterator<Integer> it2 = tabuleiro.pecasBranco.iterator();
                        boolean todos_brancos_estao_em_moinho = true;
                        while (it2.hasNext()){
                            int removerPosicao = it2.next();
                            // Se peca BRANCA pertencer a um Moinho NÃO podemos retirá-la
                            if (eh_moinho(tabuleiro, removerPosicao)){
                                arr.add( new Triple(posicao,posicao, 24) );
                            }else {
                                todos_brancos_estao_em_moinho = false;
                                arr.add( new Triple(posicao,posicao,removerPosicao) );
                            }
                        }
                        
                        if (todos_brancos_estao_em_moinho == true){
                            it2 = tabuleiro.pecasBranco.iterator();
                            
                            while (it2.hasNext()){
                                int removerPosicao = it2.next();
                                arr.add( new Triple(posicao, posicao, removerPosicao) );
                            }
                        }
                    }
                    
                }else {
                    arr.add( new Triple(posicao, posicao, 24) );
                }
                // vis[u] = false
                tabuleiro.removerPeca(posicao);
                tabuleiro.FASE_DO_JOGO = FASE_DO_JOGO;
                tabuleiro.numeroDeJogadas = numeroDeJogadas;
                tabuleiro.jogadas_sem_pecas_removidas = jogadas_sem_pecas_removidas;
                tabuleiro.turno = turno;
                
            }
        }
        
        return arr;
    }
    
    public static ArrayList< Triple > movimentosPermitidosFase3 (Tabuleiro tabuleiro, int jogador){
        ArrayList< Triple > arr = new ArrayList< Triple >();
        
        if (jogador == BRANCO){
            
            if (tabuleiro.brancoEstaNaFase3 == false){
                return movimentosPermitidosFase2(tabuleiro, jogador);
                
            }else {
                ArrayList< Integer >posicoes_vazias = new ArrayList< Integer >();
                for (int posicao = 0; posicao < 24; posicao++){
                    if (tabuleiro.getPosicao(posicao) == VAZIO){
                        posicoes_vazias.add(posicao);
                    }
                }
                
                Iterator<Integer> it = tabuleiro.pecasBranco.iterator();
                while (it.hasNext()){
                    int posicao = it.next();
                    
                    Iterator<Integer> it2 = posicoes_vazias.iterator();
                    while (it2.hasNext()){
                        int novaPosicao = it2.next();
                        
                        // Verificar se eh moinho
                        Tabuleiro tabuleiro_auxiliar = new Tabuleiro(tabuleiro);
                        tabuleiro_auxiliar.moverPeca(posicao, novaPosicao);
                        
                        if (eh_moinho(tabuleiro_auxiliar, novaPosicao)){
                            
                            Iterator<Integer>it3 = tabuleiro_auxiliar.pecasPreto.iterator();
                            while (it3.hasNext()){
                                int posicaoRemovida = it3.next();
                                // Se peca PRETA pertencer a um Moinho NÃO podemos retirá-la
                                if (eh_moinho(tabuleiro_auxiliar, posicaoRemovida)){
                                    arr.add( new Triple(posicao,posicao,24) );
                                }else {
                                    arr.add( new Triple(posicao,posicao,posicaoRemovida) );
                                }
                            }
                            
                        }else {
                 //           System.out.printf ("Adicionando um movimento possivel %d %d %d %c", posicao, novaPosicao, 24, 10);
                            Triple aux = new Triple(posicao, novaPosicao, 24);
                            arr.add(aux);
                        }
                    }
                }
            }
            
        }else{
            
            if (tabuleiro.pretoEstaNaFase3 == false){
                return movimentosPermitidosFase2(tabuleiro, jogador);
                
            }else {
                ArrayList< Integer >posicoes_vazias = new ArrayList< Integer >();
                for (int posicao = 0; posicao < 24; posicao++){
                    if (tabuleiro.getPosicao(posicao) == VAZIO){
                        posicoes_vazias.add(posicao);
                    }
                }
                
                Iterator<Integer> it = tabuleiro.pecasPreto.iterator();
                while (it.hasNext()){
                    int posicao = it.next();
                    
                    Iterator<Integer> it2 = posicoes_vazias.iterator();
                    while (it2.hasNext()){
                        int novaPosicao = it2.next();
                        
                        // Verificar se eh moinho
                        Tabuleiro tabuleiro_auxiliar = new Tabuleiro(tabuleiro);
                        tabuleiro_auxiliar.moverPeca(posicao, novaPosicao);
                        
                        if (eh_moinho(tabuleiro_auxiliar, novaPosicao)){
                            
                            Iterator<Integer>it3 = tabuleiro_auxiliar.pecasBranco.iterator();
                            while (it3.hasNext()){
                                int posicaoRemovida = it3.next();
                                // Se peca PRETA pertencer a um Moinho NÃO podemos retirá-la
                                if (eh_moinho(tabuleiro_auxiliar, posicaoRemovida)){
                                    arr.add( new Triple(posicao,posicao,24) );
                                }else {
                                    arr.add( new Triple(posicao,posicao,posicaoRemovida) );
                                }
                            }
                            
                        }else {
                 //           System.out.printf ("Adicionando um movimento possivel %d %d %d %c", posicao, novaPosicao, 24, 10);
                            Triple aux = new Triple(posicao, novaPosicao, 24);
                            arr.add(aux);
                        }
                    }
                }
            }
        }
        System.out.printf("Movimentos permitidos do Jogador %d: %c", jogador,10);
        for (int i = 0; i < arr.size(); i++){
            System.out.printf("%d %d %d %c", arr.get(i).x, arr.get(i).y, arr.get(i).z, 10);
        }
        System.out.printf("%c", 10);
        
        return arr;
    }
    
    public static ArrayList< Triple > movimentosPermitidosFase2 (Tabuleiro tabuleiro, int jogador){
        ArrayList< Triple > arr = new ArrayList< Triple >();
        
        SortedSet<Integer> pecasBranco = new TreeSet<>(tabuleiro.pecasBranco);
        SortedSet<Integer> pecasPreto = new TreeSet<>(tabuleiro.pecasPreto);
        
        if (jogador == BRANCO){
            // Para cada peca, tentar ir para todos os vizinhos
            // lembrando de verificar se o movimento cria um novo moinho
            // Iterator<Integer>it = tabuleiro.pecasBranco.iterator();
            
            Iterator<Integer> iterador = pecasBranco.iterator();
            while (iterador.hasNext()){
                int posicao = iterador.next();
               // System.out.printf("Tentando movimentar peca %d %c", posicao, 10);
                
                for (int j = 0; j < vizinho[posicao].length; j++){
                    int novaPosicao = vizinho[posicao][j];
                    
                    // PosicaoVizinho está vazia
                    if (tabuleiro.getPosicao(novaPosicao) == VAZIO){
                       
                        // Verificar se eh moinho
                        //Tabuleiro tabuleiro_auxiliar = new Tabuleiro(tabuleiro);
                        //tabuleiro_auxiliar.moverPeca(posicao, novaPosicao);
                        
                        // vis[u] = true;
                        int FASE_DO_JOGO = tabuleiro.FASE_DO_JOGO;
                        int numeroDeJogadas = tabuleiro.numeroDeJogadas;
                        int jogadas_sem_pecas_removidas = tabuleiro.jogadas_sem_pecas_removidas;
                        boolean turno = tabuleiro.turno;
                        tabuleiro.moverPeca(posicao, novaPosicao);
                        
                        if (eh_moinho(tabuleiro, novaPosicao)){
                            
                            Iterator<Integer>it2 = pecasPreto.iterator();
                            while (it2.hasNext()){
                                int removerPosicao = it2.next();
                                // Se peca PRETA pertencer a um Moinho NÃO podemos retirá-la
                                if (eh_moinho(tabuleiro, removerPosicao)){
                                    arr.add( new Triple(posicao, novaPosicao,24) );
                                }else {
                                    arr.add( new Triple(posicao, novaPosicao, removerPosicao) );
                                }
                            }
                            
                        }else {
                 //         System.out.printf ("Adicionando um movimento possivel %d %d %d %c", posicao, novaPosicao, 24, 10);
                            Triple aux = new Triple(posicao, novaPosicao, 24);
                            arr.add(aux);
                        }
                        
                        
                        // vis[u] = false
                        tabuleiro.moverPeca(novaPosicao, posicao);
                        tabuleiro.FASE_DO_JOGO = FASE_DO_JOGO;
                        tabuleiro.numeroDeJogadas = numeroDeJogadas;
                        tabuleiro.jogadas_sem_pecas_removidas = jogadas_sem_pecas_removidas;
                        tabuleiro.turno = turno;
                    }
                }
                
            }
            
        }else if (jogador == PRETO){
            // Para cada peca, tentar ir para todos os vizinhos
            // lembrando de verificar se o movimento cria um novo moinho
            Iterator<Integer>it = pecasPreto.iterator();
            
            while (it.hasNext()){
                int posicao = it.next();
                
                for (int j = 0; j < vizinho[posicao].length; j++){
                    int novaPosicao = vizinho[posicao][j];
                    
                    // PosicaoVizinho está vazia
                    if (tabuleiro.getPosicao(novaPosicao) == VAZIO){
                        
                        // Verificar se eh moinho
                        // vis[u] = true
                        int FASE_DO_JOGO = tabuleiro.FASE_DO_JOGO;
                        int numeroDeJogadas = tabuleiro.numeroDeJogadas;
                        int jogadas_sem_pecas_removidas = tabuleiro.jogadas_sem_pecas_removidas;
                        boolean turno = tabuleiro.turno;
                        tabuleiro.moverPeca(posicao, novaPosicao);
                        
                        if (eh_moinho(tabuleiro, novaPosicao)){
                            
                            Iterator<Integer>it2 = pecasBranco.iterator();
                            while (it2.hasNext()){
                                int removerPosicao = it2.next();
                                // Se peca BRANCA pertencer a um Moinho NÃO podemos retirá-la
                                if (eh_moinho(tabuleiro, removerPosicao)){
                                    arr.add( new Triple(posicao, novaPosicao,24) );
                                }else {
                                    arr.add( new Triple(posicao, novaPosicao, removerPosicao) );
                                }
                            }
                            
                        }else {
                            Triple aux = new Triple(posicao, novaPosicao, 24);
                            arr.add(aux);
                        }
                        
                        // vis[u] = false
                        tabuleiro.moverPeca(novaPosicao, posicao);
                        tabuleiro.FASE_DO_JOGO = FASE_DO_JOGO;
                        tabuleiro.numeroDeJogadas = numeroDeJogadas;
                        tabuleiro.jogadas_sem_pecas_removidas = jogadas_sem_pecas_removidas;
                        tabuleiro.turno = turno;
                    }
                }
            }
        }
        
        
        return arr;
    }
    
    public static int minimax(int profundidade_atual, int profundidadeMaxima, int jogador, int alfa, int beta){
        
        // Nó folha
        if (profundidade_atual == profundidadeMaxima){
            return funcaoAvaliacao(tabuleiro);
        }
        
        // Jogador que maximiza
        if (jogador == BRANCO){
            int maiorValor = Integer.MIN_VALUE;
               
            ArrayList< Triple >movimento = movimentosPermitidos(tabuleiro, jogador);
            for (int j = 0; j < movimento.size(); j++){
                int posicao = movimento.get(j).x;
                int novaPosicao = movimento.get(j).y;
                int posicaoRemovida = movimento.get(j).z;
                
                int proximoJogador = PRETO;
                
                // vis[u] = true
                int FASE_DO_JOGO = tabuleiro.FASE_DO_JOGO;
                int numeroDeJogadas = tabuleiro.numeroDeJogadas;
                int jogadas_sem_pecas_removidas = tabuleiro.jogadas_sem_pecas_removidas;
                boolean turno = tabuleiro.turno;
                if (posicao == novaPosicao){
                    tabuleiro.adicionarPeca(posicao, jogador);
                    if (0 <= posicaoRemovida && posicaoRemovida < 24)
                        tabuleiro.removerPeca(posicaoRemovida);
                    
                }else {
                    tabuleiro.moverPeca(posicao, novaPosicao);
                    
                    if (0 <= posicaoRemovida && posicaoRemovida < 24)
                        tabuleiro.removerPeca(posicaoRemovida);             
                }
                
                int valor = minimax(profundidade_atual+1, profundidadeMaxima, proximoJogador, alfa, beta);
                if (profundidade_atual == 0){
                    System.out.printf ("movimento = (%d %d %d), valor = %d, fase_do_jogo = %d %c", posicao, novaPosicao, posicaoRemovida, valor, tabuleiro.FASE_DO_JOGO, 10);
                }
                
                // vis[u] = false;
                if (posicao == novaPosicao){
                    tabuleiro.removerPeca(posicao);
                    if (0 <= posicaoRemovida && posicaoRemovida < 24)
                        tabuleiro.adicionarPeca(posicaoRemovida, proximoJogador);
                    
                }else {
                    tabuleiro.moverPeca(novaPosicao, posicao);
                    
                    if (0 <= posicaoRemovida && posicaoRemovida < 24)
                        tabuleiro.adicionarPeca(posicaoRemovida, proximoJogador);                    
                }
                tabuleiro.FASE_DO_JOGO = FASE_DO_JOGO;
                tabuleiro.numeroDeJogadas = numeroDeJogadas;
                tabuleiro.jogadas_sem_pecas_removidas = jogadas_sem_pecas_removidas;
                tabuleiro.turno = turno;
                
                
                if (valor > maiorValor){
                    maiorValor = valor;
                    if (profundidade_atual == 0)
                        melhorMovimento = new Triple(posicao,novaPosicao,posicaoRemovida);
                }
                
                alfa = Integer.max(alfa, maiorValor);
                
                if (beta <= alfa){
                    break;
                }
            }
            
            return maiorValor;
        
        // Jogador que minimiza
        }else {
            int menorValor = Integer.MAX_VALUE;
            
            ArrayList< Triple >movimento = movimentosPermitidos(tabuleiro, jogador);

            for (int j = 0; j < movimento.size(); j++){
                int posicao = movimento.get(j).x;
                int novaPosicao = movimento.get(j).y;
                int posicaoRemovida = movimento.get(j).z;
                
                int proximoJogador = BRANCO;
                
                // vis[u] = true
                int FASE_DO_JOGO = tabuleiro.FASE_DO_JOGO;
                int numeroDeJogadas = tabuleiro.numeroDeJogadas;
                int jogadas_sem_pecas_removidas = tabuleiro.jogadas_sem_pecas_removidas;
                boolean turno = tabuleiro.turno;
                if (posicao == novaPosicao){
                    tabuleiro.adicionarPeca(posicao, jogador);
                    if (0 <= posicaoRemovida && posicaoRemovida < 24){
                        tabuleiro.removerPeca(posicaoRemovida);
                    }
                }else {
                    tabuleiro.moverPeca(posicao, novaPosicao);
                    
                    if (0 <= posicaoRemovida && posicaoRemovida < 24)
                        tabuleiro.removerPeca(posicaoRemovida);
                    
                }
                
                int valor = minimax(profundidade_atual+1, profundidadeMaxima, proximoJogador, alfa, beta);
                
                // vis[u] = false;
                if (posicao == novaPosicao){
                    tabuleiro.removerPeca(posicao);
                    if (0 <= posicaoRemovida && posicaoRemovida < 24)
                        tabuleiro.adicionarPeca(posicaoRemovida, proximoJogador);
                }else {
                    tabuleiro.moverPeca(novaPosicao, posicao);
                    
                    if (0 <= posicaoRemovida && posicaoRemovida < 24)
                        tabuleiro.adicionarPeca(posicaoRemovida, proximoJogador);
                }
                tabuleiro.FASE_DO_JOGO = FASE_DO_JOGO;
                tabuleiro.numeroDeJogadas = numeroDeJogadas;
                tabuleiro.jogadas_sem_pecas_removidas = jogadas_sem_pecas_removidas;
                tabuleiro.turno = turno;
                
                if (valor < menorValor){
                    menorValor = valor;
                    if (profundidade_atual == 0)
                        melhorMovimento = new Triple(posicao,novaPosicao,posicaoRemovida);
                }
                
                beta = Integer.min(beta, menorValor);
                
                if (beta <= alfa){
                    break;
                }
            }
            
            return menorValor;
        }
    }
    
    public static Triple melhorMovimento(Tabuleiro tabuleiro, int jogador){
        IA.tabuleiro = tabuleiro;

        Triple melhor_movimento = new Triple();
        
        System.out.println( tabuleiro.toString() );
        
        int profundidade_escolhida_pelo_jogador = Integer.parseInt(profundidadeStr.getValueSafe()); // ***Alterei:  Setando a profundidade selecionada na GUI na IA
        System.out.println("Profundidade escolhida: " + profundidade_escolhida_pelo_jogador);    
        
        int profundidade_maxima = profundidade_escolhida_pelo_jogador;
        if (profundidade_maxima == 8) profundidade_maxima = 49; // Ultimo nível, a IA chegará na maior profundidade que conseguir
        
        int TEMPO_MAXIMO_DA_IA;
        if (tabuleiro.fase1) TEMPO_MAXIMO_DA_IA = TEMPO_MAXIMO_PARA_FASE1;
        else TEMPO_MAXIMO_DA_IA = TEMPO_MAXIMO_PARA_FASE2;
        
        long start,end;
        int profundidade = 1;
        
        do {
            start = System.currentTimeMillis();
            
            minimax(0, profundidade, jogador, Integer.MIN_VALUE, Integer.MAX_VALUE);
            melhor_movimento = melhorMovimento;
            
            end = System.currentTimeMillis();
            
            
            System.out.printf("Tempo gasto na profundidade %d = %dms %c %c", profundidade, end-start, 10, 10);
            
            // Atualiza profundidade do BAI
            profundidade++;
             
            // dado em ms (milisegundos)       
        }while ( (end - start) < TEMPO_MAXIMO_DA_IA && profundidade <= profundidade_maxima);
        
        
        System.out.printf ("Profundidade_maxima alcancada = %d %c", profundidade-1, 10);
        System.out.printf ("Melhor movimento da IA = %d %d %d %c", melhor_movimento.x, melhor_movimento.y, melhor_movimento.z, 10);
        
        
        return melhor_movimento;
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        tabuleiro = new Tabuleiro();
        /*
        tabuleiro.adicionarPeca(1,1);
        tabuleiro.adicionarPeca(9,1);
        tabuleiro.adicionarPeca(10,1);
        tabuleiro.adicionarPeca(21,1);
        tabuleiro.adicionarPeca(22,1);
        tabuleiro.adicionarPeca(23,1);
        
        tabuleiro.adicionarPeca(6, 2);
        tabuleiro.adicionarPeca(11, 2);
        tabuleiro.adicionarPeca(15, 2);
        tabuleiro.adicionarPeca(18, 2);
        tabuleiro.adicionarPeca(19, 2);
        tabuleiro.adicionarPeca(20, 2);
        */
        
        /*
        tabuleiro.adicionarPeca(3,1);
        tabuleiro.adicionarPeca(4,1);
        tabuleiro.adicionarPeca(5,1);
        tabuleiro.adicionarPeca(11,1);
        tabuleiro.adicionarPeca(12,1);
        tabuleiro.adicionarPeca(14,1);
        tabuleiro.adicionarPeca(21,1);
        
        tabuleiro.adicionarPeca(1,2);
        tabuleiro.adicionarPeca(7,2);
        tabuleiro.adicionarPeca(18,2);
        tabuleiro.adicionarPeca(22,2);
        tabuleiro.adicionarPeca(19,2);
        */
        
        
        tabuleiro.adicionarPeca(5,PRETO);
        tabuleiro.adicionarPeca(13,PRETO);
        tabuleiro.adicionarPeca(20,PRETO);
        tabuleiro.adicionarPeca(11,PRETO);
        tabuleiro.adicionarPeca(10,PRETO);
        tabuleiro.adicionarPeca(18,PRETO);
        
        tabuleiro.adicionarPeca(8,BRANCO);
        tabuleiro.adicionarPeca(12,BRANCO);
        tabuleiro.adicionarPeca(17,BRANCO);
        tabuleiro.adicionarPeca(19,BRANCO);
        tabuleiro.adicionarPeca(22,BRANCO);
        
        
        System.out.println( tabuleiro.toString() );
        
                
        
        System.out.println("Diferencas");
        System.out.printf("Numero de pecas: %d %c", diferencaEntreNumeroDePecas(tabuleiro), (char)10);
        System.out.printf("Numero de pecas bloqueadas: %d %c", diferencaEntreNumeroDePecasBloqueadas(tabuleiro), (char)10);
        System.out.printf("Numero de pecas duplas: %d %c", diferencaEntreNumeroDePecasDuplas(tabuleiro), (char)10);
        System.out.printf("Numero de pecas triplas: %d %c", diferencaEntreNumeroDePecasTriplas(tabuleiro), (char)10);
        System.out.printf("Numero de pecas triplas adjacentes: %d %c", diferencaEntreNumeroDeMoinhosAdjacentes(tabuleiro), (char)10);
        System.out.printf ("Numero de moinhos: %d %c", diferencaEntreMoinhos(tabuleiro), 10);
        
       
        tempo_por_jogada = 5;
       
        //tabuleiro.FASE_DO_JOGO = 1;
        tabuleiro.FASE_DO_JOGO = 2;
        melhorMovimento(tabuleiro, BRANCO);
        
        System.out.printf("O melhor movimento eh: %d %d %d %c", melhorMovimento.x, melhorMovimento.y, melhorMovimento.z, 10);
        
    
    }

}
