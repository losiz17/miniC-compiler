package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class Variable extends CParseRule {
    // Variable ::= ident [array]
    private CParseRule ident ,variable;
    public Variable(CParseContext pcx) {
    }
    public static boolean isFirst(CToken tk) {
        return Ident.isFirst(tk);
    }
    public void parse(CParseContext pcx) throws FatalErrorException {
        // ここにやってくるときは、必ずisFirst()が満たされている
        ident = new Ident(pcx);
        ident.parse(pcx);
        CTokenizer ct = pcx.getTokenizer();
        CToken tk = ct.getCurrentToken(pcx);
        if (Array.isFirst(tk)) {
            variable = new Array(pcx);
            variable.parse(pcx);
        }
    }

    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        this.setCType(CType.getCType(CType.T_pint));//ポインタ型
        this.setConstant(true);//常に定数
    }

    public void codeGen(CParseContext pcx) throws FatalErrorException {
        PrintStream o = pcx.getIOContext().getOutStream();
        o.println(";;; factoramp starts");
        if (variable != null) { variable.codeGen(pcx); }
        o.println(";;; factoramp completes");
    }
}