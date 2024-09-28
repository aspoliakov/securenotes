import androidx.compose.ui.window.ComposeUIViewController
import com.aspoliakov.securenotes.MainAppComposable
import com.aspoliakov.securenotes.di.AppDI
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.initialize

fun MainViewController() = ComposeUIViewController {
    AppDI {
        Firebase.initialize()
        MainAppComposable()
    }
}