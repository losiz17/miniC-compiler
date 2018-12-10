package lang.c.parse;

import java.io.PrintStream;
import java.util.ArrayList;

import lang.*;
import lang.c.*;

public class IntDecl extends CParseRule {
    // IntDecl ::= INT declItem { COMMA constItem } SEMI
    private CParseRule ident ,variable,declitem;
    private ArrayList<CParseRule> list;
    public IntDecl(CParseContext pcx) {
        list = new ArrayList<CParseRule>();
    }
    public static boolean isFirst(CToken tk) {
        return tk.getType() == CToken.TK_INT;
    }
    public void parse(CParseContext pcx) throws FatalErrorException {
        // ここにやってくるときは、必ずisFirst()が満たされている
        CTokenizer ct = pcx.getTokenizer();
        CToken tk = ct.getCurrentToken(pcx);
        if(tk.getType() != CToken.TK_INT){
            pcx.fatalError(tk.toExplainString() + "intが抜けています。");
        }
        tk = ct.getNextToken(pcx);
//		System.out.println(tk.getText()+":IntDecl");
        if(DeclItem.isFirst(tk)){
//			System.out.println("here:IntDecl");
            declitem = new DeclItem(pcx);
            declitem.parse(pcx);
            list.add(declitem);//declitemの数だけ追加
        }else{
            pcx.fatalError(tk.toExplainString() + "intの後ろにdeclItemが抜けています。");
        }
        tk = ct.getCurrentToken(pcx);
        while(tk.getType() == CToken.TK_COMMA){
            //System.out.println(tk.getText());
            tk = ct.getNextToken(pcx);
            if(DeclItem.isFirst(tk)){
                declitem = new DeclItem(pcx);
                declitem.parse(pcx);
                list.add(declitem);
            }else{
                pcx.fatalError(tk.toExplainString() + "COMMAの後ろにdeclItemが抜けています。");
            }
            tk = ct.getCurrentToken(pcx);
        }
        tk = ct.getCurrentToken(pcx);
        //System.out.println(tk.getText()+"before semi");
        if(tk.getType() != CToken.TK_SEMI){
            pcx.fatalError(tk.toExplainString() + "文の最後に;が抜けています。");
        }
        tk = ct.getNextToken(pcx);
    }

    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        if(declitem != null){
            declitem.semanticCheck(pcx);
            this.setCType(declitem.getCType());		// declitem の型をそのままコピー
            this.setConstant(declitem.isConstant());
        }
    }

    public void codeGen(CParseContext pcx) throws FatalErrorException {
        PrintStream o = pcx.getIOContext().getOutStream();
        o.println(";;; IntDecl starts");
        if (list != null) 	{
            for(CParseRule rule : list){
                rule.codeGen(pcx);
            }
        }
        o.println(";;; IntDecl completes");
    }
}