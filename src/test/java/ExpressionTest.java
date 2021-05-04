import org.junit.Assert;
import org.junit.Test;
import org.mariuszgromada.math.mxparser.Expression;

public class ExpressionTest {

    @Test
    public void expressionArgsTest(){
        Expression e = new Expression();
        e.setExpressionString("x (+) y");

        e.defineArguments("x", "y");
        int n = e.getArgumentsNumber();
        Assert.assertEquals(2,n);
    }
}
