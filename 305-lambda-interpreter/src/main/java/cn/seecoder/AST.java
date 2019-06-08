package cn.seecoder;

/*进行分析时，需要以存储分析出的信息，为此要建立 抽象语法树 ( AST ) 。
 lambda 演算的 AST 非常简单，因为我们只有 3 种节点： 
 Abstraction （抽象）， Application （应用）以及 Identifier （标识符）。

 Abstraction 持有其参数（param） 和主体（body）；
 Application 则持有语句的左右侧； 
 Identifier 是一个叶节点，只有持有该标识符本身的字符串表示形式。

这是一个简单的程序及其 AST:

(λx. x) (λy. y)

Application {
  abstraction: Abstraction {
    param: Identifier { name: 'x' },
    body: Identifier { name: 'x' }
  },
  value: Abstraction {
    param: Identifier { name: 'y' },
    body: Identifier { name: 'y' }
  }
}*/
public abstract class AST {
    public abstract String toString();
}
