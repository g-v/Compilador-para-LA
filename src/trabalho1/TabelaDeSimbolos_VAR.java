/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trabalho1;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Esquilo
 */

public class TabelaDeSimbolos_VAR {
    private Map<String, EntradaTS_VAR> tabelaDeSimbolos;
    
    public TabelaDeSimbolos_VAR() {
        tabelaDeSimbolos = new HashMap<>();
    }
    
    public void inserir(String nome, int tipo, int dimensao) {
        EntradaTS_VAR etds = new EntradaTS_VAR();
        etds.nome = nome;
        etds.tipo = tipo;
        etds.dimensao = dimensao;
        tabelaDeSimbolos.put(nome, etds);
    }
    
    public EntradaTS_VAR verificar(String nome) {
        if(!tabelaDeSimbolos.containsKey(nome))
            return null;
        else return tabelaDeSimbolos.get(nome);
    }
}