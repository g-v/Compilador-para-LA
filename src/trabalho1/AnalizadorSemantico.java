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

    TabelaDeSimbolos_VAR tdsVAR;
    TabelaDeSimbolos_TIPOS tdsTIPOS;
    TabelaDeSimbolos_STRCT tdsSTRCT;
    
    @Override
    public Void visitDeclaracao_local(LAParser.Declaracao_localContext ctx) {
        if(ctx.variavel() != null)
        {
            visitVariavel(ctx.variavel());
        }
        else if(ctx.dclLocalConst != null)
        {
            EntradaTS_TIPO tipo = tdsTIPOS.verificar(ctx.tipo_basico().getText());
            if(tipo == null)
                //erro
            
            if(tdsVAR.verificar(ctx.IDENT().getText()) == null)
                //erro
                
            tdsVAR.inserir(ctx.IDENT().getText(), tipo.valor, 1);
        }
        else if(ctx.dclLocalTipo != null)
        {
            if(ctx.tipo().registro() != null)
            {
                String nome_STRCT = ctx.dclLocalTipo.getText();
                tdsSTRCT.inserir(nome_STRCT);
                LAParser.RegistroContext reg = ctx.tipo().registro();
                
                String tipoVar = reg.variavel().tipo().tipo_estendido().tipo_basico_ident().getText();
                EntradaTS_TIPO etds = tdsTIPOS.verificar(tipoVar);
                if(etds == null)
                {
                    //erro
                }
                   
                String nomeVar = reg.variavel().IDENT().getText();
                
                if(tdsSTRCT.verificarVarEmSTRCT(nome_STRCT, nomeVar) == null)
                    //erro
                    
                tdsSTRCT.inserirVarEmSTRCT(nome_STRCT, nomeVar, 
                        etds.valor, Integer.parseInt(reg.variavel().dimensao().getText()));
                
            }
            else if(ctx.tipo().tipo_estendido() != null)
            {
                
            }
        }
        
        return Void;
    }

    
    
}
