package com.example.ui

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.VpnTheme
import com.example.ui.theme.VpnThemeHelper
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlinx.coroutines.launch
import kotlin.math.max

@Composable
fun VpnAppContent(viewModel: VpnViewModel) {
    val currentTheme by viewModel.currentTheme.collectAsState()
    val customAccent by viewModel.customAccentColor.collectAsState()
    val lang by viewModel.currentLanguage.collectAsState()

    var activeTab by remember { mutableStateOf("dashboard") }

    // Setup active state wrapper
    MyApplicationTheme(vpnTheme = currentTheme, customAccent = customAccent) {
        val colorScheme = MaterialTheme.colorScheme
        
        Scaffold(
            bottomBar = {
                VpnBottomNavigation(
                    activeTab = activeTab,
                    onTabSelected = { activeTab = it },
                    lang = lang,
                    colorScheme = colorScheme
                )
            },
            modifier = Modifier.fillMaxSize(),
            containerColor = colorScheme.background
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Background visual asset
                Image(
                    painter = painterResource(id = R.drawable.wildvf_background),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    alpha = 0.18f
                )

                AnimatedContent(
                    targetState = activeTab,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(250)) togetherWith fadeOut(animationSpec = tween(200))
                    },
                    modifier = Modifier.fillMaxSize(),
                    label = "tabTransition"
                ) { targetTab ->
                    when (targetTab) {
                        "dashboard" -> DashboardTab(viewModel, lang)
                        "servers" -> ServersTab(viewModel, lang)
                        "subscriptions" -> SubscriptionsTab(viewModel, lang)
                        "analytics" -> AnalyticsTab(viewModel, lang)
                        "settings" -> SettingsTab(viewModel, lang)
                        "logs" -> LogsTab(viewModel, lang)
                    }
                }
            }
        }
    }
}

@Composable
fun VpnBottomNavigation(
    activeTab: String,
    onTabSelected: (String) -> Unit,
    lang: VpnLocalization.Language,
    colorScheme: ColorScheme
) {
    NavigationBar(
        containerColor = colorScheme.surface.copy(alpha = 0.85f),
        tonalElevation = 8.dp,
        modifier = Modifier
            .windowInsetsPadding(WindowInsets.navigationBars)
            .testTag("bottom_nav")
    ) {
        val items = listOf(
            Triple("dashboard", Icons.Default.Home, VpnLocalization.getString("tab_dashboard", lang)),
            Triple("servers", Icons.Default.Dns, VpnLocalization.getString("tab_servers", lang)),
            Triple("subscriptions", Icons.Default.CardMembership, VpnLocalization.getString("tab_subscriptions", lang)),
            Triple("analytics", Icons.Default.BarChart, VpnLocalization.getString("tab_analytics", lang)),
            Triple("logs", Icons.Default.Terminal, VpnLocalization.getString("tab_logs", lang)),
            Triple("settings", Icons.Default.Settings, VpnLocalization.getString("tab_settings", lang))
        )

        items.forEach { (tab, icon, label) ->
            NavigationBarItem(
                selected = activeTab == tab,
                onClick = { onTabSelected(tab) },
                icon = { Icon(imageVector = icon, contentDescription = label) },
                label = { Text(text = label, maxLines = 1, overflow = TextOverflow.Ellipsis, fontSize = 11.sp) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = colorScheme.primary,
                    selectedTextColor = colorScheme.primary,
                    indicatorColor = colorScheme.primary.copy(alpha = 0.15f),
                    unselectedIconColor = colorScheme.onSurface.copy(alpha = 0.6f),
                    unselectedTextColor = colorScheme.onSurface.copy(alpha = 0.6f)
                ),
                modifier = Modifier.testTag("nav_tab_$tab")
            )
        }
    }
}

// 1. DASHBOARD SCREEN
@Composable
fun DashboardTab(viewModel: VpnViewModel, lang: VpnLocalization.Language) {
    val connectionState by viewModel.connectionState.collectAsState()
    val activeServer by viewModel.activeServer.collectAsState()
    val currentIp by viewModel.currentIp.collectAsState()
    val dlSpeed by viewModel.downloadSpeed.collectAsState()
    val ulSpeed by viewModel.uploadSpeed.collectAsState()
    val connectionTime by viewModel.connectionTime.collectAsState()
    val routingMode by viewModel.currentRouting.collectAsState()

    val context = LocalContext.current
    val colorScheme = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        // App Header
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(colorScheme.primary.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.VpnLock,
                    contentDescription = null,
                    tint = colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = VpnLocalization.getString("app_title", lang),
                fontSize = 26.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp,
                color = colorScheme.onBackground
            )
        }

        Spacer(modifier = Modifier.height(35.dp))

        // Large Circular Connection Trigger
        val infiniteTransition = rememberInfiniteTransition(label = "pulse")
        val pulseScale by infiniteTransition.animateFloat(
            initialValue = 1.0f,
            targetValue = if (connectionState == ConnectionState.CONNECTED) 1.15f else 1.06f,
            animationSpec = infiniteRepeatable(
                animation = tween(1500, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "pulse_animation"
        )

        val circleBg = when (connectionState) {
            ConnectionState.CONNECTED -> colorScheme.primary
            ConnectionState.CONNECTING -> Color(0xFFFFA000)
            ConnectionState.DISCONNECTING -> Color(0xFFD32F2F)
            ConnectionState.DISCONNECTED -> colorScheme.onSurface.copy(alpha = 0.1f)
        }

        Box(
            modifier = Modifier
                .size(250.dp)
                .drawBehind {
                    if (connectionState == ConnectionState.CONNECTED || connectionState == ConnectionState.CONNECTING) {
                        drawCircle(
                            color = circleBg.copy(alpha = 0.12f),
                            radius = size.minDimension / 2 * pulseScale
                        )
                    }
                }
                .pointerInput(Unit) {}
                .testTag("connect_container"),
            contentAlignment = Alignment.Center
        ) {
            Button(
                onClick = {
                    if (activeServer == null) {
                        Toast.makeText(context, "Please select a server first / Пожалуйста, выберите сервер", Toast.LENGTH_SHORT).show()
                    } else {
                        viewModel.toggleConnection(activeServer)
                    }
                },
                modifier = Modifier
                    .size(175.dp)
                    .testTag("connect_button"),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = circleBg,
                    contentColor = if (connectionState == ConnectionState.CONNECTED) colorScheme.background else colorScheme.onBackground
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    val powerIconTint = if (connectionState == ConnectionState.CONNECTED) colorScheme.background else colorScheme.primary
                    Icon(
                        imageVector = Icons.Default.PowerSettingsNew,
                        contentDescription = "Trigger button",
                        modifier = Modifier.size(60.dp),
                        tint = powerIconTint
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = when (connectionState) {
                            ConnectionState.CONNECTED -> VpnLocalization.getString("disconnect", lang)
                            ConnectionState.CONNECTING -> VpnLocalization.getString("connecting", lang)
                            ConnectionState.DISCONNECTING -> VpnLocalization.getString("disconnecting", lang)
                            ConnectionState.DISCONNECTED -> VpnLocalization.getString("connect", lang)
                        },
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        letterSpacing = 1.sp,
                        color = if (connectionState == ConnectionState.CONNECTED) colorScheme.background else colorScheme.onSurface
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(15.dp))

        // Connection State Label
        Text(
            text = when (connectionState) {
                ConnectionState.CONNECTED -> VpnLocalization.getString("connected", lang)
                ConnectionState.CONNECTING -> VpnLocalization.getString("connecting", lang)
                ConnectionState.DISCONNECTING -> VpnLocalization.getString("disconnecting", lang)
                ConnectionState.DISCONNECTED -> VpnLocalization.getString("disconnected", lang)
            },
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = when (connectionState) {
                ConnectionState.CONNECTED -> colorScheme.primary
                ConnectionState.CONNECTING -> Color(0xFFFFA000)
                else -> colorScheme.onBackground.copy(alpha = 0.5f)
            },
            letterSpacing = 1.5.sp
        )

        Spacer(modifier = Modifier.height(30.dp))

        // Active Server Quick Selector Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .clickable {
                    Toast.makeText(context, "Switch to 'Servers' tab to select a connection node / Перейдите на вкладку 'Серверы' для выбора", Toast.LENGTH_SHORT).show()
                }
                .testTag("active_server_card"),
            colors = CardDefaults.cardColors(containerColor = colorScheme.surface.copy(alpha = 0.75f)),
            border = BorderStroke(1.dp, colorScheme.outline.copy(alpha = 0.15f))
        ) {
            ListItem(
                headlineContent = {
                    Text(
                        text = activeServer?.name ?: "Select Preferred Node",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = colorScheme.onSurface
                    )
                },
                supportingContent = {
                    Text(
                        text = activeServer?.let { "${it.protocol} • ${it.address}:${it.port}" } ?: "No active tunnel selected",
                        fontSize = 12.sp,
                        color = colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                },
                leadingContent = {
                    val cc = activeServer?.countryCode ?: "GL"
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .background(colorScheme.primary.copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = getEmojiFlag(cc), fontSize = 22.sp)
                    }
                },
                trailingContent = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (activeServer != null) {
                            Text(text = "${activeServer?.pingMs ?: 45} ms", style = MaterialTheme.typography.bodySmall)
                            Spacer(modifier = Modifier.width(6.dp))
                        }
                        Icon(imageVector = Icons.Default.ChevronRight, contentDescription = "Choose Server")
                    }
                },
                colors = ListItemDefaults.colors(containerColor = Color.Transparent)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 2x2 Glassmorphism Metrics Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .height(260.dp)
                .fillMaxWidth(),
            userScrollEnabled = false,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                MetricCard(
                    title = VpnLocalization.getString("current_ip", lang),
                    value = currentIp,
                    icon = Icons.Default.Language,
                    color = colorScheme.primary,
                    colorScheme = colorScheme
                )
            }
            item {
                val formattedTime = formatElapsedTime(connectionTime)
                MetricCard(
                    title = VpnLocalization.getString("elapsed_time", lang),
                    value = formattedTime,
                    icon = Icons.Default.Timer,
                    color = Color(0xFF00E676),
                    colorScheme = colorScheme
                )
            }
            item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(2) }) {
                RealTimeTrafficBentoCard(
                    dlSpeed = dlSpeed,
                    ulSpeed = ulSpeed,
                    colorScheme = colorScheme,
                    isConnected = connectionState == ConnectionState.CONNECTED
                )
            }
        }

        Spacer(modifier = Modifier.height(15.dp))

        // Mini Routing Indicator
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = colorScheme.surface.copy(alpha = 0.4f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.MergeType, contentDescription = null, tint = colorScheme.primary, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = VpnLocalization.getString("routing_mode", lang), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
                Text(
                    text = routingMode.name,
                    color = colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    colorScheme: ColorScheme
) {
    Card(
        modifier = Modifier.fillMaxSize(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface.copy(alpha = 0.65f)),
        border = BorderStroke(1.dp, colorScheme.outline.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = title,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colorScheme.onSurface.copy(alpha = 0.5f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color.copy(alpha = 0.8f),
                    modifier = Modifier.size(16.dp)
                )
            }
            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                color = colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun RealTimeTrafficBentoCard(
    dlSpeed: Float,
    ulSpeed: Float,
    colorScheme: ColorScheme,
    isConnected: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth().height(115.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface.copy(alpha = 0.65f)),
        border = BorderStroke(1.dp, colorScheme.outline.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1.2f),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "REAL-TIME TRAFFIC",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onSurface.copy(alpha = 0.4f),
                    letterSpacing = 1.sp
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Column {
                        Text(
                            text = "Download",
                            fontSize = 10.sp,
                            color = Color(0xFF3B82F6),
                            fontWeight = FontWeight.Bold
                        )
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = String.format("%.1f", dlSpeed),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = colorScheme.onSurface
                            )
                            Text(
                                text = " Mb/s",
                                fontSize = 10.sp,
                                color = colorScheme.onSurface.copy(alpha = 0.4f),
                                modifier = Modifier.padding(bottom = 2.dp)
                            )
                        }
                    }
                    
                    Box(modifier = Modifier.width(1.dp).height(30.dp).background(colorScheme.outline.copy(alpha = 0.1f)))
                    
                    Column {
                        Text(
                            text = "Upload",
                            fontSize = 10.sp,
                            color = Color(0xFF00E5FF),
                            fontWeight = FontWeight.Bold
                        )
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = String.format("%.1f", ulSpeed),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = colorScheme.onSurface
                            )
                            Text(
                                text = " Mb/s",
                                fontSize = 10.sp,
                                color = colorScheme.onSurface.copy(alpha = 0.4f),
                                modifier = Modifier.padding(bottom = 2.dp)
                            )
                        }
                    }
                }
            }
            
            Row(
                modifier = Modifier.weight(0.8f).height(60.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.End),
                verticalAlignment = Alignment.Bottom
            ) {
                val heights = listOf(0.3f, 0.5f, 0.85f, 0.45f, 0.65f, 0.25f, 0.75f, 0.5f)
                heights.forEachIndexed { index, baseHeight ->
                    val infiniteTransition = rememberInfiniteTransition(label = "eq_$index")
                    val pulseHeight by infiniteTransition.animateFloat(
                        initialValue = baseHeight,
                        targetValue = if (isConnected) (baseHeight + 0.35f).coerceAtMost(1f) else 0.1f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(
                                durationMillis = 400 + (index * 70),
                                easing = FastOutLinearInEasing
                            ),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "eq_anim_$index"
                    )
                    
                    val barColor = if (index % 2 == 0) Color(0xFF3B82F6) else Color(0xFF00E5FF)
                    
                    Box(
                        modifier = Modifier
                            .width(5.dp)
                            .fillMaxHeight(fraction = pulseHeight)
                            .background(
                                color = barColor.copy(alpha = if (isConnected) 0.85f else 0.2f),
                                shape = RoundedCornerShape(10.dp)
                            )
                    )
                }
            }
        }
    }
}

// 2. SERVER SCREEN
@Composable
fun ServersTab(viewModel: VpnViewModel, lang: VpnLocalization.Language) {
    val serversList by viewModel.servers.collectAsState()
    val searchQuery by viewModel.serverSearchQuery.collectAsState()
    val favoritesOnly by viewModel.favoritesOnly.collectAsState()
    val activeServer by viewModel.activeServer.collectAsState()
    val selectCountryCode by viewModel.selectedCountryFilter.collectAsState()

    val colorScheme = MaterialTheme.colorScheme
    val context = LocalContext.current

    var showAddDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = VpnLocalization.getString("tab_servers", lang),
            fontSize = 22.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier.fillMaxWidth(),
            color = colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(15.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.setServerSearchQuery(it) },
            placeholder = { Text(text = VpnLocalization.getString("search_servers", lang)) },
            leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Search") },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.setServerSearchQuery("") }) {
                        Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear")
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("server_search_input"),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = colorScheme.outline.copy(alpha = 0.2f),
                focusedBorderColor = colorScheme.primary
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Controls (Filter Favorites, Ping Diagnose, Add Node)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Favorites filter pill
            FilterChip(
                selected = favoritesOnly,
                onClick = { viewModel.setFavoritesOnly(!favoritesOnly) },
                label = { Text(VpnLocalization.getString("favorites", lang)) },
                leadingIcon = {
                    Icon(
                        imageVector = if (favoritesOnly) Icons.Default.Star else Icons.Default.StarOutline,
                        contentDescription = "Favs"
                    )
                },
                modifier = Modifier.testTag("favorite_filter_pill")
            )

            Row {
                IconButton(
                    onClick = { viewModel.pingAllServers() },
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("ping_all_button")
                ) {
                    Icon(imageVector = Icons.Default.Bolt, contentDescription = "Ping", tint = colorScheme.primary)
                }

                Spacer(modifier = Modifier.width(6.dp))

                Button(
                    onClick = { showAddDialog = true },
                    modifier = Modifier
                        .height(40.dp)
                        .testTag("add_server_fab"),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = VpnLocalization.getString("add_server", lang), fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(15.dp))

        // Filter lists
        val filteredServers = serversList.filter {
            val matchesQuery = it.name.contains(searchQuery, ignoreCase = true) || 
                               it.address.contains(searchQuery, ignoreCase = true) || 
                               it.protocol.contains(searchQuery, ignoreCase = true)
            val matchesFav = !favoritesOnly || it.isFavorite
            matchesQuery && matchesFav
        }

        if (filteredServers.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(imageVector = Icons.Default.Dns, contentDescription = null, modifier = Modifier.size(60.dp), tint = colorScheme.onSurface.copy(alpha = 0.2f))
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(text = "No servers matching requirements.", color = colorScheme.onSurface.copy(alpha = 0.5f))
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .testTag("servers_list"),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredServers) { server ->
                    val isSelected = activeServer?.id == server.id
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.toggleConnection(server) }
                            .testTag("server_item_${server.id}"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) colorScheme.primary.copy(alpha = 0.15f) else colorScheme.surface.copy(alpha = 0.65f)
                        ),
                        border = BorderStroke(
                            width = if (isSelected) 1.5.dp else 1.dp,
                            color = if (isSelected) colorScheme.primary else colorScheme.outline.copy(alpha = 0.1f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Text(
                                    text = getEmojiFlag(server.countryCode),
                                    fontSize = 24.sp,
                                    modifier = Modifier.padding(end = 12.dp)
                                )
                                Column {
                                    Text(
                                        text = server.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .background(colorScheme.primary.copy(alpha = 0.12f), RoundedCornerShape(4.dp))
                                                .padding(horizontal = 5.dp, vertical = 2.dp)
                                        ) {
                                            Text(text = server.protocol, fontSize = 9.sp, color = colorScheme.primary, fontWeight = FontWeight.Bold)
                                        }
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(text = "${server.address}:${server.port}", fontSize = 11.sp, color = colorScheme.onSurface.copy(alpha = 0.5f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    }
                                }
                            }
                            
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                val latency = server.pingMs
                                val latencyColor = if (latency < 0) colorScheme.onSurface.copy(alpha = 0.3f) 
                                                   else if (latency < 60) Color(0xFF00E676) 
                                                   else if (latency < 120) Color(0xFFFFA000) 
                                                   else Color(0xFFD32F2F)

                                Icon(
                                    imageVector = Icons.Default.NetworkCheck,
                                    contentDescription = "latency indicator",
                                    tint = latencyColor,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (latency < 0) "--" else "${latency}ms",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = latencyColor
                                )
                                
                                Spacer(modifier = Modifier.width(14.dp))

                                IconButton(
                                    onClick = { viewModel.toggleFavorite(server) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = if (server.isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                                        contentDescription = "Favorite",
                                        tint = if (server.isFavorite) Color(0xFFFFD600) else colorScheme.onSurface.copy(alpha = 0.4f),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                IconButton(
                                    onClick = { viewModel.deleteServer(server) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        tint = colorScheme.error.copy(alpha = 0.7f),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Add Server Prompt Dialog
    if (showAddDialog) {
        var sName by remember { mutableStateOf("") }
        var sAddr by remember { mutableStateOf("") }
        var sPort by remember { mutableStateOf("") }
        var sProto by remember { mutableStateOf("VLESS") }
        var sCc by remember { mutableStateOf("US") }
        var sPath by remember { mutableStateOf("") }
        var sSni by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text(text = VpnLocalization.getString("add_server", lang), fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = sName,
                        onValueChange = { sName = it },
                        label = { Text(VpnLocalization.getString("server_name", lang)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = sAddr,
                        onValueChange = { sAddr = it },
                        label = { Text(VpnLocalization.getString("address", lang)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = sPort,
                        onValueChange = { sPort = it },
                        label = { Text(VpnLocalization.getString("port", lang)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = sCc,
                        onValueChange = { sCc = it },
                        label = { Text("Country Code (e.g. US, DE, JP)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    // Protocol selection row
                    Text(text = VpnLocalization.getString("protocol", lang), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    val protos = listOf("VLESS", "VMESS", "Trojan", "Shadowsocks", "Hysteria 2", "WireGuard")
                    Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                        protos.forEach { p ->
                            FilterChip(
                                selected = sProto == p,
                                onClick = { sProto = p },
                                label = { Text(p) },
                                modifier = Modifier.padding(end = 4.dp)
                            )
                        }
                    }
                    OutlinedTextField(
                        value = sSni,
                        onValueChange = { sSni = it },
                        label = { Text("Server SNI / Reality Domain (Optional)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = sPath,
                        onValueChange = { sPath = it },
                        label = { Text("WebSocket/gRPC Path (Optional)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val portInt = sPort.toIntOrNull() ?: 443
                        if (sName.isNotEmpty() && sAddr.isNotEmpty()) {
                            viewModel.addServer(sName, sAddr, portInt, sProto, sCc, sPath, sSni)
                            showAddDialog = false
                        } else {
                            Toast.makeText(context, "Fill out basic information! / Заполните базовые поля!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.testTag("submit_add_server")
                ) {
                    Text(VpnLocalization.getString("save", lang))
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text(VpnLocalization.getString("cancel", lang))
                }
            }
        )
    }
}

// 3. SUBSCRIPTIONS SCREEN
@Composable
fun SubscriptionsTab(viewModel: VpnViewModel, lang: VpnLocalization.Language) {
    val subsList by viewModel.subscriptions.collectAsState()
    val colorScheme = MaterialTheme.colorScheme
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    var showAddDialog by remember { mutableStateOf(false) }
    var showScanDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = VpnLocalization.getString("sub_title", lang),
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                color = colorScheme.onBackground
            )

            Button(
                onClick = { viewModel.refreshAllSubscriptions() },
                modifier = Modifier
                    .height(38.dp)
                    .testTag("update_all_subs"),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(imageVector = Icons.Default.Refresh, contentDescription = "Sync", modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = VpnLocalization.getString("update_all", lang), fontSize = 11.sp)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Subscription creation options row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { showAddDialog = true },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("add_sub_manual"),
                colors = ButtonDefaults.buttonColors(containerColor = colorScheme.primary.copy(alpha = 0.1f), contentColor = colorScheme.primary)
            ) {
                Icon(imageVector = Icons.Default.Link, contentDescription = "Add Link")
                Spacer(modifier = Modifier.width(4.dp))
                Text(VpnLocalization.getString("add_subscription", lang), fontSize = 12.sp, maxLines = 1)
            }

            Button(
                onClick = { showScanDialog = true },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1.5f)
                    .height(48.dp)
                    .testTag("scan_sub_qr"),
                colors = ButtonDefaults.buttonColors(containerColor = colorScheme.primary)
            ) {
                Icon(imageVector = Icons.Default.QrCodeScanner, contentDescription = "QR Scanner")
                Spacer(modifier = Modifier.width(4.dp))
                Text(VpnLocalization.getString("import_qr", lang), fontSize = 12.sp, maxLines = 1)
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Subscription list
        if (subsList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(imageVector = Icons.Default.FeaturedPlayList, contentDescription = null, modifier = Modifier.size(60.dp), tint = colorScheme.onSurface.copy(alpha = 0.2f))
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(text = "No saved subscriptions. Add one above!", color = colorScheme.onSurface.copy(alpha = 0.5f))
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .testTag("subscriptions_list"),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(subsList) { sub ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("sub_item_${sub.id}"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = colorScheme.surface.copy(alpha = 0.6f)),
                        border = BorderStroke(1.dp, colorScheme.outline.copy(alpha = 0.1f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.CloudQueue, contentDescription = "Sub", tint = colorScheme.primary)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(text = sub.name, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = colorScheme.onSurface)
                                }

                                Row {
                                    IconButton(
                                        onClick = { viewModel.refreshSubscription(sub) },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.Autorenew, contentDescription = "Sync", tint = colorScheme.primary)
                                    }
                                    IconButton(
                                        onClick = { viewModel.deleteSubscription(sub) },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.DeleteForever, contentDescription = "Delete", tint = colorScheme.error.copy(alpha = 0.8f))
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = sub.url, fontSize = 11.sp, color = colorScheme.onSurface.copy(alpha = 0.5f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                            
                            Spacer(modifier = Modifier.height(10.dp))
                            HorizontalDivider(color = colorScheme.outline.copy(alpha = 0.08f))
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .background(colorScheme.secondary.copy(alpha = 0.12f), RoundedCornerShape(4.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(text = sub.type, fontSize = 9.sp, color = colorScheme.secondary, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Sync policy: ${sub.autoUpdateHours} hrs",
                                        fontSize = 11.sp,
                                        color = colorScheme.onSurface.copy(alpha = 0.4f)
                                    )
                                }
                                Text(
                                    text = "Updated: ${formatLastUpdated(sub.lastUpdated)}",
                                    fontSize = 11.sp,
                                    color = colorScheme.onSurface.copy(alpha = 0.4f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Manual Subscription Dialog
    if (showAddDialog) {
        var subName by remember { mutableStateOf("") }
        var subUrl by remember { mutableStateOf("") }
        var subFormat by remember { mutableStateOf("URL") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text(text = VpnLocalization.getString("add_subscription", lang), fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = subName,
                        onValueChange = { subName = it },
                        label = { Text(VpnLocalization.getString("sub_name", lang)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = subUrl,
                        onValueChange = { subUrl = it },
                        label = { Text(VpnLocalization.getString("sub_url", lang)) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(text = VpnLocalization.getString("sub_type", lang), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    val formatOptions = listOf("URL", "Base64", "Clash", "Sing-box")
                    Row {
                        formatOptions.forEach { opt ->
                            FilterChip(
                                selected = subFormat == opt,
                                onClick = { subFormat = opt },
                                label = { Text(opt) },
                                modifier = Modifier.padding(end = 4.dp)
                            )
                        }
                    }

                    // Button import clip
                    TextButton(
                        onClick = {
                            clipboardManager.getText()?.let {
                                subUrl = it.text
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Default.ContentPaste, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(VpnLocalization.getString("import_clipboard", lang))
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (subName.isNotEmpty() && subUrl.isNotEmpty()) {
                            viewModel.addSubscription(subName, subUrl, subFormat)
                            showAddDialog = false
                        } else {
                            Toast.makeText(context, "Fill all information / Заполните все поля", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.testTag("btn_save_sub")
                ) {
                    Text(VpnLocalization.getString("save", lang))
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text(VpnLocalization.getString("cancel", lang))
                }
            }
        )
    }

    // VR / Simulated QR Scan Dialog
    if (showScanDialog) {
        AlertDialog(
            onDismissRequest = { showScanDialog = false },
            title = { Text(text = VpnLocalization.getString("qr_camera_sim", lang), fontWeight = FontWeight.Black) },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = VpnLocalization.getString("qr_camera_instructions", lang),
                        fontSize = 12.sp,
                        color = colorScheme.onSurface.copy(alpha = 0.6f),
                        textAlign = TextAlign.Center
                    )

                    // Cool visual camera view simulation frame
                    Box(
                        modifier = Modifier
                            .size(180.dp)
                            .background(Color.Black, RoundedCornerShape(16.dp))
                            .border(2.dp, colorScheme.primary, RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        // Scan line animation
                        val scanTransition = rememberInfiniteTransition(label = "scan")
                        val scanOffset by scanTransition.animateFloat(
                            initialValue = -80f,
                            targetValue = 80f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(2000, easing = LinearEasing),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "line_offset"
                        )

                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val lineY = (size.height / 2) + scanOffset
                            drawLine(
                                color = colorScheme.primary,
                                start = Offset(10f, lineY),
                                end = Offset(size.width - 10f, lineY),
                                strokeWidth = 4f,
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.QrCode2,
                            contentDescription = "Simulated QR code",
                            tint = colorScheme.primary.copy(alpha = 0.6f),
                            modifier = Modifier.size(100.dp)
                        )
                    }

                    Button(
                        onClick = {
                            viewModel.addSubscription(
                                name = "QR Imported Node",
                                url = "ss://Y2hhc2hhd2lmdmZAY29yZTAxLndpbGR2Zi5uZXQ6NDQz#QR_Premium_Import",
                                type = "Base64"
                            )
                            showScanDialog = false
                            Toast.makeText(context, "Scanning successful! Connected and configuration synced.", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("simulate_success_scan")
                    ) {
                        Text(VpnLocalization.getString("simulate_scan", lang))
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showScanDialog = false }) {
                    Text(VpnLocalization.getString("cancel", lang))
                }
            }
        )
    }
}

// 4. ANALYTICS SCREEN
@Composable
fun AnalyticsTab(viewModel: VpnViewModel, lang: VpnLocalization.Language) {
    val analyticsPoints by viewModel.analyticsPoints.collectAsState()
    val colorScheme = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = VpnLocalization.getString("analytics_title", lang),
            fontSize = 22.sp,
            fontWeight = FontWeight.Black,
            color = colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(15.dp))

        // Total Traffic Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = colorScheme.surface.copy(alpha = 0.5f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = VpnLocalization.getString("traffic_used", lang),
                        fontSize = 12.sp,
                        color = colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "1.24 GB / 100.00 GB", fontSize = 20.sp, fontWeight = FontWeight.Black)
                }

                Box(
                    modifier = Modifier.size(50.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        progress = { 0.0124f },
                        modifier = Modifier.fillMaxSize(),
                        color = colorScheme.primary,
                        strokeWidth = 5.dp,
                        trackColor = colorScheme.onSurface.copy(alpha = 0.08f),
                    )
                    Text(text = "1.2%", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Custom High Fidelity speed Canvas-based chart
        Text(text = VpnLocalization.getString("speed_chart", lang), fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(10.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .testTag("speed_canvas_chart"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = colorScheme.surface.copy(alpha = 0.85f)),
            border = BorderStroke(1.dp, colorScheme.outline.copy(alpha = 0.1f))
        ) {
            Box(modifier = Modifier.padding(16.dp)) {
                if (analyticsPoints.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Awaiting active telemetric data stream...",
                            color = colorScheme.onSurface.copy(alpha = 0.4f),
                            fontSize = 12.sp
                        )
                    }
                } else {
                    val dlPoints = analyticsPoints.map { it.downloadSpeedMbps }
                    val ulPoints = analyticsPoints.map { it.uploadSpeedMbps }
                    VpnFlowChart(dlPoints, ulPoints, colorScheme)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Latency Chart
        Text(text = VpnLocalization.getString("ping_chart", lang), fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(10.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .testTag("ping_canvas_chart"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = colorScheme.surface.copy(alpha = 0.85f)),
            border = BorderStroke(1.dp, colorScheme.outline.copy(alpha = 0.1f))
        ) {
            Box(modifier = Modifier.padding(16.dp)) {
                if (analyticsPoints.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = "Connect to display ping performance profile.", color = colorScheme.onSurface.copy(alpha = 0.4f), fontSize = 12.sp)
                    }
                } else {
                    val pings = analyticsPoints.map { it.pingMs.toFloat() }
                    VpnLatencyChart(pings, colorScheme)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Session History
        Text(text = VpnLocalization.getString("session_history", lang), fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(10.dp))
        
        val historySessions = listOf(
            Triple("Frankfurt Core 1", "35m 12s", "125.4 MB"),
            Triple("Moscow Fast SSL", "1h 14m", "432.1 MB"),
            Triple("London Gateway", "10m 5s", "14.2 MB")
        )

        historySessions.forEach { (srv, duration, payload) ->
            ListItem(
                headlineContent = { Text(text = srv, fontWeight = FontWeight.SemiBold, fontSize = 13.sp) },
                supportingContent = { Text(text = "Duration: $duration", fontSize = 11.sp) },
                trailingContent = { Text(text = payload, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = colorScheme.primary) },
                colors = ListItemDefaults.colors(containerColor = colorScheme.surface.copy(alpha = 0.3f)),
                modifier = Modifier
                    .padding(vertical = 4.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
        }
    }
}

// Custom Native Speed flow Curve graph
@Composable
fun VpnFlowChart(dl: List<Float>, ul: List<Float>, colorScheme: ColorScheme) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val maxVal = max(50f, (dl + ul).maxOrNull() ?: 10f)
        val numPoints = dl.size
        if (numPoints < 2) return@Canvas

        val w = size.width
        val h = size.height
        val stepX = w / (numPoints - 1)

        // Draw grids
        val numGrid = 4
        for (i in 0..numGrid) {
            val gy = (h / numGrid) * i
            drawLine(
                color = colorScheme.onSurface.copy(alpha = 0.05f),
                start = Offset(0f, gy),
                end = Offset(w, gy),
                strokeWidth = 2f
            )
        }

        // Paths
        val dlPath = Path()
        val ulPath = Path()
        val dlAreaPath = Path()

        dl.forEachIndexed { index, value ->
            val cx = index * stepX
            val cy = h - (value / maxVal) * h
            if (index == 0) {
                dlPath.moveTo(cx, cy)
                dlAreaPath.moveTo(cx, h)
                dlAreaPath.lineTo(cx, cy)
            } else {
                dlPath.lineTo(cx, cy)
                dlAreaPath.lineTo(cx, cy)
            }
            if (index == numPoints - 1) {
                dlAreaPath.lineTo(cx, h)
                dlAreaPath.close()
            }
        }

        ul.forEachIndexed { index, value ->
            val cx = index * stepX
            val cy = h - (value / maxVal) * h
            if (index == 0) {
                ulPath.moveTo(cx, cy)
            } else {
                ulPath.lineTo(cx, cy)
            }
        }

        // Draw filled glow areas
        drawPath(
            path = dlAreaPath,
            brush = Brush.verticalGradient(
                colors = listOf(colorScheme.primary.copy(alpha = 0.25f), Color.Transparent)
            )
        )

        // Draw lines
        drawPath(
            path = dlPath,
            color = colorScheme.primary,
            style = Stroke(width = 6f, cap = StrokeCap.Round)
        )

        drawPath(
            path = ulPath,
            color = Color(0xFFD500F9),
            style = Stroke(width = 4f, cap = StrokeCap.Round, pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f))
        )
    }
}

@Composable
fun VpnLatencyChart(pings: List<Float>, colorScheme: ColorScheme) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val maxVal = max(180f, pings.maxOrNull() ?: 10f)
        val numPoints = pings.size
        if (numPoints < 2) return@Canvas

        val w = size.width
        val h = size.height
        val stepX = w / (numPoints - 1)

        val pingPath = Path()
        pings.forEachIndexed { index, value ->
            val cx = index * stepX
            val cy = h - (value / maxVal) * h
            if (index == 0) {
                pingPath.moveTo(cx, cy)
            } else {
                pingPath.lineTo(cx, cy)
            }
            // Draw indicators dots
            drawCircle(color = colorScheme.secondary, radius = 6f, center = Offset(cx, cy))
        }

        drawPath(
            path = pingPath,
            color = colorScheme.secondary.copy(alpha = 0.6f),
            style = Stroke(width = 3f)
        )
    }
}

// 5. SETTINGS SCREEN
@Composable
fun SettingsTab(viewModel: VpnViewModel, lang: VpnLocalization.Language) {
    val currentTheme by viewModel.currentTheme.collectAsState()
    val currentRouting by viewModel.currentRouting.collectAsState()
    val tunMode by viewModel.tunModeEnabled.collectAsState()
    val killSwitch by viewModel.killSwitchEnabled.collectAsState()
    val leakProtection by viewModel.leakProtectionEnabled.collectAsState()
    val ipv6Control by viewModel.ipv6ControlEnabled.collectAsState()
    val tlsFp by viewModel.tlsFingerprint.collectAsState()
    val obfus by viewModel.trafficObfuscation.collectAsState()
    val doh by viewModel.dnsOverHttps.collectAsState()
    val dot by viewModel.dnsOverTls.collectAsState()
    val doq by viewModel.dnsOverQuic.collectAsState()
    val fakeDns by viewModel.fakeDns.collectAsState()

    val colorScheme = MaterialTheme.colorScheme
    val context = LocalContext.current

    var selectedSettingsTab by remember { mutableStateOf("general") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = VpnLocalization.getString("settings_title", lang),
            fontSize = 22.sp,
            fontWeight = FontWeight.Black,
            color = colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(15.dp))

        // Mini Settings Category Tabs
        val categories = listOf(
            "general" to VpnLocalization.getString("cat_general", lang),
            "tunnel" to VpnLocalization.getString("cat_tunnel", lang),
            "dns" to VpnLocalization.getString("cat_dns", lang),
            "security" to VpnLocalization.getString("cat_security", lang),
            "interface" to VpnLocalization.getString("cat_theme", lang)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 4.dp)
        ) {
            categories.forEach { (catId, catLabel) ->
                FilterChip(
                    selected = selectedSettingsTab == catId,
                    onClick = { selectedSettingsTab = catId },
                    label = { Text(text = catLabel, fontSize = 12.sp) },
                    modifier = Modifier
                        .padding(end = 6.dp)
                        .testTag("setting_tab_$catId"),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = colorScheme.primary.copy(alpha = 0.2f),
                        selectedLabelColor = colorScheme.primary
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(15.dp))

        // Context view layout
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when (selectedSettingsTab) {
                "general" -> {
                    // GENERAL CATEGORY INTERACTIVE SETTINGS
                    Text(text = VpnLocalization.getString("cat_general", lang), fontWeight = FontWeight.Bold, fontSize = 15.sp, color = colorScheme.primary)
                    
                    // Routing select cards
                    Text(text = VpnLocalization.getString("routing_mode", lang), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                    val routingModes = RoutingMode.values()
                    routingModes.forEach { mode ->
                        val isSelected = currentRouting == mode
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.changeRouting(mode) }
                                .testTag("routing_mode_${mode.name}"),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = if (isSelected) colorScheme.primary.copy(alpha = 0.12f) else colorScheme.surface.copy(alpha = 0.5f)),
                            border = BorderStroke(1.dp, if (isSelected) colorScheme.primary else colorScheme.outline.copy(alpha = 0.1f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (isSelected) Icons.Default.RadioButtonChecked else Icons.Default.RadioButtonUnchecked,
                                        contentDescription = null,
                                        tint = if (isSelected) colorScheme.primary else colorScheme.onSurface.copy(alpha = 0.4f)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = when (mode) {
                                            RoutingMode.GLOBAL -> VpnLocalization.getString("route_global", lang)
                                            RoutingMode.DIRECT -> VpnLocalization.getString("route_direct", lang)
                                            RoutingMode.PROXY -> VpnLocalization.getString("route_proxy", lang)
                                            RoutingMode.RULE -> VpnLocalization.getString("route_rule", lang)
                                            RoutingMode.AUTO -> VpnLocalization.getString("route_auto", lang)
                                        },
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Language Selector Dynamic Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = colorScheme.surface.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = VpnLocalization.getString("language_label", lang), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = { viewModel.changeLanguage(VpnLocalization.Language.EN) },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("lang_en"),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (lang == VpnLocalization.Language.EN) colorScheme.primary else colorScheme.outline.copy(alpha = 0.12f),
                                        contentColor = if (lang == VpnLocalization.Language.EN) colorScheme.background else colorScheme.onSurface
                                    )
                                ) {
                                    Text(text = "English 🇬🇧")
                                }
                                Button(
                                    onClick = { viewModel.changeLanguage(VpnLocalization.Language.RU) },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("lang_ru"),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (lang == VpnLocalization.Language.RU) colorScheme.primary else colorScheme.outline.copy(alpha = 0.12f),
                                        contentColor = if (lang == VpnLocalization.Language.RU) colorScheme.background else colorScheme.onSurface
                                    )
                                ) {
                                    Text(text = "Русский 🇷🇺")
                                }
                            }
                        }
                    }
                }

                "tunnel" -> {
                    // TUNNEL & SPLIT TUNNEL CATEGORY
                    Text(text = VpnLocalization.getString("cat_tunnel", lang), fontWeight = FontWeight.Bold, fontSize = 15.sp, color = colorScheme.primary)
                    
                    // TUN Toggle Card
                    ListItem(
                        headlineContent = { Text(text = VpnLocalization.getString("tun_mode", lang), fontWeight = FontWeight.Bold, fontSize = 14.sp) },
                        supportingContent = { Text(text = VpnLocalization.getString("tun_mode_desc", lang), fontSize = 11.sp) },
                        trailingContent = {
                            Switch(
                                checked = tunMode,
                                onCheckedChange = { viewModel.setTunMode(it) },
                                modifier = Modifier.testTag("tun_mode_switch")
                            )
                        },
                        colors = ListItemDefaults.colors(containerColor = colorScheme.surface.copy(alpha = 0.5f)),
                        modifier = Modifier.clip(RoundedCornerShape(12.dp))
                    )

                    // App Split Tunneling checked lists
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = colorScheme.surface.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = VpnLocalization.getString("split_tunneling", lang), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = VpnLocalization.getString("split_tunnel_desc", lang), fontSize = 11.sp, color = colorScheme.onSurface.copy(alpha = 0.5f))
                            
                            Spacer(modifier = Modifier.height(12.dp))

                            val recommandedBypasses = listOf("Telegram", "WhatsApp", "Chrome Explorer", "YouTube Player", "Spotify Music", "Zoom Conferences", "Netflix")
                            recommandedBypasses.forEach { appName ->
                                val isBypassed = viewModel.splitTunnelingBypassedApps.contains(appName)
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            if (isBypassed) {
                                                viewModel.splitTunnelingBypassedApps.remove(appName)
                                            } else {
                                                viewModel.splitTunnelingBypassedApps.add(appName)
                                            }
                                        }
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = if (isBypassed) Icons.Default.CheckCircle else Icons.Default.AddCircleOutline,
                                            contentDescription = null,
                                            tint = if (isBypassed) colorScheme.primary else colorScheme.onSurface.copy(alpha = 0.3f),
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(text = appName, fontSize = 13.sp)
                                    }
                                    Checkbox(
                                        checked = isBypassed,
                                        onCheckedChange = { checked ->
                                            if (checked == true) {
                                                if (!isBypassed) viewModel.splitTunnelingBypassedApps.add(appName)
                                            } else {
                                                viewModel.splitTunnelingBypassedApps.remove(appName)
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                "dns" -> {
                    // DNS SECURE PRESETS
                    Text(text = VpnLocalization.getString("cat_dns", lang), fontWeight = FontWeight.Bold, fontSize = 15.sp, color = colorScheme.primary)
                    
                    // DoH
                    ToggleOptionCard(title = VpnLocalization.getString("dns_over_https", lang), desc = "Route secure DNS resolution requests over HTTPS wrappers.", checked = doh, onCheckedChange = { viewModel.setDnsOverHttps(it) }, tag = "dns_doh_switch", colorScheme = colorScheme)
                    // DoT
                    ToggleOptionCard(title = VpnLocalization.getString("dns_over_tls", lang), desc = "Encrypted binary payload transmission over active TLS ports.", checked = dot, onCheckedChange = { viewModel.setDnsOverTls(it) }, tag = "dns_dot_switch", colorScheme = colorScheme)
                    // DoQ
                    ToggleOptionCard(title = VpnLocalization.getString("dns_over_quic", lang), desc = "High reliability DNS transmission maps bypassing UDP throttling.", checked = doq, onCheckedChange = { viewModel.setDnsOverQuic(it) }, tag = "dns_doq_switch", colorScheme = colorScheme)
                    // Fake DNS
                    ToggleOptionCard(title = VpnLocalization.getString("fake_dns", lang), desc = "Spoofs metadata IP responses dynamically to improve latency response.", checked = fakeDns, onCheckedChange = { viewModel.setFakeDns(it) }, tag = "dns_fake_switch", colorScheme = colorScheme)
                }

                "security" -> {
                    // SECURITY PARAMETERS
                    Text(text = VpnLocalization.getString("cat_security", lang), fontWeight = FontWeight.Bold, fontSize = 15.sp, color = colorScheme.primary)

                    ToggleOptionCard(title = VpnLocalization.getString("kill_switch", lang), desc = VpnLocalization.getString("kill_switch_desc", lang), checked = killSwitch, onCheckedChange = { viewModel.setKillSwitch(it) }, tag = "kill_switch_toggle", colorScheme = colorScheme)
                    ToggleOptionCard(title = VpnLocalization.getString("leak_protection", lang), desc = "Failsafe blocks for accidental backend IPv4 leaks.", checked = leakProtection, onCheckedChange = { viewModel.setLeakProtection(it) }, tag = "leak_protection_toggle", colorScheme = colorScheme)
                    ToggleOptionCard(title = VpnLocalization.getString("v6_control", lang), desc = "Drop packet requests on native IPv6 adapters completely.", checked = ipv6Control, onCheckedChange = { viewModel.setIpv6Control(it) }, tag = "ipv6_control_toggle", colorScheme = colorScheme)
                    
                    // TLS Client Fingerprints Selection
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = colorScheme.surface.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(text = VpnLocalization.getString("tls_fingerprint", lang), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            val fingerprints = listOf("Chrome", "Firefox", "Safari", "Edge", "360Secure")
                            fingerprints.forEach { fp ->
                                val isSelected = tlsFp == fp
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { viewModel.setTlsFingerprint(fp) }
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (isSelected) Icons.Default.RadioButtonChecked else Icons.Default.RadioButtonUnchecked,
                                        contentDescription = null,
                                        tint = if (isSelected) colorScheme.primary else colorScheme.onSurface.copy(alpha = 0.4f),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(text = fp, fontSize = 13.sp)
                                }
                            }
                        }
                    }

                    // Obfuscation
                    ToggleOptionCard(
                        title = VpnLocalization.getString("obfuscation", lang),
                        desc = "Wrap standard outbound handshakes into randomized dummy blocks to pass deep-packet inspect.",
                        checked = obfus,
                        onCheckedChange = { viewModel.setObfuscation(it) },
                        tag = "obfuscation_toggle",
                        colorScheme = colorScheme
                    )
                }

                "interface" -> {
                    // THEMES AND INTERFACE CUSTOMIZER MATRIX
                    Text(text = VpnLocalization.getString("cat_theme", lang), fontWeight = FontWeight.Bold, fontSize = 15.sp, color = colorScheme.primary)
                    
                    Text(text = VpnLocalization.getString("theme", lang), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier
                            .height(290.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        userScrollEnabled = false
                    ) {
                        val themes = VpnTheme.values()
                        items(themes.size) { index ->
                            val t = themes[index]
                            val isSelected = currentTheme == t
                            Card(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clickable { viewModel.changeTheme(t) }
                                    .testTag("theme_pill_${t.name}"),
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) colorScheme.primary.copy(alpha = 0.15f) else colorScheme.surface.copy(alpha = 0.5f)
                                ),
                                border = BorderStroke(1.dp, if (isSelected) colorScheme.primary else colorScheme.outline.copy(alpha = 0.1f))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(16.dp)
                                            .background(VpnThemeHelper.getColorScheme(t).primary, CircleShape)
                                    )
                                    Text(text = t.displayName, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Custom accent color selection
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = colorScheme.surface.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = VpnLocalization.getString("theme_custom_title", lang), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(10.dp))

                            // Accent colors
                            val accents = listOf(
                                Color(0xFFFF5252), // Toxic Coral Red
                                Color(0xFFFF4081), // Neon Pink
                                Color(0xFFE040FB), // Psychedelic Violet
                                Color(0xFF7C4DFF), // Arctic Cobalt
                                Color(0xFF00E5FF), // Cyber Turquoise
                                Color(0xFF00E676), // Toxic Green
                                Color(0xFFEEFF41), // Acid Lime
                                Color(0xFFFFEB3B), // Cyber gold
                                Color(0xFFFF9100)  // Fire Orange
                            )

                            Row(
                                modifier = Modifier.horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                accents.forEach { c ->
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .background(c, CircleShape)
                                            .clickable {
                                                viewModel.setCustomAccent(c)
                                                Toast.makeText(context, "Accent set! / Акцент изменен!", Toast.LENGTH_SHORT).show()
                                            }
                                            .border(2.dp, if (MaterialTheme.colorScheme.primary == c) Color.White else Color.Transparent, CircleShape)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Action button template import style
                            Button(
                                onClick = {
                                    viewModel.importThemeStyle("""{"primary":"#00E5FF","theme":"CYBERPUNK"}""")
                                    Toast.makeText(context, VpnLocalization.getString("theme_created", lang), Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("import_style_json")
                            ) {
                                Icon(imageVector = Icons.Default.Palette, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Import Theme Config (JSON)")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ToggleOptionCard(
    title: String,
    desc: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    tag: String,
    colorScheme: ColorScheme
) {
    ListItem(
        headlineContent = { Text(text = title, fontWeight = FontWeight.Bold, fontSize = 13.sp) },
        supportingContent = { Text(text = desc, fontSize = 11.sp, color = colorScheme.onSurface.copy(alpha = 0.5f)) },
        trailingContent = {
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                modifier = Modifier.testTag(tag)
            )
        },
        colors = ListItemDefaults.colors(containerColor = colorScheme.surface.copy(alpha = 0.5f)),
        modifier = Modifier
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(12.dp))
    )
}

// 6. LOGS CONSOLE TAB SCREEN
@Composable
fun LogsTab(viewModel: VpnViewModel, lang: VpnLocalization.Language) {
    val logsList by viewModel.logs.collectAsState()
    val searchQuery by viewModel.logSearchQuery.collectAsState()
    val colorScheme = MaterialTheme.colorScheme
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = VpnLocalization.getString("logs_title", lang),
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                color = colorScheme.onBackground
            )

            Row {
                IconButton(
                    onClick = {
                        val fullDump = logsList.joinToString("\n") { "[${formatLogTime(it.timestamp)}] [${it.level}] ${it.message}" }
                        clipboardManager.setText(AnnotatedString(fullDump))
                        Toast.makeText(context, "Logs copied. Ready to export! / Логи скопированы!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.testTag("btn_export_logs")
                ) {
                    Icon(imageVector = Icons.Default.Share, contentDescription = "Export Logs", tint = colorScheme.primary)
                }

                IconButton(
                    onClick = { viewModel.clearAllLogs() },
                    modifier = Modifier.testTag("btn_clear_logs")
                ) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Clear Logs", tint = colorScheme.error)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Search log console
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.setLogSearchQuery(it) },
            placeholder = { Text(text = VpnLocalization.getString("search_logs", lang)) },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("logs_search_input"),
            shape = RoundedCornerShape(10.dp),
            singleLine = true,
            leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null) }
        )

        Spacer(modifier = Modifier.height(15.dp))

        // Console container
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Color.Black, RoundedCornerShape(16.dp))
                .border(1.dp, colorScheme.outline.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
                .padding(12.dp)
        ) {
            val filteredLogs = logsList.filter {
                it.message.contains(searchQuery, ignoreCase = true) || 
                it.level.contains(searchQuery, ignoreCase = true)
            }

            if (filteredLogs.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "No logs currently trace matching query.", color = Color.Gray, fontSize = 12.sp)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("logs_console_list"),
                    reverseLayout = true
                ) {
                    items(filteredLogs) { log ->
                        val textColor = when (log.level) {
                            "ERROR" -> Color(0xFFFFA4A4)
                            "WARN" -> Color(0xFFFFD180)
                            "DEBUG" -> Color(0xFF80DEEA)
                            else -> Color(0xFFC8E6C9)
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(
                                text = "[${formatLogTime(log.timestamp)}]",
                                color = Color.Gray,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Text(
                                text = "[${log.level}]",
                                color = textColor,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Text(
                                text = log.message,
                                color = Color.White,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}

// General Utilities & Formatters
fun getEmojiFlag(countryCode: String?): String {
    if (countryCode == null || countryCode.length < 2) return "🌐"
    val code = countryCode.uppercase()
    val firstChar = Character.codePointAt(code, 0) - 0x41 + 0x1F1E6
    val secondChar = Character.codePointAt(code, 1) - 0x41 + 0x1F1E6
    return String(Character.toChars(firstChar)) + String(Character.toChars(secondChar))
}

fun formatElapsedTime(seconds: Long): String {
    val h = seconds / 3600
    val m = (seconds % 3600) / 60
    val s = seconds % 60
    return String.format("%02d:%02d:%02d", h, m, s)
}

fun formatLastUpdated(timestamp: Long): String {
    val durationText = System.currentTimeMillis() - timestamp
    val mins = durationText / 60000
    if (mins < 1) return "just now"
    if (mins < 60) return "${mins}m ago"
    val hrs = mins / 60
    if (hrs < 24) return "${hrs}h ago"
    return "1d ago"
}

fun formatLogTime(timestamp: Long): String {
    val sdf = java.text.SimpleDateFormat("HH:mm:ss.SSS", java.util.Locale.getDefault())
    return sdf.format(java.util.Date(timestamp))
}
