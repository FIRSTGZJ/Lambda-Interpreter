package cn.seecoder;

public class Abstraction extends AST {
    Identifier param;//变量
    AST body;//表达式
    //body用AST是因为body可能为\\x.  又包含term
    
    Abstraction(Identifier p, AST b){
        param = p;
        body = b;
    }

    public String toString(){
    	//body.toString()调用哪个子类方法由虚拟机决定，即由传进来的b的类型决定
    	String m = "（\\."+this.body.toString()+")";
    	return m;
    }
}
