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
    int nParametros;
    String nome;
    String tipoAlias;
    boolean isStructure;
    //tmp

    @Override
    public Void visitDeclaracao_local(LAParser.Declaracao_localContext ctx) {
        if(ctx.dclLocalConst != null)
        {
            EntradaTS_TIPO etds = tdsContext.verificaTIPO(ctx.tipo_basico().getText());
            if(etds == null)
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
            else
                tdsContext.setCurrentStructure(nome);
            
            isStructure = true;
            nPonteiros = 0;
            
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

    @Override
    public Void visitDeclaracao_global(LAParser.Declaracao_globalContext ctx) {
        nParametros = 0;
        if(ctx.dclGlobalProcedimento != null)
        {
            EntradaTS_FUNC etds = tdsContext.verificaFUNC(ctx.dclGlobalProcedimento.getText());
            if(etds != null)
            {
                //erro, função ja declarada
            }
            else
            {
                tdsContext.insereFUNC(ctx.dclGlobalProcedimento.getText(), -1, 0);
  
                tdsContext.setFUNCMode(ctx.dclGlobalProcedimento.getText());
                //comeca a adicionar as variaveis ao procedimento
                if(ctx.parametros_opcional().parametro() != null)
                {
                    visitParametros_opcional(ctx.parametros_opcional());
                    tdsContext.setNumeroArgumentosFunc(ctx.dclGlobalProcedimento.getText(), nParametros);
                }
                //enough
                tdsContext.leaveFUNCMode();
                visitDeclaracoes_locais(ctx.declaracoes_locais());
            }
        }else if(ctx.dclGlobalFuncao != null)
        {
            EntradaTS_FUNC etds = tdsContext.verificaFUNC(ctx.dclGlobalFuncao.getText());
            if(etds != null)
            {
                //erro, função ja declarada
            }
            else
            {
                EntradaTS_TIPO entradaTipo = tdsContext.verificaTIPO(ctx.tipo_estendido().tipo_basico_ident().getText());
                if(entradaTipo == null)
                {
                    //erro, tipo nao declarado
                }else
                {
                    tdsContext.insereFUNC(ctx.dclGlobalFuncao.getText(), entradaTipo.valor, 
                            ctx.tipo_estendido().ponteiros_opcionais().depth());
  
                    tdsContext.setFUNCMode(ctx.dclGlobalFuncao.getText());
                    //comeca a adicionar as variaveis ao procedimento
                    if(ctx.parametros_opcional().parametro() != null)
                    {
                        visitParametros_opcional(ctx.parametros_opcional());
                        tdsContext.setNumeroArgumentosFunc(ctx.dclGlobalFuncao.getText(), nParametros);
                    }
                }
                
                //enough
                tdsContext.leaveFUNCMode();
                visitDeclaracoes_locais(ctx.declaracoes_locais());
            }
        }
        
        return null;
    }

    @Override
    public Void visitParametro(LAParser.ParametroContext ctx) {
        
        EntradaTS_TIPO etds = tdsContext.verificaTIPO(ctx.tipo_estendido().tipo_basico_ident().getText());
        if(etds == null)
        {
            //erro, tipo nao declarado
        }else
        {
            tipo = etds.valor;
            nPonteiros = ctx.tipo_estendido().ponteiros_opcionais().depth();
        }
        
        visitIdentificador(ctx.identificador());
        visitMais_ident(ctx.mais_ident());
        
        visitMais_parametros(ctx.mais_parametros());
        return null;
    }

    @Override
    public Void visitIdentificador(LAParser.IdentificadorContext ctx) {
        tdsContext.insereVAR(ctx.IDENT().getText(), tipo, Integer.parseInt(ctx.dimensao().getText()), nPonteiros);
        nParametros++;
        return null;
    }
    
    

    
    
}
