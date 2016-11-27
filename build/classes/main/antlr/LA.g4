grammar LA;

@members {
TabelaDeSimbolos ts = new TabelaDeSimbolos();
int endereco = 0;
int label = 0;
}

programa returns [ String C ] : declaracoes 'algoritmo' corpo 'fim_algoritmo'
{ $C = "int main() {" + '\n'
     + declaracoes.C + '\n'
     + corpo.C + '\n'
     + "return 0\n}\n"; }
 ;

declaracoes returns [ String C ] : decl_local_global declaracoes 
{ $C = decl_local_global.C
     + declaracoes.C; }
| /* epsilon */ { $C = ""; }
;

decl_local_global returns [ String C ] : declaracao_local 
{ $C = declaracao_local.C; }
| declaracao_global 
{ $C = declaracao_global.C; };

declaracao_local returns [ String C ] : 'declare' variavel 
{ $C = variavel.C; }
                   | 'constante' IDENT ':' tipo_basico  '=' valor_constante
{ $C = "const"+' '+tipo_basico.C+' '+IDENT.C+'='+valor_constante.C; }
                   | 'tipo' IDENT ':' tipo ;
{ $C = "typedef"+' '+tipo.C+' '+IDENT.C; }

variavel returns [ String C ] : IDENT dimensao mais_var ':' tipo 
{ $C = tipo.C+' '+IDENT.C+dimensao.C+mais_var.C; }
;

mais_var returns [ String C ] : ',' IDENT dimensao mais_var
{ $C = ', '+IDENT.C+dimensao.C+mais_var.C; }
 | /* epsilon */ 
{ $C = ""; }
;

identificador returns [ String C ] : ponteiros_opcionais IDENT dimensao outros_ident 
{ $C = ponteiros_opcionais.C+IDENT.C+dimensao.C+outros_ident.C; }
;

ponteiros_opcionais returns [ String C ] : '^' ponteiros_opcionais
{ $C = '&'+ponteiros_opcionais.C; }
 | /* epsilon */ 
{ $C = ""; }
;

outros_ident returns [ String C ] : '.' identificador
{ $C = '.'+identificador.C; }
 | /* epsilon */ 
{ $C = ""; }
;

dimensao returns [ String C ] : '[' exp_aritmetica ']' dimensao 
{ $C = '['+exp_aritmetica.C+']'+dimensao.C; }
| /* epsilon */ 
{ $C = ""; }
;

tipo returns [ String C ] : registro 
{ $C = registro.C; }
| tipo_estendido
{ $C = tipo_estendido.C; } 
;

mais_ident returns [ String C ] : ',' identificador mais_ident 
{ $C = ', '+identificador.C+mais_ident.C; }
| /* epsilon */ 
{ $C = ""; }
;

mais_variaveis returns [ String C ] : variavel mais_variaveis 
{ $C = ';\n'+variavel.C+mais_variaveis.C; }
| /* epsilon */ 
{ $C = ""; }
;

tipo_basico returns [ String C ] : 'literal' { $C = "literal"; }
| 'inteiro' { $C = "inteiro"; }
| 'real' { $C = "real"; }
| 'logico' { $C = "logico"; }
;

tipo_basico_ident returns [ String C ] : tipo_basico 
{ $C = tipo_basico.C; }
| IDENT 
{ $C = IDENT.C }
; 

tipo_estendido returns [ String C ] : ponteiros_opcionais tipo_basico_ident 
{ $C = ponteiros_opcionais.C+tipo_basico_ident.C; }
;

valor_constante	returns [ String C ] : CADEIA { $C = CADEIA.C; }
| NUM_INT { $C = NUM_INT.C; }
| NUM_REAL { $C = NUM_REAL.C; }
| 'verdadeiro' { $C = "verdadeiro"; }
| 'falso' ;{ $C = "falso"; }

registro returns [ String C ] : 'registro' variavel mais_variaveis 'fim_registro' 
{ $C = "struct{\n"
     + variavel.C
     + mais_variaveis.C
     + "\n}"; }
;

declaracao_global	returns [ String C ] : 'procedimento' IDENT '(' parametros_opcional ')' declaracoes_locais comandos 'fim_procedimento'
{ $C = "void"+' '+IDENT.C+'('+parametros_opcional.C+')'+"{\n"		     
     + declaracoes_locais
     + comandos
     + "\nreturn\n}"; }
			              | 'funcao' IDENT '(' parametros_opcional ')' ':' tipo_estendido declaracoes_locais comandos 'fim_funcao' 
{ $C = tipo_estendido.C+' '+IDENT.C+'('+parametros_opcional.C+')'+"{\n"		     
     + declaracoes_locais
     + comandos
     + "\n}"; }
;

parametros_opcional	returns [ String C ] : parametro 
{ $C = parametro.C; }
| /* epsilon */ 
{ $C = ""; }
;

parametro	returns [ String C ] : var_opcional identificador mais_ident ':' tipo_estendido mais_parametros 
{ $C = tipo_estendido.C+' '+identificador.C+mais_ident.t+mais_parametros.C; }
;

var_opcional returns [ String C ] : 'var' 
{ $C = "var"; }
| /* epsilon */ 
{ $C = ""; }
;

mais_parametros	returns [ String C ] : ',' parametro 
{ $C = ', '+parametro.C; }
| /* epsilon */ 
{ $C = ""; }
;

declaracoes_locais returns [ String C ] : declaracao_local declaracoes_locais 
{ $C = declaracao_local.C
     + declaracoes_locais.C; }
| /* epsilon */ 
{ $C = ""; }
;

corpo : returns [ String C ] : declaracoes_locais comandos 
{ $C = declaracoes_locais.C
     + comandos.C; }
;

comandos returns [ String C ] : cmd comandos 
{ $C = cmd.C
     + comandos.C; }
| /* epsilon */ 
{ $C = ""; }
;

cmd	returns [ String C ] : 'leia' '(' identificador mais_ident ')'
{ $C = "readf("+identificador.C+mais_ident.C+')'; }
			| 'escreva' '(' expressao mais_expressao ')'
{ $C = "printf("+expressao.C+mais_expressao.C+')'; }
			| 'se' expressao 'entao' comandos senao_opcional 'fim_se'
{ $C = "if ("+expressao.C+"){\n"
     + comandos.C+"\n}"
     + senao_opcional.C; }
		  | 'caso' exp_aritmetica 'seja' selecao senao_opcional 'fim_caso'

			| 'para' IDENT '<-' exp_aritmetica 'ate' exp_aritmetica 'faca' comandos 'fim_para'

			| 'enquanto' expressao 'faca' comandos 'fim_enquanto'

			| 'faca' comandos 'ate' expressao

			| '^' IDENT outros_ident dimensao '<-' expressao

			| IDENT chamada_atribuicao

			| 'retorne' expressao ;

mais_expressao returns [ String C ] : ',' expressao mais_expressao | /* epsilon */ ;

senao_opcional returns [ String C ] : 'senao' comandos | /* epsilon */ ;

chamada_atribuicao returns [ String C ] : '(' argumentos_opcional ')' | outros_ident dimensao '<-' expressao ;

argumentos_opcional returns [ String C ] : expressao mais_expressao | /* epsilon */ ;

selecao returns [ String C ] : constantes ':' comandos mais_selecao ;

mais_selecao returns [ String C ] : selecao | /* epsilon */ ;

constantes returns [ String C ] : numero_intervalo mais_constantes ;

mais_constantes returns [ String C ] : ',' constantes | /* epsilon */ ;

numero_intervalo returns [ String C ] : op_unario NUM_INT intervalo_opcional ;

intervalo_opcional returns [ String C ] : '..' op_unario NUM_INT | /* epsilon */ ;

op_unario returns [ String C ] : '-' | /* epsilon */ ;

exp_aritmetica returns [ String C ] : termo outros_termos ;

op_multiplicacao returns [ String C ] : '*' | '/' ;

op_adicao returns [ String C ] : '+' | '-' ;

termo returns [ String C ] : fator outros_fatores ;

outros_termos returns [ String C ] : op_adicao termo outros_termos | /* epsilon */ ;

fator returns [ String C ] : parcela outras_parcelas ;

outros_fatores returns [ String C ] : op_multiplicacao fator outros_fatores  | /* epsilon */ ;

parcela returns [ String C ] : op_unario parcela_unario | parcela_nao_unario ;

parcela_unario returns [ String C ] : '^' IDENT outros_ident dimensao | IDENT chamada_partes | NUM_INT | NUM_REAL | '(' expressao ')' ;

parcela_nao_unario returns [ String C ] : '&' IDENT outros_ident dimensao | CADEIA ;

outras_parcelas returns [ String C ] : '%' parcela outras_parcelas | /* epsilon */ ;

chamada_partes returns [ String C ] : '(' expressao mais_expressao ')' | outros_ident dimensao | /* epsilon */ ;

exp_relacional returns [ String C ] : exp_aritmetica op_opcional ;

op_opcional returns [ String C ] : op_relacional exp_aritmetica | /* epsilon */ ;

op_relacional returns [ String C ] : '='  | '<>' | '>=' | '<=' | '>' | '<' ;

expressao returns [ String C ] : termo_logico outros_termos_logicos ;

op_nao returns [ String C ] : 'nao' | /* epsilon */ ;

termo_logico returns [ String C ] : fator_logico outros_fatores_logicos ;

outros_termos_logicos returns [ String C ] : 'ou' termo_logico outros_termos_logicos | /* epsilon */ ;

outros_fatores_logicos returns [ String C ] : 'e' fator_logico outros_fatores_logicos | /* epsilon */ ;

fator_logico returns [ String C ] : op_nao parcela_logica ;

parcela_logica returns [ String C ] : 'verdadeiro' |  'falso' | exp_relacional ;


                  
				  
		  
IDENT returns [ String C ] :      ('a'..'z' | 'A'..'Z' | '_') ('a'..'z' | 'A'..'Z' | '0'..'9' | '_')*;
NUM_INT returns [ String C ] :    ('0'..'9')+;
NUM_REAL returns [ String C ] :   ('0'..'9')+ '.' ('0'..'9')+;
CADEIA returns [ String C ] :     '"' ~('\n' | '\r' | '"')* '"';
WS : 		 [ \n\t\r]+ -> skip;
COMMENT:  	 '{' .*? '}' -> skip;
