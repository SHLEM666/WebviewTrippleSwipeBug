package com.example.webviewtrippleswipebug

import android.annotation.SuppressLint
import android.os.Bundle
import android.content.Context
import android.util.Log
import android.view.MotionEvent
import android.webkit.WebView
import android.webkit.WebView.setWebContentsDebuggingEnabled
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.compose.ui.viewinterop.AndroidView
import org.mozilla.geckoview.GeckoRuntime
import org.mozilla.geckoview.GeckoSession
import org.mozilla.geckoview.GeckoView

class MainActivity : ComponentActivity() {
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val data = """
            <!DOCTYPE html>
            <html>
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <body>
                    Non eram nescius, Brute, cum, quae summis ingeniis exquisitaque doctrina philosophi Graeco sermone tractavissent, ea Latinis litteris mandaremus, fore ut hic noster labor in varias reprehensiones incurreret; nam quibusdam, et iis quidem non admodum indoctis, totum hoc displicet philosophari. Quidam autem non tam id reprehendunt, si remissius agatur, sed tantum studium tamque multam operam ponendam in eo non arbitrantur. Erunt etiam, et hi quidem eruditi Graecis litteris, contemnentes Latinas, qui se dicant in Graecis legendis operam malle consumere. Postremo aliquos futuros suspicor, qui me ad alias litteras vocent, genus hoc scribendi, etsi sit elegans, personae tamen et dignitatis esse negent.
                    Contra quos omnes dicendum breviter existimo: quamquam philosophiae quidem vituperatoribus satis responsum est eo libro, quo a nobis philosophia defensa et collaudata est, cum esset accusata et vituperata ab Hortensio. Qui liber cum et tibi probatus videretur et iis, quos ego posse iudicare arbitrarer, plura suscepi, veritus ne movere hominum studia viderer, retinere non posse. Qui autem, si maxime hoc placeat, moderatius tamen id volunt fieri, difficilem quandam temperantiam postulant in eo, quod semel admissum coerceri reprimique non potest, ut propemodum iustioribus utamur illis, qui omnino avocent a philosophia, quam his, qui rebus infinitis modum constituant in reque eo meliore, quo maior sit, mediocritatem desiderent.
                    Sive enim ad sapientiam perveniri potest, non paranda nobis solum ea, sed fruenda etiam est; sive hoc difficile est, tamen nec modus est ullus investigandi veri, nisi inveneris, et quaerendi defetigatio turpis est, cum id, quod quaeritur, sit pulcherrimum. Etenim si delectamur, cum scribimus, quis est tam invidus, qui ab eo nos abducat? sin laboramus, quis est, qui alienae modum statuat industriae?
                    <script>
                        [
                            "pointerdown",
                            "pointerup",
                        ].forEach(type => {
                            document.addEventListener(type, e => {
                                console.log(type);
                                document.body.innerHTML += "<br>" + type;
                                window.scrollTo(0, document.body.scrollHeight);
                            }, true);
                        });
                    </script>
                </body>
            </html>
        """.trimIndent()

        val webView = DebugWebView(this).apply {
            settings.javaScriptEnabled = true
            setWebContentsDebuggingEnabled(true)
            loadData(
                data,
                "text/html",
                "UTF-8"
            )
        }

        val runtime = GeckoRuntime.create(this)
        val session = GeckoSession().apply {
            open(runtime)
            loadUri("data:text/html,$data")
        }
        val geckoView = GeckoView(this).apply {
            setSession(session)
        }

        // bug occurs
        setContent { AndroidView( factory = { webView } ) }

        // bug does not occur
//        setContent { AndroidView( factory = { geckoView } ) }
//        setContentView(webView)
//        setContentView(geckoView)

        this.onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    webView.reload()
                    session.reload()
                }
            }
        )
    }
}

class DebugWebView(context: Context) : WebView(context) {
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        Log.d(
            "pointers",
            (MotionEvent.actionToString(ev.actionMasked)
                    + " pointers=" + ev.pointerCount)
        )
        return super.dispatchTouchEvent(ev)
    }
}