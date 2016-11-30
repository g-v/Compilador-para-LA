/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trabalho1;

/**
 *
 * @author Esquilo
 */
public class EntradaTS_STRCT extends EntradaTabelaDeSimbolos{
    public TabelaDeSimbolos_VAR variaveis;
    public TabelaDeSimbolos_STRCT estruturasAninhadas;
    
    EntradaTS_STRCT()
    {
        variaveis = new TabelaDeSimbolos_VAR();
        estruturasAninhadas = new TabelaDeSimbolos_STRCT();
    }
    
    public void inserirVar(String nome, EntradaTS_TIPO tipo, int dimensao, int nPonteiros) {
        variaveis.inserir(nome, tipo, dimensao, nPonteiros);
    }
    
    public EntradaTS_VAR verificarVar(String nome) {
        return variaveis.verificar(nome);
    }
}
