package lang.c;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

import lang.Tokenizer;

public class CTokenizer extends Tokenizer<CToken, CParseContext> {
	@SuppressWarnings("unused")
	private CTokenRule	rule;
	private int			lineNo, colNo;
	private char		backCh;
	private boolean		backChExist = false;

	public CTokenizer(CTokenRule rule) {
		this.rule = rule;
		lineNo = 1; colNo = 1;
	}

	private InputStream in;
	private PrintStream err;

	private char readChar() {
		char ch;
		if (backChExist) {
			ch = backCh;
			backChExist = false;
		} else {
			try {
				ch = (char) in.read();
			} catch (IOException e) {
				e.printStackTrace(err);
				ch = (char) -1;
			}
		}
		++colNo;
		if (ch == '\n')  { colNo = 1; ++lineNo; }
//		System.out.print("'"+ch+"'("+(int)ch+")");
		return ch;
	}
	private void backChar(char c) {
		backCh = c;
		backChExist = true;
		--colNo;
		if (c == '\n') { --lineNo; }
	}

	// 現在読み込まれているトークンを返す
	private CToken currentTk = null;
	public CToken getCurrentToken(CParseContext pctx) {
		return currentTk;
	}
	// 次のトークンを読んで返す
	public CToken getNextToken(CParseContext pctx) {
		in = pctx.getIOContext().getInStream();
		err = pctx.getIOContext().getErrStream();
		currentTk = readToken();
//		System.out.println("Token='" + currentTk.toString());
		return currentTk;
	}
	private CToken readToken() {
		CToken tk = null;
		char ch;
		int  startCol = colNo;
		StringBuffer text = new StringBuffer();

		int state = 0;
		boolean accept = false;
		while (!accept) {
			switch (state) {
				case 0:                    // 初期状態
					ch = readChar();
					if (ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r') {
					} else if (ch == (char) -1) {    // EOF
						startCol = colNo - 1;
						state = 1;
					} else if (ch > '0' && ch <= '9') {
						startCol = colNo - 1;
						text.append(ch);
						state = 3;
					} else if (ch == '+') {
						startCol = colNo - 1;
						text.append(ch);
						state = 5;
					} else if (ch == '-') {
						startCol = colNo - 1;
						text.append(ch);
						state = 6;
					} else if (ch == '/') {
						startCol = colNo - 1;
						state = 7;
//					除算ならappend
					} else if (ch == '&') {
						startCol = colNo - 1;
						text.append(ch);
						state = 11;
					} else if (ch == '0') {
						startCol = colNo - 1;
						text.append(ch);
						state = 12;

					}else if(ch == '*'){
						startCol = colNo - 1;
						text.append(ch);
						state = 16;
					}else if(ch == '('){
						startCol = colNo - 1;
						text.append(ch);
						state = 18;
					}else if(ch ==')'){
						startCol = colNo - 1;
						text.append(ch);
						state = 19;
					}else if(ch =='['){
						startCol = colNo - 1;
						text.append(ch);
						state = 20;
					}else if(ch ==']'){
						startCol = colNo - 1;
						text.append(ch);
						state = 21;
					}else if(ch >='a' && ch<='z' || ch >='A' && ch<='Z'){//identの始まり
						startCol = colNo - 1;
						text.append(ch);
						state = 22;
					}else if(ch == '='){
						startCol = colNo - 1;
						text.append(ch);
						state = 24;
					}else if(ch == ';') {
						startCol = colNo - 1;
						text.append(ch);
						state = 25;
					}else if(ch == ','){
						startCol = colNo - 1;
						text.append(ch);
						state = 26;
					} else {            // ヘンな文字を読んだ
						startCol = colNo - 1;
						text.append(ch);
						state = 2;
					}
					break;
				case 1:                    // EOFを読んだ
					tk = new CToken(CToken.TK_EOF, lineNo, startCol, "end_of_file");
					accept = true;
					break;
				case 2:                    // ヘンな文字を読んだ
					tk = new CToken(CToken.TK_ILL, lineNo, startCol, text.toString());
					accept = true;
					//System.out.println("case2");
					break;
				case 3:                    // 数（10進数）の開始
					ch = readChar();
					if (Character.isDigit(ch)) {
						text.append(ch);
					} else {
						// 数の終わり
						if (text!=null) {
							try {
								int num = Integer.decode(text.toString());//文字列を10進数に変換
								if (num > 65535) {
									state = 2;//ILLへ
									break;
								}
							}catch (NumberFormatException e) {
								state=2;
								break;
							}
						}
						backChar(ch);    // 数を表さない文字は戻す（読まなかったことにする）
						tk = new CToken(CToken.TK_NUM, lineNo, startCol, text.toString());
						accept = true;
					}
					break;
				case 4:                    // 数の終わり
					if (text!=null) {
						try {
							int num = Integer.decode(text.toString());//文字列を10進数に変換
							if (num > 65535) {
								state = 2;//ILLへ
								break;
							}
						}catch (NumberFormatException e) {
							state=2;
							break;
						}
					}

					tk = new CToken(CToken.TK_NUM, lineNo, startCol, text.toString());

					accept = true;
					break;
				case 5:                    // +を読んだ
					tk = new CToken(CToken.TK_PLUS, lineNo, startCol, "+");
					accept = true;
					break;
				case 6:                    // -を読んだ
					tk = new CToken(CToken.TK_MINUS, lineNo, startCol, "-");
					accept = true;
					break;
				case 7:                    // /を読んだ
					ch = readChar();
					if (ch == '/') {
						state = 8;
					} else if (ch == '*') {
						state = 9;
					} else {
						backChar(ch);
						state = 17;
					}
					break;
				case 8:                // //を読んだ
					ch = readChar();
					if (ch == (char) -1) {//EOF
						backChar(ch);
						state = 1;
					} else if (ch == '\n') {//\n
						state = 0;
						//accept = true;
					} else {

					}
					break;
				case 9:                // /*を読んだ
					ch = readChar();
					if (ch == '*') {
						state = 10;
					} else if (ch == (char) -1) {
						backChar(ch);
						state = 2;
					} else {
						state = 9;
					}
					break;
				case 10:                // /*~*を読んだ
					ch = readChar();
					if (ch == '*') {
						state = 10;
					} else if (ch == '/') {
						state = 0;
						//accept = true;

					} else if (ch == (char) -1) {
						backChar(ch);
						state = 2;
					} else {
						state = 9;
					}
					break;
				case 11:                    // &を文字を読んだ
					tk = new CToken(CToken.TK_AMP, lineNo, startCol, text.toString());
					accept = true;
					break;
				case 12:                    // 0を読んだ
					ch = readChar();
					if (ch >= '0' && ch <= '7') {//8進数
						text.append(ch);
						state = 13;
					} else if (ch == 'x') {//16進数
						text.append(ch);
						state = 14;
					} else {//'0'のとき
						backChar(ch);
						state = 4;
					}
					break;
				case 13:                //8進数
					ch = readChar();
					if (ch >= '0' && ch <= '7') {
						text.append(ch);
						state = 10;
					} else {        //8進数の字句
						backChar(ch);
						state = 4;
					}

				case 14:                //16進数
					ch = readChar();
					if ((ch >= '0' && ch <= '9') || (ch >= '0' && ch <= 'f') || (ch >= 'A' && ch <= 'F')) {
						text.append(ch);
						state = 15;
					} else {
						backChar(ch);
						state = 2;
					}
				case 15:
					ch = readChar();
					if ((ch >= '0' && ch <= '9') || (ch >= '0' && ch <= 'z') || (ch >= 'A' && ch <= 'Z')) {
						text.append(ch);
						state = 15;
					}else {        //16進数の字句
						backChar(ch);
						state = 4;
					}
				case 16:
					tk = new CToken(CToken.TK_MULT, lineNo, startCol, text.toString());
					accept = true;
					break;
				case 17:
					tk = new CToken(CToken.TK_DIV, lineNo, startCol, "/");
					accept = true;
					break;
				case 18:
					tk = new CToken(CToken.TK_LPAR, lineNo, startCol, text.toString());
					accept = true;
					break;
				case 19:
					tk = new CToken(CToken.TK_RPAR, lineNo, startCol, text.toString());
					accept = true;
					break;
				case 20:
					tk = new CToken(CToken.TK_LBRA, lineNo, startCol, "[");
					accept = true;
					break;
				case 21:
					tk = new CToken(CToken.TK_RBRA, lineNo, startCol, "]");
					accept = true;
					break;
				case 22:
					ch = readChar();
					if( (ch >= '0' && ch <= '9') || (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') ){
						text.append(ch);
						state = 22;
					}else{
						backChar(ch);
						state = 23;
					}
					break;
				case 23://ident受理
					String s = text.toString();
					Integer i = (Integer)rule.get(s);
					tk = new CToken((i == null) ? CToken.TK_IDENT : i.intValue(), lineNo, startCol, s);
					accept = true;
					break;

				case 24://'='受理
					tk = new CToken(CToken.TK_ASSIGN, lineNo, startCol, "=");
					accept = true;
					break;
				case 25://';'受理
					tk = new CToken(CToken.TK_SEMI, lineNo, startCol, ";");
					accept = true;
					break;
				case 26://','受理
					tk = new CToken(CToken.TK_COMMA, lineNo, startCol, ",");
					accept = true;
					break;

			}
		}
		return tk;
	}
}
