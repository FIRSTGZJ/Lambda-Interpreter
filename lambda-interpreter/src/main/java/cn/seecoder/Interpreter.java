package cn.seecoder;

//解释器
public class Interpreter {
    Parser parser;
    AST astAfterParser;

    public Interpreter(Parser p){
        parser = p;
        astAfterParser = p.parse();
    }


    private  boolean isAbstraction(AST ast){
        return ast instanceof Abstraction;
    }
    private  boolean isApplication(AST ast){
        return ast instanceof Application;
    }
    private  boolean isIdentifier(AST ast){
        return ast instanceof Identifier;
    }

    public AST eval(){
        return evalAST(astAfterParser);
    }
    
    //计算化简
    private   AST evalAST(AST ast){
    	while(true){
    		//只在最简时返回，其他为递归
    		if(isValue(ast)) return ast;
    		else if(isApplication(ast)){
    			if(isValue(((Application)ast).lhs)&&isValue(((Application)ast).rhs)){
    				ast = substitute(((Abstraction)((Application)ast).lhs).body,((Application)ast).rhs);
    			}//左侧最简，化简右式
    			else if(isValue(((Application)ast).lhs)){
    				((Application)ast).rhs = evalAST(((Application)ast).rhs);
    			}//右侧最简，化简左式
    			else ((Application)ast).lhs = evalAST(((Application)ast).lhs);
    		}
    		else if(isAbstraction(ast))  ((Abstraction)ast).body = evalAST(((Abstraction)ast).body);
    	}
    }
    
    //判断是否为最简形式
    public boolean isValue(AST ast){
    	if(isIdentifier(ast))  return true;
    	else if(isApplication(ast)){
    		if(isAbstraction(((Application)ast).lhs)) return false;
    		//确保一定不是
    		else return isValue(((Application)ast).lhs)&&isValue(((Application)ast).rhs);
    	} 
    	else if(isAbstraction(ast)){
    		if(isValue(((Abstraction)ast).body)) return true;
    		else return false;
    	}else return false;
    }
    
    //替换node节点中变量
    private AST substitute(AST node,AST value){
        return shift(-1,subst(node,shift(1,value,0),0),0);
    }

    /**
     *  value替换node节点中的变量：
     *  如果节点是Applation，分别对左右树替换；
     *  如果node节点是abstraction，替入node.body时深度得+1；
     *  如果node是identifier，则替换De Bruijn index值等于depth的identifier（替换之后value的值加深depth）

     *@param value 替换成为的value
     *@param node 被替换的整个节点
     *@param depth 外围的深度

             
     *@return AST
     *@exception  (方法有异常的话加)


     */
    /**
     * process of substitute
     *        app
     *       /  \
     *      abs  abs(value)  ::the former:node
     *     /  \
     * param  body
     * app->substitute(value,body)->the outermost param was dropped out
     **/
    private AST subst(AST node, AST value, int depth){
        if(isApplication(node)) {
        	return new Application(subst(((Application)node).lhs, value, depth),subst(((Application)node).rhs, value, depth));
        }else if(isAbstraction(node)) {
        	return new Abstraction(((Abstraction)node).param, subst(((Abstraction)node).body, value,depth+1));
        }else if(isIdentifier(node)) {
        	if(depth == Integer.parseInt(((Identifier)node).value)) {
        		return shift(depth,value,0);          
        	}else return node;
        }
        return node;

    }

    /**
     *  De Bruijn index值位移
     *  如果节点是Applation，分别对左右树位移；
     *  如果node节点是abstraction，新的body等于旧node.body位移by（from得+1）；
     *  如果node是identifier，则新的identifier的De Bruijn index值如
                  果大于等于from则加by，否则加0（超出内层的范围的外层变量才要
        shift by位）.

     *@param by 位移的距离
     *@param node 位移的节点
     *@param from 内层的深度
     *@return AST
     */
    private AST shift(int by, AST node,int from){
        //write your code here
    	if(isApplication(node)) {
    		return new Application(shift(by,((Application)node).lhs,from),shift(by,((Application)node).rhs,from));
    	}
    	else if(isAbstraction(node)) {
    		return new Abstraction((Identifier) ((Abstraction)node).param,shift(by, ((Abstraction)node).body, from+1));
    	}
    	else if(isIdentifier(node)) {
    			int temp = Integer.parseInt(((Identifier)node).value);
    			return new Identifier(((Identifier)node).name , String.valueOf(temp + (temp >= from ? by : 0)));
    	}else return node;
    }

    
    static String ZERO = "(\\f.\\x.x)";
    static String SUCC = "(\\n.\\f.\\x.f (n f x))";
    static String ONE = app(SUCC, ZERO);
    static String TWO = app(SUCC, ONE);
    static String THREE = app(SUCC, TWO);
    static String FOUR = app(SUCC, THREE);
    static String FIVE = app(SUCC, FOUR);
    static String PLUS = "(\\m.\\n.((m "+SUCC+") n))";
    static String POW = "(\\b.\\e.e b)";       // POW not ready
    static String PRED = "(\\n.\\f.\\x.n(\\g.\\h.h(g f))(\\u.x)(\\u.u))";
    static String SUB = "(\\m.\\n.n"+PRED+"m)";
    static String TRUE = "(\\x.\\y.x)";
    static String FALSE = "(\\x.\\y.y)";
    static String AND = "(\\p.\\q.p q p)";
    static String OR = "(\\p.\\q.p p q)";
    static String NOT = "(\\p.\\a.\\b.p b a)";
    static String IF = "(\\p.\\a.\\b.p a b)";
    static String ISZERO = "(\\n.n(\\x."+FALSE+")"+TRUE+")";
    static String LEQ = "(\\m.\\n."+ISZERO+"("+SUB+"m n))";
    static String EQ = "(\\m.\\n."+AND+"("+LEQ+"m n)("+LEQ+"n m))";
    static String MAX = "(\\m.\\n."+IF+"("+LEQ+" m n)n m)";
    static String MIN = "(\\m.\\n."+IF+"("+LEQ+" m n)m n)";

    private static String app(String func, String x){
        return "(" + func + x + ")";
    }
    private static String app(String func, String x, String y){
        return "(" +  "(" + func + x +")"+ y + ")";
    }
    private static String app(String func, String cond, String x, String y){
        return "(" + func + cond + x + y + ")";
    }

    public static void main(String[] args) {
        // write your code here

        String[] sources = {
                ZERO,//0
                ONE,//1
                TWO,//2
                THREE,//3
                app(PLUS, ZERO, ONE),//4
                app(PLUS, TWO, THREE),//5
                app(POW, TWO, TWO),//6
                app(PRED, ONE),//7
                app(PRED, TWO),//8
                app(SUB, FOUR, TWO),//9
                app(AND, TRUE, TRUE),//10
                app(AND, TRUE, FALSE),//11
                app(AND, FALSE, FALSE),//12
                app(OR, TRUE, TRUE),//13
                app(OR, TRUE, FALSE),//14
                app(OR, FALSE, FALSE),//15
                app(NOT, TRUE),//16
                app(NOT, FALSE),//17
                app(IF, TRUE, TRUE, FALSE),//18
                app(IF, FALSE, TRUE, FALSE),//19
                app(IF, app(OR, TRUE, FALSE), ONE, ZERO),//20
                app(IF, app(AND, TRUE, FALSE), FOUR, THREE),//21
                app(ISZERO, ZERO),//22
                app(ISZERO, ONE),//23
                app(LEQ, THREE, TWO),//24
                app(LEQ, TWO, THREE),//25
                app(EQ, TWO, FOUR),//26
                app(EQ, FIVE, FIVE),//27
                app(MAX, ONE, TWO),//28
                app(MAX, FOUR, TWO),//29
                app(MIN, ONE, TWO),//30
                app(MIN, FOUR, TWO),//31
        };

        //for(int i=0; i<sources.length; i++) {
        	int i=4;
            String source = sources[i];

            System.out.println(i+":"+source);

            Lexer lexer = new Lexer(source);

            Parser parser = new Parser(lexer);

            Interpreter interpreter = new Interpreter(parser);

            AST result = interpreter.eval();

            System.out.println(i+":" + result.toString());

        //}
    }
}
