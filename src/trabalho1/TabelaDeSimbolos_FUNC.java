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
public class TabelaDeSimbolos_FUNC {
    private Map<String, EntradaTS_FUNC> tabelaDeSimbolos;
    
    public TabelaDeSimbolos_FUNC() {
        tabelaDeSimbolos = new HashMap<>();
    }
    
    public void inserir(String nome, String tipoRetorno, int nPonteirosRetorno) {
        EntradaTS_FUNC etds = new EntradaTS_FUNC();
        etds.nome = nome;
        etds.tipoDeRetorno = tipoRetorno;
        etds.nPonteirosRetorno = nPonteirosRetorno;
        tabelaDeSimbolos.put(nome, etds);
    }
    
    public EntradaTS_FUNC verificar(String nome) {
        if(!tabelaDeSimbolos.containsKey(nome))
            return null;
        else return tabelaDeSimbolos.get(nome);
    }
    
    public boolean inserirVarEmFUNC(String nome, String nomeVar, EntradaTS_TIPO tipoVar, int dimensaoVar, int nPonteiros) {
        EntradaTS_FUNC etds = verificar(nome);
        if(etds.verificarVar(nomeVar) != null)
            return false;
        else
        {
            etds.inserirVar(nomeVar, tipoVar, dimensaoVar, nPonteiros);
            return true;
        }
    }
    
    public EntradaTS_VAR verificarVarEmFUNC(String nome, String nomeVar)
    {
        EntradaTS_FUNC etds = verificar(nome);
        if(etds == null)
            return null;
        else
            return etds.verificarVar(nomeVar);
    }
    
    void incNumeroArgumentosFunc(String nome)
    {
        EntradaTS_FUNC etds = verificar(nome);
        etds.nArgumentos++;
    }
    
    void setNumeroArgumentosFunc(String nome, int n)
    {
        EntradaTS_FUNC etds = verificar(nome);
        etds.nArgumentos = n;
    }
}
