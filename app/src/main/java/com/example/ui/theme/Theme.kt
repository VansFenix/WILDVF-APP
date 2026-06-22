package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.graphics.Color

@Composable
fun MyApplicationTheme(
  vpnTheme: VpnTheme = VpnTheme.DARK,
  customAccent: Color? = null,
  content: @Composable () -> Unit,
) {
  val colorScheme = VpnThemeHelper.getColorScheme(vpnTheme, customAccent)

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
