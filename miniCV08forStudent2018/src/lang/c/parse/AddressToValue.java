package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class AddressToValue extends CParseRule {
    // addressToVlaue ::= primary
    private CParseRule number;
    public AddressToValue(CParseContext pcx) {
    }
    public static boolean isFirst(CToken tk) {
        return Primary.isFirst(tk);
    }
    public void parse(CParseContext pcx) throws FatalErrorException {
        // ここにやってくるときは、必ずisFirst()が満たされている
        CTokenizer ct = pcx.getTokenizer();
        CToken tk = ct.getCurrentToken(pcx);
        number = new Primary(pcx);
        number.parse(pcx);
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
        o.println(";;; addressToValue starts");
        if (number != null) {
            number.codeGen(pcx);
            o.println("\tMOV\t -(R6),R0\t; adressToValue: アドレス先の値を取り出して、スタックに積む");
            o.println("\tMOV\t (R0),(R6)+\t; adressToValue: ");
        }
        o.println(";;; addressToValue completes");
    }
}