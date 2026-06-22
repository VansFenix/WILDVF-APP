package com.example.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

enum class VpnTheme(val displayName: String) {
    DARK("Dark"),
    LIGHT("Light"),
    AMOLED("AMOLED"),
    OCEAN_BLUE("Ocean Blue"),
    EMERALD_GREEN("Emerald Green"),
    PURPLE_NEON("Purple Neon"),
    CYBERPUNK("Cyberpunk"),
    ARCTIC_WHITE("Arctic White"),
    SUNSET_ORANGE("Sunset Orange"),
    ROSE_PINK("Rose Pink"),
    MIDNIGHT_BLACK("Midnight Black")
}

object VpnThemeHelper {

    fun getColorScheme(theme: VpnTheme, customAccent: Color? = null): ColorScheme {
        return when (theme) {
            VpnTheme.DARK -> darkColorScheme(
                primary = customAccent ?: Color(0xFF3B82F6),
                secondary = Color(0xFF00E5FF),
                background = Color(0xFF05070A),
                surface = Color(0xFF0F131E),
                onBackground = Color(0xFFE2E8F0),
                onSurface = Color(0xFFF8FAFC),
                outline = Color(0xFF1E293B)
            )
            VpnTheme.LIGHT -> lightColorScheme(
                primary = customAccent ?: Color(0xFF1976D2),
                secondary = Color(0xFF1565C0),
                background = Color(0xFFF5F5F5),
                surface = Color(0xFFFFFFFF),
                onBackground = Color(0xFF212121),
                onSurface = Color(0xFF212121),
                outline = Color(0xFFBDBDBD)
            )
            VpnTheme.AMOLED -> darkColorScheme(
                primary = customAccent ?: Color(0xFFFFFFFF),
                secondary = Color(0xFFB0BEC5),
                background = Color(0xFF000000),
                surface = Color(0xFF121212),
                onBackground = Color(0xFFECEFF1),
                onSurface = Color(0xFFFFFFFF),
                outline = Color(0xFF2A2A2A)
            )
            VpnTheme.OCEAN_BLUE -> darkColorScheme(
                primary = customAccent ?: Color(0xFF00E5FF),
                secondary = Color(0xFF00B0FF),
                background = Color(0xFF0A192F),
                surface = Color(0xFF172A45),
                onBackground = Color(0xFFCCD6F6),
                onSurface = Color(0xFFE6F1FF),
                outline = Color(0xFF233554)
            )
            VpnTheme.EMERALD_GREEN -> darkColorScheme(
                primary = customAccent ?: Color(0xFF00E676),
                secondary = Color(0xFF00C853),
                background = Color(0xFF0A140F),
                surface = Color(0xFF12241A),
                onBackground = Color(0xFFE3F2FD),
                onSurface = Color(0xFFE8F5E9),
                outline = Color(0xFF1E3E2B)
            )
            VpnTheme.PURPLE_NEON -> darkColorScheme(
                primary = customAccent ?: Color(0xFFF50057),
                secondary = Color(0xFFD500F9),
                background = Color(0xFF0E001A),
                surface = Color(0xFF1A0033),
                onBackground = Color(0xFFF3E5F5),
                onSurface = Color(0xFFFAFAFA),
                outline = Color(0xFF330066)
            )
            VpnTheme.CYBERPUNK -> darkColorScheme(
                primary = customAccent ?: Color(0xFFFF007F),
                secondary = Color(0xFFFFEA00),
                background = Color(0xFF0D0A14),
                surface = Color(0xFF1B152A),
                onBackground = Color(0xFF00FFFF),
                onSurface = Color(0xFFFFFF00),
                outline = Color(0xFF38154D)
            )
            VpnTheme.ARCTIC_WHITE -> lightColorScheme(
                primary = customAccent ?: Color(0xFF0052D4),
                secondary = Color(0xFF4364F7),
                background = Color(0xFFECEFF1),
                surface = Color(0xFFFAFAFA),
                onBackground = Color(0xFF1C3144),
                onSurface = Color(0xFF1C3144),
                outline = Color(0xFFCFD8DC)
            )
            VpnTheme.SUNSET_ORANGE -> darkColorScheme(
                primary = customAccent ?: Color(0xFFFFA000),
                secondary = Color(0xFFFF5722),
                background = Color(0xFF1F120C),
                surface = Color(0xFF2D1E16),
                onBackground = Color(0xFFFFE0B2),
                onSurface = Color(0xFFFFF3E0),
                outline = Color(0xFF4E3629)
            )
            VpnTheme.ROSE_PINK -> darkColorScheme(
                primary = customAccent ?: Color(0xFFFF80AB),
                secondary = Color(0xFFFF4081),
                background = Color(0xFF1C1014),
                surface = Color(0xFF2C1E23),
                onBackground = Color(0xFFFCE4EC),
                onSurface = Color(0xFFFFF1F5),
                outline = Color(0xFF4A2A35)
            )
            VpnTheme.MIDNIGHT_BLACK -> darkColorScheme(
                primary = customAccent ?: Color(0xFFECEFF1),
                secondary = Color(0xFF78909C),
                background = Color(0xFF050607),
                surface = Color(0xFF0E1113),
                onBackground = Color(0xFFCFD8DC),
                onSurface = Color(0xFFFFFFFF),
                outline = Color(0xFF21262B)
            )
        }
    }
}
