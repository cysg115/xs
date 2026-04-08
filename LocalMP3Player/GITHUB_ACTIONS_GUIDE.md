# ☁️ GitHub Actions 云端构建指南

无需安装任何开发环境，使用GitHub的免费云服务自动构建APK。

## 🚀 快速开始（5分钟完成）

### 步骤1：创建GitHub仓库
1. 登录或注册 GitHub：https://github.com
2. 点击右上角 "+" → "New repository"
3. 仓库名：`local-mp3-player`（任意名称）
4. 选择 "Public"（公开，免费）
5. **不要**勾选 "Add a README file"
6. 点击 "Create repository"

### 步骤2：上传项目代码
```bash
# 方法A：使用Git命令行（推荐）
# 1. 解压项目
tar -xzf LocalMP3Player.tar.gz
cd LocalMP3Player

# 2. 初始化Git
git init
git add .
git commit -m "初始提交：本地MP3播放器"

# 3. 关联远程仓库并推送
git remote add origin https://github.com/你的用户名/local-mp3-player.git
git branch -M main
git push -u origin main

# 方法B：使用GitHub网页上传
# 1. 在仓库页面点击 "Add file" → "Upload files"
# 2. 将解压后的所有文件拖拽到上传区域
# 3. 点击 "Commit changes"
```

### 步骤3：触发构建
1. 进入仓库页面
2. 点击顶部 "Actions" 标签
3. 左侧选择 "Build APK" 工作流
4. 点击 "Run workflow" → "Run workflow"
5. 等待约3-5分钟完成构建

### 步骤4：下载APK
1. 构建完成后，点击对应的运行记录
2. 页面底部 "Artifacts" 区域
3. 点击 "mp3-player-apk" 下载ZIP包
4. 解压得到 `app-debug.apk`

## 📱 一键构建链接（高级用法）

创建一个快速构建链接，点击即可触发构建：

```
https://github.com/你的用户名/local-mp3-player/actions/workflows/build-apk.yml
```

将此链接保存为书签，每次需要新APK时点击即可。

## 🔄 自动构建设置

### 每次推送自动构建
- 默认已配置：每次推送到main分支时自动构建
- 无需手动触发

### 定时构建（每周更新）
在 `.github/workflows/build-apk.yml` 中添加：
```yaml
schedule:
  - cron: '0 0 * * 0'  # 每周日0点自动构建
```

### 手动触发构建
- 仓库页面 → Actions → Build APK → Run workflow
- 或访问：`https://github.com/用户名/仓库名/actions/workflows/build-apk.yml`

## 📦 构建产物说明

每次构建会生成两个APK：

| APK类型 | 用途 | 说明 |
|---------|------|------|
| `app-debug.apk` | **推荐使用** | 调试版本，可直接安装 |
| `app-release.apk` | 发布版本 | 需要签名，暂不可直接安装 |

**建议使用 `app-debug.apk`**，无需签名，适合自用。

## ⚙️ 自定义配置

### 修改构建参数
编辑 `.github/workflows/build-apk.yml`：

```yaml
# 修改Java版本
java-version: '21'  # 或 '17', '11'

# 修改Android SDK版本
# 默认使用最新稳定版
```

### 添加自动签名（高级）
如需生成可直接发布的APK，需要配置签名密钥：

1. 生成签名密钥：
```bash
keytool -genkey -v -keystore release.keystore \
  -alias mp3player -keyalg RSA -keysize 2048 \
  -validity 10000
```

2. 在GitHub仓库设置Secrets：
   - `KEYSTORE_FILE`: 密钥库文件(base64编码)
   - `KEYSTORE_PASSWORD`: 密钥库密码
   - `KEY_ALIAS`: 密钥别名
   - `KEY_PASSWORD`: 密钥密码

3. 工作流会自动使用签名

## 📊 构建状态监控

### 查看构建历史
- 仓库 → Actions → Build APK
- 查看每次构建的详细日志

### 构建状态徽章
在README中添加构建状态徽章：
```markdown
![构建状态](https://github.com/你的用户名/local-mp3-player/actions/workflows/build-apk.yml/badge.svg)
```

## ⚠️ 注意事项

### 免费额度
- GitHub Actions 每月有免费额度
- 本项目构建一次约消耗 3-5 分钟
- 个人账户每月有 2000 分钟免费额度
- 完全足够日常使用

### 安全性
- **公开仓库**：任何人都能看到代码
- **私有仓库**：需要GitHub Pro（付费）
- 建议：使用公开仓库，无敏感信息

### 网络限制
- GitHub服务器在国外
- 下载APK可能需要科学上网
- 或使用GitHub加速服务

## 🔧 故障排除

### Q1: 构建失败 "Could not find tools.jar"
**解决**：确保Java版本为17+，工作流已配置正确

### Q2: 无法下载APK
**解决**：尝试使用GitHub加速下载，或使用Git命令行下载：
```bash
# 下载最新构建产物
gh run download -R 用户名/仓库名
# 需要安装GitHub CLI: https://cli.github.com/
```

### Q3: 构建时间过长
**解决**：首次构建较慢（需要下载SDK），后续构建会缓存

### Q4: 手机安装失败
**解决**：
1. 确保下载的是 `app-debug.apk`
2. 手机开启"允许未知来源"
3. 卸载旧版本再安装新版本

## 🌐 替代方案

如果GitHub访问困难，可考虑：

### 方案A：Gitee（码云）
- 国内服务，速度快
- 类似GitHub，支持Actions
- 需要手动迁移工作流

### 方案B：GitLab.com
- 免费CI/CD额度
- 类似配置方式

### 方案C：本地构建
- 参考 BUILD_GUIDE_CN.md
- 需要本地开发环境

## 📞 技术支持

遇到问题：
1. 查看构建日志中的错误信息
2. 检查GitHub Actions文档
3. 确保仓库中有 `.github/workflows/build-apk.yml` 文件
4. 确认代码已正确推送

---

**优势总结**：
- ✅ 无需安装任何软件
- ✅ 免费云服务
- ✅ 自动更新，一键构建
- ✅ 支持多版本管理
- ✅ 跨平台（Windows/Mac/Linux通用）

**推荐指数**：⭐⭐⭐⭐⭐（最适合非开发者）