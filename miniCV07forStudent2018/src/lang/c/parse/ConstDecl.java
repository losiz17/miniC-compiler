package lang.c.parse;

import java.io.PrintStream;
import java.util.ArrayList;

import lang.*;
import lang.c.*;

public class ConstDecl extends CParseRule {
    // Variable ::= CONST INT constItem { COMMA constItem } SEMI
    private CParseRule ident ,variable,constitem;
    private ArrayList<CParseRule> list;
    public ConstDecl(CParseContext pcx) {
        list = new ArrayList<CParseRule>();
    }
    public static boolean isFirst(CToken tk) {
        return tk.getType() == CToken.TK_CONST;
    }
    public void parse(CParseContext pcx) throws FatalErrorException {
        // ここにやってくるときは、必ずisFirst()が満たされている
        CTokenizer ct = pcx.getTokenizer();
        CToken tk = ct.getCurrentToken(pcx);
        if(tk.getType() != CToken.TK_CONST){
            pcx.fatalError(tk.toExplainString() + "constが抜けています。");
        }
        tk = ct.getNextToken(pcx);
        if(tk.getType() != CToken.TK_INT){
            pcx.fatalError(tk.toExplainString() + "constの後にintが抜けています。");
        }
        tk = ct.getNextToken(pcx);
        if(ConstItem.isFirst(tk)){
//			System.out.println(tk.getText()+":ConstDecl");
            constitem = new ConstItem(pcx);
            constitem.parse(pcx);
            list.add(constitem);
        }
        tk = ct.getCurrentToken(pcx);
//		System.out.println(tk.getText()+":ConstDecl");
        while(tk.getType() == CToken.TK_COMMA){
            tk = ct.getNextToken(pcx);
            if(ConstItem.isFirst(tk)){
                constitem = new ConstItem(pcx);
                constitem.parse(pcx);
                list.add(constitem);
                //tk = ct.getCurrentToken(pcx);
            }else{
                pcx.fatalError(tk.toExplainString() + "COMMAの後ろにdeclItemが抜けています。");
            }
        }
        tk = ct.getCurrentToken(pcx);
        if(tk.getType() != CToken.TK_SEMI){
            pcx.fatalError(tk.toExplainString() + "文の最後に;が抜けています。");
        }
        tk = ct.getNextToken(pcx);
    }

    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        if(constitem != null){
            constitem.semanticCheck(pcx);
            this.setCType(constitem.getCType());		// constitem の型をそのままコピー
            this.setConstant(constitem.isConstant());
        }
    }

    public void codeGen(CParseContext pcx) throws FatalErrorException {
        PrintStream o = pcx.getIOContext().getOutStream();
        o.println(";;; ConstDecl starts");
        if (list != null) 	{
            for (int i= 0; i < list.size(); i++) {
                //System.out.println("here");
                list.get(i).codeGen(pcx);
            }
        }
        o.println(";;; ConstDecl completes");
    }
}