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
public class TabelaDeSimbolos_STRCT {
private Map<String, EntradaTS_STRCT> tabelaDeSimbolos;
    
    public TabelaDeSimbolos_STRCT() {
        tabelaDeSimbolos = new HashMap<>();
    }
    
    public void inserir(String nome) {
        EntradaTS_STRCT etds = new EntradaTS_STRCT();
        etds.nome = nome;
        tabelaDeSimbolos.put(nome, etds);
    }
    
    public boolean inserirVarEmSTRCT(String nome, String nomeVar, EntradaTS_TIPO tipoVar, int dimensaoVar, int nPonteiros) {
        EntradaTS_STRCT etds = verificar(nome);
        if(etds.verificarVar(nomeVar) != null)
            return false;
        else
        {
            etds.inserirVar(nomeVar, tipoVar, dimensaoVar, nPonteiros);
            tabelaDeSimbolos.put(nome, etds);
            return true;
        }
    }
    
    public EntradaTS_VAR verificarVarEmSTRCT(String nome, String nomeVar)
    {
        EntradaTS_STRCT etds = verificar(nome);
        if(etds == null)
            return null;
        else
            return etds.verificarVar(nomeVar);
    }
    
    public EntradaTS_STRCT verificar(String nome) {
        if(!tabelaDeSimbolos.containsKey(nome))
            return null;
        else return tabelaDeSimbolos.get(nome);
    }
}
