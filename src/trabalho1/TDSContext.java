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
    
    int STRCTLevel;
    TDSContext()
    {
        structContext = new STRCTContext(new TabelaDeSimbolos_STRCT(), new TabelaDeSimbolos_VAR());
        tabelaDeTipos = new TabelaDeSimbolos_TIPOS();
        
        STRCTLevel = 0;
    }
    
    void setCurrentStructure(String name)
    {
        structContext.setSTRCTContext(name);
    }
    
    void setNoStructure()
    {
        structContext.setSTRCTContext(0);
    }
    
    void enterSTRCTLevel(String nomeEstrutura)
    {
        structContext.enterSTRCTLevel(nomeEstrutura);
        STRCTLevel++;
    }
    
    void leaveSTRCTLevel()
    {
        structContext.leaveSTRCTLevel();
        STRCTLevel--;
    }
    
    int getSTRCTLevel()
    {
        return STRCTLevel;
    }
    
    void insereVAR(String nome, int tipo, int dimensao, int nPonteiros)
    {
        structContext.insereVariavel(nome, tipo, dimensao, nPonteiros);
    }
    
    EntradaTS_VAR verificaVAR(String nome)
    {
        return structContext.verificaVariavel(nome);
    }
    
    void insereTIPO(String nome, int valor, int nPonteiros, String tipoAlias, boolean isStructure)
    {
        tabelaDeTipos.inserir(nome, valor, nPonteiros, tipoAlias, isStructure);
    }
    
    EntradaTS_TIPO verificaTIPO(String nome)
    {
        return tabelaDeTipos.verificar(nome);
    }
}
