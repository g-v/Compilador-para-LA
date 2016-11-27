package trabalho1;

import java.io.File;
import java.io.IOException;

public class Example {
    public static int modo = 0;
    public static void main(String[] args) throws IOException {
        String entrada = "";
        String saida = "";
        boolean simplifyTree = false;
        for (String s: args) {
            if (entrada.isEmpty())
            {
                entrada = s;
                System.out.println("Entrada: " + s);
            }else if(saida.isEmpty())
            {
                saida = s;
                System.out.println("Saida: " + s);
                if(saida.contains("saidaSintatico"))
                    modo = 1;
                else if(saida.contains("saidaSemanticoComErros"))
                    modo = 2;
                else if((saida.contains("saidaGeradorDeCodigo")))
                    modo = 3;
                else
                    modo = 4;
            }else
            {
                if(s.equals("0") || s.equals("false"))
                    simplifyTree = false;
                else if(s.equals("1") || s.equals("true"))
                    simplifyTree = true;
                
                break;
            }
        }
        
        if(entrada.isEmpty())
        {
            System.out.println("Usando entrada default - in.txt");
            entrada = "in.txt";
        }
        if(saida.isEmpty())
            saida = "out.txt";
        
        ParserFacade parserFacade = new ParserFacade();
        //AstPrinter astPrinter = new AstPrinter();
       // astPrinter.setIgnoringWrappers(simplifyTree);
        parserFacade.parse(new File(entrada), new File(saida));
        System.out.println("Fim da compilacao");
    }
}
