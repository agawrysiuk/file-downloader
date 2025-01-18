package pl.agawrysiuk.filedownloader.annotations

import jakarta.validation.Validation
import jakarta.validation.Validator
import jakarta.validation.ValidatorFactory
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class NullableNotBlankValidatorTest {

    private val validatorFactory: ValidatorFactory = Validation.buildDefaultValidatorFactory()
    private val validator: Validator = validatorFactory.validator

    data class TestClass(
        @field:NullableNotBlank
        val field: String?
    )

    @Test
    fun `test null value`() {
        val invalidNullValue = TestClass(null)
        val violationsNull = validator.validate(invalidNullValue)
        assertTrue(violationsNull.isEmpty())
    }

    @Test
    fun `test non-blank valid value`() {
        val validValue = TestClass("Valid String")
        val violationsValid = validator.validate(validValue)
        assertTrue(violationsValid.isEmpty())
    }

    @Test
    fun `test empty string invalid value`() {
        val invalidEmptyValue = TestClass("   ")
        val violationsEmpty = validator.validate(invalidEmptyValue)
        assertFalse(violationsEmpty.isEmpty())
    }

    @Test
    fun `test blank string invalid value`() {
        val invalidBlankValue = TestClass("   ")
        val violationsBlank = validator.validate(invalidBlankValue)
        assertFalse(violationsBlank.isEmpty())
    }
}
