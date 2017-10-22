package krangl.test

import io.kotlintest.matchers.have
import io.kotlintest.specs.FlatSpec
import krangl.*
import org.apache.commons.csv.CSVFormat


val irisData = DataFrame.fromCSV(DataFrame::class.java.getResourceAsStream("data/iris.txt"), format = CSVFormat.TDF.withHeader())
val flights = DataFrame.fromCSV(DataFrame::class.java.getResourceAsStream("data/nycflights.tsv.gz"), format = CSVFormat.TDF.withHeader(), isCompressed = true)

/**
An example data frame with 83 rows and 11 variables

This is an updated and expanded version of the mammals sleep dataset. Updated sleep times and weights were taken from V. M. Savage and G. B. West. A quantitative, theoretical framework for understanding mammalian sleep. Proceedings of the National Academy of Sciences, 104 (3):1051-1056, 2007.

Additional variables order, conservation status and vore were added from wikipedia.
- name. common name
- genus.
- vore. carnivore, omnivore or herbivore?
- order.
- conservation. the conservation status of the animal
- sleep\_total. total amount of sleep, in hours
- sleep\_rem. rem sleep, in hours
- sleep\_cycle. length of sleep cycle, in hours
- awake. amount of time spent awake, in hours
- brainwt. brain weight in kilograms
- bodywt. body weight in kilograms
 */
val sleepData = DataFrame.fromCSV(DataFrame::class.java.getResourceAsStream("data/msleep.csv"), CSVFormat.DEFAULT.withHeader())


class SelectTest : FlatSpec() { init {

    "it" should "select with regex" {
        sleepData.select({ endsWith("wt") }).ncol shouldBe 2
        sleepData.select({ endsWith("wt") }).ncol shouldBe 2
        sleepData.select({ startsWith("sleep") }).ncol shouldBe 3
        sleepData.select({ oneOf("conservation", "foobar", "order") }).ncol shouldBe 2
    }


    "it" should "select non-existing column" {
        try {
            sleepData.select("foobar")
            fail("foobar should not be selectable")
        } catch(t: Throwable) {
            // todo expect more descriptive exception here. eg. ColumnDoesNotExistException
        }
    }


    "it" should "select no columns" {
        try {
            sleepData.select(listOf())
            fail("should complain about mismatching selector array dimensionality")
        } catch(t: Throwable) {
        }

        sleepData.select(*arrayOf<String>()).ncol shouldBe 0
    }


    "it" should "select same columns twice" {
        // double selection is flattend out as in dplyr:  iris %>% select(Species, Species) %>% glimpse

        shouldThrow<IllegalArgumentException> {
            sleepData.select("name", "vore", "name").ncol shouldBe 2
        }

        sleepData.select("name", "vore").ncol shouldBe 2
    }


    "it" should "do a negative selection" {
        sleepData.select(-"name", -"vore").apply {
            names.contains("name") shouldBe false
            names.contains("vore") shouldBe false

            // ensure preserved order of remaining columns
            sleepData.names.minus(arrayOf("name", "vore")) shouldEqual names
        }

    }

    // krangl should prevent that negative and positive selections are combined in a single select() statement
    "it" should "do combined negative and positive selection" {
        // cf.  iris %>% select(ends_with("Length"), - Petal.Length) %>% glimpse()
        // not symmetric:  iris %>% select(- Petal.Length, ends_with("Length")) %>% glimpse()
        //  iris %>% select(-Petal.Length, ends_with("Length")) %>% glimpse()
        irisData.select({ endsWith("Length") }, -"Petal.Length").apply {
            names shouldEqual listOf("Sepal.Length")
        }
    }
}
}


class MutateTest : FlatSpec() { init {
    "it" should "rename columns and preserve their positions" {
        sleepData.rename("vore" to "new_vore", "awake" to "awa2").apply {
            glimpse()
            names.contains("vore") shouldBe false
            names.contains("new_vore") shouldBe true

            // column renaming should preserve positions
            names.indexOf("new_vore") shouldEqual sleepData.names.indexOf("vore")

            // renaming should not affect column or row counts
            nrow == sleepData.nrow
            ncol == sleepData.ncol
        }
    }

    "it" should "allow dummy rename" {
        sleepData.rename("vore" to "vore").names shouldBe sleepData.names
    }

    "it" should "mutate existing columns while keeping their posi" {
        irisData.mutate("Sepal.Length" to { it["Sepal.Length"] + 10 }).names shouldBe irisData.names
    }

    "it" should "allow to use a new column in the same mutate call" {
        sleepData.mutate(
                "vore_new" to { it["vore"] },
                "vore_first_char" to { it["vore"].asStrings().ignoreNA { this.toList().first().toString() } }
        )
    }
}
}


class FilterTest : FlatSpec() { init {
    "it" should "head tail and slic should extract data as expextd" {
        // todo test that the right portions were extracted and not just size
        sleepData.head().nrow shouldBe  5
        sleepData.tail().nrow shouldBe  5
        sleepData.slice(1, 3, 5).nrow shouldBe  3
    }

    "it" should "filter in empty table" {
        sleepData
                .filter { it["name"] eq "foo" }
                // refilter on empty one
                .filter { it["name"] eq "bar" }
    }

    "it" should "sub sample data" {

//        sleepData.count("vore").print()

        // fixed sampling should work
        sleepData.sampleN(2).nrow shouldBe 2
        sleepData.sampleN(1000, replace = true).nrow shouldBe 1000 // oversampling

        //  fractional sampling should work as well
        sleepData.sampleFrac(0.3).nrow shouldBe Math.round(sleepData.nrow * 0.3).toInt()
        sleepData.sampleFrac(0.3, replace = true).nrow shouldBe Math.round(sleepData.nrow * 0.3).toInt()
        sleepData.sampleFrac(2.0, replace = true).nrow shouldBe sleepData.nrow * 2 // oversampling

        // also test boundary conditions
        sleepData.sampleN(0).nrow shouldBe 0
        sleepData.sampleN(0, replace = true).nrow shouldBe 0
        sleepData.sampleFrac(0.0).nrow shouldBe 0
        sleepData.sampleFrac(0.0, replace = true).nrow shouldBe 0

        sleepData.sampleN(sleepData.nrow).nrow shouldBe sleepData.nrow
        sleepData.sampleN(sleepData.nrow, replace = true).nrow shouldBe sleepData.nrow
        sleepData.sampleFrac(1.0).nrow shouldBe sleepData.nrow
        sleepData.sampleFrac(1.0, replace = true).nrow shouldBe sleepData.nrow


        // make sure that invalid sampling parameters throw exceptions
        shouldThrow<IllegalArgumentException> { sleepData.sampleN(-1) }
        shouldThrow<IllegalArgumentException> { sleepData.sampleN(-1, replace = true) }
        shouldThrow<IllegalArgumentException> { sleepData.sampleFrac(-.3) }
        shouldThrow<IllegalArgumentException> { sleepData.sampleFrac(-.3, replace = true) }

        // oversampling without replacement should not work
        shouldThrow<IllegalArgumentException> { sleepData.sampleN(1000) }
        shouldThrow<IllegalArgumentException> { sleepData.sampleFrac(1.3) }


        // fixed sampling of grouped data should be done per group
        val groupCounts = sleepData.groupBy("vore").sampleN(2).count("vore")
        groupCounts["n"].asInts().distinct().apply {
            size shouldBe 1
            first() shouldBe 2
        }

        //  fractional sampling of grouped data should be done per group
        sleepData
                .groupBy("vore")
                .sampleFrac(0.5)
                .count("vore")
                .filter({ it["vore"] eq "omni" })
                .apply {
                    this["n"].asInts().first() shouldBe 10
                }
    }

}
}


class SummarizeTest : FlatSpec() { init {
    "it" should "fail if summaries are not scalar values" {
        shouldThrow<NonScalarValueException> {
            sleepData.summarize("foo", { listOf("a", "b", "c") })
        }
        shouldThrow<NonScalarValueException> {
            sleepData.summarize("foo", { BooleanArray(12) })
        }
    }

    "it" should "should allow complex objects as summaries" {
        class Something {
            override fun toString(): String = "Something(${hashCode()}"
        }

        sleepData.groupBy("vore").summarize("foo" to { Something() }, "bar" to { Something() }).print()
    }
}
}


class EmptyTest : FlatSpec() { init {
    "it" should "handle  empty (row and column-empty) data-frames in all operations" {
        SimpleDataFrame().apply {
            // structure
            ncol shouldBe 0
            nrow shouldBe 0
            rows.toList() should have size 0
            cols.toList() should have size 0

            // rendering
            glimpse()
            print()

            select(emptyList()) // will output warning
            // core verbs
            filter { BooleanArray(0) }
            mutate("foo", { "bar" })
            summarize("foo" to { "bar" })
            arrange()

            // grouping
            (groupBy() as GroupedDataFrame).groups()
        }
    }
}
}

class GroupedDataTest : FlatSpec() { init {

    /** dplyr considers NA as a group and krangl should do the same

    ```
    require(dplyr)

    iris
    iris$Species[1] <- NA

    ?group_by
    grpdIris <- group_by(iris, Species)
    grpdIris %>% slice(1)
    ```
     */
    "it" should "allow for NA as a group value" {

        // 1) test single attribute grouping with NA
        (sleepData.groupBy("vore") as GroupedDataFrame).groups().nrow shouldBe 5

        // 2) test multi-attribute grouping with NA in one or all attributes
//        (sleepData.groupBy("vore") as GroupedDataFrame).groups().nrow shouldBe 6
        //todo implement me
    }


    "it" should "count group sizes and report distinct rows in a table" {
        // 1) test single attribute grouping with NA
        sleepData.count("vore").apply {
            print()
            ncol shouldBe 2
            nrow shouldBe 5
        }

        sleepData.distinct("vore", "order").apply {
            print()
            nrow shouldBe 32
            ncol shouldBe 11
        }
    }


    "it" should "should auto-select grouping attributes from a grouped dataframe"{
//        flights.glimpse()
        val subFlights = flights
                .groupBy("year", "month", "day")
//                .select({ range("year", "day") }, { oneOf("arr_delay", "dep_delay") })
                .select("arr_delay", "dep_delay", "year")

        subFlights.apply {
            ncol shouldBe 5
            (this is GroupedDataFrame) shouldBe true
            (this as GroupedDataFrame).groups.toList().first().df.ncol shouldBe 5
        }

    }


    "it" should "calculate same group hash irrespective of column order"{
//        flights.glimpse()

        var dfA: DataFrame = dataFrameOf(
                "first_name", "last_name", "age", "weight")(
                "Max", "Doe", 23, 55,
                "Franz", "Smith", 23, 88,
                "Horst", "Keanes", 12, 82
        )

        val dfB = dfA.select("age", "last_name", "weight", "first_name")

        // by joining with multiple attributes we inherentily group (which is the actual test
        val dummyJoin = dfA.leftJoin(dfB, by = listOf("last_name", "first_name"))

        dummyJoin.apply {
            nrow shouldBe 3
        }
    }

}
}

