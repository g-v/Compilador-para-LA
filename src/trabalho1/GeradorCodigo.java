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
 *
 * @author guerra
 */
public class GeradorCodigo extends LABaseVisitor<Void> {

    Map<String, String> dictionaryTipos = new ConcurrentHashMap<String, String>();
    Map<String, String> variaveisTipos = new ConcurrentHashMap<String, String>();
    Map<String, String> variaveisScanf = new ConcurrentHashMap<String, String>();
    String tipoAtual = "";
    String scanfVariables = "";
    String imprimirCadeia = "";
    String imprimirVariavel = "";
    String imprimirExpressao = "";
    String cadastrandoRegistro = "";
    String saida = "";
    boolean imprimindo = false;
    boolean registrando = false;
    PrintWriter writer;

    public GeradorCodigo(File arquivoSaida) throws IOException {
        /*
        System.out.print("#include <stdio.h>\n");
        System.out.print("#include <stdlib.h>\n");
        System.out.print("typedef int bool\n");
        System.out.print("#define true 1\n");
        System.out.print("#define false 0\n\n\n");
         */

        writer = new PrintWriter(arquivoSaida, "UTF-8");

        saida += "#include <stdio.h>\n";
        saida += "#include <stdlib.h>\n";
        saida += "typedef int bool\n";
        saida += "#define true 1\n";
        saida += "#define false 0\n\n\n";

        dictionaryTipos.put("real", "float");
        dictionaryTipos.put("inteiro", "int");
        dictionaryTipos.put("logico", "bool");
        dictionaryTipos.put("literal", "char");

        variaveisScanf.put("float", "%f");
        variaveisScanf.put("int", "%d");
        variaveisScanf.put("bool", "%d");
        variaveisScanf.put("char", "%s");
    }

    public void closeCerto() {
        writer.println(saida);
        writer.close();
    }

    @Override
    public Void visitPrograma(LAParser.ProgramaContext ctx) {
        visitDeclaracoes(ctx.declaracoes());
        //System.out.print("int main() {\n");
        saida += "int main() {\n";
        visitCorpo(ctx.corpo());
        //System.out.print("return 0;\n}\n");
        saida += "return 0;\n}\n";

        // escrever String "saida" no arquivo
        return null;
    }

    @Override
    public Void visitDeclaracoes(LAParser.DeclaracoesContext ctx) {
        if (ctx.decl_local_global() != null && ctx.decl_local_global().getText().isEmpty() == false) {
            visitDecl_local_global(ctx.decl_local_global());
            visitDeclaracoes(ctx.declaracoes());
        }
        return null;
    }

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

    @Override
    public Void visitCmd(LAParser.CmdContext ctx) {

        // LEIA
        if (ctx.idLeia != null && ctx.identificador().getText().isEmpty() == false) {

            String tipo = variaveisTipos.get(ctx.identificador().IDENT().getText());
            if (!tipo.equals("char")) {
                //System.out.print("scanf(\"" + variaveisScanf.get(tipo) + "\", &");
                saida += "scanf(\"" + variaveisScanf.get(tipo) + "\", &";
            } else {
                //System.out.print("gets(");
                saida += "gets(";
            }
            visitIdentificador(ctx.identificador());
            //System.out.print(");\n");
            saida += ");\n";

            LAParser.Mais_identContext pointer = ctx.mais_ident();

            while (pointer != null && pointer.getText().isEmpty() == false) {
                tipo = variaveisTipos.get(pointer.identificador().IDENT().getText());
                //System.out.print("scanf(\"" + variaveisScanf.get(tipo) + "\", ");
                saida += "scanf(\"" + variaveisScanf.get(tipo) + "\", ";
                if (!tipo.equals("char*")) {
                    //System.out.print("&");
                    saida += "&";
                }
                visitIdentificador(pointer.identificador());
                //System.out.print(");\n");
                saida += ");\n";
                if (pointer.mais_ident() != null) {
                    pointer = pointer.mais_ident();
                }
            }
        }

        // ESCREVA
        if (ctx.idEscreva != null && ctx.idEscreva.getText().isEmpty() == false) {

            imprimindo = true;

            imprimirExpressao = "";
            imprimirCadeia = "";
            imprimirVariavel = "";

            //System.out.print("printf(");
            saida += "printf(";
            visitExpressao(ctx.expressao());

            if (imprimirCadeia != "") {
                //System.out.print(imprimirCadeia + ");\n");
                saida += imprimirCadeia + ");\n";
            } else {
                //System.out.print("\"" + variaveisScanf.get(variaveisTipos.get(imprimirVariavel)) + "\", " + imprimirExpressao + ");\n");
                saida += "\"" + variaveisScanf.get(variaveisTipos.get(imprimirVariavel)) + "\", " + imprimirExpressao + ");\n";
            }

            LAParser.Mais_expressaoContext pointer = ctx.mais_expressao();

            while (pointer != null && pointer.getText().isEmpty() == false) {

                imprimirExpressao = "";
                imprimirCadeia = "";
                imprimirVariavel = "";

                //System.out.print("printf(");
                saida += "printf(";
                visitExpressao(pointer.expressao());

                if (imprimirCadeia != "") {
                    //System.out.print(imprimirCadeia + ");\n");
                    saida += imprimirCadeia + ");\n";
                } else {
                    //System.out.print("\"" + variaveisScanf.get(variaveisTipos.get(imprimirVariavel)) + "\", " + imprimirExpressao + ");\n");
                    saida += "\"" + variaveisScanf.get(variaveisTipos.get(imprimirVariavel)) + "\", " + imprimirExpressao + ");\n";
                }

                if (pointer.mais_expressao() != null) {
                    pointer = pointer.mais_expressao();
                }
            }

            imprimirExpressao = "";
            imprimirCadeia = "";
            imprimirVariavel = "";

            //////////////////System.out.print("printf(\"\\n\");\n");
            imprimindo = false;

        }

        // IF ELSE
        if (ctx.idIf != null && ctx.idIf.getText().isEmpty() == false) {
            //System.out.print("if(");
            saida += "if(";
            visitExpressao(ctx.expressao());
            //System.out.print(") {\n");
            saida += ") {\n";
            visitComandos(ctx.idComandos);
            //System.out.print("\n}\n");
            saida += "\n}\n";
            if (ctx.senao_opcional() != null && ctx.senao_opcional().getText().isEmpty() == false) {
                //System.out.print("else {\n");
                saida += "else {\n";
                visitSenao_opcional(ctx.senao_opcional());
                //System.out.print("\n}\n");
                saida += "\n}\n";
            }
        }

        // ATRIBUICAO
        if (ctx.cmdAtribuicaoIdent != null && ctx.cmdAtribuicaoIdent.getText().isEmpty() == false) {

            imprimindo = true;

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
                    //System.out.print(ctx.IDENT().getText());
                    saida += ctx.IDENT().getText();
                    //System.out.print("(");
                    saida += "(";
                    visitArgumentos_opcional(ctx.chamada_atribuicao().argumentos_opcional());
                    //System.out.print(")");
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
                        //System.out.print("strcpy(" + imprimirExpressao + ",");
                        saida += "strcpy(" + imprimirExpressao + ",";
                        if (ctx.chamada_atribuicao().expressao() != null && ctx.chamada_atribuicao().expressao().getText().isEmpty() == false) {
                            visitExpressao(ctx.chamada_atribuicao().expressao());
                        }
                        //System.out.print(")");
                        saida += ")";
                    } else {
                        //System.out.print(imprimirExpressao + " = ");
                        saida += imprimirExpressao + " = ";
                        if (ctx.chamada_atribuicao().expressao() != null && ctx.chamada_atribuicao().expressao().getText().isEmpty() == false) {
                            visitExpressao(ctx.chamada_atribuicao().expressao());
                        }
                    }
                }
            }

            imprimindo = false;

            //System.out.print(";\n");
            saida += ";\n";
        }

        // SWITCH CASE
        if (ctx.idCaso != null && ctx.idCaso.getText().isEmpty() == false) {
            //System.out.print("switch(");
            saida += "switch(";
            visitExp_aritmetica(ctx.idCaso);
            //System.out.print("){\n");
            saida += "){\n";

            if (ctx.selecao() != null && ctx.selecao().getText().isEmpty() == false) {
                visitSelecao(ctx.selecao());
            }

            if (ctx.idDefault != null && ctx.idDefault.getText().isEmpty() == false) {
                //System.out.print("default:\n");
                saida += "default:\n";
                visitComandos(ctx.idDefault.comandos());
            }

            //System.out.print("}\n");
            saida += "}\n";
        }

        //FOR
        if (ctx.idFor != null && ctx.idFor.getText().isEmpty() == false) {
            //System.out.print("for(");
            saida += "for(";
            //System.out.print(ctx.idFor.getText());
            saida += ctx.idFor.getText();
            //System.out.print(" = ");
            saida += " = ";
            visitExp_aritmetica(ctx.idExp1);
            //System.out.print("; ");
            saida += "; ";
            //System.out.print(ctx.idFor.getText());
            saida += ctx.idFor.getText();
            //System.out.print(" <= ");
            saida += " <= ";
            visitExp_aritmetica(ctx.idExp2);
            //System.out.print("; ");
            saida += "; ";
            //System.out.print(ctx.idFor.getText() + "++){\n");
            saida += ctx.idFor.getText() + "++){\n";
            visitComandos(ctx.comandos());
            //System.out.print("}\n");
            saida += "}\n";

        }

        //WHILE
        if (ctx.idWhile != null && ctx.idWhile.getText().isEmpty() == false) {
            //System.out.print("while(");
            saida += "while(";
            //System.out.print(ctx.idWhile.getText());
            saida += ctx.idWhile.getText();
            //System.out.print("){\n");
            saida += "){\n";
            visitComandos(ctx.comandos());
            //System.out.print("}\n");
            saida += "}\n";
        }

        //DO WHILE
        if (ctx.idDoWhile != null && ctx.idDoWhile.getText().isEmpty() == false) {
            //System.out.print("do{\n");
            saida += "do{\n";
            visitComandos(ctx.comandos());
            //System.out.print("}");
            saida += "}";
            //System.out.print("while(");
            saida += "while(";
            visitExpressao(ctx.expressao());
            //System.out.print(");\n");
            saida += ");\n";
        }

        //ATRIBUIÇÂO DE PONTEIRO
        if (ctx.cmdAtribPonteiroIdent != null && ctx.cmdAtribPonteiroIdent.getText().isEmpty() == false) {
            //System.out.print("*");
            saida += "*";
            //System.out.print(ctx.cmdAtribPonteiroIdent.getText());
            saida += ctx.cmdAtribPonteiroIdent.getText();
            if (ctx.outros_ident() != null && ctx.outros_ident().getText().isEmpty() == false) {
                visitOutros_ident(ctx.outros_ident());
            }

            if (ctx.dimensao() != null && ctx.dimensao().getText().isEmpty() == false) {
                visitDimensao(ctx.dimensao());
            }

            //System.out.print("=");
            saida += "=";
            visitExpressao(ctx.expressao());
            //System.out.print(";\n");
            saida += ";\n";
        }

        //  RETORNO
        if (ctx.cmdReturn != null && ctx.cmdReturn.getText().isEmpty() == false) {
            //System.out.print("return ");
            saida += "return ";
            visitExpressao(ctx.expressao());
            //System.out.println();
            saida += "\n";
        }

        return null;
    }

    @Override
    public Void visitSelecao(LAParser.SelecaoContext ctx) {
        visitConstantes(ctx.constantes());
        visitComandos(ctx.comandos());
        //System.out.print("break;\n");
        saida += "break;\n";
        if (ctx.mais_selecao() != null && ctx.mais_selecao().getText().isEmpty() == false) {
            visitSelecao(ctx.mais_selecao().selecao());
        }

        return null;
    }

    @Override
    public Void visitConstantes(LAParser.ConstantesContext ctx) {

        if (ctx.numero_intervalo().intervalo_opcional().NUM_INT() == null) {
            int value = Integer.parseInt(ctx.numero_intervalo().NUM_INT().toString());
            //System.out.print("case " + value + ":\n ");
            saida += "case " + value + ":\n ";
        } else {
            int firstValue = Integer.parseInt(ctx.numero_intervalo().NUM_INT().toString());
            int lastValue = Integer.parseInt(ctx.numero_intervalo().intervalo_opcional().NUM_INT().toString());

            for (int i = firstValue; i <= lastValue; i++) {
                //System.out.print("case " + i + ":\n");   
                saida += "case " + i + ":\n";
            }
        }
        return null;
    }

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

    @Override
    public Void visitFator_logico(LAParser.Fator_logicoContext ctx) {
        if (ctx.op_nao() != null && ctx.op_nao().getText().isEmpty() == false) {
            //System.out.print("!");
            saida += "!";
        }
        if (ctx.parcela_logica() != null && ctx.parcela_logica().getText().isEmpty() == false) {
            visitParcela_logica(ctx.parcela_logica());
        }
        return null;
    }

    @Override
    public Void visitParcela_logica(LAParser.Parcela_logicaContext ctx) {

        if (ctx.plTRUE != null) {
            //System.out.print("true");
            saida += "true";
        } else if (ctx.plFALSE != null) {
            //System.out.print("false");
            saida += "false";
        } else {
            visitExp_relacional(ctx.exp_relacional());
        }
        return null;
    }

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

    @Override
    public Void visitOutros_termos_logicos(LAParser.Outros_termos_logicosContext ctx) {
        if (ctx.termo_logico() != null && ctx.termo_logico().getText().isEmpty() == false) {
            //System.out.print(" || ");
            saida += " || ";
            visitTermo_logico(ctx.termo_logico());
        }
        if (ctx.outros_termos_logicos() != null && ctx.outros_termos_logicos().getText().isEmpty() == false) {
            visitOutros_termos_logicos(ctx.outros_termos_logicos());
        }
        return null;
    }

    @Override
    public Void visitOutros_fatores_logicos(LAParser.Outros_fatores_logicosContext ctx) {
        if (ctx.fator_logico() != null && ctx.fator_logico().getText().isEmpty() == false) {
            //System.out.print(" && ");
            saida += " && ";
            visitFator_logico(ctx.fator_logico());
        }
        if (ctx.outros_fatores_logicos() != null && ctx.outros_fatores_logicos().getText().isEmpty() == false) {
            visitOutros_fatores_logicos(ctx.outros_fatores_logicos());
        }
        return null;
    }

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
                //System.out.print(ctx.IDENT().getText());
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

    @Override
    public Void visitOutros_ident(LAParser.Outros_identContext ctx) {
        if (ctx.identificador() != null && ctx.identificador().getText().isEmpty() == false) {
            if (imprimindo) {
                imprimirVariavel += ".";
                imprimirExpressao += ".";
            } else {
                //System.out.print(".");
                saida += ".";
            }
            visitIdentificador(ctx.identificador());
        }
        return null;
    }

    @Override
    public Void visitPonteiros_opcionais(LAParser.Ponteiros_opcionaisContext ctx) {
        if (imprimindo) {
            //System.out.print("*");
            saida += "*";
        }
        if (ctx.ponteiros_opcionais() != null && ctx.ponteiros_opcionais().getText().isEmpty() == false) {
            visitPonteiros_opcionais(ctx.ponteiros_opcionais());
        }

        return null;
    }

    @Override
    public Void visitDecl_local_global(LAParser.Decl_local_globalContext ctx) {
        if (ctx.declaracao_local() != null && ctx.declaracao_local().getText().isEmpty() == false) {
            visitDeclaracao_local(ctx.declaracao_local());
        } else {
            visitDeclaracao_global(ctx.declaracao_global());
        }
        return null;
    }

    @Override
    public Void visitDeclaracao_local(LAParser.Declaracao_localContext ctx) {
        if (ctx.variavel() != null && ctx.variavel().getText().isEmpty() == false) {
            visitVariavel(ctx.variavel());
        } else if (ctx.dclLocalConst != null && ctx.dclLocalConst.getText().isEmpty() == false) {
            //System.out.print("#define ");
            saida += "#define ";
            //System.out.print(ctx.IDENT().getText() + " ");
            saida += ctx.IDENT().getText() + " ";
            if (ctx.valor_constante().idVdd != null) {
                //System.out.print("true");
                saida += "true";
            } else if (ctx.valor_constante().idFake != null) {
                //System.out.print("false");
                saida += "false";
            } else {
                //System.out.print(ctx.valor_constante().getText());
                saida += ctx.valor_constante().getText();
            }
            //System.out.print("\n");
            saida += "\n";
        }

        if (ctx.dclLocalTipo != null && ctx.dclLocalTipo.getText().isEmpty() == false) {
            //System.out.print("typedef ");
            saida += "typedef ";
            String ident = ctx.IDENT().getText();

            if (ctx.tipo().registro() != null && ctx.tipo().registro().getText().isEmpty() == false) {
                registrando = true;
                cadastrandoRegistro = ident;
                visitRegistro(ctx.tipo().registro());
                //System.out.print(" " + ident + ";\n");
                saida += " " + ident + ";\n";
                registrando = false;
            } else {
                visitTipo(ctx.tipo());
                //System.out.print(ctx.IDENT().getText() + " ");
                saida += ctx.IDENT().getText() + " ";
                //System.out.print(";\n");
                saida += ";\n";
            }

            dictionaryTipos.put(ident, ident);
        }

        return null;
    }

    @Override
    public Void visitDeclaracoes_locais(LAParser.Declaracoes_locaisContext ctx) {
        visitDeclaracao_local(ctx.declaracao_local());
        if (ctx.declaracoes_locais() != null && ctx.declaracoes_locais().getText().isEmpty() == false) {
            visitDeclaracoes_locais(ctx.declaracoes_locais());
        }
        return null;
    }

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

            //System.out.print(dictionaryTipos.get(tipoAtual) + nPointers + " " + ident);
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
                //System.out.print("[80]");
                saida += "[80]";
            }

            //System.out.print(";\n");
            saida += ";\n";
        } else {
            registrando = true;
            cadastrandoRegistro = ident;
            visitRegistro(ctx.tipo().registro());
            //System.out.print(" " + ident + ";\n");
            saida += " " + ident + ";\n";
            registrando = false;
        }
        return null;
    }

    @Override
    public Void visitMais_var(LAParser.Mais_varContext ctx) {
        //System.out.print(", ");
        saida += ", ";
        String ident = ctx.IDENT().getText();
        //System.out.print(ident);
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

    @Override
    public Void visitDimensao(LAParser.DimensaoContext ctx) {

        if (imprimindo) {
            imprimirExpressao += "[";
        } else {
            //System.out.print("[");
            saida += "[";
        }

        visitExp_aritmetica(ctx.exp_aritmetica());

        if (imprimindo) {
            imprimirExpressao += "]";
        } else {
            //System.out.print("]");
            saida += "]";
        }

        if (ctx.dimensao() != null && ctx.dimensao().getText().isEmpty() == false) {
            visitDimensao(ctx.dimensao());
        }
        return null;
    }

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

    @Override
    public Void visitOutros_fatores(LAParser.Outros_fatoresContext ctx) {
        if (ctx.op_multiplicacao() != null && ctx.op_multiplicacao().getText().isEmpty() == false) {
            //System.out.print(ctx.op_multiplicacao().getText());
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

    @Override
    public Void visitFator(LAParser.FatorContext ctx) {
        if (ctx.parcela() != null && ctx.parcela().getText().isEmpty() == false) {
            visitParcela(ctx.parcela());
        } else if (ctx.outras_parcelas() != null && ctx.outras_parcelas().getText().isEmpty() == false) {
            visitOutras_parcelas(ctx.outras_parcelas());
        }
        return null;
    }

    @Override
    public Void visitOutros_termos(LAParser.Outros_termosContext ctx) {
        if (ctx.op_adicao() != null && ctx.op_adicao().getText().isEmpty() == false) {
            if (imprimindo) {
                imprimirExpressao += ctx.op_adicao().getText();
            } else {
                //System.out.print(ctx.op_adicao().getText());
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

    @Override
    public Void visitParcela_unario(LAParser.Parcela_unarioContext ctx) {
        if (ctx.puNomeIdent1 != null) {
            if (imprimindo) {
                imprimirExpressao += "*" + ctx.IDENT().getText();
            } else {
                //System.out.print("*" + ctx.IDENT().getText());
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
                //System.out.print(ctx.IDENT().getText());
                saida += ctx.IDENT().getText();
            }
            visitChamada_partes(ctx.chamada_partes());
        } else if (ctx.NUM_INT() != null) {

            if (imprimindo) {
                imprimirExpressao += ctx.NUM_INT().getText();
            } else {
                //System.out.print(ctx.NUM_INT().getText());
                saida += ctx.NUM_INT().getText();
            }
        } else if (ctx.NUM_REAL() != null) {
            if (imprimindo) {
                imprimirExpressao += ctx.NUM_REAL().getText();
            } else {
                //System.out.print(ctx.NUM_REAL().getText());
                saida += ctx.NUM_REAL().getText();
            }
        } else {
            //System.out.print("(");
            saida += "(";
            if (ctx.expressao() != null && ctx.expressao().getText().isEmpty() == false) {
                visitExpressao(ctx.expressao());
            }
            //System.out.print(")");
            saida += ")";
        }
        return null;
    }

    @Override
    public Void visitChamada_partes(LAParser.Chamada_partesContext ctx) {
        if (ctx.idAbre != null && ctx.idFecha != null) {
            if (imprimindo) {
                imprimirExpressao += "(";
            } else //System.out.print("(");
            {
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
            } else //System.out.print(")");
            {
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

    @Override
    public Void visitParcela_nao_unario(LAParser.Parcela_nao_unarioContext ctx) {
        if (ctx.IDENT() != null) {
            //System.out.print("&" + ctx.IDENT().getText());
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
            } else //System.out.print(ctx.CADEIA().getText());
            {
                saida += ctx.CADEIA().getText();
            }
        }
        return null;
    }

    @Override
    public Void visitOutras_parcelas(LAParser.Outras_parcelasContext ctx) {
        if (ctx.parcela() != null && ctx.parcela().getText().isEmpty() == false) {
            //System.out.print("%");
            saida += "%";
            visitParcela(ctx.parcela());
            if (ctx.outras_parcelas() != null && ctx.outras_parcelas().getText().isEmpty() == false) {
                visitOutras_parcelas(ctx.outras_parcelas());
            }
        }
        return null;
    }

    @Override
    public Void visitOp_relacional(LAParser.Op_relacionalContext ctx) {
        String op = ctx.getText();
        switch (op) {
            case "=":
                /*System.out.print("=="); break;*/ saida += "==";
                break;
            case "<>":
                /*System.out.print("!="); break;*/ saida += "!=";
                break;
            case ">=":
                /*System.out.print(">="); break;*/ saida += ">=";
                break;
            case "<=":
                /*System.out.print("<="); break;*/ saida += "<=";
                break;
            case ">":
                /*System.out.print(">"); break;*/ saida += ">";
                break;
            case "<":
                /*System.out.print("<"); break;*/ saida += "<";
                break;
        }
        return null;
    }

    @Override
    public Void visitRegistro(LAParser.RegistroContext ctx) {
        //System.out.print("struct {\n");
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
        //System.out.print("}");
        saida += "}";
        return null;
    }

    @Override
    public Void visitDeclaracao_global(LAParser.Declaracao_globalContext ctx) {
        if (ctx.dclGlobalProcedimento != null) {
            //System.out.print("void " + ctx.dclGlobalProcedimento.getText() + "(");
            saida += "void " + ctx.dclGlobalProcedimento.getText() + "(";
            if (ctx.parametros_opcional() != null && ctx.parametros_opcional().getText().isEmpty() == false) {
                visitParametros_opcional(ctx.parametros_opcional());
            }
            //System.out.print("){\n");
            saida += "){\n";
            if (ctx.declaracoes_locais() != null && ctx.declaracoes_locais().getText().isEmpty() == false) {
                visitDeclaracoes_locais(ctx.declaracoes_locais());
            }
            if (ctx.comandos() != null && ctx.comandos().getText().isEmpty() == false) {
                visitComandos(ctx.comandos());
            }
            //System.out.print("}\n");
            saida += "}\n";
        } else if (ctx.dclGlobalFuncao != null) {
            visitTipo_estendido(ctx.tipo_estendido());
            //System.out.print(ctx.dclGlobalFuncao.getText() + "(");
            saida += ctx.dclGlobalFuncao.getText() + "(";
            if (ctx.parametros_opcional() != null && ctx.parametros_opcional().getText().isEmpty() == false) {
                visitParametros_opcional(ctx.parametros_opcional());
            }
            //System.out.print("){\n");
            saida += "){\n";
            if (ctx.declaracoes_locais() != null && ctx.declaracoes_locais().getText().isEmpty() == false) {
                visitDeclaracoes_locais(ctx.declaracoes_locais());
            }
            if (ctx.comandos() != null && ctx.comandos().getText().isEmpty() == false) {
                visitComandos(ctx.comandos());
            }
            //System.out.print("}\n");
            saida += "}\n";

            /*Colocando a função na lista de variáveis*/
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
            /*Término da colocação da função na lista de variáveis*/

        }

        return null;
    }

    @Override
    public Void visitParametros_opcional(LAParser.Parametros_opcionalContext ctx) {
        if (ctx.parametro() != null && ctx.parametro().getText().isEmpty() == false) {
            visitParametro(ctx.parametro());
        }

        return null;
    }

    @Override
    public Void visitParametro(LAParser.ParametroContext ctx) {
        visitTipo_estendido(ctx.tipo_estendido());
        visitIdentificador(ctx.identificador());

        /*Colocando parâmetros na lista de variáveis*/
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
        /*Término da colocação de parametros na lista de variáveis*/

        LAParser.Mais_identContext pointer = ctx.mais_ident();

        while (pointer != null && pointer.getText().isEmpty() == false) {
            //System.out.print(", ");
            saida += ", ";
            visitTipo_estendido(ctx.tipo_estendido());
            visitIdentificador(pointer.identificador());

            /*Colocando parâmetros na lista de variáveis*/
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
            /*Término da colocação de parametros na lista de variáveis*/

            if (pointer.mais_ident() != null) {
                pointer = pointer.mais_ident();
            }
        }

        if (ctx.mais_parametros() != null && ctx.mais_parametros().getText().isEmpty() == false) {
            //System.out.print(", ");
            saida += ", ";
            visitParametro(ctx.mais_parametros().parametro());
        }

        return null;
    }

    @Override
    public Void visitTipo_estendido(LAParser.Tipo_estendidoContext ctx) {
        if (ctx.ponteiros_opcionais() != null && ctx.ponteiros_opcionais().getText().isEmpty() == false) {
            visitPonteiros_opcionais(ctx.ponteiros_opcionais());
        }
        visitTipo_basico_ident(ctx.tipo_basico_ident());

        return null;
    }

    @Override
    public Void visitTipo_basico_ident(LAParser.Tipo_basico_identContext ctx) {

        if (ctx.tipo_basico().getText().equals("literal")) {
            //System.out.print("char* ");
            saida += "char* ";
        } else if (ctx.tipo_basico().getText().equals("inteiro")) {
            System.out.print("int ");
            saida += "int ";
        } else if (ctx.tipo_basico().getText().equals("real")) {
            //System.out.print("float ");
            saida += "float ";
        } else if (ctx.tipo_basico().getText().equals("logico")) {
            //System.out.print("bool ");
            saida += "bool ";
        } else if (ctx.IDENT() != null && ctx.IDENT().getText().isEmpty() == false) {
            //System.out.print(ctx.IDENT().getText());
            saida += ctx.IDENT().getText();
        }
        return null;
    }

    @Override
    public Void visitChamada_atribuicao(LAParser.Chamada_atribuicaoContext ctx) {
        if (ctx.argumentos_opcional() != null && ctx.argumentos_opcional().getText().isEmpty() == false) {
            //System.out.print("(");
            saida += "(";
            visitArgumentos_opcional(ctx.argumentos_opcional());
            //System.out.print(")");
            saida += ")";

        }
        return null;
    }

}
