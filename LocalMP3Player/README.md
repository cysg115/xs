# Local MP3 Player

一个简洁的本地MP3播放器应用，具有iOS风格设计和核心功能：

## ✨ 核心功能

1. **本地MP3播放** - 播放自定义文件夹中的MP3文件
2. **定时关闭** - 设置睡眠定时器（15/30/45/60分钟）
3. **播放记录** - 自动记录播放位置和历史
4. **启动直接播放** - 启动时自动续播上次未听完的歌曲
5. **桌面小组件** - 在桌面显示播放控制

## 🛠 技术栈

- **Kotlin** + **Jetpack Compose** - 现代Android开发
- **ExoPlayer** - 强大的音频播放引擎
- **Room** - 本地数据库存储播放记录
- **WorkManager** - 后台定时任务管理
- **App Widgets** - 桌面小组件支持

## 📱 界面特点

- iOS风格设计（简洁、圆角、卡片式）
- 系统主题跟随（深色/浅色模式）
- 直观的播放控制
- 实时播放进度显示

## 🔧 构建说明

### 环境要求
- Android Studio Flamingo (2022.2.1) 或更高版本
- Android SDK 34 (API 34)
- Gradle 8.0+

### 构建步骤

1. **导入项目**
   - 打开Android Studio
   - 选择 "Open" -> 选择本项目文件夹
   - 等待Gradle同步完成

2. **配置签名**
   - 如需生成发布APK，请配置签名密钥：
     - 菜单栏：Build → Generate Signed Bundle / APK
     - 选择 "APK"
     - 创建或选择现有密钥库

3. **生成APK**
   - 调试版：Run → Run 'app'
   - 发布版：Build → Build Bundle(s) / APK(s) → Build APK(s)

4. **安装测试**
   - 将生成的APK复制到Android设备
   - 启用"未知来源"安装权限
   - 安装并运行

## 📂 文件结构

```
app/src/main/java/com/example/localmp3player/
├── MainActivity.kt                 # 主入口
├── player/                         # 播放器核心
│   ├── AudioPlayerService.kt      # 后台播放服务
│   ├── PlayerScreen.kt            # 播放界面
│   └── PlaybackHistoryManager.kt  # 播放记录管理
├── viewmodel/                      # ViewModel
│   └── PlayerViewModel.kt         # 播放状态管理
├── data/                           # 数据层
│   ├── database/                  # Room数据库
│   └── model/                     # 数据模型
├── widget/                         # 桌面小组件
│   └── AudioPlayerWidget.kt       # 小组件实现
├── timer/                          # 定时器
│   └── SleepTimerReceiver.kt      # 睡眠定时器
├── receiver/                       # 广播接收器
│   └── BootCompletedReceiver.kt   # 启动自动播放
├── settings/                       # 设置界面
│   └── SettingsScreen.kt
└── playlist/                       # 播放列表界面
    └── PlaylistScreen.kt
```

## ⚙️ 权限说明

应用需要以下权限：
- **读取存储权限** - 扫描MP3文件
- **前台服务权限** - 后台播放
- **通知权限** - 显示播放控制通知
- **开机启动权限** - 启动时自动播放（可选）

## 🚀 使用说明

1. **首次启动**
   - 授予存储权限
   - 在设置中选择MP3文件夹
   - 开始播放

2. **播放控制**
   - 播放/暂停：主界面中央按钮
   - 上一首/下一首：两侧按钮
   - 进度调整：拖动进度条

3. **睡眠定时器**
   - 点击"Sleep Timer"卡片
   - 选择定时时长（15/30/45/60分钟）
   - 定时结束后自动暂停播放

4. **桌面小组件**
   - 长按桌面空白处
   - 选择"小部件" → "Local MP3 Player"
   - 添加小组件到桌面
   - 点击控制播放

## 🔄 自动播放设置

在设置中启用"Auto-play on startup"：
- 应用启动时自动续播上次播放
- 设备重启后自动恢复播放（需授予开机启动权限）

## 📝 已知限制

- 当前版本仅支持MP3格式
- 播放列表功能为占位符（待实现）
- 均衡器等高级功能待添加

## 🤝 贡献

欢迎提交Issue和Pull Request！

## 📄 许可证

MIT License