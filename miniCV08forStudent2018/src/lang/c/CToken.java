package lang.c;

import lang.SimpleToken;

public class CToken extends SimpleToken {
	public static final int TK_PLUS			= 2;				// +
	public static final int TK_MINUS		= 3;				// -
	public static final int TK_DIV			= 4;				// /
	public static final int TK_MULT			= 5;				// *
	public static final int TK_AMP			= 6;				// &
	public static final int TK_LPAR			= 7;				// (
	public static final int TK_RPAR			= 8;				// )
	public static final int TK_LBRA			= 9;				// [
	public static final int TK_RBRA			= 10;				// ]
	public static final int TK_ASSIGN		= 11;				// =
	public static final int TK_SEMI			= 12;				// ;
	public static final int TK_COMMA		= 13;				// ,
	public static final int TK_INT			= 14;				// int
	public static final int TK_CONST		= 15;				// const
	public static final int TK_TRUE			= 16;				// true
	public static final int TK_FALSE		= 17;				// false
	public static final int TK_LT			= 18;				// <
	public static final int TK_LE			= 19;				// <=
	public static final int TK_GT			= 20;				// >
	public static final int TK_GE			= 21;				// >=
	public static final int TK_EQ			= 22;				// ==
	public static final int TK_NE			= 23;				// !=
	public static final int TK_LCUR			= 24;				// {
	public static final int TK_RCUR			= 25;				// }
	public static final int TK_IF			= 26;				// if
	public static final int TK_ELSE			= 27;				// else
	public static final int TK_INPUT		= 28;				// input
	public static final int TK_OUTPUT		= 29;				// output
	public static final int TK_WHILE		= 30;				// while

	public CToken(int type, int lineNo, int colNo, String s) {
		super(type, lineNo, colNo, s);
	}
}
