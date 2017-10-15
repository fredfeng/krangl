/**
 * Created by yufeng on 9/11/17.
 */
import krangl.*
import java.util.*


fun main(args: Array<String>) {

    // Create data-frame in memory
    val df: DataFrame = dataFrameOf(
            "round", "var1", "var2", "nam", "val")(
            "round1", 22, 33, "foo", 0.169122009770945,
            "round2", 11, 44, "foo", 0.185708264587447,
            "round1", 22, 33, "bar", 0.124105813913047,
            "round2", 11, 44, "bar", 0.0325823465827852
    )

    val target: DataFrame = dataFrameOf(
            "nam", "val_round1", "val_round2", "var1_round1", "var1_round2", "var2_round1", "var2_round2")(
            "bar", 0.124105813913047, 0.0325823465827852, 22, 11, 33, 44,
            "foo", 0.169122009770945, 0.185708264587447, 22, 11, 33, 44
    )

    var TBL_3=df.gather("MORPH2","MORPH1",-"round",-"nam")
    TBL_3.print()

    var TBL_1=TBL_3.unite("MORPH159",mutableListOf("MORPH2","round"))
    TBL_1.print()

    var morpheus=TBL_1.spread("MORPH159","MORPH1")

    println(morpheus.cols)

    morpheus.print()

    target.print()
    println(hasSameContents(target, morpheus))

}