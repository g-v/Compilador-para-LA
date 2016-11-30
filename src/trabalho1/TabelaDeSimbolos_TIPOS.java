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
public class TabelaDeSimbolos_TIPOS {
private Map<String, EntradaTS_TIPO> tabelaDeSimbolos;
    
    int indiceAtual;

    public TabelaDeSimbolos_TIPOS() {
        tabelaDeSimbolos = new HashMap<>();
        indiceAtual = 0;
    }
    
    public void inserir(String nome, int valor, int nPonteiros, String tipoAlias, boolean isStructure) {
        EntradaTS_TIPO etds = new EntradaTS_TIPO();
        etds.nome = nome;
        etds.valor = valor;
        etds.nPonteiros = nPonteiros;
        etds.isStructure = isStructure;
        tabelaDeSimbolos.put(nome, etds);
        indiceAtual++;
    }
    
    public EntradaTS_TIPO verificar(String nome) {
        if(!tabelaDeSimbolos.containsKey(nome))
            return null;
        else return tabelaDeSimbolos.get(nome);
    }
}
