package krangl

/**
 * Created by yufeng on 9/24/17.
 */

import krangl.*


fun main(args: Array<String>) {

    // Create data-frame in memory

    val df: DataFrame = dataFrameOf(
            "first_name", "last_name", "age", "weight")(
            "Max", "Doe", 23, 55,
            "Franz", "Smith", 23, 88,
            "Horst", "Keanes", 12, 82
    )


    // Add columns with mutate
    // by adding constant values as new column
    val df1 = df.mutate("salary_category", { 3 })
    df1.print()
    // by doing basic column arithmetics
    val df2 = df.mutate("sum", { it["age"] + it["weight"]})
    df2.print()
    // Note: krangl dataframes are immutable so we need to (re)assign results to preserve changes.
    val df3 = df.mutate("full_name", { it["first_name"]  + it["last_name"] })

    df3.print()

    val df4 = df.summarize("mean_col" to {it["age"].mean()})

    df4.print()


}