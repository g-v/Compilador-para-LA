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
public class TDSContext {
    STRCTContext structContext;
    
    TabelaDeSimbolos_TIPOS tabelaDeTipos;
    
    TDSContext()
    {
        structContext = new STRCTContext(new TabelaDeSimbolos_STRCT(), new TabelaDeSimbolos_VAR());
        tabelaDeTipos = new TabelaDeSimbolos_TIPOS();
    }
    
    void setCurrentStructure(String name)
    {
        structContext.setSTRCTContext(name);
    }
    
    void setNoStructure()
    {
        structContext.setSTRCTContext(0);
    }
    
    void insereVAR(String nome, int tipo, int dimensao, int nPonteiros)
    {
        structContext.insereVariavel(nome, tipo, dimensao, nPonteiros);
    }
    
    EntradaTS_VAR verificaVAR(String nome)
    {
        return structContext.verificaVariavel(nome);
    }
    
    void insereTIPO(String nome, int valor, int nPonteiros, boolean isStructure)
    {
        tabelaDeTipos.inserir(nome, valor, nPonteiros, isStructure);
    }
    
    EntradaTS_TIPO verificaTIPO(String nome)
    {
        return tabelaDeTipos.verificar(nome);
    }
}
