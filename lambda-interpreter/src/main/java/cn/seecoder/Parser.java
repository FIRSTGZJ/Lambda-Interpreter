package cn.seecoder;

import java.util.ArrayList;

//解析器
public class Parser {
    Lexer lexer;
    AST out;

    public Parser(Lexer l){
        lexer = l;
    }
    
    public AST parse(){
    	AST ast = term(new ArrayList<String>());
    	//确定读到EOF结束
    	this.lexer.match(TokenType.EOF);         
        return ast;
    }
    
    //递归实现,填装De Bruin值对应的形参     term ::= LAMBDA LCID DOT term
    private AST term(ArrayList<String> ctx){
	    	if(this.lexer.skip(TokenType.LAMBDA)) {
	    		String id = this.lexer.tokenvalue;
	    		ArrayList<String> temp = new ArrayList<String>();
	    		this.lexer.match(TokenType.LCID);
	    		this.lexer.match(TokenType.DOT);
	    		temp.add(id);
	    		temp.addAll(ctx);
	    		AST term = this.term(temp);  
	    		//term相当于application
	    		return new Abstraction(new Identifier(id,String.valueOf(temp.indexOf(id))),term);       
	    		//返回一个以第一个id为形参，term为body的Abstraction
	    	}else 	return this.application(ctx);
    }

    //移除左递归   application ::= atom application'  Application::=Application Atom | Atom
    private AST application(ArrayList<String> ctx){
        AST left = this.atom(ctx);
        while(true) {
        	AST right = this.atom(ctx);
        	//检查递归是否结束，返回左部分
        	if(right==null) return left;
        	//未结束，继续建立抽象树
        	else left = new Application(left,right);
        }
    }

    // atom ::= LPAREN term RPAREN | LCID
    private AST atom(ArrayList<String> ctx){
    	if(this.lexer.skip(TokenType.LPAREN)) {
    		AST term = this.term(ctx);
    		this.lexer.match(TokenType.RPAREN);
    		return term;
    	}else if(this.lexer.next(TokenType.LCID)) {
    		String id = this.lexer.tokenvalue;
    		this.lexer.match(TokenType.LCID);
    		return new Identifier(id,String.valueOf(ctx.indexOf(id)));
    	}else return null;
    }
}
