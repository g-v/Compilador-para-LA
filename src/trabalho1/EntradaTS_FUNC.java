/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trabalho1;

/**
 *
 * @author Lucas
 */
public class EntradaTS_FUNC extends EntradaTabelaDeSimbolos{
    int nArgumentos;
    int tipoDeRetorno;
    int nPonteirosRetorno;
    TabelaDeSimbolos_VAR tabelaArgumentos;

    public EntradaTS_FUNC() {
        tabelaArgumentos = new TabelaDeSimbolos_VAR();
        nArgumentos = 0;
    }
    
    public void inserirVar(String nome, EntradaTS_TIPO tipo, int dimensao, int nPonteiros) {
        tabelaArgumentos.inserir(nome, tipo, dimensao, nPonteiros);
    }
    
    public EntradaTS_VAR verificarVar(String nome) {
        return tabelaArgumentos.verificar(nome);
    }
    
}

