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
public class STRCTContext {
    String structName;
    int mode;
    TabelaDeSimbolos_STRCT tabelaDeEstruturas;
    TabelaDeSimbolos_VAR tabelaVARBase;
    
    STRCTContext(TabelaDeSimbolos_STRCT tabela, TabelaDeSimbolos_VAR tabelaBase)
    {
        tabelaDeEstruturas = tabela;
        tabelaVARBase = tabelaBase;
        mode = 0;
    }
    
    void setSTRCTContext(String name)
    {
        structName = name;
        mode = 1;
    }
    
    void setSTRCTContext(int modo)
    {
        if(modo > 0)
            mode = 1;
        else
            mode = 0;
    }
    
    void insereVariavel(String nome, int tipo, int dimensao, int nPonteiros)
    {
        if(mode == 0)
        {
            tabelaVARBase.inserir(nome, tipo, dimensao, nPonteiros);
        }else
        {
            tabelaDeEstruturas.inserirVarEmSTRCT(structName, nome, tipo, dimensao, nPonteiros);
        }
    }
    
    EntradaTS_VAR verificaVariavel(String nome)
    {
        if(mode == 0)
        {
            return tabelaVARBase.verificar(nome);
        }else
        {
            return tabelaDeEstruturas.verificarVarEmSTRCT(structName, nome);
        }
    }
}
