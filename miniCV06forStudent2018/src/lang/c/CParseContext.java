package lang.c;

import lang.*;

public class CParseContext extends ParseContext {
	CSymbolTable Symbol;
	public CParseContext(IOContext ioCtx,  CTokenizer tknz) {
		super(ioCtx, tknz);
		Symbol = new CSymbolTable();
	}
	//記号表を管理
	public CSymbolTable getCSymbolTable(){
		return Symbol;
	}
	@Override
	public CTokenizer getTokenizer()		{ return (CTokenizer) super.getTokenizer(); }

	private int seqNo = 0;
	public int getSeqId() { return ++seqNo; }
}
