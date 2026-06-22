package com.example.data

object VpnLocalization {
    
    enum class Language {
        EN, RU
    }

    private val translations = mapOf(
        "app_title" to mapOf(
            Language.EN to "WILDVF VPN",
            Language.RU to "WILDVF VPN"
        ),
        "connected" to mapOf(
            Language.EN to "CONNECTED",
            Language.RU to "ПОДКЛЮЧЕНО"
        ),
        "disconnected" to mapOf(
            Language.EN to "DISCONNECTED",
            Language.RU to "ОТКЛЮЧЕНО"
        ),
        "connecting" to mapOf(
            Language.EN to "CONNECTING...",
            Language.RU to "ПОДКЛЮЧЕНИЕ..."
        ),
        "disconnecting" to mapOf(
            Language.EN to "DISCONNECTING...",
            Language.RU to "ОТКЛЮЧЕНИЕ..."
        ),
        "connect" to mapOf(
            Language.EN to "CONNECT",
            Language.RU to "ПОДКЛЮЧИТЬ"
        ),
        "disconnect" to mapOf(
            Language.EN to "DISCONNECT",
            Language.RU to "ОТКЛЮЧИТЬ"
        ),
        "vless_desc" to mapOf(
            Language.EN to "Next-gen secure lightweight VLESS protocol",
            Language.RU to "Облегченный протокол VLESS нового поколения"
        ),
        // Tabs
        "tab_dashboard" to mapOf(
            Language.EN to "Home",
            Language.RU to "Главная"
        ),
        "tab_servers" to mapOf(
            Language.EN to "Servers",
            Language.RU to "Серверы"
        ),
        "tab_subscriptions" to mapOf(
            Language.EN to "Subs",
            Language.RU to "Подписки"
        ),
        "tab_analytics" to mapOf(
            Language.EN to "Analytics",
            Language.RU to "Статистика"
        ),
        "tab_settings" to mapOf(
            Language.EN to "Settings",
            Language.RU to "Настройки"
        ),
        "tab_logs" to mapOf(
            Language.EN to "Logs",
            Language.RU to "Логи"
        ),
        // Server Screen
        "search_servers" to mapOf(
            Language.EN to "Search servers...",
            Language.RU to "Поиск серверов..."
        ),
        "all_countries" to mapOf(
            Language.EN to "All Countries",
            Language.RU to "Все страны"
        ),
        "ping_all" to mapOf(
            Language.EN to "Ping All",
            Language.RU to "Тест пинга"
        ),
        "favorites" to mapOf(
            Language.EN to "FavoritesOnly",
            Language.RU to "Только избранное"
        ),
        "add_server" to mapOf(
            Language.EN to "Add VPN Server",
            Language.RU to "Добавить сервер"
        ),
        "server_name" to mapOf(
            Language.EN to "Server Name",
            Language.RU to "Название сервера"
        ),
        "address" to mapOf(
            Language.EN to "Server Address / IP",
            Language.RU to "Адрес сервера / IP"
        ),
        "port" to mapOf(
            Language.EN to "Port",
            Language.RU to "Порт"
        ),
        "protocol" to mapOf(
            Language.EN to "Protocol",
            Language.RU to "Протокол"
        ),
        "save" to mapOf(
            Language.EN to "Save",
            Language.RU to "Сохранить"
        ),
        "cancel" to mapOf(
            Language.EN to "Cancel",
            Language.RU to "Отмена"
        ),
        // Routing Mod
        "routing_mode" to mapOf(
            Language.EN to "Routing Mode",
            Language.RU to "Режим маршрутизации"
        ),
        "route_global" to mapOf(
            Language.EN to "Global (Full Tunnel)",
            Language.RU to "Global (Весь трафик)"
        ),
        "route_direct" to mapOf(
            Language.EN to "Direct (No Proxy)",
            Language.RU to "Direct (Напрямую)"
        ),
        "route_proxy" to mapOf(
            Language.EN to "Proxy Only",
            Language.RU to "Proxy Only (Только Прокси)"
        ),
        "route_rule" to mapOf(
            Language.EN to "Rule (Smart Routing)",
            Language.RU to "Rule (По правилам)"
        ),
        "route_auto" to mapOf(
            Language.EN to "Auto (Smart ping)",
            Language.RU to "Auto (Автовыбор)"
        ),
        // Subscription Screen
        "sub_title" to mapOf(
            Language.EN to "Subscription Managers",
            Language.RU to "Управление подписками"
        ),
        "add_subscription" to mapOf(
            Language.EN to "Add Subscription",
            Language.RU to "Добавить подписку"
        ),
        "sub_name" to mapOf(
            Language.EN to "Subscription Name",
            Language.RU to "Имя подписки"
        ),
        "sub_url" to mapOf(
            Language.EN to "Subscription URL",
            Language.RU to "Ссылка подписки"
        ),
        "sub_type" to mapOf(
            Language.EN to "Format",
            Language.RU to "Формат"
        ),
        "import_clipboard" to mapOf(
            Language.EN to "Import Clipboard",
            Language.RU to "Из буфера обмена"
        ),
        "import_qr" to mapOf(
            Language.EN to "Scan QR Code",
            Language.RU to "Сканировать QR"
        ),
        "import_file" to mapOf(
            Language.EN to "Import File",
            Language.RU to "Импорт файла"
        ),
        "update_all" to mapOf(
            Language.EN to "Update All",
            Language.RU to "Обновить все"
        ),
        // Analytics
        "analytics_title" to mapOf(
            Language.EN to "Analytics & Network",
            Language.RU to "Аналитика сети"
        ),
        "traffic_used" to mapOf(
            Language.EN to "Total Traffic Used",
            Language.RU to "Использовано трафика"
        ),
        "speed_chart" to mapOf(
            Language.EN to "Speed Chart (Mbps)",
            Language.RU to "График скорости (Мбит/с)"
        ),
        "ping_chart" to mapOf(
            Language.EN to "Latency Chart (ms)",
            Language.RU to "График задержки (мс)"
        ),
        "session_history" to mapOf(
            Language.EN to "Session History",
            Language.RU to "История подключений"
        ),
        // Log screen
        "logs_title" to mapOf(
            Language.EN to "System Live Logs",
            Language.RU to "Системные логи"
        ),
        "search_logs" to mapOf(
            Language.EN to "Search logs...",
            Language.RU to "Поиск по логам..."
        ),
        "clear_logs" to mapOf(
            Language.EN to "Clear",
            Language.RU to "Очистить"
        ),
        "export_logs" to mapOf(
            Language.EN to "Export Logs",
            Language.RU to "Экспорт логов"
        ),
        // Settings Categories
        "settings_title" to mapOf(
            Language.EN to "Settings",
            Language.RU to "Настройки"
        ),
        "cat_general" to mapOf(
            Language.EN to "General Settings",
            Language.RU to "Общие"
        ),
        "cat_dns" to mapOf(
            Language.EN to "DNS Security Config",
            Language.RU to "DNS Настройки"
        ),
        "cat_security" to mapOf(
            Language.EN to "Security & Obfuscation",
            Language.RU to "Безопасность"
        ),
        "cat_tunnel" to mapOf(
            Language.EN to "TUN Mode & Split Tunneling",
            Language.RU to "Раздельное туннелирование"
        ),
        "cat_theme" to mapOf(
            Language.EN to "App Interface & Styling",
            Language.RU to "Темы и интерфейс"
        ),
        "language_label" to mapOf(
            Language.EN to "App Language",
            Language.RU to "Язык приложения"
        ),
        "theme" to mapOf(
            Language.EN to "Theme Selection",
            Language.RU to "Выбор темы"
        ),
        "accent_color" to mapOf(
            Language.EN to "Accent Color Mode",
            Language.RU to "Акцентный цвет"
        ),
        "theme_custom_title" to mapOf(
            Language.EN to "Custom Styling Core",
            Language.RU to "Персонализация стиля"
        ),
        // Security Settings
        "kill_switch" to mapOf(
            Language.EN to "Kill Switch",
            Language.RU to "Kill Switch"
        ),
        "kill_switch_desc" to mapOf(
            Language.EN to "Block internet when VPN connection is lost",
            Language.RU to "Блокировать интернет при обрыве соединения"
        ),
        "leak_protection" to mapOf(
            Language.EN to "WebRTC & DNS Leak Protection",
            Language.RU to "Защита от утечек DNS и WebRTC"
        ),
        "v6_control" to mapOf(
            Language.EN to "IPv6 Traffic Control",
            Language.RU to "Управление трафиком IPv6"
        ),
        "tls_fingerprint" to mapOf(
            Language.EN to "TLS Client Fingerprint",
            Language.RU to "TLS Отпечаток (Fingerprint)"
        ),
        "obfuscation" to mapOf(
            Language.EN to "Traffic Obfuscation",
            Language.RU to "Обфускация трафика"
        ),
        // Split Tunneling
        "split_tunneling" to mapOf(
            Language.EN to "App Split Tunneling",
            Language.RU to "Раздельный туннель"
        ),
        "split_tunnel_desc" to mapOf(
            Language.EN to "Select apps to bypass or force through VPN",
            Language.RU to "Выберите приложения для обхода VPN"
        ),
        "dns_over_https" to mapOf(
            Language.EN to "DNS over HTTPS (DoH)",
            Language.RU to "DNS over HTTPS (DoH)"
        ),
        "dns_over_tls" to mapOf(
            Language.EN to "DNS over TLS (DoT)",
            Language.RU to "DNS over TLS (DoT)"
        ),
        "dns_over_quic" to mapOf(
            Language.EN to "DNS over QUIC (DoQ)",
            Language.RU to "DNS over QUIC (DoQ)"
        ),
        "fake_dns" to mapOf(
            Language.EN to "Enable Fake DNS (Speed boost)",
            Language.RU to "Включить Fake DNS (Ускорение)"
        ),
        "tun_mode" to mapOf(
            Language.EN to "Global TUN Mode Overlay",
            Language.RU to "Глобальный режим TUN (Ядро)"
        ),
        "tun_mode_desc" to mapOf(
            Language.EN to "Creates a virtual TUN adapter for system-level routing",
            Language.RU to "Создает виртуальный адаптер TUN на уровне системы"
        ),
        "current_ip" to mapOf(
            Language.EN to "IP Address",
            Language.RU to "IP адрес"
        ),
        "server" to mapOf(
            Language.EN to "Server",
            Language.RU to "Сервер"
        ),
        "ping_speed" to mapOf(
            Language.EN to "Ping",
            Language.RU to "Пинг"
        ),
        "download_rate" to mapOf(
            Language.EN to "Download",
            Language.RU to "Загрузка"
        ),
        "upload_rate" to mapOf(
            Language.EN to "Upload",
            Language.RU to "Отдача"
        ),
        "elapsed_time" to mapOf(
            Language.EN to "Connected Duration",
            Language.RU to "Время подключения"
        ),
        "active_protocol" to mapOf(
            Language.EN to "Active Protocol",
            Language.RU to "Протокол"
        ),
        "search_hint" to mapOf(
            Language.EN to "Search...",
            Language.RU to "Поиск..."
        ),
        "qr_camera_sim" to mapOf(
            Language.EN to "QR Scanner (Virtual Camera Simulation)",
            Language.RU to "QR-сканер (Симуляция виртуальной камеры)"
        ),
        "qr_camera_instructions" to mapOf(
            Language.EN to "Hold the QR code within the frame to automatically parse subscription config.",
            Language.RU to "Поместите QR-код в рамку для автоматического распознавания подписки."
        ),
        "simulate_scan" to mapOf(
            Language.EN to "Simulate Successful Scan",
            Language.RU to "Имитировать успешное сканирование"
        ),
        "theme_created" to mapOf(
            Language.EN to "Custom style loaded successfully",
            Language.RU to "Пользовательский стиль успешно загружен"
        )
    )

    fun getString(key: String, lang: Language): String {
        return translations[key]?.get(lang) ?: key
    }
}
