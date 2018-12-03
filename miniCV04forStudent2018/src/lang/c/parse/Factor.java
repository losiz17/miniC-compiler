package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class Factor extends CParseRule {
	// factor ::= plusFactor | minusFactor | unsignedFactor
	private CParseRule factor;
	public Factor(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return plusFactor.isFirst(tk) | minusFactor.isFirst(tk) | UnsignedFactor.isFirst(tk);
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		if(plusFactor.isFirst(tk)){
			factor = new plusFactor(pcx);
			factor.parse(pcx);
		}else if(minusFactor.isFirst(tk)){
			factor = new minusFactor(pcx);
			factor.parse(pcx);
		}else{
			factor = new UnsignedFactor(pcx);
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
		o.println(";;; factor starts");
		if (factor != null) { factor.codeGen(pcx); }
		o.println(";;; factor completes");
	}
}