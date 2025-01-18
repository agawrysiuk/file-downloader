package pl.agawrysiuk.filedownloader.annotations

import jakarta.validation.Constraint
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import jakarta.validation.Payload
import kotlin.reflect.KClass


class NullableNotBlankValidator : ConstraintValidator<NullableNotBlank, String?> {
    override fun isValid(value: String?, context: ConstraintValidatorContext?): Boolean {
        return value == null || value.trim().isNotEmpty()
    }
}

@MustBeDocumented
@Constraint(validatedBy = [NullableNotBlankValidator::class])
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class NullableNotBlank(
    val message: String = "must not be blank",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)
