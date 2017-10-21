package krangl;

import kotlin.jvm.functions.Function2;

import java.util.Arrays;

import static krangl.ColumnsKt.asDoubles;
import static krangl.ColumnsKt.count;
import static krangl.ColumnsKt.sum;
import static krangl.MathHelpersKt.mean;

public class Summarise {
    public static void main(String[] args) {

        System.out.println("Summarise test===============");
        String[] inHeader = {"x", "z"};
        String[] sel = {"z"};

        Object[] inContent = {"a", 93.7, "b", 115.9, "c", 101.2, "a", 63.7, "b", 15.9, "c", 10.2 };

        DataFrame inDf = SimpleDataFrameKt.dataFrameOf(inHeader).invoke(inContent);
        DataFrame groupDf = inDf.groupBy("x");

        TableFormula tab = new TableFormula("sum_col", new Function2<DataFrame, DataFrame, Object>() {
            public Object invoke(DataFrame df, DataFrame dataFrame2) {
                return sum(df.get("z"), true);
            }
        });
        DataFrame sumDf = groupDf.summarize(tab);

        Extensions.print(inDf);
        Extensions.print(sumDf);

    }
}
