package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class UnsignedFactor extends CParseRule {
    // unsignedFactor ::= factorAmp | number | LPAR expression RPAP | addressToValue
    private CParseRule factor;
    public UnsignedFactor(CParseContext pcx) {
    }
    public static boolean isFirst(CToken tk) {
        return Number.isFirst(tk) | FactorAmp.isFirst(tk) | tk.getType() == CToken.TK_LPAR | AddressToValue.isFirst(tk);
    }
    public void parse(CParseContext pcx) throws FatalErrorException {
        // ここにやってくるときは、必ずisFirst()が満たされている
        CTokenizer ct = pcx.getTokenizer();
        CToken tk = ct.getCurrentToken(pcx);
        if(Number.isFirst(tk)){
            factor = new Number(pcx);
            factor.parse(pcx);
        }else if(FactorAmp.isFirst(tk)){
            factor = new FactorAmp(pcx);
            factor.parse(pcx);
        }else if(AddressToValue.isFirst(tk)){
            factor = new AddressToValue(pcx);
            factor.parse(pcx);
        }
        else{// '('のとき
            tk = ct.getNextToken(pcx);
            if(Expression.isFirst(tk)){
                factor = new Expression(pcx);
                factor.parse(pcx);
            }else{
                pcx.fatalError(tk.toExplainString() + "(の後ろはexpressionです");
            }
            tk = ct.getCurrentToken(pcx);
            if(tk.getType() != CToken.TK_RPAR){
                pcx.fatalError(tk.toExplainString() + ")で閉じ忘れています");
            }
            tk = ct.getNextToken(pcx);
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
        o.println(";;; unsignedfactor starts");
        if (factor != null) { factor.codeGen(pcx); }
        o.println(";;; unsignedfactor completes");
    }
}