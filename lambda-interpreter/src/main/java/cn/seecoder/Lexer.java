package cn.seecoder;

import java.util.regex.Pattern;

//词法分析器
public class Lexer{
	
    public String source;
    public int index;
    public TokenType token;
    public String tokenvalue;

    public Lexer(String s){
        index = 0;
        source = s;
        nextToken();
    }
    
    // 得到并识别token
    private TokenType nextToken(){
    	//得到第一个非空白字符,使用正则表达式匹配
    	char c;
    	do {
    		c = this.nextChar();
    	}while(Pattern.matches("\\s",String.valueOf(c)));
    	//
    	switch(c) {
    	case 'λ':
        case '\\':
          this.token = TokenType.LAMBDA;
          break;

        case '.':
          this.token = TokenType.DOT;
          break;

        case '(':
          this.token = TokenType.LPAREN;
          break;

        case ')':
          this.token = TokenType.RPAREN;
          break;

        case '\0':
          this.token = TokenType.EOF;
          break;
        default:
          if(Pattern.matches("[a-z]",String.valueOf(c))) {
        	  //类型匹配就把字符串读完
        	  StringBuffer str= new StringBuffer();
        	  do {
        		  str.append(c);
        		  c = this.nextChar();
        	  }while(Pattern.matches("[a-zA-Z]",String.valueOf(c)));
        	  //已经读到非字符，索引回退
	          this.index--;                                                    
	          this.token = TokenType.LCID;
	          this.tokenvalue = String.valueOf(str);
          }
    	}
    	return token;
    }

    //得到下一个字符，直到最后source.charAt(index-1)
    private char nextChar(){
    	if(this.index>=this.source.length())  return '\0'; 
    	else return this.source.charAt(this.index++);
    }

    //检查现在的token是否为给定token类型
    public boolean next(TokenType t){
    	return this.token==t;
    }

    //无返回的skip
    public void match(TokenType t){
    	if(this.next(t)) {
    		System.out.println(String.valueOf(token));
    		this.nextToken();
    	}
    }

    //next方法检查后，再取下一个token
    public boolean skip(TokenType t){
    	if(this.next(t)) {
    		System.out.println(String.valueOf(token));
    		this.nextToken();
    		return true;
    	}else return false;
    }

}
