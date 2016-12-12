/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trabalho1;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.sound.midi.SysexMessage;
import trabalho1.parser.LABaseVisitor;
import trabalho1.parser.LAParser;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Classe principal do Gerador de Código.
 *
 * Recebe como input um arquivo com um programa em linguagem LA, sem erros
 * (tanto sintáticos quanto semânticos). Como output, gera o mesmo programa em
 * linguagem C, "traduzindo" de forma a preservar o máximo de características da
 * linguagem LA.
 */
public class GeradorCodigo extends LABaseVisitor<Void> {

    // "dicionários" (ou seja, Maps) vão ser usados nesta classe, pois além
    // de serem simples de implementar, são mais que o suficiente para
    // a classe do Gerador de Código.
    // Uso padrão: - relação int <-> %d (scanf);
    //             - conversão real -> float;
    //             - etc
    Map<String, String> dictionaryTipos = new ConcurrentHashMap<String, String>();
    Map<String, String> variaveisTipos = new ConcurrentHashMap<String, String>();
    Map<String, String> variaveisScanf = new ConcurrentHashMap<String, String>();
    String tipoAtual = "";
    String scanfVariables = "";
    String imprimirCadeia = "";
    String imprimirVariavel = "";
    String imprimirExpressao = "";
    String cadastrandoRegistro = "";

    // na String saida irá ser armazenado todo o código do programa, que depois
    // iremos escrever efetivamente em um arquivo
    String saida = "";

    // variáveis auxiliares
    boolean imprimindo = false;
    boolean registrando = false;

    // Iremos usar o método "println" da classe PrintWriter para escrever
    // em arquivo
    PrintWriter writer;

    /**
     * Construtor GeradorCodigo
     *
     * arquivoSaida é o arquivo a ser escrito.
     *
     * @param arquivoSaida
     * @throws IOException
     */
    public GeradorCodigo(File arquivoSaida) throws IOException {

        // configurar para UTF-8
        writer = new PrintWriter(arquivoSaida, "UTF-8");

        // includes essenciais para compilar um programa em C
        saida += "#include <stdio.h>\n";
        saida += "#include <stdlib.h>\n";

        // dicionário com tradução de tipos
        dictionaryTipos.put("real", "float");
        dictionaryTipos.put("inteiro", "int");
        dictionaryTipos.put("logico", "int");
        dictionaryTipos.put("literal", "char");

        // dicionário com tradução dos tipos para o scanf
        variaveisScanf.put("float", "%f");
        variaveisScanf.put("int", "%d");
        variaveisScanf.put("char", "%s");
    }

    /**
     * closeCerto
     * 
     * Escrita no arquivo
     */
    public void closeCerto() {
        writer.println(saida);
        writer.close();
    }

    /**
     * visitPrograma
     * Regra inicial - programa
     *
     * @param ctx
     * @return
     */
    @Override
    public Void visitPrograma(LAParser.ProgramaContext ctx) {
        visitDeclaracoes(ctx.declaracoes());
        saida += "int main() {\n";
        visitCorpo(ctx.corpo());
        saida += "return 0;\n}\n"; // retorno padrão em C

        return null;
    }

    /**
     * visitDeclaracoes
     * Declarações globais de variável e função
     *
     * @param ctx
     * @return
     */
    @Override
    public Void visitDeclaracoes(LAParser.DeclaracoesContext ctx) {
        if (ctx.decl_local_global() != null && ctx.decl_local_global().getText().isEmpty() == false) {
            visitDecl_local_global(ctx.decl_local_global());
            visitDeclaracoes(ctx.declaracoes());
        }
        return null;
    }

    /**
     * visitCorpo
     * Corpo do programa
     *
     * @param ctx
     * @return
     */
    @Override
    public Void visitCorpo(LAParser.CorpoContext ctx) {
        if (ctx.declaracoes_locais() != null && ctx.declaracoes_locais().getText().isEmpty() == false) {
            visitDeclaracoes_locais(ctx.declaracoes_locais());
        }
        if (ctx.comandos() != null && ctx.comandos().getText().isEmpty() == false) {
            visitComandos(ctx.comandos());
        }
        return null;
    }

    /**
     * visitComandos
     * Comandos do programa, "wrapper"
     *
     * @param ctx
     * @return
     */
    @Override
    public Void visitComandos(LAParser.ComandosContext ctx) {
        if (ctx.cmd() != null && ctx.cmd().getText().isEmpty() == false) {
            visitCmd(ctx.cmd());
        }
        if (ctx.comandos() != null && ctx.comandos().getText().isEmpty() == false) {
            visitComandos(ctx.comandos());
        }
        return null;
    }

    /**
     * visitCmd
     * Comandos do programa, implementação
     *
     * @param ctx
     * @return
     */
    @Override
    public Void visitCmd(LAParser.CmdContext ctx) {

        // comando LEIA
        // há a necessidade de usar o dicionário para funcionar
        // corretamente
        if (ctx.idLeia != null && ctx.identificador().getText().isEmpty() == false) {

            String tipo = variaveisTipos.get(ctx.identificador().IDENT().getText());
            if (!tipo.equals("char")) {
                saida += "scanf(\"" + variaveisScanf.get(tipo) + "\", &";
            } else {
                saida += "gets(";
            }
            visitIdentificador(ctx.identificador());
            saida += ");\n";

            LAParser.Mais_identContext pointer = ctx.mais_ident();

            while (pointer != null && pointer.getText().isEmpty() == false) {
                tipo = variaveisTipos.get(pointer.identificador().IDENT().getText());
                saida += "scanf(\"" + variaveisScanf.get(tipo) + "\", ";
                if (!tipo.equals("char*")) {
                    saida += "&";
                }
                visitIdentificador(pointer.identificador());
                saida += ");\n";
                if (pointer.mais_ident() != null) {
                    pointer = pointer.mais_ident();
                }
            }
        }

        // comando ESCREVA
        if (ctx.idEscreva != null && ctx.idEscreva.getText().isEmpty() == false) {

            imprimindo = true;

            imprimirExpressao = "";
            imprimirCadeia = "";
            imprimirVariavel = "";

            saida += "printf(";
            visitExpressao(ctx.expressao());

            if (imprimirCadeia != "") {
                saida += imprimirCadeia + ");\n";
            } else {
                saida += "\"" + variaveisScanf.get(variaveisTipos.get(imprimirVariavel)) + "\", " + imprimirExpressao + ");\n";
            }

            LAParser.Mais_expressaoContext pointer = ctx.mais_expressao();

            while (pointer != null && pointer.getText().isEmpty() == false) {

                imprimirExpressao = "";
                imprimirCadeia = "";
                imprimirVariavel = "";

                saida += "printf(";
                visitExpressao(pointer.expressao());

                if (imprimirCadeia != "") {
                    saida += imprimirCadeia + ");\n";
                } else {
                    saida += "\"" + variaveisScanf.get(variaveisTipos.get(imprimirVariavel)) + "\", " + imprimirExpressao + ");\n";
                }

                if (pointer.mais_expressao() != null) {
                    pointer = pointer.mais_expressao();
                }
            }

            imprimirExpressao = "";
            imprimirCadeia = "";
            imprimirVariavel = "";

            imprimindo = false;

        }

        // IF ELSE
        if (ctx.idIf != null && ctx.idIf.getText().isEmpty() == false) {
            saida += "if(";
            visitExpressao(ctx.expressao());
            saida += ") {\n";
            visitComandos(ctx.idComandos);
            saida += "\n}\n";
            if (ctx.senao_opcional() != null && ctx.senao_opcional().getText().isEmpty() == false) {
                saida += "else {\n";
                visitSenao_opcional(ctx.senao_opcional());
                saida += "\n}\n";
            }
        }

        // ATRIBUICAO
        if (ctx.cmdAtribuicaoIdent != null && ctx.cmdAtribuicaoIdent.getText().isEmpty() == false) {

            if (ctx.chamada_atribuicao().argumentos_opcional() != null && ctx.chamada_atribuicao().argumentos_opcional().getText().isEmpty() == false) {
                imprimindo = false;
            } else {
                imprimindo = true;
            }

            imprimirExpressao = ctx.IDENT().getText();

            if (variaveisTipos.get(imprimirExpressao) != null && dictionaryTipos.get(variaveisTipos.get(imprimirExpressao)) != null) {
                imprimirExpressao = ctx.IDENT().getText();
                Iterator it = variaveisTipos.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry pair = (Map.Entry) it.next();
                    String key = pair.getKey().toString();
                    if (key.startsWith(variaveisTipos.get(imprimirExpressao))) {
                        key = key.replace(variaveisTipos.get(imprimirExpressao), imprimirExpressao);
                        variaveisTipos.put(key, pair.getValue().toString());
                    }

                }

            }

            if (ctx.chamada_atribuicao() != null && ctx.chamada_atribuicao().getText().isEmpty() == false) {
                if (ctx.chamada_atribuicao().argumentos_opcional() != null && ctx.chamada_atribuicao().argumentos_opcional().getText().isEmpty() == false) {
                    saida += ctx.IDENT().getText();
                    saida += "(";
                    visitArgumentos_opcional(ctx.chamada_atribuicao().argumentos_opcional());
                    saida += ")";
                } else {
                    if (ctx.chamada_atribuicao().outros_ident() != null && ctx.chamada_atribuicao().outros_ident().getText().isEmpty() == false) {
                        visitOutros_ident(ctx.chamada_atribuicao().outros_ident());
                    }
                    if (ctx.chamada_atribuicao().dimensao() != null && ctx.chamada_atribuicao().dimensao().getText().isEmpty() == false) {
                        visitDimensao(ctx.chamada_atribuicao().dimensao());
                    }

                    imprimindo = false;

                    if (variaveisTipos.get(imprimirExpressao) != null && variaveisTipos.get(imprimirExpressao).equals("char")) {
                        saida += "strcpy(" + imprimirExpressao + ",";
                        if (ctx.chamada_atribuicao().expressao() != null && ctx.chamada_atribuicao().expressao().getText().isEmpty() == false) {
                            visitExpressao(ctx.chamada_atribuicao().expressao());
                        }
                        saida += ")";
                    } else {
                        saida += imprimirExpressao + " = ";
                        if (ctx.chamada_atribuicao().expressao() != null && ctx.chamada_atribuicao().expressao().getText().isEmpty() == false) {
                            visitExpressao(ctx.chamada_atribuicao().expressao());
                        }
                    }
                }
            }

            imprimindo = false;

            saida += ";\n";
        }

        // SWITCH CASE
        if (ctx.idCaso != null && ctx.idCaso.getText().isEmpty() == false) {
            saida += "switch(";
            visitExp_aritmetica(ctx.idCaso);
            saida += "){\n";

            if (ctx.selecao() != null && ctx.selecao().getText().isEmpty() == false) {
                visitSelecao(ctx.selecao());
            }

            if (ctx.idDefault != null && ctx.idDefault.getText().isEmpty() == false) {
                saida += "default:\n";
                visitComandos(ctx.idDefault.comandos());
            }

            saida += "}\n";
        }

        // FOR
        if (ctx.idFor != null && ctx.idFor.getText().isEmpty() == false) {
            saida += "for(";
            saida += ctx.idFor.getText();
            saida += " = ";
            visitExp_aritmetica(ctx.idExp1);
            saida += "; ";
            saida += ctx.idFor.getText();
            saida += " <= ";
            visitExp_aritmetica(ctx.idExp2);
            saida += "; ";
            saida += ctx.idFor.getText() + "++){\n";
            visitComandos(ctx.comandos());
            saida += "}\n";

        }

        // WHILE
        if (ctx.idWhile != null && ctx.idWhile.getText().isEmpty() == false) {
            saida += "while(";
            saida += ctx.idWhile.getText();
            saida += "){\n";
            visitComandos(ctx.comandos());
            saida += "}\n";
        }

        // DO WHILE
        if (ctx.idDoWhile != null && ctx.idDoWhile.getText().isEmpty() == false) {
            saida += "do{\n";
            visitComandos(ctx.comandos());
            saida += "}";
            saida += "while(";
            visitExpressao(ctx.expressao());
            saida += ");\n";
        }

        // ATRIBUIÇÂO DE PONTEIRO
        if (ctx.cmdAtribPonteiroIdent != null && ctx.cmdAtribPonteiroIdent.getText().isEmpty() == false) {
            saida += "*";
            saida += ctx.cmdAtribPonteiroIdent.getText();
            if (ctx.outros_ident() != null && ctx.outros_ident().getText().isEmpty() == false) {
                visitOutros_ident(ctx.outros_ident());
            }

            if (ctx.dimensao() != null && ctx.dimensao().getText().isEmpty() == false) {
                visitDimensao(ctx.dimensao());
            }

            saida += "=";
            visitExpressao(ctx.expressao());
            saida += ";\n";
        }

        // RETORNO
        if (ctx.cmdReturn != null && ctx.cmdReturn.getText().isEmpty() == false) {
            saida += "return ";
            visitExpressao(ctx.expressao());
            saida += ";\n";
        }

        return null;
    }

    /**
     * visitSelecao
     *
     * @param ctx
     * @return
     */
    @Override
    public Void visitSelecao(LAParser.SelecaoContext ctx) {
        visitConstantes(ctx.constantes());
        visitComandos(ctx.comandos());
        saida += "break;\n";
        if (ctx.mais_selecao() != null && ctx.mais_selecao().getText().isEmpty() == false) {
            visitSelecao(ctx.mais_selecao().selecao());
        }

        return null;
    }

    /**
     * visitConstantes
     *
     * @param ctx
     * @return
     */
    @Override
    public Void visitConstantes(LAParser.ConstantesContext ctx) {

        if (ctx.numero_intervalo().intervalo_opcional().NUM_INT() == null) {
            int value = Integer.parseInt(ctx.numero_intervalo().NUM_INT().toString());
            saida += "case " + value + ":\n ";
        } else {
            int firstValue = Integer.parseInt(ctx.numero_intervalo().NUM_INT().toString());
            int lastValue = Integer.parseInt(ctx.numero_intervalo().intervalo_opcional().NUM_INT().toString());

            for (int i = firstValue; i <= lastValue; i++) {
                saida += "case " + i + ":\n";
            }
        }
        return null;
    }

    /**
     * visitExpressao
     *
     * @param ctx
     * @return
     */
    @Override
    public Void visitExpressao(LAParser.ExpressaoContext ctx) {
        if (ctx.termo_logico() != null && ctx.termo_logico().getText().isEmpty() == false) {
            visitTermo_logico(ctx.termo_logico());
        }
        if (ctx.outros_termos_logicos() != null && ctx.outros_termos_logicos().getText().isEmpty() == false) {
            visitOutros_termos_logicos(ctx.outros_termos_logicos());
        }
        return null;
    }

    /**
     * visitTermo_logico
     *
     * @param ctx
     * @return
     */
    @Override
    public Void visitTermo_logico(LAParser.Termo_logicoContext ctx) {
        if (ctx.fator_logico() != null && ctx.fator_logico().getText().isEmpty() == false) {
            visitFator_logico(ctx.fator_logico());
        }
        if (ctx.outros_fatores_logicos() != null && ctx.outros_fatores_logicos().getText().isEmpty() == false) {
            visitOutros_fatores_logicos(ctx.outros_fatores_logicos());
        }
        return null;
    }

    /**
     * visitFator_logico
     *
     * @param ctx
     * @return
     */
    @Override
    public Void visitFator_logico(LAParser.Fator_logicoContext ctx) {
        if (ctx.op_nao() != null && ctx.op_nao().getText().isEmpty() == false) {
            saida += "!";
        }
        if (ctx.parcela_logica() != null && ctx.parcela_logica().getText().isEmpty() == false) {
            visitParcela_logica(ctx.parcela_logica());
        }
        return null;
    }

    /**
     * visitParcela_logica
     *
     * @param ctx
     * @return
     */
    @Override
    public Void visitParcela_logica(LAParser.Parcela_logicaContext ctx) {

        if (ctx.plTRUE != null) {
            saida += "true";
        } else if (ctx.plFALSE != null) {
            saida += "false";
        } else {
            visitExp_relacional(ctx.exp_relacional());
        }
        return null;
    }

    /**
     * visitExp_relacional
     *
     * @param ctx
     * @return
     */
    @Override
    public Void visitExp_relacional(LAParser.Exp_relacionalContext ctx) {
        if (ctx.exp_aritmetica() != null && ctx.exp_aritmetica().getText().isEmpty() == false) {
            visitExp_aritmetica(ctx.exp_aritmetica());
        }
        if (ctx.op_opcional() != null && ctx.op_opcional().getText().isEmpty() == false) {
            visitOp_opcional(ctx.op_opcional());
        }
        return null;
    }

    /**
     * visitOutros_termos_logicos
     *
     * @param ctx
     * @return
     */
    @Override
    public Void visitOutros_termos_logicos(LAParser.Outros_termos_logicosContext ctx) {
        if (ctx.termo_logico() != null && ctx.termo_logico().getText().isEmpty() == false) {
            saida += " || ";
            visitTermo_logico(ctx.termo_logico());
        }
        if (ctx.outros_termos_logicos() != null && ctx.outros_termos_logicos().getText().isEmpty() == false) {
            visitOutros_termos_logicos(ctx.outros_termos_logicos());
        }
        return null;
    }

    /**
     * visitOutros_fatores_logicos
     *
     * @param ctx
     * @return
     */
    @Override
    public Void visitOutros_fatores_logicos(LAParser.Outros_fatores_logicosContext ctx) {
        if (ctx.fator_logico() != null && ctx.fator_logico().getText().isEmpty() == false) {
            saida += " && ";
            visitFator_logico(ctx.fator_logico());
        }
        if (ctx.outros_fatores_logicos() != null && ctx.outros_fatores_logicos().getText().isEmpty() == false) {
            visitOutros_fatores_logicos(ctx.outros_fatores_logicos());
        }
        return null;
    }

    /**
     * visitIdentificador
     *
     * @param ctx
     * @return
     */
    @Override
    public Void visitIdentificador(LAParser.IdentificadorContext ctx) {
        if (ctx.ponteiros_opcionais() != null && ctx.ponteiros_opcionais().getText().isEmpty() == false) {
            visitPonteiros_opcionais(ctx.ponteiros_opcionais());
        }
        if (ctx.IDENT() != null && ctx.IDENT().getText().isEmpty() == false) {
            if (imprimindo) {
                imprimirVariavel += ctx.IDENT().getText();
                imprimirExpressao += ctx.IDENT().getText();
            } else {
                saida += ctx.IDENT().getText();
            }
        }
        if (ctx.dimensao() != null && ctx.dimensao().getText().isEmpty() == false) {
            visitDimensao(ctx.dimensao());
        }
        if (ctx.outros_ident() != null && ctx.outros_ident().getText().isEmpty() == false) {
            visitOutros_ident(ctx.outros_ident());
        }
        return null;
    }

    /**
     * visitOutros_ident
     *
     * @param ctx
     * @return
     */
    @Override
    public Void visitOutros_ident(LAParser.Outros_identContext ctx) {
        if (ctx.identificador() != null && ctx.identificador().getText().isEmpty() == false) {
            if (imprimindo) {
                imprimirVariavel += ".";
                imprimirExpressao += ".";
            } else {
                saida += ".";
            }
            visitIdentificador(ctx.identificador());
        }
        return null;
    }

    /**
     * visitPonteiros_opcionais
     *
     * @param ctx
     * @return
     */
    @Override
    public Void visitPonteiros_opcionais(LAParser.Ponteiros_opcionaisContext ctx) {
        if (imprimindo) {
            saida += "*";
        }
        if (ctx.ponteiros_opcionais() != null && ctx.ponteiros_opcionais().getText().isEmpty() == false) {
            visitPonteiros_opcionais(ctx.ponteiros_opcionais());
        }

        return null;
    }

    /**
     * visitDecl_local_global
     *
     * @param ctx
     * @return
     */
    @Override
    public Void visitDecl_local_global(LAParser.Decl_local_globalContext ctx) {
        if (ctx.declaracao_local() != null && ctx.declaracao_local().getText().isEmpty() == false) {
            visitDeclaracao_local(ctx.declaracao_local());
        } else {
            visitDeclaracao_global(ctx.declaracao_global());
        }
        return null;
    }

    /**
     * visitDeclaracao_local
     *
     * @param ctx
     * @return
     */
    @Override
    public Void visitDeclaracao_local(LAParser.Declaracao_localContext ctx) {
        if (ctx.variavel() != null && ctx.variavel().getText().isEmpty() == false) {
            visitVariavel(ctx.variavel());
        } else if (ctx.dclLocalConst != null && ctx.dclLocalConst.getText().isEmpty() == false) {
            saida += "#define ";
            saida += ctx.IDENT().getText() + " ";
            if (ctx.valor_constante().idVdd != null) {
                saida += "true";
            } else if (ctx.valor_constante().idFake != null) {
                saida += "false";
            } else {
                saida += ctx.valor_constante().getText();
            }
            saida += "\n";
        }

        if (ctx.dclLocalTipo != null && ctx.dclLocalTipo.getText().isEmpty() == false) {
            saida += "typedef ";
            String ident = ctx.IDENT().getText();

            if (ctx.tipo().registro() != null && ctx.tipo().registro().getText().isEmpty() == false) {
                registrando = true;
                cadastrandoRegistro = ident;
                visitRegistro(ctx.tipo().registro());
                saida += " " + ident + ";\n";
                registrando = false;
            } else {
                visitTipo(ctx.tipo());
                saida += ctx.IDENT().getText() + " ";
                saida += ";\n";
            }

            dictionaryTipos.put(ident, ident);
        }

        return null;
    }

    /**
     * visitDeclaracoes_locais
     *
     * @param ctx
     * @return
     */
    @Override
    public Void visitDeclaracoes_locais(LAParser.Declaracoes_locaisContext ctx) {
        visitDeclaracao_local(ctx.declaracao_local());
        if (ctx.declaracoes_locais() != null && ctx.declaracoes_locais().getText().isEmpty() == false) {
            visitDeclaracoes_locais(ctx.declaracoes_locais());
        }
        return null;
    }

    /**
     * visitVariavel
     *
     * @param ctx
     * @return
     */
    @Override
    public Void visitVariavel(LAParser.VariavelContext ctx) {

        String ident = ctx.IDENT().getText();

        if (ctx.tipo().registro() == null) {

            tipoAtual = ctx.tipo().getText();
            String nPointers = "";

            for (char ch : tipoAtual.toCharArray()) {
                if (ch == '^') {
                    nPointers += "*";
                }
            }

            tipoAtual = tipoAtual.replaceAll("[^a-zA-Z]", "");

            saida += dictionaryTipos.get(tipoAtual) + nPointers + " " + ident;

            if (!registrando) {
                variaveisTipos.put(ident, dictionaryTipos.get(tipoAtual) + nPointers);
            } else {
                variaveisTipos.put(cadastrandoRegistro + "." + ident, dictionaryTipos.get(tipoAtual) + nPointers);
            }

            if (ctx.dimensao() != null && ctx.dimensao().getText().isEmpty() == false) {
                visitDimensao(ctx.dimensao());
            }
            if (ctx.mais_var() != null && ctx.mais_var().getText().isEmpty() == false) {
                visitMais_var(ctx.mais_var());
            }

            if (tipoAtual.equals("literal")) {
                saida += "[80]";
            }

            saida += ";\n";
        } else {
            registrando = true;
            cadastrandoRegistro = ident;
            visitRegistro(ctx.tipo().registro());
            saida += " " + ident + ";\n";
            registrando = false;
        }
        return null;
    }

    /**
     * visitMais_var
     *
     * @param ctx
     * @return
     */
    @Override
    public Void visitMais_var(LAParser.Mais_varContext ctx) {
        saida += ", ";
        String ident = ctx.IDENT().getText();
        saida += ident;
        variaveisTipos.put(ident, dictionaryTipos.get(tipoAtual));
        if (ctx.dimensao() != null && ctx.dimensao().getText().isEmpty() == false) {
            visitDimensao(ctx.dimensao());
        }
        if (ctx.mais_var() != null && ctx.mais_var().getText().isEmpty() == false) {
            visitMais_var(ctx.mais_var());
        }

        return null;
    }

    /**
     * visitDimensao
     *
     * @param ctx
     * @return
     */
    @Override
    public Void visitDimensao(LAParser.DimensaoContext ctx) {

        if (imprimindo) {
            imprimirExpressao += "[";
        } else {
            saida += "[";
        }

        visitExp_aritmetica(ctx.exp_aritmetica());

        if (imprimindo) {
            imprimirExpressao += "]";
        } else {
            saida += "]";
        }

        if (ctx.dimensao() != null && ctx.dimensao().getText().isEmpty() == false) {
            visitDimensao(ctx.dimensao());
        }
        return null;
    }

    /**
     * visitExp_aritmetica
     *
     * @param ctx
     * @return
     */
    @Override
    public Void visitExp_aritmetica(LAParser.Exp_aritmeticaContext ctx) {
        if (ctx.termo() != null && ctx.termo().getText().isEmpty() == false) {
            visitTermo(ctx.termo());
        }
        if (ctx.outros_termos() != null && ctx.outros_termos().getText().isEmpty() == false) {
            visitOutros_termos(ctx.outros_termos());
        }
        return null;
    }

    /**
     * visitTermo
     *
     * @param ctx
     * @return
     */
    @Override
    public Void visitTermo(LAParser.TermoContext ctx) {
        if (ctx.fator() != null && ctx.fator().getText().isEmpty() == false) {
            visitFator(ctx.fator());
        }
        if (ctx.outros_fatores() != null && ctx.outros_fatores().getText().isEmpty() == false) {
            visitOutros_fatores(ctx.outros_fatores());
        }
        return null;
    }

    /**
     * visitOutros_fatores
     *
     * @param ctx
     * @return
     */
    @Override
    public Void visitOutros_fatores(LAParser.Outros_fatoresContext ctx) {
        if (ctx.op_multiplicacao() != null && ctx.op_multiplicacao().getText().isEmpty() == false) {
            saida += ctx.op_multiplicacao().getText();
        }
        if (ctx.fator() != null && ctx.fator().getText().isEmpty() == false) {
            visitFator(ctx.fator());
        }
        if (ctx.outros_fatores() != null && ctx.outros_fatores().getText().isEmpty() == false) {
            visitOutros_fatores(ctx.outros_fatores());
        }
        return null;
    }

    /**
     * visitFator
     *
     * @param ctx
     * @return
     */
    @Override
    public Void visitFator(LAParser.FatorContext ctx) {
        if (ctx.parcela() != null && ctx.parcela().getText().isEmpty() == false) {
            visitParcela(ctx.parcela());
        } else if (ctx.outras_parcelas() != null && ctx.outras_parcelas().getText().isEmpty() == false) {
            visitOutras_parcelas(ctx.outras_parcelas());
        }
        return null;
    }

    /**
     * visitOutros_termos
     *
     * @param ctx
     * @return
     */
    @Override
    public Void visitOutros_termos(LAParser.Outros_termosContext ctx) {
        if (ctx.op_adicao() != null && ctx.op_adicao().getText().isEmpty() == false) {
            if (imprimindo) {
                imprimirExpressao += ctx.op_adicao().getText();
            } else {
                saida += ctx.op_adicao().getText();
            }
        }
        if (ctx.termo() != null && ctx.termo().getText().isEmpty() == false) {
            visitTermo(ctx.termo());
        }
        if (ctx.outros_termos() != null && ctx.outros_termos().getText().isEmpty() == false) {
            visitOutros_termos(ctx.outros_termos());
        }
        return null;
    }

    /**
     * visitParcela_unario
     *
     * @param ctx
     * @return
     */
    @Override
    public Void visitParcela_unario(LAParser.Parcela_unarioContext ctx) {
        if (ctx.puNomeIdent1 != null) {
            if (imprimindo) {
                imprimirExpressao += "*" + ctx.IDENT().getText();
            } else {
                saida += "*" + ctx.IDENT().getText();
            }
            if (ctx.outros_ident() != null && ctx.outros_ident().getText().isEmpty() == false) {
                visitOutros_ident(ctx.outros_ident());
            }
            if (ctx.dimensao() != null && ctx.dimensao().getText().isEmpty() == false) {
                visitDimensao(ctx.dimensao());
            }
        } else if (ctx.puNomeIdent2 != null) {
            if (imprimindo) {
                if (imprimirVariavel == "") {
                    imprimirVariavel = ctx.IDENT().getText();
                }
                if ((variaveisTipos.get(imprimirVariavel) == "int" && variaveisTipos.get(ctx.IDENT().getText()) == "float") || (variaveisTipos.get(ctx.IDENT().getText()) == "*char") || variaveisTipos.get(ctx.IDENT().getText()) == "bool") {
                    imprimirVariavel = ctx.IDENT().getText();
                }

                imprimirExpressao += ctx.IDENT().getText();
            } else {
                saida += ctx.IDENT().getText();
            }
            visitChamada_partes(ctx.chamada_partes());
        } else if (ctx.NUM_INT() != null) {

            if (imprimindo) {
                imprimirExpressao += ctx.NUM_INT().getText();
            } else {
                saida += ctx.NUM_INT().getText();
            }
        } else if (ctx.NUM_REAL() != null) {
            if (imprimindo) {
                imprimirExpressao += ctx.NUM_REAL().getText();
            } else {
                saida += ctx.NUM_REAL().getText();
            }
        } else {
            saida += "(";
            if (ctx.expressao() != null && ctx.expressao().getText().isEmpty() == false) {
                visitExpressao(ctx.expressao());
            }
            saida += ")";
        }
        return null;
    }

    /**
     * visitChamada_partes
     *
     * @param ctx
     * @return
     */
    @Override
    public Void visitChamada_partes(LAParser.Chamada_partesContext ctx) {
        if (ctx.idAbre != null && ctx.idFecha != null) {
            if (imprimindo) {
                imprimirExpressao += "(";
            } else {
                saida += "(";
            }
            if (ctx.expressao() != null && ctx.expressao().getText().isEmpty() == false) {
                visitExpressao(ctx.expressao());
            }
            if (ctx.mais_expressao() != null && ctx.mais_expressao().getText().isEmpty() == false) {
                visitMais_expressao(ctx.mais_expressao());
            }
            if (imprimindo) {
                imprimirExpressao += ")";
            } else {
                saida += ")";
            }
        } else if (ctx.outros_ident() != null && ctx.outros_ident().getText().isEmpty() == false) {
            visitOutros_ident(ctx.outros_ident());
        }

        if (ctx.dimensao() != null && ctx.dimensao().getText().isEmpty() == false) {
            visitDimensao(ctx.dimensao());
        }

        return null;
    }

    /**
     * visitParcela
     *
     * @param ctx
     * @return
     */
    @Override
    public Void visitParcela(LAParser.ParcelaContext ctx) {
        if (ctx.op_unario() != null && ctx.op_unario().getText().isEmpty() == false) {
            //System.out.print(ctx.op_unario().getText());
            saida += ctx.op_unario().getText();
        }
        if (ctx.parcela_unario() != null && ctx.parcela_unario().getText().isEmpty() == false) {
            visitParcela_unario(ctx.parcela_unario());
        }
        if (ctx.parcela_nao_unario() != null && ctx.parcela_nao_unario().getText().isEmpty() == false) {
            visitParcela_nao_unario(ctx.parcela_nao_unario());
        }
        return null;
    }
    
    /**
     * visitParcela_nao_unario
     * @param ctx
     * @return 
     */
    @Override
    public Void visitParcela_nao_unario(LAParser.Parcela_nao_unarioContext ctx) {
        if (ctx.IDENT() != null) {
            saida += "&" + ctx.IDENT().getText();
            if (ctx.outros_ident() != null && ctx.outros_ident().getText().isEmpty() == false) {
                visitOutros_ident(ctx.outros_ident());
            }
            if (ctx.dimensao() != null && ctx.dimensao().getText().isEmpty() == false) {
                visitDimensao(ctx.dimensao());
            }
        } else {
            if (imprimindo) {
                imprimirCadeia += ctx.CADEIA().getText();
            } else {
                saida += ctx.CADEIA().getText();
            }
        }
        return null;
    }

    /**
     * visitOutras_parcelas
     * @param ctx
     * @return 
     */
    @Override
    public Void visitOutras_parcelas(LAParser.Outras_parcelasContext ctx) {
        if (ctx.parcela() != null && ctx.parcela().getText().isEmpty() == false) {
            saida += "%";
            visitParcela(ctx.parcela());
            if (ctx.outras_parcelas() != null && ctx.outras_parcelas().getText().isEmpty() == false) {
                visitOutras_parcelas(ctx.outras_parcelas());
            }
        }
        return null;
    }

    /**
     * visitOp_relacional
     * @param ctx
     * @return 
     */
    @Override
    public Void visitOp_relacional(LAParser.Op_relacionalContext ctx) {
        String op = ctx.getText();
        switch (op) {
            case "=":
                saida += "==";
                break;
            case "<>":
                saida += "!=";
                break;
            case ">=":
                saida += ">=";
                break;
            case "<=":
                saida += "<=";
                break;
            case ">":
                saida += ">";
                break;
            case "<":
                saida += "<";
                break;
        }
        return null;
    }

    /**
     * visitRegistro
     * @param ctx
     * @return 
     */
    @Override
    public Void visitRegistro(LAParser.RegistroContext ctx) {
        saida += "struct {\n";
        visitVariavel(ctx.variavel());
        if (ctx.mais_variaveis() != null && ctx.mais_variaveis().getText().isEmpty() == false) {

            LAParser.Mais_variaveisContext pointer = ctx.mais_variaveis();

            while (pointer != null && pointer.getText().isEmpty() == false) {

                visitVariavel(pointer.variavel());
                if (pointer.mais_variaveis() != null) {
                    pointer = pointer.mais_variaveis();
                }
            }
        }
        saida += "}";
        return null;
    }

    /**
     * visitDeclaracao_global
     * @param ctx
     * @return 
     */
    @Override
    public Void visitDeclaracao_global(LAParser.Declaracao_globalContext ctx) {
        if (ctx.dclGlobalProcedimento != null) {
            saida += "void " + ctx.dclGlobalProcedimento.getText() + "(";
            if (ctx.parametros_opcional() != null && ctx.parametros_opcional().getText().isEmpty() == false) {
                visitParametros_opcional(ctx.parametros_opcional());
            }
            saida += "){\n";
            if (ctx.declaracoes_locais() != null && ctx.declaracoes_locais().getText().isEmpty() == false) {
                visitDeclaracoes_locais(ctx.declaracoes_locais());
            }
            if (ctx.comandos() != null && ctx.comandos().getText().isEmpty() == false) {
                visitComandos(ctx.comandos());
            }
            saida += "}\n";
        } else if (ctx.dclGlobalFuncao != null) {
            visitTipo_estendido(ctx.tipo_estendido());
            saida += ctx.dclGlobalFuncao.getText() + "(";
            if (ctx.parametros_opcional() != null && ctx.parametros_opcional().getText().isEmpty() == false) {
                visitParametros_opcional(ctx.parametros_opcional());
            }
            saida += "){\n";
            if (ctx.declaracoes_locais() != null && ctx.declaracoes_locais().getText().isEmpty() == false) {
                visitDeclaracoes_locais(ctx.declaracoes_locais());
            }
            if (ctx.comandos() != null && ctx.comandos().getText().isEmpty() == false) {
                visitComandos(ctx.comandos());
            }
            saida += "}\n";

            String ident = ctx.dclGlobalFuncao.getText();

            if (ctx.tipo_estendido().tipo_basico_ident().tipo_basico() != null && ctx.tipo_estendido().tipo_basico_ident().tipo_basico().getText().isEmpty() == false) {
                if (ctx.tipo_estendido().tipo_basico_ident().tipo_basico().getText().equals("literal")) {
                    variaveisTipos.put(ident, "char");
                } else if (ctx.tipo_estendido().tipo_basico_ident().tipo_basico().getText().equals("inteiro")) {
                    variaveisTipos.put(ident, "int");
                } else if (ctx.tipo_estendido().tipo_basico_ident().tipo_basico().getText().equals("real")) {
                    variaveisTipos.put(ident, "float");
                } else if (ctx.tipo_estendido().tipo_basico_ident().tipo_basico().getText().equals("logico")) {
                    variaveisTipos.put(ident, "bool");
                }
            } else {
                variaveisTipos.put(ident, ctx.tipo_estendido().tipo_basico_ident().IDENT().getText());
            }
            /* Término da colocação da função na lista de variáveis */

        }

        return null;
    }

    /**
     * visitParametros_opcional
     * @param ctx
     * @return 
     */
    @Override
    public Void visitParametros_opcional(LAParser.Parametros_opcionalContext ctx) {
        if (ctx.parametro() != null && ctx.parametro().getText().isEmpty() == false) {
            visitParametro(ctx.parametro());
        }

        return null;
    }
    
    /**
     * visitParametro
     * @param ctx
     * @return 
     */
    @Override
    public Void visitParametro(LAParser.ParametroContext ctx) {
        visitTipo_estendido(ctx.tipo_estendido());
        visitIdentificador(ctx.identificador());

        /* Colocando parâmetros na lista de variáveis */
        String ident = ctx.identificador().IDENT().getText();

        if (ctx.tipo_estendido().tipo_basico_ident().tipo_basico() != null && ctx.tipo_estendido().tipo_basico_ident().tipo_basico().getText().isEmpty() == false) {
            if (ctx.tipo_estendido().tipo_basico_ident().tipo_basico().getText().equals("literal")) {
                variaveisTipos.put(ident, "char");
            } else if (ctx.tipo_estendido().tipo_basico_ident().tipo_basico().getText().equals("inteiro")) {
                variaveisTipos.put(ident, "int");
            } else if (ctx.tipo_estendido().tipo_basico_ident().tipo_basico().getText().equals("real")) {
                variaveisTipos.put(ident, "float");
            } else if (ctx.tipo_estendido().tipo_basico_ident().tipo_basico().getText().equals("logico")) {
                variaveisTipos.put(ident, "bool");
            }
        } else {
            variaveisTipos.put(ident, ctx.tipo_estendido().tipo_basico_ident().IDENT().getText());
        }
        /* Término da colocação de parametros na lista de variáveis */

        LAParser.Mais_identContext pointer = ctx.mais_ident();

        while (pointer != null && pointer.getText().isEmpty() == false) {
            saida += ", ";
            visitTipo_estendido(ctx.tipo_estendido());
            visitIdentificador(pointer.identificador());

            /* Colocando parâmetros na lista de variáveis */
            ident = pointer.identificador().IDENT().getText();

            if (ctx.tipo_estendido().tipo_basico_ident().tipo_basico() != null && ctx.tipo_estendido().tipo_basico_ident().tipo_basico().getText().isEmpty() == false) {
                if (ctx.tipo_estendido().tipo_basico_ident().tipo_basico().getText().equals("literal")) {
                    variaveisTipos.put(ident, "char");
                } else if (ctx.tipo_estendido().tipo_basico_ident().tipo_basico().getText().equals("inteiro")) {
                    variaveisTipos.put(ident, "int");
                } else if (ctx.tipo_estendido().tipo_basico_ident().tipo_basico().getText().equals("real")) {
                    variaveisTipos.put(ident, "float");
                } else if (ctx.tipo_estendido().tipo_basico_ident().tipo_basico().getText().equals("logico")) {
                    variaveisTipos.put(ident, "bool");
                }
            } else {
                variaveisTipos.put(ident, ctx.tipo_estendido().tipo_basico_ident().IDENT().getText());
            }
            /* Término da colocação de parametros na lista de variáveis */

            if (pointer.mais_ident() != null) {
                pointer = pointer.mais_ident();
            }
        }

        if (ctx.mais_parametros() != null && ctx.mais_parametros().getText().isEmpty() == false) {
            saida += ", ";
            visitParametro(ctx.mais_parametros().parametro());
        }

        return null;
    }

    /**
     * visitTipo_estendido
     * @param ctx
     * @return 
     */
    @Override
    public Void visitTipo_estendido(LAParser.Tipo_estendidoContext ctx) {
        if (ctx.ponteiros_opcionais() != null && ctx.ponteiros_opcionais().getText().isEmpty() == false) {
            visitPonteiros_opcionais(ctx.ponteiros_opcionais());
        }
        visitTipo_basico_ident(ctx.tipo_basico_ident());

        return null;
    }

    /**
     * visitTipo_basico_ident
     * @param ctx
     * @return 
     */
    @Override
    public Void visitTipo_basico_ident(LAParser.Tipo_basico_identContext ctx) {

        if (ctx.tipo_basico().getText().equals("literal")) {
            saida += "char* ";
        } else if (ctx.tipo_basico().getText().equals("inteiro")) {
            System.out.print("int ");
            saida += "int ";
        } else if (ctx.tipo_basico().getText().equals("real")) {
            saida += "float ";
        } else if (ctx.tipo_basico().getText().equals("logico")) {
            saida += "bool ";
        } else if (ctx.IDENT() != null && ctx.IDENT().getText().isEmpty() == false) {
            saida += ctx.IDENT().getText();
        }
        return null;
    }

    /**
     * visitChamada_atribuicao
     * @param ctx
     * @return 
     */
    @Override
    public Void visitChamada_atribuicao(LAParser.Chamada_atribuicaoContext ctx) {
        if (ctx.argumentos_opcional() != null && ctx.argumentos_opcional().getText().isEmpty() == false) {
            saida += "(";
            visitArgumentos_opcional(ctx.argumentos_opcional());
            saida += ")";

        }
        return null;
    }

}
