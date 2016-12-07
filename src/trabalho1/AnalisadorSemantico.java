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
public class AnalisadorSemantico extends LABaseVisitor<Void>{

    TDSContext tdsContext;

    //tmp
    
    EntradaTS_TIPO tipo;
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
                    tdsContext.insereVAR(ctx.IDENT().getText(), etds, 1, 0);
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
            
            tipo.valor = tdsContext.tabelaDeTipos.indiceAtual + 1;
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
                tipo = etds;
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
        EntradaTS_TIPO entradaTipo = new EntradaTS_TIPO();
        if(ctx.dclGlobalProcedimento != null)
        {
            nome = ctx.dclGlobalProcedimento.getText();
            entradaTipo.valor = -1;
            nPonteiros = 0;
        }
        else
        {
            nome = ctx.dclGlobalFuncao.getText();
            nPonteiros = ctx.tipo_estendido().ponteiros_opcionais().depth();
            entradaTipo = tdsContext.verificaTIPO(ctx.tipo_estendido().tipo_basico_ident().getText());
            if(entradaTipo == null)
            {
                //erro, tipo de retorno nao declarado
            }
        }
        
        EntradaTS_FUNC etds = tdsContext.verificaFUNC(nome);
        if(etds != null)
        {
            //erro, função ja declarada
        }
        else
        {

            tdsContext.insereFUNC(nome, entradaTipo.valor, nPonteiros);

            tdsContext.setFUNCMode(nome);
            //comeca a adicionar as variaveis ao procedimento
            if(ctx.parametros_opcional().parametro() != null)
            {
                visitParametros_opcional(ctx.parametros_opcional());
                tdsContext.setNumeroArgumentosFunc(nome, nParametros);
            }
        }

        //enough
        tdsContext.leaveFUNCMode();
        visitDeclaracoes_locais(ctx.declaracoes_locais());
        visitComandos(ctx.comandos());
        
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
            tipo = etds;
            nPonteiros = ctx.tipo_estendido().ponteiros_opcionais().depth();
        }
        
        visitIdentificador(ctx.identificador());
        visitMais_ident(ctx.mais_ident());
        
        visitMais_parametros(ctx.mais_parametros());
        return null;
    }

    @Override
    public Void visitCmd(LAParser.CmdContext ctx) {
        if(ctx.cmdReturn != null)
        {
            EntradaTS_FUNC etds = tdsContext.verificaFUNC(tdsContext.nomeFUNC);
            tipo.valor = -1;
            visitExpressao(ctx.expressao());
            
            if(etds.valor == -1 && tipo.valor != -1)
            {
                //erro, procedimento nao aceita tipo de retorno nao nulo
            }
            
            if(etds.valor != tipo.valor)
            {
                //erro, tipo de retorno nao compativel com o pedido pela funcao
            }
        }
        
        visitChildren(ctx);
        
        return null;
    }
    
    
    
    @Override
    public Void visitIdentificador(LAParser.IdentificadorContext ctx) {
        if(tdsContext.FUNCMode == true)
        {
            if(tdsContext.verificaVAR(ctx.IDENT().getText()) != null)
            {
                //erro, outro parametro com mesmo nome
            }
                    
            tdsContext.insereVAR(ctx.IDENT().getText(), tipo, Integer.parseInt(ctx.dimensao().getText()), nPonteiros);
            nParametros++;
        }else
        {
            EntradaTS_VAR etds = tdsContext.verificaVAR(ctx.IDENT().getText());
            if(etds == null)
            {
                //erro, variavel nao declarada
            }
            
            if(Integer.parseInt(ctx.dimensao().getText()) > etds.dimensao)
            {
                //erro, tentativa de acesso incorreta ou fora do vetor
            }
            
            if(ctx.outros_ident() != null)
            {
                if(etds.tipo.isStructure == false)
                {
                    //erro, identificador nao e estrutura
                }
            }
        }
            
        return null;
    }

    @Override
    public Void visitParcela_unario(LAParser.Parcela_unarioContext ctx) {
        if(ctx.puNomeFuncao != null)
        {
        
            if(tdsContext.verificaFUNC(ctx.puNomeFuncao.getText()) == null)
            {
                //erro, funçao ou procedimento nao declarado
            }
        
        }
        return null;
    }
    
    

    
    
}
