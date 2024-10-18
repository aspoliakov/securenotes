
import androidx.compose.ui.window.ComposeUIViewController
import com.aspoliakov.securenotes.MainAppComposable
import com.aspoliakov.securenotes.di.AppDI

fun MainViewController() = ComposeUIViewController {
    AppDI {
        MainAppComposable()
    }
}