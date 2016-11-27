/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trabalho1;
import trabalho1.parser.LABaseVisitor;
import trabalho1.parser.LAParser;
/**
 *
 * @author Esquilo
 */
public class AnalizadorSemantico extends LABaseVisitor<Void>{

    TabelaDeSimbolos_VAR tdsVAR;
    TabelaDeSimbolos_TIPOS tdsTIPOS;
    TabelaDeSimbolos_STRCT tdsSTRCT;

    //tmp
    
    int tipo;
    
    //tmp

    @Override
    public Void visitDeclaracao_local(LAParser.Declaracao_localContext ctx) {
        if(ctx.dclLocalConst != null)
        {
            EntradaTS_TIPO etds = tdsTIPOS.verificar(ctx.tipo().getText());
            if(ctx.tipo() == null)
            {
                //erro tipo nao declarado
            }else
            {
                if(tdsVAR.verificar(ctx.IDENT().getText()) != null)
                {
                    //erro, variavel ja declarada
                }else
                    tdsVAR.inserir(ctx.IDENT().getText(), etds.valor, 1);
            }
        }else if(ctx.dclLocalTipo != null)
        {
            
        }else // variavel
        {
            visitVariavel(ctx.variavel());
        }
        return null;
    }

    @Override
    public Void visitVariavel(LAParser.VariavelContext ctx) {
        
        EntradaTS_TIPO etds = tdsTIPOS.verificar(ctx.tipo().getText());
        if(etds == null)
        {
            //erro tipo nao existe
        }else
        {
           tipo = etds.valor;
           if(tdsVAR.verificar(ctx.IDENT().getText()) != null)
           {
               //erro, variavel ja declarada
           }else
           {    
               tdsVAR.inserir(ctx.IDENT().getText(), tipo, Integer.parseInt(ctx.dimensao().getText()));
               if(ctx.mais_var() != null)
               {
                   visitMais_var(ctx.mais_var());
               }
           }
        }

        
        return null;
    }
    
    
    
    @Override
    public Void visitMais_var(LAParser.Mais_varContext ctx) {
        if(tdsVAR.verificar(ctx.IDENT().getText()) != null)
        {
            //erro, variavel ja declarada
        }else
        {
            tdsVAR.inserir(ctx.IDENT().getText(), tipo, Integer.parseInt(ctx.dimensao().getText()));
                if(ctx.mais_var() != null)
                    visitMais_var(ctx.mais_var());
        
        }
  
        return null;
    }
    
    

    
    
}
