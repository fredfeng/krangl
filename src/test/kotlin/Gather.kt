/**
 * Created by yufeng on 9/11/17.
 */

import krangl.*


fun main(args: Array<String>) {

    // Create data-frame in memory
    val df: DataFrame = dataFrameOf("ID", "T", "P.1", "P.2", "Q.1")(
      1, 24.3, 10.2, 5.5, 4.5,
      2, 23.4, 10.4, 5.7, 3.2
    )
    df.print()

//    var TBL_3=df.gather("MORPH394","P",-"ID",-"T")
    var TBL_3=df.gather("MORPH394","P",-"T",-"ID")

    TBL_3.print()

    var TBL_1=TBL_3.separate("MORPH394",mutableListOf("MORPH469","Channel"), "\\.|_|\\|")
    TBL_1.print()

    var morpheus=TBL_1.select(-"MORPH469")
    morpheus.print()

}