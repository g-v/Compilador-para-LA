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
        System.out.print("typedef int bool\n");
        System.out.print("#define true 1\n");
        System.out.print("#define false 0\n\n\n");
        System.out.print("int main() {\n");
        visitCorpo(ctx.corpo());
        System.out.print("  return 0;\n}\n");
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
        
        // LEIA
        if(ctx.idLeia != null && ctx.identificador().getText().isEmpty() == false){
        
            String tipo = variaveisTipos.get(ctx.identificador().IDENT().getText());
            System.out.print("scanf(\"" + variaveisScanf.get(tipo) + "\", ");
            if(!tipo.equals("char*")) {
                System.out.print("&");
            }
            visitIdentificador(ctx.identificador());
            System.out.print(");\n");
            
            LAParser.Mais_identContext pointer = ctx.mais_ident();
            
            while(pointer != null && pointer.getText().isEmpty() == false){
                tipo = variaveisTipos.get(pointer.identificador().IDENT().getText());
                System.out.print("scanf(\"" + variaveisScanf.get(tipo) + "\", ");
                if(!tipo.equals("char*")) {
                    System.out.print("&");
                }
                visitIdentificador(pointer.identificador());
                System.out.print(");\n");
                if(pointer.mais_ident() != null)
                    pointer = pointer.mais_ident();
            }
        }
        
        // ESCREVA
        if(ctx.idEscreva != null && ctx.idEscreva.getText().isEmpty() == false){
       
        }
            
        return null;
    }

    @Override
    public Void visitExpressao(LAParser.ExpressaoContext ctx) {
        if(ctx.termo_logico()!= null && ctx.termo_logico().getText().isEmpty() == false){
            visitTermo_logico(ctx.termo_logico());
        }
        if(ctx.outros_termos_logicos()!= null && ctx.outros_termos_logicos().getText().isEmpty() == false){
            visitOutros_termos_logicos(ctx.outros_termos_logicos());
        } 
        return null;
    }

    @Override
    public Void visitTermo_logico(LAParser.Termo_logicoContext ctx) {
        if(ctx.fator_logico()!= null && ctx.fator_logico().getText().isEmpty() == false){
            visitFator_logico(ctx.fator_logico());
        }
        if(ctx.outros_fatores_logicos() != null && ctx.outros_fatores_logicos().getText().isEmpty() == false){
            visitOutros_fatores_logicos(ctx.outros_fatores_logicos());
        } 
        return null;
    }

    @Override
    public Void visitFator_logico(LAParser.Fator_logicoContext ctx) {
        if(ctx.op_nao()!= null && ctx.op_nao().getText().isEmpty() == false){
            System.out.print("!");
        }
        if(ctx.parcela_logica() != null && ctx.parcela_logica().getText().isEmpty() == false){
            visitParcela_logica(ctx.parcela_logica());
        } 
        return null;
    }

    
    @Override
    public Void visitParcela_logica(LAParser.Parcela_logicaContext ctx) {
        
        if(ctx.plTRUE.getText().isEmpty() == false){
            System.out.print("true");
        }
        else if(ctx.plFALSE.getText().isEmpty() == false){
            System.out.print("false");
        }
        else{
            visitExp_relacional(ctx.exp_relacional());
        }
        return null;
    }

    @Override
    public Void visitExp_relacional(LAParser.Exp_relacionalContext ctx) {
        if(ctx.exp_aritmetica()!= null && ctx.exp_aritmetica().getText().isEmpty() == false){
            visitExp_aritmetica(ctx.exp_aritmetica());
        }
        if(ctx.op_opcional() != null && ctx.op_opcional().getText().isEmpty() == false){
            visitOp_opcional(ctx.op_opcional());
        } 
        return null;
    }
    
    
    
    
    
    

    @Override
    public Void visitOutros_termos_logicos(LAParser.Outros_termos_logicosContext ctx) {
        if(ctx.termo_logico()!= null && ctx.termo_logico().getText().isEmpty() == false){
            System.out.println("||");
            visitTermo_logico(ctx.termo_logico());
        }
        if(ctx.outros_termos_logicos()!= null && ctx.outros_termos_logicos().getText().isEmpty() == false){
            visitOutros_termos_logicos(ctx.outros_termos_logicos());
        } 
        return null;
    }

    @Override
    public Void visitOutros_fatores_logicos(LAParser.Outros_fatores_logicosContext ctx) {
         if(ctx.fator_logico()!= null && ctx.fator_logico().getText().isEmpty() == false){
            System.out.println("&&");
            visitFator_logico(ctx.fator_logico());
        }
        if(ctx.outros_fatores_logicos()!= null && ctx.outros_fatores_logicos().getText().isEmpty() == false){
            visitOutros_fatores_logicos(ctx.outros_fatores_logicos());
        } 
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
        if (ctx.fator()!= null && ctx.fator().getText().isEmpty() == false) {
            visitFator(ctx.fator());
        }
        if (ctx.outros_fatores()!= null && ctx.outros_fatores().getText().isEmpty() == false) {
            visitOutros_fatores(ctx.outros_fatores());
        }
        return null;
    }

    @Override
    public Void visitOutros_fatores(LAParser.Outros_fatoresContext ctx) {
        if (ctx.op_multiplicacao()!= null && ctx.op_multiplicacao().getText().isEmpty() == false) {
            System.out.print(ctx.op_multiplicacao().getText());
        }
        if (ctx.fator()!= null && ctx.fator().getText().isEmpty() == false) {
            visitFator(ctx.fator());
        }
        if (ctx.outros_fatores()!= null && ctx.outros_fatores().getText().isEmpty() == false) {
            visitOutros_fatores(ctx.outros_fatores());
        }
        return null;
    }

    @Override
    public Void visitFator(LAParser.FatorContext ctx) {
        if (ctx.parcela()!= null && ctx.parcela().getText().isEmpty() == false) {
            visitParcela(ctx.parcela());
        }
        else if (ctx.outras_parcelas()!= null && ctx.outras_parcelas().getText().isEmpty() == false) {
            visitOutras_parcelas(ctx.outras_parcelas());
        }
        return null;
    }
    
    @Override
    public Void visitOutros_termos(LAParser.Outros_termosContext ctx) {
        if (ctx.op_adicao()!= null && ctx.op_adicao().getText().isEmpty() == false) {
            System.out.print(ctx.op_adicao().getText());
        }
        if (ctx.termo()!= null && ctx.termo().getText().isEmpty() == false) {
            visitTermo(ctx.termo());
        }
        if (ctx.outros_termos()!= null && ctx.outros_termos().getText().isEmpty() == false) {
            visitOutros_termos(ctx.outros_termos());
        }
        return null;
    }

    @Override
    public Void visitOutras_parcelas(LAParser.Outras_parcelasContext ctx) {
        return super.visitOutras_parcelas(ctx); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Void visitParcela_unario(LAParser.Parcela_unarioContext ctx) {
        if(ctx.puNomeIdent1.getText().isEmpty() == false){
            System.out.print("*" + ctx.puNomeIdent1.getText());
            if(ctx.outros_ident()!= null && ctx.outros_ident().getText().isEmpty() == false){
                visitOutros_ident(ctx.outros_ident());
            }
            if(ctx.dimensao()!= null && ctx.dimensao().getText().isEmpty() == false){
                visitDimensao(ctx.dimensao());
            }
        }
        else if(ctx.puNomeIdent2.getText().isEmpty() == false){
            System.out.print(ctx.puNomeIdent2.getText());
            visitChamada_partes(ctx.chamada_partes());
        }
        else if(ctx.puNomeIdent2.getText().isEmpty() == false){
            System.out.print(ctx.puNomeIdent2.getText());
            visitChamada_partes(ctx.chamada_partes());
        }
        return null;
    }

    @Override
    public Void visitChamada_partes(LAParser.Chamada_partesContext ctx) {
        if(ctx.dimensao()!= null && ctx.dimensao().getText().isEmpty() == false){
                visitDimensao(ctx.dimensao());
        }
        
        System.out.print("(");
        
        System.out.print(")");
    }

    
    
    @Override
    public Void visitParcela(LAParser.ParcelaContext ctx) {
        if (ctx.op_unario()!= null && ctx.op_unario().getText().isEmpty() == false) {
            System.out.print(ctx.op_unario().getText());
        }
        if (ctx.parcela_unario()!= null && ctx.parcela_unario().getText().isEmpty() == false) {
            visitParcela_unario(ctx.parcela_unario());
        }
        if (ctx.parcela_nao_unario()!= null && ctx.parcela_nao_unario().getText().isEmpty() == false) {
            visitParcela_nao_unario(ctx.parcela_nao_unario());
        }
        return null;
    }

    @Override
    public Void visitParcela_nao_unario(LAParser.Parcela_nao_unarioContext ctx) {
        return super.visitParcela_nao_unario(ctx); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    

 
    
    
    
    
    
    
    
}
