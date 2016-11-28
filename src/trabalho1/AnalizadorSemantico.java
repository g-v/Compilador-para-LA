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

    TDSContext tdsContext;

    //tmp
    
    int tipo;
    int nPonteiros;
    String nome;
    String tipoAlias;
    boolean isStructure;
    //tmp

    @Override
    public Void visitDeclaracao_local(LAParser.Declaracao_localContext ctx) {
        if(ctx.dclLocalConst != null)
        {
            EntradaTS_TIPO etds = tdsContext.verificaTIPO(ctx.tipo_basico().getText());
            if(ctx.tipo() == null)
            {
                //erro tipo nao declarado
            }else
            {
                if(tdsContext.verificaVAR(ctx.IDENT().getText()) != null)
                {
                    //erro, variavel ja declarada
                }else
                    tdsContext.insereVAR(ctx.IDENT().getText(), etds.valor, 1, 0);
            }
        }else if(ctx.dclLocalTipo != null)
        {
            nome = ctx.dclLocalTipo.getText();
            visitTipo(ctx.tipo());
            tdsContext.insereTIPO(nome, tdsContext.tabelaDeTipos.indiceAtual + 1, nPonteiros, tipoAlias, isStructure);
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
            if(isStructure == true)
                tdsContext.enterSTRCTLevel(nome);
            
            isStructure = true;
            nPonteiros = 0;
            
            tdsContext.setCurrentStructure(nome);
            visitRegistro(ctx.registro());
            tdsContext.setNoStructure();
            
            isStructure = false;
            
            if(tdsContext.getSTRCTLevel() > 0)
                tdsContext.leaveSTRCTLevel();
            
            tipo = tdsContext.tabelaDeTipos.indiceAtual + 1;
            tipoAlias = "registro";
        }else //tipo estendido
        {
            isStructure = false;
            tipoAlias = ctx.tipo_estendido().tipo_basico_ident().getText();
            EntradaTS_TIPO etds = tdsContext.verificaTIPO(ctx.tipo_estendido().tipo_basico_ident().getText());
            if(etds == null)
            {
                //erro, tipo nao declarado
            }else
            {
                tipo = etds.valor;
                nPonteiros = ctx.tipo_estendido().ponteiros_opcionais().depth();
            }
        }
        
        return null;
    }

    @Override
    public Void visitRegistro(LAParser.RegistroContext ctx) {
        
        visitVariavel(ctx.variavel());
        
        if(ctx.mais_variaveis() != null)
            visitMais_variaveis(ctx.mais_variaveis());
        
        return null;
    }

    
    
    @Override
    public Void visitVariavel(LAParser.VariavelContext ctx) {
        
        if(tdsContext.verificaVAR(ctx.IDENT().getText()) != null)
        {
            //erro, variavel ja declarada
        }else
        {   
            nome = ctx.IDENT().getText() + "_anonSTRCT_";
            visitTipo(ctx.tipo());
            tdsContext.insereVAR(ctx.IDENT().getText(), tipo, Integer.parseInt(ctx.dimensao().getText()), 0);
            if(ctx.mais_var() != null)
            {
                visitMais_var(ctx.mais_var());
            }
        }
        
        return null;
     }
    
    
    
    @Override
    public Void visitMais_var(LAParser.Mais_varContext ctx) {
        if(tdsContext.verificaVAR(ctx.IDENT().getText()) != null)
        {
            //erro, variavel ja declarada
        }else
        {
            tdsContext.insereVAR(ctx.IDENT().getText(), tipo, Integer.parseInt(ctx.dimensao().getText()), 0);
                if(ctx.mais_var() != null)
                    visitMais_var(ctx.mais_var());
        
        }
  
        return null;
    }
    
    

    
    
}
