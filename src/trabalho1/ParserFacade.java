package trabalho1;

import trabalho1.parser.LALexer;
import trabalho1.parser.LAParser;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

public class ParserFacade {

    PrintWriter writer;

    public class SintaxErrorListener extends BaseErrorListener {

        @Override
        public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol,
                int line, int charPositionInLine,
                String msg, RecognitionException e) {
            String tokenName = ((org.antlr.v4.runtime.CommonToken) offendingSymbol).getText();

            if (tokenName.matches("(.)*EOF(.)*")) {
                tokenName = "EOF";
            }
            if (Example.modo == 1 || Example.modo == 4) {
                writer.println("Linha " + line + ": erro sintatico proximo a " + tokenName);
                writer.println("Fim da compilacao");
                writer.close();
                throw new RuntimeException(e);
            }
        }

    }

    public class LexerErrorListener extends BaseErrorListener {

        @Override
        public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol,
                int line, int charPositionInLine,
                String msg, RecognitionException e) {
            msg = msg.replaceAll("token recognition error at: ", "");
            msg = msg.replaceAll("'", "");
            if (msg.charAt(0) == '"') {
                msg = "";
                msg += '"' + " - simbolo nao identificado";
            } else if (msg.charAt(0) == '{') {
                msg = "comentario nao fechado";
                line += 1;
            } else {
                msg += " - simbolo nao identificado";
            }

            if (Example.modo == 1 || Example.modo == 4) {

                writer.println("Linha " + line + ": " + msg);
                System.err.println("Linha " + line + ": " + msg);
                System.err.println("Fim da compilacao");
                writer.println("Fim da compilacao");
                writer.close();
                throw new RuntimeException(e);
            }
        }

    }

    private static String readFile(File file, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(file.toPath());
        return new String(encoded, encoding);
    }

    public LAParser.ProgramaContext parse(File file, File saida) throws IOException {
        String code = readFile(file, Charset.forName("UTF-8"));
        LALexer lexer = new LALexer(new ANTLRInputStream(code));
        CommonTokenStream tokens = new CommonTokenStream(lexer);

        writer = new PrintWriter(saida, "UTF-8");
        LAParser parser = new LAParser(tokens);

        LexerErrorListener lexerListener = new LexerErrorListener();

        lexer.removeErrorListeners();
        lexer.addErrorListener(lexerListener);

        SintaxErrorListener sintaxListener = new SintaxErrorListener();

        parser.removeErrorListeners();
        parser.addErrorListener(sintaxListener);

        AnalisadorSemantico analisadorSemantico = new AnalisadorSemantico(saida);

        LAParser.ProgramaContext context = parser.programa();

        if (Example.modo == 2) {
            System.out.println("Iniciando analise semantica");
            analisadorSemantico.visit(context);
            analisadorSemantico.close();
        }

        GeradorCodigo geradorCodigo = new GeradorCodigo(saida);

        if (Example.modo == 3) {
            System.out.println("Iniciando geração de codigo");
            geradorCodigo.visit(context);
            geradorCodigo.closeCerto();
        }

        System.err.flush();

        writer.close();
        return context;
    }
}
