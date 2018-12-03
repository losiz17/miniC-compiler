package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class Primary extends CParseRule {
    // primary ::= primaryMult | variable
    private CParseRule number;
    public Primary(CParseContext pcx) {
    }
    public static boolean isFirst(CToken tk) {
        return PrimaryMult.isFirst(tk) | Variable.isFirst(tk);
    }
    public void parse(CParseContext pcx) throws FatalErrorException {
        // ここにやってくるときは、必ずisFirst()が満たされている
        CTokenizer ct = pcx.getTokenizer();
        CToken tk = ct.getCurrentToken(pcx);
        if(PrimaryMult.isFirst(tk)){
            number = new PrimaryMult(pcx);
            number.parse(pcx);
        }
        else if(Variable.isFirst(tk)){
            number = new Variable(pcx);
            number.parse(pcx);
        }

    }

    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        if (number != null) {
            number.semanticCheck(pcx);
            setCType(number.getCType());		// number の型をそのままコピー
            setConstant(number.isConstant());	// number は常に定数
        }
    }

    public void codeGen(CParseContext pcx) throws FatalErrorException {
        PrintStream o = pcx.getIOContext().getOutStream();
        o.println(";;; factor starts");
        if (number != null) { number.codeGen(pcx); }
        o.println(";;; factor completes");
    }
}