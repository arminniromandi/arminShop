package ir.arminniromandi.arminshop

import ir.arminniromandi.arminshop.data.mapper.calculateFinalPrice
import ir.arminniromandi.arminshop.data.mapper.parseProductCategory
import ir.arminniromandi.arminshop.domain.model.ProductCategory
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.math.roundToInt

class BusinessLogicTest {

    @Test
    fun testDiscountCalculation() {
        // Base: 650,000 with 40% discount -> 390,000
        val price1 = calculateFinalPrice(650000L, 40)
        assertEquals(390000L, price1)

        // Base: 580,000 with 20% discount -> 464,000
        val price2 = calculateFinalPrice(580000L, 20)
        assertEquals(464000L, price2)

        // 0% discount -> original price
        val price3 = calculateFinalPrice(100000L, 0)
        assertEquals(100000L, price3)
    }

    @Test
    fun testAverageRatingRecalculation() {
        fun calculateNewRating(currentRating: Float, currentCount: Int, userScore: Float): Float {
            val newCount = currentCount + 1
            val totalScore = (currentRating * currentCount) + userScore
            return ((totalScore / newCount * 10).roundToInt() / 10f)
        }

        // Example: rating 4.5 with 1 review, new score 5.0 -> (4.5*1 + 5.0) / 2 = 9.5 / 2 = 4.75 -> 4.8
        val newRating1 = calculateNewRating(4.5f, 1, 5.0f)
        assertEquals(4.8f, newRating1, 0.01f)

        // Example: rating 4.0 with 3 reviews, new score 5.0 -> (12 + 5) / 4 = 17 / 4 = 4.25 -> 4.3
        val newRating2 = calculateNewRating(4.0f, 3, 5.0f)
        assertEquals(4.3f, newRating2, 0.01f)
    }

    @Test
    fun testCategoryParsing() {
        assertEquals(ProductCategory.MEN, parseProductCategory("مردانه"))
        assertEquals(ProductCategory.WOMEN, parseProductCategory("زنانه"))
        assertEquals(ProductCategory.KIDS, parseProductCategory("بچه گانه"))
        assertEquals(ProductCategory.KIDS, parseProductCategory("بچهگانه"))
        assertEquals(ProductCategory.SHOES, parseProductCategory("کفش"))
    }
}
