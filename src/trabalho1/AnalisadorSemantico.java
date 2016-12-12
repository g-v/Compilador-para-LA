/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trabalho1;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.TerminalNode;
import trabalho1.parser.LABaseVisitor;
import trabalho1.parser.LAParser;

/**
 *
 * @author Esquilo
 */
public class AnalisadorSemantico extends LABaseVisitor<Void> {

    TDSContext tdsContext;

    PrintWriter writer;
    //tmp

    static final class varInfo {

        static String nome;
        static EntradaTS_TIPO tipo = new EntradaTS_TIPO();
        static int nPonteiros;
        static String tipoAlias;
        static boolean isStructure;
        static Escopos<List<String>> dimensoes = new Escopos<>(new LinkedList<>());
    }

    static final class funcInfo {

        static String nomeFuncao = "main";
        ;
        static String nomeChamada = "main";
        ;
        static int nParametros;
    }

    static final class expInfo {

        static String tipoExpressao;
        static String acumulaIdent;
        static boolean resetAcumulaIdent = true;
        static String identAtribuicao;
        static int nivelPonteirosTipo;
        static int nExpressoes;
    }

    //tmp
    public AnalisadorSemantico(File saida) throws IOException {
        tdsContext = new TDSContext();

        writer = new PrintWriter(saida, "UTF-8");

        tdsContext.insereTIPO("inteiro", 0, 0, "tipo_basico", false);
        tdsContext.insereTIPO("real", 1, 0, "tipo_basico", false);
        tdsContext.insereTIPO("literal", 2, 0, "tipo_basico", false);
        tdsContext.insereTIPO("logico", 3, 0, "tipo_basico", false);
        tdsContext.insereTIPO("erro", 100, 0, "tipo_basico", false);

        tdsContext.insereConversaoDeTipo("inteiro", "real");
        tdsContext.insereConversaoDeTipo("real", "inteiro");
    }

    private static <T extends RuleContext> boolean regraVazia(T regra) {
        return regra == null || regra.getText().isEmpty();
    }

    private static boolean regraVazia(TerminalNode regra) {
        return regra == null || regra.getText().isEmpty();
    }

    public void close() {
        writer.println("Fim da compilacao");
        writer.close();
    }

    @Override
    public Void visitCorpo(LAParser.CorpoContext ctx) {
        tdsContext.criaNovoEscopo();
        visitDeclaracoes_locais(ctx.declaracoes_locais());

        visitComandos(ctx.comandos());

        return null;
    }

    @Override
    public Void visitDeclaracao_local(LAParser.Declaracao_localContext ctx) {
        if (ctx.dclLocalConst != null) {
            EntradaTS_TIPO etds = tdsContext.verificaTIPO(ctx.tipo_basico().getText());
            if (etds == null) {
                writer.println("Linha " + ctx.start.getLine() + ": tipo "
                        + ctx.tipo_basico().getText() + " nao declarado");
                varInfo.tipo.valor = 100;
            } else {
                if (tdsContext.verificaVAR(ctx.IDENT().getText()) != null
                        || tdsContext.verificaTIPO(ctx.IDENT().getText()) != null
                        || tdsContext.verificaFUNC(ctx.IDENT().getText()) != null) {
                    writer.println("erro: Identificador já declarado");
                } else {
                    tdsContext.insereVAR(ctx.IDENT().getText(), etds, 1, "");
                }
            }
        } else if (ctx.dclLocalTipo != null) {
            varInfo.nome = ctx.dclLocalTipo.getText();
            visitTipo(ctx.tipo());

            varInfo.isStructure = !ctx.tipo().registro().getText().isEmpty();

            tdsContext.insereTIPO(ctx.dclLocalTipo.getText(), tdsContext.tabelaDeTipos.indiceAtual + 1,
                    varInfo.nPonteiros, varInfo.tipoAlias, varInfo.isStructure);
        } else // variavel
        {
            visitVariavel(ctx.variavel());
        }
        return null;
    }

    @Override
    public Void visitTipo(LAParser.TipoContext ctx) {
        if (tdsContext.verificaTIPO(varInfo.nome) != null || tdsContext.verificaVAR(varInfo.nome) != null
                || tdsContext.verificaFUNC(varInfo.nome) != null) {
            writer.println("Linha " + ctx.start.getLine() + ": identificador "
                    + varInfo.nome + " ja declarado anteriormente");
        } else {
            if (ctx.registro() != null) {
                if (tdsContext.STRCTLevel > 0) {
                    tdsContext.enterSTRCTLevel();
                }

                tdsContext.setCurrentStructure(varInfo.nome);

                varInfo.isStructure = true;
                varInfo.nPonteiros = 0;

                visitRegistro(ctx.registro());
                tdsContext.leaveSTRCTLevel();

                varInfo.isStructure = false;

                tdsContext.leaveSTRCTLevel();

                varInfo.tipo.valor = tdsContext.tabelaDeTipos.indiceAtual + 1;
                varInfo.tipoAlias = "registro";
            } else //tipo estendido
            {
                varInfo.isStructure = false;
                varInfo.tipoAlias = ctx.tipo_estendido().tipo_basico_ident().getText();
                EntradaTS_TIPO etds = tdsContext.verificaTIPO(ctx.tipo_estendido().tipo_basico_ident().getText());
                if (etds == null) {
                    writer.println("Linha " + ctx.start.getLine() + ": tipo "
                            + ctx.tipo_estendido().tipo_basico_ident().getText() + " nao declarado");
                    varInfo.tipo.valor = 100;
                } else {
                    varInfo.nPonteiros = 0;
                    if (ctx.tipo_estendido().ponteiros_opcionais().getText().isEmpty() == false) {
                        visitPonteiros_opcionais(ctx.tipo_estendido().ponteiros_opcionais());
                    }

                    varInfo.tipo = etds;
                }
            }
        }

        return null;
    }

    @Override
    public Void visitRegistro(LAParser.RegistroContext ctx) {

        visitVariavel(ctx.variavel());

        if (ctx.mais_variaveis() != null) {
            visitMais_variaveis(ctx.mais_variaveis());
        }

        return null;
    }

    @Override
    public Void visitVariavel(LAParser.VariavelContext ctx) {

        varInfo.nome = ctx.IDENT().getText() + "_anonSTRCT_";
        String tmpNome = varInfo.nome;

        visitTipo(ctx.tipo());
        if (AnalisadorSemantico.regraVazia(ctx.tipo().registro()) == false) {
            tdsContext.insereTIPO(tmpNome, varInfo.tipo.valor, 0, "estrutura", true);
            varInfo.tipo = tdsContext.verificaTIPO(tmpNome);
        }

        if (tdsContext.verificaVAR(ctx.IDENT().getText()) != null
                || tdsContext.verificaTIPO(ctx.IDENT().getText()) != null
                || tdsContext.verificaFUNC(ctx.IDENT().getText()) != null) {
            writer.println("Linha " + ctx.start.getLine() + ": identificador "
                    + ctx.IDENT().getText() + " ja declarado anteriormente");
        } else {
            String[] dimensao = new String[varInfo.dimensoes.pegarEscopoAtual().size()];
            if (ctx.dimensao().getText().isEmpty() == false) {
                visitDimensao(ctx.dimensao());
                dimensao = varInfo.dimensoes.pegarEscopoAtual().toArray(dimensao);
            }

            tdsContext.insereVAR(ctx.IDENT().getText(), varInfo.tipo, varInfo.nPonteiros, dimensao);
        }

        if (ctx.mais_var().getText().isEmpty() == false) {
            visitMais_var(ctx.mais_var());
        }

        return null;
    }

    @Override
    public Void visitDimensao(LAParser.DimensaoContext ctx) {
        varInfo.dimensoes.criarNovoEscopo(new LinkedList<>());
        visitExp_aritmetica(ctx.exp_aritmetica());
        varInfo.dimensoes.abandonarEscopo();

        varInfo.dimensoes.pegarEscopoAtual().add(ctx.exp_aritmetica().getText());

        if (ctx.dimensao().getText().isEmpty() == false) {
            visitDimensao(ctx.dimensao());
        }

        return null;
    }

    @Override
    public Void visitMais_var(LAParser.Mais_varContext ctx) {
        if (tdsContext.verificaVAR(ctx.IDENT().getText()) != null
                || tdsContext.verificaTIPO(ctx.IDENT().getText()) != null
                || tdsContext.verificaFUNC(ctx.IDENT().getText()) != null) {
            writer.println("Linha " + ctx.start.getLine() + ": identificador "
                    + ctx.IDENT().getText() + " ja declarado anteriormente");
        } else {
            String[] dimensao = new String[varInfo.dimensoes.pegarEscopoAtual().size()];
            if (ctx.dimensao().getText().isEmpty() == false) {
                visitDimensao(ctx.dimensao());
                dimensao = varInfo.dimensoes.pegarEscopoAtual().toArray(dimensao);
            }

            tdsContext.insereVAR(ctx.IDENT().getText(), varInfo.tipo, 0, dimensao);
        }

        if (ctx.mais_var().getText().isEmpty() == false) {
            visitMais_var(ctx.mais_var());
        }

        return null;
    }

    @Override
    public Void visitDeclaracao_global(LAParser.Declaracao_globalContext ctx) {
        EntradaTS_TIPO entradaTipo = new EntradaTS_TIPO();
        if (ctx.dclGlobalProcedimento != null) {
            funcInfo.nomeFuncao = ctx.dclGlobalProcedimento.getText();
            entradaTipo.valor = -1;
            entradaTipo.nome = "void";
            varInfo.nPonteiros = 0;
        } else {
            funcInfo.nomeFuncao = ctx.dclGlobalFuncao.getText();

            varInfo.nPonteiros = 0;
            if (ctx.tipo_estendido().ponteiros_opcionais().getText().isEmpty() == false) {
                visitPonteiros_opcionais(ctx.tipo_estendido().ponteiros_opcionais());
            }

            entradaTipo = tdsContext.verificaTIPO(ctx.tipo_estendido().tipo_basico_ident().getText());
            if (entradaTipo == null) {
                //erro, tipo de retorno nao declarado
                entradaTipo = new EntradaTS_TIPO();
                entradaTipo.valor = 100;
            }
        }

        EntradaTS_FUNC etds = tdsContext.verificaFUNC(funcInfo.nomeFuncao);
        if (etds != null) {
            //erro, função ja declarada
        } else {
            tdsContext.setFUNCMode(funcInfo.nomeFuncao);
            if (entradaTipo.valor != 100) {
                tdsContext.insereFUNC(funcInfo.nomeFuncao, entradaTipo.nome, varInfo.nPonteiros);

                //comeca a adicionar as variaveis a funcao
                funcInfo.nParametros = 0;
                if (ctx.parametros_opcional().parametro() != null) {
                    visitParametros_opcional(ctx.parametros_opcional());
                    tdsContext.setNumeroArgumentosFunc(funcInfo.nomeFuncao, funcInfo.nParametros);
                }
            }
        }

        //enough
        tdsContext.leaveFUNCMode();
        tdsContext.criaNovoEscopo();
        EntradaTS_VAR etdsVAR;
        for (int x = 0; x < funcInfo.nParametros; x++) {
            tdsContext.setFUNCMode(funcInfo.nomeFuncao);
            etdsVAR = tdsContext.recuperaArg(x);
            tdsContext.leaveFUNCMode();
            String[] dimensao = new String[etdsVAR.dimensao.size()];
            dimensao = etdsVAR.dimensao.toArray(dimensao);
            tdsContext.insereVAR(etdsVAR.nome, etdsVAR.tipo, etdsVAR.nPonteiros, dimensao);
        }
        visitDeclaracoes_locais(ctx.declaracoes_locais());
        visitComandos(ctx.comandos());
        tdsContext.abandonaEscopo();
        funcInfo.nomeFuncao = "main";

        return null;
    }

    @Override
    public Void visitPonteiros_opcionais(LAParser.Ponteiros_opcionaisContext ctx) {
        varInfo.nPonteiros++;
        if (ctx.ponteiros_opcionais().getText().isEmpty() == false) {
            visitPonteiros_opcionais(ctx.ponteiros_opcionais());
        }

        return null;
    }

    @Override
    public Void visitParametro(LAParser.ParametroContext ctx) {

        EntradaTS_TIPO etds = tdsContext.verificaTIPO(ctx.tipo_estendido().tipo_basico_ident().getText());
        if (etds == null) {
            //erro, tipo nao declarado
            varInfo.tipo.valor = 100;
        } else {
            varInfo.tipo = etds;

            varInfo.nPonteiros = 0;
            if (ctx.tipo_estendido().ponteiros_opcionais().getText().isEmpty() == false) {
                visitPonteiros_opcionais(ctx.tipo_estendido().ponteiros_opcionais());
            }

        }

        visitIdentificador(ctx.identificador());
        visitMais_ident(ctx.mais_ident());

        visitMais_parametros(ctx.mais_parametros());
        return null;
    }

    @Override
    public Void visitCmd(LAParser.CmdContext ctx) {
        if (ctx.cmdReturn != null) {
            if (funcInfo.nomeFuncao.equals("main")) {
                writer.println("Linha " + ctx.start.getLine()
                        + ": comando retorne nao permitido nesse escopo");
            } else {
                EntradaTS_FUNC etds = tdsContext.verificaFUNC(funcInfo.nomeFuncao);
                expInfo.tipoExpressao = "void";
                visitExpressao(ctx.expressao());

                if (etds.tipoDeRetorno.equals("void") == true && expInfo.tipoExpressao.equals("void") == false) {
                    writer.println("Linha " + ctx.start.getLine()
                            + ": comando retorne nao permitido nesse escopo");
                }

                if (tdsContext.tiposEquivalentes(etds.tipoDeRetorno, expInfo.tipoExpressao, false) == false) {
                    //erro, tipo de retorno nao compativel com o pedido pela funcao
                }
            }
        } else if (ctx.cmdAtribuicaoIdent != null) {
            if (AnalisadorSemantico.regraVazia(ctx.chamada_atribuicao().expressao()) == false) {
                EntradaTS_VAR etds = tdsContext.verificaVAR(ctx.cmdAtribuicaoIdent.getText());
                if (etds == null) {
                    writer.println("Linha " + ctx.start.getLine()
                            + ": identificador " + ctx.IDENT().getText() + " nao declarado");
                } else {
                    /*if(ctx.chamada_atribuicao().dimensao().getText().isEmpty() == false)
                            if(Integer.parseInt(ctx.chamada_atribuicao().dimensao().getText()) > etds.dimensao.get(0))
                            {
                                //erro, tentativa de acesso incorreta ou fora do vetor
                            }*/

                    if (ctx.chamada_atribuicao().expressao().getText().isEmpty() == false) {
                        varInfo.nome = ctx.cmdAtribuicaoIdent.getText();
                    }
                    expInfo.tipoExpressao = etds.tipo.nome;
                    visitChamada_atribuicao(ctx.chamada_atribuicao());
                }
            } else {
                EntradaTS_FUNC etds = tdsContext.verificaFUNC(ctx.cmdAtribuicaoIdent.getText());
                if (etds == null) {
                    writer.println("Linha " + ctx.start.getLine()
                            + ": identificador " + ctx.IDENT().getText() + " nao declarado");
                } else {
                    funcInfo.nomeChamada = ctx.cmdAtribuicaoIdent.getText();
                    if (ctx.chamada_atribuicao().argumentos_opcional().getText().isEmpty() == false) {
                        visitArgumentos_opcional(ctx.chamada_atribuicao().argumentos_opcional());
                    } else if (etds.nArgumentos > 0) {
                        writer.println("Linha " + ctx.start.getLine()
                                + ": imcompatibilidade de parametros na chamada de " + funcInfo.nomeChamada);
                    }
                }
            }
        } else if (ctx.cmdAtribPonteiroIdent != null) {
            EntradaTS_VAR etds = tdsContext.verificaVAR(ctx.cmdAtribPonteiroIdent.getText());
            if (etds == null) {
                writer.println("Linha " + ctx.start.getLine()
                        + ": identificador " + ctx.cmdAtribPonteiroIdent.getText() + " nao declarado");
            } else {
                String tmpNome = ctx.cmdAtribPonteiroIdent.getText();
                expInfo.acumulaIdent = tmpNome;
                expInfo.identAtribuicao = tmpNome;
                if (ctx.outros_ident().getText().isEmpty() == false) {
                    expInfo.identAtribuicao += ctx.outros_ident().getText();
                    expInfo.acumulaIdent += ".";
                    if (tdsContext.STRCTLevel > 0) {
                        tdsContext.enterSTRCTLevel();
                    }

                    tdsContext.setCurrentStructure(etds.tipo.nome);
                    visitOutros_ident(ctx.outros_ident());
                    tdsContext.leaveSTRCTLevel();
                    tmpNome = varInfo.nome;
                    etds = tdsContext.verificaVAR(varInfo.nome);
                } else {
                    /*if(ctx.dimensao().getText().isEmpty() == false)
                        if(Integer.parseInt(ctx.dimensao().getText()) > etds.dimensao.get(0))
                        {
                            //erro, tentativa de acesso incorreta ou fora do vetor
                        }*/
                }
                expInfo.identAtribuicao += ctx.dimensao().getText();
                if (etds.nPonteiros <= 0) {
                    //erro, tentativa de acessar endereco de identificador nao ponteiro
                }

                visitExpressao(ctx.expressao());
                if (expInfo.nivelPonteirosTipo - (etds.tipo.nPonteiros + etds.nPonteiros - 1) < 0) {
                    writer.println("Linha " + ctx.start.getLine() + ": erro de ponteiros na atribuicao para ^"
                            + tmpNome);
                }

                if (expInfo.tipoExpressao.equals("erro")) {
                    writer.println("Linha " + ctx.start.getLine() + ": atribuicao nao compativel para ^"
                            + tmpNome);
                } else if (tdsContext.tiposEquivalentes(expInfo.tipoExpressao, etds.tipo.nome, false) == false) {
                    writer.println("Linha " + ctx.start.getLine() + ": atribuicao nao compativel para ^"
                            + tmpNome);
                }
            }
        } else if (ctx.cmdIsCase != null) {
            visitExp_aritmetica(ctx.exp_aritmetica(0));
            visitSelecao(ctx.selecao());
            visitSenao_opcional(ctx.senao_opcional());
        } else if (ctx.cmdIsEnquanto != null) {
            visitExpressao(ctx.expressao());
            visitComandos(ctx.comandos());
        } else if (ctx.cmdIsEscreva != null) {
            visitExpressao(ctx.expressao());
            visitMais_expressao(ctx.mais_expressao());
        } else if (ctx.cmdIsFaca != null) {
            visitComandos(ctx.comandos());
            visitExpressao(ctx.expressao());
        } else if (ctx.cmdIsLeia != null) {
            visitIdentificador(ctx.identificador());
            visitMais_ident(ctx.mais_ident());
        } else if (ctx.cmdIsPara != null) {
            visitExp_aritmetica(ctx.exp_aritmetica(0));
            visitExp_aritmetica(ctx.exp_aritmetica(1));
            visitComandos(ctx.comandos());
        } else if (ctx.cmdIsSe != null) {
            visitExpressao(ctx.expressao());
            visitComandos(ctx.comandos());
            visitSenao_opcional(ctx.senao_opcional());
        }

        return null;
    }

    @Override
    public Void visitArgumentos_opcional(LAParser.Argumentos_opcionalContext ctx) {
        expInfo.nExpressoes = 0;
        visitExpressao(ctx.expressao());

        tdsContext.setFUNCMode(funcInfo.nomeChamada);
        if (tdsContext.tiposIguais(tdsContext.recuperaArg(0).tipo.nome, expInfo.tipoExpressao) == false) {
            writer.println("Linha " + ctx.start.getLine()
                    + ": imcompatibilidade de parametros na chamada de " + funcInfo.nomeFuncao);
        }

        tdsContext.leaveFUNCMode();

        if (ctx.mais_expressao().getText().isEmpty() == false) {
            visitMais_expressao(ctx.mais_expressao());
        }

        EntradaTS_FUNC etds = tdsContext.verificaFUNC(funcInfo.nomeChamada);

        if (expInfo.nExpressoes > etds.nArgumentos) {
            //erro, numero de argumentos acima do pedido pela funcao
        } else if (expInfo.nExpressoes < etds.nArgumentos) {
            //erro, numero de argumentos insuficiente
        }

        return null;
    }

    @Override
    public Void visitChamada_atribuicao(LAParser.Chamada_atribuicaoContext ctx) {
        String tmpNome = varInfo.nome;
        String tipoIdent = expInfo.tipoExpressao;

        EntradaTS_VAR etds = tdsContext.verificaVAR(varInfo.nome);
        if (etds == null) {
            writer.println("Linha " + ctx.start.getLine()
                    + ": identificador " + varInfo.nome + " nao declarado");
        } else {
            if (AnalisadorSemantico.regraVazia(ctx.expressao()) == false) {
                expInfo.acumulaIdent = tmpNome;
                expInfo.identAtribuicao = tmpNome;
                if (ctx.outros_ident().getText().isEmpty() == false) {
                    expInfo.identAtribuicao += ctx.outros_ident().getText();
                    if (etds.tipo.isStructure == false) {
                        //erro, identificador nao e estrutura
                    } else {
                        expInfo.acumulaIdent += ".";
                        if (tdsContext.STRCTLevel > 0) {
                            tdsContext.enterSTRCTLevel();
                        }

                        tdsContext.setCurrentStructure(etds.tipo.nome);
                        visitOutros_ident(ctx.outros_ident());
                        tdsContext.leaveSTRCTLevel();
                        tipoIdent = expInfo.tipoExpressao;
                    }
                }
                expInfo.identAtribuicao += ctx.dimensao().getText();
                varInfo.nome = tmpNome;

                visitExpressao(ctx.expressao());

                if (expInfo.nivelPonteirosTipo - (etds.tipo.nPonteiros + etds.nPonteiros) != 0) {
                    writer.println("Linha " + ctx.start.getLine() + ": erro de ponteiros na atribuicao para "
                            + expInfo.acumulaIdent);
                }

                if (expInfo.tipoExpressao.equals("erro")) {
                    writer.println("Linha " + ctx.start.getLine() + ": atribuicao nao compativel para "
                            + expInfo.identAtribuicao);
                } else if (tdsContext.tiposEquivalentes(expInfo.tipoExpressao, tipoIdent, false) == false) {
                    writer.println("Linha " + ctx.start.getLine() + ": atribuicao nao compativel para "
                            + expInfo.identAtribuicao);
                }
            } else {
                if (ctx.argumentos_opcional().getText().isEmpty() == false) {
                    visitArgumentos_opcional(ctx.argumentos_opcional());
                }
            }
        }
        return null;
    }

    @Override
    public Void visitOutros_ident(LAParser.Outros_identContext ctx) {
        if (AnalisadorSemantico.regraVazia(ctx.identificador()) == false) {
            expInfo.resetAcumulaIdent = false;
            visitIdentificador(ctx.identificador());
        }

        return null;
    }

    @Override
    public Void visitIdentificador(LAParser.IdentificadorContext ctx) {
        if (tdsContext.FUNCMode == true) {
            if (tdsContext.verificaVAR(ctx.IDENT().getText()) != null) {
                //erro, outro parametro com mesmo nome
            } else {
                if (varInfo.tipo.valor != 100) {
                    String[] dimensao = new String[varInfo.dimensoes.pegarEscopoAtual().size()];
                    if (ctx.dimensao().getText().isEmpty() == false) {
                        visitDimensao(ctx.dimensao());
                        dimensao = varInfo.dimensoes.pegarEscopoAtual().toArray(dimensao);
                    }

                    tdsContext.insereVAR(ctx.IDENT().getText(), varInfo.tipo, varInfo.nPonteiros, dimensao);
                }
            }

            funcInfo.nParametros++;
        } else {
            if (expInfo.resetAcumulaIdent == true) {
                expInfo.acumulaIdent = "";
            } else {
                expInfo.resetAcumulaIdent = true;
            }

            String a = ctx.IDENT().getText();
            EntradaTS_VAR etds = tdsContext.verificaVAR(ctx.IDENT().getText());
            if (etds == null) {
                String ruleString = ctx.outros_ident().getText();

                writer.println("Linha " + ctx.start.getLine()
                        + ": identificador " + expInfo.acumulaIdent + ctx.IDENT().getText() + ruleString + " nao declarado");
            } else {
                /*int dimensao = 0;
                if(ctx.dimensao().getText().isEmpty() == false)
                    Integer.parseInt(ctx.dimensao().getText());

                if(dimensao > etds.dimensao.get(0))
                {
                    //erro, tentativa de acesso incorreta ou fora do vetor
                }*/

                if (ctx.outros_ident().getText().isEmpty() == false) {
                    if (etds.tipo.isStructure == false) {
                        writer.println("Linha " + ctx.start.getLine()
                                + ": identificador " + ctx.IDENT().getText() + " nao e estrutura");
                    } else {
                        expInfo.acumulaIdent += ctx.IDENT().getText() + ".";
                        if (tdsContext.STRCTLevel > 0) {
                            tdsContext.enterSTRCTLevel();
                        }

                        tdsContext.setCurrentStructure(etds.tipo.nome);
                        visitOutros_ident(ctx.outros_ident());
                        tdsContext.leaveSTRCTLevel();
                    }
                } else {
                    expInfo.acumulaIdent += ctx.IDENT().getText();
                    expInfo.tipoExpressao = tdsContext.verificaVAR(ctx.IDENT().getText()).tipo.nome;
                    varInfo.nome = ctx.IDENT().getText();
                }
            }
        }

        return null;
    }

    @Override
    public Void visitParcela_unario(LAParser.Parcela_unarioContext ctx) {
        if (ctx.puNomeIdent1 != null) {
            EntradaTS_VAR etds = tdsContext.verificaVAR(ctx.IDENT().getText());
            if (etds == null) {
                writer.println("Linha " + ctx.start.getLine()
                        + ": identificador " + ctx.IDENT().getText() + " nao declarado");
            } else {
                if (ctx.outros_ident().getText().isEmpty() == false) {
                    if (etds.tipo.isStructure == false) {
                        //erro, identificador nao e estrutura
                    } else {
                        if (tdsContext.STRCTLevel > 0) {
                            tdsContext.enterSTRCTLevel();
                        }

                        tdsContext.setCurrentStructure(etds.tipo.nome);
                        visitOutros_ident(ctx.outros_ident());
                        tdsContext.leaveSTRCTLevel();
                    }
                } else {
                    /*if(ctx.dimensao().getText().isEmpty() == false)
                        if(Integer.parseInt(ctx.dimensao().getText()) > etds.dimensao.get(0))
                        {
                            //erro, tentativa de acesso incorreta ou fora do vetor
                        }*/
                    expInfo.tipoExpressao = etds.tipo.nome;
                    expInfo.nivelPonteirosTipo = -1 + etds.tipo.nPonteiros;
                }
            }
        } else if (ctx.puNomeIdent2 != null) {
            if (AnalisadorSemantico.regraVazia(ctx.chamada_partes()) == false
                    && ctx.chamada_partes().cpIndicaFunc != null) {
                EntradaTS_FUNC etds = tdsContext.verificaFUNC(ctx.puNomeIdent2.getText());
                if (etds == null) {
                    //erro, funçao ou procedimento nao declarado
                } else {
                    String paiFuncao = funcInfo.nomeChamada;
                    funcInfo.nomeChamada = ctx.puNomeIdent2.getText();
                    visitChamada_partes(ctx.chamada_partes());
                    funcInfo.nomeChamada = paiFuncao;

                    expInfo.tipoExpressao = etds.tipoDeRetorno;
                }
            } else {
                expInfo.acumulaIdent = "";
                EntradaTS_VAR etds = tdsContext.verificaVAR(ctx.puNomeIdent2.getText());
                if (etds == null) {
                    String ruleString = "";
                    if (ctx.chamada_partes().outros_ident() != null) {
                        ruleString = ctx.chamada_partes().outros_ident().getText();
                    }

                    writer.println("Linha " + ctx.start.getLine()
                            + ": identificador " + expInfo.acumulaIdent + ctx.IDENT().getText() + ruleString + " nao declarado");
                } else {
                    /*if(ctx.chamada_partes().dimensao().getText().isEmpty() == false)
                        if(Integer.parseInt(ctx.chamada_partes().dimensao().getText()) > etds.dimensao.get(0))
                        {
                            //erro, tentativa de acesso incorreta ou fora do vetor
                        }*/

                    if (ctx.chamada_partes().outros_ident().getText().isEmpty() == false) {
                        if (etds.tipo.isStructure == false) {
                            //erro, identificador nao e estrutura
                        } else {
                            expInfo.acumulaIdent = "." + ctx.puNomeIdent2.getText();
                            if (tdsContext.STRCTLevel > 0) {
                                tdsContext.enterSTRCTLevel();
                            }

                            tdsContext.setCurrentStructure(etds.tipo.nome);
                            visitOutros_ident(ctx.chamada_partes().outros_ident());
                            tdsContext.leaveSTRCTLevel();
                        }
                    } else {
                        expInfo.tipoExpressao = etds.tipo.nome;
                        expInfo.acumulaIdent = ctx.puNomeIdent2.getText();
                    }
                }
            }
        } else if (AnalisadorSemantico.regraVazia(ctx.NUM_INT()) == false) {
            expInfo.tipoExpressao = "inteiro";
        } else if (AnalisadorSemantico.regraVazia(ctx.NUM_REAL()) == false) {
            expInfo.tipoExpressao = "real";
        } else {
            visitExpressao(ctx.expressao());
        }
        return null;
    }

    @Override
    public Void visitExpressao(LAParser.ExpressaoContext ctx) {
        expInfo.nExpressoes++;
        visitChildren(ctx);
        return null;
    }

    @Override
    public Void visitMais_expressao(LAParser.Mais_expressaoContext ctx) {
        if (ctx.expressao() != null) {
            visitExpressao(ctx.expressao());
        }

        if (funcInfo.nomeChamada.equals("main") == false) {
            tdsContext.setFUNCMode(funcInfo.nomeChamada);

            EntradaTS_VAR etds = tdsContext.recuperaArg(expInfo.nExpressoes - 1);
            if (etds == null) {
                //erro, argumento nExpressoes - 1 excede a quantidade pedida pela funcao ou procedimento
            } else {
                if (tdsContext.tiposIguais(etds.tipo.nome, expInfo.tipoExpressao) == false) {
                    writer.println("Linha " + ctx.start.getLine()
                            + ": imcompatibilidade de parametros na chamada de " + funcInfo.nomeChamada);
                }
            }

            tdsContext.leaveFUNCMode();
        }

        if (AnalisadorSemantico.regraVazia(ctx.mais_expressao()) == false) {
            visitMais_expressao(ctx.mais_expressao());
        }

        return null;
    }

    @Override
    public Void visitChamada_partes(LAParser.Chamada_partesContext ctx) {
        if (ctx.cpIndicaFunc != null) {
            expInfo.nExpressoes = 0;
            EntradaTS_FUNC etds = tdsContext.verificaFUNC(funcInfo.nomeChamada);
            if (ctx.expressao().getText().isEmpty() == true) {
                if (etds.nArgumentos > 0) {
                    writer.println("Linha " + ctx.start.getLine()
                            + ": imcompatibilidade de parametros na chamada de " + funcInfo.nomeChamada);
                }
            } else {
                visitExpressao(ctx.expressao());

                tdsContext.setFUNCMode(funcInfo.nomeChamada);

                if (tdsContext.tiposIguais(tdsContext.recuperaArg(0).tipo.nome, expInfo.tipoExpressao) == false) {
                    writer.println("Linha " + ctx.start.getLine()
                            + ": imcompatibilidade de parametros na chamada de " + funcInfo.nomeChamada);
                }
                tdsContext.leaveFUNCMode();
            }

            if (ctx.mais_expressao().getText().isEmpty() == false) {
                visitMais_expressao(ctx.mais_expressao());
            }

            if (expInfo.nExpressoes > etds.nArgumentos) {
                writer.println("Linha " + ctx.start.getLine()
                        + ": imcompatibilidade de parametros na chamada de " + funcInfo.nomeChamada);
            } else if (expInfo.nExpressoes < etds.nArgumentos) {
                writer.println("Linha " + ctx.start.getLine()
                        + ": imcompatibilidade de parametros na chamada de " + funcInfo.nomeChamada);
            }
        }

        return null;
    }

    @Override
    public Void visitOp_unario(LAParser.Op_unarioContext ctx) {
        visitChildren(ctx);
        if (ctx.getText().isEmpty() == false) {
            expInfo.tipoExpressao = "real";
        }

        return null;
    }

    @Override
    public Void visitOutros_termos(LAParser.Outros_termosContext ctx) {
        String tipoTermos = new String();
        int tmpTipo = -1;
        boolean isAdicao = false;
        EntradaTS_TIPO etds = tdsContext.verificaTIPO(expInfo.tipoExpressao);

        if (AnalisadorSemantico.regraVazia(ctx.termo()) == false) {
            if (ctx.op_adicao().getText().equals("-")) {
                if (etds.valor == 2) {
                    //erro, operacao ilegal para o tipo literal
                } else {
                    tipoTermos = "real";
                }
            } else {
                tmpTipo = etds.valor;
                isAdicao = true;
            }

            visitTermo(ctx.termo());
        }

        if (expInfo.tipoExpressao.equals("erro") == false && isAdicao == true) {
            etds = tdsContext.verificaTIPO(expInfo.tipoExpressao);
            if (tmpTipo == 2 || etds.valor == 2) {
                if (tmpTipo != etds.valor) {
                    tipoTermos = "erro";
                } else {
                    tipoTermos = "literal";
                }
            } else {
                tipoTermos = "real";
            }
        } else if (expInfo.tipoExpressao.equals("erro")) {
            tipoTermos = "erro";
        }

        if (AnalisadorSemantico.regraVazia(ctx.outros_termos()) == false) {
            visitOutros_termos(ctx.outros_termos());
        }

        if (expInfo.tipoExpressao.equals("erro") == false && tipoTermos.isEmpty() == false) {
            expInfo.tipoExpressao = tipoTermos;
        }

        return null;
    }

    @Override
    public Void visitOutros_fatores(LAParser.Outros_fatoresContext ctx) {
        visitChildren(ctx);
        if (ctx.getText().isEmpty() == false) {
            expInfo.tipoExpressao = "real";
        }

        return null;
    }

    @Override
    public Void visitParcela_nao_unario(LAParser.Parcela_nao_unarioContext ctx) {
        if (ctx.pnuCadeia != null) {
            expInfo.tipoExpressao = "literal";
        } else {
            EntradaTS_VAR etds = tdsContext.verificaVAR(ctx.IDENT().getText());
            if (etds == null) {
                writer.println("Linha " + ctx.start.getLine()
                        + ": identificador " + ctx.IDENT().getText() + " nao declarado");
            } else {
                if (ctx.outros_ident().getText().isEmpty() == false) {
                    if (etds.tipo.isStructure == false) {
                        //erro, identificador nao e estrutura
                    } else {
                        if (tdsContext.STRCTLevel > 0) {
                            tdsContext.enterSTRCTLevel();
                        }

                        tdsContext.setCurrentStructure(etds.tipo.nome);
                        visitOutros_ident(ctx.outros_ident());
                        tdsContext.leaveSTRCTLevel();
                    }
                } else {
                    /*if(ctx.dimensao().getText().isEmpty() == false)
                        if(Integer.parseInt(ctx.dimensao().getText()) > etds.dimensao.get(0))
                        {
                            //erro, tentativa de acesso incorreta ou fora do vetor
                        }*/
                    expInfo.tipoExpressao = etds.tipo.nome;
                    expInfo.nivelPonteirosTipo = 1 + etds.tipo.nPonteiros;
                }
            }
        }
        return null;
    }

    @Override
    public Void visitOp_nao(LAParser.Op_naoContext ctx) {
        visitChildren(ctx);
        if (ctx.getText().isEmpty() == false) {
            expInfo.tipoExpressao = "logico";
        }

        return null;
    }

    @Override
    public Void visitOp_opcional(LAParser.Op_opcionalContext ctx) {
        visitChildren(ctx);
        if (ctx.getText().isEmpty() == false) {
            expInfo.tipoExpressao = "logico";
        }

        return null;
    }

    @Override
    public Void visitOutros_termos_logicos(LAParser.Outros_termos_logicosContext ctx) {
        visitChildren(ctx);
        if (ctx.getText().isEmpty() == false) {
            expInfo.tipoExpressao = "logico";
        }

        return null;
    }

    @Override
    public Void visitParcela_logica(LAParser.Parcela_logicaContext ctx) {
        visitChildren(ctx);
        if (ctx.getText().isEmpty() == false) {
            if (ctx.plTRUE != null || ctx.plFALSE != null) {
                expInfo.tipoExpressao = "logico";
            }
        }

        return null;
    }

    @Override
    public Void visitOutros_fatores_logicos(LAParser.Outros_fatores_logicosContext ctx) {
        visitChildren(ctx);
        if (ctx.getText().isEmpty() == false) {
            expInfo.tipoExpressao = "logico";
        }

        return null;
    }

}
