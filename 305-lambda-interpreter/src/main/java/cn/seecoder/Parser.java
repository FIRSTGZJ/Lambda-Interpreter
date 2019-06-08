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
    	this.lexer.match(TokenType.EOF);      //make sure we consumed all the program, otherwise there was a syntax error
        return ast;
    }
    
    // term ::= LAMBDA LCID DOT term
    //递归实现,向term填装De Bruin值对应的形参
    
    private AST term(ArrayList<String> ctx){
	    	if(this.lexer.skip(TokenType.LAMBDA)) {
	    		String id = this.lexer.tokenvalue;
	    		ArrayList<String> temp = new ArrayList<String>();
	    		this.lexer.match(TokenType.LCID);
	    		this.lexer.match(TokenType.DOT);
	    		temp.add(id);
	    		temp.addAll(ctx);
	    		AST term = this.term(temp);  //x y z 2 1 0
	    		//term是application,举例可证代码\x.\y.\z.xyz
	    		Identifier iden = new Identifier(temp.get(temp.size()-1),String.valueOf(temp.size()-1));
	    		return new Abstraction(iden,term);       //temp内容减少，用完就扔
	    		//返回一个Abstraction类型，以第一个id为形参，term为body
	    	}else 	return this.application(ctx);
    }
    /*    Application::=Application Atom | Atom    或
     * -> Application ::=Atom ... Atom
     * -> Application'::=Atom Application'
     *                  | 'Empty
     **/
    //移除左递归
    // application ::= atom application'
    private AST application(ArrayList<String> ctx){
        AST left = this.atom(ctx);
        while(true) {
        	AST right = this.atom(ctx);
        	//检查递归是否结束，返回左部分
        	if(right==null) return left;
        	else left = new Application(left,right);
        	//未结束，建立抽象树
        }
    }

    // atom ::= LPAREN term RPAREN
    //        | LCID
    private AST atom(ArrayList<String> ctx){
    	if(this.lexer.skip(TokenType.LPAREN)) {
    		AST term = this.term(ctx);
    		this.lexer.match(TokenType.RPAREN);
    		return term;
    	}else if(this.lexer.skip(TokenType.LCID)) {
    		String id = this.lexer.tokenvalue;
    		return new Identifier(id,String.valueOf(ctx.indexOf(id)));
    	}else return null;
    }
}
