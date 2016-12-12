// Generated from /home/akari/natasha/Compilador-para-LA/src/main/antlr/LA.g4 by ANTLR 4.2.2
package trabalho1.parser;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link LAParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface LAVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link LAParser#tipo}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTipo(@NotNull LAParser.TipoContext ctx);

	/**
	 * Visit a parse tree produced by {@link LAParser#var_opcional}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVar_opcional(@NotNull LAParser.Var_opcionalContext ctx);

	/**
	 * Visit a parse tree produced by {@link LAParser#fator_logico}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFator_logico(@NotNull LAParser.Fator_logicoContext ctx);

	/**
	 * Visit a parse tree produced by {@link LAParser#parcela_logica}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParcela_logica(@NotNull LAParser.Parcela_logicaContext ctx);

	/**
	 * Visit a parse tree produced by {@link LAParser#declaracoes_locais}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDeclaracoes_locais(@NotNull LAParser.Declaracoes_locaisContext ctx);

	/**
	 * Visit a parse tree produced by {@link LAParser#programa}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrograma(@NotNull LAParser.ProgramaContext ctx);

	/**
	 * Visit a parse tree produced by {@link LAParser#expressao}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpressao(@NotNull LAParser.ExpressaoContext ctx);

	/**
	 * Visit a parse tree produced by {@link LAParser#op_multiplicacao}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOp_multiplicacao(@NotNull LAParser.Op_multiplicacaoContext ctx);

	/**
	 * Visit a parse tree produced by {@link LAParser#declaracoes}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDeclaracoes(@NotNull LAParser.DeclaracoesContext ctx);

	/**
	 * Visit a parse tree produced by {@link LAParser#ponteiros_opcionais}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPonteiros_opcionais(@NotNull LAParser.Ponteiros_opcionaisContext ctx);

	/**
	 * Visit a parse tree produced by {@link LAParser#termo}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTermo(@NotNull LAParser.TermoContext ctx);

	/**
	 * Visit a parse tree produced by {@link LAParser#outros_fatores}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOutros_fatores(@NotNull LAParser.Outros_fatoresContext ctx);

	/**
	 * Visit a parse tree produced by {@link LAParser#tipo_basico_ident}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTipo_basico_ident(@NotNull LAParser.Tipo_basico_identContext ctx);

	/**
	 * Visit a parse tree produced by {@link LAParser#op_opcional}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOp_opcional(@NotNull LAParser.Op_opcionalContext ctx);

	/**
	 * Visit a parse tree produced by {@link LAParser#chamada_partes}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitChamada_partes(@NotNull LAParser.Chamada_partesContext ctx);

	/**
	 * Visit a parse tree produced by {@link LAParser#mais_ident}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMais_ident(@NotNull LAParser.Mais_identContext ctx);

	/**
	 * Visit a parse tree produced by {@link LAParser#corpo}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCorpo(@NotNull LAParser.CorpoContext ctx);

	/**
	 * Visit a parse tree produced by {@link LAParser#declaracao_local}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDeclaracao_local(@NotNull LAParser.Declaracao_localContext ctx);

	/**
	 * Visit a parse tree produced by {@link LAParser#exp_relacional}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExp_relacional(@NotNull LAParser.Exp_relacionalContext ctx);

	/**
	 * Visit a parse tree produced by {@link LAParser#senao_opcional}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSenao_opcional(@NotNull LAParser.Senao_opcionalContext ctx);

	/**
	 * Visit a parse tree produced by {@link LAParser#termo_logico}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTermo_logico(@NotNull LAParser.Termo_logicoContext ctx);

	/**
	 * Visit a parse tree produced by {@link LAParser#dimensao}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDimensao(@NotNull LAParser.DimensaoContext ctx);

	/**
	 * Visit a parse tree produced by {@link LAParser#mais_selecao}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMais_selecao(@NotNull LAParser.Mais_selecaoContext ctx);

	/**
	 * Visit a parse tree produced by {@link LAParser#variavel}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariavel(@NotNull LAParser.VariavelContext ctx);

	/**
	 * Visit a parse tree produced by {@link LAParser#cmd}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCmd(@NotNull LAParser.CmdContext ctx);

	/**
	 * Visit a parse tree produced by {@link LAParser#declaracao_global}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDeclaracao_global(@NotNull LAParser.Declaracao_globalContext ctx);

	/**
	 * Visit a parse tree produced by {@link LAParser#parcela_nao_unario}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParcela_nao_unario(@NotNull LAParser.Parcela_nao_unarioContext ctx);

	/**
	 * Visit a parse tree produced by {@link LAParser#selecao}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelecao(@NotNull LAParser.SelecaoContext ctx);

	/**
	 * Visit a parse tree produced by {@link LAParser#tipo_estendido}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTipo_estendido(@NotNull LAParser.Tipo_estendidoContext ctx);

	/**
	 * Visit a parse tree produced by {@link LAParser#parametro}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParametro(@NotNull LAParser.ParametroContext ctx);

	/**
	 * Visit a parse tree produced by {@link LAParser#op_unario}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOp_unario(@NotNull LAParser.Op_unarioContext ctx);

	/**
	 * Visit a parse tree produced by {@link LAParser#mais_parametros}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMais_parametros(@NotNull LAParser.Mais_parametrosContext ctx);

	/**
	 * Visit a parse tree produced by {@link LAParser#parcela}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParcela(@NotNull LAParser.ParcelaContext ctx);

	/**
	 * Visit a parse tree produced by {@link LAParser#op_adicao}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOp_adicao(@NotNull LAParser.Op_adicaoContext ctx);

	/**
	 * Visit a parse tree produced by {@link LAParser#intervalo_opcional}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIntervalo_opcional(@NotNull LAParser.Intervalo_opcionalContext ctx);

	/**
	 * Visit a parse tree produced by {@link LAParser#decl_local_global}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDecl_local_global(@NotNull LAParser.Decl_local_globalContext ctx);

	/**
	 * Visit a parse tree produced by {@link LAParser#outros_ident}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOutros_ident(@NotNull LAParser.Outros_identContext ctx);

	/**
	 * Visit a parse tree produced by {@link LAParser#mais_variaveis}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMais_variaveis(@NotNull LAParser.Mais_variaveisContext ctx);

	/**
	 * Visit a parse tree produced by {@link LAParser#outros_fatores_logicos}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOutros_fatores_logicos(@NotNull LAParser.Outros_fatores_logicosContext ctx);

	/**
	 * Visit a parse tree produced by {@link LAParser#mais_expressao}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMais_expressao(@NotNull LAParser.Mais_expressaoContext ctx);

	/**
	 * Visit a parse tree produced by {@link LAParser#valor_constante}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitValor_constante(@NotNull LAParser.Valor_constanteContext ctx);

	/**
	 * Visit a parse tree produced by {@link LAParser#parcela_unario}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParcela_unario(@NotNull LAParser.Parcela_unarioContext ctx);

	/**
	 * Visit a parse tree produced by {@link LAParser#parametros_opcional}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParametros_opcional(@NotNull LAParser.Parametros_opcionalContext ctx);

	/**
	 * Visit a parse tree produced by {@link LAParser#outros_termos_logicos}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOutros_termos_logicos(@NotNull LAParser.Outros_termos_logicosContext ctx);

	/**
	 * Visit a parse tree produced by {@link LAParser#tipo_basico}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTipo_basico(@NotNull LAParser.Tipo_basicoContext ctx);

	/**
	 * Visit a parse tree produced by {@link LAParser#comandos}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComandos(@NotNull LAParser.ComandosContext ctx);

	/**
	 * Visit a parse tree produced by {@link LAParser#outros_termos}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOutros_termos(@NotNull LAParser.Outros_termosContext ctx);

	/**
	 * Visit a parse tree produced by {@link LAParser#exp_aritmetica}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExp_aritmetica(@NotNull LAParser.Exp_aritmeticaContext ctx);

	/**
	 * Visit a parse tree produced by {@link LAParser#mais_var}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMais_var(@NotNull LAParser.Mais_varContext ctx);

	/**
	 * Visit a parse tree produced by {@link LAParser#registro}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRegistro(@NotNull LAParser.RegistroContext ctx);

	/**
	 * Visit a parse tree produced by {@link LAParser#constantes}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConstantes(@NotNull LAParser.ConstantesContext ctx);

	/**
	 * Visit a parse tree produced by {@link LAParser#mais_constantes}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMais_constantes(@NotNull LAParser.Mais_constantesContext ctx);

	/**
	 * Visit a parse tree produced by {@link LAParser#outras_parcelas}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOutras_parcelas(@NotNull LAParser.Outras_parcelasContext ctx);

	/**
	 * Visit a parse tree produced by {@link LAParser#fator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFator(@NotNull LAParser.FatorContext ctx);

	/**
	 * Visit a parse tree produced by {@link LAParser#op_relacional}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOp_relacional(@NotNull LAParser.Op_relacionalContext ctx);

	/**
	 * Visit a parse tree produced by {@link LAParser#argumentos_opcional}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArgumentos_opcional(@NotNull LAParser.Argumentos_opcionalContext ctx);

	/**
	 * Visit a parse tree produced by {@link LAParser#chamada_atribuicao}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitChamada_atribuicao(@NotNull LAParser.Chamada_atribuicaoContext ctx);

	/**
	 * Visit a parse tree produced by {@link LAParser#identificador}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIdentificador(@NotNull LAParser.IdentificadorContext ctx);

	/**
	 * Visit a parse tree produced by {@link LAParser#numero_intervalo}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNumero_intervalo(@NotNull LAParser.Numero_intervaloContext ctx);

	/**
	 * Visit a parse tree produced by {@link LAParser#op_nao}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOp_nao(@NotNull LAParser.Op_naoContext ctx);
}