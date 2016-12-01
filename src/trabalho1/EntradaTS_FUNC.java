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
 * @author Lucas
 */
public class EntradaTS_FUNC extends EntradaTabelaDeSimbolos{
    int nArgumentos;
    String tipoDeRetorno;
    int nPonteirosRetorno;
    private Map<Integer, String> nomesArgumentos;
    TabelaDeSimbolos_VAR tabelaArgumentos;

    public EntradaTS_FUNC() {
        tabelaArgumentos = new TabelaDeSimbolos_VAR();
        nArgumentos = 0;
        nomesArgumentos = new HashMap();
    }
    
    public EntradaTS_VAR recuperarArgumento(int i)
    {
        if(!nomesArgumentos.containsKey(i))
            return null;
        else return verificarVar(nomesArgumentos.get(i));
    }
    
    public void inserirVar(String nome, EntradaTS_TIPO tipo, int dimensao, int nPonteiros) {
        tabelaArgumentos.inserir(nome, tipo, dimensao, nPonteiros);
    }
    
    public EntradaTS_VAR verificarVar(String nome) {
        return tabelaArgumentos.verificar(nome);
    }
    
}

