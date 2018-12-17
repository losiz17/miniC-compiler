package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class Declaration extends CParseRule {
    // Declaration ::= intDecl | constDecl
    private CParseRule ident ,variable,declaration;

    public Declaration(CParseContext pcx) {
    }
    public static boolean isFirst(CToken tk) {
        return IntDecl.isFirst(tk) || ConstDecl.isFirst(tk);
    }
    public void parse(CParseContext pcx) throws FatalErrorException {

        // ここにやってくるときは、必ずisFirst()が満たされている
        CTokenizer ct = pcx.getTokenizer();
        CToken tk = ct.getCurrentToken(pcx);
//        System.out.println(tk.getText()+":Declaration");
        if(IntDecl.isFirst(tk)){
            declaration = new IntDecl(pcx);
            declaration.parse(pcx);
        }
        else if(ConstDecl.isFirst(tk)){
            declaration = new ConstDecl(pcx);
            declaration.parse(pcx);
        }
    }

    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        if(declaration != null){
            declaration.semanticCheck(pcx);
            this.setCType(declaration.getCType());		// declaration の型をそのままコピー
            this.setConstant(declaration.isConstant());
        }
    }

    public void codeGen(CParseContext pcx) throws FatalErrorException {
        PrintStream o = pcx.getIOContext().getOutStream();
        o.println(";;; Declaration starts");
        if (declaration != null) 		{ declaration.codeGen(pcx); }
        o.println(";;; Declaration completes");
    }
}