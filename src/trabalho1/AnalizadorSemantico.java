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
    int nPonteiros;
    
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
                    tdsVAR.inserir(ctx.IDENT().getText(), etds.valor, 1, 0);
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
    public Void visitTipo(LAParser.TipoContext ctx) {
        if(ctx.registro() != null)
        {
            EntradaTS_TIPO etds = tdsTIPOS.verificar(ctx.tipo_estendido().tipo_basico_ident().getText());
            if(etds == null)
            {
                //erro, tipo nao declarado
            }else
                tipo = etds.valor;
        }else //tipo estendido
        {
            nPonteiros = ctx.tipo_estendido().ponteiros_opcionais().depth();
            
        }
        
        return null;
    }

    
    
    @Override
    public Void visitVariavel(LAParser.VariavelContext ctx) {
        
        visitTipo(ctx.tipo());
        if(tdsVAR.verificar(ctx.IDENT().getText()) != null)
        {
            //erro, variavel ja declarada
        }else
        {    
            tdsVAR.inserir(ctx.IDENT().getText(), tipo, Integer.parseInt(ctx.dimensao().getText()), 0);
            if(ctx.mais_var() != null)
            {
                visitMais_var(ctx.mais_var());
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
            tdsVAR.inserir(ctx.IDENT().getText(), tipo, Integer.parseInt(ctx.dimensao().getText()), 0);
                if(ctx.mais_var() != null)
                    visitMais_var(ctx.mais_var());
        
        }
  
        return null;
    }
    
    

    
    
}
