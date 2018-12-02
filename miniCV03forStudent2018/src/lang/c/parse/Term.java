package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class Term extends CParseRule {
	// term ::= factor{termMult | termDiv}
	private CParseRule factor;
	public Term(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return Factor.isFirst(tk);
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CParseRule term = null, list = null;
		term = new Factor(pcx);//term用の節点
		term.parse(pcx);//解析
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		while (termMult.isFirst(tk) || termDiv.isFirst(tk)) {
			if(termDiv.isFirst(tk)){
				term = new termDiv(pcx, term);
				term.parse(pcx);
				//term = list;
				tk = ct.getCurrentToken(pcx);
			}
			else if(termMult.isFirst(tk)){
				term = new termMult(pcx, term);
				term.parse(pcx);
				//term = list;
				tk = ct.getCurrentToken(pcx);
			}
		}
		factor = term;
	}


	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (factor != null) {
			factor.semanticCheck(pcx);
			this.setCType(factor.getCType());		// factor の型をそのままコピー
			this.setConstant(factor.isConstant());
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; term starts");
		if (factor != null) { factor.codeGen(pcx); }
		o.println(";;; term completes");
	}
}

class termMult extends CParseRule {
	// termMult ::= '*' factor
	private CToken mult;
	private CParseRule left, right;

	public termMult(CParseContext pcx, CParseRule left) {
		this.left = left;//leftの値を記憶
	}
	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_MULT;
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		mult = ct.getCurrentToken(pcx);
		// *の次の字句を読む
		CToken tk = ct.getNextToken(pcx);
		if (Factor.isFirst(tk)) {
			right = new Factor(pcx);
			right.parse(pcx);
		} else {
			pcx.fatalError(tk.toExplainString() + "*の後ろはfactorです");
		}
	}
	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		// 掛け算の型計算規則
		final int s[][] = {
//				ポインタ*整数＝err	ポインタ*ポインタ＝ERR
				//右辺	T_err			T_int			T_pint
				{	CType.T_err,	CType.T_err,	CType.T_err },	// T_err
				{	CType.T_err,	CType.T_int,	CType.T_err },	// T_int
				{	CType.T_err,	CType.T_err,	CType.T_err },	// T_pint
				// 左辺
		};
		int lt = 0, rt = 0;
		boolean lc = false, rc = false;
		if (left != null) {
			left.semanticCheck(pcx);
			lt = left.getCType().getType();		// +の左辺の型
			lc = left.isConstant();
		} else {
			pcx.fatalError(mult.toExplainString() + "左辺がありません");
		}
		if (right != null) {
			right.semanticCheck(pcx);
			rt = right.getCType().getType();	// +の右辺の型
			rc = right.isConstant();
		} else {
			pcx.fatalError(mult.toExplainString() + "右辺がありません");
		}
		int nt = s[lt][rt];						// 規則による型計算
		if (nt == CType.T_err) {
			pcx.fatalError(mult.toExplainString() + "左辺の型[" + left.getCType().toString() + "]と右辺の型[" + right.getCType().toString() + "]は掛けられません");
		}
		this.setCType(CType.getCType(nt));
		this.setConstant(lc && rc);				// +の左右両方が定数のときだけ定数
	}
	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		if (left != null && right != null) {
			left.codeGen(pcx);
			right.codeGen(pcx);
			o.println("\tJSR\tMUL\t; termMult: サブルーチンMULへ");
			o.println("\tSUB\t#2, R6\t;termMult:スタックの引数を下ろす");
			o.println("\tMOV\tR0, (R6)+\t; termMult:");
		}
	}
}


class termDiv extends CParseRule {
	// expressionSub ::= '/' factor
	private CToken div;
	private CParseRule left, right;

	public termDiv(CParseContext pcx, CParseRule left) {
		this.left = left;//leftの値を記憶
	}
	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_DIV;
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		div = ct.getCurrentToken(pcx);
		// -の次の字句を読む
		CToken tk = ct.getNextToken(pcx);
		if (Factor.isFirst(tk)) {
			right = new Factor(pcx);
			right.parse(pcx);
		} else {
			pcx.fatalError(tk.toExplainString() + "/(除算)の後ろはfactorです");
		}
	}
	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		// 割り算の型計算規則
		final int s[][] = {
//
				//右辺		T_err			T_int			T_pint
				{	CType.T_err,	CType.T_err,	CType.T_err },	// T_err
				{	CType.T_err,	CType.T_int,	CType.T_err },	// T_int
				{	CType.T_err,	CType.T_err,	CType.T_err },	// T_pint
				// 左辺
		};
		int lt = 0, rt = 0;
		boolean lc = false, rc = false;
		if (left != null) {
			left.semanticCheck(pcx);
			lt = left.getCType().getType();		// -の左辺の型
			lc = left.isConstant();
		} else {
			pcx.fatalError(div.toExplainString() + "左辺がありません");
		}
		if (right != null) {
			right.semanticCheck(pcx);
			rt = right.getCType().getType();	// -の右辺の型
			rc = right.isConstant();
		} else {
			pcx.fatalError(div.toExplainString() + "右辺がありません");
		}
		int nt = s[lt][rt];						// 規則による型計算
		if (nt == CType.T_err) {
			pcx.fatalError(div.toExplainString() + "左辺の型[" + left.getCType().toString() + "]と右辺の型[" + right.getCType().toString() + "]は引けません");
		}
		this.setCType(CType.getCType(nt));
		this.setConstant(lc && rc);				// '/'の左右両方が定数のときだけ定数
	}
	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		if (left != null && right != null) {
			right.codeGen(pcx);
			left.codeGen(pcx);
			o.println("\tJSR\tDIV\t; termDiv: サブルーチンンDIVへ飛ぶ");
			o.println("\tSUB\t#2,R6\t; termDiv:スタックの引数を下ろす");
			o.println("\tMOV\tR0, (R6)+\t; termDiv:");
		}
	}
}
