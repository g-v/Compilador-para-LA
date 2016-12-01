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
    
    TabelaDeSimbolos_FUNC tabelaFunc;
    TabelaDeSimbolos_TIPOS tabelaDeTipos;
    
    int STRCTLevel;
    boolean FUNCMode;
    String nomeFUNC;
    TDSContext()
    {
        structContext = new STRCTContext(new TabelaDeSimbolos_STRCT(), new TabelaDeSimbolos_VAR());
        tabelaFunc = new TabelaDeSimbolos_FUNC();
        tabelaDeTipos = new TabelaDeSimbolos_TIPOS();
        
        STRCTLevel = 0;
        FUNCMode = false;
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
    
    void setFUNCMode(String nome)
    {
        nomeFUNC = nome;
        FUNCMode = true;
    }
    
    void leaveFUNCMode()
    {
        nomeFUNC = "main";
        FUNCMode = false;
    }
    
    void insereVAR(String nome, EntradaTS_TIPO tipo, int dimensao, int nPonteiros)
    {
        if(FUNCMode == false)
            structContext.insereVariavel(nome, tipo, dimensao, nPonteiros);
        else
            tabelaFunc.inserirVarEmFUNC(nome, nomeFUNC, tipo, dimensao, nPonteiros);
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
    
    void insereFUNC(String nome, String tipoRetorno, int nPonteirosRetorno)
    {
        tabelaFunc.inserir(nome, tipoRetorno, nPonteirosRetorno);
    }
    
    void incNumeroArgumentosFunc(String nome)
    {
        tabelaFunc.incNumeroArgumentosFunc(nome);
    }
    
    void setNumeroArgumentosFunc(String nome, int n)
    {
        tabelaFunc.setNumeroArgumentosFunc(nome, n);
    }
    
    EntradaTS_FUNC verificaFUNC(String nome)
    {
        return tabelaFunc.verificar(nome);
    }
    
    EntradaTS_VAR recuperaArg(int i)
    {
        EntradaTS_FUNC etds = verificaFUNC(nomeFUNC);
        return etds.recuperarArgumento(i);
    }
}
