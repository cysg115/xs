# 📱 本地MP3播放器 APK 构建指南

## 🎯 目标设备
- **设备型号**：三星 Galaxy S25 Ultra
- **Android 版本**：16 (API 级别 35+)
- **用途**：自用，无需签名

## 🛠️ 构建方法（选择一种）

### 方法一：Android Studio（最简单，推荐）
**适合所有人，无需命令行经验**

1. **下载并安装 Android Studio**
   - 官网：https://developer.android.com/studio
   - 安装时选择默认选项即可

2. **导入项目**
   - 解压 `LocalMP3Player.tar.gz`
   - 打开 Android Studio
   - 选择 "Open" → 选择解压后的 `LocalMP3Player` 文件夹

3. **连接手机**
   - 开启手机USB调试：
     - 设置 → 关于手机 → 连续点击"版本号"7次（开启开发者模式）
     - 设置 → 开发者选项 → 开启"USB调试"
   - 用USB线连接电脑和手机

4. **构建并安装**
   - 点击 Android Studio 工具栏的 ▶️ 运行按钮
   - 选择你的三星手机
   - 点击 "OK" 自动构建并安装到手机

5. **完成**
   - 首次运行需授予"存储权限"
   - 在设置中选择MP3文件夹
   - 开始使用！

### 方法二：命令行构建（适合开发者）
**需要已安装 Java 和 Android SDK**

```bash
# 1. 解压项目
tar -xzf LocalMP3Player.tar.gz
cd LocalMP3Player

# 2. 检查环境（需要以下工具）
#    - Java JDK 17+
#    - Android SDK（包含 build-tools 35.0.0+）
#    - 环境变量 ANDROID_HOME 已设置

# 3. 构建APK（使用项目自带的Gradle Wrapper）
chmod +x gradlew
./gradlew assembleDebug

# 4. 获取APK
#    生成的APK位置：
#    app/build/outputs/apk/debug/app-debug.apk
```

### 方法三：使用提供的构建脚本
**Linux/macOS 系统可用**

```bash
# 1. 解压项目
tar -xzf LocalMP3Player.tar.gz
cd LocalMP3Player

# 2. 运行构建脚本
chmod +x build_apk.sh
./build_apk.sh

# 脚本会自动检查环境并尝试构建
# 如果缺少工具会给出安装提示
```

### 方法四：GitHub Actions 云端构建（无需本地环境）
**需要有 GitHub 账号**

1. **创建 GitHub 仓库**
2. **上传项目代码**
3. **添加 GitHub Actions 工作流**
4. **自动生成 APK 并下载**

（详细步骤见 `GITHUB_ACTIONS_GUIDE.md`）

## 📦 APK 安装步骤

无论用哪种方法生成 `app-debug.apk`：

1. **传输APK到手机**
   - USB数据线复制
   - 微信/QQ文件传输助手
   - 网盘下载

2. **安装APK**
   - 使用文件管理器找到APK文件
   - 点击安装
   - 如提示"禁止安装"，请开启：
     - 设置 → 安全 → 未知来源/安装未知应用 → 允许

3. **首次运行设置**
   - 打开应用
   - 授予"存储权限"（必须，用于扫描MP3文件）
   - 进入设置 → 选择MP3文件夹
   - 开始播放

## 🔧 环境要求

### 最低配置
- **Java**: JDK 17 或更高
- **Android SDK**: API 35 (Android 15) 或更高
- **内存**: 4GB RAM
- **磁盘空间**: 2GB 可用空间

### 推荐配置
- **Android Studio 2023.3+**
- **Java 21**
- **Android SDK 35+**
- **8GB RAM**

## ⚠️ 常见问题

### Q1: 构建时出现 "Could not find tools.jar"
**原因**: Java环境不完整，只有JRE没有JDK
**解决**: 安装完整的Java JDK

### Q2: 安装时提示 "与设备不兼容"
**原因**: APK的minSdk过高
**解决**: 本项目已设置 minSdk=24 (Android 7.0)，支持绝大多数设备

### Q3: 应用闪退或无响应
**原因**: 缺少存储权限
**解决**: 设置 → 应用管理 → MP3播放器 → 权限 → 授予存储权限

### Q4: 扫描不到MP3文件
**原因**: 未选择正确文件夹
**解决**: 应用内设置 → 选择包含MP3的文件夹

### Q5: 睡眠定时器不生效
**原因**: 可能被系统省电策略限制
**解决**: 设置 → 电池 → 应用省电管理 → MP3播放器 → 无限制

## 📞 技术支持

如果遇到问题：
1. 查看本指南的"常见问题"部分
2. 检查Android Studio的错误提示
3. 确保手机USB调试已开启
4. 尝试换用不同的USB数据线

## 🚀 快速开始（懒人包）

**最简步骤：**
1. 电脑安装 Android Studio
2. USB连接三星S25U，开启USB调试
3. Android Studio打开项目，点击运行
4. 手机点击安装，授予权限
5. 完成！

---

**构建成功的关键：**
- ✅ 正确的Java环境（JDK 17+）
- ✅ 完整的Android SDK
- ✅ 手机USB调试已开启
- ✅ 授予应用存储权限

**如果所有方法都失败：**
可以考虑使用在线APK构建服务（如 buildozer、appcircle等），但需要上传源代码，请自行评估安全性。