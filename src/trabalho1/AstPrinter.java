package trabalho1;

import trabalho1.parser.LAParser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.PrintWriter;
import java.io.IOException;

public class AstPrinter {

    private boolean ignoringWrappers = false;

    public void setIgnoringWrappers(boolean ignoringWrappers) {
        this.ignoringWrappers = ignoringWrappers;
    }

    public void print(RuleContext ctx, String out)  throws IOException{

        PrintWriter writer = new PrintWriter(out, "UTF-8");
        explore(ctx, 0, writer);
        writer.close();
    }

    private void explore(RuleContext ctx, int indentation,  PrintWriter out) {
        boolean toBeIgnored = ignoringWrappers
                && ctx.getChildCount() == 1
                && ctx.getChild(0) instanceof ParserRuleContext;
        if (!toBeIgnored) {
            String ruleName = LAParser.ruleNames[ctx.getRuleIndex()];
            for (int i = 0; i < indentation; i++) {
                out.print("  ");
            }
            out.println(ruleName);
        }
        for (int i=0;i<ctx.getChildCount();i++) {
            ParseTree element = ctx.getChild(i);
            if (element instanceof RuleContext) {
                explore((RuleContext)element, indentation + (toBeIgnored ? 0 : 1), out);
            }
        }
    }

}
