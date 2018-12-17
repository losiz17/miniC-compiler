package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class Statement extends CParseRule {
    // statement ::= statementAssign | StatementIf | StatementWhile | StatementInput | StatementOutput
    private CParseRule factor;
    public Statement(CParseContext pcx) {
    }
    public static boolean isFirst(CToken tk) {
        return StatementAssign.isFirst(tk) | StatementIf.isFirst(tk) | StatementWhile.isFirst(tk) | StatementInput.isFirst(tk) | StatementOutput.isFirst(tk);
    }
    public void parse(CParseContext pcx) throws FatalErrorException {
        // ここにやってくるときは、必ずisFirst()が満たされている
        CTokenizer ct = pcx.getTokenizer();
        CToken tk = ct.getCurrentToken(pcx);
        // System.out.println(tk.getText()+":Statement");
        if(StatementAssign.isFirst(tk)){
            factor = new StatementAssign(pcx);
            factor.parse(pcx);
        }else if(StatementIf.isFirst(tk)){
            factor = new StatementIf(pcx);
            factor.parse(pcx);
        }else if(StatementWhile.isFirst(tk)){
            factor = new StatementWhile(pcx);
            factor.parse(pcx);
        }else if(StatementInput.isFirst(tk)){
            factor = new StatementInput(pcx);
            factor.parse(pcx);
        }else if(StatementOutput.isFirst(tk)){
            factor = new StatementOutput(pcx);
            factor.parse(pcx);
        }
    }

    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        if (factor != null) {
            factor.semanticCheck(pcx);
            setCType(factor.getCType());		// factor の型をそのままコピー
            setConstant(factor.isConstant());	// factor は常に定数
        }
    }

    public void codeGen(CParseContext pcx) throws FatalErrorException {
        PrintStream o = pcx.getIOContext().getOutStream();
        o.println(";;; Statement starts");
        if (factor != null) { factor.codeGen(pcx); }
        o.println(";;; Statement completes");
    }
}