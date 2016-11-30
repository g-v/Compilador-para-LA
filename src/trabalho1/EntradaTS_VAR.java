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
public class EntradaTS_VAR extends EntradaTabelaDeSimbolos{

    public EntradaTS_VAR() {
        tipo = new EntradaTS_TIPO();
    }
    
    EntradaTS_TIPO tipo;
    int dimensao;
    int nPonteiros;
}
