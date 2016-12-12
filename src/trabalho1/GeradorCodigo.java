/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trabalho1;

import java.util.HashMap;
import java.util.Map;
import trabalho1.parser.LABaseVisitor;
import trabalho1.parser.LAParser;

/**
 *
 * @author guerra
 */
public class GeradorCodigo extends LABaseVisitor<Void>{
    
    Map<String, String> dictionaryTipos = new HashMap<String, String>();
    Map<String, String> variaveisTipos = new HashMap<String, String>();
    Map<String, String> variaveisScanf = new HashMap<String, String>();
    String tipoAtual = "";
    String scanfVariables = "";
        

    public GeradorCodigo() {
        dictionaryTipos.put("real", "float");
        dictionaryTipos.put("inteiro", "int");
        dictionaryTipos.put("logico", "bool");
        dictionaryTipos.put("literal", "char*");
        
        
        variaveisScanf.put("float", "%f");
        variaveisScanf.put("int", "%d");
        variaveisScanf.put("bool", "%d");
        variaveisScanf.put("char*", "%s");
    }

    
    @Override
    public Void visitPrograma(LAParser.ProgramaContext ctx) {
        visitDeclaracoes(ctx.declaracoes());
        System.out.print("typedef int bool;\n");
        System.out.print("#define true 1\n");
        System.out.print("#define false 0\n\n\n");
        System.out.print("int main() {\n");
        visitCorpo(ctx.corpo());
        System.out.print("return 0;\n}\n");
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
        if (ctx.declaracoes_locais()!= null && ctx.declaracoes_locais().getText().isEmpty() == false) { 
            visitDeclaracoes_locais(ctx.declaracoes_locais());
        }
        if (ctx.comandos()!= null && ctx.comandos().getText().isEmpty() == false) { 
            visitComandos(ctx.comandos());
        }
        return null;
    }

    @Override
    public Void visitComandos(LAParser.ComandosContext ctx) {
        if (ctx.cmd()!= null && ctx.cmd().getText().isEmpty() == false) { 
            visitCmd(ctx.cmd());
        }
        if (ctx.comandos()!= null && ctx.comandos().getText().isEmpty() == false) { 
            visitComandos(ctx.comandos());
        }
        return null;
    }

    @Override
    public Void visitCmd(LAParser.CmdContext ctx) {
        if(ctx.identificador()!= null && ctx.identificador().getText().isEmpty() == false){
        
            String tipo = variaveisTipos.get(ctx.identificador().IDENT().getText());
            System.out.print("scanf(\"" + variaveisScanf.get(tipo) + "\", ");
            if(!tipo.equals("char*")) {
                System.out.print("&");
            }
            visitIdentificador(ctx.identificador());
            System.out.print(");\n");
            
            if(ctx.mais_ident()!= null && ctx.mais_ident().getText().isEmpty() == false){
                  

            }
        }
            
        return null;
    }

    @Override
    public Void visitMais_ident(LAParser.Mais_identContext ctx) {
        String tipo = variaveisTipos.get(ctx.identificador().IDENT().getText());
        System.out.print("scanf(\"" + variaveisScanf.get(tipo) + "\", ");
        if(!tipo.equals("char*")) {
            System.out.print("&");
        }
        visitIdentificador(ctx.identificador());
        System.out.print(")");
        return null;
    }
    
    

    @Override
    public Void visitIdentificador(LAParser.IdentificadorContext ctx) {
        if(ctx.ponteiros_opcionais()!= null && ctx.ponteiros_opcionais().getText().isEmpty() == false){
            visitPonteiros_opcionais(ctx.ponteiros_opcionais());
        }
        if(ctx.IDENT()!= null && ctx.IDENT().getText().isEmpty() == false){
            System.out.print(ctx.IDENT().getText());
        } 
        if(ctx.dimensao()!= null && ctx.dimensao().getText().isEmpty() == false){
            visitDimensao(ctx.dimensao());
        }
        if(ctx.outros_ident()!= null && ctx.outros_ident().getText().isEmpty() == false){
            visitOutros_ident(ctx.outros_ident());
        }
        return null;
    }

    @Override
    public Void visitOutros_ident(LAParser.Outros_identContext ctx) {
        if(ctx.identificador()!= null && ctx.identificador().getText().isEmpty() == false){
            System.out.print(".");
            visitIdentificador(ctx.identificador());
        }
        return null;
    }
    
    

    @Override
    public Void visitPonteiros_opcionais(LAParser.Ponteiros_opcionaisContext ctx) {
            System.out.print("*");
            if(ctx.ponteiros_opcionais()!= null && ctx.ponteiros_opcionais().getText().isEmpty() == false){
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
        }
        
        
        
        return null;
    }

    @Override
    public Void visitDeclaracoes_locais(LAParser.Declaracoes_locaisContext ctx) {
        visitDeclaracao_local(ctx.declaracao_local());
        if (ctx.declaracoes_locais()!= null && ctx.declaracoes_locais().getText().isEmpty() == false) { 
            visitDeclaracoes_locais(ctx.declaracoes_locais());
        }
        return null;                
    }
    
    

    @Override
    public Void visitVariavel(LAParser.VariavelContext ctx) {
        tipoAtual = ctx.tipo().getText();
        String ident = ctx.IDENT().getText();
        System.out.print(dictionaryTipos.get(tipoAtual) + " " + ident);
        variaveisTipos.put(ident, dictionaryTipos.get(tipoAtual));
        if (ctx.dimensao() != null && ctx.dimensao().getText().isEmpty() == false) {
            visitDimensao(ctx.dimensao());
        }
        if (ctx.mais_var() != null && ctx.mais_var().getText().isEmpty() == false) {
            visitMais_var(ctx.mais_var());
        }
        System.out.print(";\n");
        
        return null;
    }
    
      @Override
    public Void visitMais_var(LAParser.Mais_varContext ctx) {
        System.out.print(", ");
        String ident = ctx.IDENT().getText();
        System.out.print(ident);
        variaveisTipos.put(dictionaryTipos.get(tipoAtual), ident);
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
        if (ctx.dimensao() != null && ctx.dimensao().getText().isEmpty() == false) {
            System.out.print("[");

            visitExp_aritmetica(ctx.exp_aritmetica());

            System.out.print("]");

            if (ctx.dimensao() != null && ctx.dimensao().getText().isEmpty() == false) {
                visitDimensao(ctx.dimensao());
            }
        }
        return null;
    }

  
    
    @Override
    public Void visitExp_aritmetica(LAParser.Exp_aritmeticaContext ctx) {
        
        return null;
    }
    
    
    
    
}
