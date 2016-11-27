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
    
    public void inserirVar(String nome, int valor, int dimensao) {
        variaveis.inserir(nome, valor, dimensao);
    }
    
    public EntradaTS_VAR verificarVar(String nome) {
        return variaveis.verificar(nome);
    }
}
