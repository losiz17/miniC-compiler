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
        ident = new Ident(pcx);//ident用の節点
        ident.parse(pcx);
        CTokenizer ct = pcx.getTokenizer();
        CToken tk = ct.getCurrentToken(pcx);
        if (Array.isFirst(tk)) {
            variable = new Array(pcx);
            variable.parse(pcx);

        }
    }

    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        if (ident != null) {
            ident.semanticCheck(pcx);
            setCType(ident.getCType());  // identの型をそのままコピー
        }
        if (variable != null) {
            variable.semanticCheck(pcx);
            if(this.getCType().getType()!=CType.T_int_array){
                if(this.getCType().getType()!=CType.T_pint_array) {
                    pcx.fatalError("配列名の識別子はint[]型または、pint[]型である必要があります。");
                }
            }
            if(this.getCType().getType()==CType.T_int_array)
                this.setCType(CType.getCType(CType.T_int));
            if(this.getCType().getType()==CType.T_pint_array)
                this.setCType(CType.getCType(CType.T_pint));
        }else {
            if(this.getCType().getType()==CType.T_int_array) {
                pcx.fatalError("int配列型なのに添字がありません");
            }else if(this.getCType().getType()==CType.T_pint_array){
                pcx.fatalError("ポインタ配列型なのに添字がありません");
            }
        }
//		   setConstant(variable.isConstant()); // number は常に定数

//		if(ident != null){
//			ident.semanticCheck(pcx);
//			setCType(ident.getCType());		// number の型をそのままコピー
//			setConstant(ident.isConstant());	// number は常に定数
//			if( variable!= null){
//				variable.semanticCheck(pcx);
//				if(ident.getCType().getType() != CType.T_int_array){
//					if(ident.getCType().getType() != CType.T_pint_array){
//						pcx.fatalError("配列名の識別子はint[]型または、pint[]型である必要があります。");
//					}
//
//				}
//				if(ident.getCType().getType() == CType.T_int_array && variable.getCType().getType() == CType.T_int){//添字がint型ならint型
//					setCType(CType.getCType(CType.T_int));//int型に
//					setConstant(ident.isConstant());//常に定数
//				}
//				if(ident.getCType().getType() == CType.T_pint_array && variable.getCType().getType() == CType.T_int){//
//					setCType(CType.getCType(CType.T_pint));//pint型に
//					setConstant(ident.isConstant());//常に定数
//				}
//				else{
//					setCType(ident.getCType());//identの型をコピー
//					setConstant(ident.isConstant());//常に定数
//				}
//
//			}else{//variableがnull
//				if(ident.getCType().getType() == CType.T_int_array){
//					System.out.println(ident.getCType().getType());
//					pcx.fatalError("int配列型なのに添字がありません");
//				}
//				if(ident.getCType().getType() == CType.T_pint_array){
//					pcx.fatalError("ポインタ配列型なのに添字がありません");
//				}
//			}
//		}


    }

    public void codeGen(CParseContext pcx) throws FatalErrorException {
        PrintStream o = pcx.getIOContext().getOutStream();
        o.println(";;; Variable starts");
        if (ident != null) 		{ ident.codeGen(pcx); }
        if (variable != null) {
            variable.codeGen(pcx);
        }
        o.println(";;; Variable completes");
    }
}