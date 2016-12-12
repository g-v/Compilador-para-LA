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
        structContext = new STRCTContext();
        tabelaFunc = new TabelaDeSimbolos_FUNC();
        tabelaDeTipos = new TabelaDeSimbolos_TIPOS();
        
        STRCTLevel = 0;
        FUNCMode = false;
    }
    
    void setCurrentStructure(String name)
    {
        structContext.setSTRCTContext(name);
        if(STRCTLevel == 0)
            STRCTLevel++;
    }
    
    void setNoStructure()
    {
        structContext.setSTRCTContext(0);
    }
    
    void enterSTRCTLevel()
    {
        structContext.enterSTRCTLevel();
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
    
    void insereVAR(String nome, EntradaTS_TIPO tipo, int nPonteiros, String... dimensao)
    {
        if(FUNCMode == false)
        {
            structContext.insereVariavel(nome, tipo, nPonteiros, dimensao);
        }
        else
            tabelaFunc.inserirVarEmFUNC(nomeFUNC, nome, tipo, nPonteiros, dimensao);
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
    
    void criaNovoEscopo()
    {
        structContext.criaNovoEscopo();
    }
    
    void abandonaEscopo()
    {
        structContext.abandonaEscopo();
    }
    
    EntradaTS_VAR recuperaArg(int i)
    {
        EntradaTS_FUNC etds = verificaFUNC(nomeFUNC);
        return etds.recuperarArgumento(i);
    }
    
    void insereConversaoDeTipo(String tipo, String convertePara)
    {
        EntradaTS_TIPO etds = verificaTIPO(tipo);
        if(etds != null)
            if(verificaTIPO(convertePara) != null)
                etds.insereConversao(convertePara);
    }
    
    boolean tiposEquivalentes(String tipo1, String tipo2, boolean doReverse)
    {
        boolean converte = false;
        EntradaTS_TIPO etds = verificaTIPO(tipo1);
        EntradaTS_TIPO etdsAlias;
        if(etds == null)
            return false;
        if(verificaTIPO(tipo2) == null)
            return false;
        
        if(tipo1.equals(tipo2))
            return true;
        
        for(String t : etds.getConversoes())
        {
            converte = t.equals(tipo2);
            if(converte == true)
                break;
            
            etdsAlias = verificaTIPO(t);
            if(etdsAlias.tipoAlias != null)
                converte = etdsAlias.tipoAlias.equals(tipo2);
            if(converte == true)
                break;
        }
        if(converte == false && doReverse == true)
        {
            etds = verificaTIPO(tipo2);
            
            for(String t : etds.getConversoes())
            {
                converte = t.equals(tipo1);
                if(converte == true)
                    break;
                
                etdsAlias = verificaTIPO(t);
                converte = etdsAlias.tipoAlias.equals(tipo1);
                if(converte == true)
                    break;
            }
        }
        
        return converte;
    }
    
    boolean tiposIguais(String tipo1, String tipo2)
    {
        return tipo1.equals(tipo2);
    }
}
