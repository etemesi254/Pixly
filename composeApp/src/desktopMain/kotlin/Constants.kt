import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.platform.Font
import androidx.compose.ui.unit.sp




val poppinsFamily = FontFamily(
    Font(resource = "fonts/Poppins/Poppins-Light.ttf", weight = FontWeight.Light),
    Font(resource = "fonts/Poppins/Poppins-Regular.ttf", weight = FontWeight.Normal),
    Font(resource = "fonts/Poppins/Poppins-Bold.ttf", weight = FontWeight.Bold),
    Font(resource = "fonts/Poppins/Poppins-Black.ttf", weight = FontWeight.Black),
    Font(resource = "fonts/Poppins/Poppins-BlackItalic.ttf", weight = FontWeight.Black, style = FontStyle.Italic),
    Font(resource = "fonts/Poppins/Poppins-ExtraBold.ttf", weight = FontWeight.ExtraBold),

    Font(
        resource = "fonts/Poppins/Poppins-ExtraBoldItalic.ttf",
        weight = FontWeight.ExtraBold,
        style = FontStyle.Italic
    )
)


val poppinsTypography = Typography(
    defaultFontFamily = poppinsFamily,
    h1 = TextStyle(
        fontWeight = FontWeight.Light,
        fontSize = 96.sp,
        letterSpacing = (-1.5).sp
    ),
    h2 = TextStyle(
        fontWeight = FontWeight.Light,
        fontSize = 60.sp,
        letterSpacing = (-0.5).sp
    )
)