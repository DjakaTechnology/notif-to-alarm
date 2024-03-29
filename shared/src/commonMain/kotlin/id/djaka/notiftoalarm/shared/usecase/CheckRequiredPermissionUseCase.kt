package id.djaka.notiftoalarm.shared.usecase

class CheckRequiredPermissionUseCase {
    fun invoke(): Result {
        return Result(
            isNotificationPermissionAllowed = checkIsNotificationPermissionAllowed(),
            isManageFullScreenIntentAllowed = checkIsManageFullScreenIntentAllowed()
        )
    }

    class Result(
        val isNotificationPermissionAllowed: Boolean,
        val isManageFullScreenIntentAllowed: Boolean
    )
}

internal expect fun checkIsNotificationPermissionAllowed(): Boolean

internal expect fun checkIsManageFullScreenIntentAllowed(): Boolean