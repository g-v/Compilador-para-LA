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
    String tipoExpressao;
    int nExpressoes;
    //tmp

    public AnalisadorSemantico() {
        tdsContext = new TDSContext();
        tipo = new EntradaTS_TIPO();
        
        tdsContext.insereTIPO("inteiro", 0, 0, "tipo_basico", false);
        tdsContext.insereTIPO("real", 1, 0, "tipo_basico", false);
        tdsContext.insereTIPO("literal", 2, 0, "tipo_basico", false);
        tdsContext.insereTIPO("logico", 3, 0, "tipo_basico", false);
    }
    
    
    @Override
    public Void visitDeclaracao_local(LAParser.Declaracao_localContext ctx) {
        if(ctx.dclLocalConst != null)
        {
            EntradaTS_TIPO etds = tdsContext.verificaTIPO(ctx.tipo_basico().getText());
            if(etds == null)
            {
                System.err.println("Linha " + ctx.start.getLine() + ": tipo " + 
                        ctx.tipo_basico().getText() + " nao declarado");
                tipo.valor = 100;
            }else
            {
                if(tdsContext.verificaVAR(ctx.IDENT().getText()) != null)
                {
                    System.err.println("erro: Variável já declarada");
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
                System.err.println("Linha " + ctx.start.getLine() + ": tipo " + 
                        ctx.tipo_estendido().tipo_basico_ident().getText() + " nao declarado");
                tipo.valor = 100;
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
            System.err.println("erro: Variável já declarada");
        }else
        {   
            nome = ctx.IDENT().getText() + "_anonSTRCT_";
            visitTipo(ctx.tipo());
            int dimensao = 0;
            if(ctx.dimensao().isEmpty() != false)
                dimensao = Integer.parseInt(ctx.dimensao().getText());
                
            tdsContext.insereVAR(ctx.IDENT().getText(), tipo, dimensao, 0);
        }
        
        if(ctx.mais_var().isEmpty() != false)
        {
            visitMais_var(ctx.mais_var());
        }
        
        return null;
     }
    
    
    
    @Override
    public Void visitMais_var(LAParser.Mais_varContext ctx) {
        if(tdsContext.verificaVAR(ctx.IDENT().getText()) != null)
        {
            System.err.println("erro: Variável já declarada");
        }else
        {
            int dimensao = 0;
            if(ctx.dimensao().isEmpty() != false)
                dimensao = Integer.parseInt(ctx.dimensao().getText());
            
            tdsContext.insereVAR(ctx.IDENT().getText(), tipo, dimensao, 0);
        }
        
        if(ctx.mais_var().isEmpty() != false)
            visitMais_var(ctx.mais_var());
  
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
                entradaTipo = new EntradaTS_TIPO();
                entradaTipo.valor = 100;
            }
        }
        
        EntradaTS_FUNC etds = tdsContext.verificaFUNC(nome);
        if(etds != null)
        {
            //erro, função ja declarada
        }
        else
        {
            tdsContext.setFUNCMode(nome);
            if(entradaTipo.valor != 100)
            {
                tdsContext.insereFUNC(nome, entradaTipo.nome, nPonteiros);

                //comeca a adicionar as variaveis ao procedimento
                if(ctx.parametros_opcional().parametro() != null)
                {
                    visitParametros_opcional(ctx.parametros_opcional());
                    tdsContext.setNumeroArgumentosFunc(nome, nParametros);
                }
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
            tipo.valor = 100;
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
            }else
            {
                if(tipo.valor != 100)
                    tdsContext.insereVAR(ctx.IDENT().getText(), tipo, Integer.parseInt(ctx.dimensao().getText()), nPonteiros);
            }
            
            nParametros++;
        }else
        {
            EntradaTS_VAR etds = tdsContext.verificaVAR(ctx.IDENT().getText());
            if(etds == null)
            {
                System.err.println("Linha " + ctx.start.getLine() + 
                        ": identificador " + ctx.IDENT().getText() + " nao declarado");
            }else
            {
            
                int dimensao = 0;
                if(ctx.dimensao().isEmpty() != false)
                    Integer.parseInt(ctx.dimensao().getText());

                if(dimensao > etds.dimensao)
                {
                    //erro, tentativa de acesso incorreta ou fora do vetor
                }

                if(ctx.outros_ident().isEmpty() != false)
                {
                    if(etds.tipo.isStructure == false)
                    {
                        //erro, identificador nao e estrutura
                    }else
                    {
                        tdsContext.enterSTRCTLevel(ctx.IDENT().getText());
                        visitOutros_ident(ctx.outros_ident());
                        tdsContext.leaveSTRCTLevel();
                    }
                }else
                {
                    tipoExpressao = tdsContext.verificaVAR(ctx.IDENT().getText()).tipo.nome;
                }
            }
        }
            
        return null;
    }

    @Override
    public Void visitParcela_unario(LAParser.Parcela_unarioContext ctx) {
        if(ctx.puNomeIdent1 != null)
        {
            
        }else if(ctx.puNomeIdent2 != null)
        {
            if(ctx.chamada_partes().expressao().isEmpty() == false)
            {
                EntradaTS_FUNC etds = tdsContext.verificaFUNC(ctx.puNomeIdent2.getText());
                if(etds == null)
                {
                    //erro, funçao ou procedimento nao declarado
                }
                else
                {
                    nome = ctx.puNomeIdent2.getText();
                    visitChamada_partes(ctx.chamada_partes());
                    
                    tipoExpressao = etds.tipoDeRetorno;
                }
            }else
            {
                EntradaTS_VAR etds = tdsContext.verificaVAR(ctx.puNomeIdent2.getText());
                if(etds == null)
                {
                    System.err.println("Linha " + ctx.start.getLine() + 
                        ": identificador " + ctx.IDENT().getText() + " nao declarado");
                }else
                {
                    if(ctx.chamada_partes().dimensao().isEmpty() == false)
                        if(Integer.parseInt(ctx.chamada_partes().dimensao().getText()) > etds.dimensao)
                        {
                            //erro, tentativa de acesso incorreta ou fora do vetor
                        }
                    
                    if(ctx.chamada_partes().outros_ident().isEmpty() == false)
                    {
                        tdsContext.enterSTRCTLevel(ctx.puNomeIdent2.getText());
                        visitOutros_ident(ctx.chamada_partes().outros_ident());
                        tdsContext.leaveSTRCTLevel();
                    }
                }
            }
        }else if(ctx.NUM_INT() != null)
            tipoExpressao = "inteiro";
        else if(ctx.NUM_REAL() != null)
            tipoExpressao = "real";
        else
            visitExpressao(ctx.expressao());
        return null;
    }

    @Override
    public Void visitExpressao(LAParser.ExpressaoContext ctx) {
        nExpressoes++;
        visitChildren(ctx);
        return null;
    }

    @Override
    public Void visitMais_expressao(LAParser.Mais_expressaoContext ctx) {
        visitExpressao(ctx.expressao());
        
        if(nome.contains("main") == false)
        {
            tdsContext.setFUNCMode(nome);

            EntradaTS_VAR etds = tdsContext.recuperaArg(nExpressoes - 1);
            if(etds == null)
            {
                //erro, argumento nExpressoes - 1 excede a quantidade pedida pela funcao ou procedimento
            }else
            {
                if(etds.valor != tdsContext.verificaTIPO(tipoExpressao).valor)
                {
                    //erro, tipo do argumento nExpressoes - 1 incompativel com o pedido pela funcao
                }
            }

            tdsContext.leaveFUNCMode();
            nome = "main";
        }
        
        if(ctx.mais_expressao().isEmpty() == false)
            visitMais_expressao(ctx.mais_expressao());
        
        return null;
    }

    @Override
    public Void visitChamada_partes(LAParser.Chamada_partesContext ctx) {
        nExpressoes = 0;
        visitExpressao(ctx.expressao());
        
        tdsContext.setFUNCMode(nome);
        
        if(tdsContext.recuperaArg(0).valor != tdsContext.verificaTIPO(tipoExpressao).valor)
        {
            //erro, tipo do argumento 0 incompativel com o pedido pela funcao
        }
        
        tdsContext.leaveFUNCMode();
        
        if(ctx.mais_expressao().isEmpty() == false)
            visitMais_expressao(ctx.mais_expressao());
        
        EntradaTS_FUNC etds = tdsContext.verificaFUNC(tdsContext.nomeFUNC);
        
        if(nExpressoes > etds.nArgumentos)
        {
            //erro, numero de argumentos acima do pedido pela funcao
        }else if(nExpressoes < etds.nArgumentos)
        {
            //erro, numero de argumentos insuficiente
        }
        return null;
    }
    
    

    @Override
    public Void visitOp_unario(LAParser.Op_unarioContext ctx) {
        visitChildren(ctx);
        tipoExpressao = "real";
        return null;
    }

    @Override
    public Void visitOutros_termos(LAParser.Outros_termosContext ctx) {
        visitChildren(ctx);
        tipoExpressao = "real";
        return null;
    }

    @Override
    public Void visitOutros_fatores(LAParser.Outros_fatoresContext ctx) {
        visitChildren(ctx);
        tipoExpressao = "real";
        return null;
    }

    @Override
    public Void visitParcela_nao_unario(LAParser.Parcela_nao_unarioContext ctx) {
        visitChildren(ctx);
        if (ctx.pnuCadeia != null)
            tipoExpressao = "literal";
        else
        {
            //
        }
        return null;
    }

    @Override
    public Void visitOp_nao(LAParser.Op_naoContext ctx) {
        visitChildren(ctx);
        tipoExpressao = "logico";
        return null;
    }

    @Override
    public Void visitOp_opcional(LAParser.Op_opcionalContext ctx) {
        visitChildren(ctx);
        tipoExpressao = "logico";
        return null;
    }
    @Override
    public Void visitOutros_termos_logicos(LAParser.Outros_termos_logicosContext ctx) {
        visitChildren(ctx);
        tipoExpressao = "logico";
        return null;
    }

    @Override
    public Void visitParcela_logica(LAParser.Parcela_logicaContext ctx) {
        visitChildren(ctx);
        tipoExpressao = "logico";
        return null;
    }

    @Override
    public Void visitOutros_fatores_logicos(LAParser.Outros_fatores_logicosContext ctx) {
        visitChildren(ctx);
        tipoExpressao = "logico";
        return null;
    }
    
    

    
    
}
