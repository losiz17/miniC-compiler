package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class Ident extends CParseRule {
    // addressToVlaue ::= primary
    private CToken ident;
    public Ident(CParseContext pcx) {
    }
    public static boolean isFirst(CToken tk) {
        return tk.getType()==CToken.TK_IDENT;
    }
    public void parse(CParseContext pcx) throws FatalErrorException {
        // ここにやってくるときは、必ずisFirst()が満たされている
        CTokenizer ct = pcx.getTokenizer();
        CToken tk = ct.getCurrentToken(pcx);
        ident = tk;		//ここで綴りを保存
        tk = ct.getNextToken(pcx);
    }

    public void semanticCheck(CParseContext pcx) throws FatalErrorException {

				this.setCType(CType.getCType(CType.T_int));
//				this.setCType(CType.getCType(CType.T_pint));
//				this.setCType(CType.getCType(CType.T_int_array));
//        this.setCType(CType.getCType(CType.T_pint_array));
        this.setConstant(false);
    }

    public void codeGen(CParseContext pcx) throws FatalErrorException {
        PrintStream o = pcx.getIOContext().getOutStream();
        o.println(";;; ident starts");
        if (ident != null) {
            o.println("\tMOV\t#" + ident.getText() + ", (R6)+\t; Ident: 変数アドレスを積む<" + ident.toExplainString() + ">");
        }
        o.println(";;; ident completes");
    }
}